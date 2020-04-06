#include "pch.h"
#include "EllipticalOctahedron.h"
#include "Game.h"

EllipticalOctahedron::EllipticalOctahedron(double wSec)
{
}

EllipticalOctahedron::EllipticalOctahedron(double wSec, DirectX::XMFLOAT4* colors)
{
}

EllipticalOctahedron::EllipticalOctahedron(double wSec, DirectX::XMMATRIX world)
{
}

EllipticalOctahedron::EllipticalOctahedron(double wSec, DirectX::XMMATRIX world, DirectX::XMFLOAT4* colors)
{
	if (colors == nullptr)
	{
		colors = new XMFLOAT4[6];
		for (int i = 0; i < 6; i++)
			colors[i] = GenerateRandomColor();
	}

    double section = wSec;
    double inv = sqrt(1. - section * section);

    SphericalMesh::VertexPosColor vertices[] = {
        { XMFLOAT4(0.f,  0.f, -inv, section), colors[0] }, // 0
        { XMFLOAT4(0.f,  0.f, inv, section), colors[1]  }, // 1
        { XMFLOAT4(0.f, -inv, 0.f, section), colors[2]  }, // 2
        { XMFLOAT4(0.f,  inv, 0.f, section), colors[3]  }, // 3
        { XMFLOAT4(-inv,  0.f, 0.f, section), colors[4]  }, // 4
        { XMFLOAT4(inv,  0.f, 0.f, section), colors[5]  }, // 5
    };
    g_Vertices = vertices;
    verticesCount = _countof(vertices);

    if (section < 0)
        world *= Matrix(1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, -1, 0,
            0, 0, 0, 1);   //матрица отражения!

    WORD indices[] = {
            0, 2, 4,
            4, 3, 0,
            5, 2, 0,
            0, 3, 5,
            4, 2, 1,
            1, 3, 4,
            1, 2, 5,
            5, 3, 1,
    };

    auto& game = Game::GetInstance();
    auto device = game.g_d3dDevice;
    deviceContext = game.g_d3dDeviceContext;
    d3dConstantBuffer = game.g_d3dVSConstantBuffers[2];
    g_Indices = indices;

    indicesCount = _countof(indices);

    // Create and initialize the vertex buffer.
    D3D11_BUFFER_DESC vertexBufferDesc;
    ZeroMemory(&vertexBufferDesc, sizeof(D3D11_BUFFER_DESC));

    vertexBufferDesc.BindFlags = D3D11_BIND_VERTEX_BUFFER;  //how the buffer is bound to pipeline
    vertexBufferDesc.ByteWidth = sizeof(VertexPosColor) * verticesCount;
    vertexBufferDesc.CPUAccessFlags = 0;    // no CPU access is necessary
    vertexBufferDesc.Usage = D3D11_USAGE_DEFAULT;

    //used to specify the data that is used to initialize a buffer when it is created.
    D3D11_SUBRESOURCE_DATA resourceData;
    ZeroMemory(&resourceData, sizeof(D3D11_SUBRESOURCE_DATA));
    resourceData.pSysMem = g_Vertices; //A pointer to the data to initialize the buffer with.

    device->CreateBuffer(&vertexBufferDesc, &resourceData, &g_d3dVertexBuffer);

    // Create and initialize the index buffer.
    D3D11_BUFFER_DESC indexBufferDesc;
    ZeroMemory(&indexBufferDesc, sizeof(D3D11_BUFFER_DESC));

    indexBufferDesc.BindFlags = D3D11_BIND_INDEX_BUFFER;
    indexBufferDesc.ByteWidth = sizeof(WORD) * indicesCount;
    indexBufferDesc.CPUAccessFlags = 0;
    indexBufferDesc.Usage = D3D11_USAGE_DEFAULT;
    resourceData.pSysMem = g_Indices;

    device->CreateBuffer(&indexBufferDesc, &resourceData, &g_d3dIndexBuffer);

    constantBuffer.m_world = world;
}

void EllipticalOctahedron::SetSectionHeight(double newSectionHeight)
{
	//todo
}

double EllipticalOctahedron::GetSectionHeight()
{
	//todo
	return 0.0;
}

DirectX::XMFLOAT4 EllipticalOctahedron::GenerateRandomColor()
{
	return XMFLOAT4(float(rand()) / float(RAND_MAX), float(rand()) / float(RAND_MAX), float(rand()) / float(RAND_MAX), 1.f);
}
