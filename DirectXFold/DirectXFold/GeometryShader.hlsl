struct VertexInput
{
    float4 color : COLOR;
    float4 position : POSITIONT;
};

struct VertexOutput
{
    float4 color : COLOR;
	noperspective float3 edgeDistance : EDGEDISTANCE;
    float4 position : SV_Position; //стандартный System-Value для вертекса, который требуется на RS
};

[maxvertexcount(3)] // максимальное кол-во вертексов, которое мы можем добавить
void SimpleGeometryShader(triangle VertexInput input[3], inout TriangleStream<VertexOutput> stream)
{
	//БАРИОЦЕНТРИЧЕСКИЕ КООРДИНАТЫ!
	
	float a = length(input[1].position.xyz - input[2].position.xyz);
	float b = length(input[2].position.xyz - input[0].position.xyz);
	float c = length(input[1].position.xyz - input[0].position.xyz);
		
	float alpha = acos((b * b + c * c - a * a) / (2.0 * b * c));
	float beta = acos((a * a + c * c - b * b) / (2.0 * a * c));
	float ha = abs(c * sin(beta));
	float hb = abs(c * sin(alpha));
	float hc = abs(b * sin(alpha));
	
	VertexOutput v1 = { input[0].color, float3(ha, 0, 0), input[0].position };
	stream.Append(v1); // добавление вертекса
	VertexOutput v2 = { input[1].color, float3(0, hb, 0), input[1].position };
	stream.Append(v2); // добавление вертекса
	VertexOutput v3 = { input[2].color, float3(0, 0, hc), input[2].position };
	stream.Append(v3); // добавление вертекса
	
	stream.RestartStrip(); // создаем примитив 
}