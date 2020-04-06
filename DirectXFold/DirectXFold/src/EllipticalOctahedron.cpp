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
