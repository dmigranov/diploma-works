#include "pch.h"
#include "SphericalCamera.h"

//todo: сделать такую навигацию, чтобы можно было подойти к икосаэдру СБОКУ!

SphericalCamera::SphericalCamera() 
{
	m_view = Matrix::Identity;
}
const XMMATRIX& SphericalCamera::GetView()
{
	if (m_viewDirty)
	{

		//todo: разобраться с порядком. не должен ли он быть оьратынм?
		//m_view точно должна стоять на первом месте!

		pitchTotal += m_pitch;
		pitchTotal = std::max<double>(-pitchLimit, pitchTotal);
		pitchTotal = std::min<double>(+pitchLimit, pitchTotal);
		if (pitchTotal == pitchLimit || pitchTotal == -pitchLimit)
			m_pitch = 0;

		
		m_view = (Matrix)m_view
			* SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z)
			* SphericalRotationXZ(m_yaw) * SphericalRotationYZ(m_pitch) * SphericalRotationXY(m_roll) ;
		

		m_position = Vector3::Zero;
		m_pitch = 0;
		m_yaw = 0;
		m_roll = 0;

		//этот вариант хорошо упавляет мышкой (без перемещения...) работает
		//m_view = SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z) * SphericalRotationXZ(m_yaw) * SphericalRotationYZ(m_pitch);

		/*T = SphericalRotationZW(-m_position.z) * SphericalRotationYW(-m_position.y) * SphericalRotationXW(-m_position.x) * T;
		R = R * SphericalRotationYZ(-m_pitch) * SphericalRotationXZ(-m_yaw);
		cameraTransform = R * T;
		spherePos = XMVector4Transform(Vector4(0, 0, 0, 1), cameraTransform);*/

		
/*Matrix temp = (1 - abs(pitchTotal / pitchLimit)) * SphericalRotationXZ(m_yaw) + abs(pitchTotal / pitchLimit) * SphericalRotationXY(-m_yaw);
m_view = (Matrix)m_view * SphericalRotationYZ(m_pitch) * temp *
	SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z);
	*/

	}
	return m_view;
}

//todo: 
//2. перемещатаься в одной плоскости (проецирование)

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
	return XMVector4Transform(spherePos, ((Matrix)m_view).Invert());
	return spherePos;
}

//v = dx dy dz (градусы)
void SphericalCamera::Move(Vector3 v3)
{
	m_position = v3;	//+= - если полное создание матрицы

	//Vector4 pos = XMVector4Transform(Vector4(1.f, 0.f, 0.f, 0.f), SphericalRotationXZ(m_yaw) * SphericalRotationYZ(m_pitch) * SphericalRotationXW(v3.x));
	//m_position += GetSphericalFromCartesian(pos.x, pos.y, pos.z, pos.w)/100;

	//Vector4 newCameraPos = XMVector4Transform(spherePos, SphericalRotationZW(v3.z) * SphericalRotationYW(v3.y) * SphericalRotationXW(v3.x));
	//m_position += Vector3(newCameraPos.x, newCameraPos.y, newCameraPos.z);

	m_viewDirty = true;
}

void SphericalCamera::ChangeRoll(double roll)
{
	m_roll = roll;
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


