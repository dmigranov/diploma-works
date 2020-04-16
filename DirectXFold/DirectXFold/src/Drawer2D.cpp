#include "pch.h"
#include "Drawer2D.h"

using namespace DirectX;

Drawer2D::Drawer2D(ID3D11Device* device, ID3D11DeviceContext* context)
{
    m_states = std::make_unique<CommonStates>(device);
    m_effect = std::make_unique<BasicEffect>(device);

    m_effect->SetVertexColorEnabled(true);

    void const* shaderByteCode;
    size_t byteCodeLength;

    m_effect->GetVertexShaderBytecode(&shaderByteCode, &byteCodeLength);

    DX::ThrowIfFailed(
        device->CreateInputLayout(VertexPositionColor::InputElements,
            VertexPositionColor::InputElementCount,
            shaderByteCode, byteCodeLength,
            &m_inputLayout));

    m_batch = std::make_unique<PrimitiveBatch<VertexPositionColor>>(context);
}
