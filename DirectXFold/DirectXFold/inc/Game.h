#pragma once
#include <sstream>
#include <iomanip>

#include "PixelShader.h"
#include "VertexShader.h"
#include "MorphingVertexShader.h"

#include "Line.h"
#include "Mesh.h"
#include "Icosahedron.h"

#include "SimpleInputHandler.h"
#include "SphericalCamera.h"
#include "TextDrawer.h"




using namespace DirectX;
using namespace DirectX::SimpleMath;

class Game
{
public:
    static Game& GetInstance();


    // Initialization and management
    int Initialize(HWND window, int width, int height);
    void Cleanup();

    // Basic game loop
    void Tick();

    // Properties
    void GetDefaultSize(int& width, int& height);


private:
    friend class Mesh;
    friend class Octahedron;


    Game() noexcept;
    Game(Game const&) = delete;
    Game& operator=(Game const&) = delete;

    void Update(float deltaTime);
    void Render();
    void Clear(const float clearColor[4], float clearDepth, UINT8 clearStencil);
    void Present();


    bool LoadContent();
    void UnloadContent();

    XMFLOAT4 GetCartesianFromSpherical(float a3, float a2, float a1);
    XMFLOAT3 GetSphericalFromCartesian(float x1, float x2, float x3, float x4);



    // Device resources.
    HWND                                            m_hwnd;				//дескриптор окна игры
    int                                             m_outputWidth;
    int                                             m_outputHeight;


    // Direct3D device and swap chain.
    ID3D11Device* g_d3dDevice = nullptr;                                    //used for allocating GPU resources such as buffers, textures, shaders, and state objects
    ID3D11DeviceContext* g_d3dDeviceContext = nullptr;                      //used to configure the rendering pipeline and draw geometry
    IDXGISwapChain* g_d3dSwapChain = nullptr;                               //stores the buffers that are used for rendering data; used to determine how the buffers are swapped when the rendered image should be presented to the scree

    // Render target view for the back buffer of the swap chain.
    ID3D11RenderTargetView* g_d3dRenderTargetView = nullptr;
    // Depth/stencil view for use as a depth buffer.
    ID3D11DepthStencilView* g_d3dDepthStencilView = nullptr;
    // A texture to associate to the depth stencil view.
    ID3D11Texture2D* g_d3dDepthStencilBuffer = nullptr;

    // Define the functionality of the depth/stencil stages.
    ID3D11DepthStencilState* g_d3dDepthStencilState = nullptr;
    // Define the functionality of the rasterizer stage.
    ID3D11RasterizerState* g_d3dRasterizerState = nullptr;
    D3D11_VIEWPORT g_Viewport = { 0 };                                      //The g_Viewport variable defines the size of the viewport rectangle. The viewport rectangle is also used by the rasterizer stage to determine the renderable area on screen.


    // Vertex buffer data
    ID3D11InputLayout * g_d3dInputLayout = nullptr;                         //used to describe the order and type of data that is expected by the vertex shader.
    
    std::list<Mesh *> meshes;
    Mesh *mesh1, *mesh2;

    // Shader data
    ID3D11VertexShader* g_d3dVertexShader = nullptr;
    ID3D11PixelShader* g_d3dPixelShader = nullptr;

    // Shader resources
    enum ConstantBuffer
    {
        CB_Application, //The application level constant buffer stores variables that rarely change. 
        CB_Frame,       //The frame level constant buffer stores variables that change each frame. An example of a frame level shader variable would be the cameraТs view matrix which changes whenever the camera moves
        CB_Object,      //The object level constant buffer stores variables that are different for every object being rendered. An example of an object level shader variable is the objectТs world matrix.
        NumConstantBuffers
    };

    //three constant buffers: buffers are used to store shader variables that remain constant during current draw call. An example of a constant shader variable is the cameraТs projection matrix. Since the projection matrix will be the same for every vertex of the object, this variable does not need to be passed to the shader using vertex data.
    ID3D11Buffer* g_d3dConstantBuffers[NumConstantBuffers];
    //todo: создать отдельные классы-константбуфферы (три штуки), которые будут инкапс. все вещи дл€ каждого этапа

    XMMATRIX m_morph;
    XMMATRIX m_view;
    XMMATRIX m_proj;

    // Timer
    DWORD                                           previousTime = timeGetTime();
    const float                                     targetFramerate = 30.0f;
    const float                                     maxTimeStep = 1.0f / targetFramerate;

    // Input
    std::unique_ptr<InputHandler>         m_inputHandler;
    // Camera
    std::shared_ptr<Camera>               m_camera;

    TextDrawer                            *m_textDrawer;
};