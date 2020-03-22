#pragma once
#include "Mesh.h"
class SphericalDodecahedron :
	public Mesh
{
public:
	enum class FixedCoordinate {
		FC_X,
		FC_Y,
		FC_Z,
		FC_W
	};

	SphericalDodecahedron(double wSec, XMMATRIX world);
	SphericalDodecahedron(double wSec);

	void SetSectionHeight(double newSectionHeight);
	double GetSectionHeight();

private:
	FixedCoordinate fc;
	double sectionHeight;
	XMFLOAT4 GenerateRandomColor();
};

