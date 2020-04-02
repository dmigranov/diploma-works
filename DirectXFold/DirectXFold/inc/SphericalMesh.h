#pragma once
#include "Mesh.h"

class SphericalMesh : Mesh
{
    //todo: добавить parent mesh (возможно null)
    friend class SphericalOctahedron;
    friend class SphericalDodecahedron;
    friend class SphericalCube;

public:

    
    SphericalMesh();
    SphericalMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices);
    SphericalMesh(int nv, VertexPosColor* vertices, int ni, WORD* indices, DirectX::XMMATRIX world);

    ~SphericalMesh();

    DirectX::XMMATRIX GetWorldMatrix() override;
	void SetWorldMatrix(DirectX::XMMATRIX world) override;
    void SetConstants(MeshConstantBuffer constantBuffer) override;

    void Move(float x, float y, float z) override;

    void AddUpdater(MeshUpdater updater) override;
    void Update(float deltaTime) override;


    //можно рисовать один и тот же меш использу€ разные матрицы
    //std::list<XMMATRIX> list = { ..., ... }; cube->Render(list); 
    virtual void Render(std::list<DirectX::XMMATRIX> matrices);
    virtual void Render();
    virtual void Render(DirectX::XMMATRIX matrix);

private: 
    std::list<MeshUpdater> meshUpdaters;

	ID3D11Buffer* g_d3dVertexBuffer = nullptr;
	ID3D11Buffer* g_d3dIndexBuffer = nullptr;
    ID3D11DeviceContext* deviceContext;
    //todo: ƒјЋ≈≈ Ё“» буфферы должны быть в классе дл€ Shader'а
    ID3D11Resource* d3dConstantBuffer;
	MeshConstantBuffer constantBuffer;
    
    VertexPosColor* g_Vertices;
    int verticesCount;
    WORD* g_Indices;
    int indicesCount;
};

