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
		m_view = SphericalRotationXW(m_position.x) * SphericalRotationZW(m_position.z);
	}
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