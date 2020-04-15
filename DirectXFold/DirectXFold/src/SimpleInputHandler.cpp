#include "pch.h"
#include "SimpleInputHandler.h"

using namespace DirectX;
using namespace DirectX::SimpleMath;

//extern void ExitGame();

const float MOVEMENT_GAIN = 0.0075f;
const float ROTATION_GAIN = 0.012f;

SimpleInputHandler::SimpleInputHandler(std::shared_ptr<Camera> camera, std::function<void()> func, HWND window)
{
    m_keyboard = std::make_unique<Keyboard>();
    m_mouse = std::make_unique<Mouse>();
    m_mouse->SetWindow(window);
    m_camera = camera;
    this->func = func;
}

void SimpleInputHandler::HandleInput()
{
	HandleKeyboard();
	HandleMouse();
}

void SimpleInputHandler::HandleKeyboard()
{
    auto kb = m_keyboard->GetState();
    if (kb.Escape)
    {
        //ExitGame();
    }

    Vector3 move = Vector3::Zero;

    if (kb.Up || kb.W)
        move.z += 1.f;

    if (kb.Down || kb.S)
        move.z -= 1.f;

    if (kb.Left || kb.A)
        move.x -= 1.f;

    if (kb.Right || kb.D)
        move.x += 1.f;

    if (kb.R)
        move.y += 1.f;

    if (kb.F)
        move.y -= 1.f;


    move *= MOVEMENT_GAIN;
    if(move != Vector3::Zero)
        m_camera->Move(move);

    func();
}

void SimpleInputHandler::HandleMouse()
{
    auto mouse = m_mouse->GetState();

    if (mouse.positionMode == Mouse::MODE_RELATIVE)
    {
        Vector3 delta = Vector3(float(mouse.x), float(mouse.y), 0.f)
            * ROTATION_GAIN;

        m_camera->ChangePitchYawRoll(-delta.y, delta.x);
    }

    m_mouse->SetMode(mouse.leftButton ? Mouse::MODE_RELATIVE : Mouse::MODE_ABSOLUTE);
}
