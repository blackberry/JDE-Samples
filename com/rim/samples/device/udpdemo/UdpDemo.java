/*
 * UDPDemo.java
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

package com.rim.samples.device.udpdemo;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringProvider;

/**
 * Sample application to demonstrate client/server UDP capability. Run this
 * application after executing run.bat in the com.rim.samples.server.udpdemo
 * directory.
 */
public final class UDPDemo extends UiApplication {
    private final UdpDemoScreen _screen;

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final UDPDemo theApp = new UDPDemo();
        theApp.enterEventDispatcher();
    }

    /**
     * Creates a new UDPDemo object
     */
    public UDPDemo() {
        _screen = new UdpDemoScreen();
        pushScreen(_screen);
    }

    /**
     * Provides access to the UI screen
     * 
     * @return The UI screen
     */
    UdpDemoScreen getScreen() {
        return _screen;
    }
}

/**
 * MainScreen class for this application
 */
final class UdpDemoScreen extends MainScreen implements FieldChangeListener {
    private final EditField _messageField;
    private final LabelField _statusField;
    private final ButtonField _sendButton;
    private final ButtonField _clearButton;
    private final StringBuffer _status;

    /**
     * Creates a new UdpDemoScreen object
     */
    UdpDemoScreen() {
        _status = new StringBuffer();

        // Initialize UI components
        setTitle("UDP Demo");

        final UdpDemoVFM verticalManager = new UdpDemoVFM();
        _messageField =
                new EditField("Type a message to send: \n", "", 256,
                        Field.USE_ALL_HEIGHT);
        verticalManager.add(_messageField);
        add(verticalManager);

        final HorizontalFieldManager hfm =
                new HorizontalFieldManager(Field.FIELD_HCENTER);
        _sendButton = new ButtonField("Send", ButtonField.CONSUME_CLICK);
        _sendButton.setChangeListener(this);
        _clearButton = new ButtonField("Clear", ButtonField.CONSUME_CLICK);
        _clearButton.setChangeListener(this);
        hfm.add(_sendButton);
        hfm.add(_clearButton);
        add(hfm);

        _statusField = new LabelField();
        add(_statusField);

        // This menu item provides an alternative to clicking the send button
        final MenuItem sendItem =
                new MenuItem(new StringProvider("Send"), 0x230010, 0);
        sendItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                handleSend();
            }
        }));

        // This menu item provides an alternative to clicking the clear button
        final MenuItem clearItem =
                new MenuItem(new StringProvider("Clear"), 0x230020, 0);
        clearItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                handleClear();
            }
        }));

        addMenuItem(sendItem);
        addMenuItem(clearItem);
    }

    /**
     * A customized VerticalFieldManager
     */
    private static class UdpDemoVFM extends VerticalFieldManager {
        /**
         * Creates a new UdpDemoVFM object
         */
        UdpDemoVFM() {
            super(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);
        }

        /**
         * @see net.rim.device.api.ui.container.VerticalFieldManager#sublayout(int,int)
         */
        protected void sublayout(final int width, final int height) {
            // Prevent size of text area changing when typing on a new line
            final Field field = getField(0);
            layoutChild(field, Display.getWidth(), 100);
            setPositionChild(field, 0, 0);
            setExtent(Display.getWidth(), 100);
        }
    }

    /**
     * Initializes a new client thread
     */
    private void createClient() {
        // The UDPClient class needs to be run in a separate thread as
        // blocking operations are not permitted on event dispatch thread.
        final UDPClient client = new UDPClient(_messageField.getText());
        client.start();
    }

    /**
     * @see net.rim.device.api.ui.MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Supress the save dialog
        return true;
    }

    /**
     * @see FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field == _sendButton) {
            handleSend();
        }
        if (field == _clearButton) {
            handleClear();
        }
    }

    /**
     * Handles a send button or send menu item click
     */
    private void handleSend() {
        if (_messageField.getTextLength() > 0) {
            createClient();
            _messageField.setText("");
        } else {
            Dialog.alert("Please type a message");
        }

        _messageField.setFocus();
    }

    /**
     * Handles a clear button or clear menu item click
     */
    private void handleClear() {
        _statusField.setText("");
        _messageField.setText("");
        _status.setLength(0);
        _messageField.setFocus();
    }

    /**
     * Updates the status field
     * 
     * @param text
     *            The text with which to update the status field
     */
    void updateStatus(final String text) {
        _status.append(text + '\n');
        _statusField.setText(_status.toString());
    }

    /**
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    protected boolean invokeAction(final int action) {
        final boolean handled = super.invokeAction(action);

        if (!handled) {
            if (action == ACTION_INVOKE) {
                // Suppress the menu
                return true;
            }
        }

        return handled;
    }
}
