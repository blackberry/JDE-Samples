/*
 * SpriteGameGLField.java
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

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import net.rim.device.api.animation.AbstractAnimation;
import net.rim.device.api.animation.Animation;
import net.rim.device.api.animation.AnimationListener;
import net.rim.device.api.animation.Animator;
import net.rim.device.api.math.Matrix4f;
import net.rim.device.api.math.Transform3D;
import net.rim.device.api.math.Vector3f;
import net.rim.device.api.opengles.GL20;
import net.rim.device.api.opengles.GLField;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.util.TimeSource;

/**
 * The GLField which renders the current level and handles the animations and
 * any keyboard or touch events.
 */
public class SpriteGameGLField extends GLField implements AnimationListener {
    /** Holds the game's current level */
    private SpriteGameLevel _level;

    /** Holds the win sprite */
    private final Sprite _win;

    /** Holds the win animation */
    private final Animation _winAnimation;

    /** Holds whether or not the win sprite should be rendered */
    private boolean _renderWin = false;

    /** Holds the lose sprite */
    private final Sprite _lose;

    /** Holds the lose animation */
    private final Animation _loseAnimation;

    /** Holds whether or not the lose sprite should be rendered */
    private boolean _renderLose = false;

    /** Holds the modelview matrix. Specific to GL20 version */
    private Matrix4f _modelview;

    /** Holds the projection matrix. Specific to GL20 version */
    private Matrix4f _projection;

    /** Holds the current level */
    private int _currentLevel = 0;

    /** Holds the version of GL being used */
    private final int _glVersion;

    /** A translation vector */
    Vector3f _t = new Vector3f();

    /** Holds the animator used to update the obstacle animations */
    private final Animator _animator;

    /** Holds whether the sprite should be moving left */
    private boolean _moveLeft;

    /** Holds whether the sprite should be moving right */
    private boolean _moveRight;

    /** Holds the list of levels */
    private final String[] _levelNames = { "/levelone.xml", "/leveltwo.xml" };

    /** Time source used for animator and to calculate elapsed time */
    private final TimeSource _timeSource;

    /** Time value used in calculating elapsed time between updates */
    private long _prevTime;

    /**
     * Constructor for the SpriteGameGLField
     * 
     * @param glVersion
     *            The version of OpenGL to use
     */
    SpriteGameGLField(final int glVersion) {
        super(glVersion);

        _glVersion = glVersion;

        final Vector3f translation = new Vector3f(-40.0f, -4.0f, 0.0f);

        // Set up the win sprite
        _win = new Sprite("win.png");
        _win._transform.setScale(new Vector3f(5.0f, 5.0f, 5.0f));
        _win._transform.setTranslation(translation);

        // Set up the lose sprite
        _lose = new Sprite("lose.png");
        _lose._transform.setScale(new Vector3f(5.0f, 5.0f, 5.0f));
        _lose._transform.setTranslation(translation);

        _timeSource = new TimeSource();
        _timeSource.start();
        _animator = new Animator(0, _timeSource);

        // Set up the win animation
        _winAnimation =
                _animator.addAnimation(_win._transform,
                        Transform3D.ANIMATION_PROPERTY_TRANSLATE, 4,
                        new float[] { 0.0f, 0.2f, 0.8f, 1.0f }, 0, new float[] {
                                -40.0f, -4.0f, 0.0f, 0.0f, -4.0f, 0.0f, 0.0f,
                                -4.0f, 0.0f, 20.0f, -4.0f, 0.0f }, 0,
                        Animation.EASINGCURVE_LINEAR, 1750L);
        _winAnimation.setRepeatCount(1);
        _winAnimation.setListener(this);

        // set up the lose animation
        _loseAnimation =
                _animator.addAnimation(_lose._transform,
                        Transform3D.ANIMATION_PROPERTY_TRANSLATE, 4,
                        new float[] { 0.0f, 0.2f, 0.8f, 1.0f }, 0, new float[] {
                                -40.0f, -4.0f, 0.0f, 0.0f, -4.0f, 0.0f, 0.0f,
                                -4.0f, 0.0f, 20.0f, -4.0f, 0.0f }, 0,
                        Animation.EASINGCURVE_LINEAR, 1750L);
        _loseAnimation.setRepeatCount(1);
        _loseAnimation.setListener(this);

        loadLevel();
    }

    /**
     * @see net.rim.device.api.ui.Field#layout(int, int)
     */
    protected void layout(final int width, final int height) {
        setExtent(width, height);
    }

    /**
     * @see GLField#initialize(GL)
     */
    protected void initialize(final GL gl) {
        // Determine what version of OpenGL we are using
        // and call the corresponding initialize() method.
        if (_glVersion == GLField.VERSION_1_1) {
            initialize((GL11) gl);
        } else {
            initialize((GL20) gl);
        }

        setTargetFrameRate(70);
    }

    /**
     * Initialize method for OpenGL v1.1
     * 
     * @param gl
     *            The OpenGL 1.1 object that will be initialized
     */
    private void initialize(final GL11 gl) {
        // Set up the GL11 object
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glClearColor(0.35f, 0.67f, 1.0f, 1.0f);
        gl.glEnable(GL10.GL_NORMALIZE);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glEnable(GL10.GL_COLOR_MATERIAL);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glDisable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        // Set up the orthographic (2D) projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(-12.0f, 12.0f, -10.0f, 20.0f * Display.getHeight()
                / Display.getWidth() - 10.0f, -1.0f, 1.0f);

        _win.initialize(gl);
        _lose.initialize(gl);
    }

    /**
     * Initialize method for OpenGL v2.0
     * 
     * @param gl
     *            The OpenGL 2.0 object that will be initialized
     */
    private void initialize(final GL20 gl) {
        _modelview = new Matrix4f();
        _projection = new Matrix4f();

        // Set up the GL20 object
        gl.glClearColor(0.35f, 0.67f, 1.0f, 1.0f);
        gl.glEnable(GL20.GL_CULL_FACE);
        gl.glDisable(GL20.GL_DITHER);
        gl.glEnable(GL20.GL_BLEND);
        gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Load the shader used for rendering
        Sprite.loadShader(gl);

        // Set up the orthographic (2D) projection matrix
        Matrix4f.createOrthographic(-12.0f, 12.0f, -10.0f, 20.0f
                * Display.getHeight() / Display.getWidth() - 10.0f, -1.0f,
                10.0f, _projection);

        _win.initialize(gl);
        _lose.initialize(gl);
    }

    /**
     * @see GLField#update()
     */
    protected void update() {
        _animator.update();

        // Get amount of time last frame took, in seconds
        final float elapsedTime = (_timeSource.getTime() - _prevTime) / 1000.0f;
        _prevTime = _timeSource.getTime();

        // Process input (Note: touch events are also set up to add
        // these keys to the array).
        if (_moveLeft) {
            _level.getCharacter().move(-elapsedTime * 60);
        }

        if (_moveRight) {
            _level.getCharacter().move(elapsedTime * 60);
        }

        int result = 0;

        // Update the level
        if (!_renderWin || !_renderLose) {
            result = _level.update();
        }

        // The level has been won, so begin the win animation
        if (result == SpriteGameLevel.LEVEL_WIN && !_renderWin) {
            _winAnimation.begin(0L);
        }

        // The level has been lost, so begin the lose animation
        else if (result == SpriteGameLevel.LEVEL_LOSE) {
            _level.getCharacter().reset();
            _loseAnimation.begin(0L);
        }
    }

    /**
     * @see GLField#render(GL)
     */
    protected void render(final GL gl) {
        // Determine what OpenGL version to use
        // and call the corresponding render() method.
        if (gl instanceof GL20) {
            render((GL20) gl);
        } else {
            render((GL11) gl);
        }
    }

    /**
     * Render method for OpenGL v1.1
     * 
     * @param gl
     *            The OpenGL v1.1 object that will be used to render
     */
    private void render(final GL11 gl) {
        if (!_level.isLevelLoaded()) {
            return;
        }

        if (!_level.isLevelInitialized()) {
            _level.initialize(gl);
        }

        // Clear the color and depth buffers
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Clear the modelview matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        // Translate to the correct location
        gl.glTranslatef(0.0f, 0.0f, -1.0f);

        // Render the level.
        _level.render(gl);

        // If the win or lose animations are running, render the corresponding
        // sprite
        if (_renderWin) {
            _win.render(gl);
        }

        if (_renderLose) {
            _lose.render(gl);
        }
    }

    /**
     * Render method for OpenGL v2.0
     * 
     * @param gl
     *            The OpenGL 2.0 object that will be used to render
     */
    private void render(final GL20 gl) {
        if (!_level.isLevelLoaded()) {
            return;
        }

        if (!_level.isLevelInitialized()) {
            _level.initialize(gl);
        }

        // Clear the color and depth buffers
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Load the shader program
        gl.glUseProgram(Sprite.getProgram());

        // Load the projection matrix
        gl.glUniformMatrix4fv(Sprite.getProjectionMatrixLocation(), 1, false,
                _projection.getArray(), 0);

        // Render the level
        _level.render(gl, _modelview);

        // If the win or lose animations are running, render the corresponding
        // sprite
        if (_renderWin) {
            _win.render(gl, _modelview);
        }

        if (_renderLose) {
            _lose.render(gl, _modelview);
        }
    }

    /**
     * Loads the current level
     */
    private void loadLevel() {
        _level = new SpriteGameLevel(_levelNames[_currentLevel], _animator);

        _currentLevel++;

        if (_currentLevel >= _levelNames.length) {
            _currentLevel = 0;
        }
    }

    /**
     * @see net.rim.device.api.animation.AnimationListener#animationBegin(AbstractAnimation)
     */
    public void animationBegin(final AbstractAnimation animation) {
        if (animation == _winAnimation) {
            _renderWin = true;
        } else if (animation == _loseAnimation) {
            _renderLose = true;
        }
    }

    /**
     * @see net.rim.device.api.animation.AnimationListener#animationEnd(AbstractAnimation)
     */
    public void animationEnd(final AbstractAnimation animation) {
        if (animation == _winAnimation) {
            _renderWin = false;
            loadLevel();
        } else if (animation == _loseAnimation) {
            _renderLose = false;
        }
    }

    /**
     * @see net.rim.device.api.ui.Field#keyDown(int, int)
     */
    protected boolean keyDown(final int keycode, final int time) {
        final StringBuffer sb = new StringBuffer();

        // Retrieve the characters mapped to the keycode for the current
        // keyboard layout
        Keypad.getKeyChars(keycode, sb);

        if (sb.toString().indexOf('q') != -1) {
            _moveLeft = true;
        }

        if (sb.toString().indexOf('w') != -1) {
            _moveRight = true;
        }

        return false;
    }

    /**
     * @see net.rim.device.api.ui.Field#keyUp(int, int)
     */
    protected boolean keyUp(final int keycode, final int time) {
        final StringBuffer sb = new StringBuffer();

        // Retrieve the characters mapped to the keycode for the current
        // keyboard layout
        Keypad.getKeyChars(keycode, sb);

        if (sb.toString().indexOf('q') != -1) {
            _moveLeft = false;
        }
        if (sb.toString().indexOf('w') != -1) {
            _moveRight = false;
        }

        return false;
    }

    /**
     * @see net.rim.device.api.ui.Field#keyChar(char, int, int)
     */
    protected boolean keyChar(final char c, final int status, final int time) {
        switch (c) {
        case Characters.SPACE:
            _level.getCharacter().jump();
            return true;
        case Characters.LATIN_SMALL_LETTER_S:
            _level.getCharacter().shrink();
            return true;
        case Characters.ESCAPE:
        default:
            return false;
        }
    }

    /**
     * @see net.rim.device.api.ui.Field#navigationClick(int, int)
     */
    protected boolean navigationClick(final int status, final int time) {
        _level.getCharacter().jump();
        return true;
    }

    /**
     * @see net.rim.device.api.ui.Field#navigationMovement(int, int, int, int)
     */
    protected boolean navigationMovement(final int dx, final int dy,
            final int status, final int time) {
        _level.getCharacter().move(dx * 4);
        return true;
    }

    /**
     * @see net.rim.device.api.ui.Field#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(final TouchEvent message) {
        if (message == null) {
            return false;
        }

        boolean tappedCenter = false;

        switch (message.getEvent()) {
        case TouchEvent.GESTURE: {
            final TouchGesture gesture = message.getGesture();

            if (gesture.getEvent() == TouchGesture.SWIPE) {
                final int direction = gesture.getSwipeDirection();
                if (direction == TouchGesture.SWIPE_NORTH
                        || direction == TouchGesture.SWIPE_SOUTH) {
                    // Swipe up or down to shrink or grow
                    _level.getCharacter().shrink();
                }
            }

            break;
        }
        case TouchEvent.DOWN: {
            final int x = message.getX(1);
            final int x2 = message.getX(2);

            // Touch with two fingers to jump. Note: can tap the screen
            // while moving in a direction to jump while moving.
            if (x2 != -1) {
                _level.getCharacter().jump();
            } else {
                final int displayWidth = Display.getWidth();

                // Touch the left or right quarter of the
                // screen to move in that direction.
                if (x < displayWidth / 4) {
                    _moveLeft = true;
                } else if (x > displayWidth - displayWidth / 4) {
                    _moveRight = true;
                } else {
                    tappedCenter = true;
                }
            }

            break;
        }

        case TouchEvent.UP: {
            final int x = message.getX(1);

            // Release the first finger that went down to stop moving
            if (x != -1) {
                _moveLeft = false;
                _moveRight = false;
            }

            // Jump if the middle of the screen was tapped
            if (tappedCenter) {
                _level.getCharacter().jump();
            }

            break;
        }
        }

        return true;
    }
}
