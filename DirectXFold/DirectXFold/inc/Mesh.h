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

    

    virtual DirectX::XMMATRIX GetWorldMatrix() = 0;
    virtual void SetWorldMatrix(DirectX::XMMATRIX world) = 0;
    virtual void SetConstants(MeshConstantBuffer constantBuffer) = 0;
    virtual void SetParent(Mesh* parent) = 0;

    virtual void Move(float x, float y, float z) = 0;

    virtual void AddUpdater(MeshUpdater updater) = 0;
    virtual void Update(float deltaTime) = 0;

    virtual void Render(std::list<DirectX::XMMATRIX> matrices) = 0;
    virtual void Render() = 0;
    virtual void Render(DirectX::XMMATRIX matrix) = 0;

};

