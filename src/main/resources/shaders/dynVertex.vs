 #version 330

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;
layout (location=3) in vec3 weight;
layout (location=4) in int matrixIndices[3];

out vec2 outTexCoord;

uniform mat4 jointTransformMatrices[MAX_JOINTS];
uniform mat4 bindPosMatrix[MAX_JOINTS];
uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;


void main()
{

    vec4 fposition = vec4(position.x,position.z,position.y,0.0);
    vec4 accPosition = vec4(0.0,0.0,0.0,0.0);

    accPosition = accPosition + (((fposition*bindPosMatrix[matrixIndices[0]])*jointTransformMatrices[matrixIndices[0]])*weight.x);
    accPosition = accPosition + (((fposition*bindPosMatrix[matrixIndices[1]])*jointTransformMatrices[matrixIndices[1]])*weight.y);
    accPosition = accPosition + (((fposition*bindPosMatrix[matrixIndices[2]])*jointTransformMatrices[matrixIndices[2]])*weight.z);


    accPosition = vec4(accPosition.x,accPosition.z,accPosition.y,1.0);

    gl_Position = projectionMatrix * modelViewMatrix * accPosition;
    outTexCoord = texCoord;
}