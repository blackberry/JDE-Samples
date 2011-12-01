/**
 * MenuSendScreen.java
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

public final class MenuSendScreen extends MainScreen {
    private final LabelField _instructions;

    /**
     * Creates a new MenuSendScreen object
     */
    public MenuSendScreen(final MenuManager menuManager) {
        setTitle("Send Messages");

        // Initialize UI components
        _instructions = new LabelField("", Field.NON_FOCUSABLE);

        final FullWidthButton fireForgetSendButton =
                new FullWidthButton("Fire-And-Forget");
        fireForgetSendButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                menuManager.showSendFireForgetScreen();
            }
        });

        fireForgetSendButton.setFocusListener(new FocusChangeListener() {
            /**
             * @see FocusChangeListener#focusChanged(Field, int)
             */
            public void focusChanged(final Field field, final int eventType) {
                setInstructions("Send messages using fire and forget destination.");

            }
        });

        final FullWidthButton nonBlockSendButton =
                new FullWidthButton("Non-Blocking");
        nonBlockSendButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                menuManager.showMenuSendNonBlockScreen();
            }
        });

        nonBlockSendButton.setFocusListener(new FocusChangeListener() {
            /**
             * @see FocusChangeListener#focusChanged(Field, int)
             */
            public void focusChanged(final Field field, final int eventType) {
                setInstructions("Send messages using non-blocking destination. Message parsing examples are also included.");
            }
        });

        final FullWidthButton blockSendButton = new FullWidthButton("Blocking");
        blockSendButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                menuManager.showSendBlockScreen();
            }
        });

        blockSendButton.setFocusListener(new FocusChangeListener() {
            /**
             * @see FocusChangeListener#focusChanged(Field, int)
             */
            public void focusChanged(final Field field, final int eventType) {
                setInstructions("Send messages using blocking destination.");

            }
        });

        // Add UI components to screen
        add(fireForgetSendButton);
        add(nonBlockSendButton);
        add(blockSendButton);
        add(new SeparatorField());
        add(_instructions);
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Suppress save dialog
        return true;
    }

    private void setInstructions(final String instruction) {
        _instructions.setText(instruction);
    }
}
