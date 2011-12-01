/*
 * Cube.java
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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import net.rim.device.api.opengles.GL20;

/**
 * Encapsulates a 3D Cube that can be drawn using OpenGL ES 2.0
 */
public class Cube {
    // Contains the vertex buffer names
    private int _buffers[];

    // Vertex positions
    private static final float VERTICES[] = { -0.5f, 0.5f, 0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f };

    // Vertex normals
    private static final float NORMALS[] = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
            1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
            1, 0, 0, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0,
            -1, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, 0,
            1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, -1, 0, 0, -1,
            0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0 };

    // Vertex texture coordinates
    private static final float TEX_COORDS[] = { 0, 0, 0, 1, 1, 0, 1, 0, 0, 1,
            1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0,
            0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0,
            1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1 };

    // Primitive indices
    private static final short INDICES[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
            28, 29, 30, 31, 32, 33, 34, 35 };

    // Size of the buffers
    private final int _verticesSize = VERTICES.length * 4;
    private final int _normalsSize = NORMALS.length * 4;
    private final int _texcoordsSize = TEX_COORDS.length * 4;
    private final int _indicesSize = INDICES.length * 2;

    private int _vertexAttribIndex = -1;
    private int _normalAttribIndex = -1;
    private int _texcoordAttribIndex = -1;

    private final int _mode = GL20.GL_TRIANGLES;

    /**
     * Initializes the cube by creating vertex buffer objects and loading the
     * vertex and indices data.
     * 
     * @param gl
     *            The GL context.
     */
    public void init(final GL20 gl) {
        // Load the vertex data into the NIO buffer
        final FloatBuffer vertices =
                ByteBuffer.allocateDirect(
                        _verticesSize + _normalsSize + _texcoordsSize)
                        .asFloatBuffer();
        vertices.put(VERTICES);

        if (_normalsSize > 0) {
            vertices.put(NORMALS);
        }

        if (_texcoordsSize > 0) {
            vertices.put(TEX_COORDS);
        }

        vertices.rewind();

        // Load the indices data into the NIO buffer
        final ShortBuffer indices =
                ByteBuffer.allocateDirect(_indicesSize).asShortBuffer();
        indices.put(INDICES);
        indices.rewind();

        _buffers = new int[2];

        // Generate 2 buffer object names
        gl.glGenBuffers(2, _buffers, 0);

        // Bind the buffer object that will store the vertex data
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, _buffers[0]);

        // Load the vertex data
        gl.glBufferData(GL20.GL_ARRAY_BUFFER, _verticesSize + _normalsSize
                + _texcoordsSize, vertices, GL20.GL_STATIC_DRAW);

        // Bind the buffer object that will store the indices data
        gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, _buffers[1]);

        // Load the element indices data
        gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, _indicesSize, indices,
                GL20.GL_STATIC_DRAW);

        // Unbind the buffers because we should not assume
        // the cube will be the first thing drawn.
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Renders the cube
     * 
     * @param gl
     *            The GL context
     */
    public void render(final GL20 gl) {
        // Bind the buffer object that contains the vertex data of the cube
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, _buffers[0]);

        // Bind the buffer object that contains the
        // element indices data of the cube.
        gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, _buffers[1]);

        // For each vertex attribute, set the offsets into the vertex
        // buffer object and set the vertex attribute location.
        if (_vertexAttribIndex != -1) {
            gl.glVertexAttribPointer(_vertexAttribIndex, 3, GL20.GL_FLOAT,
                    false, 0, 0);
            gl.glEnableVertexAttribArray(_vertexAttribIndex);
        }
        if (_normalAttribIndex != -1 && _normalsSize > 0) {
            gl.glVertexAttribPointer(_normalAttribIndex, 3, GL20.GL_FLOAT,
                    false, 0, _verticesSize);
            gl.glEnableVertexAttribArray(_normalAttribIndex);
        }
        if (_texcoordAttribIndex != -1 && _texcoordsSize > 0) {
            gl.glVertexAttribPointer(_texcoordAttribIndex, 2, GL20.GL_FLOAT,
                    false, 0, _verticesSize + _normalsSize);
            gl.glEnableVertexAttribArray(_texcoordAttribIndex);
        }

        // Render the primitives
        gl.glDrawElements(_mode, _indicesSize / 2, GL20.GL_UNSIGNED_SHORT, 0);

        if (_vertexAttribIndex != -1) {
            gl.glDisableVertexAttribArray(_vertexAttribIndex);
        }

        if (_normalAttribIndex != -1 && _normalsSize > 0) {
            gl.glDisableVertexAttribArray(_normalAttribIndex);
        }

        if (_texcoordAttribIndex != -1 && _texcoordsSize > 0) {
            gl.glDisableVertexAttribArray(_texcoordAttribIndex);
        }

        // Unbind the buffers because more geometry may be drawn after this
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Enables the vertex position attribute and sets the attribute location
     * 
     * @param vertexAttribIndex
     *            Index of the generic vertex attribute for the vertex position
     */
    public void enableVertexAttrib(final int vertexAttribIndex) {
        _vertexAttribIndex = vertexAttribIndex;
    }

    /**
     * Enables the normal attribute and sets the attribute location
     * 
     * @param normalAttribIndex
     *            Index of the generic vertex attribute for the normal
     */
    public void enableNormalAttrib(final int normalAttribIndex) {
        _normalAttribIndex = normalAttribIndex;
    }

    /**
     * Enables the texture coordinate attribute and sets attribute location
     * 
     * @param texcoordAttribIndex
     *            Index of the generic vertex attribute for the texture
     *            coordinate
     */
    public void enableTexcoordAttrib(final int texcoordAttribIndex) {
        _texcoordAttribIndex = texcoordAttribIndex;
    }
}
