/*
 * TiltDemo.java
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

package com.rim.samples.device.tiltdemo;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Touchscreen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngineInstance;
import net.rim.device.api.ui.VirtualKeyboard;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This sample application demonstrates how to control the tilting and virtual
 * keyboard functionality on a touch screen BlackBerry Smartphone. The sample
 * shows how to control which tilt orientations are allowed and also
 * demonstrates manually displaying or hiding the virtual keyboard. The sample
 * also contains an example of how to trap virtual keyboard input.
 */
final class TiltDemo extends UiApplication {
    /**
     * Entry point
     */
    public static void main(final String args[]) {
        final TiltDemo theApp = new TiltDemo();
        theApp.enterEventDispatcher();
    }

    /**
     * Default constructor
     */
    public TiltDemo() {
        final TiltDemoScreen screen = new TiltDemoScreen();
        pushScreen(screen);
    }
}

/**
 * Main screen for the Tilt Demo.
 */
final class TiltDemoScreen extends MainScreen implements FieldChangeListener {
    CheckboxField _northCheckbox;
    CheckboxField _eastCheckbox;
    CheckboxField _westCheckbox;

    ButtonField _keyboardButton;

    BasicEditField _editField;

    CustomSpanField _customSpanField;

    /**
     * Default constructor.
     */
    TiltDemoScreen() {
        // If this is not a touchscreen device, exit the application.
        if (!Touchscreen.isSupported()) {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert("This application requires a touch screen device.");
                    System.exit(0);
                }
            });
        } else {
            setTitle("Tilt Demo");

            // Create a Field that will change size depending on orientation.
            _customSpanField = new CustomSpanField();
            add(_customSpanField);

            // Create a button to toggle the visibility of the virtual keyboard.
            _keyboardButton =
                    new ButtonField("Toggle Keyboard",
                            ButtonField.CONSUME_CLICK);
            _keyboardButton.setChangeListener(this);
            add(_keyboardButton);

            /*
             * Create a BasicEditField for simple text input. This field can be
             * used to experiment with how the virtual keyboard works in the
             * different orientations, and show how bringing the focus to a
             * field that requires input will display the virtual keyboard if it
             * isn't already visible.
             */
            _editField = new BasicEditField("Basic Edit Field: ", "");
            add(_editField);

            // Create one check box per direction.
            _northCheckbox =
                    new CheckboxField("Allow north orientation?", true);
            _eastCheckbox = new CheckboxField("Allow east orientation?", true);
            _westCheckbox = new CheckboxField("Allow west orientation?", true);

            // Set change listeners and add to the screen.
            _northCheckbox.setChangeListener(this);
            _eastCheckbox.setChangeListener(this);
            _westCheckbox.setChangeListener(this);

            add(_northCheckbox);
            add(_eastCheckbox);
            add(_westCheckbox);
        }
    }

    /**
     * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field instanceof CheckboxField) {

            boolean checked = false;

            // We want to set the acceptable directions to every direction which
            // has
            // its corresponding check box checked.
            int acceptableDirections = 0;

            // For each direction, set the bit if the checkbox is checked.
            if (_northCheckbox.getChecked() == true) {
                acceptableDirections =
                        acceptableDirections | Display.DIRECTION_NORTH;
                checked = true;
            }
            if (_eastCheckbox.getChecked() == true) {
                acceptableDirections =
                        acceptableDirections | Display.DIRECTION_EAST;
                checked = true;
            }
            if (_westCheckbox.getChecked() == true) {
                acceptableDirections =
                        acceptableDirections | Display.DIRECTION_WEST;
                checked = true;
            }

            if (!checked) {

                // If no checkboxes are checked, all directions will be allowed.
                // We won't allow the user to uncheck all checkboxes.
                final CheckboxField checkboxField = (CheckboxField) field;
                checkboxField.setChecked(true);
                Dialog.alert("Please allow at least one direction");
            } else {

                // Set the acceptable directions and then force the app to check
                // if the screen requires a rotation.
                final UiEngineInstance ui = Ui.getUiEngineInstance();
                ui.setAcceptableDirections(acceptableDirections);
            }
        } else {
            if (field == _keyboardButton) {

                // Get this screen's instance of the virtual keyboard
                final VirtualKeyboard keyboard = this.getVirtualKeyboard();

                // Change the visibility
                if (keyboard.getVisibility() == VirtualKeyboard.SHOW) {
                    keyboard.setVisibility(VirtualKeyboard.HIDE);
                } else {
                    keyboard.setVisibility(VirtualKeyboard.SHOW);
                }
            }
        }
    }

    /**
     * Prevent the save dialog from being displayed.
     * 
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        return true;
    }

    /**
     * Sample overriding of Screen method to trap virtual keyboard input.
     * 
     * @see net.rim.device.api.system.KeyListener#keyChar(char,int,int)
     */
    public boolean keyChar(final char key, final int status, final int time) {
        // If the key pressed is ENTER, show the menu.
        if (key == Characters.ENTER) {
            return this.onMenu(0);
        }
        return false;
    }
}
