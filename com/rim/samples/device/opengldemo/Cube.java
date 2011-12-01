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

package com.rim.samples.device.opengldemo;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Defines geometry for a 3D Cube
 */
public final class Cube {
    /**
     * Creates a float buffer containing vertices for a cube
     * 
     * @return The vertex float buffer
     */
    static FloatBuffer createVertexBuffer() {
        final FloatBuffer buffer =
                ByteBuffer.allocateDirect(_vertices.length * 4).asFloatBuffer();
        buffer.put(_vertices);
        buffer.rewind();
        return buffer;
    }

    /**
     * Creates a float buffer containing normals for a cube
     * 
     * @return The normal float buffer
     */
    static FloatBuffer createNormalBuffer() {
        final FloatBuffer buffer =
                ByteBuffer.allocateDirect(_normals.length * 4).asFloatBuffer();
        buffer.put(_normals);
        buffer.rewind();
        return buffer;
    }

    /**
     * Creates a float buffer containing texture coordinates for a cube
     * 
     * @return The texture coordinate float buffer
     */
    static FloatBuffer createTexCoordBuffer() {
        final FloatBuffer buffer =
                ByteBuffer.allocateDirect(_texCoords.length * 4)
                        .asFloatBuffer();
        buffer.put(_texCoords);
        buffer.rewind();
        return buffer;
    }

    private static float[] _vertices = {
            // front
            -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, -0.5f, -0.5f,
            0.5f,
            0.5f,
            -0.5f,
            0.5f,

            // Right
            0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f, 0.5f,
            0.5f,
            -0.5f,
            -0.5f,

            // Back
            0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f,
            -0.5f,
            -0.5f,

            // Left
            -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f,
            0.5f,

            // Top
            -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,

            // bottom
            -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f };

    private static float[] _normals = {
            // Front
            0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,

            // Right
            1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,

            // Back
            0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1,

            // Left
            -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0,

            // Top
            0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,

            // Bottom
            0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0 };

    private static float[] _texCoords = {
            // Front
            0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1,

            // Right
            0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1,

            // Back
            0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1,

            // Left
            0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1,

            // Top
            0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1,

            // Bottom
            0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, };
}
