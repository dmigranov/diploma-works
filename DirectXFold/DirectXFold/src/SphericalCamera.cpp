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
		/*pitchTotal += m_pitch;
		pitchTotal = std::max<double>(-pitchLimit, pitchTotal);
		pitchTotal = std::min<double>(+pitchLimit, pitchTotal);
		if (pitchTotal == pitchLimit || pitchTotal == -pitchLimit)
			m_pitch = 0;*/

		/*m_view = (Matrix)m_view 
			* SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z) 
			* SphericalRotationXZ(m_yaw) * SphericalRotationYZ(m_pitch);*/
		
		m_view = SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z) * SphericalRotationXZ(m_yaw) * SphericalRotationYZ(m_pitch);

	
		//этот вариант хорошо упавл€ет мышкой (без перемещени€...) работает
		//m_view = SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z) * SphericalRotationXZ(m_yaw) * SphericalRotationYZ(m_pitch);
		
		//первые три члена - аналог трансл€ции. —начала перемещаем камеру в (0 0 0 1)
		/*m_position = Vector3::Zero;
		m_pitch = 0;
		m_yaw = 0;*/



		//todo: разобратьс€ с пор€дком. не должен ли он быть оьратынм?
//m_view точно должна сто€ть на первом месте!
/*Matrix temp = (1 - abs(pitchTotal / pitchLimit)) * SphericalRotationXZ(m_yaw) + abs(pitchTotal / pitchLimit) * SphericalRotationXY(-m_yaw);
m_view = (Matrix)m_view * SphericalRotationYZ(m_pitch) * temp *
	SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z);
	*/

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
	return ell;
}

Vector4 SphericalCamera::GetPosition()
{
	return XMVector4Transform(spherePos, m_view);
}

//v = dx dy dz (градусы)
void SphericalCamera::Move(Vector3 v3)
{
	//m_position = v3;	//+= - если полное создание матрицы

	Vector4 pos = XMVector4Transform(Vector4(0.f, 0.f, 1.f, 0.f), SphericalRotationXZ(m_yaw) * SphericalRotationYZ(m_pitch) * SphericalRotationXW(v3.x) * SphericalRotationYW(v3.y) * SphericalRotationZW(v3.z));
	m_position += GetSphericalFromCartesian(pos.x, pos.y, pos.z, pos.w)/100;

	m_viewDirty = true;
}


//xyzw
XMFLOAT3 SphericalCamera::GetSphericalFromCartesian(float x4, float x3, float x2, float x1)
{
	float x42 = x4 * x4;
	float x22 = x2 * x2;
	float x32 = x3 * x3;

	float a1 = acosf(x1);
	if (x2 == 0 && x3 == 0 && x4 == 0)
		if (x1 > 0)
			return Vector3(a1, 0, 0);
		else
			return Vector3(a1, XM_PI, XM_PI);

	float a2 = acosf(x2 / sqrtf(x22 + x32 + x42));
	if (x3 == 0 && x4 == 0)
		if (x2 > 0)
			return Vector3(a1, a2, 0);
		else
			return Vector3(a1, a2, XM_PI);

	float a3;
	if (x4 >= 0)
		a3 = acosf(x3 / sqrtf(x32 + x42));
	else
		a3 = XM_2PI - acosf(x3 / sqrtf(x32 + x42));

	return XMFLOAT3(a1, a2, a3);
}


