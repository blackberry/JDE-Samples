/*
 * OpenGLScreen.java
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

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.FullScreen;

/**
 * A FullScreen implementation on which to display EGL graphics
 */
public class OpenGLScreen extends FullScreen implements Runnable {
    private EGL11 _egl;
    private EGLDisplay _eglDisplay;
    private EGLConfig _eglConfig;
    private EGLContext _eglContext;
    private EGLSurface _eglSurface;
    private GL10 _gl;

    private final Renderer _renderer;

    private boolean _running;
    private boolean _idle;
    private boolean _unload;

    private Bitmap _backBuffer;
    private Graphics _backBufferGraphics;

    /**
     * Creates a new OpenGLScreen object
     * 
     * @param renderer
     *            An object implementing the Renderer interface
     */
    public OpenGLScreen(final Renderer renderer) {
        super(Screen.DEFAULT_MENU | Screen.DEFAULT_CLOSE);
        _renderer = renderer;
    }

    /**
     * Called when the application starts to initialize the EGL display, surface
     * and context.
     */
    private void initializeEGL() throws Exception {
        // Get the EGL interface
        _egl = (EGL11) EGLContext.getEGL();

        // Get a display and initialize it
        _eglDisplay = _egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (_eglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new Exception("_eglDisplay failed: " + _egl.eglGetError());
        }

        if (!_egl.eglInitialize(_eglDisplay, null)) {
            throw new Exception("_eglInitialize failed: " + _egl.eglGetError());
        }

        // Define a 16-bit color config
        final int[] configAttrs =
                new int[] { EGL10.EGL_RED_SIZE, 5, EGL10.EGL_GREEN_SIZE, 6,
                        EGL10.EGL_BLUE_SIZE, 5, EGL10.EGL_SURFACE_TYPE,
                        EGL10.EGL_WINDOW_BIT, EGL10.EGL_DEPTH_SIZE,
                        EGL10.EGL_DONT_CARE, EGL10.EGL_STENCIL_SIZE,
                        EGL10.EGL_DONT_CARE, EGL10.EGL_NONE };

        // Get the first matching config
        final EGLConfig[] configs = new EGLConfig[1];
        final int[] numConfigs = new int[1];
        if (!_egl.eglChooseConfig(_eglDisplay, configAttrs, configs, 1,
                numConfigs)) {
            throw new Exception("_eglChooseConfig failed: "
                    + _egl.eglGetError());
        }
        if (numConfigs[0] == 0) {
            throw new Exception("_eglChooseConfig returned no valid configs.");
        }
        _eglConfig = configs[0];

        // Load the EGL surface and context
        loadEGL();
    }

    private void loadEGL() throws Exception {
        // Create a window surface (passing the screen in as the native_window
        // argument)
        _eglSurface =
                _egl.eglCreateWindowSurface(_eglDisplay, _eglConfig, this, null);
        if (_eglSurface == EGL10.EGL_NO_SURFACE) {
            throw new Exception("_eglCreateWindowSurface failed: "
                    + _egl.eglGetError());
        }

        // Initialize the EGL context
        initializeEGLContext();
    }

    private void unloadEGL() {
        // Destroy our surface and context
        _egl.eglMakeCurrent(_eglDisplay, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        _egl.eglDestroyContext(_eglDisplay, _eglContext);
        _egl.eglDestroySurface(_eglDisplay, _eglSurface);
    }

    /**
     * Called to initialize the EGL Context, which happens at startup and after
     * an EGL_CONTEXT_LOST error.
     */
    private void initializeEGLContext() throws Exception {
        // Get an EGL rendering context
        _eglContext =
                _egl.eglCreateContext(_eglDisplay, _eglConfig,
                        EGL10.EGL_NO_CONTEXT, null);
        if (_eglContext == EGL10.EGL_NO_CONTEXT) {
            throw new Exception("_eglCreateContext failed: "
                    + _egl.eglGetError());
        }

        // Make the context and surface current
        if (!_egl.eglMakeCurrent(_eglDisplay, _eglSurface, _eglSurface,
                _eglContext)) {
            throw new Exception("_eglMakeCurrent failed: " + _egl.eglGetError());
        }

        _gl = (GL10) _eglContext.getGL();

        _renderer.contextCreated(_gl);

        updateViewport();
        _renderer.sizeChanged(_gl, _backBuffer.getWidth(), _backBuffer
                .getHeight());
    }

    /**
     * Handles an EGL_CONTEXT_LOST error
     * 
     * If and when this error occurs, the application must destroy its EGL
     * context and reinitialize it and all OpenGL ES states. The error can occur
     * due to power management events (such as the device going to sleep) and
     * may also occur due to low memory conditions.
     * 
     * When an application receives this error, it should try to reinitialize
     * the context only at the point that it needs to be rendered again. For
     * example, an application should not re-create its context if it is
     * currently in the background (rather, it should wait until it regains the
     * foreground).
     */
    private void handleContextLost() throws Exception {
        // Destroy the context
        _egl.eglMakeCurrent(_eglDisplay, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        _egl.eglDestroyContext(_eglDisplay, _eglContext);

        // Attempt to re-initialize the EGL context
        initializeEGLContext();
    }

    /**
     * Main rendering loop
     */
    public void run() {
        // Sleep for a small amount of time to allow the initial screen
        // transition to finish.
        try {
            Thread.sleep(250);
        } catch (final InterruptedException ie) {
        }

        try {
            // Initialize EGL
            initializeEGL();

            // Initialize renderer
            _renderer.initialize();

            int throttle = 0;

            while (_running) {
                throttle = (int) System.currentTimeMillis();

                // Update the renderer
                _renderer.update();

                // Draw the frame
                renderFrame();

                if (_idle) {
                    // Copy the contents of the surface to the offscreen buffer
                    // before going idle.
                    _egl.eglCopyBuffers(_eglDisplay, _eglSurface, _backBuffer);

                    // If we were asked to unload, do so now
                    final boolean requestUnload = _unload;
                    if (requestUnload) {
                        _unload = false;
                        unloadEGL();
                    }

                    // Wait until we are resumed/notified
                    synchronized (this) {
                        try {
                            wait();
                        } catch (final InterruptedException ie) {
                        }
                    }

                    // If we unloaded EGL as a result of going into the
                    // background,
                    // reload the EGL surface and context now
                    if (requestUnload) {
                        loadEGL();
                    }
                } else {
                    // Swap the buffers
                    _egl.eglSwapBuffers(_eglDisplay, _eglSurface);

                    // Determine how long the frame took to render
                    throttle = (int) System.currentTimeMillis() - throttle;

                    // Throttle to 30 FPS to control CPU usage. In order to
                    // achieve the 30 FPS rate, one frame should take
                    // approximately 33 ms to render (33ms * 30 =~ 1000 ms).
                    throttle = 33 - throttle;

                    if (throttle > 0) {
                        // Throttle cpu usage
                        try {
                            Thread.sleep(throttle);
                        } catch (final InterruptedException ie) {
                        }
                    }
                }
            }

            unloadEGL();
        } catch (final Exception e) {
            Application.getApplication().invokeAndWait(new Runnable() {
                public void run() {
                    Dialog.alert(e.toString());
                }
            });
            System.exit(1);
        }
    }

    /**
     * Renders a single frame
     */
    private void renderFrame() throws Exception {
        // Although not strictly neccessary, call _eglMakeCurrent each frame to
        // check for EGL_CONTEXT_LOST errors.
        if (!_egl.eglMakeCurrent(_eglDisplay, _eglSurface, _eglSurface,
                _eglContext)) {
            final int error = _egl.eglGetError();
            if (error == EGL11.EGL_CONTEXT_LOST) {
                // The context was lost, so we need to destroy and reinitialize
                // it
                handleContextLost();
            }
        }

        // Check for a viewport change
        if (updateViewport()) {
            _renderer.sizeChanged(_gl, _backBuffer.getWidth(), _backBuffer
                    .getHeight());
        }

        // Signal that we're about to begin GL rendering
        _egl.eglWaitNative(EGL10.EGL_CORE_NATIVE_ENGINE, _backBufferGraphics);

        _renderer.render(_gl);

        // Signal that we're finished GL rendering
        _egl.eglWaitGL();
    }

    /**
     * Checks if the viewport needs to be updated to match the screen size
     */
    private boolean updateViewport() {
        final int width = getWidth();
        final int height = getHeight();

        if (_backBuffer != null && _backBuffer.getWidth() == width
                && _backBuffer.getHeight() == height) {
            return false; // No change needed
        }

        _backBuffer = new Bitmap(Bitmap.ROWWISE_16BIT_COLOR, width, height);
        _backBufferGraphics = Graphics.create(_backBuffer);

        return true;
    }

    /**
     * Starts the rendering thread
     * 
     * This is called the first time the screen becomes visible
     */
    private void start() {
        _running = true;
        new Thread(this).start();
    }

    /**
     * @see Screen#close()
     */
    public void close() {
        _running = false;

        synchronized (this) {
            notifyAll();
        }

        super.close();
    }

    /**
     * @see Field#onVisibilityChange(boolean)
     */
    protected void onVisibilityChange(final boolean visible) {
        if (visible) {
            if (!_running) {
                // The screen was shown for the first time, start
                // the rendering thread.
                start();
            } else {
                resume();
            }
        } else {
            // Pause and unload the context and surface
            pause(true);
        }
    }

    /**
     * @see Screen#onObscured()
     */
    protected void onObscured() {
        // Pause rendering loop
        pause(false);
    }

    /**
     * @see Screen#onExposed()
     */
    protected void onExposed() {
        // Resume rendering loop
        resume();
    }

    /**
     * Pauses the rendering loop
     */
    private void pause(final boolean unload) {
        _unload = unload;
        _idle = true;
    }

    /**
     * Resumes the rendering loop
     */
    private void resume() {
        _idle = false;

        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * @see Screen#paint(Graphics)
     * 
     *      Draws the offscreen buffer. This is neccessary to prevent flickers
     *      and to ensure that the 3D content is drawn when the UI system
     *      decides to paint the screen. This method is only called when the
     *      screen is invalidated (for example, when menus or popup screens are
     *      invoked).
     */
    protected void paint(final Graphics g) {
        if (_backBuffer == null) {
            g.setBackgroundColor(Color.BLACK);
            g.clear();
        } else {
            g.drawBitmap(0, 0, _backBuffer.getWidth(), _backBuffer.getHeight(),
                    _backBuffer, 0, 0);
        }
    }
}
