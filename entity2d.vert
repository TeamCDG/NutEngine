#version 330 core

in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TextureCoord;

out vec4 pass_Position;
out vec4 pass_Color;
out vec2 pass_TextureCoord;

layout (std140) uniform CameraMatrices
{
    mat4 cam_Matrix;
    mat4 camRot_Matrix;
};


uniform mat4 window_Matrix = mat4(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);
uniform mat4 translation_Matrix = mat4(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);
uniform mat4 rotation_Matrix = mat4(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);

void main(void) {

	vec4 pos = window_Matrix *  camRot_Matrix * cam_Matrix * translation_Matrix * rotation_Matrix * in_Position;
	gl_Position =  pos;
	pass_Position =  pos;
	pass_TextureCoord = in_TextureCoord;
	pass_Color = in_Color;
	
	
}