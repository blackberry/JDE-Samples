/**
 * MessageListDemoStore.java
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

import java.util.Vector;

import net.rim.blackberry.api.messagelist.ApplicationIndicator;
import net.rim.blackberry.api.messagelist.ApplicationIndicatorRegistry;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolder;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.RuntimeStore;

/**
 * This class is used to facilitate the storage of messages. For the sake of
 * simplicitly, we are saving messages in the device runtime store. In a real
 * world situation, messages would be saved in device persistent store and/or a
 * mail server.
 */
public final class MessageListDemoStore {
    // com.rim.samples.device.messagelistdemo.MessageListDemoStore
    private static final long MSG_KEY = 0xcf2b552e0e98a715L;

    private static MessageListDemoStore _instance;

    private final ReadableListImpl _inboxMessages;
    private final ReadableListImpl _deletedMessages;
    private ApplicationMessageFolder _mainFolder;
    private ApplicationMessageFolder _deletedFolder;
    private final ApplicationIndicator _indicator;

    // Constructor
    private MessageListDemoStore() {
        _inboxMessages = new ReadableListImpl();
        _deletedMessages = new ReadableListImpl();
        _indicator =
                ApplicationIndicatorRegistry.getInstance()
                        .getApplicationIndicator();
    }

    /**
     * Gets the singleton instance of the MessageListDemoStore.
     * 
     * @return The singleton instance of the MessagelistDemoStore
     */
    public static synchronized MessageListDemoStore getInstance() {
        // Keep messages as singleton in the RuntimeStore.
        if (_instance == null) {
            final RuntimeStore rs = RuntimeStore.getRuntimeStore();

            synchronized (rs) {
                _instance = (MessageListDemoStore) rs.get(MSG_KEY);

                if (_instance == null) {
                    _instance = new MessageListDemoStore();
                    rs.put(MSG_KEY, _instance);
                }
            }
        }
        return _instance;
    }

    /**
     * Sets the main and deleted folders.
     * 
     * @param mainFolder
     *            The main folder to use
     * @param deletedFolder
     *            The deleted folder to use
     */
    void setFolders(final ApplicationMessageFolder mainFolder,
            final ApplicationMessageFolder deletedFolder) {
        _mainFolder = mainFolder;
        _deletedFolder = deletedFolder;
    }

    /**
     * Gets the inbox folder.
     * 
     * @return The inbox folder
     */
    ApplicationMessageFolder getInboxFolder() {
        return _mainFolder;
    }

    /**
     * Gets the deleted folder.
     * 
     * @return The deleted folder
     */
    ApplicationMessageFolder getDeletedFolder() {
        return _deletedFolder;
    }

    /**
     * User deleted message, move it into deleted folder.
     * 
     * @param message
     *            The message to move to the deleted folder
     */
    void deleteInboxMessage(final DemoMessage message) {
        if (message.isNew()) {
            // Update indicator.
            _indicator.setValue(_indicator.getValue() - 1);
            if (_indicator.getValue() <= 0) {
                _indicator.setVisible(false);
            }
        }
        message.messageDeleted();
        _inboxMessages.removeMessage(message);
        _deletedMessages.addMessage(message);

    }

    /**
     * Commits the message to a persistant store.
     * 
     * @param message
     *            The message to commit
     */
    void commitMessage(final DemoMessage message) {
        // This empty method exists to reinforce the idea that in a real world
        // situation messages would be saved in device persistent store and/or
        // on a mail server.
    }

    /**
     * Adds a message to the inbox.
     * 
     * @param message
     *            The message to add to the inbox
     */
    void addInboxMessage(final DemoMessage message) {
        _inboxMessages.addMessage(message);

        if (message.isNew()) {
            // Update indicator.
            _indicator.setValue(_indicator.getValue() + 1);
            if (!_indicator.isVisible()) {
                _indicator.setVisible(true);
            }
        }
    }

    /**
     * Completely deletes the message from the message store.
     * 
     * @param message
     *            The message to delete from the message store
     */
    void deleteMessageCompletely(final DemoMessage message) {
        _deletedMessages.removeMessage(message);
    }

    /**
     * Gets the inbox messages as a readable list.
     * 
     * @return The readable list of all the inbox messages
     */
    ReadableListImpl getInboxMessages() {
        return _inboxMessages;
    }

    /**
     * Gets the deleted messages as a readable list.
     * 
     * @return The readable list of all the deleted messages
     */
    ReadableListImpl getDeletedMessages() {
        return _deletedMessages;
    }

    /**
     * This is an implementation of the ReadableList interface which stores the
     * list of messages using a Vector.
     */
    static class ReadableListImpl implements ReadableList {
        private final Vector messages;

        /**
         * Creates a empty instance of ReadableListImpl.
         */
        ReadableListImpl() {
            messages = new Vector();
        }

        /**
         * @see net.rim.device.api.collection.ReadableList#getAt(int)
         */
        public Object getAt(final int index) {
            return messages.elementAt(index);
        }

        /**
         * @see net.rim.device.api.collection.ReadableList#getAt(int, int,
         *      Object, int)
         */
        public int getAt(final int index, final int count,
                final Object[] elements, final int destIndex) {
            return 0;
        }

        /**
         * @see net.rim.device.api.collection.ReadableList#getIndex(Object)
         */
        public int getIndex(final Object element) {
            return messages.indexOf(element);
        }

        /**
         * @see net.rim.device.api.collection.ReadableList#size()
         */
        public int size() {
            return messages.size();
        }

        /**
         * Add a message to this list.
         * 
         * @param message
         *            The message to add to this list
         */
        void addMessage(final DemoMessage message) {
            messages.addElement(message);
        }

        /**
         * Removes a message from this list.
         * 
         * @param message
         *            The message to remove from this list
         */
        void removeMessage(final DemoMessage message) {
            messages.removeElement(message);
        }
    }
}
