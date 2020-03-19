static const float thickness = 0.001;


struct PixelShaderInput
{
	float4 color : COLOR;
	noperspective float3 edgeDistance : EDGEDISTANCE;
};
 
float4 SimplePixelShader(PixelShaderInput IN) : SV_TARGET
{
	float3 edgeDistance = IN.edgeDistance;
	float distance = min(edgeDistance.x, min(edgeDistance.y, edgeDistance.z));
	
	if (distance == edgeDistance.x)
		return float4(0.52, 0, 0, 1);
	else if (distance == edgeDistance.y)
		return float4(0, 0.52, 0, 1);
	else// if (distance==GEdgeDistance[2])
		return float4(0, 0, 0.52, 1);
	
	/*if (distance < thickness)
		return float4(0, 0, 0, 1); //draw fragment if close to edge
	return IN.color; //else; а можно дискард*/
}