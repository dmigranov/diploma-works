#pragma once
#include "SphericalMesh.h"
class SphericalSphere :
	public SphericalMesh
{
public: 
	SphericalSphere(float radius, int sliceCount, int stackCount, DirectX::XMFLOAT4 color);
	SphericalSphere(float radius, int sliceCount, int stackCount, DirectX::XMFLOAT4 color, DirectX::XMMATRIX world);
};

