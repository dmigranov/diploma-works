#include "pch.h"
#include "Icosahedron.h"
#include "Game.h"


Icosahedron::Icosahedron(float wSec, XMMATRIX world)
{
    float inv = sqrtf(1 - 0.8 * 0.8);
    Mesh::VertexPosColor vertices[] = {
    { XMFLOAT4(0.f,  0.f, -inv, wSec), XMFLOAT3(0.5f, 1.0f, 0.0f) }, // 0
    { XMFLOAT4(0.f,  0.f, inv, wSec), XMFLOAT3(0.0f, 1.0f, 0.5f) }, // 1
    { XMFLOAT4(0.f, -inv, 0.f, wSec), XMFLOAT3(0.5f, 1.0f, 1.0f) }, // 2
    { XMFLOAT4(0.f,  inv, 0.f, wSec), XMFLOAT3(1.0f, 0.5f, 0.0f) }, // 3
    { XMFLOAT4(-inv,  0.f, 0.f, wSec), XMFLOAT3(0.5f, 0.0f, 0.5f) }, // 4
    { XMFLOAT4(inv,  0.f, 0.f, wSec), XMFLOAT3(0.5f, 0.5f, 0.5f) }, // 5
    };

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

    auto &game = Game::GetInstance();
    auto device = game.g_d3dDevice;
    deviceContext = game.g_d3dDeviceContext;
    d3dConstantBuffer = game.g_d3dConstantBuffers[2];
    g_Indices = indices;
    g_Vertices = vertices;
    verticesCount = _countof(vertices);
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
    constantBuffer.m_morph = XMMatrixIdentity();
}

Icosahedron::Icosahedron(float wSec) : Icosahedron(wSec, XMMatrixIdentity())
{
}
