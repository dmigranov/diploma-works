#include "pch.h"

using namespace DirectX::SimpleMath;
using namespace DirectX;

//rotation around plane XY which stays invariant
static Matrix SphericalRotationZW(double d)
{
	return Matrix(	1.f, 0.f, 0.f, 0.f,
					0.f, 1.f, 0.f, 0.f,
					0.f, 0.f, cosf(d), sinf(d),
					0.f, 0.f, sinf(d), cosf(d));
}		