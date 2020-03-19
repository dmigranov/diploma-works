struct VertexInput
{
    float4 color : COLOR;
    float4 position : POSITIONT0;
};

struct VertexOutput
{
    float4 color : COLOR;
    float2 tex : TEXCOORD;
    float4 position : SV_Position; //стандартный System-Value для вертекса, который требуется на RS
};


float3 GetCylinderPoint(float3 pos, float3 w, float3 u, float3 v, float theta, float t)
{
	float cost = cos(theta), sint = sin(theta);
	return cost * u + sint * v + t * w;
}

[maxvertexcount(3)] // максимальное кол-во вертексов, которое мы можем добавить

	void SimpleGeometryShader
	(triangle
	VertexInput input[3], inout
	TriangleStream<VertexOutput> stream)
{    
		VertexOutput v1 = { input[0].color, { 0, 0 }, input[0].position };
		stream.Append(v1); // добавление вертекса
		VertexOutput v2 = { input[1].color, { 0, 0 }, input[1].position };
		stream.Append(v2); // добавление вертекса
		VertexOutput v3 = { input[2].color, { 0, 0 }, input[2].position };
		stream.Append(v3); // добавление вертекса
    
    
		for (uint i = 0; i < 3; i++)
		{
			float4 p1 = input[i].position;
			float4 p2 = input[(i + 1) % 3].position;
			p1 /= p1.w;
			p2 /= p2.w;
        
        //u and v are perpendiculat to each other and tubeDir
			float3 tubeDir = normalize(p1.xyz - p2.xyz);
			float3 u;
			if (tubeDir.x != 0 && tubeDir.y != 0)
				u = normalize(cross(tubeDir, float3(0, 0, 1)));
			else
				u = normalize(cross(tubeDir, float3(1, 0, 0)));
			float3 v = cross(tubeDir, v);

		}

		stream.RestartStrip(); // создаем примитив 
	}

