#include "pch.h"
#include "Mesh.h"

using namespace DirectX;

Mesh::Mesh()
{
}

Mesh::Mesh(int nv, VertexPosColor* vertices, int ni, WORD* indices)
{

}

XMMATRIX Mesh::GetWorldMatrix()
{
	return constantBuffer.m_world;
}

void Mesh::SetWorldMatrix(XMMATRIX world)
{
	constantBuffer.m_world = world;
}

void Mesh::SetConstants(MeshConstantBuffer constantBuffer)
{
	this->constantBuffer = constantBuffer;
}

void Mesh::SetParent(Mesh* parent)
{
	parentMesh = parent;
}

void Mesh::AddUpdater(MeshUpdater updater)
{
	meshUpdaters.push_back(updater);
}

void Mesh::Update(float deltaTime)
{
	for (auto updater : meshUpdaters)
	{
		constantBuffer.m_world = updater(constantBuffer.m_world, deltaTime);
	}
}
