#pragma once
#include "Camera.h"
#include "SphericalMath.h"
class SphericalCamera :
	public Camera
{
	const XMMATRIX& GetView() override;
	const XMMATRIX& GetAntipodalView();
	const XMMATRIX& GetProj() override;


};

