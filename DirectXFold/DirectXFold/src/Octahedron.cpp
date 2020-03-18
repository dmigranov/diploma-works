#include "pch.h"
#include "Octahedron.h"
#include "Game.h"

XMFLOAT4 GenerateRandomColor()
{
    return XMFLOAT4(float(rand())/float(RAND_MAX), float(rand()) / float(RAND_MAX), float(rand()) / float(RAND_MAX), 1.f);
}

Octahedron::Octahedron(double wSec, XMMATRIX world) : Octahedron(FixedCoordinate::FC_W, wSec, world)
{
}

Octahedron::Octahedron(double wSec) : Octahedron(wSec, XMMatrixIdentity())
{}

Octahedron::Octahedron(FixedCoordinate coord, double section, XMMATRIX world)
{
    this->fc = coord;
    sectionHeight = section;

    double inv = sqrt(1 - section * section);
    if (coord == FixedCoordinate::FC_W)
    {
        Mesh::VertexPosColor vertices[] = {
        { XMFLOAT4(0.f,  0.f, -inv, section), GenerateRandomColor() }, // 0
        { XMFLOAT4(0.f,  0.f, inv, section), GenerateRandomColor() }, // 1
        { XMFLOAT4(0.f, -inv, 0.f, section), GenerateRandomColor() }, // 2
        { XMFLOAT4(0.f,  inv, 0.f, section), GenerateRandomColor() }, // 3
        { XMFLOAT4(-inv,  0.f, 0.f, section), GenerateRandomColor() }, // 4
        { XMFLOAT4(inv,  0.f, 0.f, section), GenerateRandomColor() }, // 5
        };
        g_Vertices = vertices;
        verticesCount = _countof(vertices);
    }
    else if (coord == FixedCoordinate::FC_Z)
    {
        Mesh::VertexPosColor vertices[] = {
        { XMFLOAT4(0.f,  0.f, section, -inv), GenerateRandomColor() }, // 0
        { XMFLOAT4(0.f,  0.f, section, inv), GenerateRandomColor() }, // 1
        { XMFLOAT4(0.f, -inv, section, 0.f), GenerateRandomColor() }, // 2
        { XMFLOAT4(0.f,  inv, section, 0.f), GenerateRandomColor() }, // 3
        { XMFLOAT4(-inv,  0.f, section, 0.f), GenerateRandomColor()}, // 4
        { XMFLOAT4(inv,  0.f, section, 0.f), GenerateRandomColor() }, // 5
        };
        g_Vertices = vertices;
        verticesCount = _countof(vertices);
    }
    else if (coord == FixedCoordinate::FC_Y)
    {
        Mesh::VertexPosColor vertices[] = {
        { XMFLOAT4(0.f, section, 0.f, -inv), GenerateRandomColor() }, // 0
        { XMFLOAT4(0.f, section, 0.f, inv), GenerateRandomColor() }, // 1
        { XMFLOAT4(0.f, section, -inv, 0.f), GenerateRandomColor() }, // 2
        { XMFLOAT4(0.f, section, inv, 0.f), GenerateRandomColor() }, // 3
        { XMFLOAT4(-inv, section, 0.f, 0.f), GenerateRandomColor() }, // 4
        { XMFLOAT4(inv, section, 0.f, 0.f), GenerateRandomColor() }, // 5
        };
        g_Vertices = vertices;
        verticesCount = _countof(vertices);
    }
    else //if (coord == FixedCoordinate::FC_X)
    {
        Mesh::VertexPosColor vertices[] = {
        { XMFLOAT4(section, 0.f, 0.f, -inv), GenerateRandomColor() }, // 0
        { XMFLOAT4(section, 0.f, 0.f, inv), GenerateRandomColor() }, // 1
        { XMFLOAT4(section, 0.f, -inv, 0.f), GenerateRandomColor() }, // 2
        { XMFLOAT4(section, 0.f, inv, 0.f), GenerateRandomColor() }, // 3
        { XMFLOAT4(section, -inv, 0.f, 0.f), GenerateRandomColor() }, // 4
        { XMFLOAT4(section, inv, 0.f, 0.f), GenerateRandomColor() }, // 5
        };
        g_Vertices = vertices;
        verticesCount = _countof(vertices);
    }

    WORD indices[] = {
            0, 2, 4,
            4, 3, 0,
            5, 2, 0,
            0, 3, 5,
            4, 2, 1,
            1, 3, 4,
            1, 2, 5,
            5, 3, 1,
    };

    auto& game = Game::GetInstance();
    auto device = game.g_d3dDevice;
    deviceContext = game.g_d3dDeviceContext;
    d3dConstantBuffer = game.g_d3dConstantBuffers[2];
    g_Indices = indices;

    indicesCount = _countof(indices);

    // Create and initialize the vertex buffer.
    D3D11_BUFFER_DESC vertexBufferDesc;
    ZeroMemory(&vertexBufferDesc, sizeof(D3D11_BUFFER_DESC));

    vertexBufferDesc.BindFlags = D3D11_BIND_VERTEX_BUFFER;  //how the buffer is bound to pipeline
    vertexBufferDesc.ByteWidth = sizeof(VertexPosColor) * verticesCount;
    vertexBufferDesc.CPUAccessFlags = 0;    // no CPU access is necessary
    vertexBufferDesc.Usage = D3D11_USAGE_DEFAULT;

    //used to specify the data that is used to initialize a buffer when it is created.
    D3D11_SUBRESOURCE_DATA resourceData;
    ZeroMemory(&resourceData, sizeof(D3D11_SUBRESOURCE_DATA));
    resourceData.pSysMem = g_Vertices; //A pointer to the data to initialize the buffer with.

    device->CreateBuffer(&vertexBufferDesc, &resourceData, &g_d3dVertexBuffer);

    // Create and initialize the index buffer.
    D3D11_BUFFER_DESC indexBufferDesc;
    ZeroMemory(&indexBufferDesc, sizeof(D3D11_BUFFER_DESC));

    indexBufferDesc.BindFlags = D3D11_BIND_INDEX_BUFFER;
    indexBufferDesc.ByteWidth = sizeof(WORD) * indicesCount;
    indexBufferDesc.CPUAccessFlags = 0;
    indexBufferDesc.Usage = D3D11_USAGE_DEFAULT;
    resourceData.pSysMem = g_Indices;

    device->CreateBuffer(&indexBufferDesc, &resourceData, &g_d3dIndexBuffer);

    constantBuffer.m_world = world;
}

Octahedron::Octahedron(FixedCoordinate coord, double section) : Octahedron(coord, section, XMMatrixIdentity())
{
}

void Octahedron::SetSectionHeight(double newSectionHeight)
{
    if (newSectionHeight < -1 || newSectionHeight > 1)
        return;

    double multiplier = sqrt((1. - newSectionHeight * newSectionHeight) / (1. - sectionHeight * sectionHeight));;
    switch (fc)
    {
    case FixedCoordinate::FC_X:
        constantBuffer.m_world *= Matrix(newSectionHeight / sectionHeight, 0, 0, 0,
            0, multiplier, 0, 0,
            0, 0, multiplier, 0,
            0, 0, 0, multiplier);
        break;
    case FixedCoordinate::FC_Y:
        constantBuffer.m_world *= Matrix(multiplier, 0, 0, 0,
            0, newSectionHeight / sectionHeight, 0, 0,
            0, 0, multiplier, 0,
            0, 0, 0, multiplier);
        break;
    case FixedCoordinate::FC_Z:
        constantBuffer.m_world *= Matrix(multiplier, 0, 0, 0,
            0, multiplier, 0, 0,
            0, 0, newSectionHeight / sectionHeight, 0,
            0, 0, 0, multiplier);
        break;
    case FixedCoordinate::FC_W:
        constantBuffer.m_world *= Matrix(multiplier, 0, 0, 0,
            0, multiplier, 0, 0,
            0, 0, multiplier, 0,
            0, 0, 0, newSectionHeight / sectionHeight);

        break;
    }
    sectionHeight = newSectionHeight;

}

double Octahedron::GetSectionHeight()
{
    return sectionHeight;
}
