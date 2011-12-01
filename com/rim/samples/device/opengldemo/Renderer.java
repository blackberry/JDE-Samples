/*
 * Renderer.java
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

import javax.microedition.khronos.opengles.GL10;

/**
 * A simple generic renderer interface
 */
interface Renderer {
    /**
     * Called during startup to initialize state and data that is not bound to
     * an OpenGL context.
     */
    public void initialize();

    /**
     * Called after the OpenGL context is created. This method is called both at
     * startup and after an EGL_CONTEXT_LOST error occurs. The renderer should
     * set up any OpenGL context related data here, such as textures and an
     * initial OpenGL state.
     * 
     * @param gl
     *            The newly created OpenGL context
     */
    public void contextCreated(GL10 gl);

    /**
     * Called when the size of the displayable area changes
     * 
     * @param gl
     *            The OpenGL context
     * @param width
     *            The new displayble width
     * @param height
     *            The new displayable height
     */
    public void sizeChanged(GL10 gl, int width, int height);

    /**
     * Called once per frame
     */
    public void update();

    /**
     * Called once per frame
     * 
     * @param gl
     *            The OpenGL context.
     */
    public void render(GL10 gl);
}
