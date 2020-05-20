//inspired by:
//https://www.3dgep.com/introduction-to-directx-11/#Initialize_DirectX
//DirectXTK template by Chuck Walbourne

#include <pch.h>
#include "Game.h"


// Entry point
int WINAPI wWinMain(_In_ HINSTANCE hInstance, _In_opt_ HINSTANCE, _In_ LPWSTR, _In_ int nCmdShow)
{
    Game& g_game = Game::GetInstance();
    return g_game.StartGameLoop(hInstance, nCmdShow);
}