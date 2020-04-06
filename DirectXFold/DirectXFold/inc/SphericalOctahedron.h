#pragma once
#include "SphericalMesh.h"

class SphericalOctahedron :
	public SphericalMesh
{
public:
	enum class FixedCoordinate {
		FC_X,
		FC_Y,
		FC_Z,
		FC_W
	};
	SphericalOctahedron(double wSec, DirectX::XMMATRIX world);
	SphericalOctahedron(double wSec);
	SphericalOctahedron(double wSec, DirectX::XMMATRIX world, DirectX::XMFLOAT4* colors);
	SphericalOctahedron(double wSec, DirectX::XMFLOAT4* colors);

	void SetSectionHeight(double newSectionHeight);
	double GetSectionHeight();
private:
	SphericalOctahedron(FixedCoordinate coord, double section, DirectX::XMMATRIX world);
	SphericalOctahedron(FixedCoordinate coord, double section);
	SphericalOctahedron(FixedCoordinate coord, double section, DirectX::XMMATRIX world, DirectX::XMFLOAT4 * colors);




private:
	FixedCoordinate fc;
	double sectionHeight;
};

