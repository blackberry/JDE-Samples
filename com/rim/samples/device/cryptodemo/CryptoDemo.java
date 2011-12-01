/**
 * CryptoDemo.java
 * A simple crypto example
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
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;

/**
 * This class provides demonstrates basic functionality of the crypto library
 * with a very basic compilation of cypto code. For more information on how to
 * write crypto code please see the javadocs and check out our tutorial in the
 * Developers Knowledge Base. The javadocs contain additional sample code to
 * assist you.
 */
public class CryptoDemo extends UiApplication {

    private final RichTextField _status;

    /**
     * Entry point for Application.
     */
    public static void main(final String[] args) {
        final CryptoDemo theApp = new CryptoDemo();
        theApp.enterEventDispatcher();
    }

    /**
     * Constructor
     */
    public CryptoDemo() {
        final MainScreen screen = new MainScreen();
        screen.setTitle(new LabelField("Crypto Demo", DrawStyle.ELLIPSIS
                | Field.USE_ALL_WIDTH));

        _status =
                new RichTextField(
                        "Select 'Go' from the menu to perform the test.");
        screen.add(_status);

        // Add the menu item.
        screen.addMenuItem(new MenuItem("Go", 100, 10) {
            public void run() {
                go();
            }
        });

        pushScreen(screen);
    }

    /**
     * <p>
     * The test method containing the sample code.
     * <p>
     * We want to create a sample that will encrypt and decrypt test data to
     * demonstrate how a simple crypto example can be implemented.
     */
    public void go() {
        try {
            // We are going to use TripleDES as the algorithm for encrypting and
            // decrypting
            // the data. It is a very common algorithm and was chosen for this
            // reason.

            // Here is the data that we are going to encrypt.
            final String message = "Using the crypto api is as easy as pie.";

            // Create a new random TripleDESKey.
            final TripleDESKey key = new TripleDESKey();

            // Create the encryption engine for encrypting the data.
            final TripleDESEncryptorEngine encryptionEngine =
                    new TripleDESEncryptorEngine(key);

            // Due to the fact that in most cases the data that we are going to
            // encrypt will
            // not fit perfectly into the block length of a cipher, we want to
            // use a padding
            // algorithm to pad out the last block (if necessary). We are going
            // to use PKCS5
            // to do the padding for us.
            final PKCS5FormatterEngine formatterEngine =
                    new PKCS5FormatterEngine(encryptionEngine);

            // Use the byte array output stream to catch the encrypted
            // information.
            final ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            // Create a block encryptor which will help us use the triple des
            // engine.
            final BlockEncryptor encryptor =
                    new BlockEncryptor(formatterEngine, outputStream);

            // Encrypt the actual data.
            encryptor.write(message.getBytes());

            // Close the stream. This forces the extra bytes to be padded out if
            // there were
            // not enough bytes to fill all of the blocks.
            encryptor.close();

            // Get the actual encrypted data.
            final byte[] encryptedData = outputStream.toByteArray();

            // End of Encryption.
            // -----------------------------------------------------------------------------------------------
            // Beginning of Decryption.

            // We are now going to perform the decryption. We want to ensure
            // that the
            // message we get back is same as the original. Note that since this
            // is a
            // symmetric algorithm we want to use the same key as before.
            final TripleDESDecryptorEngine decryptorEngine =
                    new TripleDESDecryptorEngine(key);

            // Create the unformatter engine that will remove any of the padding
            // bytes.
            final PKCS5UnformatterEngine unformatterEngine =
                    new PKCS5UnformatterEngine(decryptorEngine);

            // Set up an input stream to hand the encrypted data to the block
            // decryptor.
            final ByteArrayInputStream inputStream =
                    new ByteArrayInputStream(encryptedData);

            // Create the block decryptor passing in the unformatter engine and
            // the
            // encrypted data.
            final BlockDecryptor decryptor =
                    new BlockDecryptor(unformatterEngine, inputStream);

            // Now we want to read from the stream. We are going to read the
            // data 10 bytes
            // at a time and then add that new data to the decryptedData array.
            // It is
            // important to note that for efficiency one would most likely want
            // to use a
            // larger value than 10. We use a small value so that we can
            // demonstrate
            // several iterations through the loop.
            final byte[] temp = new byte[10];
            final DataBuffer db = new DataBuffer();

            for (;;) {
                final int bytesRead = decryptor.read(temp);

                if (bytesRead <= 0) {
                    // We have run out of information to read, bail out of loop.
                    break;
                }

                db.write(temp, 0, bytesRead);
            }

            // Now we want to ensure that the decrypted data is the same as the
            // data we
            // passed into the encryptor.
            final byte[] decryptedData = db.toArray();

            if (Arrays.equals(message.getBytes(), decryptedData)) {
                // They are the same.
                _status.setText("Test Passed.  The message is identical.");

            } else {
                // They differ.
                _status.setText("Test Failed.  The messages are different.");
            }
        } catch (final CryptoTokenException e) {
            System.out.println(e.toString());
        } catch (final CryptoUnsupportedOperationException e) {
            System.out.println(e.toString());
        } catch (final IOException e) {
            System.out.println(e.toString());
        }
    }
}
