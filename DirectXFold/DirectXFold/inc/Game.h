class Game
{
public:
	Game() noexcept;


    // Initialization and management
    void Initialize(HWND window, int width, int height);

    // Basic game loop
    void Tick();

    // Messages (called by WndProc)
    void OnActivated();
    void OnDeactivated();
    void OnSuspending();
    void OnResuming();
    void OnWindowSizeChanged(int width, int height);

    // Properties
    void GetDefaultSize(int& width, int& height);




private:
    // Device resources.
    HWND                                            m_window;				//дескриптор окна игры
    int                                             m_outputWidth;
    int                                             m_outputHeight;

    bool                                            m_minimized = false;

};