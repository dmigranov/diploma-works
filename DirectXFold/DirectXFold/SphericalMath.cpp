#include "pch.h"
#include "SphericalMath.h"



//rotation around plane XY which stays invariant
Matrix SphericalRotationZW(double d)
{
	return Matrix(	1.f, 0.f, 0.f, 0.f,
					0.f, 1.f, 0.f, 0.f,
					0.f, 0.f, cosf(d), sinf(d),
					0.f, 0.f, -sinf(d), cosf(d));
}		

Matrix SphericalRotationXW(double d)
{
	return Matrix(	cosf(d), 0.f, 0.f, sinf(d),
					0.f, 1.f, 0.f, 0.f,
					0.f, 0.f, 1.f, 0.f,
					-sinf(d), 0.f, 0.f, cosf(d));
}