#version 330

in vec3 outColor;

out vec4 fragColor;

uniform sampler2D texture_sampler;

void main()
{
        fragColor = vec4(outColor, 1);
}