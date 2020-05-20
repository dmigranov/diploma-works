//inspired by:
//https://www.3dgep.com/introduction-to-directx-11/#Initialize_DirectX
//DirectXTK template by Chuck Walbourne

#include <pch.h>
#include "Game.h"


// Entry point
int WINAPI wWinMain(_In_ HINSTANCE hInstance, _In_opt_ HINSTANCE, _In_ LPWSTR, _In_ int nCmdShow)
{
    Game& game = Game::GetInstance();

    auto earthTexture = game.AddTexture(L"earth.dds");
    auto asteroidTexture = game.AddTexture(L"asteroid2.dds");
    auto fabricTexture = game.AddTexture(L"fabric.dds");


    return game.StartGame(hInstance, nCmdShow);
}