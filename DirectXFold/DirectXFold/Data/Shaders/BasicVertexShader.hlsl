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
VertexShaderOutput SimpleVertexShader(VertexShaderInput IN, uint instanceID : SV_InstanceID)
{
	VertexShaderOutput OUT;
 
	matrix mvp, temp;
	if (instanceID == 1)
		temp = matrix(0, 0, 0, 1,
	0, 1, 0, 0,
	0, 0, 1, 0,
	-1, 0, 0, 0);
	if (instanceID == 0)
		temp = matrix(1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 1, 0,
	0, 0, 0, 1);

		mvp = mul(projectionMatrix, mul(viewMatrix, mul(worldMatrix, temp)));

    OUT.color = IN.color;
	OUT.position = mul(mvp, IN.position);

	return OUT;
}