cbuffer PerApplication : register(b0)
{
    matrix projectionMatrix;
	matrix projectionMatrixAnti;
}

cbuffer PerFrame : register(b1)
{
    matrix viewMatrix;
	matrix viewMatrixAnti;
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
VertexShaderOutput SimpleVertexShader(VertexShaderInput IN, uint instanceID : SV_VertexID)
{
	VertexShaderOutput OUT;
 
	matrix mvp;
	bool cond = (instanceID % 2  == 0);
	if (cond)
		OUT.color = float4(1.f, 1.f, 1.f, 1.f);
	else;
		OUT.color = float4(0.f, 0.f, 0.f, 0.f);


	mvp = mul(projectionMatrix, mul(viewMatrix, worldMatrix));

    //OUT.color = IN.color;
	OUT.position = mul(mvp, IN.position);

	return OUT;
}