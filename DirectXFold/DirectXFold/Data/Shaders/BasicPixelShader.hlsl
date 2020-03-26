//static const float thickness = 0.01;

cbuffer PerApplication : register(b0)
{
	float4 fogColor;
	float thickness;
}


struct PixelShaderInput
{
	float4 color : COLOR;
	noperspective float3 edgeDistance : EDGEDISTANCE;
	float fogFactor : FOG_FACTOR;
};

//static const float4 fogColor = float4(0.5f, 0.5f, 0.5f, 1.0f);
 
float4 SimplePixelShader(PixelShaderInput IN) : SV_TARGET
{
	float3 edgeDistance = IN.edgeDistance;
	float distance = min(edgeDistance.x, min(edgeDistance.y, edgeDistance.z));
	
	float4 preFogColor;
	
	if (distance < thickness)
		//preFogColor = float4(0, 0, 0, 1);
		preFogColor = 0.7 * float4(0, 0, 0, 1) + 0.3 * IN.color; //draw fragment if close to edge
	else 
		preFogColor = IN.color; //а можно дискард
	//todo: исправить черные полоски при выключенном edgeDistance а то некрасиво
	
	return IN.fogFactor * preFogColor + (1.0 - IN.fogFactor) * fogColor;
;
}