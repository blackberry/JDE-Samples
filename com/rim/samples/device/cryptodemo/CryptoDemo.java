/**
 * CryptoDemo.java
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

package com.rim.samples.device.cryptodemo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.rim.device.api.crypto.BlockDecryptor;
import net.rim.device.api.crypto.BlockEncryptor;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.PKCS5FormatterEngine;
import net.rim.device.api.crypto.PKCS5UnformatterEngine;
import net.rim.device.api.crypto.TripleDESDecryptorEngine;
import net.rim.device.api.crypto.TripleDESEncryptorEngine;
import net.rim.device.api.crypto.TripleDESKey;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;

/**
 * This sample application demonstrates basic functionality of the
 * net.rim.device.api.crypto package. For more information please see the
 * Developer Knowledge Base on www.blackberry.com/developers.
 */
public class CryptoDemo extends UiApplication {
    private final RichTextField _statusField;
    private final EditField _inputField;

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final CryptoDemo theApp = new CryptoDemo();
        theApp.enterEventDispatcher();
    }

    // Constructor
    public CryptoDemo() {
        final MainScreen screen = new MainScreen();
        screen.setTitle("Crypto Demo");

        _inputField = new EditField("Text: ", "");
        screen.add(_inputField);
        screen.add(new SeparatorField());

        _statusField =
                new RichTextField(
                        "Select 'Go' from the menu to perform the test.");
        screen.add(_statusField);

        // Add the menu item
        screen.addMenuItem(new MenuItem("Go", 100, 10) {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                final String text = _inputField.getText();
                if (text.length() > 0) {
                    runTest(text);
                } else {
                    Dialog.alert("Please enter some text");
                }
            }
        });

        pushScreen(screen);
    }

    /**
     * Encrypts, then decrypts test data
     * 
     * @param text
     *            The text to encrpt and decrypt
     */
    private void runTest(final String text) {
        try {
            // We are going to use TripleDES as the algorithm for encrypting and
            // decrypting the data. It is a very common algorithm and was chosen
            // for this reason

            // Create a new random TripleDESKey
            final TripleDESKey key = new TripleDESKey();

            // Create the encryption engine for encrypting the data
            final TripleDESEncryptorEngine encryptionEngine =
                    new TripleDESEncryptorEngine(key);

            // Due to the fact that in most cases the data that we are going to
            // encrypt will not fit perfectly into the block length of a cipher,
            // we want to use a padding algorithm to pad out the last block
            // (if necessary). We are going to use PKCS5 to do the padding
            // for us.
            final PKCS5FormatterEngine formatterEngine =
                    new PKCS5FormatterEngine(encryptionEngine);

            // Use the byte array output stream to catch the
            // encrypted information.
            final ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            // Create a block encryptor which will help us use the triple
            // des engine.
            final BlockEncryptor encryptor =
                    new BlockEncryptor(formatterEngine, outputStream);

            // Encrypt the actual data
            encryptor.write(text.getBytes());

            // Close the stream. This forces the extra bytes to be padded out if
            // there were not enough bytes to fill all of the blocks.
            encryptor.close();

            // Get the actual encrypted data
            final byte[] encryptedData = outputStream.toByteArray();

            // End of Encryption
            // -----------------------------------------------------------------------------------------------
            // Beginning of Decryption

            // We are now going to perform the decryption. We want to ensure
            // that the message we get back is the same as the original. Note
            // that since this is a symmetric algorithm we want to use the same
            // key as was used for encryption.
            final TripleDESDecryptorEngine decryptorEngine =
                    new TripleDESDecryptorEngine(key);

            // Create the unformatter engine that will remove any of the
            // padding bytes.
            final PKCS5UnformatterEngine unformatterEngine =
                    new PKCS5UnformatterEngine(decryptorEngine);

            // Set up an input stream to hand the encrypted data to the
            // block decryptor.
            final ByteArrayInputStream inputStream =
                    new ByteArrayInputStream(encryptedData);

            // Create the block decryptor passing in the unformatter engine and
            // the encrypted data.
            final BlockDecryptor decryptor =
                    new BlockDecryptor(unformatterEngine, inputStream);

            // Now we want to read from the stream. We are going to read the
            // data 10 bytes at a time and then add the new data to the
            // decryptedData array. It is important to note that for
            // efficiency one would most likely want to use a larger
            // value than 10. We use a small value so that we can
            // demonstrate several iterations through the loop.
            final byte[] temp = new byte[10];
            final DataBuffer db = new DataBuffer();

            for (;;) {
                final int bytesRead = decryptor.read(temp);

                if (bytesRead <= 0) {
                    // We have run out of information to read, bail out of loop
                    break;
                }

                db.write(temp, 0, bytesRead);
            }

            // Now we want to ensure that the decrypted data is the same as the
            // data we passed into the encryptor.
            final byte[] decryptedData = db.toArray();

            final String decryptedText = new String(decryptedData);

            if (Arrays.equals(text.getBytes(), decryptedData)) {
                _statusField
                        .setText("Test passed, the message is identical.\nDecrypted text: "
                                + decryptedText);
            } else {
                _statusField
                        .setText("Test failed, the messages are different.\nDecrypted text: "
                                + decryptedText);
            }
        } catch (final CryptoTokenException e) {
            errorDialog(e.toString());
        } catch (final CryptoUnsupportedOperationException e) {
            errorDialog(e.toString());
        } catch (final IOException e) {
            errorDialog(e.toString());
        }
    }

    /**
     * Presents a dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     */
    public static void errorDialog(final String message) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(message);
            }
        });
    }
}
