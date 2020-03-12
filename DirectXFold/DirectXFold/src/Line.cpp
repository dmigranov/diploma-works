#include "pch.h"
#include "Line.h"

void Line::Render()
{
    for (auto updater : meshUpdaters)
    {
        constantBuffer.m_world = updater(constantBuffer.m_world);
    }

    // Input Assembler Stage - unique for every mesh
    const UINT vertexStride = sizeof(VertexPosColor);   //Each stride is the size (in bytes) of the elements that are to be used from that vertex buffer.
    const UINT offset = 0;
    deviceContext->IASetPrimitiveTopology(D3D11_PRIMITIVE_TOPOLOGY_LINESTRIP);
    deviceContext->IASetVertexBuffers(0, 1, &g_d3dVertexBuffer, &vertexStride, &offset);
    deviceContext->IASetIndexBuffer(g_d3dIndexBuffer, DXGI_FORMAT_R16_UINT, 0);

    deviceContext->UpdateSubresource(d3dConstantBuffer, 0, nullptr, &constantBuffer, 0, 0);

    //DRAW
    deviceContext->DrawIndexed(indicesCount, 0, 0);
}

void Line::Render(XMMATRIX matrix)
{
    // Input Assembler Stage - unique for every mesh
    const UINT vertexStride = sizeof(VertexPosColor);   //Each stride is the size (in bytes) of the elements that are to be used from that vertex buffer.
    const UINT offset = 0;
    deviceContext->IASetPrimitiveTopology(D3D11_PRIMITIVE_TOPOLOGY_LINESTRIP);
    deviceContext->IASetVertexBuffers(0, 1, &g_d3dVertexBuffer, &vertexStride, &offset);
    deviceContext->IASetIndexBuffer(g_d3dIndexBuffer, DXGI_FORMAT_R16_UINT, 0);

    MeshConstantBuffer constantBufferTemp = { matrix, constantBuffer.m_morph };
    deviceContext->UpdateSubresource(d3dConstantBuffer, 0, nullptr, &constantBufferTemp, 0, 0);

    //DRAW
    deviceContext->DrawIndexed(indicesCount, 0, 0);
}

void Line::Render(std::list<XMMATRIX> matrices)
{
    // Input Assembler Stage - unique for every mesh
    const UINT vertexStride = sizeof(VertexPosColor);   //Each stride is the size (in bytes) of the elements that are to be used from that vertex buffer.
    const UINT offset = 0;
    deviceContext->IASetPrimitiveTopology(D3D11_PRIMITIVE_TOPOLOGY_LINESTRIP);
    deviceContext->IASetVertexBuffers(0, 1, &g_d3dVertexBuffer, &vertexStride, &offset);
    deviceContext->IASetIndexBuffer(g_d3dIndexBuffer, DXGI_FORMAT_R16_UINT, 0);

    for (auto matrix : matrices)
    {
        MeshConstantBuffer constantBufferTemp = { matrix, constantBuffer.m_morph };
        deviceContext->UpdateSubresource(d3dConstantBuffer, 0, nullptr, &constantBufferTemp, 0, 0);

        //DRAW
        deviceContext->DrawIndexed(indicesCount, 0, 0);
    }
}
