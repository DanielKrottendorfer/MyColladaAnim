package org.lwjglb.engine.graph.World;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;
import org.lwjglb.engine.OpenSimplexNoise;
import org.lwjglb.engine.graph.Camera;
import org.lwjglb.engine.graph.Mesh;


public class WorldMesh {

    private final double FEATURE_SIZE;

    private final float REPEAT_DISTANZE = 10f;

    private float viewDistance;

    private final OpenSimplexNoise noise;

    private Camera camera;


    private Mesh plain;

    private WorldLoader wL;

    private Thread wLT;

    private int vertexCount;


    public WorldMesh(Camera camera, float viewDistance, int vertexCount, long seed, int featureSize) {

        this.camera=camera;
        this.noise = new OpenSimplexNoise(seed);

        this.FEATURE_SIZE = featureSize;

        this.vertexCount = vertexCount;

        Vector3f camPos = camera.getPosition();

        float xStart = camPos.x-viewDistance;
        float zStart = camPos.z-viewDistance;

        plain = PlaneGenerator.generateNoisePlane(xStart,zStart,viewDistance*2,viewDistance*2,vertexCount,vertexCount,noise,FEATURE_SIZE);

        /*
        wL = new WorldLoader(this);

        wLT = new Thread(wL);

        wLT.start();

        */

    }


    public double getFEATURE_SIZE() {
        return FEATURE_SIZE;
    }

    public OpenSimplexNoise getNoise() {
        return noise;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public float getViewDistance() {
        return viewDistance;
    }

    public void setViewDistance(float viewDistance) {
        this.viewDistance = viewDistance;
    }

    public Mesh getPlain() {
        return plain;
    }

    public void setPlain(Mesh plain) {
        this.plain = plain;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public float getREPEAT_DISTANZE() {
        return REPEAT_DISTANZE;
    }

    public void cleanUp(){

        //wL.stop();

        plain.cleanUp();

    }

}