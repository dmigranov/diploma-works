#pragma once
#include "Mesh.h"
class EllipticalMesh :
	public Mesh
{
    friend class SphericalOctahedron;
    friend class SphericalDodecahedron;
    friend class SphericalCube;

public:
    EllipticalMesh();
    EllipticalMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices);
    EllipticalMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices, DirectX::XMMATRIX world);

    //можно рисовать один и тот же меш используя разные матрицы
    //std::list<XMMATRIX> list = { ..., ... }; cube->Render(list); 
    virtual void Render() override;
    virtual void Render(DirectX::XMMATRIX matrix) override;
    virtual void Render(std::list<DirectX::XMMATRIX> matrices) override;
};

