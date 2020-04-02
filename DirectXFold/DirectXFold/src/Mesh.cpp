#include "pch.h"
#include "Mesh.h"

using namespace DirectX;

void Mesh::SetParent(Mesh* parent)
{
	parentMesh = parent;
}
