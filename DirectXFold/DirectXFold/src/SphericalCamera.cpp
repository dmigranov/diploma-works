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
	if (m_viewDirty)
	{
		/*float y = sinf(m_pitch);
		float r = cosf(m_pitch);
		float z = r * cosf(m_yaw);
		float x = r * sinf(m_yaw);

		Vector3 lookAt = m_position + Vector3(x, y, z);

		m_view = XMMatrixLookAtLH(m_position, lookAt, Vector3::Up);*/
		m_view = SphericalRotationXW(m_position.x + XM_PI) * SphericalRotationZW(m_position.z + XM_PI);
	}
	return m_view;
}

const XMMATRIX& SphericalCamera::GetProj()
{
	m_proj = BananaProjectionMatrix(m_nearPlane);

	return m_proj;
}

const XMMATRIX& SphericalCamera::GetFrontProj()
{
	// TODO: вставьте здесь оператор return
}

const XMMATRIX& SphericalCamera::GetBackProj()
{
	// TODO: вставьте здесь оператор return
}


