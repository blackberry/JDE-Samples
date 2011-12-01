/*
 * PhoneMultiLineAction.java
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
 *
 */

package com.rim.samples.device.phonemultilinedemo;

import net.rim.blackberry.api.phone.InvalidIDException;
import net.rim.blackberry.api.phone.MultiLineListener;
import net.rim.blackberry.api.phone.Phone;

/**
 * This class listens for multi-line events, such as switching phone lines,
 * determining all available phone lines, etc., which are invoked by the user.
 * It then delegates the appropriate action to the application.
 */
public final class PhoneMultiLineAction extends MultiLineListener {
    private final PhoneMultiLineScreen _screen;

    /**
     * Constructs a new PhoneMultiLineAction object
     * 
     * @param screen
     *            The screen which will be updated to reflect multi line events
     */
    public PhoneMultiLineAction(final PhoneMultiLineScreen screen) {
        _screen = screen;

        // Register this class as the multi-line listener
        Phone.addPhoneListener(this);
    }

    /**
     * Refresh the list of available lines
     */
    public void refreshAvailableList() {
        _screen.setSelectedChoice();
    }

    /**
     * Invoked when preferred line switching failed
     * 
     * @param lineId
     *            The phone line that the user tried to switch to
     */
    public void setPreferredLineFailure(final int lineId) {
        String lineNumber = "";
        try {
            lineNumber = Phone.getLineNumber(lineId);
        } catch (final InvalidIDException e) {
            PhoneMultiLineDemo.messageDialog("Phone.getLineNumber(int) threw "
                    + e.toString());
            return;
        }
        final StringBuffer buf = new StringBuffer();
        buf.append("Failed to switch to phone number ");
        buf.append(lineNumber);
        PhoneMultiLineDemo.messageDialog(buf.toString());
    }

    /**
     * Invoked when preferred line switching is successful
     * 
     * @param lineId
     *            The phone number that was switched to successfully
     */
    public void setPreferredLineSuccess(final int lineId) {
        String lineNumber = "";
        try {
            lineNumber = Phone.getLineNumber(lineId);
        } catch (final InvalidIDException e) {
            PhoneMultiLineDemo.messageDialog("Phone.getLineNumber(int) threw "
                    + e.toString());
            return;
        }

        final StringBuffer buf = new StringBuffer();
        buf.append("Switch to phone number ");
        buf.append(lineNumber);
        buf.append(", (line id ");
        buf.append(lineId);
        buf.append(") completed");

        PhoneMultiLineDemo.messageDialog(buf.toString());
    }

    /**
     * Invoked when one of the line's availability has changed
     * 
     * @param lineId
     *            The phone number whose availibility has changed
     */
    public void activeLineAvailabilityChanged(final int lineId) {
        _screen.setSelectedChoice();
    }

    /**
     * Invoked when the phone line validation check fails
     * 
     * @param reason
     *            An integer representing the reason for failure, encoded in the
     *            form of a bitmask
     */
    public void preSwitchingConditionFailure(int reason) {
        final String[] errorMessage =
                { "Line not available", "Line already active",
                        "Call in progress", "Previous switching not completed",
                        "Line does not exist", "Emergency mode enabled" };
        final StringBuffer buf = new StringBuffer();
        int i = 0;

        do {
            if ((reason & 1) == 1) {
                if (buf.length() > 0) {
                    buf.append(", ");
                }
                buf.append(errorMessage[i]);
            }
            reason = reason >> 1;
            i++;
        } while (reason != 0 && i < errorMessage.length);

        PhoneMultiLineDemo.messageDialog(buf.toString());
    }

    /**
     * Brings the application to the foreground when the call has disconnected
     * 
     * @param callId
     *            The phone number of the disconnected call
     */
    public void callDisconnected(final int callId) {
        _screen.getApplication().requestForeground();
    }

    /**
     * Brings the application to the foreground when the call has failed
     * 
     * @param callId
     *            The phone number of the failed call
     * @param reason
     *            An integer representing the reason for failure, encoded in the
     *            form of a bitmask
     */
    public void callFailed(final int callId, final int reason) {
        _screen.getApplication().requestForeground();
    }
}
