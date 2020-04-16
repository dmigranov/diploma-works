#pragma once
#include <memory>
#include "Drawer2D.h"
#include "PrimitiveBatch.h"
#include "GeometricPrimitive.h"
#include "CommonStates.h"
#include "Effects.h"

class Drawer2D
{
public:
	Drawer2D(ID3D11Device* device, ID3D11DeviceContext* context);

private:
	std::unique_ptr<DirectX::CommonStates> m_states;
	std::unique_ptr<DirectX::BasicEffect> m_effect;
	std::unique_ptr<DirectX::PrimitiveBatch<DirectX::VertexPositionColor>> m_batch;
	ID3D11InputLayout * m_inputLayout;
};

