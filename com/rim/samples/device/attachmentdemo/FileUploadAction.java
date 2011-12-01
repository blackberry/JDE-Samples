/*
 * FileUploadAction.java
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
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.AddressException;
import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.FolderNotFoundException;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.blackberry.api.mail.Multipart;
import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.mail.SupportedAttachmentPart;
import net.rim.blackberry.api.mail.TextBodyPart;
import net.rim.blackberry.api.mail.Transport;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.MIMETypeAssociations;

/**
 * Class to send an attachment
 */
public final class FileUploadAction {
    /**
     * Sends an attachment
     * 
     * @param fileHolder
     *            An object which stores information about the file to be
     *            uploaded
     * @param email
     *            Recipient email address
     */
    public void upload(final FileHolder fileHolder, final String email)
            throws FolderNotFoundException, AddressException,
            MessagingException, IOException {
        final Multipart mp = new Multipart();

        if (fileHolder == null || email == null || email.length() == 0) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        if (fileHolder.getPath().lastIndexOf('/') == -1) {
            throw new FolderNotFoundException(fileHolder.getPath(),
                    "Directory not found.");
        }

        final byte[] stream =
                readStream(fileHolder.getPath() + "/"
                        + fileHolder.getFileName());
        final String messageData =
                "See attachment: " + fileHolder.getFileName();

        if (stream == null || stream.length == 0) {
            throw new IOException("Failed to read the file stream");
        }

        final SupportedAttachmentPart sap =
                new SupportedAttachmentPart(mp, MIMETypeAssociations
                        .getMIMEType(fileHolder.getFileName()), fileHolder
                        .getFileName(), stream);

        final TextBodyPart tbp = new TextBodyPart(mp, messageData);
        mp.addBodyPart(tbp);
        mp.addBodyPart(sap);
        final Folder folders[] =
                Session.getDefaultInstance().getStore().list(Folder.SENT);
        final Message message = new Message(folders[0]);
        final Address[] toAdds = new Address[1];
        toAdds[0] = new Address(email, email);
        message.addRecipients(Message.RecipientType.TO, toAdds);
        message.setContent(mp);
        message.setSubject("Message with attachment "
                + fileHolder.getFileName() + ".");
        Transport.send(message);
    }

    /**
     * Retrieves a resource as a byte array
     * 
     * @param path
     *            Path to the resource to be read
     * @return byte[] A byte array containing the specified resource
     */
    private byte[] readStream(final String path) {
        InputStream in = null;
        FileConnection fc = null;
        byte[] bytes = null;

        try {
            fc = (FileConnection) Connector.open(path);
            if (fc != null && fc.exists()) {
                in = fc.openInputStream();
                if (in != null) {
                    bytes = IOUtilities.streamToBytes(in);
                }
            }
        } catch (final IOException e) {
            AttachmentDemo.errorDialog(e.toString());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
            }
            try {
                if (fc != null) {
                    fc.close();
                }
            } catch (final IOException e) {
            }

        }
        return bytes;
    }
}
