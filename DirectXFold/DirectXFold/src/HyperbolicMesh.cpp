#include "pch.h"
#include "HyperbolicMesh.h"

HyperbolicMesh::HyperbolicMesh()
{
}

HyperbolicMesh::HyperbolicMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices) : Mesh(nv, vertices, ni, indices)
{
}

HyperbolicMesh::HyperbolicMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices, DirectX::XMMATRIX world) : Mesh(nv, vertices, ni, indices, world)
{
}
