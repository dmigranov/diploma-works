#include "pch.h"
#include "SphericalCamera.h"

const XMMATRIX& SphericalCamera::GetView()
{
	//todo: посмотреть как сделана камера в проекте для визуализации
	if (m_viewDirty)
	{
		/*float y = sinf(m_pitch);
		float r = cosf(m_pitch);
		float z = r * cosf(m_yaw);
		float x = r * sinf(m_yaw);

		Vector3 lookAt = m_position + Vector3(x, y, z);

		m_view = XMMatrixLookAtLH(m_position, lookAt, Vector3::Up);*/
		m_view = SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z) * SphericalRotationXW(m_yaw);
	}
	//spherePos = 
	return m_view;
}

const XMMATRIX& SphericalCamera::GetAntipodalView()
{
	//todo: так? с минусом картинка неправильная: при повортах указ. в одном направлении...
	return /*Matrix(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, -1)**/-Matrix(m_view);
}

const XMMATRIX& SphericalCamera::GetProj()
{
	m_proj = BananaProjectionMatrix(m_nearPlane);

	return m_proj;
}

const XMMATRIX& SphericalCamera::GetFrontProj()
{
	static Matrix front = BananaProjectionMatrixFrontHalf(0);
	return front;
}

const XMMATRIX& SphericalCamera::GetBackProj()
{
	static Matrix back = BananaProjectionMatrixBackHalf(0);
	return back;
}

Vector4 SphericalCamera::GetPosition()
{
	return XMVector4Transform(spherePos, m_view);
}

void SphericalCamera::Move(Vector4 v)
{
	//todo
	//Vector3 v = Vector3(v4.x, v4.y, v4.z);
	//Quaternion q = Quaternion::CreateFromYawPitchRoll(m_yaw, -m_pitch, 0.f);

	Vector4 move = XMVector4Transform(v, SphericalRotationXW(m_yaw));
	Vector3 moveTemp = Vector3(move.x, move.y, move.z);

	m_position += moveTemp;
	m_viewDirty = true;
}
