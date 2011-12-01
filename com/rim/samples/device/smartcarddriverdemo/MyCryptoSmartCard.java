/*
 * MyCryptoSmartCard.java
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

import net.rim.device.api.crypto.CryptoSmartCard;
import net.rim.device.api.crypto.CryptoToken;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.smartcard.AnswerToReset;
import net.rim.device.api.smartcard.SmartCardCapabilities;
import net.rim.device.api.smartcard.SmartCardException;
import net.rim.device.api.smartcard.SmartCardFactory;
import net.rim.device.api.smartcard.SmartCardReaderSession;
import net.rim.device.api.smartcard.SmartCardSession;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Persistable;

/**
 * This class represents a kind (or model or family) of a physical smart card.
 * There should only be one instance of this class in the system at a time,
 * which is referenced by the SmartCardFactory.
 */
public class MyCryptoSmartCard extends CryptoSmartCard implements Persistable {
    private final static byte MY_ATR[] = { (byte) 0x3b, (byte) 0x7d,
            (byte) 0x11, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31,
            (byte) 0x80, (byte) 0x71, (byte) 0x8e, (byte) 0x64, (byte) 0x86,
            (byte) 0xd6, (byte) 0x01, (byte) 0x00, (byte) 0x81, (byte) 0x90,
            (byte) 0x00 };

    private final static AnswerToReset _myATR = new AnswerToReset(MY_ATR);

    private static String LABEL = "RIM Sample";
    private static String DISPLAY_SETTINGS =
            "Show driver properties/settings now";
    private static String RSA = "RSA";

    /**
     * Called on startup of the device. Register this driver with the smart card
     * factory. When you do this, your driver is automatically registered with
     * the user authenticator framework, which allows your smart card to be used
     * as a second factor of authentication when unlocking the BlackBerry.
     * 
     * Under the "Application" tab in this project's properties, make sure the
     * project type is set to "Library" and "Auto-run on startup" is checked.
     * The demo is not intended to be run on the simulator or on a real device,
     * but rather it is just instructional code.
     */
    public static void libMain(final String args[]) {
        try {
            SmartCardFactory.addSmartCard(new MyCryptoSmartCard());
        } catch (final ControlledAccessException cae) {
            // Application control may not allow your driver to be used with the
            // user authenticator
            // framework, in which case it will throw a
            // ControlledAccessException.
            // Your driver was registered with the smart card API framework and
            // can still
            // be used for importing certificates and signing/decrypting
            // messages.
        }
    }

    /**
     * Returns the appropriate subclass of SmartCardSession. Implementations of
     * this method should not bring up UI.
     */
    protected SmartCardSession openSessionImpl(
            final SmartCardReaderSession readerSession)
            throws SmartCardException {
        return new MyCryptoSmartCardSession(this, readerSession);
    }

    /**
     * Returns true if this SmartCard implementation should be used to
     * communicate with a physical smart card that has the given AnswerToReset.
     * This method is called when the system is trying to ascertain which
     * SmartCard implementation should be used to communicate with a physical
     * smart card found in a reader.
     */
    protected boolean checkAnswerToResetImpl(final AnswerToReset atr) {
        // If you return false from this method your driver will never be called
        // to do additional
        // operations on that particular smart card. You should *only* return
        // true if you support
        // the particular ATR. Returning true when you do not support the card
        // corresponding to
        // the ATR may prevent other smart card drivers from functioning
        // correctly.

        // The AnswerToReset object passed in contains the full ATR from the
        // card. Your implementation
        // may check the entire ATR or just parts of the ATR, as long as you are
        // certain that
        // your driver supports the corresponding smart card.

        return _myATR.equals(atr);
    }

    /**
     * Returns a label associated with the kind of smart card. The string should
     * not include the words "smart card", as this method will be used to
     * generate strings such as:
     * 
     * ( "Please insert your %s smart card", getLabel() )
     */
    protected String getLabelImpl() {
        return LABEL;
    }

    /**
     * Returns the <code>SmartCardCapabilities</code> of the smart card.
     * <p>
     * 
     * @return The capabilities of the smart card.
     */
    protected SmartCardCapabilities getCapabilitiesImpl() {
        return new SmartCardCapabilities(SmartCardCapabilities.PROTOCOL_T0);
    }

    /**
     * Allows the driver to indicate if they support displaying settings.
     */
    protected boolean isDisplaySettingsAvailableImpl(final Object context) {
        return true;
    }

    /**
     * Allows the driver to display some settings or properties. This method
     * will be invoked from the smart card options screen when the user selects
     * the driver and chooses to view the settings of that driver.
     * 
     * This method could be called from the event thread. The driver should not
     * block the event thread for long periods of time.
     * 
     * @param context
     *            Reserved for future use.
     */
    protected void displaySettingsImpl(final Object context) {
        Dialog.alert(DISPLAY_SETTINGS);
    }

    /**
     * Returns the names of all the algorithms supported by this token (e.g.,
     * "RSA", "DSA").
     * <p>
     * 
     * @return A String that represents the names of the algorithms.
     */
    public String[] getAlgorithms() {
        return new String[] { RSA };
    }

    /**
     * Returns a crypto token that supports the given algorithm.
     * <p>
     * 
     * @param algorithm
     *            A String representing the name of the algorithm.
     * @return The crypto token.
     * @throws NoSuchAlgorithmException
     *             Thrown if the specified algorithm is invalid.
     * @throws CryptoTokenException
     *             Thrown if there is a token related problem.
     */
    public CryptoToken getCryptoToken(final String algorithm)
            throws NoSuchAlgorithmException, CryptoTokenException {
        if (algorithm.equals(RSA)) {
            return new MyRSACryptoToken();
        }

        throw new NoSuchAlgorithmException();
    }
}
