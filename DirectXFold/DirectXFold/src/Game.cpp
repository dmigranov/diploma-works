#include <pch.h>
#include "Game.h"
#include "..\inc\Game.h"

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

// Message handlers
// The template includes a number of message handlers that are called for process state changes: 
// OnActivated, OnDeactivated, OnSuspending, OnResuming, and OnWindowSizeChanged.
// вызываются в WndProc
void Game::OnActivated()
{
    // TODO: Game is becoming active window.
}

void Game::OnDeactivated()
{
    // TODO: Game is becoming background window.
}

void Game::OnSuspending()
{
    // TODO: Game is being power-suspended (or minimized).
    m_minimized = true;
}

void Game::OnResuming()
{
    m_timer.ResetElapsedTime();
    m_minimized = false;

    // TODO: Game is being power-resumed (or returning from minimize).
}

void Game::OnWindowSizeChanged(int width, int height)
{
    m_outputWidth = std::max(width, 1);
    m_outputHeight = std::max(height, 1);

    CreateResources();

    // TODO: Game window is being resized.
}

// Properties
void Game::GetDefaultSize(int& width, int& height)
{
    // TODO: Change to desired default window size (note minimum size is 320x200).
    width = 800;
    height = 600;
}