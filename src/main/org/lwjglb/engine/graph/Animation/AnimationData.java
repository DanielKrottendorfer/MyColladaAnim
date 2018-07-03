package org.lwjglb.engine.graph.Animation;

import org.joml.Matrix4f;

import java.util.Arrays;

public class AnimationData {
    int[] transformI;
    int[] weightI;

    public AnimationData() {
    }

    public int[] getTransformI() {
        return transformI;
    }

    public void setTransformI(int[] transformI) {
        this.transformI = transformI;
    }

    public AnimationData(int[] transformI, int[] weightI) {
        this.transformI = transformI;
        this.weightI = weightI;
    }

    public int[] getWeights() {
        return weightI;
    }

    public void setWeights(int[] weights) {
        this.weightI = weights;
    }

    @Override
    public String toString() {
        return "AnimationData{" +
                "transformI=" + Arrays.toString(transformI) +
                ", weightI=" + Arrays.toString(weightI) +
                '}';
    }
}
