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

SphericalDodecahedron::SphericalDodecahedron(FixedCoordinate coord, double section) : SphericalDodecahedron(coord, section, XMMatrixIdentity())
{}

SphericalDodecahedron::SphericalDodecahedron(FixedCoordinate coord, double section, XMMATRIX world)
{
    this->fc = coord;
    sectionHeight = section;

    double inv = sqrt(1. - section * section);

}

void SphericalDodecahedron::SetSectionHeight(double newSectionHeight)
{
    //todo
}

double SphericalDodecahedron::GetSectionHeight()
{
    //todo
    return 0.0;
}

