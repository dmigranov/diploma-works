#pragma once
#include "Camera.h"
#include "SphericalMath.h"
class SphericalCamera :
	public Camera
{
public:
	const XMMATRIX& GetView() override;
	const XMMATRIX& GetAntipodalView();
	const XMMATRIX& GetProj() override;
	const XMMATRIX& GetFrontProj();
	const XMMATRIX& GetBackProj();
	Vector4 GetPosition() override;
private:
	Vector4 spherePos = Vector4(0, 0, 0, 1);

};

