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
	void ChangePitchYawRoll(double deltaPitch, double deltaYaw, double deltaRoll) override;


private:
	Vector4 spherePos = Vector4(0, 0, 0, 1);

	double pitchDelta = 0, yawDelta = 0;
	double pitchLimit = DirectX::XM_PI / 2.0f - 0.01f;

	Matrix T = Matrix::Identity; // represents the translation of the camera in world space
	Matrix R = Matrix::Identity, RInv = Matrix::Identity, RYaw = Matrix::Identity, RPitch = Matrix::Identity; // represents camera orientation
	Matrix cameraTransform = Matrix::Identity;

	XMFLOAT3 GetSphericalFromCartesian(float x4, float x3, float x2, float x1);
};

