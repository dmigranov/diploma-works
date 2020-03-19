cbuffer PerApplication : register(b0)
{
    matrix projectionMatrix;
}

cbuffer PerFrame : register(b1)
{
    matrix viewMatrix;
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
    float4 position : POSITIONT;
    float4 color : COLOR;
};

//entry point
VertexShaderOutput SimpleVertexShader(VertexShaderInput IN)
{
	VertexShaderOutput OUT;
 
	matrix mvp = mul(projectionMatrix, mul(viewMatrix, worldMatrix));
    OUT.color = IN.color;
	OUT.position = mul(mvp, IN.position);
 
	return OUT;
}