package org.lwjglb.game;

import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL11.*;
import org.lwjglb.engine.GameItem;
import org.lwjglb.engine.Utils;
import org.lwjglb.engine.Window;
import org.lwjglb.engine.graph.Animation.AnimatedModel;
import org.lwjglb.engine.graph.Animation.Joint;
import org.lwjglb.engine.graph.Camera;
import org.lwjglb.engine.graph.Mesh;
import org.lwjglb.engine.graph.ShaderProgram;
import org.lwjglb.engine.graph.Transformation;

import java.util.Arrays;

public class Renderer {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.1f;

    private static final float Z_FAR = 1000.f;

    private final Transformation transformation;

    private ShaderProgram StaticShaderProgram;
    private ShaderProgram DynamicShaderProgram;

    private boolean b = true;

    public Renderer() {
        transformation = new Transformation();
    }

    public void init(Window window) throws Exception {
        DynamicShaderProgram = new ShaderProgram();
        DynamicShaderProgram.createVertexShader(Utils.loadResource("/shaders/dynVertex2.vs"));
        DynamicShaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        DynamicShaderProgram.link();
        // Create uniform for default colour and the flag that controls it

        //DynamicShaderProgram.createUniform("colour");


        DynamicShaderProgram.createUniform("jointTransform");

/*
        DynamicShaderProgram.createUniform("jointTransformB");
        DynamicShaderProgram.createUniform("jointTransformM");
        DynamicShaderProgram.createUniform("jointTransformT");
*/
        DynamicShaderProgram.createUniform("texture_sampler");
        DynamicShaderProgram.createUniform("projectionMatrix");
        DynamicShaderProgram.createUniform("modelViewMatrix");
        DynamicShaderProgram.createUniform("useColour");


        // Create shader
        StaticShaderProgram = new ShaderProgram();
        StaticShaderProgram.createVertexShader(Utils.loadResource("/shaders/statVertex.vs"));
        StaticShaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        StaticShaderProgram.link();
        
        // Create uniforms for modelView and projection matrices and texture
        StaticShaderProgram.createUniform("projectionMatrix");
        StaticShaderProgram.createUniform("modelViewMatrix");
        StaticShaderProgram.createUniform("texture_sampler");
        // Create uniform for default colour and the flag that controls it
        //shaderProgram.createUniform("colour");
        StaticShaderProgram.createUniform("useColour");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, GameItem[] gameItems,float elapsedTime) {
        clear();

        if ( window.isResized() ) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }


        StaticShaderProgram.bind();
        
        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        StaticShaderProgram.setUniform("projectionMatrix", projectionMatrix);


        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);
        
        StaticShaderProgram.setUniform("texture_sampler", 0);

        // Render each gameItem
        for(GameItem gameItem : gameItems) {
            if(!gameItem.isAnimated()){
                Mesh mesh = gameItem.getMesh();
                // Set model view matrix for this item
                Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
                StaticShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                // Render the mesh for this game item
                //shaderProgram.setUniform("colour", mesh.getColour());
                StaticShaderProgram.setUniform("useColour", mesh.isTextured() ? 0 : 1);
                mesh.render();
            }
        }


        StaticShaderProgram.unbind();


        DynamicShaderProgram.bind();

        // Update projection Matrix


        DynamicShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix

        DynamicShaderProgram.setUniform("texture_sampler", 0);

        // Render each gameItem
        for(GameItem gameItem : gameItems) {
            if(gameItem.isAnimated()) {

                AnimatedModel animModel = gameItem.getAnimatedModel();

                int jointC = animModel.getCurrentPose().length;

                Matrix4f[] jointTransforms = new Matrix4f[jointC];

                //System.out.println(Arrays.toString(joints));

                for (int i = 0; i < jointC; i++) {
                    jointTransforms[i] = animModel.getCurrentPose()[i];
                    //System.out.println(jointTransforms[i]+"\n");
                }
                //System.out.println("___________________________________________");
/*
                DynamicShaderProgram.setUniform("jointTransformB", jointTransforms[0],true);
                DynamicShaderProgram.setUniform("jointTransformM", jointTransforms[1],true);
                DynamicShaderProgram.setUniform("jointTransformT", jointTransforms[2],true);
*/

                DynamicShaderProgram.setUniform("jointTransform", jointTransforms);

                Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
                DynamicShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                DynamicShaderProgram.setUniform("useColour", animModel.isTextured() ? 0 : 1);

                animModel.render();
            }
        }

        DynamicShaderProgram.unbind();

    }


    public void cleanup() {
        if (StaticShaderProgram != null) {
            StaticShaderProgram.cleanup();
        }
        if (DynamicShaderProgram != null) {
            DynamicShaderProgram.cleanup();
        }
    }
}
