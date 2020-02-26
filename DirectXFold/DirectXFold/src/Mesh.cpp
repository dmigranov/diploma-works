#include "pch.h"
#include "Mesh.h"

Mesh::Mesh() : Mesh(XMMatrixIdentity())
{ }

Mesh::~Mesh()
{
    SafeRelease(g_d3dIndexBuffer);
    SafeRelease(g_d3dVertexBuffer);
}

Mesh::Mesh(XMMATRIX world)
{
    auto &game = Game::GetInstance();
    auto device = game.g_d3dDevice;
    deviceContext = game.g_d3dDeviceContext;
    d3dConstantBuffer = game.g_d3dConstantBuffers[2];

    // Create and initialize the vertex buffer.
    D3D11_BUFFER_DESC vertexBufferDesc;
    ZeroMemory(&vertexBufferDesc, sizeof(D3D11_BUFFER_DESC));

    vertexBufferDesc.BindFlags = D3D11_BIND_VERTEX_BUFFER;  //how the buffer is bound to pipeline
    vertexBufferDesc.ByteWidth = sizeof(VertexPosColor) * _countof(g_Vertices);
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
    indexBufferDesc.ByteWidth = sizeof(WORD) * _countof(g_Indicies);
    indexBufferDesc.CPUAccessFlags = 0;
    indexBufferDesc.Usage = D3D11_USAGE_DEFAULT;
    resourceData.pSysMem = g_Indicies;

    device->CreateBuffer(&indexBufferDesc, &resourceData, &g_d3dIndexBuffer);

    constantBuffer.m_world = world;
    constantBuffer.m_morph = XMMatrixIdentity();
}


void Mesh::SetWorldMatrix(XMMATRIX world)
{
    constantBuffer.m_world = world;
}

void Mesh::SetConstants(MeshConstantBuffer constantBuffer)
{
    this->constantBuffer = constantBuffer;
}

void Mesh::SetConstants(XMMATRIX world, XMMATRIX morph)
{
    SetConstants({world, morph});
}

void Mesh::Move(float x, float y, float z)
{
    constantBuffer.m_world = XMMatrixTranslation(x, y, z) *
        constantBuffer.m_world;
}

XMMATRIX Mesh::GetWorldMatrix()
{
    return constantBuffer.m_world;
}

void Mesh::Render()
{
    // Input Assembler Stage - unique for every mesh
    const UINT vertexStride = sizeof(VertexPosColor);   //Each stride is the size (in bytes) of the elements that are to be used from that vertex buffer.
    const UINT offset = 0;
    deviceContext->IASetVertexBuffers(0, 1, &g_d3dVertexBuffer, &vertexStride, &offset);
    deviceContext->IASetIndexBuffer(g_d3dIndexBuffer, DXGI_FORMAT_R16_UINT, 0);

    deviceContext->UpdateSubresource(d3dConstantBuffer, 0, nullptr, &constantBuffer, 0, 0);
    
    //DRAW
    deviceContext->DrawIndexed(_countof(g_Indicies), 0, 0);
}

void Mesh::Render(XMMATRIX matrix)
{
    //TODO: теоретически, если мы рисуем один и тот же меш много раз, нам не надо
    //каждый раз устанавливать буферы
    //можно конечно использовать инстансинг
    //а можно передавать сразу много .. матриц в этот метод и рисовать их все

    // Input Assembler Stage - unique for every mesh
    const UINT vertexStride = sizeof(VertexPosColor);   //Each stride is the size (in bytes) of the elements that are to be used from that vertex buffer.
    const UINT offset = 0;
    deviceContext->IASetVertexBuffers(0, 1, &g_d3dVertexBuffer, &vertexStride, &offset);
    deviceContext->IASetIndexBuffer(g_d3dIndexBuffer, DXGI_FORMAT_R16_UINT, 0);

    MeshConstantBuffer constantBufferTemp = { matrix, constantBuffer.m_morph };
    deviceContext->UpdateSubresource(d3dConstantBuffer, 0, nullptr, &constantBufferTemp, 0, 0);

    //DRAW
    deviceContext->DrawIndexed(_countof(g_Indicies), 0, 0);
}

void Mesh::Render(std::list<XMMATRIX> matrices)
{

    // Input Assembler Stage - unique for every mesh
    const UINT vertexStride = sizeof(VertexPosColor);   //Each stride is the size (in bytes) of the elements that are to be used from that vertex buffer.
    const UINT offset = 0;
    deviceContext->IASetVertexBuffers(0, 1, &g_d3dVertexBuffer, &vertexStride, &offset);
    deviceContext->IASetIndexBuffer(g_d3dIndexBuffer, DXGI_FORMAT_R16_UINT, 0);

    for (auto matrix : matrices)
    {
        MeshConstantBuffer constantBufferTemp = { matrix, constantBuffer.m_morph };
        deviceContext->UpdateSubresource(d3dConstantBuffer, 0, nullptr, &constantBufferTemp, 0, 0);

        //DRAW
        deviceContext->DrawIndexed(_countof(g_Indicies), 0, 0);
    }
}