/*
 * SpriteGameCharacter.java
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

import net.rim.device.api.animation.AbstractAnimation;
import net.rim.device.api.animation.Animatable;
import net.rim.device.api.animation.Animation;
import net.rim.device.api.animation.AnimationListener;
import net.rim.device.api.animation.AnimationValue;
import net.rim.device.api.animation.Animator;
import net.rim.device.api.math.BoundingBox;
import net.rim.device.api.math.Bounds;
import net.rim.device.api.math.Transform3D;
import net.rim.device.api.math.Vector3f;

/**
 * Represents the character for the sprite game.
 */
public class SpriteGameCharacter extends Sprite implements Animatable,
        AnimationListener {
    /** Holds the character's texture coordinates */
    private static final float[] TEXCOORDS = new float[] { 0.1875f, 0.8125f,
            0.8125f, 0.8125f, 0.8125f, 0.1875f, 0.1875f, 0.1875f };

    /** TODO */
    private final Transform3D _prevTransform;

    /** TODO */
    private final Bounds _prevBounds;

    /** Holds how much the character falls each frame */
    private static final float FALL_INCREMENT = 0.5f;

    /** Holds how much the character's shrink mode affects walking and jumping */
    private static final float SHRINK_FACTOR = 0.85f;

    /** Holds how much the character moves each frame when in normal mode */
    private static final float MOVE_INCREMENT = 0.04f;

    /** Holds whether the character is in shrink mode */
    private boolean _isShrunk = false;

    /** Holds whether the character is changing into or out of shrink mode */
    private boolean _isChangingShrinkState = false;

    /** Represents the movement of the character for the current frame */
    private float _movement = 0.0f;

    /** Holds the factor to multiply movement by each frame */
    private static final float SLOW_FACTOR = 0.85f;

    /**
     * Temporary vector used to update the character's translation (see
     * update()).
     */
    private final Vector3f _t = new Vector3f();

    /** Holds the base translation along the Y axis */
    private float _ty = 0.0f;

    /** Holds whether the character is jumping or not */
    private boolean _isJumping = false;

    /** Holds the character's jump value */
    private float _jumpY = 0.0f;

    /** Animation property for jumping */
    public static final int ANIMATION_PROPERTY_JUMP_VALUE = 0;

    /** Total number of animation properties */
    public static final int ANIMATION_PROPERTY_COUNT = 1;

    /** List of all animation properties */
    public static final int[] ANIMATION_PROPERTIES =
            { ANIMATION_PROPERTY_JUMP_VALUE };

    /** List of the component sizes for each animation property */
    public static final int[] ANIMATION_PROPERTY_COMPONENTCOUNTS = { 1 };

    /** Holds the character's jump animation's key times */
    private static float[] JUMP_KEYTIMES = new float[] { 0.0f, 1.0f };

    /** Holds the character's jump animation's key values */
    private static float[] JUMP_KEYVALUES = new float[] { 0.0f, 3.6f };

    /** Holds the character's jump animation */
    private final Animation _jump;

    /** Holds the character's default scale */
    protected static final float DEFAULT_SCALE = 0.8f;

    /** Holds the character's "enter shrink" animation's key values */
    private static float[] SHRINK_ENTER_KEYVALUES = new float[] { 0.36f, 0.36f,
            0.36f };

    /** Holds the character's "exit shrink" animation's key values */
    private static float[] SHRINK_EXIT_KEYVALUES = new float[] { DEFAULT_SCALE,
            DEFAULT_SCALE, DEFAULT_SCALE };

    /** Holds the character's "enter shrink" animation */
    private final Animation _shrinkEnter;

    /** Holds the character's "exit shrink" animation */
    private final Animation _shrinkExit;

    /** Constant used to restrict movement upwards */
    public static final int MOVE_UP = 0x01;

    /** Constant used to restrict movement downwards */
    public static final int MOVE_DOWN = 0x02;

    /** Constant used to restrict movement to the left */
    public static final int MOVE_LEFT = 0x04;

    /** Constant used to restrict movement to the right */
    public static final int MOVE_RIGHT = 0x08;

    /** Holds the directions the character is currently not allowed to move in */
    private int _movementRestrictions = 0;

    /**
     * Creates a new SpriteGameCharacter object
     * 
     * @param texture
     *            The texture to use for the character
     * @param animator
     *            The animator that the character will use
     * @param position
     *            The position vector for the character
     */
    SpriteGameCharacter(final String texture, final Animator animator,
            final Vector3f position) {
        super(TEXCOORDS, texture, new BoundingBox(new Vector3f(-1.0f, -1.0f,
                0.0f), new Vector3f(1.0f, 1.0f, 0.0f)), position);

        _transform.setScale(new Vector3f(DEFAULT_SCALE, DEFAULT_SCALE,
                DEFAULT_SCALE));

        _prevTransform = new Transform3D(_transform);
        _prevBounds = new BoundingBox();

        // Create the character's jump animation
        _jump =
                animator.addAnimation(this,
                        SpriteGameCharacter.ANIMATION_PROPERTY_JUMP_VALUE, 2,
                        JUMP_KEYTIMES, 0, JUMP_KEYVALUES, 0,
                        Animation.EASINGCURVE_QUARTIC_OUT, 400L);
        _jump.setListener(this);

        // Create the character's "shrink enter" animation
        _shrinkEnter =
                animator.addAnimationTo(_transform,
                        Transform3D.ANIMATION_PROPERTY_SCALE,
                        SHRINK_ENTER_KEYVALUES, 0,
                        Animation.EASINGCURVE_QUARTIC_IN, 400L);
        _shrinkEnter.setListener(this);

        // Create the character's "shrink exit" animation
        _shrinkExit =
                animator.addAnimationTo(_transform,
                        Transform3D.ANIMATION_PROPERTY_SCALE,
                        SHRINK_EXIT_KEYVALUES, 0,
                        Animation.EASINGCURVE_QUARTIC_IN, 400L);
        _shrinkExit.setListener(this);
    }

    /**
     * Tells the character to move
     * 
     * @param direction
     *            The direction and magnitude of movement
     */
    void move(final float direction) {
        if (direction != 0) {
            _movement +=
                    direction
                            * (!_isShrunk ? MOVE_INCREMENT : MOVE_INCREMENT
                                    * SHRINK_FACTOR);
        }
    }

    /**
     * Tells the character to jump (if possible - i.e. if the character is not
     * in mid-air).
     */
    void jump() {
        // If the character is not in mid-jump or mid-fall and
        // can jump, then start the jump animation.
        if (!_isJumping && !canMoveDown() && canMoveUp()) {
            _isJumping = true;
            _jump.begin(0L);
        }
    }

    /**
     * Tells the character to enter or exit shrink mode (toggles between the
     * two).
     */
    void shrink() {
        if (!_isChangingShrinkState) {
            if (!_isShrunk) {
                _shrinkEnter.begin(0L);
            } else {
                _shrinkExit.begin(0L);
            }

            _isChangingShrinkState = true;
        }
    }

    /**
     * Updates the character
     */
    public void update() {
        // Get the translation
        _transform.getTranslation(_t);

        // Update the vertical movement
        if (_isJumping) {
            // While the character is jumping and is allowed to move
            // upward, continue the jump animation.
            if (canMoveUp()) {
                _t.y = _ty + (!_isShrunk ? _jumpY : _jumpY * SHRINK_FACTOR);
            } else {
                // If the character has hit a block from below, stop jumping
                _isJumping = false;
                _jump.end(0L);
            }
        }

        // Character is falling
        if (canMoveDown() && !_isChangingShrinkState) {
            _t.y -= FALL_INCREMENT;
        }

        // Update the horizontal movement
        if (_movement > 0.0f && canMoveRight() || _movement < 0.0f
                && canMoveLeft()) {
            _t.x += _movement;
        }

        // Character is slowing down
        if (_movement != 0.0f) {
            _movement *= SLOW_FACTOR;
            if (Math.abs(_movement) < 0.001) {
                _movement = 0.0f;
            }
        }

        // Update the translation
        _prevTransform.set(_transform);
        _transform.setTranslation(_t);
    }

    /**
     * Retrieves the previous transform bounds
     * 
     * @return The previous transform bounds
     */
    public Bounds getPrevBounds() {
        _prevTransform.transformBounds(_originalBounds, _prevBounds);

        return _prevBounds;
    }

    /**
     * Sets the base translation along the Y axis
     * 
     * @param ty
     *            The new base translation along the Y axis
     */
    public void setTy(final float ty) {
        // Don't set this if the character grazes a tile while jumping
        if (!_isJumping) {
            _ty = ty;
        }
    }

    /**
     * Resets the character to its initial state for starting the level
     */
    public void reset() {
        super.reset();
        _isJumping = false;
        _ty = 0.0f;
        _transform.setScale(new Vector3f(SpriteGameCharacter.DEFAULT_SCALE,
                SpriteGameCharacter.DEFAULT_SCALE,
                SpriteGameCharacter.DEFAULT_SCALE));
    }

    /**
     * Clears the movement restrictions
     */
    public void clearMovementRestrictions() {
        _movementRestrictions = 0;
    }

    /**
     * Adds the given movement restriction
     * 
     * @param restriction
     *            The direction to restrict movement in
     */
    public void addMovementRestriction(final int restriction) {
        _movementRestrictions |= restriction;
        if (restriction == MOVE_LEFT && _movement < 0.0f
                || restriction == MOVE_RIGHT && _movement > 0.0f) {
            _movement = 0.0f;
        }
    }

    /**
     * Determines if the character can move up
     * 
     * @return Whether the character can move up or not
     */
    private boolean canMoveUp() {
        return (_movementRestrictions & MOVE_UP) == 0;
    }

    /**
     * Determines if the character can move down
     * 
     * @return Whether the character can move down or not
     */
    private boolean canMoveDown() {
        return (_movementRestrictions & MOVE_DOWN) == 0;
    }

    /**
     * Determines if the character can move left
     * 
     * @return Whether the character can move left or not
     */
    private boolean canMoveLeft() {
        return (_movementRestrictions & MOVE_LEFT) == 0;
    }

    /**
     * Determines if the character can move right
     * 
     * @return Whether the character can move right or not
     */
    private boolean canMoveRight() {
        return (_movementRestrictions & MOVE_RIGHT) == 0;
    }

    /**
     * @see net.rim.device.api.animation.Animatable#getAnimationValue(int,
     *      AnimationValue)
     */
    public void
            getAnimationValue(final int property, final AnimationValue value) {
        switch (property) {
        case ANIMATION_PROPERTY_JUMP_VALUE:
            value.setFloat(0, _jumpY);
            break;
        default:
            break;
        }
    }

    /**
     * @see net.rim.device.api.animation.Animatable#setAnimationValue(int,
     *      AnimationValue)
     */
    public void
            setAnimationValue(final int property, final AnimationValue value) {
        switch (property) {
        case ANIMATION_PROPERTY_JUMP_VALUE:
            _jumpY = value.getFloat(0);
            break;
        default:
            break;
        }
    }

    /**
     * @see net.rim.device.api.animation.Animatable#getAnimationPropertyComponentCount(int)
     */
    public int getAnimationPropertyComponentCount(final int property) {
        if (property >= 0 && property < ANIMATION_PROPERTY_COUNT) {
            return ANIMATION_PROPERTY_COMPONENTCOUNTS[property];
        }
        return 0;
    }

    /**
     * @see net.rim.device.api.animation.AnimationListener#animationBegin(AbstractAnimation)
     */
    public void animationBegin(final AbstractAnimation animation) {
        // Not implemented
    }

    /**
     * @see net.rim.device.api.animation.AnimationListener#animationEnd(AbstractAnimation)
     */
    public void animationEnd(final AbstractAnimation animation) {
        // Update the shrink state variables based on the status
        // of the shrink animations.
        if (animation == _shrinkEnter) {
            _isShrunk = true;
            _isChangingShrinkState = false;
        } else if (animation == _shrinkExit) {
            _isShrunk = false;
            _isChangingShrinkState = false;
        } else if (animation == _jump) {
            _isJumping = false;
        }
    }
}
