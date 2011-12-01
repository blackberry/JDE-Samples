/**
 * CreateImageVGField.java
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

package com.rim.samples.device.openvgdemo;

import net.rim.device.api.animation.AnimatedScalar;
import net.rim.device.api.animation.Animation;
import net.rim.device.api.animation.Animator;
import net.rim.device.api.openvg.VG;
import net.rim.device.api.openvg.VG10;
import net.rim.device.api.openvg.VG11;
import net.rim.device.api.openvg.VGField;
import net.rim.device.api.openvg.VGUtils;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontSpec;
import net.rim.device.api.ui.XYRect;

/**
 * A VGField class that demonstrates displaying a series of icon images
 * contained in a single image file in an circular animated fashion. The class
 * also uses Open VG to draw a textual description on the screen.
 */
public class CreateImageVGField extends VGField {
    private static final int TARGET_FRAME_RATE = 60;
    private static final int NUM_ICONS = 13;
    private static final int NUM_COLUMNS = 3;
    private static final int ICON_WIDTH = 27;
    private static final int ICON_HEIGHT = 27;
    private static final int TEXT_OFFSET = 30;

    private static final float[] MY_CLEAR_COLOR = new float[] { 0.6f, 0.8f,
            1.0f, 1.0f };
    private static final float RADIUS = 52.0f;
    private static final float ROTATE = -360.0f / NUM_ICONS;

    private final int[] _imageHandles = new int[NUM_ICONS];

    private int _textImage;
    private int _displayHeight;

    private float _xScreenCenter;
    private float _yScreenCenter;

    private Animator _animator;
    private final AnimatedScalar _mainRotation = new AnimatedScalar(0.0f);

    /**
     * Creates a new CreateImageVGField object
     */
    public CreateImageVGField() {
        super(VGField.VERSION_1_1);

        // Set the target frame rate for calling update()
        setTargetFrameRate(TARGET_FRAME_RATE);
    }

    /**
     * @see net.rim.device.api.openvg.VGField#initialize(VG)
     */
    protected void initialize(final VG g) {
        final VG11 vg = (VG11) g;
        vg.vgSetfv(VG10.VG_CLEAR_COLOR, 4, MY_CLEAR_COLOR, 0);

        final XYRect rect = new XYRect();

        // Create the bitmap from bundled resource "icons.png"
        final Bitmap bitmap = Bitmap.getBitmapResource("icons.png");

        // Create the image for all the icons
        for (int i = 0; i < _imageHandles.length; i++) {
            // Update the XYRect in which the image will be displayed
            updateRect(rect, i);

            _imageHandles[i] =
                    VGUtils.vgCreateImage(vg, bitmap, true,
                            VG10.VG_IMAGE_QUALITY_BETTER, rect);
        }

        // Get the default font and its FontSpec
        final Font font = Font.getDefault();
        final FontSpec fontSpec = font.getFontSpec();

        // Create text image
        _textImage =
                VGUtils.vgCreateTextAsImage(vg, fontSpec,
                        "Tap or click to swap screens", null, null);

        // Set up the animation. The animation will animate a scalar float value
        // (_mainRotation) from 360 to 0 over 3 seconds and will repeat
        // indefinitely.
        _animator = new Animator(0);
        final Animation animation =
                _animator.addAnimationFromTo(_mainRotation,
                        AnimatedScalar.ANIMATION_PROPERTY_SCALAR, 360.0f, 0.0f,
                        Animation.EASINGCURVE_LINEAR, 3000L);
        animation.setRepeatCount(Animation.REPEAT_COUNT_INDEFINITE);

        _animator.begin(0L);
    }

    /**
     * Updates the XYRect where the icons will be displayed and places the icons
     * in a circle.
     * 
     * @param rect
     *            XYRect that will be updated
     * @param position
     *            The icon's position in the bitmap
     */
    private void updateRect(final XYRect rect, final int position) {
        final int x = position % NUM_COLUMNS * ICON_WIDTH;
        final int y = position / NUM_COLUMNS * ICON_HEIGHT;
        rect.set(x, y, ICON_WIDTH, ICON_HEIGHT);
    }

    /**
     * @see net.rim.device.api.openvg.VGField#render(VG)
     */
    protected void render(final VG g) {
        final VG11 vg = (VG11) g;

        // Clear the display
        vg.vgClear(0, 0, getWidth(), getHeight());

        vg.vgSeti(VG10.VG_MATRIX_MODE, VG10.VG_MATRIX_IMAGE_USER_TO_SURFACE);

        // Shifting bits by >> 1 is equivalent to division by 2
        final float halfIconWidth = ICON_WIDTH >> 1;

        // Go through all the images and rotate them
        // around the center of the screen.
        for (int i = 0; i < _imageHandles.length; i++) {
            // Load clean Identity matrix
            vg.vgLoadIdentity();

            // Translate to the center of the display
            vg.vgTranslate(_xScreenCenter, _yScreenCenter);

            // Rotate the image
            vg.vgRotate(_mainRotation.getFloat() + ROTATE * i);

            // Translate the image half of the icon's width
            vg.vgTranslate(-halfIconWidth, RADIUS);

            // Draw the rotated, translated image
            vg.vgDrawImage(_imageHandles[i]);
        }

        // Draw the text image on this field
        drawText(vg);
    }

    /**
     * Draws text at the top of the VGField
     * 
     * @param vg
     *            The object that will be used to render the text
     */
    public void drawText(final VG11 vg) {
        vg.vgSeti(VG10.VG_MATRIX_MODE, VG10.VG_MATRIX_IMAGE_USER_TO_SURFACE);

        // Load a clean identity matrix
        vg.vgLoadIdentity();

        // Translate to the next drawing location
        vg.vgTranslate(0.0f, _displayHeight - TEXT_OFFSET);

        // Draw the text on the display
        vg.vgDrawImage(_textImage);
    }

    /**
     * @see net.rim.device.api.openvg.VGField#update()
     */
    public void update() {
        _animator.update();
    }

    /**
     * @see net.rim.device.api.openvg.VGField#layout(int, int)
     */
    protected void layout(final int width, final int height) {
        final int displayWidth = Display.getWidth();
        _displayHeight = Display.getHeight();

        // Shifting bits by >> 1 is equivalent to division by 2
        _xScreenCenter = (displayWidth >> 1);
        _yScreenCenter = (_displayHeight >> 1);

        setExtent(Math.min(displayWidth, width), Math.min(_displayHeight,
                height));
    }
}
