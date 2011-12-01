/*
 * AdjustmentScreen.java
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

package com.rim.samples.device.ui.uitoolkitdemo;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Adjustment;
import net.rim.device.api.ui.AdjustmentListener;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.TextSpinBoxField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.SpinBoxFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;

/**
 * A class to demonstrate usage of the Adjustment API
 */
public final class AdjustmentScreen extends MainScreen {
    private static final int ROW_HEIGHT = 45;

    private final Adjustment _adjustmentRed;
    private final Adjustment _adjustmentGreen;
    private final Adjustment _adjustmentBlue;

    /**
     * Creates a new AdjustmentScreen object
     */
    public AdjustmentScreen() {
        super(NO_VERTICAL_SCROLL);

        setTitle("Adjustment Screen");

        final LabelField directions =
                new LabelField(
                        "Use the spin box to change the RGB values of the color displayed in the bottom field.");
        add(directions);

        add(new SeparatorField());

        final ColorBox colorBox = new ColorBox(Field.FIELD_HCENTER);

        final Integer[] choices = new Integer[256];
        for (int i = 0; i < choices.length; ++i) {
            choices[i] = new Integer(i);
        }

        final MySpinBoxField spinBoxFieldRed = new MySpinBoxField(choices);
        spinBoxFieldRed.setListener(colorBox);
        _adjustmentRed = spinBoxFieldRed.getAdjustment();

        final MySpinBoxField spinBoxFieldGreen = new MySpinBoxField(choices);
        spinBoxFieldGreen.setListener(colorBox);
        _adjustmentGreen = spinBoxFieldGreen.getAdjustment();

        final MySpinBoxField spinBoxFieldBlue = new MySpinBoxField(choices);
        spinBoxFieldBlue.setListener(colorBox);
        _adjustmentBlue = spinBoxFieldBlue.getAdjustment();

        final SpinBoxFieldManager spinBoxManager = new SpinBoxFieldManager();
        spinBoxManager.add(spinBoxFieldRed);
        spinBoxManager.add(spinBoxFieldGreen);
        spinBoxManager.add(spinBoxFieldBlue);
        spinBoxManager.setVisibleRows(3);
        spinBoxManager.setRowHeight(ROW_HEIGHT);

        final VerticalFieldManager vfm =
                new VerticalFieldManager(Field.FIELD_HCENTER);
        vfm.setBackground(BackgroundFactory.createSolidBackground(Color.BLACK));
        vfm.add(spinBoxManager);
        add(vfm);

        add(new SeparatorField());

        add(colorBox);
    }

    /**
     * A field to display a colored rectangle. The color is updated dynamically
     * on changes to spin box fields. The field will be sized based on
     * orientation and available height and width.
     */
    private final class ColorBox extends Field implements AdjustmentListener {
        private static final int PADDING = 30;

        private int _red;
        private int _green;
        private int _blue;
        private int _width;
        private int _height;

        /**
         * Creates a new ColorBox object
         * 
         * @param style
         *            Style bit for this field
         */
        ColorBox(final long style) {
            super(style);
        }

        /**
         * @see AdjustmentListener#configurationChanged(Adjustment)
         */
        public void configurationChanged(final Adjustment source) {
            // Do nothing
        }

        /**
         * @see AdjustmentListener#valueChanged(Adjustment)
         */
        public void valueChanged(final Adjustment source) {
            if (source == _adjustmentRed) {
                _red = source.getValue();
            } else if (source == _adjustmentGreen) {
                _green = source.getValue();
            } else if (source == _adjustmentBlue) {
                _blue = source.getValue();
            }

            // Re-paint
            invalidate();
        }

        /**
         * @see Field#layout(int, int)
         */
        protected void layout(final int width, final int height) {
            final UiApplication uiApp = UiApplication.getUiApplication();
            final MainScreen mainScreen = (MainScreen) uiApp.getActiveScreen();
            final Manager manager = mainScreen.getMainManager();
            int availableHeight = Display.getHeight();
            final int fieldCount = manager.getFieldCount();

            for (int i = fieldCount - 1; i >= 0; --i) {
                final Field field = manager.getField(i);
                if (field != this) {
                    availableHeight -= field.getHeight();
                }
            }

            _width = Display.getWidth() - PADDING;
            _height = availableHeight - PADDING;

            setExtent(_width, _height);
        }

        /**
         * @see Field#paint(Graphics)
         */
        protected void paint(final Graphics graphics) {
            graphics.setColor(_red << 16 | _green << 8 | _blue << 0);
            graphics.fillRect(0, 0, _width, _height);
            graphics.setColor(Color.BLACK);
            graphics.drawRect(0, 0, _width, _height);
        }

        /**
         * @see Field#getPreferredHeight()
         */
        public int getPreferredHeight() {
            return _height;
        }

        /**
         * @see Field#getPreferredWidth()
         */
        public int getPreferredWidth() {
            return _width;
        }
    }

    /**
     * A custom spin box utilizing the Adjustment API to handle all
     * notifications through a single interface.
     */
    private static final class MySpinBoxField extends TextSpinBoxField
            implements FieldChangeListener {
        private static final int WIDTH = 45;

        private final Adjustment _adjustment;

        /**
         * Creates a new MySpinBoxField object
         * 
         * @param choices
         *            An array of Integers representing the values which can be
         *            selected by this field
         */
        MySpinBoxField(final Integer[] choices) {
            super(choices);
            _adjustment = new Adjustment(0, 0, choices.length, 0, 0, 0);
            setChangeListener(this);
        }

        /**
         * @see FieldChangeListener#fieldChanged(Field, int)
         */
        public void fieldChanged(final Field field, final int context) {
            _adjustment.setValue(getSelectedIndex());
        }

        /**
         * Return the adjustment object associated with this field
         * 
         * @return The adjustment associated with this field
         */
        Adjustment getAdjustment() {
            return _adjustment;
        }

        /**
         * Adds a listener which will be informed of changes via the Adjustment
         * object
         * 
         * @param listener
         *            The new listener
         */
        void setListener(final AdjustmentListener listener) {
            _adjustment.addListener(listener);
        }

        /**
         * @see Field#getPreferredWidth()
         */
        public int getPreferredWidth() {
            return WIDTH;
        }
    }
}
