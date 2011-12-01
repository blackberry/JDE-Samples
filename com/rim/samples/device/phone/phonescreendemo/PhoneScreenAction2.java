/*
 * PhoneScreenAction2.java
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

import net.rim.blackberry.api.phone.AbstractPhoneListener;
import net.rim.blackberry.api.phone.Phone;
import net.rim.blackberry.api.phone.PhoneCall;
import net.rim.blackberry.api.phone.phonegui.PhoneScreen;
import net.rim.blackberry.api.phone.phonegui.PhoneScreenHorizontalManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.XYRect;

/**
 * This class listens for incoming calls. When either of the callIncoming(),
 * callWaiting(), or callInitiated() callbacks have been invoked, this sample
 * application will send data to the incoming and active call screens.
 */
public final class PhoneScreenAction2 extends AbstractPhoneListener {
    private final Application _app;

    /**
     * Constructs a new PhoneScreenAction object
     * 
     * @param app
     *            A reference to the Application instance
     */
    public PhoneScreenAction2(final Application app) {
        _app = app;
    }

    /**
     * Creates and adds an image to a PhoneScreen object which then sends its
     * data to the Phone application's foreground screen.
     * 
     * @param callid
     *            Id of an incoming call
     */
    private void sendPhoneScreenData(final int callid) {
        final PhoneCall phoneCall = Phone.getCall(callid);

        if (phoneCall != null) {
            final String phoneNumber = phoneCall.getPhoneNumber();

            if (phoneNumber.equals("5550111")) {
                // Initialize PhoneScreen
                final PhoneScreen phoneScreen = new PhoneScreen(callid, _app);

                // Create a PhoneScreenHorizontalManager
                final PhoneScreenHorizontalManager phoneScreenHorizontalManager =
                        new PhoneScreenHorizontalManager();

                // Create a Bitmap
                Bitmap bitmap = Bitmap.getBitmapResource("logo_2.jpg");

                // Get phone screen dimensions
                final XYRect incomingLandscape =
                        PhoneScreen.getDimensions(PhoneScreen.LANDSCAPE,
                                PhoneScreen.INCOMING);
                final XYRect activeLandscape =
                        PhoneScreen.getDimensions(PhoneScreen.LANDSCAPE,
                                PhoneScreen.ACTIVECALL);
                final XYRect incomingPortrait =
                        PhoneScreen.getDimensions(PhoneScreen.PORTRAIT,
                                PhoneScreen.INCOMING);
                final XYRect activePortrait =
                        PhoneScreen.getDimensions(PhoneScreen.PORTRAIT,
                                PhoneScreen.ACTIVECALL);

                // Get phone screen widths
                int incomingLandscapeWidth = 0;
                if (incomingLandscape != null) {
                    incomingLandscapeWidth = incomingLandscape.width;
                }
                int activeLandscapeWidth = 0;
                if (activeLandscape != null) {
                    activeLandscapeWidth = activeLandscape.width;
                }
                final int incomingPortraitWidth = incomingPortrait.width;
                final int activePortraitWidth = activePortrait.width;

                // Calculate minimum width for active and incoming phone screens
                // in portrait or landscape orientation
                int minWidth =
                        Math.min(incomingPortraitWidth, activePortraitWidth);
                if (activeLandscapeWidth > 0) {
                    minWidth = Math.min(activeLandscapeWidth, minWidth);
                }
                if (incomingLandscapeWidth > 0) {
                    minWidth = Math.min(incomingLandscapeWidth, minWidth);
                }
                int bitmapWidth = bitmap.getWidth();
                final int bitmapHeight = bitmap.getHeight();

                // Resize the bitmap if its width exceeds the minimum width
                if (bitmapWidth > minWidth) {
                    final Bitmap bitmapScaled =
                            new Bitmap(minWidth, bitmapHeight);

                    // Scale the original Bitmap into the new Bitmap using
                    // a Lanczos filter.
                    bitmap.scaleInto(bitmapScaled, Bitmap.FILTER_LANCZOS);

                    // Assign the bitmap and bitmapWidth references
                    bitmap = bitmapScaled;
                    bitmapWidth = bitmap.getWidth();
                }

                // Calculate and set padding
                final int displayWidth = Display.getWidth();
                final int padding = (displayWidth - bitmapWidth) / 2;
                phoneScreenHorizontalManager.setPadding(0, padding, 0, padding);

                // Add the bitmap image to the PhoneScreenHorizontalManager
                // and add to the phone screen.
                phoneScreenHorizontalManager.addImage(bitmap);
                phoneScreen.add(phoneScreenHorizontalManager);

                // Display the data
                phoneScreen.sendDataToScreen();
            }
        }
    }

    /**
     * @see AbstractPhoneListener#callWaiting(int)
     */
    public void callWaiting(final int callid) {
        sendPhoneScreenData(callid);
    }

    /**
     * @see AbstractPhoneListener#callInitiated(int)
     */
    public void callInitiated(final int callid) {
        sendPhoneScreenData(callid);
    }

    /**
     * @see AbstractPhoneListener#callIncoming(int)
     */
    public void callIncoming(final int callid) {
        sendPhoneScreenData(callid);
    }
}
