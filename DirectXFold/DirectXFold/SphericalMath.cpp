#include "pch.h"
#include "SphericalMath.h"


//z0 - radians
//gives 90 degree FOV
Matrix BananaProjectionMatrix(double z0)
{
	return Matrix(1.f, 0.f, 0.f, 0.f,
		0.f, 1.f, 0.f, 0.f,
		0.f, 0.f, 0.5f, 1.f,
		0.f, 0.f, -z0/2.f, 0);
}

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