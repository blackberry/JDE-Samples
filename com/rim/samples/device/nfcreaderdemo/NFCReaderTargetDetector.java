/*
 * NFCReaderTargetDetector.java
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

package com.rim.samples.device.nfcreaderdemo;

import java.io.IOException;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;

import net.rim.device.api.io.nfc.NFCException;
import net.rim.device.api.io.nfc.NFCManager;
import net.rim.device.api.io.nfc.ndef.NDEFMessage;
import net.rim.device.api.io.nfc.ndef.NDEFRecord;
import net.rim.device.api.io.nfc.ndef.NDEFTagConnection;
import net.rim.device.api.io.nfc.readerwriter.DetectionListener;
import net.rim.device.api.io.nfc.readerwriter.ISO14443Part3Connection;
import net.rim.device.api.io.nfc.readerwriter.ISO14443Part4Connection;
import net.rim.device.api.io.nfc.readerwriter.Target;
import net.rim.device.api.ui.component.LabelField;

/**
 * A DetectionListener class that listens for a target to come into the device's
 * NFC field.
 */
public class NFCReaderTargetDetector implements DetectionListener {
    private final NFCReaderScreen _screen;
    private final NFCManager _manager;

    /**
     * Creates a new NFCReaderTargetDetector object
     * 
     * @param screen
     *            The application's main screen
     * @param nfcManager
     *            NFCManager instance
     */
    public NFCReaderTargetDetector(final NFCReaderScreen screen,
            final NFCManager nfcManager) throws NFCException {
        if (screen == null) {
            throw new IllegalArgumentException("screen == null");
        }
        _screen = screen;

        if (nfcManager == null) {
            throw new IllegalArgumentException("_manager == null");
        }
        _manager = nfcManager;
    }

    /**
     * @see net.rim.device.api.io.nfc.readerwriter.DetectionListener#onTargetDetected(Target)
     */
    public void onTargetDetected(final Target target) {
        String uri = null;

        // Try to determine out what type of target was detected
        // and read it if possible.
        if (target.isType(Target.NDEF_TAG)) // NDEF Target
        {
            _screen.addTargetInfo("Target Type: NDEF Tag");
            uri = target.getUri(Target.NDEF_TAG);
            readNDEF(uri);
        } else if (target.isType(Target.ISO_14443_4)) // ISO 14443 4 target
        {
            _screen.deleteFields();
            _screen.addTargetInfo("Target Type: ISO 14443 Part 4");
            uri = target.getUri(Target.ISO_14443_4);
            readISO14443Target(uri, target);
        } else if (target.isType(Target.ISO_14443_3)) // ISO 14443 3 target
        {
            _screen.deleteFields();
            _screen.addTargetInfo("Target Type: ISO 14443 Part 3");
            uri = target.getUri(Target.ISO_14443_3);
            readISO14443Part3(uri, target);
        } else
        // Unknown type of target was found
        {
            _screen.addTargetInfo("Unknown target type found... "
                    + target.getProperty("Type"));
        }
    }

    /**
     * Reads the contents of a detected ISO 14443 3 target
     * 
     * @param uri
     *            The detected ISO 14443 3's URI
     * @param target
     *            The ISO 14443 3 target that has been detected
     */
    public void readISO14443Part3(final String uri, final Target target) {
        _screen.addTargetInfo("URI: " + uri);
        ISO14443Part3Connection c = null;

        // Try to open a connection to the tag and send it a command
        try {
            c = (ISO14443Part3Connection) Connector.open(uri);

            // Get some metadata about the tag so we can determine
            // what command set to use on it.
            _screen.addTargetInfo("Name: " + target.getProperty("Name"));

            // Note: For the sake of this demo, we are hardcoding the request
            // that we will send the tag as the "Capability Container Select"
            // command.

            // Capability Container select command
            final byte[] response =
                    c.transceive(new byte[] { (byte) 0x00, (byte) 0xA4,
                            (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0xE1,
                            (byte) 0x03 });

            // Parse the response bytes to see if the
            // command was a success or not.
            parseISO14443Part3Response(response);

            // Play sound once the read operation is complete
            _manager.playNFCOperationSuccessSound();
        } catch (final IOException e) {
            _manager.playNFCOperationFailureSound();
            _screen.add(new LabelField("Error: " + e.toString()));
        } finally {
            closeConnection(c);
        }
    }

    /**
     * Parses the response bytes from a ISO 14443 3 target and displays them on
     * the screen.
     * 
     * @param response
     *            Response to ISO14443Part3Connection.transceive()
     */
    public void parseISO14443Part3Response(final byte[] response) {
        // Note: You need to determine what type of tag you are dealing with
        // and what the command set for that tag type is. Once you know that
        // information you can parse the response appropriately.

        // Response that we are looking for
        final byte[] ccSelectResponse = new byte[] { (byte) 0x09, (byte) 0x00 };

        // Make sure that the response is the same length as the response we are
        // looking for
        if (ccSelectResponse.length == response.length) {
            // If the first byte in the response is not 0x09, it is not the
            // response we want
            if (response[0] != (byte) 0x09) {
                _screen.addResponseStatus("Not capability container select command was not successful...");
            }

            // Otherwise it was 0x09, and if the 2nd byte is 0x00, it is the
            // response we are looking for
            else if (response[1] == (byte) 0x00) {
                _screen.addResponseStatus("The capability container select command was successful!");
            }
        }
    }

    /**
     * Reads an ISO 14443 4 target
     * 
     * @param uri
     *            Detected ISO 14443 4 target's URI
     * @param target
     *            The detected ISO 14443 4 tag
     */
    public void readISO14443Target(final String uri, final Target target) {
        _screen.addTargetInfo("URI: " + uri);
        ISO14443Part4Connection c = null;

        // Try to open a connection to the tag and send it a command
        try {
            c = (ISO14443Part4Connection) Connector.open(uri);

            // Get some metadata about the tag to determine what type
            // of tag it is and what command set to use on it.
            _screen.addTargetInfo("Name: " + target.getProperty("Name"));

            // Note: not a real command. Command bytes need
            // to be determined based on type of tag detected.

            // Note: For the sake of this demo, we are hardcoding the request
            // that we will send the tag as the "Capability Container Select"
            // command.

            // Capability Container select command
            final byte[] response =
                    c.transceive(new byte[] { (byte) 0x00, (byte) 0xA4,
                            (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0xE1,
                            (byte) 0x03 });

            parseISO14443Response(response);

            // Play sound once the read operation is complete
            _manager.playNFCOperationSuccessSound();
        } catch (final IOException e) {
            _manager.playNFCOperationFailureSound();
            _screen.add(new LabelField("Error: " + e.toString()));
        } finally {
            closeConnection(c);
        }
    }

    /**
     * Parses the response bytes from an ISO 14443 4 tag and displays them on
     * the screen.
     * 
     * @param response
     *            Response to ISO1443_4Connection.send()
     */
    public void parseISO14443Response(final byte[] response) {
        // Note: Need to determine what type of tag you are dealing with
        // and what the command set for that tag type is. Once you know
        // that information you can parse the response appropriately.

        // Response that we are looking for
        final byte[] ccSelectResponse = new byte[] { (byte) 0x09, (byte) 0x00 };

        // Make sure that the response is the same length as the response we are
        // looking for
        if (ccSelectResponse.length == response.length) {
            // If the first byte in the response is not 0x09, it is not the
            // response we want
            if (response[0] != (byte) 0x09) {
                _screen.addResponseStatus("Not capability container select command was not successful...");
            }

            // Otherwise it was 0x09, and if the 2nd byte is 0x00, it is the
            // response we are looking for
            else if (response[1] == (byte) 0x00) {
                _screen.addResponseStatus("The capability container select command was successful!");
            }
        }
    }

    /**
     * Read the detected NDEF target. Currently only handles NDEFRecords of type
     * "text/plain".
     * 
     * @param uri
     *            Detected NDEF target's URI
     */
    public void readNDEF(final String uri) {
        _screen.addTargetInfo("URI: " + uri);
        NDEFTagConnection c = null;
        String payload;
        final StringBuffer sb = new StringBuffer();

        // Try to open a connection to the NDEF tag and read its content
        try {
            c = (NDEFTagConnection) Connector.open(uri);
            final NDEFMessage ndefMessage = c.read();
            final NDEFRecord[] ndefRecords = ndefMessage.getRecords();

            // Go through all the NDEFRecords in the NDEFMessage
            for (int i = 0; i < ndefMessage.getNumberOfRecords(); i++) {
                final String type = ndefRecords[i].getType();
                sb.append("Type for record ");
                sb.append((i + 1));
                sb.append(" : ");
                sb.append(type);
                _screen.addResponseStatus(sb.toString());
                sb.delete(0, sb.length());

                if (!type.equals("text/plain")) // Unhandled type
                {
                    _screen.addResponseStatus("Unhandled record type: " + type);
                }

                // Display the recordType and the String version of the payload
                payload = new String(ndefRecords[i].getPayload());
                sb.append("\tPayload for ");
                sb.append((i + 1));
                sb.append(" : ");
                sb.append(payload);
                _screen.addResponseStatus(sb.toString());
                sb.delete(0, sb.length());
            }
            // Play sound once the read operation is complete
            _manager.playNFCOperationSuccessSound();
        } catch (final IOException e) {
            _manager.playNFCOperationFailureSound();
            _screen.add(new LabelField("Error: " + e.toString()));
        }

        finally {
            closeConnection(c);
        }
    }

    /**
     * Closes the connection
     * 
     * @param conn
     *            The connection object to be closed
     */
    public void closeConnection(final Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (final IOException e) {
            _manager.playNFCOperationFailureSound();
            _screen.add(new LabelField("Error: " + e.toString()));
        }
    }
}
