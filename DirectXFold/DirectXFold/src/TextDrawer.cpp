#include "pch.h"
#include "TextDrawer.h"
using namespace DirectX;
using namespace DirectX::SimpleMath;


TextDrawer::TextDrawer(ID3D11Device* device, ID3D11DeviceContext *context, const wchar_t *fontFile)
{
	m_spriteBatch = std::make_unique<SpriteBatch>(context);
	m_font = std::make_unique<SpriteFont>(device, fontFile);
}

void TextDrawer::DrawText()
{
	m_spriteBatch->Begin();

	const wchar_t* output = L"Hello World";

	Vector2 origin = m_font->MeasureString(output) / 2.f;

	m_font->DrawString(m_spriteBatch.get(), output,
		m_fontPos, Colors::White, 0.f, origin);

	m_spriteBatch->End();
}
