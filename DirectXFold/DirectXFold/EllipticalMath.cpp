#include "pch.h"
#include "EllipticalMath.h"

using namespace DirectX::SimpleMath;
using namespace DirectX;


Matrix EllipticalBananaProjectionMatrix(double z0)
{
	return Matrix(1.f, 0.f, 0.f, 0.f,
		0.f, 1.f, 0.f, 0.f,
		0.f, 0.f, 0.5f, 1.f,
		0.f, 0.f, -z0 / 2.f, 0);
}