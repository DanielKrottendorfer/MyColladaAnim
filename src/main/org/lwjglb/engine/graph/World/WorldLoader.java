package org.lwjglb.engine.graph.World;

import org.joml.Vector3f;
import org.lwjglb.engine.OpenSimplexNoise;
import org.lwjglb.engine.graph.Camera;
import org.lwjglb.engine.graph.Mesh;

public class WorldLoader implements Runnable {

    private WorldMesh world;
    private boolean running = true;

    private Mesh tempPlane;

    private boolean nextPlaneReady = false;


    public WorldLoader(WorldMesh worldMesh) {
        this.world = worldMesh;
    }

    public void stop(){
        running = false;
    }

    public void run() {

        Camera cam = world.getCamera();
        Vector3f lastPos = new Vector3f(cam.getPosition());

        float rD = world.getREPEAT_DISTANZE();

        float viewDistance = world.getViewDistance();

        int vertexCount = world.getVertexCount();

        OpenSimplexNoise noise = world.getNoise();

        double FEATURE_SIZE = world.getFEATURE_SIZE();

        while (running){

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Vector3f currentPos = new Vector3f(cam.getPosition());

            float dX = lastPos.x-currentPos.x;
            float dZ = lastPos.z-currentPos.z;


            if(Math.abs(dX)>rD&&Math.abs(dZ)>rD){




                float xStart = currentPos.x-viewDistance;
                float zStart = currentPos.z-viewDistance;

                setTempPlane(PlaneGenerator.generateNoisePlane(xStart,zStart,viewDistance*2,viewDistance*2,vertexCount,vertexCount,noise,FEATURE_SIZE));

                setNextPlaneReady(true);

                lastPos = currentPos;

            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Mesh getTempPlane() {
        return tempPlane;
    }

    public void setTempPlane(Mesh tempPlane) {
        this.tempPlane = tempPlane;
        setNextPlaneReady(false);
    }

    public boolean isNextPlaneReady() {
        return nextPlaneReady;
    }

    public void setNextPlaneReady(boolean nextPlaneReady) {
        this.nextPlaneReady = nextPlaneReady;
    }
}
