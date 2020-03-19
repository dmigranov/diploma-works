static const float thickness = 0.01;


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
		return 0.7 * float4(0, 0, 0, 1) + 0.3 * IN.color; //draw fragment if close to edge
	return IN.color; //else; а можно дискард
}