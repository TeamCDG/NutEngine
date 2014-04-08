package cdg.nut.util;

public abstract class DefaultShader {

	public static final ShaderProgram simple = new ShaderProgram("#version 150 core\n"+
			"uniform mat4 window_Matrix = mat4(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);\n"+
			"in vec4 in_Position;\n"+
			"in vec4 in_Color;\n"+
			"in vec2 in_TextureCoord;\n"+
			"out vec4 pass_Color;\n"+
			"out vec2 pass_TextureCoord;\n"+
			"void main(void) {\n"+
			"	vec4 v = window_Matrix * in_Position;\n"+
			"	gl_Position = v;\n"+
			"	pass_Color = in_Color;\n"+
			"	pass_TextureCoord = in_TextureCoord;\n"+
			"}", "#version 150 core\n"+
			"uniform sampler2D texture_diffuse;\n"+
			"uniform vec4 color = vec4(1.0, 1.0, 1.0, 1.0);\n"+
			"uniform int selection = 0;\n"+
			"in vec4 pass_Color;\n"+
			"in vec2 pass_TextureCoord;\n"+
			"out vec4 out_Color;\n"+
			"void main(void) {\n"+
			"	vec4 c = texture2D(texture_diffuse, vec2(pass_TextureCoord.x * -1, pass_TextureCoord.y));\n"+
			"	if(selection == 0) {\n"+
			"	if(c == vec4(0.0, 0.0, 0.0, 0.0)) {\n"+
			"		out_Color = color;\n"+
			"	 } else {\n"+
			"		out_Color = color*c; }\n"+
			"	 } else\n"+
			"		out_Color = pass_Color;\n"+
			"}", "simple");
	
	public static final ShaderProgram text = new ShaderProgram("#version 150 core\n"+
			"uniform mat4 window_Matrix = mat4(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);\n"+
			"in vec4 in_Position;\n"+
			"in vec4 in_Color;\n"+
			"in vec2 in_TextureCoord;\n"+
			"out vec4 pass_Color;\n"+
			"out vec2 pass_TextureCoord;\n"+
			"void main(void) {\n"+
			"	vec4 v = window_Matrix * in_Position;\n"+
			"	gl_Position = v;\n"+
			"	pass_Color = in_Color;\n"+
			"	pass_TextureCoord = in_TextureCoord;\n"+
			"}", "#version 150 core\n"+
			"uniform sampler2D texture_diffuse;\n"+
			"uniform vec4 color = vec4(1.0, 1.0, 1.0, 1.0);\n"+
			"uniform int selection = 0;\n"+
			"in vec4 pass_Color;\n"+
			"in vec2 pass_TextureCoord;\n"+
			"out vec4 out_Color;\n"+
			"void main(void) {\n"+
			"	vec4 c = pass_Color*texture2D(texture_diffuse, pass_TextureCoord);\n"+
			"	out_Color = c;/*vec4(1.0,1.0,1.0,1.0);*/"+
			"}", "text");
}
