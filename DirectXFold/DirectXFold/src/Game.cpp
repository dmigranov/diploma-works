#include <pch.h>
#include "Game.h"

Game::Game() noexcept :
    m_hwnd(nullptr),
    m_outputWidth(800),
    m_outputHeight(600)
{
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
    depthStencilBufferDesc.Format = DXGI_FORMAT_D24_UNORM_S8_UINT;
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

    depthStencilStateDesc.DepthEnable = TRUE;       //òåñò ãëóáèíû ïðîâîäèòñÿ
    depthStencilStateDesc.DepthWriteMask = D3D11_DEPTH_WRITE_MASK_ALL;
    depthStencilStateDesc.DepthFunc = D3D11_COMPARISON_LESS;
    depthStencilStateDesc.StencilEnable = FALSE;    //ÒÅÑÒ òðàôàðåòà ÍÅ ÏÐÎÂÎÄÈÒÑß!

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

    //            Update( deltaTime );
    //            Render();
}

// Properties
void Game::GetDefaultSize(int& width, int& height)
{
    // TODO: Change to desired default window size (note minimum size is 320x200).
    width = m_outputWidth;
    height = m_outputHeight;
}

bool Game::LoadContent()
{
    assert(g_d3dDevice);

    // Create an initialize the vertex buffer.
    D3D11_BUFFER_DESC vertexBufferDesc;
    ZeroMemory(&vertexBufferDesc, sizeof(D3D11_BUFFER_DESC));

    vertexBufferDesc.BindFlags = D3D11_BIND_VERTEX_BUFFER;  //how the buffer is bound to pipeline
    vertexBufferDesc.ByteWidth = sizeof(VertexPosColor) * _countof(g_Vertices);
    vertexBufferDesc.CPUAccessFlags = 0;    // no CPU access is necessary
    vertexBufferDesc.Usage = D3D11_USAGE_DEFAULT;

    //used to specify the data that is used to initialize a buffer when it is created.
    D3D11_SUBRESOURCE_DATA resourceData;
    ZeroMemory(&resourceData, sizeof(D3D11_SUBRESOURCE_DATA));
    resourceData.pSysMem = g_Vertices; //A pointer to the data to initialize the buffer with.

    HRESULT hr = g_d3dDevice->CreateBuffer(&vertexBufferDesc, &resourceData, &g_d3dVertexBuffer);
    if (FAILED(hr))
    {
        return false;
    }

    // Create and initialize the index buffer.
    D3D11_BUFFER_DESC indexBufferDesc;
    ZeroMemory(&indexBufferDesc, sizeof(D3D11_BUFFER_DESC));

    indexBufferDesc.BindFlags = D3D11_BIND_INDEX_BUFFER;
    indexBufferDesc.ByteWidth = sizeof(WORD) * _countof(g_Indicies);
    indexBufferDesc.CPUAccessFlags = 0;
    indexBufferDesc.Usage = D3D11_USAGE_DEFAULT;
    resourceData.pSysMem = g_Indicies;

    hr = g_d3dDevice->CreateBuffer(&indexBufferDesc, &resourceData, &g_d3dIndexBuffer);
    if (FAILED(hr))
    {
        return false;
    }


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
        { "POSITION", 0, DXGI_FORMAT_R32G32B32A32_FLOAT, 0, offsetof(VertexPosColor, Position), D3D11_INPUT_PER_VERTEX_DATA, 0 },
        { "COLOR", 0, DXGI_FORMAT_R32G32B32_FLOAT, 0, offsetof(VertexPosColor, Color), D3D11_INPUT_PER_VERTEX_DATA, 0 }
    };

    hr = g_d3dDevice->CreateInputLayout(vertexLayoutDesc, _countof(vertexLayoutDesc), g_vs, sizeof(g_vs), &g_d3dInputLayout);
    if (FAILED(hr))
    {
        return false;
    }

}
