#include "pch.h"
#include "SphericalDodecahedron.h"
#include "Game.h"

XMFLOAT4 GenerateRandomColor()
{
    return XMFLOAT4(float(rand()) / float(RAND_MAX), float(rand()) / float(RAND_MAX), float(rand()) / float(RAND_MAX), 1.f);
}

SphericalDodecahedron::SphericalDodecahedron(double wSec, XMMATRIX world) : SphericalDodecahedron(FixedCoordinate::FC_W, wSec, world)
{}

SphericalDodecahedron::SphericalDodecahedron(double wSec) : SphericalDodecahedron(wSec, XMMatrixIdentity())
{}
