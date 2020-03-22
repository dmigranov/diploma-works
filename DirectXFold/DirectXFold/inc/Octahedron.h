#pragma once
#include "Mesh.h"
class SphericalOctahedron :
	public Mesh
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
	SphericalOctahedron(FixedCoordinate coord, double section, XMMATRIX world);
	SphericalOctahedron(FixedCoordinate coord, double section);
	void SetSectionHeight(double newSectionHeight);
	double GetSectionHeight();


private:
	FixedCoordinate fc;
	double sectionHeight;
};

