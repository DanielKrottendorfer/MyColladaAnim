#version 330

in vec3 outColor;

out vec4 flatColor;

uniform sampler2D texture_sampler;

void main()
{
        flatColor = vec4(outColor, 1);
}