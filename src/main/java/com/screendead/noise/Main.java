package com.screendead.noise;

import com.screendead.noise.graphics.Mesh;
import com.screendead.noise.graphics.Shader;
import com.screendead.noise.graphics.Window;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
    static boolean SHOULD_LIMIT_FRAMES = true;
    static boolean FULLSCREEN = true;
    static long UPS = 60, FPS = 165;

    // The window handle
    private Window window;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        window = new Window("Noise", 1920, 1080, true, true);

        loop();
        window.destroy();
    }

    private void loop() {

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        long initialTime = System.nanoTime();
        final float timeU = 1000000000.0f / UPS;
        final float timeF = 1000000000.0f / FPS;
        float deltaU = 0, deltaF = 0;
        int frames = 0, ticks = 0, totalTicks = 0;
        long timer = System.currentTimeMillis();

        while (!glfwWindowShouldClose(window.getHandle())) {
            if (SHOULD_LIMIT_FRAMES) {
                long currentTime = System.nanoTime();
                deltaU += (currentTime - initialTime) / timeU;
                deltaF += (currentTime - initialTime) / timeF;
                initialTime = currentTime;

                if (deltaU >= 1) {
                    glfwPollEvents();
                    ticks++;
                    totalTicks++;
                    window.update();
                    deltaU--;
                }

                if (deltaF >= 1) {
                    window.render();
                    frames++;
                    deltaF--;
                }

                if (System.currentTimeMillis() - timer > 1000) {
                    System.out.printf("UPS: %s, FPS: %s%n", ticks, frames);
                    frames = 0;
                    ticks = 0;
                    timer += 1000;
                }
            } else {
                window.update();
                window.render();
            }
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}