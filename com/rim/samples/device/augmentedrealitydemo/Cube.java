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

package com.rim.samples.device.augmentedrealitydemo;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * A class representing a 3D cube
 */
public final class Cube {

    private static final float[] VERTICES = { -0.5f, 0.5f, 0.5f, -0.5f, -0.5f,
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

    private static final float[] TEXCOORDS = { 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1,
            1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0,
            1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1,
            0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, };

    private static final float[] NORMALS = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
            1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
            1, 0, 0, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0,
            -1, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, 0,
            1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, -1, 0, 0, -1,
            0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0 };

    private static final short[] INDICES = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
            28, 29, 30, 31, 32, 33, 34, 35 };

    private int _buffers[];
    private final float _vertices[];
    private final float _normals[];
    private final float _texcoords[];
    private final short _indices[];
    private final int _verticesSize;
    private final int _normalsSize;
    private final int _texcoordsSize;
    private final int _indicesSize;
    private int _vertexAttribIndex = -1;
    private int _normalAttribIndex = -1;
    private int _texcoordAttribIndex = -1;

    /**
     * Creates a new Cube object
     * 
     * @param g
     *            GL object used for drawing
     */
    public Cube(final GL g) {
        _vertices = VERTICES;
        _normals = NORMALS;
        _texcoords = TEXCOORDS;
        _indices = INDICES;
        _verticesSize = VERTICES.length * 4;
        _normalsSize = NORMALS.length * 4;
        _texcoordsSize = TEXCOORDS.length * 4;
        _indicesSize = INDICES.length * 2;

        init(g);
    }

    /**
     * Initializes the cube
     * 
     * @param g
     *            GL object used for drawing
     */
    void init(final GL g) {
        final FloatBuffer vertices =
                ByteBuffer.allocateDirect(
                        _verticesSize + _normalsSize + _texcoordsSize)
                        .asFloatBuffer();
        vertices.put(_vertices);

        if (_normalsSize > 0) {
            vertices.put(_normals);
        }

        if (_texcoordsSize > 0) {
            vertices.put(_texcoords);
        }

        vertices.rewind();
        final ShortBuffer indices =
                ByteBuffer.allocateDirect(_indicesSize).asShortBuffer();
        indices.put(_indices);
        indices.rewind();

        _buffers = new int[2];

        final GL11 gl = (GL11) g;
        gl.glGenBuffers(2, _buffers, 0);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, _buffers[0]);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, _verticesSize + _normalsSize
                + _texcoordsSize, vertices, GL11.GL_STATIC_DRAW);
        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, _buffers[1]);
        gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, _indicesSize, indices,
                GL11.GL_STATIC_DRAW);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Renders the cube
     * 
     * @param g
     *            GL object used for drawing
     */
    public void render(final GL g) {
        final GL11 gl = (GL11) g;
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, _buffers[0]);
        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, _buffers[1]);

        if (_vertexAttribIndex != -1) {
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, 0);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        }

        if (_normalAttribIndex != -1 && _normalsSize > 0) {
            gl.glNormalPointer(GL10.GL_FLOAT, 0, _verticesSize);
            gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        }

        if (_texcoordAttribIndex != -1 && _texcoordsSize > 0) {
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _verticesSize
                    + _normalsSize);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }

        gl.glDrawElements(GL10.GL_TRIANGLES, _indicesSize / 2,
                GL10.GL_UNSIGNED_SHORT, 0);

        if (_vertexAttribIndex != -1) {
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }

        if (_normalAttribIndex != -1 && _normalsSize > 0) {
            gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        }

        if (_texcoordAttribIndex != -1 && _texcoordsSize > 0) {
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Enables vertex attribute
     */
    public void enableVertexAttrib() {
        _vertexAttribIndex = 1;
    }

    /**
     * Enables normal attribute
     */
    public void enableNormalAttrib() {
        _normalAttribIndex = 1;
    }

    /**
     * Enables texcoord attribute
     */
    public void enableTexcoordAttrib() {
        _texcoordAttribIndex = 1;
    }
}
