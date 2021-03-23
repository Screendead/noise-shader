package com.screendead.noise.graphics;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class Renderer {
    private Shader shader;
    private float fov = 100.0f;

    Matrix4f view = new Matrix4f(), transform = new Matrix4f();
    Mesh mesh;
    float ticks = 0;
    float pxl = 0.0f;

    /**
     * Render to the framebuffer
     */
    public void render(Camera camera) {
        // Clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Update the camera in the shader
        shader.bind();
            shader.setUniform("timestep", ticks);
            shader.setUniform("pxl", pxl);
//            shader.setUniform("view", view);
//            shader.setUniform("camera", camera.getMatrix());
        Shader.unbind();

        // Render the chunk mesh
        shader.bind();
            mesh.render();
        Shader.unbind();
    }

    /**
     * Initialise OpenGL context for use with this window
     */
    public void init() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Enable 2D texturing
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
//        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glEnable(GL_MULTISAMPLE);

        // OpenGL settings
//        glCullFace(GL_BACK);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Create texture and shader
        shader = new Shader("basic");
        shader.addUniform("timestep");
        shader.addUniform("aspect");
        shader.addUniform("pxl");
//        shader.addUniform("view");
//        shader.addUniform("transform");
//        shader.addUniform("camera");

        float size = 256;
        mesh = new Mesh(new float[] {
                -size, -size, 0,
                size, -size, 0,
                -size, size, 0,
                size, size, 0,
        }, new int[] {
                0, 1, 2,
                2, 1, 3,
        });

        int[] rgb = new int[] {
                100, 150, 256
        };

        // Set the clear color
//        glClearColor(rgb[0] / 255.0f, rgb[1] / 255.0f, rgb[2] / 255.0f, 1.0f);
        glClearColor(rgb[0], rgb[1], rgb[2], 1.0f);
    }

    /**
     * Set the OpenGL viewport transformation and update the viewMatrix
     * @param width The window width
     * @param height The window height
     */
    public void setViewport(float width, float height) {

        // Set the viewport
        glViewport(0, 0, (int) width, (int) height);

        // Set the viewMatrix
        view = new Matrix4f()
                .perspective((float) Math.toRadians(fov),
                width / height, 0.01f, Float.POSITIVE_INFINITY);

        // Update the viewMatrix in the shader
        shader.bind();
//            shader.setUniform("view", view);
            shader.setUniform("aspect", width / height);
        Shader.unbind();
    }

    /**
     * Set the FOV and update the view matrix accordingly
     * @param fov The field of view in degrees.
     */
    public void setFOV(float fov) {
        this.fov = fov;
    }

    /**
     * Set the transformation matrix for the shader
     * Rotation order is YXZ
     * @param dx X component of the translation
     * @param dy Y component of the translation
     * @param dz Z component of the translation
     * @param rx Degrees of rotation about the X axis
     * @param ry Degrees of rotation about the Y axis
     * @param rz Degrees of rotation about the Z axis
     * @param sx X component of the scale
     * @param sy Y component of the scale
     * @param sz Z component of the scale
     */
    public void setTransform(float dx, float dy, float dz, float rx, float ry, float rz, float sx, float sy, float sz) {
        transform = new Matrix4f()
                .translation(dx, dy, dz)
                .rotateYXZ((float) Math.toRadians(ry), (float) Math.toRadians(rx), (float) Math.toRadians(rz))
                .scale(sx, sy, sz);

        shader.bind();
//            shader.setUniform("transform", transform);
        Shader.unbind();
    }

    public void cleanup() {
        mesh.cleanup();
    }
}
