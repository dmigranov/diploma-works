#pragma once
#include "Camera.h"
#include "SphericalMath.h"

class SphericalCamera :
	public Camera
{
public:
	SphericalCamera();
	const XMMATRIX& GetView() override;
	const XMMATRIX& GetAntipodalView();
	const XMMATRIX& GetProj() override;
	const XMMATRIX& GetFrontProj();
	const XMMATRIX& GetBackProj();
	const XMMATRIX& GetEllipticalProj();

	Vector4 GetPosition() override;
	void Move(Vector3 v) override;
	void ChangeRoll(double roll) override;


private:
	Vector4 spherePos = Vector4(0, 0, 0, 1);
	double pitchTotal = 0;
	const float pitchLimit = DirectX::XM_PI / 2.0f - 0.01f;

	double m_roll = 0;

	Matrix viewTemp = Matrix::Identity;

	XMFLOAT3 GetSphericalFromCartesian(float x4, float x3, float x2, float x1);
};

