package org.lwjglb.engine.graph.Animation;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import org.lwjglb.engine.graph.Mesh;
import org.lwjglb.engine.graph.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class AnimatedMesh extends Mesh {

    public float currentAnimationTime = 0;

    private boolean animated = false;

    private static final Vector3f DEFAULT_COLOUR = new Vector3f(1.0f, 1.0f, 1.0f);

    /*
    private final int vaoId;

    private final List<Integer> vboIdList;

    private final int vertexCount;
*/
    private Texture texture;

    private Vector3f colour;

    public AnimatedMesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
        super(positions, textCoords, normals, indices);
    }
}
