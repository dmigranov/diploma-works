#pragma once

class Mesh
{
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
        DirectX::XMMATRIX m_world;
    };


    // Vertex data for a colored cube.
    struct VertexPosColor
    {
        DirectX::XMFLOAT4 Position;  //координаты точки в четырехмерном пространстве
        DirectX::XMFLOAT4 Color;
    };

    

    DirectX::XMMATRIX GetWorldMatrix();
    void SetWorldMatrix(DirectX::XMMATRIX world);
    void SetConstants(MeshConstantBuffer constantBuffer);

    void SetParent(Mesh* parent);

    void AddUpdater(MeshUpdater updater);
    void Update(float deltaTime);

    virtual void Render(std::list<DirectX::XMMATRIX> matrices) = 0;
    virtual void Render() = 0;
    virtual void Render(DirectX::XMMATRIX matrix) = 0;


protected:
    std::list<MeshUpdater> meshUpdaters;


    Mesh* parentMesh = nullptr;
    MeshConstantBuffer constantBuffer;

};

