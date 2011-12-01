/*
 * VirtualISOTargetListener.java
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

package com.rim.samples.device.nfcemulatordemo;

import net.rim.device.api.io.nfc.emulation.VirtualISO14443Part4TargetCallback;

/**
 * VirtualISO14443TargetListener class for Virtual ISO Targets that are being
 * emulated.
 */
public class VirtualISOTargetListener implements
        VirtualISO14443Part4TargetCallback {
    private final NFCEmulatorScreen _screen;

    /**
     * Creates a new VirtualISOTargetListener object
     * 
     * @param screen
     *            NFCEmulatorScreen object on which tag information will be
     *            displayed
     * @throws NullPointerException
     *             if screen == null
     */
    public VirtualISOTargetListener(final NFCEmulatorScreen screen) {
        if (screen == null) {
            throw new IllegalArgumentException("screen == null");
        }

        _screen = screen;
    }

    /**
     * @see net.rim.device.api.io.nfc.emulation.VirtualISO14443Part4TargetCallback#onVirtualTargetEvent(int)
     */
    public void onVirtualTargetEvent(final int targetEvent) {
        // Determine the event that occurred and notify the screen
        switch (targetEvent) {
        case VirtualISO14443Part4TargetCallback.DEACTIVATED:
            _screen.setEmulationListenerStatus("Deactivated by reader");
            break;
        case VirtualISO14443Part4TargetCallback.SELECTED:
            _screen.setEmulationListenerStatus("Selected by reader");
            break;
        case VirtualISO14443Part4TargetCallback.EMULATION_STOPPED:
            _screen.setEmulationListenerStatus("Emulation has stopped");
            break;
        }
    }

    /**
     * @see net.rim.device.api.io.nfc.emulation.VirtualISO14443Part4TargetCallback#processCommand(byte[])
     */
    public byte[] processCommand(final byte[] request) {
        // Note: For the sake of this demo, this method will check to see
        // if the request is the "Capability Container Select" command.
        // The developer can add additional logic here to provide support
        // for a larger set of commands.

        // Set up a "default" response to return if the request is not "valid"
        byte[] response = new byte[] { (byte) 0x00, (byte) 0x00 };

        // Set up the command to comapre against the request
        final byte[] capabilityContainerSelect =
                new byte[] { (byte) 0x00, (byte) 0xA4, (byte) 0x00,
                        (byte) 0x00, (byte) 0x02, (byte) 0xE1, (byte) 0x03 };

        // Make sure that the request is the same length as
        // capabilityContainerSelect.
        // If they aren't the same, its not worth parsing.
        if (request.length == capabilityContainerSelect.length) {
            // Go through the request command and compare it to
            // capabilityContainerSelect
            for (int i = 0; i < request.length; i++) {
                // If they are not the same, return (0x00, 0x00)
                if (request[i] != capabilityContainerSelect[i]) {
                    return response;
                }
            }

            // Do work associated with the "Select Capability Container" command
            // here
            // ...
            // ...
            // ...

            // The request has been completed, so send the "command completed"
            // response
            _screen.setEmulationListenerStatus("\"Capability Container Select\" command sent! Sending response...");
            response = new byte[] { (byte) 0x09, (byte) 0x00 };
            return response;
        }

        // The commands were not the same length, so send (0x00, 0x00)
        return response;
    }
}
