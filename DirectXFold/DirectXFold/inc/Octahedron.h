#pragma once
#include "Mesh.h"
class Octahedron :
	public Mesh
{
public:
	enum FixedCoordinate {
		FC_X,
		FC_Y,
		FC_Z,
		FC_W
	};
	Octahedron(float wSec, XMMATRIX world);
	Octahedron(float wSec);
	Octahedron(FixedCoordinate coord, float section, XMMATRIX world);


};

