/*
 * MMSDemoReceiveScreen.java
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

package com.rim.samples.device.mmsdemo;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This screen class displays the message content of a received MMS message
 */
public final class MMSDemoReceiveScreen extends MainScreen {
    private final RichTextField _statusField;
    private final BasicEditField _subjectField;
    private final BasicEditField _messageField;
    private final BitmapField _bitmapField;

    /**
     * Creates a new MMSDemoServerScreen object
     */
    public MMSDemoReceiveScreen() {
        setTitle("MMS Demo");
        _statusField = new RichTextField("Waiting...");
        add(_statusField);
        add(new SeparatorField());
        _subjectField = new BasicEditField("Subject: ", "");
        add(_subjectField);
        _messageField = new BasicEditField("Message: ", "");
        add(_messageField);
        _bitmapField = new BitmapField();
        add(_bitmapField);
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Prevent the save dialog from being displayed
        return true;
    }

    /**
     * Updates the status field
     * 
     * @param text
     *            The text to display in the status field
     */
    void setStatus(final String text) {
        _statusField.setText(text);
    }

    /**
     * Updates the subject field
     * 
     * @param text
     *            The text to display in the subject field
     */
    void setSubject(final String text) {
        _subjectField.setText(text);
    }

    /**
     * Updates the message field
     * 
     * @param text
     *            The text to display in the message field
     */
    void setMessage(final String text) {
        _messageField.setText(text);
    }

    /**
     * Updates the Bitmap field
     * 
     * @param bitmap
     *            The Bitmap to display in the Bitmap field
     */
    void updateBitmapField(final Bitmap bitmap) {
        _bitmapField.setBitmap(bitmap);
    }
}
