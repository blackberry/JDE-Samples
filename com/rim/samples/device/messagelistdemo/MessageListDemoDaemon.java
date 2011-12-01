/**
 * MessageListDemoDaemon.java
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

import java.util.Date;

import net.rim.blackberry.api.menuitem.ApplicationMenuItem;
import net.rim.blackberry.api.messagelist.ApplicationIcon;
import net.rim.blackberry.api.messagelist.ApplicationIndicator;
import net.rim.blackberry.api.messagelist.ApplicationIndicatorRegistry;
import net.rim.blackberry.api.messagelist.ApplicationMessage;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolder;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolderListener;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolderRegistry;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.UiApplication;

/**
 * Daemon process that runs in the background. It's tasks include non-gui
 * message operations such as message deletion, marking messages as read/unread
 * and synchronization with server.
 */
public final class MessageListDemoDaemon extends Application implements
        ApplicationMessageFolderListener {
    // Initialize the notification indicator
    public static ApplicationIndicator _indicator =
            ApplicationIndicatorRegistry.getInstance()
                    .getApplicationIndicator();

    /**
     * Called during device startup.
     */
    void init() {
        // 1. Register folders -------------------------------------------------

        final ApplicationMessageFolderRegistry reg =
                ApplicationMessageFolderRegistry.getInstance();

        // Some context menu items don't need GUI and will be run in the
        // current daemon application.
        final ApplicationDescriptor daemonDescr =
                ApplicationDescriptor.currentApplicationDescriptor();

        final ApplicationDescriptor guiDescr =
                new ApplicationDescriptor(ApplicationDescriptor
                        .currentApplicationDescriptor(), new String[] { "gui" });

        // Get existing messages from storage and register
        // them in folders.
        final MessageListDemoStore messages =
                MessageListDemoStore.getInstance();
        final ApplicationMessageFolder inbox =
                reg.registerFolder(MessageListDemo.INBOX_FOLDER_ID, "Inbox",
                        messages.getInboxMessages());
        final ApplicationMessageFolder deleted =
                reg.registerFolder(MessageListDemo.DELETED_FOLDER_ID,
                        "Deleted Messages", messages.getDeletedMessages(),
                        false);

        // Register ourselves as a listener for callback notifications.
        inbox.addListener(
                this,
                ApplicationMessageFolderListener.MESSAGE_DELETED
                        | ApplicationMessageFolderListener.MESSAGE_MARKED_OPENED
                        | ApplicationMessageFolderListener.MESSAGE_MARKED_UNOPENED,
                daemonDescr);
        deleted.addListener(this,
                ApplicationMessageFolderListener.MESSAGE_DELETED, daemonDescr);

        messages.setFolders(inbox, deleted);

        // We've registered two folders, let's specify root folder name for the
        // [View Folder] screen.
        reg.setRootFolderName("Message List Demo");

        // 2. Register message icons -------------------------------------------

        final ApplicationIcon newIcon =
                new ApplicationIcon(EncodedImage
                        .getEncodedImageResource("img/new.png"));
        final ApplicationIcon readIcon =
                new ApplicationIcon(EncodedImage
                        .getEncodedImageResource("img/read.png"));
        final ApplicationIcon repliedIcon =
                new ApplicationIcon(EncodedImage
                        .getEncodedImageResource("img/replied.png"));
        final ApplicationIcon deletedIcon =
                new ApplicationIcon(EncodedImage
                        .getEncodedImageResource("img/deleted.png"));

        reg.registerMessageIcon(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_NEW, newIcon);
        reg.registerMessageIcon(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_OPENED, readIcon);
        reg.registerMessageIcon(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_REPLIED, repliedIcon);
        reg.registerMessageIcon(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_DELETED, deletedIcon);

        // 3. Register message menu items --------------------------------------

        final ApplicationMenuItem openMenuItem = new OpenContextMenu(0);
        final ApplicationMenuItem replyMenuItem = new ReplyContextMenu(1);
        final ApplicationMenuItem markOpenedMenuItem =
                new MarkOpenedContextMenu(2);
        final ApplicationMenuItem markUnopenedMenuItem =
                new MarkUnreadContextMenu(3);

        final ApplicationMenuItem[] newGuiMenuItems =
                new ApplicationMenuItem[] { openMenuItem };
        final ApplicationMenuItem[] newDaemonMenuItems =
                new ApplicationMenuItem[] { markOpenedMenuItem, replyMenuItem };
        final ApplicationMenuItem[] openedGuiMenuItems =
                new ApplicationMenuItem[] { openMenuItem };
        final ApplicationMenuItem[] openedDaemonMenuItems =
                new ApplicationMenuItem[] { markUnopenedMenuItem, replyMenuItem };
        final ApplicationMenuItem[] repliedGuiMenuItems =
                new ApplicationMenuItem[] { openMenuItem };
        final ApplicationMenuItem[] repliedDaemonMenuItems =
                new ApplicationMenuItem[] { markUnopenedMenuItem };
        final ApplicationMenuItem[] deletedGuiMenuItems =
                new ApplicationMenuItem[] { openMenuItem, };

        reg.registerMessageMenuItems(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_NEW, newGuiMenuItems, guiDescr);
        reg.registerMessageMenuItems(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_NEW, newDaemonMenuItems, daemonDescr);
        reg.registerMessageMenuItems(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_OPENED, openedGuiMenuItems, guiDescr);
        reg.registerMessageMenuItems(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_OPENED, openedDaemonMenuItems,
                daemonDescr);
        reg.registerMessageMenuItems(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_REPLIED, repliedGuiMenuItems, guiDescr);
        reg.registerMessageMenuItems(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_REPLIED, repliedDaemonMenuItems,
                daemonDescr);
        reg.registerMessageMenuItems(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_DELETED, deletedGuiMenuItems, guiDescr);

        reg.setBulkMarkOperationsSupport(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_NEW, true, false);
        reg.setBulkMarkOperationsSupport(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_OPENED, false, true);
        reg.setBulkMarkOperationsSupport(DemoMessage.DEMO_MESSAGE_TYPE,
                MessageListDemo.STATUS_REPLIED, false, true);
    }

    /**
     * Change the indicator value by the specified amount
     * 
     * @param value
     *            The value that the indicator must change
     */
    public static void changeIndicator(final int value) {
        // Update indicator.
        _indicator.setValue(_indicator.getValue() + value);

        // Check if there are any new messages and show indicator accordingly
        if (_indicator.getValue() <= 0) {
            _indicator.setVisible(false);
        } else {
            _indicator.setVisible(true);
        }
    }

    /**
     * Normally the reply command would have GUI interaction, but we just
     * perform the replying logic in this sample.
     */
    private static class ReplyContextMenu extends ApplicationMenuItem {
        /**
         * Creates a new ApplicationMenuItem instance with provided menu
         * position.
         * 
         * @param order
         *            Display order of this item, lower numbers corresopnd to
         *            higher placement in the menu
         */
        public ReplyContextMenu(final int order) {
            super(order);
        }

        /**
         * Replies to the context message.
         * 
         * @see ApplicationMenuItem#run(Object)
         */
        public Object run(final Object context) {
            if (context instanceof DemoMessage) {
                final DemoMessage message = (DemoMessage) context;
                message.reply("You replied on " + new Date());
                final ApplicationMessageFolderRegistry reg =
                        ApplicationMessageFolderRegistry.getInstance();
                final ApplicationMessageFolder folder =
                        reg.getApplicationFolder(MessageListDemo.INBOX_FOLDER_ID);
                folder.fireElementUpdated(message, message);
            }
            return context;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "Reply to Demo Message";
        }
    }

    /**
     * Mark Opened menu item. After the method marks the message read, it fires
     * update event.
     */
    private static class MarkOpenedContextMenu extends ApplicationMenuItem {
        /**
         * Creates a new ApplicationMenuItem instance with provided menu
         * position.
         * 
         * @param order
         *            Display order of this item, lower numbers corresopnd to
         *            higher placement in the menu
         */
        MarkOpenedContextMenu(final int order) {
            super(order);
        }

        /**
         * Marks the context message opened.
         * 
         * @see ApplicationMenuItem#run(Object)
         */
        public Object run(final Object context) {
            if (context instanceof DemoMessage) {
                final DemoMessage message = (DemoMessage) context;
                message.markRead();
                final ApplicationMessageFolderRegistry reg =
                        ApplicationMessageFolderRegistry.getInstance();
                final ApplicationMessageFolder folder =
                        reg.getApplicationFolder(MessageListDemo.INBOX_FOLDER_ID);
                folder.fireElementUpdated(message, message);
                changeIndicator(-1);
            }
            return context;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "Mark Demo Message Read";
        }
    }

    /**
     * Mark unread menu item. After the method marks the message unread, it
     * fires and update event.
     */
    private static class MarkUnreadContextMenu extends ApplicationMenuItem {
        /**
         * Creates a new ApplicationMenuItem instance with provided menu
         * position.
         * 
         * @param order
         *            Display order of this item, lower numbers corresopnd to
         *            higher placement in the menu
         */
        MarkUnreadContextMenu(final int order) {
            super(order);
        }

        /**
         * Marks the context message unread.
         * 
         * @see ApplicationMenuItem#run(Object)
         */
        public Object run(final Object context) {
            if (context instanceof DemoMessage) {
                final DemoMessage message = (DemoMessage) context;
                message.markAsNew();
                final ApplicationMessageFolderRegistry reg =
                        ApplicationMessageFolderRegistry.getInstance();
                final ApplicationMessageFolder folder =
                        reg.getApplicationFolder(MessageListDemo.INBOX_FOLDER_ID);
                folder.fireElementUpdated(message, message);
                changeIndicator(1);
            }
            return context;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "Mark Demo Message Unread";
        }
    }

    /**
     * Open Context menu item. Marks read and opens the selected message for
     * viewing.
     */
    static class OpenContextMenu extends ApplicationMenuItem {
        /**
         * Creates a new ApplicationMenuItem instance with provided menu
         * position.
         * 
         * @param order
         *            Display order of this item, lower numbers corresopnd to
         *            higher placement in the menu
         */
        public OpenContextMenu(final int order) {
            super(order);
        }

        /**
         * Marks the message as read if it was new and shows the message.
         * 
         * @see ApplicationMenuItem#run(Object)
         */
        public Object run(final Object context) {
            if (context instanceof DemoMessage) {
                final DemoMessage message = (DemoMessage) context;

                // Update status if message is new.
                if (message.isNew()) {
                    message.markRead();
                    final ApplicationMessageFolderRegistry reg =
                            ApplicationMessageFolderRegistry.getInstance();
                    final ApplicationMessageFolder folder =
                            reg.getApplicationFolder(MessageListDemo.INBOX_FOLDER_ID);
                    folder.fireElementUpdated(message, message);
                    changeIndicator(-1);
                }

                // Show message.
                final DemoMessageScreen previewScreen =
                        new DemoMessageScreen(message);
                final UiApplication uiApplication =
                        UiApplication.getUiApplication();
                uiApplication.pushScreen(previewScreen);
                uiApplication.requestForeground();
            }
            return context;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "View Demo Message";
        }
    }

    /**
     * Implementation of ApplicationMessageFolderListener.
     * 
     * @param action
     *            Action code specified by one of the constants from this
     *            interface.
     * @param messages
     *            An underlying message or array of messages for a bulk
     *            operation.
     * @param folder
     *            Folder that contains the message.
     * 
     * @see net.rim.blackberry.api.messagelist.ApplicationMessageFolderListener#actionPerformed(int,
     *      ApplicationMessage[], ApplicationMessageFolder)
     */
    public void actionPerformed(final int action,
            final ApplicationMessage[] messages,
            final ApplicationMessageFolder folder) {
        final MessageListDemoStore messageStore =
                MessageListDemoStore.getInstance();

        synchronized (messageStore) {
            // Check if action was performed on multiple messages.
            if (messages.length == 1) {
                final DemoMessage message = (DemoMessage) messages[0];

                switch (action) {
                case ApplicationMessageFolderListener.MESSAGE_DELETED:
                    if (folder.getId() == MessageListDemo.INBOX_FOLDER_ID) {
                        // Message from Inbox was deleted.
                        // Update storage, the message will go into
                        // Deleted folder
                        messageStore.deleteInboxMessage(message);

                        // Notify GUI that message has moved to another
                        // folder.
                        messageStore.getDeletedFolder().fireElementAdded(
                                message);

                        // Note: There is no need to fireElementRemoved(),
                        // message was already deleted.

                    } else {
                        // Message was deleted completely from the Deleted
                        // folder, update storage folder.
                        messageStore.deleteMessageCompletely(message);

                        // Note: There is no need to fireElementRemoved(),
                        // message was already deleted.
                    }
                    break;
                case ApplicationMessageFolderListener.MESSAGE_MARKED_OPENED:

                    // Update message.
                    message.markRead();

                    // Update storage.
                    messageStore.commitMessage(message);

                    // Notify GUI that message has changed.
                    folder.fireElementUpdated(message, message);
                    break;
                case ApplicationMessageFolderListener.MESSAGE_MARKED_UNOPENED:

                    // Update message.
                    message.markAsNew();

                    // Update storage.
                    messageStore.commitMessage(message);

                    // Notify GUI that message has changed.
                    folder.fireElementUpdated(message, message);
                    break;
                }
            } else {
                // Multiple messages were affected, optimize notifications.
                ApplicationMessageFolder resetFolder = folder;

                for (int i = 0; i < messages.length; i++) {
                    final DemoMessage message = (DemoMessage) messages[i];

                    switch (action) {
                    case ApplicationMessageFolderListener.MESSAGE_DELETED:
                        if (folder.getId() == MessageListDemo.INBOX_FOLDER_ID) {
                            // Update storage, the message will go
                            // into Deleted folder.
                            messageStore.deleteInboxMessage(message);
                        } else {
                            // Message was deleted completely from the
                            // Deleted folder, update storage.
                            messageStore.deleteMessageCompletely(message);
                        }
                        break;
                    case ApplicationMessageFolderListener.MESSAGE_MARKED_OPENED:

                        if (message.isNew()) {
                            changeIndicator(-1);
                        }
                        // Update message.
                        message.markRead();

                        // Update storage.
                        messageStore.commitMessage(message);

                        // Notify GUI that message has changed.
                        folder.fireElementUpdated(message, message);
                        break;
                    case ApplicationMessageFolderListener.MESSAGE_MARKED_UNOPENED:

                        if (!message.isNew()) {
                            changeIndicator(1);
                        }

                        // Update message.
                        message.markAsNew();

                        // Update storage.
                        messageStore.commitMessage(message);

                        // Notify GUI that message has changed.
                        folder.fireElementUpdated(message, message);
                        break;
                    }
                }
                if (action == ApplicationMessageFolderListener.MESSAGE_DELETED
                        && folder.getId() == MessageListDemo.INBOX_FOLDER_ID) {
                    // There is no need to reset the Inbox folder,
                    // all the messages have already been deleted from it.
                    // We need to reset Deleted folder because messages were
                    // appended to it.
                    resetFolder = messageStore.getDeletedFolder();
                }

                if (resetFolder != null) {
                    resetFolder.fireReset();
                }
            }
        }
    }
}
