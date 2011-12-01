/*
 * TouchEventInjectorScreen.java
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

package com.rim.samples.device.toucheventinjectordemo;

import net.rim.device.api.system.EventInjector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * The MainScreen class for the Touch Event Injector Demo application
 */
public final class TouchEventInjectorScreen extends MainScreen {
    private final LabelField _helpText;
    private final LabelField _outputText;
    private final ButtonField _sampleButton;

    private final StringBuffer _output;

    // Constructor
    public TouchEventInjectorScreen() {
        // Set the displayed title of the screen
        setTitle("Touch Event Injector Demo");

        // Initialize the output String
        _output = new StringBuffer();

        // Initialize label field with on screen instructions
        _helpText =
                new LabelField("Open the menu to begin creating touch events.");
        add(_helpText);

        // Initialize button field
        _sampleButton =
                new ButtonField("Sample Button", ButtonField.CONSUME_CLICK
                        | ButtonField.NEVER_DIRTY);
        add(_sampleButton);
        _sampleButton.setChangeListener(_listener);

        // Initialize output label field
        _outputText = new LabelField();
        add(_outputText);

        // Add menu items
        addMenuItem(_clickScreen);
        addMenuItem(_clickButton);
        addMenuItem(_swipe);
        addMenuItem(_tap);
        addMenuItem(_twoFingerTap);
    }

    /**
     * A menu item to invoke a TouchEvent which clicks the button field on the
     * screen.
     */
    private final MenuItem _clickButton = new MenuItem("Click the button",
            200000, 10) {
        public void run() {
            // Get button coordinates relative to _helpText
            final int buttonYCoordinate = _helpText.getHeight() + 40;

            // Create the four touch events needed to click the button
            final EventInjector.TouchEvent downEvent =
                    new EventInjector.TouchEvent(TouchEvent.DOWN, 40,
                            buttonYCoordinate, -1, -1, -1); // Touch the screen
            final EventInjector.TouchEvent clickEvent =
                    new EventInjector.TouchEvent(TouchEvent.CLICK, 40,
                            buttonYCoordinate, -1, -1, -1); // Click the screen
            final EventInjector.TouchEvent unclickEvent =
                    new EventInjector.TouchEvent(TouchEvent.UNCLICK, 40,
                            buttonYCoordinate, -1, -1, -1); // Unlick the screen
            final EventInjector.TouchEvent upEvent =
                    new EventInjector.TouchEvent(TouchEvent.UP, 40,
                            buttonYCoordinate, -1, -1, -1); // Lift finger from
                                                            // the screen

            // Clear the output string
            _output.delete(0, _output.length());

            // Invoke the touch events
            EventInjector.invokeEvent(downEvent);
            EventInjector.invokeEvent(clickEvent);
            EventInjector.invokeEvent(unclickEvent);
            EventInjector.invokeEvent(upEvent);
            updateOutputText();
        }
    };

    /**
     * A menu item to swipe the screen
     */
    private final MenuItem _swipe =
            new MenuItem("Swipe the screen", 200000, 10) {
                public void run() {
                    /**
                     * Create a move event array to pass into
                     * injectSwipeGesture(). This array contains move events for
                     * one touch point.
                     */
                    final EventInjector.TouchEvent[] moveEvents =
                            new EventInjector.TouchEvent[3];
                    moveEvents[0] =
                            new EventInjector.TouchEvent(TouchEvent.MOVE, 60,
                                    60, -1, -1, -1);
                    moveEvents[1] =
                            new EventInjector.TouchEvent(TouchEvent.MOVE, 120,
                                    120, -1, -1, -1);
                    moveEvents[2] =
                            new EventInjector.TouchEvent(TouchEvent.MOVE, 50,
                                    50, -1, -1, -1);

                    // Clear the output string
                    _output.delete(0, _output.length());

                    // Inject a swipe gesture with origin coordinates of (0, 0)
                    EventInjector.TouchEvent.injectSwipeGesture(0, 0,
                            moveEvents);
                    updateOutputText();
                }
            };

    /**
     * A menu item to display a dialog that allows the user to specify where to
     * click the screen.
     */
    private final MenuItem _clickScreen = new MenuItem("Click the screen",
            200000, 10) {
        public void run() {
            // Dialog containing input fields for x and y coordinates
            final Dialog clickDialog =
                    new Dialog(Dialog.D_OK_CANCEL, "Specify click location",
                            Dialog.OK, null, Manager.BOTTOMMOST);

            final BasicEditField xPos1Input =
                    new BasicEditField("Click position x1: ", "");
            final BasicEditField yPos1Input =
                    new BasicEditField("Click position y1: ", "");
            final BasicEditField xPos2Input =
                    new BasicEditField("Click position x2: ", "");
            final BasicEditField yPos2Input =
                    new BasicEditField("Click position y2: ", "");

            clickDialog.add(xPos1Input);
            clickDialog.add(yPos1Input);
            clickDialog.add(xPos2Input);
            clickDialog.add(yPos2Input);

            // Display the dialog
            clickDialog.doModal();

            // Check if the user clicked OK
            if (clickDialog.getSelectedValue() == Dialog.OK) {
                // Clear the output string
                _output.delete(0, _output.length());

                try {
                    // Clear the output string
                    _output.delete(0, _output.length());

                    // Check that integers were entered and that the
                    // coordinates are valid.
                    final int x1 = Integer.parseInt(xPos1Input.getText());
                    final int y1 = Integer.parseInt(yPos1Input.getText());
                    final int x2 = Integer.parseInt(xPos2Input.getText());
                    final int y2 = Integer.parseInt(yPos2Input.getText());

                    EventInjector.TouchEvent.invokeClickThrough(x1, y1, x2, y2);

                    updateOutputText();
                } catch (final NumberFormatException nfe) {
                    Dialog.alert("Invalid input: " + nfe.getMessage()
                            + "\n\nPlease enter a number.");
                } catch (final IllegalArgumentException iae) {
                    Dialog.alert("Invalid coordinate. \n\nPlease try again.");
                }
            }
        }
    };

    /**
     * A menu item to display a dialog that allows the user to specify screen
     * location for injecting a tap gesture.
     */
    private final MenuItem _tap = new MenuItem("Tap the screen", 200000, 10) {
        public void run() {
            // Dialog containing the input fields for x and y coordinates and
            // number of taps.
            final Dialog tapDialog =
                    new Dialog(Dialog.D_OK_CANCEL, "Specify tap location",
                            Dialog.OK, null, Manager.NO_VERTICAL_SCROLL);

            final BasicEditField xPosInput =
                    new BasicEditField("Tap position x: ", "");
            final BasicEditField yPosInput =
                    new BasicEditField("Tap position y: ", "");
            final BasicEditField tapCountInput =
                    new BasicEditField("Number of taps: ", "");

            tapDialog.add(xPosInput);
            tapDialog.add(yPosInput);
            tapDialog.add(tapCountInput);

            // Display the dialog
            tapDialog.doModal();

            if (tapDialog.getSelectedValue() == Dialog.OK) {

                // Clear the output string
                _output.delete(0, _output.length());

                try {
                    // Check that integers were entered and that the coordinates
                    // and taps are valid.
                    final int x = Integer.parseInt(xPosInput.getText());
                    final int y = Integer.parseInt(yPosInput.getText());
                    final int taps = Integer.parseInt(tapCountInput.getText());

                    EventInjector.TouchEvent.injectTapGesture(x, y, taps);

                    updateOutputText();
                } catch (final NumberFormatException nfe) {
                    Dialog.alert("Invalid input: " + nfe.getMessage()
                            + "\n\nPlease enter a number.");
                } catch (final IllegalArgumentException iae) {
                    Dialog.alert("Invalid coordinate or tap count. \n\nPlease try again.");
                }
            }
        }
    };

    /**
     * A menu item to display a dialog that allows the user to specify screen
     * location for injecting a two finger tap.
     */
    private final MenuItem _twoFingerTap = new MenuItem(
            "Two Finger Tap the screen", 200000, 10) {
        public void run() {
            final Dialog tapDialog =
                    new Dialog(Dialog.D_OK_CANCEL, "Specify tap location",
                            Dialog.OK, null, Manager.NO_VERTICAL_SCROLL);

            final BasicEditField xPos1Input =
                    new BasicEditField("Tap position x1: ", "");
            final BasicEditField yPos1Input =
                    new BasicEditField("Tap position y1: ", "");
            final BasicEditField xPos2Input =
                    new BasicEditField("Tap position x2: ", "");
            final BasicEditField yPos2Input =
                    new BasicEditField("Tap position y2: ", "");
            final NumericChoiceField touchPointInput =
                    new NumericChoiceField("Touch point: ", 1, 2, 1);

            tapDialog.add(xPos1Input);
            tapDialog.add(yPos1Input);
            tapDialog.add(xPos2Input);
            tapDialog.add(yPos2Input);
            tapDialog.add(touchPointInput);

            // Display the dialog
            tapDialog.doModal();

            if (tapDialog.getSelectedValue() == Dialog.OK) {
                // Clear the output string
                _output.delete(0, _output.length());

                try {
                    final int x1 = Integer.parseInt(xPos1Input.getText());
                    final int y1 = Integer.parseInt(yPos1Input.getText());
                    final int x2 = Integer.parseInt(xPos2Input.getText());
                    final int y2 = Integer.parseInt(yPos2Input.getText());
                    final int touchPoint = touchPointInput.getSelectedValue();

                    EventInjector.TouchEvent.injectTwoFingerTap(x1, y1, x2, y2,
                            touchPoint);

                    updateOutputText();
                } catch (final NumberFormatException nfe) {
                    Dialog.alert("Invalid input: " + nfe.getMessage()
                            + "\n\nPlease enter a number.");
                } catch (final IllegalArgumentException iae) {
                    Dialog.alert("Invalid coordinate or touch point. \n\nPlease try again.");
                }
            }
        }
    };

    // Listener for button clicks
    private final FieldChangeListener _listener = new FieldChangeListener() {
        public void fieldChanged(final Field field, final int context) {
            if (field instanceof ButtonField) {
                // Button was clicked
                _output.append("Button clicked.\n");
            }
        }
    };

    /**
     * Method to update the text in the event list output
     */
    private void updateOutputText() {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                _outputText.setText(_output);
            }
        });
    }

    /**
     * @see Screen#touchEvent(TouchEvent) This implementation outputs Touch
     *      Event notifications to standard output
     */
    protected boolean touchEvent(final TouchEvent message) {
        // Retrieve the new x and y touch positions
        int x = message.getX(1);
        int y = message.getY(1);

        // If the first position is (-1, -1), use the second position
        if (x < 0 && y < 0) {
            x = message.getX(2);
            y = message.getY(2);
        }

        // Handle the current event
        switch (message.getEvent()) {
        case TouchEvent.DOWN:
            _output.append("DOWN at (");
            _output.append(x);
            _output.append(", ");
            _output.append(y);
            _output.append(")\n");
            break;

        case TouchEvent.UP:
            _output.append("UP at (");
            _output.append(x);
            _output.append(", ");
            _output.append(y);
            _output.append(")\n");
            break;

        case TouchEvent.CLICK:
            _output.append("CLICK at (");
            _output.append(x);
            _output.append(", ");
            _output.append(y);
            _output.append(")\n");
            return super.touchEvent(message);

        case TouchEvent.UNCLICK:
            _output.append("UNCLICK at (");
            _output.append(x);
            _output.append(", ");
            _output.append(y);
            _output.append(")\n");
            return super.touchEvent(message);

        case TouchEvent.MOVE:
            final int time = message.getTime();
            _output.append("MOVE at (");
            _output.append(x);
            _output.append(", ");
            _output.append(y);
            _output.append(") at time = ");
            _output.append(time);
            _output.append("\n");
            break;

        case TouchEvent.GESTURE:
            final TouchGesture gesture = message.getGesture();
            switch (gesture.getEvent()) {
            case TouchGesture.SWIPE:
                _output.append("The screen was swiped.\n");
                break;

            case TouchGesture.TAP:
                _output.append("Screen tapped at (");
                _output.append(x);
                _output.append(", ");
                _output.append(y);
                _output.append(")\n");
                break;

            default:
                return false;
            }

        default:
            return false;
        }

        // We've consumed the event
        return true;
    }
}
