/*
 * CubeRenderer.java
 *
 * Copyright © 1998-2011 Research In Motion Limited
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

package com.rim.samples.device.opengldemo;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * An implementation of the Renderer interface which can render a Cube
 */
class CubeRenderer implements Renderer {
    private FloatBuffer _cubeVertices;
    private FloatBuffer _cubeNormals;
    private FloatBuffer _cubeTexCoords;
    private int _vertexCount;

    private float _angle;
    private long _lastUpdate;

    /**
     * Default constructor
     */
    CubeRenderer() {
    }

    /**
     * Initializes all game resources that are not bound to an OpenGL context.
     */
    public void initialize() {
        // Create geometry for drawing a cube
        _cubeVertices = Cube.createVertexBuffer();
        _cubeNormals = Cube.createNormalBuffer();
        _cubeTexCoords = Cube.createTexCoordBuffer();
        _vertexCount = _cubeVertices.remaining() / 3;

        _lastUpdate = System.currentTimeMillis();
    }

    /**
     * Called when the OpenGL Context is (re)created. This method can be called
     * on startup and also after an EGL_CONTEXT_LOST error occurs. Any GL state
     * and/or data bound to the context must be set up here.
     */
    public void contextCreated(final GL10 gl) {
        // Initialize OpenGL state and load all OpenGL resources
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);

        // Load texture
        final net.rim.device.api.system.Bitmap bitmap =
                net.rim.device.api.system.Bitmap
                        .getBitmapResource("BlackBerry.png");

        final int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);

        final int textureId = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

        // Note: The below convenience method can be replaced with an
        // appropriate GL10#glTexImage2D call.
        net.rim.device.api.opengles.GLUtils.glTexImage2D(gl, 0, GL10.GL_RGB,
                GL10.GL_UNSIGNED_SHORT_5_6_5, bitmap, null);
        final int error = gl.glGetError();
        if (error != GL10.GL_NO_ERROR) {
            throw new RuntimeException("Failed to load GL texture with error "
                    + error);
        }

        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_MODULATE);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);
    }

    /**
     * Called when the size of the drawable area changes
     */
    public void sizeChanged(final GL10 gl, final int width, final int height) {
        // Update the viewport to reflect the new size
        gl.glViewport(0, 0, width, height);

        // Set up a perspective projection
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        // Note: The below convenience method can be replaced with an
        // appropriate GL10#glFrustum call.
        net.rim.device.api.opengles.GLUtils.gluPerspective(gl, 45.0f,
                (float) width / (float) height, 0.15f, 100.0f);
    }

    /**
     * Called once per frame
     */
    public void update() {
        final long currentTime = System.currentTimeMillis();
        final float t = (currentTime - _lastUpdate) / 5000.0f;

        if ((_angle += 360.0f * t) > 360.0f) {
            _angle = 360.0f - _angle;
        }

        _lastUpdate = currentTime;
    }

    /**
     * Called once per frame
     */
    public void render(final GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        // Draw the cube
        gl.glTranslatef(0, 0, -3.5f);
        gl.glRotatef(_angle, 1.0f, 1.0f, 0.0f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _cubeVertices);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, _cubeNormals);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _cubeTexCoords);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, _vertexCount);
    }
}
