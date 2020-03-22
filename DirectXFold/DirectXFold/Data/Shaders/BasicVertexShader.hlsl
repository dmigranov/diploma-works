cbuffer PerApplication : register(b0)
{
    matrix projectionMatrixFront;
	matrix projectionMatrixBack;
}

cbuffer PerFrame : register(b1)
{
    matrix viewMatrixFront;
	matrix viewMatrixBack;
}

cbuffer PerObject : register(b2)
{
    matrix worldMatrix;
}

//POSITION and COLOR are semantics that are used to link vs variables to ps variables
struct VertexShaderInput
{
    float4 position : POSITION;
    float4 color: COLOR;
};

struct VertexShaderOutput
{
	float4 position : SV_POSITION;
    float4 color : COLOR;
};

//entry point
VertexShaderOutput SimpleVertexShader(VertexShaderInput IN, uint instanceID : SV_InstanceID)
{
	VertexShaderOutput OUT;
 
	matrix mvp, viewMatrix, projectionMatrix;
	if (instanceID == 0)
	{
		projectionMatrix = projectionMatrixFront;
		viewMatrix = viewMatrixFront;
	}
	else if (instanceID == 1)
	{
		projectionMatrix = projectionMatrixBack;
		viewMatrix = viewMatrixBack;
	}

	mvp = mul(projectionMatrix, mul(viewMatrix, worldMatrix));

	OUT.color = IN.color;
	OUT.position = mul(mvp, IN.position);

	return OUT;
}