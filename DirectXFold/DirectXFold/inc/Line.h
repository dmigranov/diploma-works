#pragma once
#include "Mesh.h"
class Line :
	public Mesh
{
	void Render() override;
	void Render(XMMATRIX matrix) override;
	void Render(std::list<XMMATRIX> matrices) override;

};

