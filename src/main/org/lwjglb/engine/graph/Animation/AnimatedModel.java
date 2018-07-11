package org.lwjglb.engine.graph.Animation;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.MemoryUtil;
import org.lwjglb.engine.graph.Texture;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class AnimatedModel {

    private static final Vector3f DEFAULT_COLOUR = new Vector3f(1.0f, 1.0f, 1.0f);

    private final int vaoId;

    private final List<Integer> vboIdList;

    private final int vertexCount;

    private Texture texture;

    private Vector3f colour;

    private Joint rootJoint;

    public final float[] timestamps;

    Matrix4f[] currentPose;

    final int jointCount;

    private float currentAnimationTime = 0f;

    private boolean animationInProgress = true;

    public AnimatedModel(float[] positions, float[] normV, float[] texture, float[] weights, int[] indices, int[] matrixIndices, Joint rootJoint, float[] timeStamps) {

        this.timestamps = timeStamps;

        this.rootJoint = rootJoint;

        jointCount = rootJoint.getJointC();

        currentPose = new Matrix4f[jointCount];

        advanceAnimation(0.1f);

        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        FloatBuffer vecWeightBuffer = null;
        IntBuffer vecMatIndexBuffer = null;
        IntBuffer jointCountBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            colour = DEFAULT_COLOUR;
            vertexCount = indices.length;
            vboIdList = new ArrayList();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);


            // Texture coordinates VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(texture.length);
            textCoordsBuffer.put(texture).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normV.length);
            vecNormalsBuffer.put(normV).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);


            // Vertex weight VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecWeightBuffer = MemoryUtil.memAllocFloat(weights.length);
            vecWeightBuffer.put(weights).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecWeightBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);


            // Vertex MatrixTransformIndices VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecMatIndexBuffer = MemoryUtil.memAllocInt(matrixIndices.length);
            vecMatIndexBuffer.put(matrixIndices).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecMatIndexBuffer, GL_STATIC_DRAW);
            glVertexAttribIPointer(4,3,GL_INT,0,0);




            // Index VBO

            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

/*
            for(int i = 0,y=0;i<indices.length;i+=3,y+=2){
                String print = " x "+p[i]+" y "+p[i+1]+" z "+p[i+2]+"\n"
                        +"tx l"+text[y]+" b "+text[y+1]+"\n"
                        +"w1 "+weig[i]+" w2 "+weig[i+1]+" w3 "+weig[i+2]+"\n"
                        +"m1 "+mat[i]+" m2 "+mat[i+1]+" m3 "+mat[i+2]+"\n";

                System.out.println(print);
            }
*/




        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);
            }
            if (vecWeightBuffer != null) {
                MemoryUtil.memFree(vecWeightBuffer);
            }
            if (vecMatIndexBuffer != null) {
                MemoryUtil.memFree(vecMatIndexBuffer);
            }
            if (jointCountBuffer != null) {
                MemoryUtil.memFree(jointCountBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    public Matrix4f[] getCurrentPose() {
        return currentPose;
    }

    public float getCurrentAnimationTime() {
        return currentAnimationTime;
    }

    public void setCurrentAnimationTime(float currentAnimationTime) {
        this.currentAnimationTime = currentAnimationTime;
    }

    public boolean isAnimationInProgress() {
        return animationInProgress;
    }

    public void stopAnimation() {
        animationInProgress = false;
    }

    public void startAnimation() {
        animationInProgress = true;
    }

    public void setAnimationInProgress(boolean animationInProgress) {
        this.animationInProgress = animationInProgress;
    }

    public Joint getRootJoint() {
        return rootJoint;
    }

    public boolean isTextured() {
        return this.texture != null;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public Vector3f getColour() {
        return this.colour;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }


    public void render() {
        if (texture != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }

        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(4);

        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the texture
        if (texture != null) {
            texture.cleanup();
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    private static Joint[] generateJointArray(Joint rootJoint) {
        ArrayList<Joint> jl = new ArrayList<>();

        for(int i = 0;true;i++){
            Joint j = rootJoint.findJoint(i);
            if(j==null) {
                break;
            }

            jl.add(j);
        }

        Joint[] jr = new Joint[jl.size()];

        for(int i = 0;i<jr.length;i++){
            jr[i] = jl.get(i);
        }

        return jr;
    }

    public void multiplyMatrices(Joint[] rJ,Matrix4f[] currentPose){

        for(int i = 0;i<rJ.length;i++){

            if(rJ[i].getChildren()!=null){
                for (int y = 0;y<rJ[i].getChildren().length;y++){
                    Matrix4f temp = new Matrix4f(currentPose[rJ[i].getIndex()]);

                    currentPose[rJ[i].getChildren()[y].getIndex()] = temp.mul(currentPose[rJ[i].getChildren()[y].getIndex()]);

                }
            }

        }

    }

    /*
    private void multiplyMatrices(Joint joint, Matrix4f[] currentPose) {

        if(joint.getChildren()!=null){
            for(Joint j: joint.getChildren()){

                Matrix4f temp = new Matrix4f(currentPose[joint.getIndex()]);



                currentPose[j.getIndex()] = temp.mul(currentPose[j.getIndex()]);



            }
            for(Joint j: joint.getChildren())
                multiplyMatrices(j,currentPose);

        }

    }
*/

    public void advanceAnimation(float interval) {


        if(isAnimationInProgress()) {

            currentAnimationTime += interval;

            if(currentAnimationTime>timestamps[timestamps.length-1]) {

                currentAnimationTime -= timestamps[timestamps.length - 1];

            }
        }
        if(currentAnimationTime<timestamps[0]){
            currentAnimationTime = timestamps[0];
        }

        float iV ;

        for(int i = 1;i<timestamps.length;i++){

            if(timestamps[i-1]<=currentAnimationTime&&timestamps[i]>=currentAnimationTime){


                float prevTime = currentAnimationTime-timestamps[i-1];
                float nextTime = timestamps[i]-currentAnimationTime;

                //System.out.println(currentAnimationTime);

                //System.out.println("pT "+prevTime+" nT "+nextTime);

                iV = (nextTime/(nextTime+prevTime));

                //System.out.println("iV"+iV+"\n");

                Joint tempRoot = new Joint(rootJoint);

                Joint[] joints = generateJointArray(tempRoot);


                for (int y = 0;y<jointCount;y++){


                    Matrix4f prevPose = joints[y].jointKeyFPositionsTransformM[i-1];

                    //System.out.println("pp "+prevPose);

                    Matrix4f nextPose = joints[y].jointKeyFPositionsTransformM[i] ;

                    //System.out.println("np "+nextPose);


                    //System.out.println("bp "+bindPosM);

                    prevPose.lerp(nextPose,iV);


                    currentPose[y] = prevPose;

                }


                multiplyMatrices(joints,currentPose);


                for(int y = 0; y<jointCount;y++)
                    currentPose[y].mul(joints[y].jointBindPositionTransformM);


                return;
            }
        }
    }
}
