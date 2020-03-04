#pragma once
#include "Camera.h"
class SphericalCamera :
	public Camera
{
	const XMMATRIX& GetView();
};

