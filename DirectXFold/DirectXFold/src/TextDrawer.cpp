#include "pch.h"
#include "TextDrawer.h"
using namespace DirectX;

TextDrawer::TextDrawer(ID3D11Device* device, ID3D11DeviceContext *context, const wchar_t *fontFile)
{
	m_spriteBatch = std::make_unique<SpriteBatch>(context);
	m_font = std::make_unique<SpriteFont>(device, fontFile);
}
