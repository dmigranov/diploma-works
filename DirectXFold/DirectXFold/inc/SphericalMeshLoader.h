#pragma once
#include "Mesh.h"

class SphericalMeshLoader
{
public:
	static Mesh LoadMesh(const char* filepath);
	static Mesh LoadMeshSphericalCoordinates(const char* filepath);
private:
	std::vector<std::string> parseString(std::string str, char delimiter);
	std::vector<double> getDoubleValues(std::vector<std::string> strings);
	std::vector<double> getIntValues(std::vector<std::string> strings);

};

