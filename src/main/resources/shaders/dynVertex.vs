 #version 330

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;
layout (location=3) in vec3 weight;
layout (location=4) in ivec3 matrixIndices;

out vec2 outTexCoord;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 jointTransformB;
uniform mat4 jointTransformM;
uniform mat4 jointTransformT;


void main()
{

    vec4 accPosition = vec4(0,0,0,0);

    for( int i = 0;i<3;i++){
        float w = 0.0;
        if(i==0){
            w = weight.x;
        }
        if(i==1){
            w = weight.y;
        }
        if(i==2){
            w = weight.z;
        }
        if(matrixIndices[i]==0&&w>0.0){
            vec4 fposition = vec4(position.x,position.z,position.y,1.0);
            fposition = fposition*jointTransformB;
            fposition = fposition*w;
            accPosition = accPosition + fposition;
        }
        if(matrixIndices[i]==1&&w>0.0){
             vec4 fposition = vec4(position.x,position.z,position.y,1.0);
             fposition = fposition*jointTransformM;
             fposition = fposition*w;
             accPosition = accPosition + fposition;
         }
         if(matrixIndices[i]==2&&w>0.0){
             vec4 fposition = vec4(position.x,position.z,position.y,1.0);
             fposition = fposition*jointTransformT;
             fposition = fposition*w;
             accPosition = accPosition + fposition;
         }
    }

    accPosition = vec4(accPosition.x,accPosition.z,accPosition.y,1.0);

    gl_Position = projectionMatrix * modelViewMatrix * accPosition;
    outTexCoord = texCoord;
}