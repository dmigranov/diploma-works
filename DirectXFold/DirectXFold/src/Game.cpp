#include <pch.h>
#include "Game.h"

Game::Game() noexcept :
    m_hwnd(nullptr),
    m_outputWidth(800),
    m_outputHeight(600)
{
    m_camera = std::make_shared<SphericalCamera>();
}

Game& Game::GetInstance()
{
    static Game game;
    return game;
}

int Game::Initialize(HWND window, int width, int height)
{
    /*
    The process of initializing a Direct3D rendering device consists of several steps:
    1. Create the device and swap chain,
    2. Create a render target view of the swap chain’s back buffer,
    3. Create a texture for the depth-stencil buffer,
    4. Create a depth-stencil view from the depth-stencil buffer,
    5. Create a depth-stencil state object that defines the behaviour of the output merger stage,
    6. Create a rasterizer state object that defines the behaviour of the rasterizer stage.
    */
    m_hwnd = window;
    assert(m_hwnd != 0);

    RECT clientRect;
    GetClientRect(m_hwnd, &clientRect);
    unsigned int clientWidth = clientRect.right - clientRect.left;
    unsigned int clientHeight = clientRect.bottom - clientRect.top;

    //The swap chain description defines the size and number of render buffers that will be used by the swap chain. It also associates the window to the swap chain which determines where the final image will be presented. The swap chain description also defines the quality of anti-aliasing (if any) that should be applied and how the back buffer is flipped during presentation.
    DXGI_SWAP_CHAIN_DESC swapChainDesc;
    ZeroMemory(&swapChainDesc, sizeof(DXGI_SWAP_CHAIN_DESC));

    swapChainDesc.BufferCount = 1;
    swapChainDesc.BufferDesc.Width = clientWidth;
    swapChainDesc.BufferDesc.Height = clientHeight;
    swapChainDesc.BufferDesc.Format = DXGI_FORMAT_R8G8B8A8_UNORM;
    swapChainDesc.BufferDesc.RefreshRate = {0, 1};
    swapChainDesc.BufferUsage = DXGI_USAGE_RENDER_TARGET_OUTPUT;
    swapChainDesc.OutputWindow = m_hwnd;
    //sampledesc - multisampling
    swapChainDesc.SampleDesc.Count = 1;
    swapChainDesc.SampleDesc.Quality = 0;
    swapChainDesc.SwapEffect = DXGI_SWAP_EFFECT_DISCARD;
    swapChainDesc.Windowed = TRUE;

    UINT createDeviceFlags = 0;
#if _DEBUG
    createDeviceFlags = D3D11_CREATE_DEVICE_DEBUG;
#endif

    // These are the feature levels that we will accept.
    D3D_FEATURE_LEVEL featureLevels[] =
    {
        D3D_FEATURE_LEVEL_11_1,
        D3D_FEATURE_LEVEL_11_0,
        D3D_FEATURE_LEVEL_10_1,
        D3D_FEATURE_LEVEL_10_0,
        D3D_FEATURE_LEVEL_9_3,
        D3D_FEATURE_LEVEL_9_2,
        D3D_FEATURE_LEVEL_9_1
    };

    // This will be the feature level that 
    // is used to create our device and swap chain.
    D3D_FEATURE_LEVEL featureLevel;

    HRESULT hr = D3D11CreateDeviceAndSwapChain(nullptr, D3D_DRIVER_TYPE_HARDWARE,
        nullptr, createDeviceFlags, featureLevels, _countof(featureLevels),
        D3D11_SDK_VERSION, &swapChainDesc, &g_d3dSwapChain, &g_d3dDevice, &featureLevel,
        &g_d3dDeviceContext);

    if (hr == E_INVALIDARG)
    {
        hr = D3D11CreateDeviceAndSwapChain(nullptr, D3D_DRIVER_TYPE_HARDWARE,
            nullptr, createDeviceFlags, &featureLevels[1], _countof(featureLevels) - 1,
            D3D11_SDK_VERSION, &swapChainDesc, &g_d3dSwapChain, &g_d3dDevice, &featureLevel,
            &g_d3dDeviceContext);
    }

    if (FAILED(hr))
    {
        return -1;
    }


    // Next initialize the back buffer of the swap chain and associate it to a render target view.
    // we use the swap chain’s GetBuffer method to retrieve a pointer to the swap chain’s 
    // single back buffer. The swap chain’s back buffer is automatically created based on the content of the DXGI_SWAP_CHAIN_DESC variable
    // so we do not need to manually create a texture for this purpose. However we do need to associate the backbuffer to a render target view in order to render to the swap chain’s back buffer.
    
    ID3D11Texture2D* backBuffer;
    hr = g_d3dSwapChain->GetBuffer(0, __uuidof(ID3D11Texture2D), (LPVOID*)&backBuffer);
    if (FAILED(hr))
    {
        return -1;
    }

    hr = g_d3dDevice->CreateRenderTargetView(backBuffer, nullptr, &g_d3dRenderTargetView);
    if (FAILED(hr))
    {
        return -1;
    }

    SafeRelease(backBuffer);


    // Create the depth buffer for use with the depth/stencil view.
    D3D11_TEXTURE2D_DESC depthStencilBufferDesc;
    ZeroMemory(&depthStencilBufferDesc, sizeof(D3D11_TEXTURE2D_DESC));

    depthStencilBufferDesc.ArraySize = 1;
    depthStencilBufferDesc.BindFlags = D3D11_BIND_DEPTH_STENCIL;
    depthStencilBufferDesc.CPUAccessFlags = 0; // No CPU access required.
    depthStencilBufferDesc.Format = DXGI_FORMAT_D32_FLOAT;
    depthStencilBufferDesc.Width = clientWidth;
    depthStencilBufferDesc.Height = clientHeight;
    depthStencilBufferDesc.MipLevels = 1;
    depthStencilBufferDesc.SampleDesc.Count = 1;
    depthStencilBufferDesc.SampleDesc.Quality = 0;
    depthStencilBufferDesc.Usage = D3D11_USAGE_DEFAULT;

    hr = g_d3dDevice->CreateTexture2D(&depthStencilBufferDesc, nullptr, &g_d3dDepthStencilBuffer);
    if (FAILED(hr))
    {
        return -1;
    }
    //we must create a ID3D11DepthStencilView before we can use this depth buffer for rendering
    hr = g_d3dDevice->CreateDepthStencilView(g_d3dDepthStencilBuffer, nullptr, &g_d3dDepthStencilView);
    if (FAILED(hr))
    {
        return -1;
    }

    // Setup depth/stencil state.
    D3D11_DEPTH_STENCIL_DESC depthStencilStateDesc;
    ZeroMemory(&depthStencilStateDesc, sizeof(D3D11_DEPTH_STENCIL_DESC));

    depthStencilStateDesc.DepthEnable = TRUE;       //тест глубины проводится
    depthStencilStateDesc.DepthWriteMask = D3D11_DEPTH_WRITE_MASK_ALL;
    depthStencilStateDesc.DepthFunc = D3D11_COMPARISON_LESS;
    depthStencilStateDesc.StencilEnable = FALSE;    //ТЕСТ трафарета НЕ ПРОВОДИТСЯ!

    hr = g_d3dDevice->CreateDepthStencilState(&depthStencilStateDesc, &g_d3dDepthStencilState);


    // Setup rasterizer state.
    D3D11_RASTERIZER_DESC rasterizerDesc;
    ZeroMemory(&rasterizerDesc, sizeof(D3D11_RASTERIZER_DESC));

    rasterizerDesc.AntialiasedLineEnable = FALSE;
    rasterizerDesc.CullMode = D3D11_CULL_BACK;
    rasterizerDesc.DepthBias = 0;
    rasterizerDesc.DepthBiasClamp = 0.0f;
    rasterizerDesc.DepthClipEnable = TRUE;
    rasterizerDesc.FillMode = D3D11_FILL_SOLID;
    rasterizerDesc.FrontCounterClockwise = FALSE;
    rasterizerDesc.MultisampleEnable = FALSE;
    rasterizerDesc.ScissorEnable = FALSE;
    rasterizerDesc.SlopeScaledDepthBias = 0.0f;

    // Create the rasterizer state object.
    hr = g_d3dDevice->CreateRasterizerState(&rasterizerDesc, &g_d3dRasterizerState);
    if (FAILED(hr))
    {
        return -1;
    }

    // Initialize the viewport to occupy the entire client area.
    g_Viewport.Width = static_cast<float>(clientWidth);
    g_Viewport.Height = static_cast<float>(clientHeight);
    g_Viewport.TopLeftX = 0.0f;
    g_Viewport.TopLeftY = 0.0f;
    g_Viewport.MinDepth = 0.0f;
    g_Viewport.MaxDepth = 1.0f;

    if (!LoadContent())
    {
        return -1;
    }
    m_inputHandler = std::make_unique<SimpleInputHandler>(m_camera, [this]() { 
        auto ks = Keyboard::Get().GetState();
        float gain = 0.045f;
        if (ks.U)
            this->mesh1->SetWorldMatrix(SphericalRotationYW(-gain)*mesh1->GetWorldMatrix());
        if (ks.J)
            this->mesh1->SetWorldMatrix(SphericalRotationYW(gain) * mesh1->GetWorldMatrix());
        if (ks.H)
            this->mesh1->SetWorldMatrix(SphericalRotationXW(-gain) * mesh1->GetWorldMatrix());
        if (ks.K)
            this->mesh1->SetWorldMatrix(SphericalRotationXW(gain) * mesh1->GetWorldMatrix());
        if (ks.Y)
            this->mesh1->SetWorldMatrix(SphericalRotationXZ(-gain) * mesh1->GetWorldMatrix());
        if (ks.I)
            this->mesh1->SetWorldMatrix(SphericalRotationXZ(gain) * mesh1->GetWorldMatrix());

    }, m_hwnd);

    m_textDrawer = new TextDrawer(g_d3dDevice, g_d3dDeviceContext, L"myfile.spritefont");

    return 0;
}

void Game::Tick()
{
    DWORD currentTime = timeGetTime();
    float deltaTime = (currentTime - previousTime) / 1000.0f;
    previousTime = currentTime;

    // Cap the delta time to the max time step (useful if your 
    // debugging and you don't want the deltaTime value to explode.
    deltaTime = std::min<float>(deltaTime, maxTimeStep);

    Update( deltaTime );
    Render();
}

// Properties
void Game::GetDefaultSize(int& width, int& height)
{
    // TODO: Change to desired default window size (note minimum size is 320x200).
    width = m_outputWidth;
    height = m_outputHeight;
}

void Game::Update(float deltaTime)
{
    m_inputHandler->HandleInput();

    /*m_view = m_camera->GetView();
    g_d3dDeviceContext->UpdateSubresource(g_d3dConstantBuffers[CB_Frame], 0, nullptr, &m_view, 0, 0);
    */
    DWORD t = timeGetTime();
    m_morph = XMMatrixRotationAxis(XMVectorSet(0, 1, 0, 0), 0.4*cos(t/100.0));
    mesh1->SetConstants(mesh1->GetWorldMatrix(), m_morph);
}

void Game::Render()
{
    assert(g_d3dDevice);
    assert(g_d3dDeviceContext);

    Clear(Colors::YellowGreen, 1.0f, 0);

    //Input Assembler Stage - common
    g_d3dDeviceContext->IASetInputLayout(g_d3dInputLayout);

    //Vertex Shader Stage
    g_d3dDeviceContext->VSSetShader(g_d3dVertexShader, nullptr, 0);
    g_d3dDeviceContext->VSSetConstantBuffers(0, 3, g_d3dConstantBuffers);

    //Rasterizer Stage
    g_d3dDeviceContext->RSSetState(g_d3dRasterizerState);
    g_d3dDeviceContext->RSSetViewports(1, &g_Viewport);

    //Pixel Shader Stage
    g_d3dDeviceContext->PSSetShader(g_d3dPixelShader, nullptr, 0);

    //Output Merger Stage (merges the output from the pixel shader onto the color and depth buffers)
    g_d3dDeviceContext->OMSetRenderTargets(1, &g_d3dRenderTargetView, g_d3dDepthStencilView);
    g_d3dDeviceContext->OMSetDepthStencilState(g_d3dDepthStencilState, 1); //1 is Reference value to perform against when doing a depth-stencil test.

    //first render
    {
        auto front = (std::static_pointer_cast<SphericalCamera>(m_camera))->GetFrontProj();
        g_d3dDeviceContext->UpdateSubresource(g_d3dConstantBuffers[CB_Application], 0, nullptr, &front, 0, 0);
        auto view = m_camera->GetView();
        g_d3dDeviceContext->UpdateSubresource(g_d3dConstantBuffers[CB_Frame], 0, nullptr, &view, 0, 0);
        //mesh1->Render();
        //mesh2->Render();
        for (auto mesh : meshes)
            mesh->Render();
    }
    
    //second render
    {
        auto back = (std::static_pointer_cast<SphericalCamera>(m_camera))->GetBackProj();
        g_d3dDeviceContext->UpdateSubresource(g_d3dConstantBuffers[CB_Application], 0, nullptr, &back, 0, 0);
        auto view = (std::static_pointer_cast<SphericalCamera>(m_camera))->GetAntipodalView();
        g_d3dDeviceContext->UpdateSubresource(g_d3dConstantBuffers[CB_Frame], 0, nullptr, &view, 0, 0);
        for (auto mesh : meshes)
            mesh->Render();
    }
    std::stringstream ss;
    Vector4 pos = m_camera->GetPosition();
    Vector3 sphPos = GetSphericalFromCartesian(pos.x, pos.y, pos.z, pos.w);
    ss << std::fixed << std::setprecision(2);
    ss << "X: " << pos.x << (pos.x < 0 ? "" : " ") << " A1: " << sphPos.x << std::endl;
    ss << "Y: " << pos.y << (pos.y < 0 ? "" : " ") << " A2: " << sphPos.y << std::endl;
    ss << "Z: " << pos.z << (pos.z < 0 ? "" : " ") << " A3: " << sphPos.z << std::endl;
    ss << "W: " << pos.w << std::endl;
    m_textDrawer->DrawTextDownLeftAlign(ss.str().c_str(), 20, m_outputHeight - 20);
    

    Present();
}

void Game::Clear(const float clearColor[4], float clearDepth, UINT8 clearStencil)
{
    g_d3dDeviceContext->ClearRenderTargetView(g_d3dRenderTargetView, clearColor);   //clear the back buffer to a particlular color
    g_d3dDeviceContext->ClearDepthStencilView(g_d3dDepthStencilView, D3D11_CLEAR_DEPTH | D3D11_CLEAR_STENCIL, clearDepth, clearStencil);        //method is used to clear the depth and stencil buffer to a particular depth value and stencil value.
}

void Game::Present()
{
    g_d3dSwapChain->Present(1, 0);
    //с vsync?
}

void Game::Cleanup()
{
    UnloadContent();

    SafeRelease(g_d3dDepthStencilView);
    SafeRelease(g_d3dRenderTargetView);
    SafeRelease(g_d3dDepthStencilBuffer);
    SafeRelease(g_d3dDepthStencilState);
    SafeRelease(g_d3dRasterizerState);
    SafeRelease(g_d3dSwapChain);
    SafeRelease(g_d3dDeviceContext);
    SafeRelease(g_d3dDevice);
}

bool Game::LoadContent()
{
    assert(g_d3dDevice);
    HRESULT hr;
    
    // Create the constant buffers for the variables defined in the vertex shader.
    D3D11_BUFFER_DESC constantBufferDesc;
    ZeroMemory(&constantBufferDesc, sizeof(D3D11_BUFFER_DESC));

    constantBufferDesc.BindFlags = D3D11_BIND_CONSTANT_BUFFER;
    constantBufferDesc.ByteWidth = sizeof(XMMATRIX);
    constantBufferDesc.CPUAccessFlags = 0;
    constantBufferDesc.Usage = D3D11_USAGE_DEFAULT;

    //we will update the contents of buffers using the ID3D11DeviceContext::UpdateSubresource method and this method expects constant buffers to be initialized with D3D11_USAGE_DEFAULT usage flag and buffers that are created with the D3D11_USAGE_DEFAULT flag must have their CPUAccessFlags set to 0.
    hr = g_d3dDevice->CreateBuffer(&constantBufferDesc, nullptr, &g_d3dConstantBuffers[CB_Application]);
    if (FAILED(hr))
    {
        return false;
    }
    hr = g_d3dDevice->CreateBuffer(&constantBufferDesc, nullptr, &g_d3dConstantBuffers[CB_Frame]);
    if (FAILED(hr))
    {
        return false;
    }
    constantBufferDesc.ByteWidth = sizeof(Mesh::MeshConstantBuffer);
    hr = g_d3dDevice->CreateBuffer(&constantBufferDesc, nullptr, &g_d3dConstantBuffers[CB_Object]);
    if (FAILED(hr))
    {
        return false;
    }

    //loading shaders from global variables 
    hr = g_d3dDevice->CreateVertexShader(g_vs, sizeof(g_vs), nullptr, &g_d3dVertexShader);
    if (FAILED(hr))
    {
        return false;
    }

    hr = g_d3dDevice->CreatePixelShader(g_ps, sizeof(g_ps), nullptr, &g_d3dPixelShader);
    if (FAILED(hr))
    {
        return false;
    }

    //ID3D11InputLayout is used to define how the vertex data attached to the input-assembler stage is layed out in memory
    D3D11_INPUT_ELEMENT_DESC vertexLayoutDesc[] =
    {
        { "POSITION", 0, DXGI_FORMAT_R32G32B32A32_FLOAT, 0, offsetof(Mesh::VertexPosColor, Position), D3D11_INPUT_PER_VERTEX_DATA, 0 },
        { "COLOR", 0, DXGI_FORMAT_R32G32B32_FLOAT, 0, offsetof(Mesh::VertexPosColor, Color), D3D11_INPUT_PER_VERTEX_DATA, 0 }
    };

    hr = g_d3dDevice->CreateInputLayout(vertexLayoutDesc, _countof(vertexLayoutDesc), g_vs, sizeof(g_vs), &g_d3dInputLayout);
    if (FAILED(hr))
    {
        return false;
    }

    // Setup the projection matrix.
    RECT clientRect;
    GetClientRect(m_hwnd, &clientRect);

    // Compute the exact client dimensions.
    // This is required for a correct projection matrix.
    float clientWidth = static_cast<float>(clientRect.right - clientRect.left);
    float clientHeight = static_cast<float>(clientRect.bottom - clientRect.top);

    //m_proj = XMMatrixPerspectiveFovLH(XMConvertToRadians(45.0f), clientWidth / clientHeight, 0.1f, 100.0f);
   
    m_camera->SetPosition(0, 0, 0);
    m_camera->SetFovY(XM_PI / 4.f);
    m_camera->SetOutputSize(clientWidth, clientHeight);
    m_camera->SetNearPlane(0.1f);
    m_camera->SetFarPlane(100.f);
    /*m_proj = m_camera->GetProj();
    g_d3dDeviceContext->UpdateSubresource(g_d3dConstantBuffers[CB_Application], 0, nullptr, &m_proj, 0, 0);*/

    {


        /*Mesh::VertexPosColor vertices[] = {
            { GetCartesianFromSpherical(1.37f, 2.17f, 1.f), XMFLOAT3(0.0f, 0.0f, 0.0f) }, // 0
            { GetCartesianFromSpherical(1.f, 2.f, 3.14f), XMFLOAT3(0.0f, 1.0f, 0.0f) }, // 1
            { GetCartesianFromSpherical(1.97f, 2.57f, 1.f), XMFLOAT3(1.0f, 1.0f, 0.0f) }, // 2
        };*/

        /*Mesh::VertexPosColor vertices[] = {
            { GetCartesianFromSpherical(0.f, XM_PIDIV2, XM_PI), XMFLOAT3(0.0f, 0.0f, 0.0f) }, // 0
            { GetCartesianFromSpherical(1.f, XM_PIDIV2, XM_PIDIV4), XMFLOAT3(0.0f, 1.0f, 0.0f) }, // 1
            { GetCartesianFromSpherical(2.5f, XM_PIDIV2, XM_PIDIV4), XMFLOAT3(1.0f, 1.0f, 0.0f) }, // 2
        };*/

        Mesh::VertexPosColor vertices[] = {
            { XMFLOAT4(0.6f, 0.0f, 0.0f, 0.8f), XMFLOAT3(0.0f, 0.0f, 0.0f) },   // 0
            { XMFLOAT4(0.0f,  0.6f, 0.0f, 0.8f), XMFLOAT3(0.0f, 1.0f, 0.0f) },  // 1
            { XMFLOAT4(0.0f,  -0.6f, 0.0f, 0.8f), XMFLOAT3(1.0f, 1.0f, 0.0f) }//, // 2
            //{ XMFLOAT4(-0.33166f, 0.f, -0.5f, 0.8f), XMFLOAT3(1.0f, 0.0f, 0.0f) }   // 3
        };

        WORD indices[] = {
            /*0, 1, 2*/ 2, 1, 0, 0, 1, 2
            /*1, 2, 3,
            0, 1, 3,
            3, 2, 0*/
        };

        mesh1 = new Mesh(_countof(vertices), vertices,
            _countof(indices), indices);
        meshes.push_back(mesh1);


    }

    /*{
        Mesh::VertexPosColor vertices[] = {
        { XMFLOAT4(-0.6f, 0.0f, 0.0f, 0.8f), XMFLOAT3(1.0f, 0.0f, 0.0f) }, // 0
        { XMFLOAT4(0.0f,  0.6f, 0.0f, 0.8f), XMFLOAT3(0.0f, 0.0f, 1.0f) }, // 1
        { XMFLOAT4(0.0f,  -0.6f, 0.0f, 0.8f), XMFLOAT3(0.0f, 1.0f, 0.0f) }, // 2
        };

        WORD indices[] = {
            0, 1, 2, 2, 1, 0
        };

        mesh2 = new Mesh(_countof(vertices), vertices,
            _countof(indices), indices);
        meshes.push_back(mesh2);

    }*/

    /*{
        //0.2 0.4 0.8
        Mesh::VertexPosColor vertices[] = {
        { XMFLOAT4(0.0f, 0.33166f, 0.5f, 0.8f), XMFLOAT3(1.0f, 1.0f, 0.0f) }, // 0
        { XMFLOAT4(0.0f,  0.f, 0.6, 0.8f), XMFLOAT3(0.0f, 0.5f, 1.0f) }, // 1
        { XMFLOAT4(0.0f,  0.f, -0.6f, 0.8f), XMFLOAT3(0.5f, 1.0f, 0.0f) }, // 2
        { XMFLOAT4(0.0f,  0.33166f, -0.5f, 0.8f), XMFLOAT3(0.0f, 1.0f, 0.5f) }, // 3
        };

        WORD indices[] = {
            0, 1, 2, 3, 0
        };

        Mesh * mesh = new Line(_countof(vertices), vertices,
            _countof(indices), indices);
        meshes.push_back(mesh);

    }*/
    return true;
}


void Game::UnloadContent()
{
    SafeRelease(g_d3dConstantBuffers[CB_Application]);
    SafeRelease(g_d3dConstantBuffers[CB_Frame]);
    SafeRelease(g_d3dConstantBuffers[CB_Object]);
    SafeRelease(g_d3dInputLayout);
    SafeRelease(g_d3dVertexShader);
    SafeRelease(g_d3dPixelShader);
    delete mesh1;
    delete mesh2;
    delete m_textDrawer;
}


XMFLOAT4 Game::GetCartesianFromSpherical(float a1, float a2, float a3)
{
    float sin3 = sinf(a3), cos3 = cosf(a3);
    float sin2 = sinf(a2), cos2 = cosf(a2);
    float sin1 = sinf(a1), cos1 = cosf(a3);
    return XMFLOAT4(sin3*sin2*sin1, sin3*sin2*cos1, sin3*cos2, cos3);
}

//xyzw
XMFLOAT3 Game::GetSphericalFromCartesian(float x4, float x3, float x2, float x1)
{
    float x42 = x4 * x4;
    float x22 = x2 * x2;
    float x32 = x3 * x3;

    float a1 = acosf(x1);
    if (x2 == 0 && x3 == 0 && x4 == 0)
        if (x1 > 0)
            return Vector3(a1, 0, 0);
        else
            return Vector3(a1, XM_PI, XM_PI);

    float a2 = acosf(x2/sqrtf(x22 + x32 + x42));
    if (x3 == 0 && x4 == 0)
        if (x2 > 0)
            return Vector3(a1, a2, 0);
        else
            return Vector3(a1, a2, XM_PI);

    float a3;
    if(x4 >= 0)
        a3 = acosf(x3 / sqrtf(x32 + x42));
    else 
        a3 = XM_2PI - acosf(x3 / sqrtf(x32 + x42));

    return XMFLOAT3(a1, a2, a3);
}
