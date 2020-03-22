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
	uint instanceID : SV_InstanceID;
};

struct VertexShaderOutput
{
    float4 color : COLOR;
    float4 position : POSITIONT;
};

//entry point
VertexShaderOutput SimpleVertexShader(VertexShaderInput IN)
{
	VertexShaderOutput OUT;
 
	matrix mvp;
	bool cond = true; //(instanceID == 0);
	if (cond)
        mvp = mul(projectionMatrix, mul(viewMatrix, worldMatrix));
    else
		mvp = mul(projectionMatrix, mul(viewMatrix, worldMatrix));
    OUT.color = IN.color;
	OUT.position = mul(mvp, IN.position);

	return OUT;
}