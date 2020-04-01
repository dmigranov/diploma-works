#include "pch.h"
#include "SphericalCube.h"
#include "Game.h"

SphericalCube::SphericalCube(double wSec) : SphericalCube(wSec, Matrix::Identity)
{
}

SphericalCube::SphericalCube(double wSec, XMMATRIX world) : SphericalCube(wSec, Matrix::Identity, nullptr)
{
}

SphericalCube::SphericalCube(double wSec, XMFLOAT4* colors) : SphericalCube(wSec, Matrix::Identity, colors)
{
}

SphericalCube::SphericalCube(double wSec, XMMATRIX world, XMFLOAT4* colors)
{
	//fixed coord - всегда w!

    double cubeCoord = sqrt((1 - wSec * wSec)/3.f);
    /*Mesh::VertexPosColor vertices[] = {
        { XMFLOAT4(0.f,  0.f, -inv, wSec), colors[0] }, // 0
        { XMFLOAT4(0.f,  0.f, inv, wSec), colors[1]  }, // 1
        { XMFLOAT4(0.f, -inv, 0.f, wSec), colors[2]  }, // 2
        { XMFLOAT4(0.f,  inv, 0.f, wSec), colors[3]  }, // 3
        { XMFLOAT4(-inv,  0.f, 0.f, wSec), colors[4]  }, // 4
        { XMFLOAT4(inv,  0.f, 0.f, wSec), colors[5]  }, // 5
    };
    g_Vertices = vertices;
    verticesCount = _countof(vertices);

    if (section < 0)
        world *= Matrix(1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, -1, 0,
            0, 0, 0, 1);*/
}
