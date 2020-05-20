//inspired by:
//https://www.3dgep.com/introduction-to-directx-11/#Initialize_DirectX
//DirectXTK template by Chuck Walbourne

#include <pch.h>
#include "Game.h"


using namespace DirectX;
using namespace DirectX::SimpleMath;


// Entry point
int WINAPI wWinMain(_In_ HINSTANCE hInstance, _In_opt_ HINSTANCE, _In_ LPWSTR, _In_ int nCmdShow)
{
    Game& game = Game::GetInstance();

    auto earthTexture = game.CreateTexture(L"earth.dds");
    auto asteroidTexture = game.CreateTexture(L"asteroid2.dds");
    auto fabricTexture = game.CreateTexture(L"fabric.dds");

    auto mesh1 = new SphericalSphere(0.15f, 20, 20, earthTexture);
    mesh1->AddUpdater(SphericalMesh::MeshUpdater([](Matrix in, float delta) {
        auto ks = Keyboard::Get().GetState();

        float gain = 0.045f;
        Matrix m = Matrix::Identity;
        if (ks.U)
            m = SphericalRotationYW(-gain); //так всегда вверх!
        if (ks.J)
            m = SphericalRotationYW(gain);
        if (ks.H)
            m = SphericalRotationXW(gain);
        if (ks.K)
            m = SphericalRotationXW(-gain);

        return in * m;
    }));
    game.AddMesh(mesh1);


    int sect = 9;
    for (int i = 1; i < sect; i++)
    {
        Mesh* mesh = new SphericalSphere(0.15f, 20, 20, earthTexture, SphericalRotationZW(i * XM_PI / sect));
        /*mesh->AddUpdater(SphericalMesh::MeshUpdater([i](Matrix in, float delta) {
            return SphericalRotationYZ(delta / i / 3.f) * SphericalRotationXY(delta / i / 2.f) * in * SphericalRotationYW(-delta / i / 6.f) * SphericalRotationZW(delta / 3.f) * SphericalRotationXW(delta / 12.f);
        }));*/
        game.AddMesh(mesh);
    }

    return game.StartGame(hInstance, nCmdShow);
}