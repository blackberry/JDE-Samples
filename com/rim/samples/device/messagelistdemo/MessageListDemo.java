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

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.blackberry.api.messagelist.ApplicationIcon;
import net.rim.blackberry.api.messagelist.ApplicationIndicatorRegistry;
import net.rim.blackberry.api.messagelist.ApplicationMessage;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolder;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolderRegistry;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.image.Image;
import net.rim.device.api.ui.image.ImageFactory;
import net.rim.device.api.util.StringProvider;

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
public final class MessageListDemo extends UiApplication {
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

    // All our messages are received, we don't show sent messages
    static final int BASE_STATUS = ApplicationMessage.Status.INCOMING;

    static final int STATUS_NEW = BASE_STATUS
            | ApplicationMessage.Status.UNOPENED;
    static final int STATUS_OPENED = BASE_STATUS
            | ApplicationMessage.Status.OPENED;
    static final int STATUS_REPLIED = BASE_STATUS
            | ApplicationMessage.Status.OPENED | FLAG_REPLIED;
    static final int STATUS_DELETED = BASE_STATUS | FLAG_DELETED;

    // Constant to define number of bulk messages
    static final int MAX_MSGS = 50;

    private CommunicationSimulatorThread _commThread;

    /**
     * Entry point for application.
     * 
     * @param args
     *            Command line arguments
     */
    public static void main(final String[] args) {
        if (args != null && args.length > 0) {
            // Perform initialization on device startup
            if (args.length == 1 && args[0].equals("startup")) {
                final MessageListDemoDaemon daemon =
                        new MessageListDemoDaemon();

                // Register application indicator
                final EncodedImage indicatorIcon =
                        EncodedImage
                                .getEncodedImageResource("img/indicator.png");
                final ApplicationIcon applicationIcon =
                        new ApplicationIcon(indicatorIcon);
                ApplicationIndicatorRegistry.getInstance().register(
                        applicationIcon, false, false);

                // Check if this application registered folders already
                final ApplicationMessageFolderRegistry reg =
                        ApplicationMessageFolderRegistry.getInstance();
                if (reg.getApplicationFolder(INBOX_FOLDER_ID) == null) {
                    // Register folders & message types and initialize folders
                    // with data. Normally the data would come from from a mail
                    // server or persistent store.
                    daemon.init();
                }

                // This daemon application will be responsible for
                // listening for notifications and menu actions, it runs until
                // the device shuts down or the application is uninstalled.
                daemon.enterEventDispatcher();

            } else if (args.length == 1 && args[0].equals("gui")) {
                // Create a GUI instance for displaying a DemoMessageScreen.
                // This will occur when this application is invoked by the
                // View Demo Message menu item.
                final MessageListDemo messageScreenApp =
                        new MessageListDemo(false);
                messageScreenApp.enterEventDispatcher();

            }
        } else {
            // Create an instance of the main GUI application. This occurs
            // when the application is launched from home screen.
            final MessageListDemo mainGuiApp = new MessageListDemo(true);
            mainGuiApp.enterEventDispatcher();
        }
    }

    /**
     * Creates a new MessageListDemo object
     * 
     * @param isMainGui
     *            True if constructor should create GUI, false otherwise
     */
    public MessageListDemo(final boolean isMainGui) {
        if (isMainGui) {
            final MainScreen mainScreen = new MainScreen();
            mainScreen.setTitle("Message List Demo");
            mainScreen
                    .add(new RichTextField(
                            "Please choose one of the menu options, then open the Messages application from the home "
                                    + "screen to manipulate Message List Demo messages. You can open, reply to, mark opened and unopened, delete and search "
                                    + "for messages in the list."));
            addMenuItems(mainScreen);
            pushScreen(mainScreen);
        }
    }

    /**
     * Adds a menu which allows a user to perform various message store actions
     * 
     * @param mainScreen
     *            The screen to add the menu to
     */
    void addMenuItems(final MainScreen mainScreen) {
        final MenuItemWithIcon appendUnreadMessage =
                new MenuItemWithIcon("Append Unread Message", 0,
                        "img/sm_add_message.png");
        appendUnreadMessage.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                // Create a new message
                final DemoMessage message = new DemoMessage();
                message.setSender("John Smith");
                message.setSubject("Hello from John");
                message.setMessage("Hello Chris. Do you know that you can get a BlackBerry Smartphone cheaper through the Message List Demo Store?");
                message.setReceivedTime(System.currentTimeMillis());
                message.setPreviewPicture(CommunicationSimulatorThread
                        .getRandomPhotoImage());

                // Store message
                final MessageListDemoStore messageStore =
                        MessageListDemoStore.getInstance();
                synchronized (messageStore) {
                    messageStore.addInboxMessage(message);
                }

                Dialog.alert("Unread message was added to inbox");

                // Notify folder
                final ApplicationMessageFolder inboxFolder =
                        messageStore.getInboxFolder();
                inboxFolder.fireElementAdded(message, true);
            }
        }));
        mainScreen.addMenuItem(appendUnreadMessage);

        final MenuItemWithIcon appendOpenedMessage =
                new MenuItemWithIcon("Append Opened Message", 1,
                        "img/sm_add_message.png");
        appendOpenedMessage.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                // Create a new message
                final DemoMessage message = new DemoMessage();
                message.setSender("Maria Rosevelt");
                message.setSubject("How have you been?");
                message.setMessage("Hi Chris. I haven't seen you in ages.  Let's do lunch!");
                message.setReceivedTime(System.currentTimeMillis() - 2 * 24
                        * 60 * 60 * 1000L);
                message.setPreviewPicture(CommunicationSimulatorThread
                        .getRandomPhotoImage());
                message.markRead();

                // Store message
                final MessageListDemoStore messageStore =
                        MessageListDemoStore.getInstance();
                synchronized (messageStore) {
                    messageStore.addInboxMessage(message);
                }

                Dialog.alert("Opened message was added to inbox");

                // Notify folder
                messageStore.getInboxFolder().fireElementAdded(message);
            }
        }));
        mainScreen.addMenuItem(appendOpenedMessage);

        final MenuItemWithIcon appendMessagesInBulk =
                new MenuItemWithIcon("Append Messages in Bulk", 2,
                        "sm_add_messages_bulk");
        appendMessagesInBulk.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                final MessageListDemoStore messageStore =
                        MessageListDemoStore.getInstance();
                synchronized (messageStore) {
                    for (int i = 1; i <= MAX_MSGS; i++) {
                        // Create a new message
                        final DemoMessage message = new DemoMessage();
                        message.setSender("Mark Duval");
                        message.setSubject(i + " of " + MAX_MSGS);
                        message.setMessage("Please pick up milk and bread on the way home.");
                        message.setReceivedTime(System.currentTimeMillis());
                        message.setPreviewPicture(CommunicationSimulatorThread
                                .getRandomPhotoImage());

                        // Store message in the runtime store
                        messageStore.addInboxMessage(message);
                    }

                    // Notify folder
                    final ApplicationMessageFolder inboxFolder =
                            messageStore.getInboxFolder();
                    inboxFolder.fireReset(true);
                }

                Dialog.alert("Bulk messages were added to inbox");
            }
        }));
        mainScreen.addMenuItem(appendMessagesInBulk);

        final MenuItemWithIcon startCommunicationThread =
                new MenuItemWithIcon("Start Communication Thread", 3,
                        "sm_start_auto");
        startCommunicationThread.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                if (_commThread != null) {
                    Dialog.alert("Communication thread already running");
                    return;
                }

                _commThread = new CommunicationSimulatorThread();
                _commThread.start();

                Dialog.alert("Communication thread started successfully.\n After dismissing this dialog, press the End (red phone) key and"
                        + " open the Messages application from the home screen.");
            }
        }));
        mainScreen.addMenuItem(startCommunicationThread);

        final MenuItemWithIcon stopCommunicationThread =
                new MenuItemWithIcon("Stop Communication Thread", 4,
                        "sm_stop_auto");
        stopCommunicationThread.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                if (_commThread == null) {
                    Dialog.alert("Communication thread is not running");
                    return;
                }
                _commThread.stopRunning();
                _commThread = null;
                Dialog.alert("Communication thread stopped.");
            }
        }));
        mainScreen.addMenuItem(stopCommunicationThread);

        final MenuItemWithIcon viewMessagesInMessageList =
                new MenuItemWithIcon("View Messages In Message List", 5,
                        "sm_open_messages_app");
        viewMessagesInMessageList.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                final MessageListDemoStore messageStore =
                        MessageListDemoStore.getInstance();
                final MessageArguments mArg =
                        new MessageArguments(messageStore.getInboxFolder());
                Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES, mArg);
            }
        }));
        mainScreen.addMenuItem(viewMessagesInMessageList);
    }

    /**
     * A MenuItem with an icon
     */
    private static class MenuItemWithIcon extends MenuItem {
        /**
         * Creates a new MenuItemWithIcon object
         * 
         * @param label
         *            The text to appear with the menu item
         * @param priority
         *            The priority of the menu item
         * @param iconResourcePath
         *            Location of icon resource
         */
        public MenuItemWithIcon(final String label, final int priority,
                final String iconResourcePath) {
            super(new StringProvider(label), 0x230100, priority);

            if (iconResourcePath != null) {
                // Retrieve the icon resource and add it to the menu item
                final EncodedImage encodedImage =
                        EncodedImage.getEncodedImageResource(iconResourcePath);
                if (encodedImage != null) {
                    final Image iconImage =
                            ImageFactory.createImage(encodedImage);
                    this.setIcon(iconImage);
                }
            }
        }
    }
}
