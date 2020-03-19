static const float thickness = 0.02;


struct PixelShaderInput
{
	float4 color : COLOR;
	noperspective float3 edgeDistance : EDGEDISTANCE;
};
 
float4 SimplePixelShader(PixelShaderInput IN) : SV_TARGET
{
	float3 edgeDistance = IN.edgeDistance;
	float distance = min(edgeDistance.x, min(edgeDistance.y, edgeDistance.z));
	
	if (distance < thickness)
		return float4(0, 0, 0, 1); //draw fragment if close to edge
	return IN.color; //else; а можно дискард
    //return float4(IN.depth, IN.depth, IN.depth, 1);
}