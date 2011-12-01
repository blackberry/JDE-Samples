/*
 * PhoneScreenDataSender.java
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

package com.rim.samples.device.phonescreendemo;

import javax.microedition.pim.Contact;
import javax.microedition.pim.PIMList;

import net.rim.blackberry.api.pdap.BlackBerryContact;
import net.rim.blackberry.api.phone.AbstractPhoneListener;
import net.rim.blackberry.api.phone.Phone;
import net.rim.blackberry.api.phone.PhoneCall;
import net.rim.blackberry.api.phone.phonegui.PhoneScreen;
import net.rim.blackberry.api.phone.phonegui.PhoneScreenHorizontalManager;
import net.rim.blackberry.api.phone.phonegui.ScreenModel;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;

/**
 * This class listens for incoming calls. When any of the AbstractPhoneListener
 * callbacks are invoked, the class will send data to the incoming and active
 * call screens.
 */
public final class PhoneScreenDataSender extends AbstractPhoneListener {
    private static final String IMAGE = "logo.jpg";

    /**
     * Creates a new PhoneScreenDataSender object
     */
    public PhoneScreenDataSender() {
    }

    /**
     * @see AbstractPhoneListener#callWaiting(int)
     */
    public void callWaiting(final int callID) {
        // Data for active call screen needs to be provided in callWaiting() if
        // a phone is CDMA. For a GSM phone, data can be added in
        // CallAnswered().
        if ((RadioInfo.getSupportedWAFs() & RadioInfo.WAF_CDMA) != 0) {
            sendData(callID, PhoneScreen.ACTIVECALL);
        }
        sendData(callID, PhoneScreen.WAITING);
    }

    /**
     * @see AbstractPhoneListener#callInitiated(int)
     */
    public void callInitiated(final int callID) {
        sendData(callID, PhoneScreen.OUTGOING);
    }

    /**
     * @see AbstractPhoneListener#callAnswered(int)
     */
    public void callAnswered(final int callID) {
        sendData(callID, PhoneScreen.ACTIVECALL);
    }

    /**
     * @see AbstractPhoneListener#callIncoming(int)
     */
    public void callIncoming(final int callID) {
        sendData(callID, PhoneScreen.INCOMING);
    }

    /**
     * Sends PhoneScreen data to the device screen
     * 
     * @param callID
     *            ID for the current call
     * @param screenType
     *            One of {@link PhoneScreen#ACTIVECALL},
     *            {@link PhoneScreen#INCOMING} or {@link PhoneScreen#OUTGOING}
     */
    private static void sendData(final int callID, final int screenType) {
        BlackBerryContact contact = null;

        final PhoneCall phoneCall = Phone.getCall(callID);
        if (phoneCall != null) {
            // Try to obtain BlackBerryContact associated with the call
            contact = phoneCall.getContact();
        }

        // Obtain ScreenModel for current call
        final ScreenModel model = getScreenModel(callID, contact, screenType);

        if (model != null) {
            // Send data contained in ScreenModel to the screen
            model.sendAllDataToScreen();
        }
    }

    /**
     * Creates a <code>ScreenModel</code> containing PhoneScreen objects for
     * portrait mode and landscape mode (if supported).
     * 
     * @param callID
     *            ID for the current call
     * @param contact
     *            A <code>BlackBerryContact</code> object, may be null
     * @param screenType
     *            One of {@link PhoneScreen#ACTIVECALL},
     *            {@link PhoneScreen#INCOMING} or {@link PhoneScreen#OUTGOING}
     * @return ScreenModel ScreenModel containing PhoneScreen objects for
     *         landscape and portrait modes, or null
     */
    private static ScreenModel getScreenModel(final int callID,
            final BlackBerryContact contact, final int screenType) {
        // Obtain ScreenModel for current call
        final ScreenModel screenModel = new ScreenModel(callID);

        // Create a PhoneScreen in portrait view
        PhoneScreen portraitPhoneScreen;
        try {
            portraitPhoneScreen =
                    screenModel
                            .getPhoneScreen(PhoneScreen.PORTRAIT, screenType);
        } catch (final ControlledAccessException e) {
            return null;
        }

        if (portraitPhoneScreen != null) {
            setUpPhoneScreen(portraitPhoneScreen, contact);
        }

        // Create a PhoneScreen in landscape view
        PhoneScreen landscapePhoneScreen;
        try {
            landscapePhoneScreen =
                    screenModel.getPhoneScreen(PhoneScreen.LANDSCAPE,
                            screenType);
        } catch (final ControlledAccessException e) {
            return null;
        }

        if (landscapePhoneScreen != null) {
            setUpPhoneScreen(landscapePhoneScreen, contact);
        }

        return screenModel;
    }

    /**
     * Configures a PhoneScreen with UI elements
     * 
     * @param phoneScreen
     *            The PhoneScreen to configure
     * @param contact
     *            A BlackBerryContact associated with the current call
     */
    private static void setUpPhoneScreen(final PhoneScreen phoneScreen,
            final BlackBerryContact contact) {
        final StringBuffer strBuffer = new StringBuffer();

        if (contact != null) {
            final PIMList pimList = contact.getPIMList();
            if (pimList.isSupportedField(Contact.EMAIL)) {
                // Try to extract email info from contact
                if (contact.countValues(Contact.EMAIL) > 0) {
                    final String email =
                            contact.getString(Contact.EMAIL, 0).trim();
                    if (email != null && email.endsWith("rim.com")) {
                        // Add an image if contact is a RIM employee
                        final Bitmap bitmap = Bitmap.getBitmapResource(IMAGE);
                        if (bitmap != null) {
                            final BitmapField bitmapField =
                                    new BitmapField(bitmap);
                            phoneScreen.add(bitmapField);
                            phoneScreen.add(new SeparatorField());
                        }
                    }
                }
            }

            // Try to extract address info from contact
            if (contact.countValues(Contact.ADDR) > 0) {
                final String[] strArray =
                        contact.getStringArray(Contact.ADDR, 0);
                final String city = strArray[Contact.ADDR_LOCALITY];
                if (city != null && city.length() > 0) {
                    strBuffer.append(city);
                }

                final String country = strArray[Contact.ADDR_COUNTRY];
                if (country != null && country.length() > 0) {
                    if (city != null && city.length() > 0) {
                        strBuffer.append(", ");
                    }

                    strBuffer.append(country);
                }
            }
        }

        String location = strBuffer.toString();
        if (location.length() == 0) {
            // Couldn't find any address info
            location = "Location: unknown";
        }

        // Initialize a LabelField to display location info
        final LabelField labelField = new LabelField(location) {
            /**
             * @see LabelField#paint(Graphics g)
             */
            public void paint(final Graphics g) {
                g.setColor(Color.SKYBLUE);
                g.setBackgroundColor(Color.IVORY);
                g.clear();
                super.paint(g);
            }
        };

        // Set the font of the LabelField to be the same as that of the
        // caller info displayed on the screen by the Phone application.
        labelField.setFont(phoneScreen.getCallerInfoFont());

        // Initialize a PhoneScreenHorizontalManager
        final PhoneScreenHorizontalManager pshm =
                new PhoneScreenHorizontalManager();

        // Add the LabelField to the PhoneScreenHorizontalManager
        pshm.add(labelField);

        // Center the PhoneScreenHorizontalManager. UI fields
        // are centered in the PhoneScreen by default with the
        // exception of PhoneScreenHorizontalManager.
        final int padPoint =
                (phoneScreen.getDimensions().width - pshm.getPreferredWidth()) / 2;
        if (padPoint > 0) {
            pshm.setPadding(0, padPoint, 0, padPoint);
        }

        // Add the PhoneScreenHorizontalManager to the phone screen
        phoneScreen.add(pshm);
    }
}
