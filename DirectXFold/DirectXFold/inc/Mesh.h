#pragma once
#include "Game.h"

using namespace DirectX;

class Mesh
{
public:
    struct MeshConstantBuffer	//=Object Constant Buffer
    {
        XMMATRIX m_world;
        XMMATRIX m_morph;
    };

    Mesh();
    Mesh(XMMATRIX world);
	void SetWorldMatrix(XMMATRIX world);
    void SetConstants(MeshConstantBuffer constantBuffer);
    void SetConstants(XMMATRIX world, XMMATRIX morph);

    void Render();


    // Vertex data for a colored cube.
    struct VertexPosColor
    {
        XMFLOAT4 Position;  //координаты точки в четырехмерном пространстве
        XMFLOAT3 Color;
    };

private:
	ID3D11Buffer* g_d3dVertexBuffer = nullptr;
	ID3D11Buffer* g_d3dIndexBuffer = nullptr;
    ID3D11DeviceContext* deviceContext;
    ID3D11Resource* d3dConstantBuffer;
	MeshConstantBuffer constantBuffer;

    VertexPosColor g_Vertices[8] =
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
    };
};

