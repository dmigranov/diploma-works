#include "pch.h"
#include "SphericalMesh.h"
#include "Game.h"

using namespace DirectX;

SphericalMesh::SphericalMesh()
{
}

SphericalMesh::SphericalMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices) : SphericalMesh(nv, vertices, ni, indices, XMMatrixIdentity())
{ }

SphericalMesh::~SphericalMesh()
{
    SafeRelease(g_d3dIndexBuffer);
    SafeRelease(g_d3dVertexBuffer);
}

SphericalMesh::SphericalMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices, XMMATRIX world)
{
    auto &game = Game::GetInstance();
    auto device = game.g_d3dDevice;
    deviceContext = game.g_d3dDeviceContext;
    d3dConstantBuffer = game.g_d3dVSConstantBuffers[2];
    g_Indices = indices;
    g_Vertices = vertices;
    verticesCount = nv;
    indicesCount = ni;

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


void SphericalMesh::SetWorldMatrix(XMMATRIX world)
{
    constantBuffer.m_world = world;
}

void SphericalMesh::SetConstants(MeshConstantBuffer constantBuffer)
{
    this->constantBuffer = constantBuffer;
}

void SphericalMesh::SetParent(SphericalMesh* parent)
{
    parentMesh = parent;
}

void SphericalMesh::Move(float x, float y, float z)
{
    constantBuffer.m_world = XMMatrixTranslation(x, y, z) *
        constantBuffer.m_world;
}

void SphericalMesh::AddUpdater(MeshUpdater updater)
{
    meshUpdaters.push_back(updater);
}

void SphericalMesh::Update(float deltaTime)
{
    for (auto updater : meshUpdaters)
    {
        constantBuffer.m_world = updater(constantBuffer.m_world, deltaTime);
    }
}

XMMATRIX SphericalMesh::GetWorldMatrix()
{
    return constantBuffer.m_world;
}

SphericalMesh* SphericalMesh::Clone()
{
    SphericalMesh* mesh = new SphericalMesh(verticesCount, g_Vertices, indicesCount, g_Indices, constantBuffer.m_world);
    mesh->SetParent(parentMesh);
    return mesh;
}

void SphericalMesh::Render()
{
    // Input Assembler Stage - unique for every mesh
    const UINT vertexStride = sizeof(VertexPosColor);   //Each stride is the size (in bytes) of the elements that are to be used from that vertex buffer.
    const UINT offset = 0;
    deviceContext->IASetPrimitiveTopology(D3D11_PRIMITIVE_TOPOLOGY_TRIANGLELIST);
    deviceContext->IASetVertexBuffers(0, 1, &g_d3dVertexBuffer, &vertexStride, &offset);
    deviceContext->IASetIndexBuffer(g_d3dIndexBuffer, DXGI_FORMAT_R16_UINT, 0);

    MeshConstantBuffer constantBufferTemp = { constantBuffer.m_world };
    if (parentMesh != nullptr)
        constantBufferTemp.m_world = constantBufferTemp.m_world * parentMesh->GetWorldMatrix();
    deviceContext->UpdateSubresource(d3dConstantBuffer, 0, nullptr, &constantBufferTemp, 0, 0);
    
    //DRAW
    deviceContext->DrawIndexedInstanced(indicesCount, 2, 0, 0, 0);
}

void SphericalMesh::Render(XMMATRIX matrix)
{
    // Input Assembler Stage - unique for every mesh
    const UINT vertexStride = sizeof(VertexPosColor);   //Each stride is the size (in bytes) of the elements that are to be used from that vertex buffer.
    const UINT offset = 0;
    deviceContext->IASetPrimitiveTopology(D3D11_PRIMITIVE_TOPOLOGY_TRIANGLELIST);
    deviceContext->IASetVertexBuffers(0, 1, &g_d3dVertexBuffer, &vertexStride, &offset);
    deviceContext->IASetIndexBuffer(g_d3dIndexBuffer, DXGI_FORMAT_R16_UINT, 0);

    MeshConstantBuffer constantBufferTemp = { matrix };
    if (parentMesh != nullptr)
        constantBufferTemp.m_world = constantBufferTemp.m_world * parentMesh->GetWorldMatrix();
    deviceContext->UpdateSubresource(d3dConstantBuffer, 0, nullptr, &constantBufferTemp, 0, 0);

    //DRAW
    deviceContext->DrawIndexedInstanced(indicesCount, 2, 0, 0, 0);
}


//если мы рисуем один и тот же меш много раз, нам не надо
//каждый раз устанавливать буферы
//можно конечно использовать инстансинг
//а можно передавать сразу много .. матриц в этот метод и рисовать их все
void SphericalMesh::Render(std::list<XMMATRIX> matrices)
{
    // Input Assembler Stage - unique for every mesh
    const UINT vertexStride = sizeof(VertexPosColor);   //Each stride is the size (in bytes) of the elements that are to be used from that vertex buffer.
    const UINT offset = 0;
    deviceContext->IASetPrimitiveTopology(D3D11_PRIMITIVE_TOPOLOGY_TRIANGLELIST);
    deviceContext->IASetVertexBuffers(0, 1, &g_d3dVertexBuffer, &vertexStride, &offset);
    deviceContext->IASetIndexBuffer(g_d3dIndexBuffer, DXGI_FORMAT_R16_UINT, 0);

    for (auto matrix : matrices)
    {
        MeshConstantBuffer constantBufferTemp = { matrix };
        if (parentMesh != nullptr)
            constantBufferTemp.m_world = constantBufferTemp.m_world * parentMesh->GetWorldMatrix();
        deviceContext->UpdateSubresource(d3dConstantBuffer, 0, nullptr, &constantBufferTemp, 0, 0);

        //DRAW
        deviceContext->DrawIndexedInstanced(indicesCount, 2, 0, 0, 0);
    }
}

SphericalMesh::MeshUpdater::MeshUpdater(std::function<DirectX::SimpleMath::Matrix(DirectX::SimpleMath::Matrix, float delta)> func)
{
    m_func = func;
}

DirectX::SimpleMath::Matrix SphericalMesh::MeshUpdater::operator()(DirectX::SimpleMath::Matrix in, float delta)
{
    return m_func(in, delta);
}
