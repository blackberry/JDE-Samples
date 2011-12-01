/**
 * EyelidFieldDemoScreen.java
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

package com.rim.samples.device.ui.eyelidfielddemo;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.VirtualKeyboard;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.extension.container.EyelidFieldManager;

/**
 * The main screen class for the Eyelid Field Demo
 */
public final class EyelidFieldDemoScreen extends MainScreen implements
        FieldChangeListener {
    private final EyelidFieldManager _eyelidFieldManager;
    private final CheckboxField _lockCheckbox;
    private final CheckboxField _showOnInputCheckbox;

    /**
     * Creates a new EyelidFieldDemoScreen object
     */
    public EyelidFieldDemoScreen() {
        super(NO_VERTICAL_SCROLL);

        _eyelidFieldManager = new EyelidFieldManager();

        // Change the display time from the default 1.2s
        _eyelidFieldManager.setEyelidDisplayTime(2000);

        // Add components to the north eye-lid of the blinker
        _eyelidFieldManager.addTop(new CustomLabelField("Eyelid Field Demo"));
        _eyelidFieldManager.addTop(new SeparatorField());
        _eyelidFieldManager.addTop(new CustomLabelField(
                "You can add any fields..."));
        _eyelidFieldManager.addTop(new CustomEditField("Type something here: ",
                "abc", 100, BasicEditField.FILTER_DEFAULT));

        // Add components to the south eye-lid of the blinker
        _eyelidFieldManager.addBottom(new CustomLabelField(
                "Here is a row of buttons..."));
        final HorizontalFieldManager buttonPanel =
                new HorizontalFieldManager(Field.FIELD_HCENTER
                        | Field.USE_ALL_WIDTH);
        buttonPanel.add(new SimpleButton("ABC"));
        buttonPanel.add(new SimpleButton("123"));
        buttonPanel.add(new SimpleButton("XYZ"));
        _eyelidFieldManager.addBottom(buttonPanel);

        // Add checkbox in non-eyelid region for showing eyelids on user input
        _showOnInputCheckbox =
                new CheckboxField("Show eyelids on user input", true,
                        Field.FIELD_HCENTER);
        _showOnInputCheckbox.setChangeListener(this);
        _eyelidFieldManager.add(_showOnInputCheckbox, 0, 85);

        // Add checkbox in non-eyelid region for locking the eyelids
        _lockCheckbox =
                new CheckboxField("Lock eyelids", false, Field.FIELD_HCENTER);
        _lockCheckbox.setChangeListener(this);
        _eyelidFieldManager.add(_lockCheckbox, 0, 115);

        add(_eyelidFieldManager);

        _showOnInputCheckbox.setFocus();

        // Disable virtual keyboard so it doesn't obscure bottom eyelid
        final VirtualKeyboard keyboard = getVirtualKeyboard();
        if (keyboard != null) {
            keyboard.setVisibility(VirtualKeyboard.IGNORE);
        }
    }

    /**
     * @see FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field == _lockCheckbox) {
            if (_lockCheckbox.getChecked()) {
                // Lock the eyelids
                _eyelidFieldManager.setEyelidDisplayTime(0);
                _eyelidFieldManager.setEyelidsVisible(true);
            } else {
                // Unlock the eyelids
                _eyelidFieldManager.setEyelidDisplayTime(2000);
                _eyelidFieldManager.setEyelidsVisible(false);
            }
        } else if (field == _showOnInputCheckbox) {
            if (_showOnInputCheckbox.getChecked()) {
                // Show the eyelids when user input detected
                _eyelidFieldManager.showOnInput(true);
            } else {
                // Don't show the eyelids when user input detected
                _eyelidFieldManager.showOnInput(false);
            }
        }
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }

    /**
     * An edit field in which the paint() method is overridden so as to change
     * the foreground color of the text.
     */
    private static final class CustomEditField extends BasicEditField {
        /**
         * Constructs a new CustomEditField object
         * 
         * @param label
         *            Label for this field
         * @param initialValue
         *            Initial text to show in the field
         * @param maxNumChars
         *            Maximum number of characters this field can hold
         * @param style
         *            Styles for this field
         */
        CustomEditField(final String label, final String initialValue,
                final int maxNumChars, final long style) {
            super(label, initialValue, maxNumChars, style);
        }

        /**
         * @see BasicEditField#paint()
         */
        public void paint(final Graphics graphics) {
            graphics.setColor(Color.WHITE);
            super.paint(graphics);
        }
    }

    /**
     * A label field in which the paint() method is overridden so as to change
     * the foreground color of the text.
     */
    private static final class CustomLabelField extends LabelField {
        /**
         * Constructs a new CustomLabelField object
         * 
         * @param text
         *            The text to display on the label
         */
        CustomLabelField(final String text) {
            super(text);
        }

        /**
         * @see LabelField#paint()
         */
        public void paint(final Graphics graphics) {
            graphics.setColor(Color.WHITE);
            super.paint(graphics);
        }
    }

    /**
     * A button that pops up a dialog when clicked
     */
    private static final class SimpleButton extends ButtonField {
        FieldChangeListener buttonClickListener = new FieldChangeListener() {
            public void fieldChanged(final Field field, final int context) {
                Dialog.alert(getLabel() + " clicked!");
            }
        };

        SimpleButton(final String label) {
            super(label, ButtonField.CONSUME_CLICK);
            setChangeListener(buttonClickListener);
        }
    }
}
