#pragma once
#include "Mesh.h"
class HyperbolicMesh :
	public Mesh
{
	HyperbolicMesh();
	HyperbolicMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices);
	HyperbolicMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices, DirectX::XMMATRIX world);
};

