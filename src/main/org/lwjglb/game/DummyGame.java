package org.lwjglb.game;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFW;
import org.lwjglb.engine.*;
import org.lwjglb.engine.graph.*;
import org.lwjglb.engine.graph.Animation.AnimatedModel;
import org.lwjglb.engine.graph.World.PlaneGenerator;
import org.lwjglb.engine.graph.World.WorldMesh;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.08f;

    private final Vector3f cameraInc;

    private final Renderer renderer;
    private final WorldRenderer worldRenderer;

    private final Camera camera;

    private GameItem[] gameItems;
    private  WorldMesh world;

    private static final float CAMERA_POS_STEP = 0.05f;

    private GameItem gameItem2;

    private static float speed = 1f;

    public DummyGame() {
        renderer = new Renderer();
        worldRenderer = new WorldRenderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        worldRenderer.init(window);


        //AnimatedModel mesh = ColladaLoader.loadAnimatedModel("src/main/resources/models/DaRealBlockTwistBend.dae");
        //AnimatedModel mesh = ColladaLoader.loadAnimatedModel("src/main/resources/models/DaRealBlock.dae");
        //AnimatedModel mesh = ColladaLoader.loadAnimatedModel("src/main/resources/models/CharacterRunning3Weights.dae");
        //Mesh mesh = ColladaLoader.loadStaticMesh("src/main/resources/models/DaRealBlock.dae");


        AnimatedModel mesh = ColladaLoader.loadAnimatedModel("src/main/resources/models/cowboy3W.dae");
        mesh.stopAnimation();
        Texture texture = new Texture("/textures/diffuse.png");
        mesh.setTexture(texture);

        GameItem gameItem = new GameItem(mesh);
        gameItem.setScale(0.5f);
        gameItem.setPosition(-1, -1, -2);




        world = new WorldMesh(cameraInc.x,cameraInc.y,10f,10,10,69,10);

        gameItems = new GameItem[]{};





    }


    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);

        if(window.isKeyPressedFirst(GLFW_KEY_SPACE)) {
            if(gameItems[0].getAnimatedModel().isAnimationInProgress()){
                gameItems[0].getAnimatedModel().stopAnimation();
            }else {
                gameItems[0].getAnimatedModel().startAnimation();
            }
        }

        Vector3f r = camera.getRotation();

        if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)){
            speed = 3f;
        }else {
            speed = 1f;
        }

        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -speed;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = speed;
        }

        if (window.isKeyPressed(GLFW_KEY_Q)) {
            camera.setRotation(r.x,r.y-2f,r.z);
        } else if (window.isKeyPressed(GLFW_KEY_E)) {
            camera.setRotation(r.x,r.y+2f,r.z);
        }

        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -speed;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = speed;
        }

        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -speed;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = speed;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse            
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        for(GameItem gi : gameItems){
            if(gi.isAnimated()){
                gi.getAnimatedModel().advanceAnimation(interval);
            }
        }
    }

    public void render(Window window) {


        worldRenderer.render(window,camera,world);
        renderer.render(window, camera, gameItems);


    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }

}
