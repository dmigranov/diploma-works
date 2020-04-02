#include "pch.h"


#include "SphericalMeshLoader.h"
using namespace DirectX::SimpleMath;


SphericalMesh* SphericalMeshLoader::LoadMesh(const char* filepath)
{
	std::ifstream infile;
	infile.open(filepath);

	if (!infile)
	{
		std::cerr << "Unable to open file " << filepath << std::endl;
		exit(1); //todo?
	}

	std::string str;

	std::vector<SphericalMesh::VertexPosColor> vertices;
	std::vector<WORD> vertexIndices;


	while (std::getline(infile, str))
	{
		if (str.length() > 1 && str[0] == 'v' && str[1] == ' ') //v -0.5 0.5 -0.5
		{

			str = str.substr(2);
			std::vector<std::string> parsedStrings = parseString(str, ' ');
			std::vector<double> values = getDoubleValues(parsedStrings);
			vertices.push_back({ XMFLOAT4(values[0], values[1], values[2], values[3]), XMFLOAT4(values[4], values[5], values[6], 1.f) });
		}

		else if (str.length() > 1 && str[0] == 'i' && str[1] == ' ') //i 1 2 3
		{

			str = str.substr(2);
			std::vector<std::string> strIndices = parseString(str, ' ');
			std::vector<int> triangleIndices = getIntValues(strIndices);			
			vertexIndices.insert(std::end(vertexIndices), std::begin(triangleIndices), std::end(triangleIndices));
		}
	}

	return new SphericalMesh(vertices.size(), &vertices[0], vertexIndices.size(), &vertexIndices[0]);
}

SphericalMesh* SphericalMeshLoader::LoadMeshSphericalCoordinates(const char* filepath)
{
	return new SphericalMesh();
}

std::vector<std::string> SphericalMeshLoader::parseString(std::string str, char delimiter)
{
	std::vector<std::string> vec;
	size_t startIndex = 0;
	size_t endIndex;
	while ((endIndex = str.find(delimiter, startIndex)) != std::string::npos)
	{
		std::string substr = str.substr(startIndex, endIndex - startIndex);
		vec.push_back(substr);
		startIndex = endIndex + 1;
	}
	std::string substr = str.substr(startIndex, endIndex - startIndex);
	vec.push_back(substr);
	return vec;
}

std::vector<double> SphericalMeshLoader::getDoubleValues(std::vector<std::string> strings)
{
	std::vector<double> vec;
	for (std::string str : strings)
	{
		vec.push_back(atof(str.c_str()));
	}
	return vec;
}

std::vector<int> SphericalMeshLoader::getIntValues(std::vector<std::string> strings)
{
	std::vector<int> vec;
	for (std::string str : strings)
	{
		vec.push_back(atoi(str.c_str()));
	}
	return vec;
}
