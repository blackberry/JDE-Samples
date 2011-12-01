/*
 * MessagesViewScreen.java
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

package com.rim.samples.device.blackberrymaildemo;

import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.blackberry.api.mail.event.FolderEvent;
import net.rim.blackberry.api.mail.event.FolderListener;
import net.rim.blackberry.api.mail.event.MessageEvent;
import net.rim.blackberry.api.mail.event.MessageListener;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.api.util.StringProvider;

/**
 * This class allows the user to view all of the messages. It uses the
 * MessagesListField class to display the messages. The messages are displayed
 * in reverse chronological order. This class also implements a FolderListener
 * to listen for any events in which messages are changed, and a FolderListener
 * to listen for events in which messages are added or removed from a store
 * folder. In addition to the menu options given by the parent class
 * BlackBerryMailDemoScreen, this class allows the user to delete messages.
 */
public final class MessagesViewScreen extends BlackBerryMailDemoScreen
        implements FolderListener, MessageListener {
    private final SimpleSortingVector _messages;
    private final UiApplication _uiApplication;

    /**
     * Creates a new MessagesViewScreen object
     */
    public MessagesViewScreen() {
        setTitle("Messages View Screen");

        // Add this object as the folder listener for the store email service
        _store.addFolderListener(this);

        // Initialize the simple sorting vector to sort manually according to
        // most recent date.
        _messages = new SimpleSortingVector();
        _messages.setSortComparator(Util.SORT_BY_MOST_RECENT_DATE);
        _messages.setSort(false);

        // Fill the message vector then sort it
        populateList(_store.list(Folder.SUBTREE));
        _messages.reSort();

        // Add the list field to display the messages
        _listField = new MessagesListField(_messages);
        add(_listField);

        // Store the UiApplication for use in refreshing the message list field
        _uiApplication = UiApplication.getUiApplication();

        _changeViewMenuItem =
                new MenuItem(new StringProvider("Folders View"), 0x230020, 1);
        _changeViewMenuItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                _currentDisplayMode = FOLDERS_VIEW_MODE;

                final Folder currentFolder =
                        ((Message) getSelectedItem()).getFolder();

                final FoldersViewScreen foldersViewScreen =
                        new FoldersViewScreen(currentFolder);
                UiApplication.getUiApplication().pushScreen(foldersViewScreen);
            }
        }));

        _deleteMenuItem =
                new MenuItem(new StringProvider("Delete Message"), 0x230010, 0);
        _deleteMenuItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                final int choice =
                        Dialog.ask(Dialog.D_YES_NO, "Delete message?",
                                Dialog.YES);
                if (choice == Dialog.YES) {
                    final Message message = (Message) getSelectedItem();
                    final Folder folder = message.getFolder();
                    folder.deleteMessage(message);
                    updateScreen();
                }
            }
        }));
    }

    /**
     * @see com.rim.samples.device.blackberrymaildemo.BlackBerryMailDemoScreen#makeMenu(Menu,
     *      int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        if (!_listField.isEmpty()) {
            menu.add(_deleteMenuItem);
        }
        super.makeMenu(menu, instance);
    }

    /**
     * Refreshes the message store, messages, and the screen when the message
     * service is changed.
     * 
     * @see com.rim.samples.device.blackberrymaildemo.BlackBerryMailDemoScreen#messageServiceChanged()
     */
    protected void messageServiceChanged() {
        _store.addFolderListener(this);
        _messages.removeAllElements();
        populateList(_store.list());
        updateScreen();
    }

    /**
     * Fills the list of messages with the messages stored in the specified
     * folders, including their subfolders.
     * 
     * @param folders
     *            The folders to populate the list with
     */
    private void populateList(final Folder[] folders) {
        for (int folderIndex = 0; folderIndex < folders.length; folderIndex++) {
            // Populate the list with the subfolders
            final Folder[] subfolders = folders[folderIndex].list();
            populateList(subfolders);

            // Search the current folder for the message to delete
            Message[] messages;
            try {
                messages = folders[folderIndex].getMessages();
            } catch (final MessagingException e) {
                BlackBerryMailDemo.errorDialog("Folder#getMessages() threw "
                        + e.toString());
                return;
            }

            // Populate the list with the messages in the current folder and
            // add a message listener to them.
            for (int messageIndex = 0; messageIndex < messages.length; messageIndex++) {
                final Message message = messages[messageIndex];
                message.addMessageListener(this);
                _messages.addElement(message);
            }
        }
    }

    /**
     * @see com.rim.samples.device.blackberrymaildemo.BlackBerryMailDemoScreen#updateScreen()
     */
    protected void updateScreen() {
        // Update the screen when the application is free
        _uiApplication.invokeLater(new Runnable() {
            public void run() {
                // Re-sort by most recent date and resize the list field
                _messages.reSort();
                _listField.setSize(_messages.size());
            }
        });
    }

    /**
     * Gets the selected message
     * 
     * @see com.rim.samples.device.blackberrymaildemo.BlackBerryMailDemoScreen#getSelectedItem()
     */
    protected Object getSelectedItem() {
        Object obj = null;

        final int index = _listField.getSelectedIndex();
        if (index > -1) {
            obj = _messages.elementAt(index);
        }

        return obj;
    }

    /**
     * Opens the selected message
     * 
     * @see com.rim.samples.device.blackberrymaildemo.BlackBerryMailDemoScreen#openAction()
     */
    protected void openAction() {
        final Message selectedMessage = (Message) getSelectedItem();
        if (selectedMessage != null) {
            openMessage(selectedMessage);
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close() {
        // De-register listeners before closing
        _store.removeFolderListener(this);

        for (int index = _messages.size() - 1; index >= 0; index--) {
            final Message msg = (Message) _messages.elementAt(index);
            msg.removeMessageListener(this);
        }

        super.close();
    }

    /**
     * MenuItem to delete a message
     */
    private final MenuItem _deleteMenuItem;

    // //////////////////////////////////////////////////////////////////////////
    // ********************* Message Listener *************************** //
    // //////////////////////////////////////////////////////////////////////////

    /**
     * @see net.rim.blackberry.api.mail.event.MessageListener#changed(MessageEvent)
     */

    public void changed(final MessageEvent e) {
        // Get the selected message to update screen at the message's row
        final Message message = e.getMessage();
        _listField.invalidate(_messages.indexOf(message));
    }

    // //////////////////////////////////////////////////////////////////////////
    // ********************** Folder Listener *************************** //
    // //////////////////////////////////////////////////////////////////////////

    /**
     * @see net.rim.blackberry.api.mail.event.FolderListener#messagesAdded(FolderEvent)
     */
    public void messagesAdded(final FolderEvent e) {
        final Message message = e.getMessage();

        message.addMessageListener(this);

        // Insert the message into the vector of messages, preserving
        // the sorted order.
        int indexToInsert = _messages.find(message);
        if (indexToInsert < 0) {
            indexToInsert = -indexToInsert - 1;
        }
        _messages.insertElementAt(message, indexToInsert);

        updateScreen();
    }

    /**
     * @see net.rim.blackberry.api.mail.event.FolderListener#messagesRemoved(FolderEvent)
     */
    public void messagesRemoved(final FolderEvent e) {
        final Message msg = e.getMessage();

        // Remove the listeners and delete the message from the list
        msg.removeMessageListener(this);
        _messages.removeElement(msg);

        updateScreen();
    }
}
