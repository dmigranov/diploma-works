#pragma once
#include "SphericalMesh.h"
class Line : public SphericalMesh
{
public:
	using SphericalMesh::SphericalMesh;		//использовать конструкторы родителя
	void Render() override;
	void Render(XMMATRIX matrix) override;
	void Render(std::list<XMMATRIX> matrices) override;

};

