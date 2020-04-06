#pragma once
#include "EllipticalMesh.h"
class EllipticalOctahedron :
	public EllipticalMesh
{
	EllipticalOctahedron(double wSec);
	EllipticalOctahedron(double wSec, DirectX::XMFLOAT4* colors);
	EllipticalOctahedron(double wSec, DirectX::XMMATRIX world);
	EllipticalOctahedron(double wSec, DirectX::XMMATRIX world, DirectX::XMFLOAT4* colors);

	void SetSectionHeight(double newSectionHeight);
	double GetSectionHeight();
private:
	DirectX::XMFLOAT4 GenerateRandomColor();
};

