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
struct AppData
{
    float4 position : POSITION;
    float3 color: COLOR;
};

struct VertexShaderOutput
{
    float4 color : COLOR;
    float4 position : SV_POSITION;  //system-value semantic that is required by the rasterizer stage
};

//entry point
VertexShaderOutput SimpleVertexShader(AppData IN)
{
	VertexShaderOutput OUT;
 
	matrix mvp = mul(projectionMatrix, mul(viewMatrix, worldMatrix));
	OUT.position = mul(mvp, IN.position);
	OUT.color = float4(IN.color, 1.0f);
 
	return OUT;
}