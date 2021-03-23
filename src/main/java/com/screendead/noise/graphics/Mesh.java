package com.screendead.noise.graphics;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private static final int DRAW_TYPE = GL_STATIC_DRAW;

//    private static Image texture;
    private static ArrayList<Integer> vboList = new ArrayList<>();
    private final int vao, vertexCount;

    public Mesh(float[] positions, int[] indices) {
        vertexCount = indices.length;
        if (vertexCount == 0) {
            vao = -1;
            System.err.println("No indices.");
            return;
        }

        vao = glGenVertexArrays();

        update(positions, indices);
    }

    public void update(float[] positions, int[] indices) {
        FloatBuffer vertBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            vboList = new ArrayList<>();

            glBindVertexArray(vao);

            // Position VBO
            int vbo = glGenBuffers();
            vboList.add(vbo);
            vertBuffer = MemoryUtil.memAllocFloat(positions.length);
            vertBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertBuffer, DRAW_TYPE);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Index VBO
            vbo = glGenBuffers();
            vboList.add(vbo);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, DRAW_TYPE);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (vertBuffer != null) MemoryUtil.memFree(vertBuffer);
            if (indicesBuffer != null) MemoryUtil.memFree(indicesBuffer);
        }
    }

    /**
     * Render this mesh to the framebuffer
     */
    public void render() {
        // Draw the mesh
        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    /**
     * Clean the memory after removal
     */
    public void cleanup() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboList) {
            glDeleteBuffers(vboId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vao);
    }

    /**
     * @return vao The vertex array object
     */
    public int getVAO() {
        return vao;
    }

    /**
     * @return vertexCount The number of vertices in the mesh
     */
    public int getVertexCount() {
        return vertexCount;
    }

    public boolean empty() {
        return vertexCount == 0;
    }
}
