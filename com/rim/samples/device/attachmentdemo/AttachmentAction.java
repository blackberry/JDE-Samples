/*
 * AttachmentAction.java
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

package com.rim.samples.device.attachmentdemo;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import net.rim.blackberry.api.mail.AttachmentDownloadManager;
import net.rim.blackberry.api.mail.BodyPart;
import net.rim.blackberry.api.mail.DownloadProgressListener;
import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.blackberry.api.mail.ServiceConfiguration;
import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.mail.Store;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;

/**
 * A class to retrieve all email messages and extract those that contain
 * attachments.
 */
public final class AttachmentAction implements DownloadProgressListener {
    private final AttachmentDownloadManager _downloadManager;
    private final AttachmentDemoScreen _screen;
    private final Vector _messageVector;

    /**
     * Creates a new AttachmentAction object
     * 
     * @param screen
     *            The main screen for the application
     */
    public AttachmentAction(final AttachmentDemoScreen screen) {
        _screen = screen;
        _downloadManager = new AttachmentDownloadManager();
        _messageVector = new Vector();
    }

    /**
     * Adds all email messages to the messages vector
     * 
     * @param folders
     *            An array of mail folder objects
     */
    private void populateMessages(final Folder[] folders) {
        for (int folderIndex = 0; folderIndex < folders.length; folderIndex++) {
            final Folder[] subfolders = folders[folderIndex].list();

            // Call this method recursively
            populateMessages(subfolders);

            Message[] messages;
            try {
                // Retrieve messages from current folder
                messages = folders[folderIndex].getMessages();
            } catch (final MessagingException e) {
                AttachmentDemo.errorDialog("Folder#getMessages() threw "
                        + e.toString());
                return;
            }

            for (int messageIndex = 0; messageIndex < messages.length; messageIndex++) {
                _messageVector.addElement(messages[messageIndex]);
            }
        }
    }

    /**
     * Gets all the email messages from the mail box
     * 
     * @return True if any messages were retrieved, otherwise false
     */
    public boolean getMessages() {
        // Open the service book and get the mail service records
        final ServiceBook serviceBook = ServiceBook.getSB();
        final ServiceRecord[] mailServiceRecords =
                serviceBook.findRecordsByCid("CMIME");

        for (int cnt = mailServiceRecords.length - 1; cnt >= 0; --cnt) {
            final ServiceConfiguration sc =
                    new ServiceConfiguration(mailServiceRecords[cnt]);
            final Store store = Session.getInstance(sc).getStore();
            populateMessages(store.list(Folder.SUBTREE));
        }

        return !_messageVector.isEmpty();
    }

    /**
     * Downloads attachments
     * 
     * @param downloadAll
     *            True if all attachments are to be downloaded, false if only
     *            msword or png files are to be downloaded
     */
    public void download(final boolean downloadAll) throws IOException {
        final Enumeration msg_enum = _messageVector.elements();
        final Vector partsToBeDownloaded = new Vector();

        // Get all messages
        while (msg_enum.hasMoreElements()) {
            final Message m = (Message) msg_enum.nextElement();

            // Get body parts containing attachments
            final BodyPart[] bodyParts =
                    _downloadManager.getAttachmentBodyParts(m);

            if (bodyParts != null && bodyParts.length > 0) {
                for (int i = 0; i < bodyParts.length; i++) {
                    final BodyPart bp = bodyParts[i];

                    if (!downloadAll) {
                        // Download png and msword attachments only
                        final String type =
                                _downloadManager.getFileContentType(bp);
                        if (!type.trim().endsWith("png")
                                && !type.trim().endsWith("msword")) {
                            continue;
                        }
                    }
                    partsToBeDownloaded.addElement(bp);
                }
            }
        }

        if (partsToBeDownloaded.size() > 0) {
            // Re-initialize the body parts array with the body parts we are
            // interested in
            final BodyPart[] bodyParts =
                    new BodyPart[partsToBeDownloaded.size()];
            partsToBeDownloaded.copyInto(bodyParts);
            if (bodyParts != null && bodyParts.length > 0) {
                // Download the body parts
                _downloadManager.download(bodyParts, null, this);

            }
        }
    }

    // DownloadProgressListener implementation ---------------------------------

    /**
     * @see DownloadProgressListener#downloadCancelled(Object)
     */
    public void downloadCancelled(final Object element) {
        final BodyPart bodyPart = (BodyPart) element;
        _screen.displayStatus("Failed to download "
                + _downloadManager.getFileName(bodyPart));
    }

    /**
     * @see DownloadProgressListener#downloadCompleted(Object)
     */
    public void downloadCompleted(final Object element) {
        final BodyPart bodyPart = (BodyPart) element;
        _screen.displayStatus(_downloadManager.getFileName(bodyPart)
                + " downloaded.");
    }

    /**
     * @see DownloadProgressListener#updateProgress(Object, int, int)
     */
    public void updateProgress(final Object element, final int current,
            final int total) {
        // Not implemented
    }
}
