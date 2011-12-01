/*
 * MMSDemoSendScreen.java
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

import javax.wireless.messaging.MessagePart;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * The client screen for the MMS Demo
 */
public final class MMSDemoSendScreen extends MainScreen implements
        ListFieldCallback {
    private final EditField _subjectField;
    private final EditField _messageField;
    private final EditField _addressField;
    private final EditField _status;
    private final MMSDemo _app;
    private final ListField _attachmentList;

    private static final int MAX_PHONE_NUMBER_LENGTH = 30;

    /**
     * Constructs a new MMSDemoSendScreen object
     * 
     * @param app
     *            The MMSDemo application instance
     */
    public MMSDemoSendScreen(final MMSDemo app) {
        _app = app;

        // Initialize UI components
        setTitle("MMS Demo");
        _addressField =
                new EditField("Destination:", "", MAX_PHONE_NUMBER_LENGTH,
                        BasicEditField.FILTER_PHONE);
        add(_addressField);
        add(new SeparatorField());
        _subjectField = new EditField("Subject:", "");
        add(_subjectField);
        _messageField = new EditField("Message:", "");
        add(_messageField);
        add(new SeparatorField());
        final LabelField attachmentText = new LabelField("Attachments");
        add(attachmentText);
        _attachmentList = new ListField();
        _attachmentList.setCallback(this);
        add(_attachmentList);
        add(new SeparatorField());
        _status = new EditField();
        add(_status);

        // Add menu items to this screen
        addMenuItem(_sendMenuItem);
        addMenuItem(_attachPicture);
        addMenuItem(_attachAudio);
    }

    // Menu items --------------------------------------------------------------
    private final MenuItem _attachPicture = new MenuItem("Attach Picture",
            65536, 0) {
        public void run() {
            _app.attach(MMSDemo.PICTURE);
        }
    };

    private final MenuItem _attachAudio =
            new MenuItem("Attach Audio", 65536, 0) {
                public void run() {
                    _app.attach(MMSDemo.AUDIO);
                }
            };

    private final MenuItem _sendMenuItem = new MenuItem("Send", 0, 0) {
        public void run() {
            // Send MMS on non-event thread
            final Thread t = new Thread() {
                public void run() {
                    _app.sendMMS(_addressField, _subjectField, _messageField);
                }
            };
            t.start();
        }
    };

    /**
     * Removes an attachment from the attachment list
     */
    private final MenuItem _removeAttachment = new MenuItem(
            "Remove Attachment", 65536, 0) {
        public void run() {
            _app.getMessageParts().removeElementAt(
                    _attachmentList.getSelectedIndex());
            updateScreen();
        }
    };

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Prevent the save dialog from being displayed
        return true;
    }

    /**
     * @see Screen#close()
     */
    public void close() {
        _app.close();
        super.close();
    }

    /**
     * @see MainScreen#makeMenu(Menu, int)
     */
    protected void makeMenu(final Menu menu, final int context) {
        if (_attachmentList.getSize() > 0) {
            menu.add(_removeAttachment);
        }
        super.makeMenu(menu, context);
    }

    /**
     * Updates the screen with the new list of attachments
     */
    public void updateScreen() {
        _attachmentList.setSize(_app.getMessageParts().size());
        this.invalidate();
    }

    // ListFieldCallback methods
    // ------------------------------------------------
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
     */
    public void drawListRow(final ListField listField, final Graphics graphics,
            final int index, final int y, final int width) {
        if (listField == _attachmentList
                && index < _app.getMessageParts().size()) {
            final MessagePart m =
                    (MessagePart) _app.getMessageParts().elementAt(index);
            final String name = m.getContentLocation();
            graphics.drawText(name, 0, y, 0, width);
        }
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField ,
     *      int)
     */
    public Object get(final ListField listField, final int index) {
        if (listField == _attachmentList) {
            // If index is out of bounds an exception will be thrown, but
            // that's the behaviour we want in that case.
            return _app.getMessageParts().elementAt(index);
        }

        return null;
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(final ListField listField) {
        // Use entire screen width
        return Display.getWidth();
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField
     *      , String , int)
     */
    public int indexOfList(final ListField listField, final String prefix,
            final int start) {
        return -1; // Not implemented
    }
}
