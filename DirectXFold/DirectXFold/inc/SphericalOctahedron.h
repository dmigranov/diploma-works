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
	SphericalOctahedron(double wSec, XMMATRIX world);
	SphericalOctahedron(double wSec);
	SphericalOctahedron(double wSec, XMMATRIX world, XMFLOAT4* colors);
	SphericalOctahedron(double wSec, XMFLOAT4* colors);

	void SetSectionHeight(double newSectionHeight);
	double GetSectionHeight();
private:
	SphericalOctahedron(FixedCoordinate coord, double section, XMMATRIX world);
	SphericalOctahedron(FixedCoordinate coord, double section);
	SphericalOctahedron(FixedCoordinate coord, double section, XMMATRIX world, XMFLOAT4 * colors);




private:
	FixedCoordinate fc;
	double sectionHeight;
};

