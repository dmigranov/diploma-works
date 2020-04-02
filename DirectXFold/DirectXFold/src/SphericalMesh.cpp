#include "pch.h"
#include "SphericalMesh.h"
//#include "Game.h"

using namespace DirectX;

SphericalMesh::SphericalMesh()
{
}

SphericalMesh::SphericalMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices) : Mesh(nv, vertices, ni, indices)
{ }

SphericalMesh::~SphericalMesh()
{
    SafeRelease(g_d3dIndexBuffer);
    SafeRelease(g_d3dVertexBuffer);
}

SphericalMesh::SphericalMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices, XMMATRIX world) : Mesh(nv, vertices, ni, indices, world)
{
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
