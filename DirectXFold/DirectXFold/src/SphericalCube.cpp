#include "pch.h"
#include "SphericalCube.h"
#include "Game.h"

XMFLOAT4 SphericalCube::GenerateRandomColor()
{
    return XMFLOAT4(float(rand()) / float(RAND_MAX), float(rand()) / float(RAND_MAX), float(rand()) / float(RAND_MAX), 1.f);
}

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

    if (colors == nullptr);
        //todo: generate random

    double cubeCoord = sqrt((1 - wSec * wSec)/3.f);
    Mesh::VertexPosColor vertices[] = {
        { XMFLOAT4(-cubeCoord,  -cubeCoord, -cubeCoord, wSec), colors[0] }, // 0
        { XMFLOAT4(-cubeCoord,  -cubeCoord, cubeCoord, wSec), colors[1]  }, // 1
        { XMFLOAT4(-cubeCoord,  cubeCoord, -cubeCoord, wSec), colors[2]  }, // 2
        { XMFLOAT4(-cubeCoord,  cubeCoord, cubeCoord, wSec), colors[3]  }, // 3
        { XMFLOAT4(cubeCoord,  -cubeCoord, -cubeCoord, wSec), colors[4]  }, // 4
        { XMFLOAT4(cubeCoord,  -cubeCoord, cubeCoord, wSec), colors[5]  }, // 5
        { XMFLOAT4(cubeCoord,  cubeCoord, -cubeCoord, wSec), colors[6]  }, // 6
        { XMFLOAT4(cubeCoord,  cubeCoord, cubeCoord, wSec), colors[7]  }, // 7
    };
    g_Vertices = vertices;
    verticesCount = _countof(vertices);

    if (wSec < 0)
        world *= Matrix(1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, -1, 0,
            0, 0, 0, 1);
}
