#include "pch.h"
#include "TextDrawer.h"
using namespace DirectX;

TextDrawer::TextDrawer(ID3D11DeviceContext context)
{
	m_spriteBatch = std::make_unique<SpriteBatch>(context);
}
