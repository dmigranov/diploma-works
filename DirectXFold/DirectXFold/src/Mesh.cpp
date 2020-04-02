#include "pch.h"
#include "Mesh.h"

using namespace DirectX;

void Mesh::SetParent(Mesh* parent)
{
	parentMesh = parent;
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