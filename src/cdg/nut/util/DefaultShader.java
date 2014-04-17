package cdg.nut.util;

public abstract class DefaultShader {

	public static final ShaderProgram simple = new ShaderProgram("#version 150 core\n"+
			"uniform mat4 window_Matrix = mat4(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);\n"+
			"uniform vec4 clippingArea = vec4(0.0,0.0,0.0,0.0);\n"+
			"uniform int selection = 0;\n"+
			"in vec4 in_Position;\n"+
			"in vec4 in_Color;\n"+
			"in vec2 in_TextureCoord;\n"+
			"out vec4 pass_Color;\n"+
			"out vec4 pass_Position;\n"+
			"out vec2 pass_TextureCoord;\n"+
			"void main(void) {\n"+
			"	vec4 v = window_Matrix * in_Position;\n"+
			"	gl_Position = v;\n"+
			"	pass_Color = in_Color;\n"+
			"	pass_Position = v;\n"+
			"	pass_TextureCoord = in_TextureCoord;\n"+
			"}", "#version 150 core\n"+
			"uniform sampler2D texture_diffuse;\n"+
			"uniform vec4 color = vec4(1.0, 1.0, 1.0, 1.0);\n"+
			"uniform vec4 clippingArea = vec4(0.0,0.0,0.0,0.0);\n"+
			"uniform int selection = 0;\n"+
			"in vec4 pass_Color;\n"+
			"in vec4 pass_Position;\n"+
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
			"	vec4 v = pass_Position;\n"+
			"	if(!(v.x+1 >= clippingArea.x+1 && v.x+1 <= clippingArea.z+1 && \n"+
			"	   v.y+1 <= clippingArea.y+1 && v.y+1 >= clippingArea.w+1) && selection == 0 && clippingArea != vec4(0.0, 0.0, 0.0, 0.0)) {\n"+
			"		//outside of the clipping area. do not draw it.\n"+
			"		out_Color = vec4(c.x, c.y, c.z, 0.0);"+
			"	}\n"+
			"}", "simple");
	
	public static final ShaderProgram text = new ShaderProgram("#version 150 core\n"+
			"uniform mat4 window_Matrix = mat4(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);\n"+
			"uniform vec4 clippingArea = vec4(0.0,0.0,0.0,0.0);\n"+
			"uniform int selection = 0;\n"+
			"in vec4 in_Position;\n"+
			"in vec4 in_Color;\n"+
			"in vec2 in_TextureCoord;\n"+
			"out vec4 pass_Color;\n"+
			"out vec4 pass_Position;\n"+
			"out vec2 pass_TextureCoord;\n"+
			"void main(void) {\n"+
			"	vec4 v = window_Matrix * in_Position;\n"+
			"	gl_Position = v;\n"+
			"	pass_Position = v;\n"+
			"	pass_Color = in_Color;\n"+
			"	pass_TextureCoord = in_TextureCoord;\n"+
			"}", "#version 150 core\n"+
			"uniform sampler2D texture_diffuse;\n"+
			"uniform vec4 color = vec4(1.0, 1.0, 1.0, 1.0);\n"+
			"uniform vec4 clippingArea = vec4(0.0,0.0,0.0,0.0);\n"+
			"uniform int selection = 0;\n"+
			"in vec4 pass_Color;\n"+
			"in vec4 pass_Position;\n"+
			"in vec2 pass_TextureCoord;\n"+
			"out vec4 out_Color;\n"+
			"void main(void) {\n"+
			"	vec4 c = pass_Color*texture2D(texture_diffuse, pass_TextureCoord);\n"+
			"	vec4 v = pass_Position;\n"+
			"	if(!(v.x+1 >= clippingArea.x+1 && v.x+1 <= clippingArea.z+1 && \n"+
			"	   v.y+1 <= clippingArea.y+1 && v.y+1 >= clippingArea.w+1) && selection == 0) {\n"+
			"		//outside of the clipping area. do not draw it.\n"+
			"		out_Color = vec4(c.x, c.y, c.z, 0.0);"+
			"	} else {\n"+
			"		out_Color = c;\n"+
			"	}\n"+
			"}", "text");
}
