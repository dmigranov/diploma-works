#pragma once
using namespace DirectX;

class Mesh
{
    //todo: добавить parent mesh (возможно null)
    friend class Line;
    friend class SphericalOctahedron;
    friend class SphericalDodecahedron;


public:

    class MeshUpdater
    {
    public:
        MeshUpdater(std::function<DirectX::SimpleMath::Matrix(DirectX::SimpleMath::Matrix, float delta)> func);
        DirectX::SimpleMath::Matrix operator()(DirectX::SimpleMath::Matrix in, float delta);
    private:
        std::function<DirectX::SimpleMath::Matrix(DirectX::SimpleMath::Matrix, float delta)> m_func;
    };

    struct MeshConstantBuffer	//=Object Constant Buffer
    {
        XMMATRIX m_world;
    };

    // Vertex data for a colored cube.
    struct VertexPosColor
    {
        XMFLOAT4 Position;  //координаты точки в четырехмерном пространстве
        XMFLOAT4 Color;
    };

    Mesh();
    Mesh(int nv, VertexPosColor* vertices, int ni, WORD* indices);
    Mesh(int nv, VertexPosColor* vertices, int ni, WORD* indices, XMMATRIX world);

    ~Mesh();

    XMMATRIX GetWorldMatrix();
	void SetWorldMatrix(XMMATRIX world);
    void SetConstants(MeshConstantBuffer constantBuffer);
    void SetParent(Mesh* parent);

    void Move(float x, float y, float z);

    void AddUpdater(MeshUpdater updater);
    void Update(float deltaTime);


    //можно рисовать один и тот же меш использу€ разные матрицы
    //std::list<XMMATRIX> list = { ..., ... }; cube->Render(list); 
    virtual void Render(std::list<XMMATRIX> matrices);
    virtual void Render();
    virtual void Render(XMMATRIX matrix);
    Mesh * Clone();

private:
    Mesh* parentMesh = nullptr;
    
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

