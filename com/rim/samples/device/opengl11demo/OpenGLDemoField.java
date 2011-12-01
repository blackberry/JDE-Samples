/*
 * OpenGLDemoField.java
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

package com.rim.samples.device.opengl11demo;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import net.rim.device.api.animation.AnimatedScalar;
import net.rim.device.api.animation.Animation;
import net.rim.device.api.animation.Animator;
import net.rim.device.api.opengles.GLField;
import net.rim.device.api.opengles.GLUtils;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;

/**
 * A GLField subclass that renders a cube using OpenGL ES 1.1
 */
class OpenGLDemoField extends GLField {
    private static final int TARGET_FRAMERATE = 60;

    private Cube _cube;

    private boolean _sizeChanged = true;

    private Animator _animator;
    private final AnimatedScalar _rotation = new AnimatedScalar(0.0f);

    /**
     * Creates a new OpenGLDemoField object
     */
    OpenGLDemoField() {
        super(GLField.VERSION_1_1);

        setTargetFrameRate(TARGET_FRAMERATE);
        initializeAnimation();
    }

    /**
     * @see net.rim.device.api.opengles.GLField#initialize(GL)
     */
    protected void initialize(final GL g) {
        final GL11 gl = (GL11) g;

        _cube = new Cube();
        _cube.init(gl);

        // Initialize OpenGL state and load all OpenGL resources
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);

        // The default light is white with direction (0, 0 ,-1)

        // Load texture
        gl.glActiveTexture(GL10.GL_TEXTURE0);
        final EncodedImage encodedImage =
                EncodedImage.getEncodedImageResource("BlackBerry.png");
        createTexture(gl, encodedImage, GL10.GL_RGB,
                GL10.GL_UNSIGNED_SHORT_5_6_5);

        checkError(gl);
    }

    /**
     * Initializes the animations
     */
    private void initializeAnimation() {
        _animator = new Animator(0);

        // Add an animation that will animate indefinitely from 0.0 to 360.0
        // over 5 seconds.
        final Animation animation =
                _animator.addAnimationFromTo(_rotation,
                        AnimatedScalar.ANIMATION_PROPERTY_SCALAR, 0.0f, 360.0f,
                        Animation.EASINGCURVE_LINEAR, 5000L);
        animation.setRepeatCount(Animation.REPEAT_COUNT_INDEFINITE);
        _animator.begin(0L);
    }

    /**
     * @see net.rim.device.api.opengles.GLField#render(GL)
     */
    protected void render(final GL g) {
        final GL11 gl = (GL11) g;

        if (_sizeChanged) {
            sizeChanged(gl, getWidth(), getHeight());
            _sizeChanged = false;
        }

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Transform the cube
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -3.5f);
        gl.glRotatef(_rotation.getFloat(), 1.0f, 1.0f, 0.0f);

        // Enable position, normal and texture coordinates
        _cube.enableVertexAttrib();
        _cube.enableTexcoordAttrib();
        _cube.enableNormalAttrib();

        // Draw the cube
        _cube.render(gl);

        checkError(gl);
    }

    /**
     * @see net.rim.device.api.ui.Field#layout(int, int)
     */
    protected void layout(final int width, final int height) {
        // Use all available width and height up to the screen's size
        setExtent(Math.min(width, Display.getWidth()), Math.min(height, Display
                .getHeight()));
        _sizeChanged = true;
    }

    /**
     * @see net.rim.device.api.ui.Field#sizeChanged(GL11 , int, int)
     */
    public void sizeChanged(final GL11 gl, final int width, final int height) {
        // Update the viewport to reflect the new size
        gl.glViewport(0, 0, width, height);

        // Set up a perspective projection
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        // Note: The below convenience method can be replaced with an
        // appropriate GL10#glFrustum call.
        GLUtils.gluPerspective(gl, 45.0f, (float) width / (float) height,
                0.15f, 100.0f);
    }

    /**
     * @see net.rim.device.api.opengles.GLField#update()
     */
    public void update() {
        // Updates the animator at the same frequency as the GLField
        _animator.update();
    }

    /**
     * Creates and binds a new texture from the given EncodedImage
     * 
     * @param gl
     *            The GL context.
     * @param encodedImage
     *            EncodedImage containing the texture data to load
     * @param format
     *            Format for the texture
     * @param type
     *            Data type for the texture
     * @return Texture name of the created texture
     */
    private static int createTexture(final GL11 gl,
            final EncodedImage encodedImage, final int format, final int type) {
        final int[] textures = new int[1];

        // Generate 1 texture
        gl.glGenTextures(1, textures, 0);
        final int texture = textures[0];

        // Bind the newly generated texture
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

        // Turn on automatic mipmap generation
        gl.glTexParameteri(GL10.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP,
                GL10.GL_TRUE);

        // Load the image data from the encodedImage into the texture
        GLUtils.glTexImage2D(gl, GL10.GL_TEXTURE_2D, 0, format, type,
                encodedImage, null);

        // Turn on tri-linear filtering
        gl.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_LINEAR_MIPMAP_LINEAR);
        gl.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);
        gl.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_REPEAT);
        gl.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_REPEAT);

        return texture;
    }

    /**
     * Throws a RuntimeException if a GL error was detected
     * 
     * @param gl
     *            The GL context
     * @throws RuntimeException
     *             If a GL error was detected
     */
    private static void checkError(final GL11 gl) {
        final int error = gl.glGetError();

        if (error != 0) {
            throw new RuntimeException("GL Error: " + error);
        }
    }
}
