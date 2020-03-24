#pragma once
#include "Mesh.h"

class SphericalMeshLoader
{
public:
	static Mesh LoadMesh(const char* filepath);
	static Mesh LoadMeshSphericalCoordinates(const char* filepath);
private:
	static std::vector<std::string> parseString(std::string str, char delimiter);
	static std::vector<double> getDoubleValues(std::vector<std::string> strings);
	static std::vector<int> getIntValues(std::vector<std::string> strings);

};

