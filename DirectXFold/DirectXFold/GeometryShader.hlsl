struct VertexInput
{
    float4 color : COLOR;
    float4 position : POSITIONT0;
};

struct VertexOutput
{
    float4 color : COLOR;
    float4 position : SV_Position; //стандартный System-Value для вертекса, который требуется на RS
};

static const float PI = 3.14159265f;

float3 GetCylinderPoint(float3 pos, float3 w, float3 u, float3 v, float theta, float t)
{
	float cost = cos(theta), sint = sin(theta);
	return pos + cost * u + sint * v + t * w;
}

[maxvertexcount(18)] // максимальное кол-во вертексов, которое мы можем добавить
void SimpleGeometryShader(triangle VertexInput input[3], inout TriangleStream<VertexOutput> stream)
{
	/*VertexOutput v1 = { input[0].color, input[0].position };
	stream.Append(v1); // добавление вертекса
	VertexOutput v2 = { input[1].color, input[1].position };
	stream.Append(v2); // добавление вертекса
	VertexOutput v3 = { input[2].color, input[2].position };
	stream.Append(v3); // добавление вертекса*/
	
	for (uint i = 0; i < 3; i++)
	{
		uint j = (i + 1) % 3;
		float4 p1 = input[i].position;
		float4 p2 = input[j].position;
		p1 /= p1.w;
		p2 /= p2.w;
       
		//u and v are perpendiculat to each other and tubeDir
		float3 tubeDir = (p2.xyz - p1.xyz);
		float tubeLength = length(tubeDir);
		float3 w = normalize(tubeDir);
		float3 u;
		if (w.x != 0 && w.y != 0)
			u = normalize(cross(w, float3(0, 0, 1)));
		else
			u = normalize(cross(w, float3(1, 0, 0)));
		float3 v = cross(w, u);
		
		uint horiVerticesCount = 4;
		float theta = 0, dtheta = 2 * PI / horiVerticesCount;
		for (uint thI = 0; thI < 4; thI++, theta += dtheta)
		{
			VertexOutput v1 = { input[i].color, float4(GetCylinderPoint(p1.xyz, w, u, v, theta, 0), 1) };
			stream.Append(v1);
			VertexOutput v2 = { input[j].color, float4(GetCylinderPoint(p2.xyz, -w, u, v, theta, 0), 1) };
			stream.Append(v2);
		}

		stream.RestartStrip(); // создаем примитив 
	}

}