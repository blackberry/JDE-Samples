/**
 * MessageListDemo.java
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

package com.rim.samples.device.messagelistdemo;

import net.rim.blackberry.api.messagelist.ApplicationIcon;
import net.rim.blackberry.api.messagelist.ApplicationIndicatorRegistry;
import net.rim.blackberry.api.messagelist.ApplicationMessage;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolderRegistry;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Sample to demonstrate the Message List API. On device startup, two
 * ApplicationMessageFolders are registered, one for inbox messages, and one for
 * deleted messages. An ApplicationIndicator is also registered and will be
 * visible on the home screen when the inbox folder contains demo messages.
 * ApplicationMenuItems are registered which allow for the display and
 * manipulation of demo messages while in the Messages application. When this
 * sample application is invoked from the home screen, a user can invoke menu
 * items to add messages to the inbox or start a thread to add, delete and and
 * change properties of messages.
 */
final class MessageListDemo extends UiApplication {
    /* com.rim.samples.device.messagelistdemo */
    static final long KEY = 0x39d90c5bc6899541L; // Base folder key

    /* com.rim.samples.device.messagelistdemo.INBOX_FOLDER_ID */
    static final long INBOX_FOLDER_ID = 0x2fb5115c0e4a6c33L;

    /* com.rim.samples.device.messagelistdemo.DELETED_FOLDER_ID */
    static final long DELETED_FOLDER_ID = 0x78d50a91eff39e5bL;

    /**
     * Flag for replied messages. The lower 16 bits are RIM-reserved, so we have
     * to use higher 16 bits.
     */
    static final int FLAG_REPLIED = 1 << 16;

    /**
     * Flag for deleted messages. The lower 16 bits are RIM-reserved, so we have
     * to use higher 16 bits.
     */
    static final int FLAG_DELETED = 1 << 17;

    // All our messages are received, we don't show sent messages.
    static final int BASE_STATUS = ApplicationMessage.Status.INCOMING;

    static final int STATUS_NEW = BASE_STATUS
            | ApplicationMessage.Status.UNOPENED;
    static final int STATUS_OPENED = BASE_STATUS
            | ApplicationMessage.Status.OPENED;
    static final int STATUS_REPLIED = BASE_STATUS
            | ApplicationMessage.Status.OPENED | FLAG_REPLIED;
    static final int STATUS_DELETED = BASE_STATUS | FLAG_DELETED;

    // Constant to define number of bulk messages.
    static final int MAX_MSGS = 50;

    private CommunicationSimulatorThread commThread;

    /**
     * Entry point for application.
     */
    public static void main(final String[] args) {
        if (args != null && args.length > 0) {
            // Perform initialization on device startup.
            if (args.length == 1 && args[0].equals("startup")) {

                // Register application indicator.
                final EncodedImage indicatorIcon =
                        EncodedImage
                                .getEncodedImageResource("img/indicator.png");
                final ApplicationIcon applicationIcon =
                        new ApplicationIcon(indicatorIcon);
                ApplicationIndicatorRegistry.getInstance().register(
                        applicationIcon, false, false);

                final MessageListDemoDaemon daemon =
                        new MessageListDemoDaemon();
                final ApplicationMessageFolderRegistry reg =
                        ApplicationMessageFolderRegistry.getInstance();

                // Check if this application registered folders already.
                if (reg.getApplicationFolder(INBOX_FOLDER_ID) == null) {
                    // Register folders & message types and initialize folders
                    // with data. Normally the data would come from from a mail
                    // server or persistent store.
                    daemon.init();
                }

                // This daemon application will be responsible for
                // listening for notifications and menu actions, it runs until
                // the device shuts down or the app is uninstalled.
                daemon.enterEventDispatcher();

            } else if (args.length == 1 && args[0].equals("gui")) {
                // Create a GUI instance for displaying a DemoMessageScreen.
                // This will occur when our app is invoked by the
                // View Demo Message menu item.
                final MessageListDemo messageScreenApp =
                        new MessageListDemo(false);
                messageScreenApp.enterEventDispatcher();

            }
        } else {
            // Create an instance of our main GUI app. This occurs
            // when app is launched from home screen.
            final MessageListDemo mainGuiApp = new MessageListDemo(true);
            mainGuiApp.enterEventDispatcher();
        }
    }

    // Constructor
    MessageListDemo(final boolean isMainGui) {
        if (isMainGui) {
            final MainScreen mainScreen = new MainScreen();
            mainScreen.setTitle("Message List Demo");
            mainScreen
                    .add(new RichTextField(
                            "Please choose one of the menu options, then open the Messages application from the home screen to manipulate Message List Demo messages. "
                                    + "You can open, reply to, mark opened and unopened, delete and search for messages in the list."));
            addApplicationMenu(mainScreen);
            pushScreen(mainScreen);
        }
    }

    void addApplicationMenu(final MainScreen mainScreen) {
        final MenuItem appendUnreadMenuItem =
                new MenuItem("Append Unread Message", 1, 1) {
                    public void run() {
                        // Create a new message.
                        final DemoMessage message = new DemoMessage();
                        message.setSender("John Smith");
                        message.setSubject("Hello from John");
                        message.setMessage("Hello Chris. Do you know that "
                                + "you can get a BlackBerry Smartphone cheaper through the Message List Demo Store?");
                        message.setReceivedTime(System.currentTimeMillis());
                        message.setPreviewPicture(CommunicationSimulatorThread
                                .getRandomPhotoImage());

                        // Store message in the runtime store.
                        final MessageListDemoStore messageStore =
                                MessageListDemoStore.getInstance();
                        synchronized (messageStore) {
                            messageStore.addInboxMessage(message);
                        }

                        Dialog.alert("Unread message was added to inbox");

                        // Notify folder.
                        messageStore.getInboxFolder().fireElementAdded(message);
                    }
                };

        final MenuItem appendOpenedMenuItem =
                new MenuItem("Append Opened Message", 2, 2) {
                    public void run() {
                        // Create a new message.
                        final DemoMessage message = new DemoMessage();
                        message.setSender("Maria Rosevelt");
                        message.setSubject("How have you been?");
                        message.setMessage("Hi Chris. I haven't seen you in ages.  Let's do lunch!");
                        message.setReceivedTime(System.currentTimeMillis() - 2
                                * 24 * 60 * 60 * 1000L);
                        message.setPreviewPicture(CommunicationSimulatorThread
                                .getRandomPhotoImage());
                        message.markRead();

                        // Store message in the runtime store.
                        final MessageListDemoStore messageStore =
                                MessageListDemoStore.getInstance();
                        synchronized (messageStore) {
                            messageStore.addInboxMessage(message);
                        }

                        Dialog.alert("Opened message was added to inbox");

                        // Notify folder.
                        messageStore.getInboxFolder().fireElementAdded(message);
                    }
                };

        final MenuItem appendBulkMenuItem =
                new MenuItem("Append Messages in Bulk", 3, 3) {
                    public void run() {
                        final MessageListDemoStore messageStore =
                                MessageListDemoStore.getInstance();
                        synchronized (messageStore) {
                            for (int i = 1; i <= MAX_MSGS; i++) {
                                // Create a new message.
                                final DemoMessage message = new DemoMessage();
                                message.setSender("Mark Duval");
                                message.setSubject(i + " of " + MAX_MSGS);
                                message.setMessage("Please pick up milk and bread on the way home.");
                                message.setReceivedTime(System
                                        .currentTimeMillis());
                                message.setPreviewPicture(CommunicationSimulatorThread
                                        .getRandomPhotoImage());

                                // Store message in the runtime store.
                                messageStore.addInboxMessage(message);
                            }
                            Dialog.alert("Bulk messages were added to inbox");
                            // Notify folder.
                            messageStore.getInboxFolder().fireReset();
                        }
                    }
                };

        final MenuItem startCommThreadMenuItem =
                new MenuItem("Start Communication Thread", 4, 4) {
                    public void run() {
                        if (commThread != null) {
                            Dialog.alert("Communication thread already running");
                            return;
                        }

                        commThread = new CommunicationSimulatorThread();
                        commThread.start();

                        Dialog.alert("Communication thread started successfully.\n"
                                + "After dismissing this dialog, press the End (red phone) key and open the Messages application from the home screen.");
                    }
                };

        final MenuItem stopCommThreadMenuItem =
                new MenuItem("Stop Communication Thread", 5, 5) {
                    public void run() {
                        if (commThread == null) {
                            Dialog.alert("Communication thread is not running");
                            return;
                        }
                        commThread.stopRunning();
                        commThread = null;
                        Dialog.alert("Communication thread stopped.");
                    }
                };

        mainScreen.addMenuItem(appendUnreadMenuItem);
        mainScreen.addMenuItem(appendOpenedMenuItem);
        mainScreen.addMenuItem(appendBulkMenuItem);
        mainScreen.addMenuItem(startCommThreadMenuItem);
        mainScreen.addMenuItem(stopCommThreadMenuItem);
    }
}
