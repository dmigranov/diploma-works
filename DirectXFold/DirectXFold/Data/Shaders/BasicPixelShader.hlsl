struct PixelShaderInput
{
	float4 color : COLOR;
};
 
float4 SimplePixelShader(PixelShaderInput IN) : SV_TARGET
{
    return IN.color;
    //return float4(IN.depth, IN.depth, IN.depth, 1);
}