package org.lwjglb.engine;

import org.joml.Vector3f;
import org.lwjglb.engine.graph.Animation.AnimatedModel;
import org.lwjglb.engine.graph.Mesh;

public class GameItem {

    private final Mesh mesh;

    private final AnimatedModel animatedModel;
    
    private final Vector3f position;
    
    private float scale;

    private final Vector3f rotation;

    private final boolean animated;

    private final boolean colored;

    private final int vaoId;

    public GameItem(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
        animatedModel = null;
        animated =false;
        colored = false;
        vaoId = mesh.getVaoId();
    }
    public GameItem(Mesh mesh,boolean isColored) {

        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
        animatedModel = null;
        animated =false;
        colored = true;
        vaoId = mesh.getVaoId();
    }
    public GameItem(AnimatedModel animatedModel) {
        this.animatedModel=animatedModel;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
        mesh = null;
        animated = true;
        colored = false;
        vaoId = animatedModel.getVaoId();
    }

    public boolean isColored() {
        return colored;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }
    
    public Mesh getMesh() {
        return mesh;
    }

    public AnimatedModel getAnimatedModel() {
        return animatedModel;
    }

    public boolean isAnimated() {
        return animated;
    }

    public int getVaoId() {
        return vaoId;
    }
}