#include "pch.h"
#include "Camera.h"
using namespace DirectX::SimpleMath;

Camera::Camera() : m_viewport(0.0f, 0.0f, 1.0f, 1.0f),
m_pitch(0), m_yaw(0),
m_lookAt(Vector3(0, 0, 0))
{}

void Camera::SetViewport(Rect rect)
{
	m_viewport = rect;
}

void Camera::SetOutputSize(double outputWidth, double outputHeight)
{
	m_outputWidth = outputWidth;
	m_outputHeight = outputHeight;
}

const Matrix& Camera::GetView()
{
	//todo: добавить булеву переменную - dirty flag

	float y = sinf(m_pitch);
	float r = cosf(m_pitch);
	float z = r * cosf(m_yaw);
	float x = r * sinf(m_yaw);

	Vector3 lookAt = m_position + Vector3(x, y, z);

	m_view = XMMatrixLookAtLH(m_position, lookAt, Vector3::Up);
	return m_view;
}

const Matrix& Camera::GetProj()
{
	RecalculateMatrixProj();
	return m_proj;
}

void Camera::RecalculateMatrixProj()
{
	double sw = m_outputWidth;
	double sh = m_outputHeight;

	double vw = m_viewport.w;
	double vh = m_viewport.h;

	float aspect = (float)((sw * vw) / (sh * vh));
	
	m_proj = XMMatrixPerspectiveFovLH(m_fovY, aspect, m_nearPlane, m_farPlane);
}

void Camera::SetPosition(double x, double y, double z)
{
	m_position = Vector3(x, y, z);
}

void Camera::SetPosition(Vector3 v)
{
	m_position = v;
}

void Camera::SetLookAt(Vector3 v)
{
	m_lookAt = v;
	GetView();
}

void Camera::Move(Vector3 v)
{
	Quaternion q = Quaternion::CreateFromYawPitchRoll(m_yaw, -m_pitch, 0.f);
	Vector3 move = Vector3::Transform(v, q);
	m_position += move;
}

Vector3 Camera::GetPosition()
{
	return m_position;
}

void Camera::ChangePitchYaw(double deltaPitch, double deltaYaw)
{
	m_pitch += deltaPitch;
	m_yaw += deltaYaw;

	// limit pitch to straight up or straight down
	// with a little fudge-factor to avoid gimbal lock
	double limit = DirectX::XM_PI / 2.0f - 0.01f;
	m_pitch = std::max<double>(-limit, m_pitch);
	m_pitch = std::min<double>(+limit, m_pitch);

	// keep longitude in sane range by wrapping
	if (m_yaw > DirectX::XM_PI)
	{
		m_yaw -= DirectX::XM_PI * 2.0f;
	}
	else if (m_yaw < -DirectX::XM_PI)
	{
		m_yaw += DirectX::XM_PI * 2.0f;
	}
}

void Camera::SetFovY(float fovY)
{
	if (fovY < 1.0f)
		m_fovY = 1.0f;
	else if (fovY > 179.0f)
		m_fovY = 179.0f;
	else
		m_fovY = fovY;
}

void Camera::SetNearPlane(float nearPlane)
{
	m_nearPlane = nearPlane;
}

void Camera::SetFarPlane(float farPlane)
{
	m_farPlane = farPlane;
}
