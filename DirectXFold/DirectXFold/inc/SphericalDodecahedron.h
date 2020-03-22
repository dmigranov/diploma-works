#pragma once
#include "Mesh.h"
class SphericalDodecahedron :
	public Mesh
{
	enum class FixedCoordinate {
		FC_X,
		FC_Y,
		FC_Z,
		FC_W
	};

	SphericalDodecahedron(double wSec, XMMATRIX world);
	SphericalDodecahedron(double wSec);
	SphericalDodecahedron(FixedCoordinate coord, double section, XMMATRIX world);
	SphericalDodecahedron(FixedCoordinate coord, double section);
	void SetSectionHeight(double newSectionHeight);
	double GetSectionHeight();

private:
	FixedCoordinate fc;
	double sectionHeight;
};

