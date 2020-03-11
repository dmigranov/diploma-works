#pragma once
#include <SpriteBatch.h>
#include <SpriteFont.h>

class TextDrawer
{
public:
	TextDrawer(ID3D11Device* device, ID3D11DeviceContext* context, const wchar_t* fontFile);
	void DrawText();
private:
	std::unique_ptr<DirectX::SpriteBatch> m_spriteBatch;
	std::unique_ptr<DirectX::SpriteFont> m_font;
};

