using namespace DirectX;

// Vertex data for a colored cube.
struct VertexPosColor
{
    XMFLOAT4 Position;  //координаты точки в четырехмерном пространстве
    XMFLOAT3 Color;
};



class Game
{
public:
	Game() noexcept;


    // Initialization and management
    int Initialize(HWND window, int width, int height);

    // Basic game loop
    void Tick();

    // Messages (called by WndProc)
    /*
    void OnActivated();
    void OnDeactivated();
    void OnSuspending();
    void OnResuming();
    void OnWindowSizeChanged(int width, int height);
    */

    // Properties
    void GetDefaultSize(int& width, int& height);




private:
    // Device resources.
    HWND                                            m_window;				//дескриптор окна игры
    int                                             m_outputWidth;
    int                                             m_outputHeight;


    HWND                                            m_hwnd;				//дескриптор окна игры
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
    ID3D11Buffer* g_d3dVertexBuffer = nullptr;
    ID3D11Buffer* g_d3dIndexBuffer = nullptr;

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

    XMMATRIX g_WorldMatrix;
    XMMATRIX g_ViewMatrix;
    XMMATRIX g_ProjectionMatrix;


    // Timer
    DWORD                                           previousTime = timeGetTime();
    const float                                     targetFramerate = 30.0f;
    const float                                     maxTimeStep = 1.0f / targetFramerate;

};