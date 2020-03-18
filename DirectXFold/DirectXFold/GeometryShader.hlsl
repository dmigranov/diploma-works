struct PixelInput
{
	float4 Position : SV_POSITION; // стандартный System-Value для вертекса
};

[maxvertexcount(1)] // максимальное кол-во вертексов, которое мы можем добавить
void SimpleGeometryShader(point PixelInput input[1], inout PointStream<PixelInput> stream)
{
	PixelInput pointOut = input[0]; // получение вертекса
	
	stream.Append(pointOut); // добавление вертекса
	stream.RestartStrip(); // создаем примитив (для Point – требуется один вертекс)
}