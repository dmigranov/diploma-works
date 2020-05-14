#pragma once
class FPSCounter
{
public: 
	FPSCounter();
	void Update();	//called each frame
	int GetFPS();
private:
	unsigned int m_fps, m_count;
	DWORD m_startTime;
};

