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
    game.InitializeGame(hInstance, nCmdShow);

    {
        auto earthTexture = game.CreateTexture(L"earth.dds");
        auto asteroidTexture = game.CreateTexture(L"asteroid2.dds");
        auto fabricTexture = game.CreateTexture(L"fabric.dds");
        auto sviborgTexture = game.CreateTexture(L"sviborg.dds");
        auto fireTexture = game.CreateTexture(L"fire.dds");
        auto moonTexture = game.CreateTexture(L"moon.dds");


        game.MoveCamera(Vector3(0, 0, -XM_PI / 4));
        game.SetCameraFovY(XM_PI / 2);

        //auto mesh1 = new SphericalEllipsoid(0.15f, 0.15f, 0.15f, 20, 20, earthTexture);
        auto mesh1 = new SphericalSphere(0.15f, 20, 20, earthTexture);

        mesh1->AddUpdater(Mesh::MeshUpdater([](Matrix in, float delta) {
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

        auto moon = new SphericalSphere(0.05f, 20, 20, moonTexture, SphericalRotationZW(-0.26f));
        moon->SetParent(mesh1);
        moon->AddUpdater(Mesh::MeshUpdater([](Matrix in, float delta) {
            return in * SphericalRotationXZ(delta);
        }));
        game.AddMesh(moon);


        auto asteroid = new SphericalSphere(0.02f, 20, 20, asteroidTexture, SphericalRotationZW(-0.1f));
        asteroid->SetParent(moon);
        asteroid->AddUpdater(Mesh::MeshUpdater([](Matrix in, float delta) {
            return in * SphericalRotationXZ(delta);
        }));
        game.AddMesh(asteroid);


        auto head = new SphericalSphere(0.08f, 20, 20, sviborgTexture);
        head->AddUpdater(Mesh::MeshUpdater([](Matrix in, float delta) {
            auto ks = Keyboard::Get().GetState();

            static double time = 0;
            if (!ks.Space)
                time += delta;

            return SphericalRotationYW(-0.15f) * SphericalRotationXZ(XM_PI / 2) * SphericalRotationZW(time / 5.) * SphericalRotationYW(0.1 * sin(time));

        }));
        game.AddMesh(head);

        int sect = 8;
        for (int i = 0; i < sect; i++)
        {
            Mesh* mesh = new SphericalSphere(0.01f, 20, 20, fireTexture, SphericalRotationYW(-0.12f) * SphericalRotationYZ(i * XM_2PI / sect));
            mesh->AddUpdater(SphericalMesh::MeshUpdater([i](Matrix in, float delta) {
                return  in * SphericalRotationYZ(delta / 3.f);
            }));
            mesh->SetParent(head);
            game.AddMesh(mesh);
        }


        auto mesh2 = SphericalMeshLoader::LoadMesh("mesh3.sph");
        mesh2->SetTexture(fabricTexture);
        mesh2->SetWorldMatrix(SphericalRotationYZ(XM_PIDIV2) * SphericalRotationYW(0.09f));
        game.AddMesh(mesh2);
    }

    return game.StartGame(hInstance, nCmdShow);
}