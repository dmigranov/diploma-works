#include "pch.h"
#include "SphericalEllipsoid.h"

using namespace DirectX;
using namespace DirectX::SimpleMath;

SphericalEllipsoid::SphericalEllipsoid(float a, float b, float c, int sliceCount, int stackCount, Texture* texture) : SphericalEllipsoid(a, b, c, sliceCount, stackCount, texture, Matrix::Identity)
{}

SphericalEllipsoid::SphericalEllipsoid(float a, float b, float c, int sliceCount, int stackCount, Texture* texture, DirectX::XMMATRIX world)
{
	auto phiStep = XM_PI / stackCount;
	auto thetaStep = XM_2PI / sliceCount;

	std::vector<VertexPosColor> vertices;
	std::vector<WORD> indices;

	XMFLOAT4 color(0, 0, 0, 0);
    float cInv = sqrtf(1.f - c * c);
	vertices.push_back({ XMFLOAT4(0.f, c, 0.f, cInv), color, XMFLOAT2(0.f, 0.f) });


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
            vertices.push_back({ pos, color, uv });
        }

    }

    vertices.push_back({ XMFLOAT4(0.f, -c, 0.f, cInv), color, XMFLOAT2(0.f, 1.f) });

}
