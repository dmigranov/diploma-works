#pragma once
#include "Mesh.h"
class Line : public Mesh
{
public:
	using Mesh::Mesh;		//использовать конструкторы родителя
	void Render() override;
	void Render(XMMATRIX matrix) override;
	void Render(std::list<XMMATRIX> matrices) override;

};

