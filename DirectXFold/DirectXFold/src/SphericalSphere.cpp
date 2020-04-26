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

	vertices.push_back({ XMFLOAT4(0.f, radius, 0.f, height), color, XMFLOAT2(0.f, 0.f) });

    for (int i = 1; i <= stackCount - 1; i++) {
        auto phi = i * phiStep;
        for (int j = 0; j <= sliceCount; j++) {
            auto theta = j * thetaStep;
            XMFLOAT4 pos(
                (radius * sinf(phi) * cosf(theta)),
                (radius * cosf(phi)),
                (radius * sinf(phi) * sinf(theta)),
                height
                );

            auto uv = XMFLOAT2(theta / XM_2PI, phi / XM_PI);
            vertices.push_back({pos, color, uv });
        }

        vertices.push_back({ XMFLOAT4(0.f, -radius, 0.f, height), color, XMFLOAT2(0.f, 1.f) });
    }
}
