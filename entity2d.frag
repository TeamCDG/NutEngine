#version 330 core
#extension GL_ARB_shading_language_420pack: enable 

layout(binding=0) uniform sampler2D primary;
layout(binding=1) uniform sampler2D layer1;
layout(binding=2) uniform sampler2D layer2;
layout(binding=3) uniform sampler2D layer3;
layout(binding=4) uniform sampler2D layer4;
layout(binding=5) uniform sampler2D layer5;


//uniform vec4 shield_color = vec4(0.4, 1.0, 0.4, 1.0);
//uniform float shield_level = 1.0;
//uniform vec4 team_color = vec4(0.0, 0.6, 0.0, 1.0);
uniform vec2 seed = vec2(1.0, 0.5);
uniform vec4 color = vec4(1.0, 1.0, 1.0, 1.0);

uniform int selection = 0;

uniform int noTexture = 0;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

float rand(vec2 n, float min)
{
	return 0.0 + min * fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);
}

void main(void) 
{

	if(selection == 1)
	{
		out_Color = vec4(pass_Color.x, pass_Color.y, pass_Color.z, 1.0);
		
	}
	else
	{
		if(noTexture == 1)
			out_Color = color;
		else
		{
			vec4 c = color * texture2D(primary,vec2(pass_TextureCoord.x, -pass_TextureCoord.y));
			vec4 l1 = color * texture2D(layer1,vec2(pass_TextureCoord.x, -pass_TextureCoord.y));
			vec4 l2 = color * texture2D(layer2,vec2(pass_TextureCoord.x, -pass_TextureCoord.y));
			vec4 l3 = color * texture2D(layer3,vec2(pass_TextureCoord.x, -pass_TextureCoord.y));
			vec4 l4 = color * texture2D(layer4,vec2(pass_TextureCoord.x, -pass_TextureCoord.y));
			
			

			if(l4.w > 0.1)
				out_Color = l4;
			else if(l3.w > 0.1)
				out_Color = l3;
			else if(l2.w > 0.1)
				out_Color = l2;
			else if(l1.w > 0.1)
				out_Color = l1;
			else
				out_Color = c;
		}
	}
}
