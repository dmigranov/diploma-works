#pragma once
#include "SphericalMesh.h"
class SphericalCube :
	public SphericalMesh
{
public:
	SphericalCube(double wSec);
	SphericalCube(double wSec, XMMATRIX world);
	SphericalCube(double wSec, XMFLOAT4* colors);
	SphericalCube(double wSec, XMMATRIX world, XMFLOAT4* colors);
private:
	XMFLOAT4 GenerateRandomColor();
};

