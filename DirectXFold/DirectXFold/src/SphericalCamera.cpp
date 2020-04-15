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

		if (m_pitch == pitchLimit || m_pitch == -pitchLimit)
			pitchDelta = 0;
		
		//view - это обратная к cameraTransform.
		//чтобы найти камераТрансформ, сначала поворачиваем камеру (слева), потом перемещаем (справа)
		//но (ABC)-1 = C-1 B-1 A-1
		//поэтому при нахождении view вращения камеры СПРАВА
		
		//пич йоу:
		//для нахождения камера трансформ сначала pitch, потом yaw. то есть там pitch левее
		//но для view питч будет правее


		//T = SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z);
		//R = SphericalRotationXZ(yawDelta) * SphericalRotationYZ(pitchDelta) /**SphericalRotationXY(m_roll)*/;
		//m_view = (Matrix)m_view * T * R ;

		//todo: поразмыслить над интерпретацией и может ещё исправить
		//todo: roll



		//Matrix ROld = SphericalRotationXZ(m_yaw) * SphericalRotationYZ(m_pitch);
		//T = T * R * SphericalRotationZW(m_position.z) * SphericalRotationYW(m_position.y) * SphericalRotationXW(m_position.x) * R.Invert();
		//T = T * SphericalRotationZW(m_position.z) * SphericalRotationYW(m_position.y) * SphericalRotationXW(m_position.x) ;		T = T * R * SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z) * R.Invert();

		T = T * R * SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z) * RInv;
		m_view = T * R ;

		/*Matrix camera;
		T = SphericalRotationZW(-m_position.z) * SphericalRotationYW(-m_position.y) * SphericalRotationXW(-m_position.x);
		R = SphericalRotationYZ(-pitchDelta) * SphericalRotationXZ(-yawDelta);
		m_view = ((Matrix)m_view).Invert() * T * R;*/

		m_position = Vector3::Zero;
		pitchDelta = 0;
		yawDelta = 0;

		//этот вариант хорошо упавляет мышкой (без перемещения...) работает
		//m_view = SphericalRotationXW(m_position.x) * SphericalRotationYW(m_position.y) * SphericalRotationZW(m_position.z) * SphericalRotationXZ(m_yaw) * SphericalRotationYZ(m_pitch);

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
	m_position = v3;


	/*Vector4 zDir = Vector4(0, 0, v3.z, 0);
	zDir = XMVector4Transform(zDir, SphericalRotationYZ(-m_pitch) * SphericalRotationXZ(-m_yaw) * SphericalRotationZW(-m_position.z) * SphericalRotationYW(-m_position.y) * SphericalRotationXW(-m_position.x));
	m_position = m_position + zDir;*/

	//Vector4 pos = XMVector4Transform(Vector4(1.f, 0.f, 0.f, 0.f), SphericalRotationXZ(m_yaw) * SphericalRotationYZ(m_pitch) * SphericalRotationXW(v3.x));
	//m_position += GetSphericalFromCartesian(pos.x, pos.y, pos.z, pos.w)/100;

	//Vector4 newCameraPos = XMVector4Transform(spherePos, SphericalRotationZW(v3.z) * SphericalRotationYW(v3.y) * SphericalRotationXW(v3.x));
	//m_position += Vector3(newCameraPos.x, newCameraPos.y, newCameraPos.z);

	m_viewDirty = true;
}


void SphericalCamera::ChangePitchYawRoll(double deltaPitch, double deltaYaw, double deltaRoll)
{
	Camera::ChangePitchYawRoll(deltaPitch, deltaYaw, deltaRoll);
	pitchDelta = deltaPitch;
	yawDelta = deltaYaw;
	//RYaw = SphericalRotationXZ(m_yaw);
	//RPitch = SphericalRotationYZ(m_pitch);

	RInv = SphericalRotationYZ(-m_pitch) * SphericalRotationXZ(-m_yaw);
	R = SphericalRotationXZ(m_yaw) * SphericalRotationYZ(m_pitch);

	//R =  SphericalRotationXZ(m_yaw) * SphericalRotationYZ(m_pitch) * SphericalRotationXY(m_roll);


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


