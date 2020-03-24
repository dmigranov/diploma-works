#include "pch.h"
#include <fstream>
#include <vector>

#include "SphericalMeshLoader.h"
using namespace DirectX::SimpleMath;


Mesh SphericalMeshLoader::LoadMesh(const char* filepath)
{
	std::ifstream infile;
	infile.open(filepath);

	if (!infile)
	{
		std::cerr << "Unable to open file " << filepath << std::endl;
		exit(1); //todo?
	}

	std::string str;

	std::vector<Vector4> vertices;
	std::vector<Vector4> colors; 
	std::vector<unsigned int> vertexIndices;


	while (std::getline(infile, str))
	{
		
	}

	return Mesh();
}

Mesh SphericalMeshLoader::LoadMeshSphericalCoordinates(const char* filepath)
{
	return Mesh();
}
