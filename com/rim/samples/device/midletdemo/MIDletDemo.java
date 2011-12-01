/**
 * MIDletDemo.java
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

package com.rim.samples.device.midletdemo;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;

/**
 * An Example MIDlet. The application must extend the MIDlet class to allow the
 * application management software to control the MIDlet.
 */
public class MIDletDemo extends MIDlet implements CommandListener {
    private final Alert _alert;
    private final int _time;
    private final Form _form;
    private final Display _display;
    private UpdateThread _updateThread;

    /**
     * The thread that updates the explosion dialog box.
     */
    private class UpdateThread extends Thread {
        private boolean _disarmed;

        public void run() {
            _disarmed = false;
            int i = _time;
            while (i > 0 && !_disarmed) {
                try {
                    _alert.setString(Integer.toString(i));
                    synchronized (this) {
                        this.wait(1000);
                    }

                    System.out.println("timeout in:" + i);
                } catch (final InterruptedException e) {
                    System.out.println("MyMidlet: Exception: " + e);
                }

                i--;
            }

            if (!_disarmed) {
                _alert.setString("BOOM");
            }
        }

        public void disarm() {
            _disarmed = true;
        }
    }

    /**
     * Thread that pops up the program's main dialog box.
     */
    private class GoCommand extends Command implements Runnable {
        public GoCommand(final String label, final int type, final int priority) {
            super(label, type, priority);
        }

        public void run() {
            _alert.setString(Integer.toString(_time));
            _alert.setTimeout(_time * 1000 + 5000);

            _updateThread = new UpdateThread();
            _updateThread.start();
            _display.setCurrent(_alert, _form);
        }
    }

    /**
     * <p>
     * The default constructor. Creates a simple screen and a command with an
     * alert dialog box which pops up when the command is selected.
     */
    public MIDletDemo() {
        _alert =
                new Alert(
                        "The Thermonuclear Device has been activated!\nTo disarm the device, dismiss this Alert.\nDevice will detonate in:");
        _alert.setCommandListener(this);
        _time = 10;

        // Create a simple screen.
        _form = new Form("Thermo-Nuclear Event");
        _form.append("Choose 'Go' from the menu.");
        _display = Display.getDisplay(this);

        // Add our command.
        _form.addCommand(new GoCommand("Go", Command.SCREEN, 1));

        _form.setCommandListener(this);
        _display.setCurrent(_form);
    }

    public void commandAction(final Command c, final Displayable s) {
        if (c instanceof Runnable) {
            ((Runnable) c).run();
        }

        if (c == Alert.DISMISS_COMMAND) {
            _updateThread.disarm();
        }
    }

    /**
     * <p>
     * Signals the MIDlet that it has entered the Active state.
     */
    public void startApp() {
        // Not implemented.
    }

    /**
     * <p>
     * Signals the MIDlet to stop and enter the Pause state.
     */
    public void pauseApp() {
        // Not implemented.
    }

    /**
     * <p>
     * Signals the MIDlet to terminate and enter the Destroyed state.
     * 
     * @param unconditional
     *            When set to true, the MIDlet must cleanup and release all
     *            resources. Otherwise, the MIDlet may throw a
     *            MIDletStateChangeException to indicate it does not want to be
     *            destroyed at this time.
     */
    public void destroyApp(final boolean unconditional) {
        // Not implemented.
    }
}
