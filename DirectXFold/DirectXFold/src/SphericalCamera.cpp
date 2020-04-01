#include "pch.h"
#include "SphericalCamera.h"

//todo: сделать такую навигацию, чтобы можно было подойти к икосаэдру СБОКУ!

SphericalCamera::SphericalCamera()
{
	m_view = Matrix::Identity;
	m_view_const = Matrix::Identity;

}
const XMMATRIX& SphericalCamera::GetView()
{
	//todo: возможно, стоит подумать над юзабилити, чтобы вращение всегда было одинаково
	if (m_viewDirty)
	{
		m_view = SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z) * SphericalRotationXZ(XZRot);

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
	static Matrix front = BananaProjectionMatrixFrontHalf(m_nearPlane);
	return front;
}

const XMMATRIX& SphericalCamera::GetBackProj()
{
	static Matrix back = BananaProjectionMatrixBackHalf(m_nearPlane);
	return back;
}

Vector4 SphericalCamera::GetPosition()
{
	return XMVector4Transform(spherePos, m_view);
}

//v = dx dy dz dw
void SphericalCamera::Move(Vector4 v)
{
	Vector4 move = XMVector4Transform(v, SphericalRotationXZ(-XZRot));
	Vector3 moveTemp = Vector3(move.x, move.y, move.z);

	m_position += moveTemp;

	m_viewDirty = true;
}

void SphericalCamera::SetXZRotation(double rot)
{
	XZRot += rot;
}


void SphericalCamera::ChangePitchYaw(double pitch, double yaw)
{
	this->Camera::ChangePitchYaw(pitch, yaw);
	//todo: сделать поправки на поворот как в Move
	m_position += Vector3(yaw, pitch, 0);
}

