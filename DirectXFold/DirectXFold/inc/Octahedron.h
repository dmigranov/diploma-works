#pragma once
#include "Mesh.h"
class Octahedron :
	public Mesh
{
public:
	enum class FixedCoordinate {
		FC_X,
		FC_Y,
		FC_Z,
		FC_W
	};
	Octahedron(double wSec, XMMATRIX world);
	Octahedron(double wSec);
	Octahedron(FixedCoordinate coord, double section, XMMATRIX world);
	Octahedron(FixedCoordinate coord, double section);
	void SetSectionHeight(double newSectionHeight);
	double GetSectionHeight();


private:
	FixedCoordinate fc;
	double sectionHeight;
};

