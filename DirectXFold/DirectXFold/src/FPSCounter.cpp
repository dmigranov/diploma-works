#include "pch.h"
#include "FPSCounter.h"

FPSCounter::FPSCounter()
{
	m_fps = 0;
	m_count = 0;
	m_startTime = timeGetTime();
}

void FPSCounter::Update()
{
	m_count++;

	if (timeGetTime() >= (m_startTime + 1000))
	{
		m_fps = m_count;
		m_count = 0;

		m_startTime = timeGetTime();
	}
}

int FPSCounter::GetFPS()
{
	return m_fps;
}
