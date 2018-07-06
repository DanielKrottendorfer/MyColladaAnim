package org.lwjglb.engine.graph.Animation;

import org.joml.Vector4f;

import java.util.Arrays;

public class SkinPoint {
    private Vector4f positon;

    private float[] weights;
    private int[] jointI;

    public SkinPoint(Vector4f positon, float[] weights, int[] jointI) {
        this.positon = positon;
        this.weights = weights;
        this.jointI = jointI;
    }

    @Override
    public String toString() {
        return "SkinPoint{" +
                "positon=" + positon +
                ", weights=" + Arrays.toString(weights) +
                ", jointI=" + Arrays.toString(jointI) +
                '}';
    }

    public Vector4f getPositon() {
        return positon;
    }

    public void setPositon(Vector4f positon) {
        this.positon = positon;
    }

    public float[] getWeights() {
        return weights;
    }

    public void setWeights(float[] weights) {
        this.weights = weights;
    }

    public int[] getJointI() {
        return jointI;
    }

    public void setJointI(int[] jointI) {
        this.jointI = jointI;
    }
}
