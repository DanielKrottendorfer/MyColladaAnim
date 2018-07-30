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
import org.lwjglb.engine.graph.World.WorldMesh;

import java.util.Arrays;

public class WorldRenderer {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.1f;

    private static final float Z_FAR = 1000.f;

    private final Transformation transformation;

    private ShaderProgram ShaderProgram;

    public WorldRenderer() {
        transformation = new Transformation();
    }

    public void init(Window window) throws Exception {

        // Create shader
        ShaderProgram = new ShaderProgram();
        ShaderProgram.createVertexShader(Utils.loadResource("/shaders/planeVertex.vs"));
        ShaderProgram.createFragmentShader(Utils.loadResource("/shaders/planeFragment.fs"));
        ShaderProgram.link();

        // Create uniforms for modelView and projection matrices and texture
        ShaderProgram.createUniform("projectionMatrix");
        ShaderProgram.createUniform("modelViewMatrix");
        // Create uniform for default colour and the flag that controls it
    }

    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, WorldMesh world) {
        clear();

        if ( window.isResized() ) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }


        ShaderProgram.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        ShaderProgram.setUniform("projectionMatrix", projectionMatrix);


        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);


        // Render each gameItem

        Mesh plane = world.getPlain();

        // Set model view matrix for this item
        ShaderProgram.setUniform("modelViewMatrix", viewMatrix);
        // Render the mesh for this game item
        plane.render();
        //System.out.println(mesh);

        ShaderProgram.unbind();

    }


    public void cleanup() {
        if (ShaderProgram != null) {
            ShaderProgram.cleanup();
        }
    }
}
