#include <pch.h>
#include "Game.h"

Game::Game() noexcept
{
}

void Game::Initialize(HWND window, int width, int height)
{
}

void Game::Tick()
{
    DWORD currentTime = timeGetTime();
    float deltaTime = (currentTime - previousTime) / 1000.0f;
    previousTime = currentTime;

    // Cap the delta time to the max time step (useful if your 
    // debugging and you don't want the deltaTime value to explode.
    deltaTime = std::min<float>(deltaTime, maxTimeStep);

    //            Update( deltaTime );
    //            Render();
}

// Properties
void Game::GetDefaultSize(int& width, int& height)
{
    // TODO: Change to desired default window size (note minimum size is 320x200).
    width = 800;
    height = 600;
}