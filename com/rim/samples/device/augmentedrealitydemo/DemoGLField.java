/*
 * DemoGLField.java
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

package com.rim.samples.device.augmentedrealitydemo;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import net.rim.device.api.math.Matrix4f;
import net.rim.device.api.math.Vector3f;
import net.rim.device.api.opengles.GLField;
import net.rim.device.api.opengles.GLUtils;
import net.rim.device.api.system.AccelerometerData;
import net.rim.device.api.system.AccelerometerListener;
import net.rim.device.api.system.AccelerometerSensor;
import net.rim.device.api.system.AccelerometerSensor.Channel;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;

/**
 * A GLField implementation which renders a Cube object and changes the rotation
 * axis based on accelerometer data.
 */
class DemoGLField extends GLField implements AccelerometerListener {
    // Holds the number of radians to rotate per frame
    private static final float RADIANS_PER_FRAME = (float) Math.toRadians(2.0f);

    // Accelerometer orientation data channel
    private Channel _orientChannel;

    // Holds the screen's aspect ratio
    private final float _aspect;

    // Holds the transformations for the boxes
    private Matrix4f _transform;

    // Holds the axis of rotation
    private final Vector3f _rotationAxis = new Vector3f(0, 1, 0);

    // Holds the cube to be rendered
    private Cube _cube;

    private final int _width;
    private final int _height;

    /**
     * Creates a new DemoGLField object
     * 
     * @param width
     *            Width for the field
     * @param height
     *            Height of the field
     */
    DemoGLField(final int width, final int height) {
        super(GLField.VERSION_1_1);

        _width = width;
        _height = height;
        _aspect = (float) width / (float) height;

        if (AccelerometerSensor.isSupported()) {
            // Initialize Accelerometer data channel and listener
            _orientChannel =
                    AccelerometerSensor.openOrientationDataChannel(Application
                            .getApplication());
            _orientChannel.addAccelerometerListener(this);
        }
    }

    /**
     * @see net.rim.device.api.ui.Field#layout(int, int)
     */
    protected void layout(final int width, final int height) {
        setExtent(_width, _height);
    }

    /**
     * @see net.rim.device.api.opengles.GLField#initialize(GL)
     */
    public void initialize(final GL renderer) {
        final GL11 gl = (GL11) renderer;

        _transform = new Matrix4f();
        _transform.translate(0, 0, -4.0f);
        _transform.scale(1.5f);

        // Create the cube for "instanced" rendering
        _cube = new Cube(gl);

        gl.glEnable(GL10.GL_TEXTURE_2D);

        // Load the texture bitmap
        final Bitmap bitmap = Bitmap.getBitmapResource("BlackBerry.png");
        if (bitmap != null) {
            final int[] textureIds = new int[1];
            gl.glGenTextures(1, textureIds, 0);
            final int _texture = textureIds[0];
            gl.glBindTexture(GL10.GL_TEXTURE_2D, _texture);
            GLUtils.glTexImage2D(gl, 0, GL10.GL_RGB,
                    GL10.GL_UNSIGNED_SHORT_5_6_5, bitmap, null);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                    GL10.GL_LINEAR);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                    GL10.GL_LINEAR);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                    GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                    GL10.GL_CLAMP_TO_EDGE);
        }

        // Setup GL state
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        gl.glDisable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);

        initializeLights(gl);
    }

    /**
     * Defines the lighting for the cube
     * 
     * @param gl
     *            The GL11 object used for drawing
     */
    private void initializeLights(final GL11 gl) {
        // Set the ambient color for the entire scene
        final float[] ambientColor = { 0.2f, 0.2f, 0.2f, 1.0f };
        gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, ambientColor, 0);

        // Set up the position and color for light 0
        final float[] position = { 5.0f, -2.0f, 20.0f, 0.0f };
        final float[] color = { 0.75f, 0.75f, 0.75f, 1.0f };
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, position, 0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, color, 0);
        gl.glEnable(GL10.GL_LIGHT0);

        gl.glEnable(GL10.GL_LIGHTING);
    }

    /**
     * @see AccelerometerListener#onData(AccelerometerData)
     */
    public void onData(final AccelerometerData accData) {
        // Adjust the rotation of the cube based on current orientation
        switch (accData.getOrientation()) {
        case AccelerometerSensor.ORIENTATION_TOP_UP:
            _rotationAxis.set(0.0f, 1.0f, 0.0f);
            break;
        case AccelerometerSensor.ORIENTATION_BOTTOM_UP:
            _rotationAxis.set(0.0f, 1.0f, 0.0f);
            break;
        case AccelerometerSensor.ORIENTATION_FRONT_UP:
            _rotationAxis.set(0.0f, 0.0f, 1.0f);
            break;
        case AccelerometerSensor.ORIENTATION_BACK_UP:
            _rotationAxis.set(0.0f, 0.0f, 1.0f);
            break;
        case AccelerometerSensor.ORIENTATION_LEFT_UP:
            _rotationAxis.set(1.0f, 0.0f, 0.0f);
            break;
        case AccelerometerSensor.ORIENTATION_RIGHT_UP:
            _rotationAxis.set(1.0f, 0.0f, 0.0f);
            break;
        }
    }

    /**
     * @see net.rim.device.api.opengles.GLField#update()
     */
    public void update() {
        _rotationAxis.normalize();
        _transform.rotate(_rotationAxis, RADIANS_PER_FRAME);
    }

    /**
     * @see net.rim.device.api.opengles.GLField#render(GL)
     */
    public void render(final GL renderer) {
        final GL11 gl = (GL11) renderer;

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Set up the perspective projection
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLUtils.gluPerspective(gl, 45.0f, _aspect, 0.15f, 1000.0f);

        // Set up the local modelview transform and render it
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        gl.glLoadMatrixf(_transform.getArray(), 0);
        _cube.enableVertexAttrib();
        _cube.enableNormalAttrib();
        _cube.enableTexcoordAttrib();
        _cube.render(gl);
    }

    /**
     * Closes the Accelerometer's data channel
     */
    public void cleanUp() {
        if (_orientChannel != null) {
            _orientChannel.close();
        }
    }
}
