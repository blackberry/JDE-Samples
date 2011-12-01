/*
 * MyCryptoSmartCardSession.java
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

package com.rim.samples.device.smartcarddriverdemo;

import net.rim.device.api.crypto.CryptoSmartCardKeyStoreData;
import net.rim.device.api.crypto.CryptoSmartCardSession;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.RSACryptoToken;
import net.rim.device.api.crypto.RSAPrivateKey;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.UnsupportedCryptoSystemException;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateParsingException;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.smartcard.CommandAPDU;
import net.rim.device.api.smartcard.ResponseAPDU;
import net.rim.device.api.smartcard.SmartCard;
import net.rim.device.api.smartcard.SmartCardException;
import net.rim.device.api.smartcard.SmartCardID;
import net.rim.device.api.smartcard.SmartCardLockedException;
import net.rim.device.api.smartcard.SmartCardReaderSession;
import net.rim.device.api.util.Arrays;

/**
 * A class which represents a communication session with a physical smart card.
 * Over this session, APDUs may be exchanged with the smart card to provide the
 * desired functionality. Sessions are short lived and should not be held open
 * when not in use. As a security precaution, only one open session is allowed
 * to exist per SmartCardReader, and subsequent openSession requests will block
 * until the current session is closed.
 */
public class MyCryptoSmartCardSession extends CryptoSmartCardSession {
    // We assume that the smart card has 3 certificates identified by: ID_PKI,
    // SIGNING_PKI and ENCRYPION_PKI. Your particular smart card may have a
    // different number of certificates or be identified differently. These 3
    // certificates are merely an example of what a smart card might contain.
    private static final byte ID_PKI = (byte) 0x00;
    private static final byte SIGNING_PKI = (byte) 0x01;
    private static final byte ENCRYPTION_PKI = (byte) 0x02;

    private static final String WAITING_MSG = "Please Wait";
    private static final String ID_STRING = "John H. Smith";
    private static final String ID_CERT = "ID Certificate";
    private static final String SIGNING_CERT = "Signing Certificate";
    private static final String ENCRYPTION_CERT = "Encryption Certificate";

    /**
     * Constructs a <code>MyCryptoSmartCardSession</code> object.
     * 
     * @param smartCard
     *            the smart card type associated with this session.
     * @param readerSession
     *            the reader session which will be used to send the commands to.
     */
    public MyCryptoSmartCardSession(final SmartCard smartCard,
            final SmartCardReaderSession readerSession) {
        super(smartCard, readerSession);
    }

    /**
     * Closes the session with the smartcard. Implementations should not close
     * the underlying SmartCardReaderSession; this will be done by the smart
     * card framework or the application.
     */
    protected void closeImpl() {
        // Do any session cleanup needed here.
    }

    /**
     * Returns the maximum number of login attempts allowed, or
     * Integer.MAX_VALUE if an infinite number of attempts are allowed.
     */
    protected int getMaxLoginAttemptsImpl() throws SmartCardException {
        return 5;
    }

    /**
     * Returns the remaining number of login attempts allowed before the
     * smartcard will lock, or Integer.MAX_VALUE if the smart card will not
     * lock.
     */
    protected int getRemainingLoginAttemptsImpl() throws SmartCardException {
        // Return then number of remaining attempts.
        return 4;
    }

    /**
     * Attempts to log the user into the smart card with the given password.
     * Implementation should not bring up UI.
     */
    protected boolean loginImpl(final String password)
            throws SmartCardException {
        // TODO: Create a CommandAPDU which your smart card will understand.
        final CommandAPDU command =
                new CommandAPDU((byte) 0x00, (byte) 0x20, (byte) 0x00,
                        (byte) 0x00);
        command.setLcData(password.getBytes());

        final ResponseAPDU response = new ResponseAPDU();

        sendAPDU(command, response);

        // TODO: Check for response codes specific to your smart card.
        if (response.checkStatusWords((byte) 0x90, (byte) 0x00)) {
            return true;
        } else if (response.checkStatusWords((byte) 0x64, (byte) 0xF8)) {
            throw new SmartCardLockedException();
        } else {
            // Authentication failed.
            return false;
        }
    }

    /**
     * Get a <code>SmartCardID</code> associated with the specific smart card
     * which this session is using. Each smart card *must* return a unique ID,
     * otherwise the system will not be able to distinguish between two
     * different smart cards.
     */
    protected SmartCardID getSmartCardIDImpl() throws SmartCardException {
        // Retrieve a unique ID off the card.
        final byte[] uniqueCardData =
                new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                        0x09, 0x0a, 0x0b };

        // Convert byte array to long.
        final SHA1Digest digest = new SHA1Digest();
        digest.update(uniqueCardData);
        final long idLong =
                byteArrayToLong(Arrays.copy(digest.getDigest(), 0, 8));

        // Using friendly display name.
        return new SmartCardID(idLong, ID_STRING, getSmartCard());
    }

    /**
     * Converts <code>array</code> into a long integer (Note: the returned value
     * should be considered as an unsigned value).
     * 
     * @throws IllegalArgumentException
     *             if <code>array</code> contains a number bigger than 64 bits.
     * 
     *             Note: If your smart card driver is only designed to work with
     *             BlackBerry 4.2+, you can replace this method with a call to
     *             CryptoByteArrayArithmetic.valueOf( byte [] ).
     */
    private long byteArrayToLong(byte[] array) {
        if (array == null) {
            throw new IllegalArgumentException();
        }

        // Remove the leading zeros from given byte[] and returns a new byte[]
        // without them.
        int zeros = 0;
        for (int i = 0; i < array.length && array[i] == 0; i++) {
            zeros++;
        }
        if (zeros != 0) {
            array = Arrays.copy(array, zeros, array.length - zeros);
        }

        final int length = array.length;
        if (length > 8) {
            throw new IllegalArgumentException();
        }

        long n = 0;

        for (int i = 0; i < length; i++) {
            n <<= 8;
            n += array[i] & 0xff;
        }

        return n;
    }

    /**
     * Returns random bytes of data from the smart card's internal RNG.
     */
    protected byte[] getRandomBytesImpl(final int maxBytes)
            throws SmartCardException {
        // TODO: Create a CommandAPDU which your smart card will understand.
        final CommandAPDU command =
                new CommandAPDU((byte) 0x00, (byte) 0x4C, (byte) 0x00,
                        (byte) 0x00, maxBytes);

        final ResponseAPDU response = new ResponseAPDU();

        sendAPDU(command, response);

        // TODO: Check for response codes specific to your smart card.
        if (response.checkStatusWords((byte) 0x90, (byte) 0x00)) {
            return response.getData();
        }

        return null;
    }

    /**
     * Extract the certificates off of the smart card.
     * 
     * @return An array of certificates which are present on the smart card.
     */
    protected CryptoSmartCardKeyStoreData[] getKeyStoreDataArrayImpl()
            throws SmartCardException, CryptoTokenException {
        try {
            // Show a progress dialog to the user as this operation may take a
            // long time.
            displayProgressDialog(WAITING_MSG, 4);

            // The certificates need to be associated with a particular smart
            // card.
            final SmartCardID smartCardID = getSmartCardID();

            final RSACryptoToken token = new MyRSACryptoToken();
            final RSACryptoSystem cryptoSystem =
                    new RSACryptoSystem(token, 1024);
            RSAPrivateKey privateKey;

            final CryptoSmartCardKeyStoreData[] keyStoreDataArray =
                    new CryptoSmartCardKeyStoreData[3];

            // For this sample we use a hard coded certificate contained below.
            // This encoding would be extracted from the card using a series of
            // APDU commands.
            Certificate certificate = null;
            try {
                certificate = new X509Certificate(CERTIFICATE_ENCODING);
            } catch (final CertificateParsingException e) {
                // Should not happen.
            }

            stepProgressDialog(1);

            privateKey =
                    new RSAPrivateKey(cryptoSystem, new MyCryptoTokenData(
                            smartCardID, ID_PKI));
            keyStoreDataArray[0] =
                    new CryptoSmartCardKeyStoreData(null, ID_CERT, privateKey,
                            null, KeyStore.SECURITY_LEVEL_HIGH, certificate,
                            null, null, 0);

            stepProgressDialog(1);

            privateKey =
                    new RSAPrivateKey(cryptoSystem, new MyCryptoTokenData(
                            smartCardID, SIGNING_PKI));
            keyStoreDataArray[1] =
                    new CryptoSmartCardKeyStoreData(null, SIGNING_CERT,
                            privateKey, null, KeyStore.SECURITY_LEVEL_HIGH,
                            certificate, null, null, 0);

            stepProgressDialog(1);

            privateKey =
                    new RSAPrivateKey(cryptoSystem, new MyCryptoTokenData(
                            smartCardID, ENCRYPTION_PKI));
            keyStoreDataArray[2] =
                    new CryptoSmartCardKeyStoreData(null, ENCRYPTION_CERT,
                            privateKey, null, KeyStore.SECURITY_LEVEL_HIGH,
                            certificate, null, null, 0);

            stepProgressDialog(1);

            // Sleep so the user sees the last step of the progress dialog move
            // to 100%
            try {
                Thread.sleep(250);
            } catch (final InterruptedException e) {
            }

            dismissProgressDialog();

            return keyStoreDataArray;

        } catch (final CryptoUnsupportedOperationException e) {
        } catch (final UnsupportedCryptoSystemException e) {
        } catch (final CryptoTokenException e) {
        }

        throw new SmartCardException();
    }

    /**
     * Send some data to the smart card to be signed or decrypted.
     */
    /* package */void signDecrypt(final RSACryptoSystem cryptoSystem,
            final MyCryptoTokenData privateKeyData, final byte[] input,
            final int inputOffset, final byte[] output, final int outputOffset)
            throws SmartCardException {
        // Check for nulls.
        if (cryptoSystem == null || privateKeyData == null || input == null
                || output == null) {
            throw new IllegalArgumentException();
        }

        // Validate the input parameters.
        final int modulusLength = cryptoSystem.getModulusLength();

        if (input.length < inputOffset + modulusLength
                || output.length < outputOffset + modulusLength) {
            throw new IllegalArgumentException();
        }

        // Construct the response Application Protocol Data Unit.
        final ResponseAPDU response = new ResponseAPDU();

        // Construct the command and set its information.
        // TODO: Create a CommandAPDU which your smart card will understand.
        final CommandAPDU signAPDU =
                new CommandAPDU((byte) 0x80, (byte) 0x56, (byte) 0x00,
                        (byte) 0x00, modulusLength);
        signAPDU.setLcData(input, inputOffset, input.length - inputOffset);

        // Send the command to the smart card
        sendAPDU(signAPDU, response);

        // Validate the status words of the response.
        // TODO: Check for response codes specific to your smart card.
        if (response.checkStatusWords((byte) 0x90, (byte) 0x00)) {
            final byte[] responseData = response.getData();
            System.arraycopy(responseData, 0, output, outputOffset,
                    responseData.length);
        } else {
            throw new SmartCardException("Invalid response code, sw1="
                    + Integer.toHexString(response.getSW1() & 0xff) + " sw2="
                    + Integer.toHexString(response.getSW2() & 0xff));
        }
    }

    private static byte[] CERTIFICATE_ENCODING = new byte[] { (byte) 0x30,
            (byte) 0x82, (byte) 0x03, (byte) 0x2d, (byte) 0x30, (byte) 0x82,
            (byte) 0x02, (byte) 0x96, (byte) 0xa0, (byte) 0x03, (byte) 0x02,
            (byte) 0x01, (byte) 0x02, (byte) 0x02, (byte) 0x01, (byte) 0x00,
            (byte) 0x30, (byte) 0x0d, (byte) 0x06, (byte) 0x09, (byte) 0x2a,
            (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xf7, (byte) 0x0d,
            (byte) 0x01, (byte) 0x01, (byte) 0x04, (byte) 0x05, (byte) 0x00,
            (byte) 0x30, (byte) 0x81, (byte) 0xd1, (byte) 0x31, (byte) 0x0b,
            (byte) 0x30, (byte) 0x09, (byte) 0x06, (byte) 0x03, (byte) 0x55,
            (byte) 0x04, (byte) 0x06, (byte) 0x13, (byte) 0x02, (byte) 0x5a,
            (byte) 0x41, (byte) 0x31, (byte) 0x15, (byte) 0x30, (byte) 0x13,
            (byte) 0x06, (byte) 0x03, (byte) 0x55, (byte) 0x04, (byte) 0x08,
            (byte) 0x13, (byte) 0x0c, (byte) 0x57, (byte) 0x65, (byte) 0x73,
            (byte) 0x74, (byte) 0x65, (byte) 0x72, (byte) 0x6e, (byte) 0x20,
            (byte) 0x43, (byte) 0x61, (byte) 0x70, (byte) 0x65, (byte) 0x31,
            (byte) 0x12, (byte) 0x30, (byte) 0x10, (byte) 0x06, (byte) 0x03,
            (byte) 0x55, (byte) 0x04, (byte) 0x07, (byte) 0x13, (byte) 0x09,
            (byte) 0x43, (byte) 0x61, (byte) 0x70, (byte) 0x65, (byte) 0x20,
            (byte) 0x54, (byte) 0x6f, (byte) 0x77, (byte) 0x6e, (byte) 0x31,
            (byte) 0x1a, (byte) 0x30, (byte) 0x18, (byte) 0x06, (byte) 0x03,
            (byte) 0x55, (byte) 0x04, (byte) 0x0a, (byte) 0x13, (byte) 0x11,
            (byte) 0x54, (byte) 0x68, (byte) 0x61, (byte) 0x77, (byte) 0x74,
            (byte) 0x65, (byte) 0x20, (byte) 0x43, (byte) 0x6f, (byte) 0x6e,
            (byte) 0x73, (byte) 0x75, (byte) 0x6c, (byte) 0x74, (byte) 0x69,
            (byte) 0x6e, (byte) 0x67, (byte) 0x31, (byte) 0x28, (byte) 0x30,
            (byte) 0x26, (byte) 0x06, (byte) 0x03, (byte) 0x55, (byte) 0x04,
            (byte) 0x0b, (byte) 0x13, (byte) 0x1f, (byte) 0x43, (byte) 0x65,
            (byte) 0x72, (byte) 0x74, (byte) 0x69, (byte) 0x66, (byte) 0x69,
            (byte) 0x63, (byte) 0x61, (byte) 0x74, (byte) 0x69, (byte) 0x6f,
            (byte) 0x6e, (byte) 0x20, (byte) 0x53, (byte) 0x65, (byte) 0x72,
            (byte) 0x76, (byte) 0x69, (byte) 0x63, (byte) 0x65, (byte) 0x73,
            (byte) 0x20, (byte) 0x44, (byte) 0x69, (byte) 0x76, (byte) 0x69,
            (byte) 0x73, (byte) 0x69, (byte) 0x6f, (byte) 0x6e, (byte) 0x31,
            (byte) 0x24, (byte) 0x30, (byte) 0x22, (byte) 0x06, (byte) 0x03,
            (byte) 0x55, (byte) 0x04, (byte) 0x03, (byte) 0x13, (byte) 0x1b,
            (byte) 0x54, (byte) 0x68, (byte) 0x61, (byte) 0x77, (byte) 0x74,
            (byte) 0x65, (byte) 0x20, (byte) 0x50, (byte) 0x65, (byte) 0x72,
            (byte) 0x73, (byte) 0x6f, (byte) 0x6e, (byte) 0x61, (byte) 0x6c,
            (byte) 0x20, (byte) 0x46, (byte) 0x72, (byte) 0x65, (byte) 0x65,
            (byte) 0x6d, (byte) 0x61, (byte) 0x69, (byte) 0x6c, (byte) 0x20,
            (byte) 0x43, (byte) 0x41, (byte) 0x31, (byte) 0x2b, (byte) 0x30,
            (byte) 0x29, (byte) 0x06, (byte) 0x09, (byte) 0x2a, (byte) 0x86,
            (byte) 0x48, (byte) 0x86, (byte) 0xf7, (byte) 0x0d, (byte) 0x01,
            (byte) 0x09, (byte) 0x01, (byte) 0x16, (byte) 0x1c, (byte) 0x70,
            (byte) 0x65, (byte) 0x72, (byte) 0x73, (byte) 0x6f, (byte) 0x6e,
            (byte) 0x61, (byte) 0x6c, (byte) 0x2d, (byte) 0x66, (byte) 0x72,
            (byte) 0x65, (byte) 0x65, (byte) 0x6d, (byte) 0x61, (byte) 0x69,
            (byte) 0x6c, (byte) 0x40, (byte) 0x74, (byte) 0x68, (byte) 0x61,
            (byte) 0x77, (byte) 0x74, (byte) 0x65, (byte) 0x2e, (byte) 0x63,
            (byte) 0x6f, (byte) 0x6d, (byte) 0x30, (byte) 0x1e, (byte) 0x17,
            (byte) 0x0d, (byte) 0x39, (byte) 0x36, (byte) 0x30, (byte) 0x31,
            (byte) 0x30, (byte) 0x31, (byte) 0x30, (byte) 0x30, (byte) 0x30,
            (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x5a, (byte) 0x17,
            (byte) 0x0d, (byte) 0x32, (byte) 0x30, (byte) 0x31, (byte) 0x32,
            (byte) 0x33, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x35,
            (byte) 0x39, (byte) 0x35, (byte) 0x39, (byte) 0x5a, (byte) 0x30,
            (byte) 0x81, (byte) 0xd1, (byte) 0x31, (byte) 0x0b, (byte) 0x30,
            (byte) 0x09, (byte) 0x06, (byte) 0x03, (byte) 0x55, (byte) 0x04,
            (byte) 0x06, (byte) 0x13, (byte) 0x02, (byte) 0x5a, (byte) 0x41,
            (byte) 0x31, (byte) 0x15, (byte) 0x30, (byte) 0x13, (byte) 0x06,
            (byte) 0x03, (byte) 0x55, (byte) 0x04, (byte) 0x08, (byte) 0x13,
            (byte) 0x0c, (byte) 0x57, (byte) 0x65, (byte) 0x73, (byte) 0x74,
            (byte) 0x65, (byte) 0x72, (byte) 0x6e, (byte) 0x20, (byte) 0x43,
            (byte) 0x61, (byte) 0x70, (byte) 0x65, (byte) 0x31, (byte) 0x12,
            (byte) 0x30, (byte) 0x10, (byte) 0x06, (byte) 0x03, (byte) 0x55,
            (byte) 0x04, (byte) 0x07, (byte) 0x13, (byte) 0x09, (byte) 0x43,
            (byte) 0x61, (byte) 0x70, (byte) 0x65, (byte) 0x20, (byte) 0x54,
            (byte) 0x6f, (byte) 0x77, (byte) 0x6e, (byte) 0x31, (byte) 0x1a,
            (byte) 0x30, (byte) 0x18, (byte) 0x06, (byte) 0x03, (byte) 0x55,
            (byte) 0x04, (byte) 0x0a, (byte) 0x13, (byte) 0x11, (byte) 0x54,
            (byte) 0x68, (byte) 0x61, (byte) 0x77, (byte) 0x74, (byte) 0x65,
            (byte) 0x20, (byte) 0x43, (byte) 0x6f, (byte) 0x6e, (byte) 0x73,
            (byte) 0x75, (byte) 0x6c, (byte) 0x74, (byte) 0x69, (byte) 0x6e,
            (byte) 0x67, (byte) 0x31, (byte) 0x28, (byte) 0x30, (byte) 0x26,
            (byte) 0x06, (byte) 0x03, (byte) 0x55, (byte) 0x04, (byte) 0x0b,
            (byte) 0x13, (byte) 0x1f, (byte) 0x43, (byte) 0x65, (byte) 0x72,
            (byte) 0x74, (byte) 0x69, (byte) 0x66, (byte) 0x69, (byte) 0x63,
            (byte) 0x61, (byte) 0x74, (byte) 0x69, (byte) 0x6f, (byte) 0x6e,
            (byte) 0x20, (byte) 0x53, (byte) 0x65, (byte) 0x72, (byte) 0x76,
            (byte) 0x69, (byte) 0x63, (byte) 0x65, (byte) 0x73, (byte) 0x20,
            (byte) 0x44, (byte) 0x69, (byte) 0x76, (byte) 0x69, (byte) 0x73,
            (byte) 0x69, (byte) 0x6f, (byte) 0x6e, (byte) 0x31, (byte) 0x24,
            (byte) 0x30, (byte) 0x22, (byte) 0x06, (byte) 0x03, (byte) 0x55,
            (byte) 0x04, (byte) 0x03, (byte) 0x13, (byte) 0x1b, (byte) 0x54,
            (byte) 0x68, (byte) 0x61, (byte) 0x77, (byte) 0x74, (byte) 0x65,
            (byte) 0x20, (byte) 0x50, (byte) 0x65, (byte) 0x72, (byte) 0x73,
            (byte) 0x6f, (byte) 0x6e, (byte) 0x61, (byte) 0x6c, (byte) 0x20,
            (byte) 0x46, (byte) 0x72, (byte) 0x65, (byte) 0x65, (byte) 0x6d,
            (byte) 0x61, (byte) 0x69, (byte) 0x6c, (byte) 0x20, (byte) 0x43,
            (byte) 0x41, (byte) 0x31, (byte) 0x2b, (byte) 0x30, (byte) 0x29,
            (byte) 0x06, (byte) 0x09, (byte) 0x2a, (byte) 0x86, (byte) 0x48,
            (byte) 0x86, (byte) 0xf7, (byte) 0x0d, (byte) 0x01, (byte) 0x09,
            (byte) 0x01, (byte) 0x16, (byte) 0x1c, (byte) 0x70, (byte) 0x65,
            (byte) 0x72, (byte) 0x73, (byte) 0x6f, (byte) 0x6e, (byte) 0x61,
            (byte) 0x6c, (byte) 0x2d, (byte) 0x66, (byte) 0x72, (byte) 0x65,
            (byte) 0x65, (byte) 0x6d, (byte) 0x61, (byte) 0x69, (byte) 0x6c,
            (byte) 0x40, (byte) 0x74, (byte) 0x68, (byte) 0x61, (byte) 0x77,
            (byte) 0x74, (byte) 0x65, (byte) 0x2e, (byte) 0x63, (byte) 0x6f,
            (byte) 0x6d, (byte) 0x30, (byte) 0x81, (byte) 0x9f, (byte) 0x30,
            (byte) 0x0d, (byte) 0x06, (byte) 0x09, (byte) 0x2a, (byte) 0x86,
            (byte) 0x48, (byte) 0x86, (byte) 0xf7, (byte) 0x0d, (byte) 0x01,
            (byte) 0x01, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x03,
            (byte) 0x81, (byte) 0x8d, (byte) 0x00, (byte) 0x30, (byte) 0x81,
            (byte) 0x89, (byte) 0x02, (byte) 0x81, (byte) 0x81, (byte) 0x00,
            (byte) 0xd4, (byte) 0x69, (byte) 0xd7, (byte) 0xd4, (byte) 0xb0,
            (byte) 0x94, (byte) 0x64, (byte) 0x5b, (byte) 0x71, (byte) 0xe9,
            (byte) 0x47, (byte) 0xd8, (byte) 0x0c, (byte) 0x51, (byte) 0xb6,
            (byte) 0xea, (byte) 0x72, (byte) 0x91, (byte) 0xb0, (byte) 0x84,
            (byte) 0x5e, (byte) 0x7d, (byte) 0x2d, (byte) 0x0d, (byte) 0x8f,
            (byte) 0x7b, (byte) 0x12, (byte) 0xdf, (byte) 0x85, (byte) 0x25,
            (byte) 0x75, (byte) 0x28, (byte) 0x74, (byte) 0x3a, (byte) 0x42,
            (byte) 0x2c, (byte) 0x63, (byte) 0x27, (byte) 0x9f, (byte) 0x95,
            (byte) 0x7b, (byte) 0x4b, (byte) 0xef, (byte) 0x7e, (byte) 0x19,
            (byte) 0x87, (byte) 0x1d, (byte) 0x86, (byte) 0xea, (byte) 0xa3,
            (byte) 0xdd, (byte) 0xb9, (byte) 0xce, (byte) 0x96, (byte) 0x64,
            (byte) 0x1a, (byte) 0xc2, (byte) 0x14, (byte) 0x6e, (byte) 0x44,
            (byte) 0xac, (byte) 0x7c, (byte) 0xe6, (byte) 0x8f, (byte) 0xe8,
            (byte) 0x4d, (byte) 0x0f, (byte) 0x71, (byte) 0x1f, (byte) 0x40,
            (byte) 0x38, (byte) 0xa6, (byte) 0x00, (byte) 0xa3, (byte) 0x87,
            (byte) 0x78, (byte) 0xf6, (byte) 0xf9, (byte) 0x94, (byte) 0x86,
            (byte) 0x5e, (byte) 0xad, (byte) 0xea, (byte) 0xc0, (byte) 0x5e,
            (byte) 0x76, (byte) 0xeb, (byte) 0xd9, (byte) 0x14, (byte) 0xa3,
            (byte) 0x5d, (byte) 0x6e, (byte) 0x7a, (byte) 0x7c, (byte) 0x0c,
            (byte) 0xa5, (byte) 0x4b, (byte) 0x55, (byte) 0x7f, (byte) 0x06,
            (byte) 0x19, (byte) 0x29, (byte) 0x7f, (byte) 0x9e, (byte) 0x9a,
            (byte) 0x26, (byte) 0xd5, (byte) 0x6a, (byte) 0xbb, (byte) 0x38,
            (byte) 0x24, (byte) 0x08, (byte) 0x6a, (byte) 0x98, (byte) 0xc7,
            (byte) 0xb1, (byte) 0xda, (byte) 0xa3, (byte) 0x98, (byte) 0x91,
            (byte) 0xfd, (byte) 0x79, (byte) 0xdb, (byte) 0xe5, (byte) 0x5a,
            (byte) 0xc4, (byte) 0x1c, (byte) 0xb9, (byte) 0x02, (byte) 0x03,
            (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0xa3, (byte) 0x13,
            (byte) 0x30, (byte) 0x11, (byte) 0x30, (byte) 0x0f, (byte) 0x06,
            (byte) 0x03, (byte) 0x55, (byte) 0x1d, (byte) 0x13, (byte) 0x01,
            (byte) 0x01, (byte) 0xff, (byte) 0x04, (byte) 0x05, (byte) 0x30,
            (byte) 0x03, (byte) 0x01, (byte) 0x01, (byte) 0xff, (byte) 0x30,
            (byte) 0x0d, (byte) 0x06, (byte) 0x09, (byte) 0x2a, (byte) 0x86,
            (byte) 0x48, (byte) 0x86, (byte) 0xf7, (byte) 0x0d, (byte) 0x01,
            (byte) 0x01, (byte) 0x04, (byte) 0x05, (byte) 0x00, (byte) 0x03,
            (byte) 0x81, (byte) 0x81, (byte) 0x00, (byte) 0xc7, (byte) 0xec,
            (byte) 0x92, (byte) 0x7e, (byte) 0x4e, (byte) 0xf8, (byte) 0xf5,
            (byte) 0x96, (byte) 0xa5, (byte) 0x67, (byte) 0x62, (byte) 0x2a,
            (byte) 0xa4, (byte) 0xf0, (byte) 0x4d, (byte) 0x11, (byte) 0x60,
            (byte) 0xd0, (byte) 0x6f, (byte) 0x8d, (byte) 0x60, (byte) 0x58,
            (byte) 0x61, (byte) 0xac, (byte) 0x26, (byte) 0xbb, (byte) 0x52,
            (byte) 0x35, (byte) 0x5c, (byte) 0x08, (byte) 0xcf, (byte) 0x30,
            (byte) 0xfb, (byte) 0xa8, (byte) 0x4a, (byte) 0x96, (byte) 0x8a,
            (byte) 0x1f, (byte) 0x62, (byte) 0x42, (byte) 0x23, (byte) 0x8c,
            (byte) 0x17, (byte) 0x0f, (byte) 0xf4, (byte) 0xba, (byte) 0x64,
            (byte) 0x9c, (byte) 0x17, (byte) 0xac, (byte) 0x47, (byte) 0x29,
            (byte) 0xdf, (byte) 0x9d, (byte) 0x98, (byte) 0x5e, (byte) 0xd2,
            (byte) 0x6c, (byte) 0x60, (byte) 0x71, (byte) 0x5c, (byte) 0xa2,
            (byte) 0xac, (byte) 0xdc, (byte) 0x79, (byte) 0xe3, (byte) 0xe7,
            (byte) 0x6e, (byte) 0x00, (byte) 0x47, (byte) 0x1f, (byte) 0xb5,
            (byte) 0x0d, (byte) 0x28, (byte) 0xe8, (byte) 0x02, (byte) 0x9d,
            (byte) 0xe4, (byte) 0x9a, (byte) 0xfd, (byte) 0x13, (byte) 0xf4,
            (byte) 0xa6, (byte) 0xd9, (byte) 0x7c, (byte) 0xb1, (byte) 0xf8,
            (byte) 0xdc, (byte) 0x5f, (byte) 0x23, (byte) 0x26, (byte) 0x09,
            (byte) 0x91, (byte) 0x80, (byte) 0x73, (byte) 0xd0, (byte) 0x14,
            (byte) 0x1b, (byte) 0xde, (byte) 0x43, (byte) 0xa9, (byte) 0x83,
            (byte) 0x25, (byte) 0xf2, (byte) 0xe6, (byte) 0x9c, (byte) 0x2f,
            (byte) 0x15, (byte) 0xca, (byte) 0xfe, (byte) 0xa6, (byte) 0xab,
            (byte) 0x8a, (byte) 0x07, (byte) 0x75, (byte) 0x8b, (byte) 0x0c,
            (byte) 0xdd, (byte) 0x51, (byte) 0x84, (byte) 0x6b, (byte) 0xe4,
            (byte) 0xf8, (byte) 0xd1, (byte) 0xce, (byte) 0x77, (byte) 0xa2,
            (byte) 0x81 };
}
