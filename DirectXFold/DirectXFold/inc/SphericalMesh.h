#pragma once
#include "Mesh.h"

class SphericalMesh : public Mesh
{
    friend class SphericalOctahedron;
    friend class SphericalDodecahedron;
    friend class SphericalCube;

public:
    SphericalMesh();
    SphericalMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices);
    SphericalMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices, DirectX::XMMATRIX world);

    ~SphericalMesh();


    //можно рисовать один и тот же меш используя разные матрицы
    //std::list<XMMATRIX> list = { ..., ... }; cube->Render(list); 
    virtual void Render(std::list<DirectX::XMMATRIX> matrices);
    virtual void Render();
    virtual void Render(DirectX::XMMATRIX matrix);

private: 


};

