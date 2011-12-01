/*
 * Sprite.java
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

package com.rim.samples.device.openglspritegamedemo;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import net.rim.device.api.animation.Animation;
import net.rim.device.api.math.BoundingBox;
import net.rim.device.api.math.BoundingSphere;
import net.rim.device.api.math.Bounds;
import net.rim.device.api.math.Matrix3f;
import net.rim.device.api.math.Matrix4f;
import net.rim.device.api.math.Transform3D;
import net.rim.device.api.math.Vector3f;
import net.rim.device.api.opengles.GL20;
import net.rim.device.api.opengles.GLUtils;
import net.rim.device.api.system.Bitmap;

/**
 * Represents a basic model for the sample application (with a 3D textured quad
 * mesh, local transformation and bounds).
 */
public class Sprite {
    /** The default vertex positions for a model (a textured quad) */
    public static final float[] VERTICES = { -1.0f, -1.0f, 0.0f, +1.0f, -1.0f,
            0.0f, +1.0f, +1.0f, 0.0f, -1.0f, +1.0f, 0.0f };

    /** The default vertex normals for a model (a textured quad) */
    public static final float[] NORMALS = { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f };

    /** The default vertex texture coordinates for a model (a textured quad) */
    public static final float[] TEXCOORDS = { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 0.0f };

    /** The default indices for a model (a textured quad) */
    public static final byte[] INDICES = { 0, 1, 2, 2, 3, 0 };

    /** The offset of the vertex positions within the vertex data */
    protected static final int VERTICES_OFFSET = 0;

    /** The offset of the vertex normals within the vertex data */
    protected static final int NORMALS_OFFSET = VERTICES.length * 4;

    /** The offset of the vertex texture coordinates within the vertex data */
    protected static final int TEXCOORDS_OFFSET =
            (VERTICES.length + NORMALS.length) * 4;

    /**
     * Holds all of the OpenGL texture handles (used so that textures are only
     * loaded once).
     */
    private static Hashtable _textureHandles = new Hashtable();

    /** Holds the path to the texture used to skin this model */
    private final String _textureString;

    /** Holds whether the model is rendered using a batch */
    protected boolean _isBatchedRender = false;

    /** Holds the model's batch */
    protected Batch _batch;

    /** Holds the model's vertex texture coordinates */
    protected float[] _texcoords;

    /**
     * Holds handles to the vertex (_buffers[0]) and index (_buffers[1])
     * buffers.
     */
    protected int[] _buffers;

    /** Holds the handle to the model's texture */
    protected int _texture;

    /** Holds the model's local transformation */
    protected Transform3D _transform;

    /** Holds the model's original bounds */
    protected Bounds _originalBounds;

    /** Holds the model's current bounds */
    protected Bounds _bounds;

    /** Holds the shader program used to render */
    private static int _program;

    /** Holds the location of the modelview matrix uniform variable */
    private static int _mLoc;

    /** Holds the location of the projection matrix uniform variable */
    private static int _pLoc;

    /** Holds the location of the normal matrix uniform variable */
    private static int _nLoc;

    /** Holds the location of the texture uniform variable */
    private static int _tLoc;

    /** Holds all the animations targeting this sprite */
    private final Vector _animations;

    /**
     * Holds the inverse transpose of the modelview matrix (used to calculate
     * the matrix used to transform vertex normals).
     */
    private static Matrix4f _mti;

    /** Holds the matrix used to transform vertex normals. */
    private static Matrix3f _normalMatrix;

    /** Holds the translation vector representing the sprite's initial position */
    Vector3f _initialPosition;

    /**
     * Constructs a new Sprite object
     * 
     * @param texture
     *            The filename of the mesh's texture
     */
    public Sprite(final String texture) {
        this(TEXCOORDS, texture, null, new Vector3f(0.0f, 0.0f, 0.0f));
    }

    /**
     * Creates a new Sprite object
     * 
     * @param texture
     *            The filename of the mesh's texture
     * @param position
     *            The Vector3f holding the position of the Sprite
     */
    public Sprite(final String texture, final Vector3f position) {
        this(TEXCOORDS, texture, null, position);
    }

    /**
     * Creates a new Sprite object
     * 
     * @param texture
     *            The filename of the mesh's texture
     * @param bounds
     *            The mesh's bounds
     */
    public Sprite(final String texture, final Bounds bounds,
            final Vector3f position) {
        this(TEXCOORDS, texture, bounds, position);
    }

    /**
     * Creates a new Sprite object
     * 
     * @param texcoords
     *            The mesh's texture coordinates
     * @param texture
     *            The filename of the mesh's texture
     * @param bounds
     *            The mesh's bounds
     * @param position
     *            The Vector3f holding the position of the Sprite
     */
    public Sprite(final float[] texcoords, final String texture,
            final Bounds bounds, final Vector3f position) {
        // Create a new BoundingBox if the Sprite doesn't have one yet
        if (bounds == null) {
            final Vector3f[] vertices = new Vector3f[VERTICES.length / 3];

            for (int i = 0; i < vertices.length; i++) {
                vertices[i] =
                        new Vector3f(VERTICES[i * 3], VERTICES[i * 3 + 1],
                                VERTICES[i * 3 + 2]);
            }

            _originalBounds = new BoundingBox(vertices);
            _bounds = new BoundingBox((BoundingBox) _originalBounds);
        } else {
            // Determine if the sprite's bounds should be a box or a sphere
            if (bounds instanceof BoundingBox) {
                _originalBounds = new BoundingBox((BoundingBox) bounds);
                _bounds = new BoundingBox((BoundingBox) bounds);
            } else {
                _originalBounds = new BoundingSphere((BoundingSphere) bounds);
                _bounds = new BoundingSphere((BoundingSphere) bounds);
            }
        }

        _texcoords = texcoords;
        _textureString = texture;

        _animations = new Vector();
        _mti = new Matrix4f();
        _normalMatrix = new Matrix3f();
        _transform = new Transform3D();

        // Set the initial position of the sprite
        setInitialPosition(position);
    }

    /**
     * Initializes the sprite using OpenGL v1.1
     * 
     * @param gl
     *            The OpenGL v1.1 object
     */
    public void initialize(final GL11 gl) {
        _texture = getTexture(gl);
        startAnimations();
    }

    /**
     * Initializes the sprite using OpenGL v2.0
     * 
     * @param gl
     *            The OpenGL v2.0 object
     */
    public void initialize(final GL20 gl) {
        _texture = getTexture(gl);
        startAnimations();
    }

    /**
     * Sets the initial position of the sprite
     * 
     * @param position
     *            The Vector3f holding the position of the Sprite
     */
    public void setInitialPosition(final Vector3f position) {
        _initialPosition = position;
        _transform.setTranslation(position);
    }

    /**
     * Gets this model's updated bounds
     * 
     * @return The current bounds
     */
    public Bounds getBounds() {
        _transform.transformBounds(_originalBounds, _bounds);

        return _bounds;
    }

    /**
     * Sets whether this model is batched or not
     * 
     * @param gl
     *            The reference to the GL
     * @param isBatched
     *            Whether the model should be batched or not
     */
    public void setIsBatched(final GL11 gl, final boolean isBatched) {
        _isBatchedRender = isBatched;
        if (_isBatchedRender && _batch == null) {
            // Pre-transform the vertices before adding them to the batch
            final Vector3f v = new Vector3f();
            final float[] vertexFloatData = new float[VERTICES.length];
            for (int i = 0; i < VERTICES.length / 3; i++) {
                v.set(VERTICES[i * 3], VERTICES[i * 3 + 1], VERTICES[i * 3 + 2]);
                _transform.transformPoint(v);
                vertexFloatData[i * 3] = v.x;
                vertexFloatData[i * 3 + 1] = v.y;
                vertexFloatData[i * 3 + 2] = v.z;
            }
            _batch =
                    Batch.getBatch(gl, vertexFloatData, NORMALS, _texcoords,
                            INDICES, _texture);
        }
    }

    /**
     * Sets whether this model is batched or not
     * 
     * @param gl
     *            The reference to the GL
     * @param isBatched
     *            Whether the model should be batched or not
     */
    public void setIsBatched(final GL20 gl, final boolean isBatched) {
        _isBatchedRender = isBatched;
        if (_isBatchedRender && _batch == null) {
            // Pre-transform the vertices before adding them to the batch
            final Vector3f v = new Vector3f();
            final float[] vertexFloatData = new float[VERTICES.length];
            for (int i = 0; i < VERTICES.length / 3; i++) {
                v.set(VERTICES[i * 3], VERTICES[i * 3 + 1], VERTICES[i * 3 + 2]);
                _transform.transformPoint(v);
                vertexFloatData[i * 3] = v.x;
                vertexFloatData[i * 3 + 1] = v.y;
                vertexFloatData[i * 3 + 2] = v.z;
            }
            _batch =
                    Batch.getBatch(gl, vertexFloatData, NORMALS, _texcoords,
                            INDICES, _texture);
        }
    }

    /**
     * Renders this model with OpenGL v1.1
     * 
     * @param gl
     *            The reference to the OpenGL v1.1 object used for rendering
     */
    public void render(final GL11 gl) {
        if (_isBatchedRender) {
            _batch.render(gl);
        } else {
            if (_buffers == null) {
                final int vertexDataSize =
                        (VERTICES.length + NORMALS.length + _texcoords.length) * 4;

                // Create the buffer for the vertex data.
                FloatBuffer vertexData;
                vertexData =
                        ByteBuffer.allocateDirect(vertexDataSize)
                                .asFloatBuffer();
                vertexData.put(VERTICES);
                vertexData.put(NORMALS);
                vertexData.put(_texcoords);
                vertexData.rewind();

                ByteBuffer indices;
                indices = ByteBuffer.allocateDirect(INDICES.length);
                indices.put(INDICES);
                indices.rewind();

                // Create the vertex and index buffers for the model's geometry
                _buffers = new int[2];
                gl.glGenBuffers(2, _buffers, 0);

                gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, _buffers[0]);
                gl.glBufferData(GL11.GL_ARRAY_BUFFER, vertexDataSize,
                        vertexData, GL11.GL_STATIC_DRAW);

                gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, _buffers[1]);
                gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, INDICES.length,
                        indices, GL11.GL_STATIC_DRAW);
            }

            // Save the old model view matrix
            gl.glPushMatrix();

            // Apply the local transformation
            gl.glMultMatrixf(_transform.getMatrix().getArray(), 0);

            // Bind the vertex and index buffers
            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, _buffers[0]);
            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, _buffers[1]);

            // Enable the arrays for the VBO
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            // Set the pointers for the vertex buffer data.
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, VERTICES_OFFSET);
            gl.glNormalPointer(GL10.GL_FLOAT, 0, NORMALS_OFFSET);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, TEXCOORDS_OFFSET);

            // Bind the mesh's texture
            gl.glBindTexture(GL10.GL_TEXTURE_2D, _texture);

            // Draw the mesh's geometry
            gl.glDrawElements(GL10.GL_TRIANGLES, INDICES.length,
                    GL10.GL_UNSIGNED_BYTE, 0);

            // Restore the old model view matrix
            gl.glPopMatrix();
        }
    }

    /**
     * Renders this model using OpenGL v2.0
     * 
     * @param gl
     *            The reference to the OpenGL v2.0 object used for rendering
     */
    public void render(final GL20 gl, final Matrix4f modelview) {
        if (_isBatchedRender) {
            _batch.render(gl, modelview);
        } else {
            if (_buffers == null) {
                final int vertexDataSize =
                        (VERTICES.length + NORMALS.length + _texcoords.length) * 4;

                // Create the buffer for the vertex data
                FloatBuffer vertexData;
                vertexData =
                        ByteBuffer.allocateDirect(vertexDataSize)
                                .asFloatBuffer();
                vertexData.put(VERTICES);
                vertexData.put(NORMALS);
                vertexData.put(_texcoords);
                vertexData.rewind();

                ByteBuffer indices;
                indices = ByteBuffer.allocateDirect(INDICES.length);
                indices.put(INDICES);
                indices.rewind();

                // Create the vertex and index buffers for the model's geometry
                _buffers = new int[2];
                gl.glGenBuffers(2, _buffers, 0);

                gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, _buffers[0]);
                gl.glBufferData(GL20.GL_ARRAY_BUFFER, vertexDataSize,
                        vertexData, GL20.GL_STATIC_DRAW);

                gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, _buffers[1]);
                gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, INDICES.length,
                        indices, GL20.GL_STATIC_DRAW);
            }

            // Save the old model view matrix
            final Matrix4f m = new Matrix4f(modelview);

            // Apply the local transformation
            m.multiply(_transform.getMatrix());

            // Bind the vertex and index buffers
            gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, _buffers[0]);
            gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, _buffers[1]);

            // Set the pointers for the vertex buffer data
            gl.glVertexAttribPointer(0, 3, GL20.GL_FLOAT, false, 0,
                    VERTICES_OFFSET);
            gl.glVertexAttribPointer(1, 3, GL20.GL_FLOAT, false, 0,
                    NORMALS_OFFSET);
            gl.glVertexAttribPointer(2, 2, GL20.GL_FLOAT, false, 0,
                    TEXCOORDS_OFFSET);

            // Enable the generic vertex attribute arrays
            gl.glEnableVertexAttribArray(0);
            gl.glEnableVertexAttribArray(1);
            gl.glEnableVertexAttribArray(2);

            // Load the modelview and normal matrices used for rendering
            gl.glUniformMatrix4fv(_mLoc, 1, false, m.getArray(), 0);
            gl.glUniformMatrix3fv(_nLoc, 1, false, getNormalMatrix(m), 0);

            // Set up the mesh's texture for rendering
            gl.glBindTexture(GL20.GL_TEXTURE_2D, _texture);
            gl.glUniform1i(_tLoc, 0);

            // Draw the mesh's geometry
            gl.glDrawElements(GL20.GL_TRIANGLES, INDICES.length,
                    GL20.GL_UNSIGNED_BYTE, 0);
        }
    }

    /**
     * Resets the model to its initial state for starting the level
     */
    public void reset() {
        _transform.setTranslation(_initialPosition);
    }

    /**
     * Adds an animation to the sprite
     * 
     * @param animation
     *            The animation to add to the sprite
     */
    public void addAnimation(final Animation animation) {
        _animations.addElement(animation);
    }

    /**
     * Starts all the animations for the sprite
     */
    public void startAnimations() {
        final int count = _animations.size();

        // Go through all the sprite's animations and start them
        for (int i = 0; i < count; i++) {
            ((Animation) _animations.elementAt(i)).begin(0);
        }
    }

    /**
     * Gets the OpenGL v1.1 handle for the given texture
     * 
     * @param gl
     *            The reference to the OpenGL v1.1
     */
    private int getTexture(final GL11 gl) {
        // Load the texture if it has not already been loaded
        if (!_textureHandles.containsKey(_textureString)) {
            final Bitmap bitmap = Bitmap.getBitmapResource(_textureString);
            final int[] textures = new int[1];
            gl.glGenTextures(1, textures, 0);
            final Integer textureHandle = new Integer(textures[0]);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureHandle.intValue());

            GLUtils.glTexImage2D(gl, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
                    bitmap, null);
            final int error = gl.glGetError();
            if (error != GL10.GL_NO_ERROR) {
                throw new RuntimeException(
                        "Failed to load GL texture with error " + error + ".");
            }

            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                    GL10.GL_LINEAR);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                    GL10.GL_LINEAR);

            _textureHandles.put(_textureString, textureHandle);

            return textureHandle.intValue();
        } else {
            final Integer textureHandle =
                    (Integer) _textureHandles.get(_textureString);

            return textureHandle.intValue();
        }
    }

    /**
     * Gets the OpenGL v2.0 handle for the given texture
     * 
     * @param gl
     *            The reference to the OpenGL v2.0
     */
    private int getTexture(final GL20 gl) {
        // Load the texture if it has not already been loaded
        if (!_textureHandles.containsKey(_textureString)) {
            final Bitmap bitmap = Bitmap.getBitmapResource(_textureString);
            final int[] textures = new int[1];
            gl.glGenTextures(1, textures, 0);
            final Integer textureHandle = new Integer(textures[0]);
            gl.glBindTexture(GL20.GL_TEXTURE_2D, textureHandle.intValue());

            GLUtils.glTexImage2D(gl, 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE,
                    bitmap, null);
            final int error = gl.glGetError();
            if (error != GL20.GL_NO_ERROR) {
                throw new RuntimeException(
                        "Failed to load GL texture with error " + error + ".");
            }

            gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER,
                    GL20.GL_LINEAR);
            gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER,
                    GL20.GL_LINEAR);

            _textureHandles.put(_textureString, textureHandle);

            return textureHandle.intValue();
        } else {
            final Integer textureHandle =
                    (Integer) _textureHandles.get(_textureString);

            return textureHandle.intValue();
        }
    }

    /**
     * Loads the shader used to render models in the application
     * 
     * @param gl
     *            The OpenGL v2.0 used for rendering
     */
    static void loadShader(final GL20 gl) {
        final StringBuffer vShaderStr = new StringBuffer();
        vShaderStr.append("uniform mat3 normalMatrix;\n");
        vShaderStr.append("uniform mat4 modelview;\n");
        vShaderStr.append("uniform mat4 projection;\n");
        vShaderStr.append("attribute vec4 position;\n");
        vShaderStr.append("attribute vec3 normal;\n");
        vShaderStr.append("attribute vec2 texcoord;\n");
        vShaderStr.append("varying vec3 lightDir, vnormal;\n");
        vShaderStr.append("varying vec2 vtexcoord;\n");
        vShaderStr.append("void main()\n");
        vShaderStr.append("{\n");
        vShaderStr.append("    lightDir = normalize(vec3(1.0, 0.0, 1.0));\n");
        vShaderStr.append("    vnormal = normalMatrix * normal;\n");
        vShaderStr
                .append("    gl_Position = projection * modelview * position;\n");
        vShaderStr.append("    vtexcoord = texcoord;\n");
        vShaderStr.append("}\n");

        final StringBuffer fShaderStr = new StringBuffer();
        fShaderStr.append("precision mediump float;\n");
        fShaderStr.append("varying vec3 lightDir, vnormal;\n");
        fShaderStr.append("varying vec2 vtexcoord;\n");
        fShaderStr.append("uniform sampler2D texture;\n");
        fShaderStr.append("void main()\n");
        fShaderStr.append("{\n");
        fShaderStr.append("    vec4 color = texture2D(texture, vtexcoord);\n");
        fShaderStr.append("    vec3 n = normalize(vnormal);\n");
        fShaderStr.append("    gl_FragColor = color;\n");
        fShaderStr.append("}\n");

        // Load the vertex and fragment shaders
        final String[] infolog = new String[2];
        final int vertexShader =
                GLUtils.glLoadShader(gl, GL20.GL_VERTEX_SHADER, vShaderStr
                        .toString(), infolog, 0);
        final int fragmentShader =
                GLUtils.glLoadShader(gl, GL20.GL_FRAGMENT_SHADER, fShaderStr
                        .toString(), infolog, 1);

        if (vertexShader == 0) {
            throw new RuntimeException("Vertex shader compile error. "
                    + infolog[0]);
        }
        if (fragmentShader == 0) {
            throw new RuntimeException("Fragment shader compile error. "
                    + infolog[1]);
        }

        // Create the program object
        _program = gl.glCreateProgram();
        if (_program == 0) {
            return;
        }

        // Attach the shaders
        gl.glAttachShader(_program, vertexShader);
        gl.glAttachShader(_program, fragmentShader);

        // Bind attributes
        gl.glBindAttribLocation(_program, 0, "position");
        gl.glBindAttribLocation(_program, 1, "normal");
        gl.glBindAttribLocation(_program, 2, "texcoord");

        // Link the program
        gl.glLinkProgram(_program);

        // Check the link status
        final int[] linked = new int[1];
        gl.glGetProgramiv(_program, GL20.GL_LINK_STATUS, linked, 0);
        if (linked[0] == GL20.GL_FALSE) {
            final String log = gl.glGetProgramInfoLog(_program);
            gl.glDeleteProgram(_program);
            throw new RuntimeException("Shader link error. " + log);
        }

        _mLoc = gl.glGetUniformLocation(_program, "modelview");
        _pLoc = gl.glGetUniformLocation(_program, "projection");
        _nLoc = gl.glGetUniformLocation(_program, "normalMatrix");
        _tLoc = gl.glGetUniformLocation(_program, "texture");
    }

    /**
     * Retrieves the shader program handle
     * 
     * @return The shader program handle
     */
    public static int getProgram() {
        return _program;
    }

    /**
     * Retrieves the location of the projection matrix uniform variable
     * 
     * @return The location of the projection matrix uniform
     */
    public static int getProjectionMatrixLocation() {
        return _pLoc;
    }

    /**
     * Calculates the normal matrix from the given modelview matrix and returns
     * the normal matrix as a float array.
     * 
     * @param modelview
     *            The modelview matrix
     * @return A float array representing the 3x3 matrix used to transform
     *         vertex normals
     */
    private static float[] getNormalMatrix(final Matrix4f modelview) {
        _mti.set(modelview);
        _mti.invert();
        _mti.transpose();
        _normalMatrix.set(_mti.get(0, 0), _mti.get(0, 1), _mti.get(0, 2), _mti
                .get(1, 0), _mti.get(1, 1), _mti.get(1, 2), _mti.get(2, 0),
                _mti.get(2, 1), _mti.get(2, 2));

        return _normalMatrix.getArray();
    }

    /**
     * Represents a render batch that uses a single texture
     * 
     */
    public static final class Batch {
        /**
         * Encapsulates a float array
         */
        private static final class FloatArray {
            public float[] array;

            public FloatArray(final float[] array) {
                this.array = new float[array.length];
                System.arraycopy(array, 0, this.array, 0, array.length);
            }
        }

        /**
         * Encapsulates a byte array
         */
        private static final class ByteArray {
            public ByteArray(final byte[] array) {
                this.array = new byte[array.length];
                System.arraycopy(array, 0, this.array, 0, array.length);
            }

            public byte[] array;
        }

        /** Holds the render batches */
        private static Hashtable _batches = new Hashtable();

        /** Holds the vertex positions */
        private final Vector _positions = new Vector();

        /** Holds the vertex normals */
        private final Vector _normals = new Vector();

        /** Holds the vertex texture coordinates */
        private final Vector _texcoords = new Vector();

        /** Holds the indices into the vertex data */
        private final Vector _indices = new Vector();

        /** Holds the actual vertex data used by the GL */
        private FloatBuffer _vertexData = null;

        /** Holds the actual index data used by the GL */
        private ByteBuffer _indexData = null;

        /**
         * Holds the offset into the vertex data where new data can be appended
         */
        private int _vertexDataOffset;

        /** Holds the offset into the index data where new data can be appended */
        private int _indexDataOffset;

        /** The offset of the vertex positions within the vertex data */
        private int _positionsOffset;

        /** The offset of the vertex normals within the vertex data */
        private int _normalsOffset;

        /** The offset of the texture coordinates within the vertex data */
        private int _texcoordsOffset;

        /**
         * Holds handles to the vertex (_buffers[0]) and index (_buffers[1])
         * buffers.
         */
        private final int _buffers[] = new int[2];

        /** Holds the OpenGL handle to the texture object */
        private int _texture = 0;

        /** Holds the number of meshes held by this batch */
        private int _meshCount = 0;

        /**
         * Holds the index of the current mesh being "rendered" by the batch
         * (this is really just the number of meshes that have called render for
         * a particular frame.
         */
        private int _meshRenderIndex = 0;

        /**
         * Constructs a render batch that using the given texture
         * 
         * @param gl
         *            The reference to the OpenGL v1.1 object
         * @param texture
         *            The texture's OpenGL handle
         */
        public Batch(final GL11 gl, final int texture) {
            // Store the texture handle
            _texture = texture;

            // Create the vertex and index buffers
            gl.glGenBuffers(2, _buffers, 0);
            _vertexDataOffset = 0;
            _indexDataOffset = 0;
            _positionsOffset = 0;
            _normalsOffset = 0;
            _texcoordsOffset = 0;
        }

        /**
         * Constructs a render batch that using the given texture
         * 
         * @param gl
         *            The reference to the OpenGL v2.0 object
         * @param texture
         *            The texture's OpenGL handle
         */
        public Batch(final GL20 gl, final int texture) {
            // Store the texture handle
            _texture = texture;

            // Create the vertex and index buffers
            gl.glGenBuffers(2, _buffers, 0);
            _vertexDataOffset = 0;
            _indexDataOffset = 0;
            _positionsOffset = 0;
            _normalsOffset = 0;
            _texcoordsOffset = 0;
        }

        /**
         * Clears the batch
         * 
         * @param gl
         *            The reference to the OpenGL v1.1 object
         */
        public static void clearBatch(final GL11 gl) {
            final Enumeration e = _batches.elements();
            int[] buffers;

            // Go through everything in the batch and remove it
            while (e.hasMoreElements()) {
                buffers = ((Batch[]) e.nextElement())[0]._buffers;
                gl.glDeleteBuffers(2, buffers, 0);
            }

            _batches.clear();
        }

        /**
         * Clears the batch
         * 
         * @param gl
         *            The reference to the OpenGL v2.0 object
         */
        public static void clearBatch(final GL20 gl) {
            final Enumeration e = _batches.elements();
            int[] buffers;

            // Go through everything in the batch and remove it
            while (e.hasMoreElements()) {
                buffers = ((Batch[]) e.nextElement())[0]._buffers;
                gl.glDeleteBuffers(2, buffers, 0);
            }

            _batches.clear();
        }

        /**
         * Renders the batch using OpenGL v1.1
         * 
         * @param gl
         *            The reference to the OpenGl v1.1 object
         */
        public void render(final GL11 gl) {
            // Check if all the batched meshes have called render()
            // (meaning we should actually draw them all now).
            if (_meshRenderIndex == _meshCount - 1) {
                if (_vertexData == null) {
                    // Update the direct byte buffers
                    _vertexData =
                            ByteBuffer.allocateDirect(_vertexDataOffset)
                                    .asFloatBuffer();

                    for (int i = 0; i < _positions.size(); i++) {
                        _vertexData
                                .put(((FloatArray) _positions.elementAt(i)).array);
                    }

                    for (int i = 0; i < _normals.size(); i++) {
                        _vertexData
                                .put(((FloatArray) _normals.elementAt(i)).array);
                    }

                    for (int i = 0; i < _texcoords.size(); i++) {
                        _vertexData
                                .put(((FloatArray) _texcoords.elementAt(i)).array);
                    }

                    _vertexData.rewind();

                    _indexData = ByteBuffer.allocateDirect(_indexDataOffset);

                    for (int i = 0; i < _indices.size(); i++) {
                        _indexData
                                .put(((ByteArray) _indices.elementAt(i)).array);
                    }
                    _indexData.rewind();

                    // Update the buffers in the GL
                    gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, _buffers[0]);
                    gl.glBufferData(GL11.GL_ARRAY_BUFFER, _vertexDataOffset,
                            _vertexData, GL11.GL_STATIC_DRAW);

                    gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, _buffers[1]);
                    gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER,
                            _indexDataOffset, _indexData, GL11.GL_STATIC_DRAW);
                }

                // Save the old model view matrix and clear the current matrix
                gl.glPushMatrix();

                // Bind the vertex and index buffers
                gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, _buffers[0]);
                gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, _buffers[1]);

                // Enable the arrays for the VBO
                gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
                gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
                gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

                // Set the pointers for the vertex buffer data
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _positionsOffset);
                gl.glNormalPointer(GL10.GL_FLOAT, 0, _normalsOffset);
                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _texcoordsOffset);

                // Bind the mesh's texture
                gl.glBindTexture(GL10.GL_TEXTURE_2D, _texture);

                // Draw the batch's geometry
                gl.glDrawElements(GL10.GL_TRIANGLES, _indexDataOffset,
                        GL10.GL_UNSIGNED_BYTE, 0);

                // Restore the old model view matrix
                gl.glPopMatrix();

                // Reset the render index
                _meshRenderIndex = 0;
            } else {
                // Update the render index
                _meshRenderIndex++;
            }
        }

        /**
         * Renders the batch using OpenGL v2.0
         * 
         * @param gl
         *            The reference to the OpenGL v2.0 object
         * @param modelview
         *            The modelview matrix
         */
        public void render(final GL20 gl, final Matrix4f modelview) {
            // Check if all the batched meshes have called render()
            // (meaning we should actually draw them all now).
            if (_meshRenderIndex == _meshCount - 1) {
                if (_vertexData == null) {
                    // Update the direct byte buffers
                    _vertexData =
                            ByteBuffer.allocateDirect(_vertexDataOffset)
                                    .asFloatBuffer();

                    for (int i = 0; i < _positions.size(); i++) {
                        _vertexData
                                .put(((FloatArray) _positions.elementAt(i)).array);
                    }

                    for (int i = 0; i < _normals.size(); i++) {
                        _vertexData
                                .put(((FloatArray) _normals.elementAt(i)).array);
                    }

                    for (int i = 0; i < _texcoords.size(); i++) {
                        _vertexData
                                .put(((FloatArray) _texcoords.elementAt(i)).array);
                    }

                    _vertexData.rewind();

                    _indexData = ByteBuffer.allocateDirect(_indexDataOffset);

                    for (int i = 0; i < _indices.size(); i++) {
                        _indexData
                                .put(((ByteArray) _indices.elementAt(i)).array);
                    }

                    _indexData.rewind();

                    // Update the buffers in the GL
                    gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, _buffers[0]);
                    gl.glBufferData(GL20.GL_ARRAY_BUFFER, _vertexDataOffset,
                            _vertexData, GL20.GL_STATIC_DRAW);

                    gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, _buffers[1]);
                    gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER,
                            _indexDataOffset, _indexData, GL20.GL_STATIC_DRAW);
                }

                // Bind the vertex and index buffers
                gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, _buffers[0]);
                gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, _buffers[1]);

                // Set the pointers for the vertex buffer data
                gl.glVertexAttribPointer(0, 3, GL20.GL_FLOAT, false, 0,
                        _positionsOffset);
                gl.glVertexAttribPointer(1, 3, GL20.GL_FLOAT, false, 0,
                        _normalsOffset);
                gl.glVertexAttribPointer(2, 2, GL20.GL_FLOAT, false, 0,
                        _texcoordsOffset);

                // Enable the generic vertex attribute arrays
                gl.glEnableVertexAttribArray(0);
                gl.glEnableVertexAttribArray(1);
                gl.glEnableVertexAttribArray(2);

                // Load the modelview and normal matrices used for rendering
                gl.glUniformMatrix4fv(_mLoc, 1, false, modelview.getArray(), 0);
                gl.glUniformMatrix3fv(_nLoc, 1, false, Sprite
                        .getNormalMatrix(modelview), 0);

                // Set up the mesh's texture for rendering
                gl.glBindTexture(GL20.GL_TEXTURE_2D, _texture);
                gl.glUniform1i(_tLoc, 0);

                // Draw the batch's geometry
                gl.glDrawElements(GL20.GL_TRIANGLES, _indexDataOffset,
                        GL20.GL_UNSIGNED_BYTE, 0);

                // Reset the render index
                _meshRenderIndex = 0;
            } else {
                // Update the render index
                _meshRenderIndex++;
            }
        }

        /**
         * Gets the first available batch that corresponds to the given texture
         * and can hold the given mesh data, adding the mesh data to the batch
         * before returning it.
         * 
         * @param gl
         *            The reference to the OpenGL v1.1 object
         * @param vertices
         *            The mesh's vertices
         * @param normals
         *            The mesh's vertice normals
         * @param texcoords
         *            The mesh's vertice texture coordinates
         * @param indices
         *            The mesh's indices
         * @param texture
         *            The OpenGL handle of the texture
         * @return The batch using the given texture
         */
        public static Batch getBatch(final GL11 gl, final float[] vertices,
                final float[] normals, final float[] texcoords,
                final byte[] indices, final int texture) {
            final Integer key = new Integer(texture);

            // If a render batch corresponding to the given
            // texture does not exist, create it.
            if (!_batches.containsKey(key)) {
                final Batch[] batches = new Batch[1];
                batches[0] = new Batch(gl, texture);
                batches[0].addMesh(gl, vertices, normals, texcoords, indices);
                _batches.put(key, batches);
                return batches[0];
            } else {
                // Find a batch that can hold the requested vertices
                final Batch[] batches = (Batch[]) _batches.get(key);

                for (int i = 0; i < batches.length; i++) {
                    if (!batches[i].isFull(vertices.length)) {
                        batches[i].addMesh(gl, vertices, normals, texcoords,
                                indices);
                        return batches[i];
                    }
                }

                // All the batches (for the given texture)
                // are full, so add a new batch.
                final Batch[] newBatches = new Batch[batches.length + 1];
                System.arraycopy(batches, 0, newBatches, 0, batches.length);
                newBatches[batches.length] = new Batch(gl, texture);
                newBatches[batches.length].addMesh(gl, vertices, normals,
                        texcoords, indices);
                _batches.put(key, newBatches);
                return newBatches[batches.length];
            }
        }

        /**
         * Gets the first available batch that corresponds to the given texture
         * and can hold the given mesh data, adding the mesh data to the batch
         * before returning it.
         * 
         * @param gl
         *            The reference to the OpenGL. v2.0 object
         * @param vertices
         *            The mesh's vertices
         * @param normals
         *            The mesh's vertices normals
         * @param texcoords
         *            The mesh's vertice texture coordinates
         * @param indices
         *            The mesh's indices
         * @param texture
         *            The OpenGL handle of the texture
         * @return The batch using the given texture
         */
        public static Batch getBatch(final GL20 gl, final float[] vertices,
                final float[] normals, final float[] texcoords,
                final byte[] indices, final int texture) {
            final Integer key = new Integer(texture);

            // If a render batch corresponding to the given
            // texture does not exist, create it.
            if (!_batches.containsKey(key)) {
                final Batch[] batches = new Batch[1];
                batches[0] = new Batch(gl, texture);
                batches[0].addMesh(gl, vertices, normals, texcoords, indices);
                _batches.put(key, batches);
                return batches[0];
            } else {
                // Find a batch that can hold the requested vertices
                final Batch[] batches = (Batch[]) _batches.get(key);

                for (int i = 0; i < batches.length; i++) {
                    if (!batches[i].isFull(vertices.length)) {
                        batches[i].addMesh(gl, vertices, normals, texcoords,
                                indices);
                        return batches[i];
                    }
                }

                // All the batches (for the given texture) are full, so add a
                // new batch
                final Batch[] newBatches = new Batch[batches.length + 1];
                System.arraycopy(batches, 0, newBatches, 0, batches.length);
                newBatches[batches.length] = new Batch(gl, texture);
                newBatches[batches.length].addMesh(gl, vertices, normals,
                        texcoords, indices);
                _batches.put(key, newBatches);
                return newBatches[batches.length];
            }
        }

        /**
         * Retrieves a boolean indicating whether the batch is full or not
         * 
         * @param vertexCount
         *            The number of vertices to be added to the batch
         * @return <code>true</code> if the batch is full; <code>false</code>
         *         otherwise
         */
        protected boolean isFull(final int vertexCount) {
            return _vertexDataOffset + vertexCount * 32 > 8192;
        }

        /**
         * Adds the given mesh to the batch
         * 
         * @param gl
         *            The reference to the OpenGL v1.1 object
         * @param vertices
         *            The mesh's vertices
         * @param normals
         *            The mesh's vertice normals
         * @param texcoords
         *            The mesh's vertice texture coordinates
         * @param indices
         *            The mesh's indices
         */
        private void addMesh(final GL11 gl, final float[] vertices,
                final float[] normals, final float[] texcoords,
                final byte[] indices) {
            // Add the vertex data to the batch's internal data
            _positions.addElement(new FloatArray(vertices));
            _normals.addElement(new FloatArray(normals));
            _texcoords.addElement(new FloatArray(texcoords));

            // Add the index data to the batch's internal data after offsetting
            // it correctly.
            // The offset is calculated as follows:
            // VertexDataOffset / ((3 + 3 + 2) * 4) = VertexDataOffset / 32
            // (3 floats(position) + 3 floats(normal) + 2 floats(texture
            // coordinate)) * 4 bytes per float
            final int indexOffset = _vertexDataOffset / 32;
            final ByteArray newIndices = new ByteArray(indices);
            for (int i = 0; i < newIndices.array.length; i++) {
                newIndices.array[i] += indexOffset;
            }
            _indices.addElement(newIndices);

            // Update the offsets
            _vertexDataOffset +=
                    (vertices.length + normals.length + texcoords.length) * 4;
            _indexDataOffset += indices.length;

            // Update the vertex array offsets used for drawing
            _positionsOffset = 0;
            _normalsOffset = 0;
            for (int i = 0; i < _positions.size(); i++) {
                _normalsOffset +=
                        ((FloatArray) _positions.elementAt(i)).array.length * 4;
            }
            _texcoordsOffset = _normalsOffset;
            for (int i = 0; i < _normals.size(); i++) {
                _texcoordsOffset +=
                        ((FloatArray) _normals.elementAt(i)).array.length * 4;
            }

            _meshCount++;
        }

        /**
         * Adds the given mesh to the batch.
         * 
         * @param gl
         *            The reference to the OpenGL v2.0 object
         * @param vertices
         *            The mesh's vertices
         * @param normals
         *            The mesh's vertice normals
         * @param texcoords
         *            The mesh's vertice texture coordinates
         * @param indices
         *            The mesh's indices
         */
        private void addMesh(final GL20 gl, final float[] vertices,
                final float[] normals, final float[] texcoords,
                final byte[] indices) {
            // Add the vertex data to the batch's internal data
            _positions.addElement(new FloatArray(vertices));
            _normals.addElement(new FloatArray(normals));
            _texcoords.addElement(new FloatArray(texcoords));

            // Add the index data to the batch's internal data after offsetting
            // it correctly.
            // The offset is calculated as follows:
            // VertexDataOffset / ((3 + 3 + 2) * 4) = VertexDataOffset / 32
            // (3 floats(position) + 3 floats(normal) + 2 floats(texture
            // coordinate)) * 4 bytes per float
            final int indexOffset = _vertexDataOffset / 32;
            final ByteArray newIndices = new ByteArray(indices);
            for (int i = 0; i < newIndices.array.length; i++) {
                newIndices.array[i] += indexOffset;
            }
            _indices.addElement(newIndices);

            // Update the offsets
            _vertexDataOffset +=
                    (vertices.length + normals.length + texcoords.length) * 4;
            _indexDataOffset += indices.length;

            // Update the vertex array offsets used for drawing
            _positionsOffset = 0;
            _normalsOffset = 0;
            for (int i = 0; i < _positions.size(); i++) {
                _normalsOffset +=
                        ((FloatArray) _positions.elementAt(i)).array.length * 4;
            }
            _texcoordsOffset = _normalsOffset;
            for (int i = 0; i < _normals.size(); i++) {
                _texcoordsOffset +=
                        ((FloatArray) _normals.elementAt(i)).array.length * 4;
            }

            _meshCount++;
        }
    }

    /**
     * Clears texture handles when game screen is closed
     */
    static void cleanup() {
        // Wipe out the table of texture handles so they'll be reloaded
        // the next time the game is initialized.
        _textureHandles = new Hashtable();
    }
}
