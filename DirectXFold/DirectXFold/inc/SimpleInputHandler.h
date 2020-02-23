#pragma once
#include "InputHandler.h"
class SimpleInputHandler :
	public InputHandler
{
public:
	SimpleInputHandler(std::shared_ptr<Camera> camera, std::function<void()> func, HWND window);
	void HandleInput();
protected:
	void HandleKeyboard();
	void HandleMouse();
private:
	std::function<void()> func;
};

