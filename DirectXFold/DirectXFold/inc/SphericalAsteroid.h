#pragma once
#include "SphericalMesh.h"
class SphericalAsteroid :
	public SphericalMesh
{
public:
	SphericalAsteroid(float amplitude, float radius, int sliceCount, int stackCount, DirectX::XMFLOAT4 color);
	SphericalAsteroid(float amplitude, float radius, int sliceCount, int stackCount, DirectX::XMFLOAT4 color, DirectX::XMMATRIX world);
};

