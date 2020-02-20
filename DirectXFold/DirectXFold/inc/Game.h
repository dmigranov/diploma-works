class Game
{
public:
	Game() noexcept;


    // Initialization and management
    void Initialize(HWND window, int width, int height);

    // Basic game loop
    void Tick();

    // Properties
    void GetDefaultSize(int& width, int& height);
};