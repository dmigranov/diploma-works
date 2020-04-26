#include "pch.h"
#include "SphericalSphere.h"

using namespace DirectX;

SphericalSphere::SphericalSphere(float radius, int sliceCount, int stackCount, DirectX::XMFLOAT4 color)
{
	auto phiStep = XM_PI / stackCount;
	auto thetaStep = XM_2PI / sliceCount;

	std::list<VertexPosColor> vertices;
	std::list<DWORD> indices;

	float height = 1.f - radius * radius;

	vertices.push_back({ XMFLOAT4(0.f, radius, 0.f, height), color, XMFLOAT2(0.f ,0.f) });

}
