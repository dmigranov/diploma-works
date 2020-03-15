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
	Vector4 GetPosition() override;
	void Move(Vector4 v) override;
	void ChangePitchYaw(double pitch, double yaw) override;

private:
	Vector4 spherePos = Vector4(0, 0, 0, 1);

};

