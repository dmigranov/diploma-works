#include "pch.h"
#include "SphericalCamera.h"

//todo: сделать такую навигацию, чтобы можно было подойти к икосаэдру —Ѕќ ”!

SphericalCamera::SphericalCamera() 
{
	m_view = Matrix::Identity;
}
const XMMATRIX& SphericalCamera::GetView()
{
	if (m_viewDirty)
	{
		pitchTotal += m_pitch;
		pitchTotal = std::max<double>(-pitchLimit, pitchTotal);
		pitchTotal = std::min<double>(+pitchLimit, pitchTotal);
		if (pitchTotal == pitchLimit || pitchTotal == -pitchLimit)
			m_pitch = 0;

		//todo: разобратьс€ с пор€дком. не должен ли он быть оьратынм?
		//m_view точно должна сто€ть на первом месте!
		/*Matrix temp = (1 - abs(pitchTotal / pitchLimit)) * SphericalRotationXZ(m_yaw) + abs(pitchTotal / pitchLimit) * SphericalRotationXY(-m_yaw);
		m_view = (Matrix)m_view * SphericalRotationYZ(m_pitch) * temp *
			SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z);
			*/
		
		m_view = (Matrix)m_view * SphericalRotationXZ(m_yaw) *SphericalRotationYZ(m_pitch)  *
					SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z) ;
		



		
		
		//первые три члена - аналог трансл€ции. —начала перемещаем камеру в (0 0 0 1)
		m_position = Vector3::Zero;
		m_pitch = 0;
		m_yaw = 0;
	}
	return m_view;
}

//todo: 
//2. перемещатаьс€ в одной плоскости (проецирование)

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
	m_position = v3;

	Vector4 pos = XMVector4Transform(spherePos, m_view);

	m_viewDirty = true;
}




