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
		if (str[0] == 'v')
		{
			if (str[1] == ' ') //v -0.5 0.5 -0.5
			{
				str = str.substr(2);
				std::vector<std::string> parsedStrings = parseString(str, ' ');
				std::vector<double> values = getDoubleValues(parsedStrings);
				vertices.push_back(Vector3(values[0], values[1], values[2]));
			}

		}
	}

	return Mesh();
}

Mesh SphericalMeshLoader::LoadMeshSphericalCoordinates(const char* filepath)
{
	return Mesh();
}
