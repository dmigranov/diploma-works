#pragma once
#include "MeshUpdater.h"
using namespace DirectX;

class Mesh
{
    //todo: добавить parent mesh (возможно null)
    friend class Line;
public:

    class MeshUpdater
    {
    public:
        MeshUpdater(std::function<DirectX::SimpleMath::Matrix(DirectX::SimpleMath::Matrix)> func);
    private:
        std::function<DirectX::SimpleMath::Matrix(DirectX::SimpleMath::Matrix)> m_func;
    };

    struct MeshConstantBuffer	//=Object Constant Buffer
    {
        XMMATRIX m_world;
        XMMATRIX m_morph;
    };

    // Vertex data for a colored cube.
    struct VertexPosColor
    {
        XMFLOAT4 Position;  //координаты точки в четырехмерном пространстве
        XMFLOAT3 Color;
    };

    Mesh(int nv, VertexPosColor vertices[], int ni, WORD indices[]);
    ~Mesh();
    Mesh(int nv, VertexPosColor vertices[], int ni, WORD indices[], XMMATRIX world);
	void SetWorldMatrix(XMMATRIX world);
    void SetConstants(MeshConstantBuffer constantBuffer);
    void SetConstants(XMMATRIX world, XMMATRIX morph);
    void Move(float x, float y, float z);
    XMMATRIX GetWorldMatrix();

    //можно рисовать один и тот же меш использу€ разные матрицы
    //std::list<XMMATRIX> list = { ..., ... }; cube->Render(list); 
    virtual void Render(std::list<XMMATRIX> matrices);
    virtual void Render();
    virtual void Render(XMMATRIX matrix);
    Mesh * Clone();

private:
    std::list<MeshUpdater> meshUpdaters;

	ID3D11Buffer* g_d3dVertexBuffer = nullptr;
	ID3D11Buffer* g_d3dIndexBuffer = nullptr;
    ID3D11DeviceContext* deviceContext;
    //todo: ƒјЋ≈≈ Ё“» буфферы должны быть в классе дл€ Shader'а
    ID3D11Resource* d3dConstantBuffer;
	MeshConstantBuffer constantBuffer;

    
    /*VertexPosColor g_Vertices[8] =
    {
        { XMFLOAT4(-1.0f, -1.0f, -1.0f, 1.0f), XMFLOAT3(0.0f, 0.0f, 0.0f) }, // 0
        { XMFLOAT4(-1.0f,  1.0f, -1.0f, 1.0f), XMFLOAT3(0.0f, 1.0f, 0.0f) }, // 1
        { XMFLOAT4(1.0f,  1.0f, -1.0f, 1.0f), XMFLOAT3(1.0f, 1.0f, 0.0f) }, // 2
        { XMFLOAT4(1.0f, -1.0f, -1.0f, 1.0f), XMFLOAT3(1.0f, 0.0f, 0.0f) }, // 3
        { XMFLOAT4(-1.0f, -1.0f,  1.0f, 1.0f), XMFLOAT3(0.0f, 0.0f, 1.0f) }, // 4
        { XMFLOAT4(-1.0f,  1.0f,  1.0f, 1.0f), XMFLOAT3(0.0f, 1.0f, 1.0f) }, // 5
        { XMFLOAT4(1.0f,  1.0f,  1.0f, 1.0f), XMFLOAT3(1.0f, 1.0f, 1.0f) }, // 6
        { XMFLOAT4(1.0f, -1.0f,  1.0f, 1.0f), XMFLOAT3(1.0f, 0.0f, 1.0f) }  // 7
    };

    WORD g_Indicies[36] =
    {
        0, 1, 2, 0, 2, 3,
        4, 6, 5, 4, 7, 6,
        4, 5, 1, 4, 1, 0,
        3, 2, 6, 3, 6, 7,
        1, 5, 6, 1, 6, 2,
        4, 0, 3, 4, 3, 7
    };*/
    
    VertexPosColor* g_Vertices;
    int verticesCount;
    WORD* g_Indices;
    int indicesCount;
};

