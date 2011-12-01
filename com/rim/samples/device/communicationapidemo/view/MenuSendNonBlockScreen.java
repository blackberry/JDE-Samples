/*
 * MenuSendNonBlockScreen.java
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
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

import com.rim.samples.device.communicationapidemo.ui.FullWidthButton;

public final class MenuSendNonBlockScreen extends MainScreen {
    /**
     * Create a new MenuSendNonBlockScreen object
     */
    public MenuSendNonBlockScreen(final MenuManager menuManager) {
        setTitle("Non-Blocking");

        final FullWidthButton atomBtn = new FullWidthButton("Atom");
        final FullWidthButton jsonBtn = new FullWidthButton("JSON");
        final FullWidthButton rssBtn = new FullWidthButton("RSS");
        final FullWidthButton soapBtn = new FullWidthButton("SOAP");
        final FullWidthButton xmlBtn = new FullWidthButton("XML");

        atomBtn.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                menuManager.showSendNonBlockAtomScreen();
            }
        });

        jsonBtn.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                menuManager.showSendNonBlockJsonScreen();
            }
        });

        rssBtn.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                menuManager.showSendNonBlockRssScreen();
            }
        });

        soapBtn.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                menuManager.showSendNonBlockSoapScreen();
            }
        });

        xmlBtn.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                menuManager.showSendNonBlockXmlScreen();
            }
        });

        final LabelField instructions =
                new LabelField("Request and parse data from echo server.",
                        Field.NON_FOCUSABLE);

        // Add UI components to screen
        add(atomBtn);
        add(jsonBtn);
        add(rssBtn);
        add(soapBtn);
        add(xmlBtn);
        add(new SeparatorField());
        add(instructions);
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }
}
