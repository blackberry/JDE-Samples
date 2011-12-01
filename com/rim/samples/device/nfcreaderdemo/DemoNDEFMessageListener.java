/*
 * DemoNDEFMessageListener.java
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

import net.rim.device.api.io.nfc.ndef.NDEFMessage;
import net.rim.device.api.io.nfc.ndef.NDEFMessageListener;

/**
 * NFCMessageListener class that listens for an NDEFMessage that has an
 * NDEFRecord with a record type of "plain/text".
 */
public class DemoNDEFMessageListener implements NDEFMessageListener {
    private final NFCReaderScreen _screen;

    /**
     * Creates a new DemoNDEFMessageListener object
     * 
     * @param screen
     *            The application's main screen
     */
    public DemoNDEFMessageListener(final NFCReaderScreen screen) {
        if (screen == null) {
            throw new IllegalArgumentException("screen == null");
        }
        _screen = screen;
    }

    /**
     * @see net.rim.device.api.io.nfc.ndef.NDEFMessageListener#onNDEFMessageDetected(NDEFMessage)
     */
    public void onNDEFMessageDetected(final NDEFMessage message) {
        // Delete all the fields in the output from the
        // last time that a target was detected and read.
        _screen.deleteFields();
        // Do something with the detected NDEFMessage
        final int recordAmount = message.getNumberOfRecords();
        _screen.addTargetInfo("There are " + recordAmount
                + " records in this NDEF message");
    }
}
