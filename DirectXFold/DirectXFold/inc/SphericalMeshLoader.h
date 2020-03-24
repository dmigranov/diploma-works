#pragma once
#include "Mesh.h"

class SphericalMeshLoader
{
public:
	static Mesh LoadMesh(const char* filepath);
	static Mesh LoadMeshSphericalCoordinates(const char* filepath);

};

