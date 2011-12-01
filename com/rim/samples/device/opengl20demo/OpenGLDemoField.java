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

package com.rim.samples.device.opengl20demo;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL;

import net.rim.device.api.animation.AnimatedScalar;
import net.rim.device.api.animation.Animation;
import net.rim.device.api.animation.Animator;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.math.Matrix4f;
import net.rim.device.api.math.Vector3f;
import net.rim.device.api.opengles.GL20;
import net.rim.device.api.opengles.GLField;
import net.rim.device.api.opengles.GLUtils;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;

/**
 * A GLField subclass that renders a cube using OpenGL ES 2.0
 */
class OpenGLDemoField extends GLField {
    private static final int TARGET_FRAMERATE = 60;

    // Shader filenames
    private static final String VERTEX_SHADER = "/shaders/TextureLight.vsh";
    private static final String FRAGMENT_SHADER = "/shaders/TextureLight.fsh";

    private Cube _cube;

    private boolean _sizeChanged = true;

    // Matrix for transforming the cube
    private final Matrix4f _matrix = new Matrix4f();

    // Projection matrix
    private final Matrix4f _projection = new Matrix4f();

    // Axis to rotate the cube around
    private final Vector3f _rotationAxis = new Vector3f(1.0f, 1.0f, 0.0f);

    // Shader program
    private int _program;

    // Vertex attribute locations
    private int _positionLoc;
    private int _texCoordLoc;
    private int _normalLoc;

    // Uniform locations
    private int _matrixLoc;
    private int _lightDirectionLoc;
    private int _lightAmbientLoc;
    private int _lightDiffuseLoc;
    private int _textureLoc;

    private Animator _animator;
    private final AnimatedScalar _rotation = new AnimatedScalar(0.0f);

    /**
     * Creates a new OpenGLDemoField object
     */
    OpenGLDemoField() {
        super(GLField.VERSION_2_0);

        setTargetFrameRate(TARGET_FRAMERATE);
        initializeAnimation();
    }

    /**
     * @see net.rim.device.api.opengles.GLField#initialize(GL)
     */
    protected void initialize(final GL g) {
        final GL20 gl = (GL20) g;

        // Create geometry for drawing a cube
        _cube = new Cube();
        _cube.init(gl);

        // Initialize OpenGL state and load all OpenGL resources
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glEnable(GL20.GL_DEPTH_TEST);
        gl.glEnable(GL20.GL_CULL_FACE);

        // Load the shaders
        _program =
                createShaderProgram(gl, getSource(VERTEX_SHADER),
                        getSource(FRAGMENT_SHADER));
        gl.glUseProgram(_program);

        // Get attribute locations
        _positionLoc = gl.glGetAttribLocation(_program, "position");
        _texCoordLoc = gl.glGetAttribLocation(_program, "texCoord");
        _normalLoc = gl.glGetAttribLocation(_program, "normal");

        // Get uniform locations
        _matrixLoc = gl.glGetUniformLocation(_program, "matrix");
        _lightDirectionLoc =
                gl.glGetUniformLocation(_program, "lightDirection");
        _lightAmbientLoc = gl.glGetUniformLocation(_program, "lightAmbient");
        _lightDiffuseLoc = gl.glGetUniformLocation(_program, "lightDiffuse");
        _textureLoc = gl.glGetUniformLocation(_program, "texture");

        // Set uniform values
        gl.glUniform1i(_textureLoc, 0);

        // Light direction (normalized)
        gl.glUniform3f(_lightDirectionLoc, 0.0f, 0.0f, -1.0f);

        // Ambient light color
        gl.glUniform3f(_lightAmbientLoc, 0.2f, 0.2f, 0.2f);

        // Diffuse light color
        gl.glUniform3f(_lightDiffuseLoc, 1.0f, 1.0f, 1.0f);

        // Load texture
        gl.glActiveTexture(GL20.GL_TEXTURE0);
        final EncodedImage encodedImage =
                EncodedImage.getEncodedImageResource("BlackBerry.png");
        createTexture(gl, encodedImage, GL20.GL_RGB,
                GL20.GL_UNSIGNED_SHORT_5_6_5);

        checkError(gl);
    }

    /**
     * Initializes the animations
     */
    private void initializeAnimation() {
        _animator = new Animator(0);

        // Add an animation that will animate indefinitely
        // from 0.0 to 2.0*PI over 5 seconds.
        final float r = (float) (Math.PI * 2.0);
        final Animation animation =
                _animator.addAnimationFromTo(_rotation,
                        AnimatedScalar.ANIMATION_PROPERTY_SCALAR, 0.0f, r,
                        Animation.EASINGCURVE_LINEAR, 5000L);
        animation.setRepeatCount(Animation.REPEAT_COUNT_INDEFINITE);
        _animator.begin(0L);
    }

    /**
     * @see net.rim.device.api.opengles.GLField#render(GL)
     */
    protected void render(final GL g) {
        final GL20 gl = (GL20) g;

        if (_sizeChanged) {
            sizeChanged(gl, getWidth(), getHeight());
            _sizeChanged = false;
        }

        gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(_program);

        // Transform the cube
        _matrix.setIdentity();
        _matrix.translate(0.0f, 0.0f, -3.5f);
        _matrix.rotate(_rotationAxis, _rotation.getFloat());

        // There are no matrix modes in GL20.
        // Multiply the model-view matrix with the projection
        // matrix and pass it to the shader program.
        Matrix4f.multiply(_projection, _matrix, _matrix);
        gl.glUniformMatrix4fv(_matrixLoc, 1, false, _matrix.getArray(), 0);

        // Set the attribute location of the position, normal and texture
        // coordinates
        _cube.enableVertexAttrib(_positionLoc);
        _cube.enableTexcoordAttrib(_texCoordLoc);
        _cube.enableNormalAttrib(_normalLoc);

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
     * Called when the size of the drawable area changes
     * 
     * @param gl
     *            The GL context
     * @param width
     *            The new width
     * @param height
     *            The new height
     */
    public void sizeChanged(final GL20 gl, final int width, final int height) {
        // Update the viewport to reflect the new size
        gl.glViewport(0, 0, width, height);

        // Set up a perspective projection
        Matrix4f.createPerspective(45.0f, (float) width / (float) height,
                0.15f, 100.0f, _projection);
    }

    /**
     * @see net.rim.device.api.opengles.GLField#update()
     */
    public void update() {
        // Updates the animator at the same frequency as the GLField
        _animator.update();
    }

    /**
     * Creates a shader program from the given vertex shader and fragment shader
     * source
     * 
     * @param gl
     *            The GL context
     * @param vertexShaderSource
     *            The vertex shader source code
     * @param fragmentShaderSource
     *            The fragment shader source code
     * @return The program object id
     * @throws RuntimeException
     *             If there was an error compiling or linking the shaders
     */
    private static int createShaderProgram(final GL20 gl,
            final String vertexShaderSource, final String fragmentShaderSource) {
        final String[] infolog = new String[2];

        // Load and compile shaders
        final int vertexShader =
                GLUtils.glLoadShader(gl, GL20.GL_VERTEX_SHADER,
                        vertexShaderSource, infolog, 0);
        final int fragmentShader =
                GLUtils.glLoadShader(gl, GL20.GL_FRAGMENT_SHADER,
                        fragmentShaderSource, infolog, 1);

        // Check for shader compile errors
        if (vertexShader == 0) {
            throw new RuntimeException("Vertex shader compile error. \n"
                    + infolog[0]);
        }
        if (fragmentShader == 0) {
            throw new RuntimeException("Fragment shader compile error. \n"
                    + infolog[1]);
        }

        // Create the program object
        final int program = gl.glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Error creating shader program.");
        }

        // Attach the shader
        gl.glAttachShader(program, vertexShader);
        gl.glAttachShader(program, fragmentShader);

        // Link the program
        gl.glLinkProgram(program);

        // Check the link status
        final int[] linked = new int[1];
        gl.glGetProgramiv(program, GL20.GL_LINK_STATUS, linked, 0);
        if (linked[0] == GL20.GL_FALSE) {
            // Get the error string before deleting the program
            final String log = gl.glGetProgramInfoLog(program);
            gl.glDeleteProgram(program);
            throw new RuntimeException("Program link error. " + log);
        }

        // Delete the shader objects now that the program is linked
        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);

        return program;
    }

    /**
     * Creates and binds a new texture from the given EncodedImage
     * 
     * @param gl
     *            The GL context
     * @param encodedImage
     *            EncodedImage containing the texture data to load
     * @param format
     *            Format for the texture
     * @param type
     *            Data type for the texture
     * @return Texture name of the created texture
     */
    private static int createTexture(final GL20 gl,
            final EncodedImage encodedImage, final int format, final int type) {
        final int[] textures = new int[1];

        // Generate 1 texture
        gl.glGenTextures(1, textures, 0);
        final int texture = textures[0];

        // Bind the newly generated texture
        gl.glBindTexture(GL20.GL_TEXTURE_2D, texture);

        // Load the image data from the encodedImage into the texture
        GLUtils.glTexImage2D(gl, GL20.GL_TEXTURE_2D, 0, format, type,
                encodedImage, null);

        // Turn on automatic mipmap generation
        gl.glGenerateMipmap(GL20.GL_TEXTURE_2D);

        // Turn on tri-linear filtering
        gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER,
                GL20.GL_LINEAR_MIPMAP_LINEAR);
        gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER,
                GL20.GL_LINEAR);
        gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S,
                GL20.GL_REPEAT);
        gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T,
                GL20.GL_REPEAT);

        return texture;
    }

    /**
     * Returns the text from the given file in the given module
     * 
     * @param filename
     *            The name of the file
     * @return The text in the file as a String or a blank String if there was
     *         an error
     */
    private String getSource(final String filename) {
        try {
            return readTextFromFile(filename);
        } catch (final IOException e) {
            return "";
        }
    }

    /**
     * Returns the text from the given file in the given module
     * 
     * @param filename
     *            The name of the file
     * @return The text in the file as a String
     * @throws IOException
     *             If there was an error
     */
    private String readTextFromFile(final String filename) throws IOException {
        if (filename == null) {
            return "";
        }

        final InputStream stream = getClass().getResourceAsStream(filename);

        final byte[] bytes = IOUtilities.streamToBytes(stream);
        final String text = new String(bytes);

        stream.close();

        return text;
    }

    /**
     * Throws a RuntimeException if a GL error was detected
     * 
     * @param gl
     *            The GL context
     * @throws RuntimeException
     *             If a GL error was detected
     */
    private static void checkError(final GL20 gl) {
        final int error = gl.glGetError();

        if (error != 0) {
            throw new RuntimeException("GL Error: " + error);
        }
    }
}
