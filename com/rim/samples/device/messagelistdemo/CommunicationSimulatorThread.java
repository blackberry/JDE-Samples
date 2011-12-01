/**
 * CommunicationSimulatorThread.java
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

import java.util.Random;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * This class simulates communication with a server and generates message
 * actions. It can create new messages or update and delete existing ones.
 */
public final class CommunicationSimulatorThread extends Thread {

    private boolean _keepRunning;
    private static Random _random = new Random();

    private static String[] NAMES =
            { "Scott Wyatt", "Tanya Wahl", "Kate Strike", "Mark McMullen",
                    "Beth Horton", "John Graham", "Ho Sung Chan",
                    "Long Feng Wu", "Kevil Wilhelm", "Trevor Van Daele" };

    private static String[] PICTURES = { "BlueDress.png", "BlueSuit.png",
            "BlueSweatshirt.png", "BrownShirt.png", "Construction.png",
            "DarkJacket.png", "DarkSuit.png", "FemaleDoctor.png",
            "GreenJacket.png", "GreenShirt.png", "GreenTop.png",
            "LeatherJacket.png", "MaleDoctor.png", "Mechanic.png",
            "OrangeShirt.png", "PatternShirt.png", "PurpleTop.png",
            "RedCap.png", "RedJacket.png", "RedShirt.png" };

    // Constructor
    public CommunicationSimulatorThread() {
        _keepRunning = true;
    }

    /**
     * Performs random actions to the message store every three seconds.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        final MessageListDemoStore messageStore =
                MessageListDemoStore.getInstance();
        while (_keepRunning) {
            synchronized (messageStore) {
                performRandomAction(messageStore);
            }
            try {
                synchronized (this) {
                    wait(3000);
                }
            } catch (final InterruptedException e) {
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        Dialog.alert("Thread#wait(long) threw " + e.toString());
                    }
                });

                return;
            }
        }
    }

    /**
     * Performs a random action. The action can either be: updating an existing
     * message, deleting an inbox message or deleting a message completely.
     * 
     * @param messageStore
     *            The message store to perform the random action to
     */
    private void performRandomAction(final MessageListDemoStore messageStore) {
        final ReadableList inboxMessages = messageStore.getInboxMessages();
        final ReadableList deletedMessages = messageStore.getDeletedMessages();

        switch (_random.nextInt(3)) {
        case 0:
            // Update an existing message.
            if (inboxMessages.size() > 0) {
                final DemoMessage msg =
                        (DemoMessage) inboxMessages.getAt(_random
                                .nextInt(inboxMessages.size()));
                if (msg.isNew()) {
                    msg.markRead();
                } else if (!msg.hasReplied()) {
                    msg.reply("Auto reply");
                } else {
                    msg.markAsNew();
                }
                messageStore.getInboxFolder().fireElementUpdated(msg, msg);
            } else {
                addInboxMessage(messageStore);
            }
            break;

        case 1:
            // Delete an inbox message.
            if (inboxMessages.size() > 0) {
                final DemoMessage msg =
                        (DemoMessage) inboxMessages.getAt(_random
                                .nextInt(inboxMessages.size()));
                messageStore.deleteInboxMessage(msg);
                messageStore.getInboxFolder().fireElementRemoved(msg);
                messageStore.getDeletedFolder().fireElementAdded(msg);
            } else {
                addInboxMessage(messageStore);
            }
            break;

        default:
            // Delete message completely.
            if (deletedMessages.size() > 0) {
                final DemoMessage msg =
                        (DemoMessage) deletedMessages.getAt(_random
                                .nextInt(deletedMessages.size()));
                messageStore.deleteMessageCompletely(msg);
                messageStore.getDeletedFolder().fireElementRemoved(msg);
            } else {
                addInboxMessage(messageStore);
            }
            break;
        }
    }

    /**
     * Adds a pre-defined message to the specified message store.
     * 
     * @param messageStore
     *            The message store to add the message to
     */
    private void addInboxMessage(final MessageListDemoStore messageStore) {
        final DemoMessage message = new DemoMessage();
        final String name = NAMES[_random.nextInt(NAMES.length)];
        message.setSender(name);
        message.setSubject("Hello from " + name);
        message.setMessage("Hello Chris. This is " + name
                + ". How are you?  Hope to see you at the conference!");
        message.setReceivedTime(System.currentTimeMillis() - 1000 * 60
                * _random.nextInt(60 * 24));

        // assign random preview picture
        message.setPreviewPicture(getRandomPhotoImage());

        // Store message.
        messageStore.addInboxMessage(message);

        // Notify folder
        messageStore.getInboxFolder().fireElementAdded(message);
    }

    /**
     * Gets a random pre-defined image.
     * 
     * @return The hard coded photo image
     */
    public static EncodedImage getRandomPhotoImage() {
        final String pictureName =
                "photo/" + PICTURES[_random.nextInt(PICTURES.length)];
        return EncodedImage.getEncodedImageResource(pictureName);
    }

    /**
     * Stops the thread from continuing its processing.
     */
    void stopRunning() {
        synchronized (this) {
            _keepRunning = false;
            notifyAll();
        }
    }
}
