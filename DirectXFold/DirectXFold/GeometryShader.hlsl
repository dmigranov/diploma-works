struct VertexInput
{
    float4 position : POSITION;
    float4 color : COLOR;
};

struct VertexOutput
{
    float4 color : COLOR;
    float2 tex : TEXCOORD;
    float4 position : SV_Position; //стандартный System-Value для вертекса, который требуется на RS
};

[maxvertexcount(3)] // максимальное кол-во вертексов, которое мы можем добавить
void SimpleGeometryShader(triangle VertexInput input[3], inout TriangleStream<VertexOutput> stream)
{    
    VertexOutput v1 = { input[0].color, {0, 0}, input[0].position };
    stream.Append(v1); // добавление вертекса
    VertexOutput v2 = { input[1].color, { 0, 0 }, input[1].position };
    stream.Append(v2); // добавление вертекса
    VertexOutput v3 = { input[2].color, { 0, 0 }, input[2].position };
    stream.Append(v3); // добавление вертекса

	stream.RestartStrip(); // создаем примитив (для Point – требуется один вертекс)
}