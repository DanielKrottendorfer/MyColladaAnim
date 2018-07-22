package org.lwjglb.game;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFW;
import org.lwjglb.engine.GameItem;
import org.lwjglb.engine.IGameLogic;
import org.lwjglb.engine.MouseInput;
import org.lwjglb.engine.Window;
import org.lwjglb.engine.graph.*;
import org.lwjglb.engine.graph.Animation.AnimatedModel;

import java.rmi.MarshalException;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.08f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private GameItem[] gameItems;

    private static final float CAMERA_POS_STEP = 0.05f;

    private GameItem gameItem2;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);


        //AnimatedModel mesh = ColladaLoader.loadAnimatedModel("src/main/resources/models/DaRealBlockTwistBend.dae");
        //AnimatedModel mesh = ColladaLoader.loadAnimatedModel("src/main/resources/models/DaRealBlock.dae");
        AnimatedModel mesh = ColladaLoader.loadAnimatedModel("src/main/resources/models/CharacterRunning3Weights.dae");
        //Mesh mesh = ColladaLoader.loadStaticMesh("src/main/resources/models/DaRealBlock.dae");
        //Mesh mesh = ColladaLoader.loadStaticMesh("src/main/resources/models/CharacterRunning.dae");


        mesh.stopAnimation();

        Texture texture = new Texture("/textures/diffuse.png");
        //Texture texture = new Texture("/textures/diffuse.png");

        mesh.setTexture(texture);

        Mesh plain = PlaneGenerator.generate(100f,100f,30, 30);

        GameItem p = new GameItem(plain,true);
        p.setScale(1);
        p.setPosition(-1, -10, -20);



        GameItem gameItem = new GameItem(mesh);
        gameItem.setScale(0.5f);
        gameItem.setPosition(-1, -1, -2);

        gameItems = new GameItem[]{gameItem,p};
    }


    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);

        if(window.isKeyPressed(GLFW_KEY_SPACE)) {
            if(gameItems[0].getAnimatedModel().isAnimationInProgress()){
                gameItems[0].getAnimatedModel().stopAnimation();
            }else {
                gameItems[0].getAnimatedModel().startAnimation();
            }
        }


        if(window.isKeyPressed(GLFW_KEY_T)){
            Vector3f gp = gameItems[0].getPosition();
            Vector3f cp = camera.getPosition();

            camera.setRotation(gp.x,gp.y,gp.z);

        }


        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1f;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1f;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1f;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1f;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1f;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1f;
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

    public void render(Window window,float elapsedTime) {
        renderer.render(window, camera, gameItems,elapsedTime);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }

}
