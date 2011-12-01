/*
 * MenuReceiveScreen.java
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

package com.rim.samples.device.communicationapidemo.view;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

import com.rim.samples.device.communicationapidemo.ui.FullWidthButton;

public final class MenuReceiveScreen extends MainScreen {
    private final LabelField _instructions;

    /**
     * Creates a new MenuReceiveScreen object
     */
    public MenuReceiveScreen(final MenuManager menuManager) {
        setTitle("Receive Messages");

        // Initialize UI components
        _instructions = new LabelField("", Field.NON_FOCUSABLE);

        final FullWidthButton testButton = new FullWidthButton("BES Push");
        testButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                menuManager.showReceivePushScreen();
            }
        });

        testButton.setFocusListener(new FocusChangeListener() {
            /**
             * @see FocusChangeListener#focusChanged(Field, int)
             */
            public void focusChanged(final Field field, final int eventType) {
                setInstructions("Receive messages sent from external push server through BES.");

            }
        });

        final FullWidthButton ipcButton =
                new FullWidthButton("IPC/Local Receiver");
        ipcButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                menuManager.showReceiveIPCScreen();
            }
        });

        ipcButton.setFocusListener(new FocusChangeListener() {
            /**
             * @see FocusChangeListener#focusChanged(Field, int)
             */
            public void focusChanged(final Field field, final int eventType) {
                setInstructions("Receive messages sent from other BlackBerry Smartphone device applications using IPC(Inter-Process Communication).");
            }
        });

        final FullWidthButton bpsButton = new FullWidthButton("BPS Push");
        bpsButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                menuManager.showReceiveBPSScreen();
            }
        });

        bpsButton.setFocusListener(new FocusChangeListener() {
            /**
             * @see FocusChangeListener#focusChanged(Field, int)
             */
            public void focusChanged(final Field field, final int eventType) {
                setInstructions("Test BPS subscription process. For receiving pushes, this test has to be run on a BlackBerry Smartphone device (will not work on a BlackBerry Smartphone simulator).");
            }
        });

        // Add components to screen
        add(testButton);
        add(ipcButton);
        add(bpsButton);
        add(new SeparatorField());
        add(_instructions);
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }

    public void setInstructions(final String instruction) {
        _instructions.setText(instruction);
    }

}
