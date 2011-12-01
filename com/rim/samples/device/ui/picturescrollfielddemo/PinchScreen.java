/*
 * PinchScreen.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.ui.picturescrollfielddemo;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.input.InputSettings;
import net.rim.device.api.ui.input.TouchscreenSettings;

/**
 * A screen to display an image which can be resized using pinch gestures
 */
public final class PinchScreen extends MainScreen {
    private final Bitmap _bitmapOrig;
    private final DemoBitmapField _bitmapField;
    private final float _origWidth;
    private final float _origHeight;
    private float _previousWidth;
    private float _previousHeight;

    /**
     * Creates a new PinchScreen object
     */
    public PinchScreen(final Bitmap bitmap) {
        super(NO_VERTICAL_SCROLL);

        setTitle("Pinch Screen");

        _origWidth = bitmap.getWidth();
        _origHeight = bitmap.getHeight();
        _previousWidth = _origWidth;
        _previousHeight = _origHeight;
        _bitmapOrig = bitmap;

        _bitmapField = new DemoBitmapField(bitmap, FIELD_HCENTER);

        // Center BitmapField using managers
        final HorizontalFieldManager hfm =
                new HorizontalFieldManager(USE_ALL_HEIGHT);
        final VerticalFieldManager vfm =
                new VerticalFieldManager(USE_ALL_WIDTH | FIELD_VCENTER);
        vfm.add(_bitmapField);
        hfm.add(vfm);
        add(hfm);

        // Enable pinch
        final InputSettings inputSettings =
                TouchscreenSettings.createEmptySet();
        inputSettings.set(TouchscreenSettings.DETECT_PINCH, 1);
        addInputSettings(inputSettings);
    }

    /**
     * @see Screen#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(final TouchEvent message) {
        if (message.getEvent() == TouchEvent.GESTURE) {
            final TouchGesture gesture = message.getGesture();
            final int event = gesture.getEvent();

            final float magnitude = gesture.getPinchMagnitude();

            if (event == TouchGesture.PINCH_UPDATE) {
                onPinch(magnitude, false);
            } else if (event == TouchGesture.PINCH_END) {
                onPinch(magnitude, true);
            }
        }

        return super.touchEvent(message);
    }

    /**
     * Updates bitmap size on TouchGesture.PINCH_UPDATE or
     * TouchGesture.PINCH_END
     * 
     * @param magnitude
     *            Pinch magnitude of the current pinch gesture
     * @param pinchEnd
     *            True if current gesture event is TouchGesture.PINCH_END,
     *            otherwise false
     */
    private void onPinch(final float magnitude, final boolean pinchEnd) {
        // Calculate dimensions, restrict to screen size
        float width = Math.min(_previousWidth * magnitude, Display.getWidth());
        float height =
                Math.min(_previousHeight * magnitude, Display.getHeight());

        // Maintain aspect ratio when either width or height is maxed out
        if (width == Display.getWidth() || height == Display.getHeight()) {
            float ratioWidth = 1f;
            float ratioHeight = 1f;

            ratioWidth = width / _previousWidth;
            ratioHeight = height / _previousHeight;

            if (ratioWidth < ratioHeight) {
                height = _previousHeight * ratioWidth;
            } else if (ratioHeight < ratioWidth) {
                width = _previousWidth * ratioHeight;
            }
        }

        // Ensure Bitmap will be at least original size
        width = Math.max(width, _origWidth);
        height = Math.max(height, _origHeight);

        // Create Bitmap from original scaled to new calculated dimensions
        final Bitmap bitmapScaled = new Bitmap((int) width, (int) height);
        _bitmapOrig.scaleInto(bitmapScaled, Bitmap.FILTER_BOX);

        // Replace Bitmap
        _bitmapField.setBitmap(bitmapScaled);

        // Save current dimensions
        if (pinchEnd) {
            _previousWidth = width;
            _previousHeight = height;
        }
    }

    /**
     * A BitmapField class that resizes itself based on device orientation
     */
    private final class DemoBitmapField extends BitmapField {
        /**
         * Creates a new DemoBitmapField object
         * 
         * @param bitmap
         *            The Bitmap to be contained by this field
         * @param style
         *            Style bit for this field
         */
        DemoBitmapField(final Bitmap bitmap, final long style) {
            super(bitmap, style);
        }

        /**
         * @see Field#layout(int, int)
         */
        protected void layout(final int width, final int height) {
            final float displayWidth = Display.getWidth();
            final float displayHeight = Display.getHeight();

            float newWidth = width;
            float newHeight = height;

            if (displayWidth < _previousWidth
                    || displayHeight < _previousHeight) {
                float ratioWidth = 1;
                float ratioHeight = 1;

                ratioWidth = displayWidth / _previousWidth;
                ratioHeight = displayHeight / _previousHeight;

                if (ratioWidth < ratioHeight) {
                    newHeight = _previousHeight * ratioWidth;
                } else if (ratioHeight < ratioWidth) {
                    newWidth = _previousWidth * ratioHeight;
                }

                // Create Bitmap from original scaled to new calculated
                // dimensions
                final Bitmap bitmapScaled =
                        new Bitmap((int) newWidth, (int) newHeight);
                _bitmapOrig.scaleInto(bitmapScaled, Bitmap.FILTER_BOX);

                // Need to use invokeLater() as bitmap cannot be set while
                // in layout().
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        _bitmapField.setBitmap(bitmapScaled);
                    }
                });

                _previousWidth = newWidth;
                _previousHeight = newHeight;

            }

            setExtent((int) newWidth, (int) newHeight);

            super.layout((int) newWidth, (int) newHeight);
        }
    }
}
