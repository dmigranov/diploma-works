#include "pch.h"
#include <fstream>
#include "SphericalMeshLoader.h"


Mesh SphericalMeshLoader::LoadMesh(const char* filepath)
{
	std::ifstream infile;
	infile.open(filepath);

	if (!infile)
	{
		std::cerr << "Unable to open file " << filepath << std::endl;
		exit(1);
	}

	std::string str;
	size_t polygonSize;

	return Mesh();
}
