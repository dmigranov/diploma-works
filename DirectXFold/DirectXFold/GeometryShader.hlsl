struct VertexInput
{
	float4 color : COLOR;
	float depth : DEPTH;
	float4 position : POSITION; //стандартный System-Value для вертекса, который требуется на RS
};

struct VertexOutput
{
    float4 color : COLOR;
    float depth : DEPTH;
    float4 position : SV_POSITION; //стандартный System-Value для вертекса, который требуется на RS
};

[maxvertexcount(3)] // максимальное кол-во вертексов, которое мы можем добавить
void SimpleGeometryShader(triangle VertexInput input[3], inout TriangleStream<VertexOutput> stream)
{
    VertexOutput v1 = { input[0].color, input[0].depth, input[0].position * 8 };
    stream.Append(v1); // добавление вертекса
    VertexOutput v2 = { input[1].color, input[1].depth, input[1].position };
    stream.Append(v2); // добавление вертекса
    VertexOutput v3 = { input[2].color, input[2].depth, input[2].position };
    stream.Append(v3); // добавление вертекса

	stream.RestartStrip(); // создаем примитив (для Point – требуется один вертекс)
}