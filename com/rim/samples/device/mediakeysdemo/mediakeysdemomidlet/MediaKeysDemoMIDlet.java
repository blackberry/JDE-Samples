/*
 * MediaKeysDemoMIDlet.java
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

package com.rim.samples.device.mediakeysdemo.mediakeysdemomidlet;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Controllable;
import javax.microedition.midlet.MIDlet;

import net.rim.device.api.lcdui.control.MediaBehaviourControl;

/**
 * This application demonstrates the use of the Media Keys API in a MIDlet. When
 * 'media player mode' is enabled, the Canvas is notified of media key events
 * even while the MIDlet is in the background. When a media key press is
 * received, the key's name along with the key event are displayed in the UI and
 * a corresponding line is printed to standard output. The initial state of
 * media player mode, which is 'disabled' by default, is overridden by the
 * RIM-MIDlet-MediaPlayerModeEnabled attribute in the JAD file (found in the
 * project directory), and can be toggled at runtime by clicking the checkbox in
 * the application's main screen. Some examples of “media keys” are volume,
 * mute, and forward/backward keys.
 * 
 * Steps: 1. Press the media keys while the MIDlet is in the foreground. Notice
 * that the key's name is displayed on the screen and also printed to standard
 * output. 2. With the 'media player mode' checkbox checked, task-switch to
 * another application and press the same keys. Notice that the media key
 * presses are printed to standard output, just as if the MIDlet were in the
 * foreground. 3. Uncheck the “media player mode” checkbox and task-switch to
 * another application. Notice that now when media keys are pressed, no output
 * is printed to standard output because the MIDlet is no longer being notified.
 * 4. Edit the MediaKeysMIDlet.jad file, located under the project directory, in
 * the IDE or a text editor and change the value of the attribute called
 * RIM-MIDlet-MediaPlayerModeEnabled and re-compile. Notice that when the
 * attribute is missing or its value is set to zero that media player mode is
 * initially disabled, while it is enabled if its value is set to one.
 */
public final class MediaKeysDemoMIDlet extends MIDlet implements
        CommandListener {
    private final MediaBehaviourControl _mediaBehaviourControl;

    // Checks if the user has the key held, to avoid repetition
    private boolean _isPressed = false;

    private final Command _mainExit;

    /**
     * Creates a new MediaKeysDemoMIDlet object
     */
    public MediaKeysDemoMIDlet() {
        final Display display = Display.getDisplay(this);

        final Controllable controllable = (Controllable) display;

        final String mbcName = MediaBehaviourControl.class.getName();
        this._mediaBehaviourControl =
                (MediaBehaviourControl) controllable.getControl(mbcName);

        if (this._mediaBehaviourControl != null) {
            System.out.println("Media Player Mode Enabled Initially: "
                    + this._mediaBehaviourControl.isMediaPlayerModeEnabled());
        } else {
            System.out.println("WARNING: unable to find control: " + mbcName);
        }

        // Display canvas
        final MyCanvas canvas = new MyCanvas();
        display.setCurrent(canvas);

        // Add close command
        _mainExit = new Command("Close", Command.EXIT, 1);
        canvas.addCommand(_mainExit);
        canvas.setCommandListener(this);
    }

    /**
     * Command listener implementation
     * 
     * @param c
     *            The menu item clicked
     * @param d
     *            The current displayable
     * @see javax.microedition.lcdui.CommandListener#commandAction(Command,
     *      Displayable)
     */
    public void commandAction(final Command c, final Displayable d) {
        if (c == _mainExit) {
            notifyDestroyed();
        }
    }

    /**
     * @see javax.microedition.midlet.MIDlet
     */
    protected void destroyApp(final boolean unconditional) {
        // Not implemented
    }

    /**
     * @see javax.microedition.midlet.MIDlet
     */
    protected void pauseApp() {
        // Not implemented
    }

    /**
     * @see javax.microedition.midlet.MIDlet
     */
    protected void startApp() {
        // Not implemented
    }

    /**
     * Checks if a key is a media key
     * 
     * @param keyCode
     *            The key code of the key to verify
     * @return True if keyCode represents a media key, false otherwise
     */
    private static boolean isMediaKey(final int keyCode) {
        switch (keyCode) {
        case MediaKeysMIDletConstants.VOLUME_UP:
        case MediaKeysMIDletConstants.VOLUME_DOWN:
        case MediaKeysMIDletConstants.MUTE:
        case MediaKeysMIDletConstants.FORWARD:
        case MediaKeysMIDletConstants.BACKWARD:
        case MediaKeysMIDletConstants.PLAY:
            return true;
        }
        return false;
    }

    /**
     * This class handles the UI of the Midlet
     */
    private class MyCanvas extends Canvas {
        private String _displayText = "";

        /**
         * @see javax.microedition.lcdui.Canvas#keyPressed(int)
         */
        protected void keyPressed(final int keyCode) {
            if (MediaKeysDemoMIDlet.isMediaKey(keyCode)) {
                this.showKey("Key Pressed", keyCode);
            }

            // Allows user to toggle the 'media player mode' by clicking the
            // trackball.
            if (keyCode == MediaKeysMIDletConstants.TRACKBALL
                    && _mediaBehaviourControl != null) {
                final boolean enabled =
                        !_mediaBehaviourControl.isMediaPlayerModeEnabled();
                _mediaBehaviourControl.setMediaPlayerModeEnabled(enabled);
                System.out.println("Setting media player mode enabled: "
                        + enabled);
            }
            repaint();

            super.keyPressed(keyCode);
        }

        /**
         * @see javax.microedition.lcdui.Canvas#pointerPressed(int, int)
         */
        protected void pointerPressed(final int x, final int y) {
            if (_mediaBehaviourControl != null) {
                final boolean enabled =
                        !_mediaBehaviourControl.isMediaPlayerModeEnabled();
                _mediaBehaviourControl.setMediaPlayerModeEnabled(enabled);
                System.out.println("Setting media player mode enabled: "
                        + enabled);
            }
            repaint();

            super.pointerPressed(x, y);
        }

        /**
         * @see javax.microedition.lcdui.Canvas#keyReleased(int)
         */
        protected void keyReleased(final int keyCode) {
            if (MediaKeysDemoMIDlet.isMediaKey(keyCode)) {
                this.showKey("Key Released", keyCode);
            }
            _isPressed = false;
            repaint();

            super.keyReleased(keyCode);
        }

        /**
         * @see javax.microedition.lcdui.Canvas#keyRepeated(int)
         */
        protected void keyRepeated(final int keyCode) {
            if (!_isPressed && MediaKeysDemoMIDlet.isMediaKey(keyCode)) {
                this.showKey("Key Held", keyCode);
            }
            _isPressed = true;
            repaint();

            super.keyRepeated(keyCode);
        }

        /**
         * @see javax.microedition.lcdui.Canvas#paint(Graphics)
         */
        protected void paint(final Graphics graphics) {
            // Clear the screen
            graphics.setColor(0xFFFFFF);
            graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
            graphics.setColor(0);

            // Title
            final String titleText = "Media Keys Midlet";
            final char[] titleChars = titleText.toCharArray();
            graphics.drawChars(titleChars, 0, titleChars.length, 0, 0,
                    Graphics.TOP | Graphics.LEFT);

            // Draw checkbox
            final int xBox = 5;
            final int yBox =
                    graphics.getFont().getHeight()
                            + graphics.getFont().getHeight() / 4;
            final int heightBox = graphics.getFont().getHeight() / 2;
            final int widthBox = heightBox;

            graphics.drawRect(xBox, yBox, widthBox, heightBox);

            // Draw cross
            if (_mediaBehaviourControl != null
                    && _mediaBehaviourControl.isMediaPlayerModeEnabled()) {
                graphics.drawLine(xBox, yBox, xBox + widthBox, yBox + heightBox);
                graphics.drawLine(xBox + widthBox, yBox, xBox, yBox + heightBox);
            }

            // Options
            final String optionText = "Media Player Mode Enabled";
            final char[] optionChars = optionText.toCharArray();

            graphics.drawChars(optionChars, 0, optionChars.length, xBox * 2
                    + widthBox, graphics.getFont().getHeight(), Graphics.TOP
                    | Graphics.LEFT);

            // Current action text
            final char[] displayChars = _displayText.toCharArray();

            graphics.setColor(255);
            graphics.drawChars(displayChars, 0, displayChars.length, 0,
                    graphics.getFont().getHeight() * 3, Graphics.BOTTOM
                            | Graphics.LEFT);
            graphics.setColor(0);

            // The instruction text
            final String instructionsTest =
                    "Try pressing a media key such as the mute key or the volume key. You can toggle the media player"
                            + " mode by clicking the screen or the trackball. If media player mode is enabled, try pressing the media keys while the"
                            + " application is in the background (Menu > Switch Application) and checking standard output with the debugger attached.";
            final char[] instructionsChars = instructionsTest.toCharArray();

            int ycoordinate = graphics.getFont().getHeight() * 4;

            final String[] t = textWrap(instructionsChars, graphics.getFont());
            for (int i = 0; i < t.length; i++) {
                final char[] tc = t[i].toCharArray();
                graphics.drawChars(tc, 0, tc.length, 0, ycoordinate,
                        Graphics.BOTTOM | Graphics.LEFT);
                ycoordinate += graphics.getFont().getHeight();
            }

        }

        /**
         * Creates the text to be displayed on screen and prints the key pressed
         * to standard output.
         * 
         * @param methodName
         *            The method that called showKey
         * @param keyCode
         *            The key that was pressed
         */
        private void showKey(final String methodName, final int keyCode) {
            _displayText = methodName + "(" + this.getKeyName(keyCode) + ")";
            System.out.println(_displayText);
            repaint();
        }

        /**
         * Word wraps the text by splitting it up into an array of Strings each
         * of which are less than the screen width.
         * 
         * @param textToWrap
         *            The text that must be wrapped
         * @param f
         *            The font being used
         * @return A Vector of String objects
         */
        private String[] textWrap(final char[] textToWrap, final Font f) {
            int currentIndex = 0;
            int currentWidth = 0;
            final int maxWidth = this.getWidth();
            final StringBuffer text = new StringBuffer();

            final Vector textVector = new Vector();

            while (currentIndex < textToWrap.length) {
                int indexOfLastSpace = -1;
                int currentStringIndex = 0;

                while (currentWidth < maxWidth
                        && currentIndex < textToWrap.length) {
                    final char c = textToWrap[currentIndex];
                    currentWidth += f.charWidth(c);
                    if (c == ' ') {
                        indexOfLastSpace = currentStringIndex + 1;
                    }
                    text.append(c);
                    currentIndex++;
                    currentStringIndex++;
                }

                if (indexOfLastSpace == -1 || currentIndex == textToWrap.length) {
                    textVector.addElement(text.toString());
                } else {
                    text.setLength(indexOfLastSpace);
                    textVector.addElement(text.toString());
                    currentIndex =
                            currentIndex
                                    - (currentStringIndex - indexOfLastSpace);
                }
                text.setLength(0);
                currentWidth = 0;
            }

            final String[] textArray = new String[textVector.size()];
            textVector.copyInto(textArray);
            return textArray;
        }
    }
}
