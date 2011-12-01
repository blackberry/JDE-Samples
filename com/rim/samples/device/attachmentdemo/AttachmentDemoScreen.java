/*
 * AttachmentDemoScreen.java
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

package com.rim.samples.device.attachmentdemo;

import java.io.IOException;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * The main screen class for the Attachment Demo sample application
 */
public final class AttachmentDemoScreen extends MainScreen {
    private final LabelField _statusField;
    private final AttachmentAction _action;
    private final UiApplication _app;

    /**
     * Creates a new AttachmentDemoScreen object
     * 
     * @param app
     *            Reference to the UiApplication instance
     */
    public AttachmentDemoScreen(final UiApplication app) {
        _app = app;
        _action = new AttachmentAction(this);

        // Initialize UI
        setTitle("Attachment Demo");
        _statusField =
                new LabelField(
                        "Open the menu to download or upload attachments",
                        Field.NON_FOCUSABLE);
        add(_statusField);
        addMenuItem(_downloadItem);
        addMenuItem(_uploadItem);
    }

    /**
     * Method to update this screen's status field
     * 
     * @param msg
     *            The text to display
     */
    public void displayStatus(final String msg) {
        _app.invokeLater(new Runnable() {
            public void run() {
                _statusField.setText(_statusField.getText() + '\n' + msg);
            }
        });
    }

    // Menu items --------------------------------------------------------------
    MenuItem _downloadItem = new MenuItem("Download Attachments", 110, 10) {
        public void run() {
            _statusField.setText("");
            if (_action.getMessages()) {
                try {
                    _action.download(Dialog.ask(Dialog.D_YES_NO,
                            "Download only png and msword attachments?") == Dialog.NO);
                } catch (final IOException ex) {
                    AttachmentDemo
                            .errorDialog("DownloadManager.download() threw "
                                    + ex.toString());
                }
            } else {
                displayStatus("Mailbox is empty.");
            }
        }
    };

    MenuItem _uploadItem = new MenuItem("Send Attachment", 110, 10) {
        public void run() {
            _statusField
                    .setText("Open the menu to download or upload attachments");
            _app.pushScreen(new FileExplorerScreen(_app));
        }
    };
}
