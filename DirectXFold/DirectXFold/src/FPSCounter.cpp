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
}

int FPSCounter::GetFPS()
{
	return 0;
}
