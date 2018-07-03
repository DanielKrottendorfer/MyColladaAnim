package org.lwjglb.game;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMetaData;
import org.lwjgl.assimp.AIScene;
import org.lwjglb.engine.GameItem;
import org.lwjglb.engine.IGameLogic;
import org.lwjglb.engine.MouseInput;
import org.lwjglb.engine.Window;
import org.lwjglb.engine.graph.*;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

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

        //Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");



        /*

        float[] p={

                //1
                1.0f,1.0f,1.0f,
                //2
                3.0f,1.0f,1.0f,
                //3
                3.0f,1.0f,3.0f,
                //4
                1.0f,1.0f,3.0f,
                //5
                2.0f,3.0f,2.0f,


                1.0f,1.0f,1.0f,
                3.0f,1.0f,1.0f,
                2.0f,3.0f,2.0f,
                3.0f,1.0f,1.0f,
                3.0f,1.0f,3.0f,
                2.0f,3.0f,2.0f,
                3.0f,1.0f,3.0f,
                1.0f,1.0f,3.0f,
                2.0f,3.0f,2.0f,
                1.0f,1.0f,3.0f,
                1.0f,1.0f,1.0f,
                2.0f,3.0f,2.0f,
                1.0f,1.0f,1.0f,
                1.0f,1.0f,3.0f,
                3.0f,1.0f,1.0f,
                1.0f,1.0f,3.0f,
                3.0f,1.0f,3.0f,
                3.0f,1.0f,1.0f,




        };
        int[] i=new int[18];

        for(int y=0;y<i.length;y++)
            i[y]=y;

        float[] t={
                /*
                //0
                0.0f,1.0f,
                //1
                1.0f/6.0f,0.0f,
                //2
                1.0f/3.0f,1.0f,
                //3
                1.0f/2.0f,0.0f,
                //4
                2.0f/3.0f,1.0f,
                //5
                5.0f/6.0f,0.0f,
                //6
                1.0f,1.0f


                0.0f,1.0f,
                1.0f/6.0f,0.0f,
                1.0f/3.0f,1.0f,
                1.0f/6.0f,0.0f,
                1.0f/3.0f,1.0f,
                1.0f/2.0f,0.0f,
                1.0f/3.0f,1.0f,
                2.0f/3.0f,1.0f,
                1.0f/2.0f,0.0f,
                2.0f/3.0f,1.0f,
                5.0f/6.0f,0.0f,
                1.0f/2.0f,0.0f,
                2.0f/3.0f,1.0f,
                1.0f,1.0f,
                5.0f/6.0f,0.0f,
                2.0f/3.0f,1.0f,
                1.0f,1.0f,
                5.0f/6.0f,0.0f,

        };
        */

        /*
        Mesh mesh = DAELoader.loadMesh("src/main/resources/models/bendingBlock2.dae");
        Texture texture = new Texture("/textures/Untitled.png");
        mesh.setTexture(texture);

        GameItem gameItem = new GameItem(mesh);
        gameItem.setScale(0.5f);
        gameItem.setPosition(0, 0, -2);
        gameItems = new GameItem[]{gameItem};


/*

        Mesh mesh = OBJLoader.loadMesh("/models/colorblock.obj");
        Texture texture = new Texture("/textures/Untitled.png");
        mesh.setTexture(texture);

        //Mesh mesh = DAELoader.loadMesh("src/main/resources/models/bendingBlock2.dae");
        GameItem gameItem = new GameItem(mesh);
        gameItem.setScale(1.5f);
        gameItem.setPosition(0, 0, -2);
        gameItems = new GameItem[]{gameItem};
*/
    /*
        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("/textures/grassblock.png");
        mesh.setTexture(texture);
        GameItem gameItem = new GameItem(mesh);
        gameItem.setScale(0.5f);
        gameItem.setPosition(0, 0, -2);
        gameItems = new GameItem[]{gameItem};
        */

        //Mesh mesh = new Mesh(p,t,p,i);


        Mesh mesh = MyOBJLoader.loadMesh("/models/shere.obj");
        Texture texture = new Texture("/textures/pyramid.png");
        mesh.setTexture(texture);
        GameItem gameItem = new GameItem(mesh);
        gameItem.setScale(0.5f);
        gameItem.setPosition(-1, -1, -2);


        /*
        Mesh mesh2 = PlaneGenerator.generate(100, 100, 2000, 2000);
        mesh2.setColour(new Vector3f(1.0f,0.0f,1.0f));
        gameItem2 = new GameItem(mesh2);
        gameItem2.setPosition(-3, -4, -8);
*/
/*
        Mesh mesh1 = MyOBJLoader.loadMesh("/models/shere.obj");
        Texture textur1 = new Texture("/textures/Untitled.png");
        mesh1.setTexture(textur1);
        GameItem gameItem1 = new GameItem(mesh1);
        gameItem.setScale(0.5f);
        gameItem.setPosition(0, 0, -1);

        System.out.println(mesh1.getVaoId());
*/
        gameItems = new GameItem[]{gameItem};
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);

        if (window.isKeyPressed(GLFW_KEY_UP)) {
            gameItem2.setPosition(gameItem2.getPosition().x,gameItem2.getPosition().y,gameItem2.getPosition().z-1f);
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            gameItem2.setPosition(gameItem2.getPosition().x,gameItem2.getPosition().y,gameItem2.getPosition().z+1f);
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            gameItem2.setPosition(gameItem2.getPosition().x-1f,gameItem2.getPosition().y,gameItem2.getPosition().z);
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            gameItem2.setPosition(gameItem2.getPosition().x+1f,gameItem2.getPosition().y,gameItem2.getPosition().z);
        }
        if (window.isKeyPressed(GLFW_KEY_O)) {
            gameItem2.setPosition(gameItem2.getPosition().x,gameItem2.getPosition().y+1f,gameItem2.getPosition().z);
        } else if (window.isKeyPressed(GLFW_KEY_L)) {
            gameItem2.setPosition(gameItem2.getPosition().x,gameItem2.getPosition().y-1f,gameItem2.getPosition().z);
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
