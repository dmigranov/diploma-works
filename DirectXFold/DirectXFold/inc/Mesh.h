#pragma once
class Mesh
{
private:
	ID3D11Buffer* g_d3dVertexBuffer = nullptr;
	ID3D11Buffer* g_d3dIndexBuffer = nullptr;
public:
	Mesh();

};

