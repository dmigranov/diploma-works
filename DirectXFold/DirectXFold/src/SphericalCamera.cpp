#include "pch.h"
#include "SphericalCamera.h"

const XMMATRIX& SphericalCamera::GetView()
{
	if (m_viewDirty)
	{
		float y = sinf(m_pitch);
		float r = cosf(m_pitch);
		float z = r * cosf(m_yaw);
		float x = r * sinf(m_yaw);

		Vector3 lookAt = m_position + Vector3(x, y, z);

		m_view = XMMatrixLookAtLH(m_position, lookAt, Vector3::Up);
		m_view = SphericalRotationXW(m_position.x) * SphericalRotationZW(m_position.z);
	}
	return m_view;
}

const XMMATRIX& SphericalCamera::GetProj()
{
	/*double sw = m_outputWidth;
	double sh = m_outputHeight;

	double vw = m_viewport.w;
	double vh = m_viewport.h;

	float aspect = (float)((sw * vw) / (sh * vh));*/

	m_proj = BananaProjectionMatrix(m_nearPlane);

	return m_proj;
}


