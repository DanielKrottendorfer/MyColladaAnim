package org.lwjglb.engine.graph.Animation;

import org.joml.Matrix4f;

public class Joint {
    public Matrix4f bindPosM;
    public Matrix4f jointPositionTransformM[];
    public final float[] timestamps;

    public Joint(Matrix4f bindPosM, Matrix4f[] jointPositionTransformM,float[] timestamps) {
        this.bindPosM = bindPosM;
        this.jointPositionTransformM = jointPositionTransformM;
        this.timestamps = timestamps;
    }
}
