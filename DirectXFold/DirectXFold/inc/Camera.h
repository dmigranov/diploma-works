#pragma once
using namespace DirectX::SimpleMath;
using namespace DirectX;

class Camera
{
public:
	struct Rect
	{
		Rect(float x = 0.0f, float y = 0.0f, float w = 1.0f, float h = 1.0f)
			: x(x), y(y), w(w), h(h)
		{ }

		float x, y, w, h;
	};

	Camera();

	void SetBackgroundColor(float r, float g, float b);

	void SetPosition(double x, double y, double z);
	void SetPosition(Vector3 v);

	void SetLookAt(Vector3 v);

	virtual void Move(Vector3 v);
	virtual Vector4 GetPosition();

	virtual void ChangePitchYaw(double pitch, double yaw);
	virtual void ChangeRoll(double roll);

	// Установка вертикального угла обзора (в градусах)
	void SetFovY(float fovY);
	// Установка ближней отсекающей плоскости
	void SetNearPlane(float nearPlane);
	// Установка дальней отсекающей плоскости
	void SetFarPlane(float farPlane);

	/**
	* @brief Where on the screen is the camera rendered in normalized (!!!) coordinates
	* http://www.gamedev.net/page/resources/_/technical/game-programming/a-room-with-a-view-r3901.
	*/
	//todo: исправить Rectangle, там long!
	void SetViewport(Rect rect = Rect(0.0f, 0.0f, 1.0f, 1.0f));
	Rect GetViewport();

	void SetOutputSize(double outputWidth, double outputHeight);


	virtual const XMMATRIX& GetView();
	virtual const XMMATRIX& GetProj();

	/**
	* @brief Returns a ray going from camera through a screen point.
	* @param [in] position Screen point position in pixels (top-left corner is (0,0)).
	*/
	Ray ScreenPointToRay(const Vector3& position) const;

	/**
	* @brief Transforms position from world space into screen space.
	* @param [in] position Point position in world space.
	*/
	Vector3 WorldToScreenPoint(const Vector3& position);


protected:

	void RecalculateMatrixProj();

	Vector3 m_position;
	double m_pitch;
	double m_yaw;

	Vector3 m_lookAt;

	// Background color
	float m_r, m_g, m_b;

	// Render texture (if it's NULL then render to the screen, otherwise - to m_pRenderTexture)
	//const RenderTexture* m_pRenderTexture;

	// Вертикальный угол обзора в градусах
	float m_fovY;
	float m_nearPlane;
	float m_farPlane;

	XMMATRIX m_proj;
	XMMATRIX m_view;

	bool m_viewDirty;

	Rect m_viewport;

	double m_outputWidth, m_outputHeight;
};

