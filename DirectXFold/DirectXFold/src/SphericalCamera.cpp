#include "pch.h"
#include "SphericalCamera.h"

//todo: сделать такую навигацию, чтобы можно было подойти к икосаэдру —Ѕќ ”!

SphericalCamera::SphericalCamera() 
{
	m_view = Matrix::Identity;
}
const XMMATRIX& SphericalCamera::GetView()
{
	//todo: возможно, стоит подумать над юзабилити, чтобы вращение всегда было одинаково
	//todo: умножить на XZ и YZ! в зависимости от мыши
	if (m_viewDirty)
	{
		m_view = (Matrix)m_view * SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z) * SphericalRotationXZ(m_yaw) * SphericalRotationYZ(m_pitch);
		//первые три члена - аналог трансл€ции. —начала перемещаем камеру в (0 0 0 1)
		m_position = Vector3::Zero;
	}
	return m_view;
}

const XMMATRIX& SphericalCamera::GetAntipodalView()
{
	return -(m_view);
}

const XMMATRIX& SphericalCamera::GetProj()
{
	m_proj = BananaProjectionMatrix(m_nearPlane);

	return m_proj;
}

const XMMATRIX& SphericalCamera::GetFrontProj()
{
	double sw = m_outputWidth;
	double sh = m_outputHeight;

	double vw = m_viewport.w;
	double vh = m_viewport.h;

	float aspect = (float)((sw * vw) / (sh * vh));
	Matrix front = BananaProjectionMatrixFrontHalf(m_fovY, aspect, m_nearPlane);
	//static Matrix front = BananaProjectionMatrixFrontHalf(m_nearPlane);
	return front;
}

const XMMATRIX& SphericalCamera::GetBackProj()
{
	double sw = m_outputWidth;
	double sh = m_outputHeight;

	double vw = m_viewport.w;
	double vh = m_viewport.h;

	float aspect = (float)((sw * vw) / (sh * vh));
	Matrix back = BananaProjectionMatrixBackHalf(m_fovY, aspect, m_nearPlane);
	//static Matrix back = BananaProjectionMatrixBackHalf(m_nearPlane);
	return back;
}

const XMMATRIX& SphericalCamera::GetEllipticalProj()
{
	double sw = m_outputWidth;
	double sh = m_outputHeight;

	double vw = m_viewport.w;
	double vh = m_viewport.h;

	float aspect = (float)((sw * vw) / (sh * vh));
	Matrix ell = BananaProjectionMatrix(m_fovY, aspect, m_nearPlane);
	//static Matrix ell = BananaProjectionMatrix(m_nearPlane);
	return ell;
}

Vector4 SphericalCamera::GetPosition()
{
	return XMVector4Transform(spherePos, m_view);
}

//v = dx dy dz (градусы)
void SphericalCamera::Move(Vector3 v3)
{
	/*Vector4 v(v3.x, v3.y, v3.z, 1.);
	Vector4 move = XMVector4Transform(v, SphericalRotationYZ(-m_pitch) * SphericalRotationXZ(-m_yaw));
	Vector3 moveTemp = Vector3(move.x, move.y, move.z);

	m_position += moveTemp;*/
	m_position = v3;

	m_viewDirty = true;
}




