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
final class MessageListDemoStore {
    // com.rim.samples.device.messagelistdemo.MessageListDemoStore
    private static final long MSG_KEY = 0xcf2b552e0e98a715L;

    private static MessageListDemoStore _instance;

    private final ReadableListImpl _inboxMessages;
    private final ReadableListImpl _deletedMessages;
    private ApplicationMessageFolder _mainFolder;
    private ApplicationMessageFolder _deletedFolder;
    private final ApplicationIndicator _indicator;

    // Constructor
    MessageListDemoStore() {
        _inboxMessages = new ReadableListImpl();
        _deletedMessages = new ReadableListImpl();
        _indicator =
                ApplicationIndicatorRegistry.getInstance()
                        .getApplicationIndicator();
    }

    static MessageListDemoStore getInstance() {
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

    void setFolders(final ApplicationMessageFolder mainFolder,
            final ApplicationMessageFolder deletedFolder) {
        _mainFolder = mainFolder;
        _deletedFolder = deletedFolder;
    }

    ApplicationMessageFolder getInboxFolder() {
        return _mainFolder;
    }

    ApplicationMessageFolder getDeletedFolder() {
        return _deletedFolder;
    }

    /**
     * User deleted message, move it into deleted folder.
     */
    void deleteInboxMessage(final DemoMessage message) {
        message.messageDeleted();
        _inboxMessages.removeMessage(message);
        _deletedMessages.addMessage(message);

        // Update indicator.
        _indicator.setValue(_indicator.getValue() - 1);
        if (_indicator.getValue() <= 0) {
            _indicator.setVisible(false);
        }

    }

    void commitMessage(final DemoMessage message) {
        // This empty method exists to reinforce the idea that in a real world
        // situation messages would be saved in device persistent store and/or
        // on a mail server.
    }

    void addInboxMessage(final DemoMessage message) {
        _inboxMessages.addMessage(message);

        // Update indicator.
        _indicator.setValue(_indicator.getValue() + 1);
        if (!_indicator.isVisible()) {
            _indicator.setVisible(true);
        }
    }

    /**
     * User deleted message from the Deleted folder.
     * 
     * @param message
     */
    void deleteMessageCompletely(final DemoMessage message) {
        _deletedMessages.removeMessage(message);
    }

    ReadableListImpl getInboxMessages() {
        return _inboxMessages;
    }

    ReadableListImpl getDeletedMessages() {
        return _deletedMessages;
    }

    static class ReadableListImpl implements ReadableList {
        private final Vector messages;

        ReadableListImpl() {
            messages = new Vector();
        }

        public Object getAt(final int index) {
            return messages.elementAt(index);
        }

        public int getAt(final int index, final int count,
                final Object[] elements, final int destIndex) {
            return 0;
        }

        public int getIndex(final Object element) {
            return messages.indexOf(element);
        }

        public int size() {
            return messages.size();
        }

        void addMessage(final DemoMessage message) {
            messages.addElement(message);
        }

        void removeMessage(final DemoMessage message) {
            messages.removeElement(message);
        }
    }
}
