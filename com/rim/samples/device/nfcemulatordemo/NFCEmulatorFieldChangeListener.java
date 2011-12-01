/*
 * NFCEmulatorFieldChangeListener.java
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

import net.rim.device.api.io.nfc.NFCException;
import net.rim.device.api.io.nfc.emulation.VirtualISO14443Part4TypeATarget;
import net.rim.device.api.io.nfc.emulation.VirtualISO14443Part4TypeBTarget;
import net.rim.device.api.io.nfc.emulation.VirtualNDEFTag;
import net.rim.device.api.io.nfc.ndef.NDEFMessage;
import net.rim.device.api.io.nfc.ndef.NDEFRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.LabelField;

/**
 * FieldChangeListener class that determines what type of emulation to start
 * based on user selection.
 */
public class NFCEmulatorFieldChangeListener implements FieldChangeListener {
    private final NFCEmulatorScreen _screen;
    private static VirtualNDEFTag _ndefVirtualTarget;
    private static VirtualISO14443Part4TypeBTarget _isoBVirtualTarget;
    private static VirtualISO14443Part4TypeATarget _isoAVirtualTarget;

    /**
     * Creates a new NFCEmulatorFieldChangeListener object
     * 
     * @param screen
     *            NFCEmulatorScreen object on which tag information will be
     *            displayed
     * @throws NullPointerException
     *             if screen == null
     */
    public NFCEmulatorFieldChangeListener(final NFCEmulatorScreen screen) {
        if (screen == null) {
            throw new IllegalArgumentException("screen==null");
        }

        _screen = screen;
    }

    /**
     * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        // User pressed start button to begin an emulation
        if (field == _screen.getStartButton()) {
            // Get the type of emulation the user selected
            final String selectedType = _screen.getEmulationType();

            // ISO 14443 Type A
            if (selectedType.equals(NFCEmulatorScreen.ISO_14443_A)) {
                _screen.setTagStatus("The target is of type ISO 14443 A");
                startISO14443AEmulation();
            }

            // ISO 14443 Type B
            else if (selectedType.equals(NFCEmulatorScreen.ISO_14443_B)) {
                _screen.setTagStatus("The target is of type ISO 14443 B");
                startISO14443BEmulation();
            }

            // NDEF Tag
            else if (selectedType.equals(NFCEmulatorScreen.NDEF)) {
                _screen.setTagStatus("The target is of type NDEF");
                startNDEFEmulation();
            }

            _screen.getStartButton().setEnabled(false);
            _screen.getStopButton().setEnabled(true);
            _screen.getStopButton().setFocus();
        }

        // Stop button pressed
        else if (field == _screen.getStopButton()) {
            // Get the current type being emulated
            final String selectedType = _screen.getCurrentEmulation();

            // Clear the ListenerStatus field
            _screen.setEmulationListenerStatus("");

            // Stop ISO 14443 Type A emulation
            if (selectedType.equals(NFCEmulatorScreen.ISO_14443_A)) {
                _screen.setTagStatus("Stopping ISO 14443 A emulation");
                stopISO14443AEmulation();
            }

            // Stop ISO 14443 Type B emulation
            else if (selectedType.equals(NFCEmulatorScreen.ISO_14443_B)) {
                _screen.setTagStatus("Stopping ISO 14443 B emulation");
                stopISO14443BEmulation();
            }

            // Stop NDEF emulation
            else if (selectedType.equals(NFCEmulatorScreen.NDEF)) {
                _screen.setTagStatus("Stopping NDEF emulation");
                stopNDEFEmulation();
            }

            _screen.getStartButton().setEnabled(true);
            _screen.getStartButton().setFocus();
            _screen.getStopButton().setEnabled(false);
        }
    }

    /**
     * Start ISO 14443 Type A emulation
     */
    public void startISO14443AEmulation() {
        try {
            // Create an ISO 14443 Type A Target with an ID of 1234567 and no
            // historical bytes
            _isoAVirtualTarget =
                    new VirtualISO14443Part4TypeATarget(
                            new VirtualISOTargetListener(_screen), "1234567",
                            null);

            // Start the emulation
            _isoAVirtualTarget.startEmulation();
            _screen.setTagStatus("Virtual ISO 14443 A emulation started");
            _screen.setCurrentEmulation(NFCEmulatorScreen.ISO_14443_A);
        } catch (final NFCException e) {
            _screen.add(new LabelField("Error: " + e.toString()));
            _screen.setTagStatus("Virtual ISO 14443 A emulation NOT started");
        }
    }

    /**
     * Stops ISO 14443 Type A emulation
     */
    public void stopISO14443AEmulation() {
        if (_isoAVirtualTarget != null) {
            try {
                _isoAVirtualTarget.stopEmulation();
            } catch (final NFCException e) {
                _screen.add(new LabelField("Error: " + e.toString()));
            }

            _screen.setTagStatus("Virtual ISO 14443 A emulation stopped");

            // Clear the current emulation value on the screen since there isn't
            // anything being emulated directly after emulation stops.
            _screen.setCurrentEmulation("");
        }
    }

    /**
     * Starts ISO 14443 Type B emulation
     */
    public void startISO14443BEmulation() {
        try {
            // Create the ISO 14443 Type B target
            _isoBVirtualTarget =
                    new VirtualISO14443Part4TypeBTarget(
                            new VirtualISOTargetListener(_screen));

            // Start the emulation
            _isoBVirtualTarget.startEmulation();
            _screen.setTagStatus("Virtual ISO 14443 B emulation started");
            _screen.setCurrentEmulation(NFCEmulatorScreen.ISO_14443_B);
        } catch (final NFCException e) {
            _screen.add(new LabelField("Error: " + e.toString()));
            _screen.setTagStatus("Virtual ISO 14443 B emulation NOT started");
        }
    }

    /**
     * Stops ISO 14443 Type B emulation
     */
    public void stopISO14443BEmulation() {
        if (_isoBVirtualTarget != null) {
            try {
                _isoBVirtualTarget.stopEmulation();
                _screen.setTagStatus("Virtual ISO 14443 B emulation stopped");

                // Clear the currentEmulation value on the screen since there
                // isn't
                // anything being emulated directly after emulation stops.
                _screen.setCurrentEmulation("");
            } catch (final NFCException e) {
            }
        }
    }

    /**
     * Starts emulating an NDEF tag with hardcoded payloads
     */
    public void startNDEFEmulation() {
        // Create the NDEFMessage that will contain the NDEFRecords
        final NDEFMessage ndefMessage = new NDEFMessage();

        // Begin creating the NDEFRecords with "text/plain" payloads
        final NDEFRecord rec1 = new NDEFRecord();
        rec1.setId("1");

        try {
            rec1.setType(NDEFRecord.TNF_MEDIA, "text/plain");
        } catch (final NFCException e) {
            _screen.add(new LabelField("Error: " + e.toString()));
        }

        rec1.setPayload("I am the 1st payload".getBytes());

        final NDEFRecord rec2 = new NDEFRecord();
        rec2.setId("2");

        try {
            rec2.setType(NDEFRecord.TNF_MEDIA, "text/plain");

        } catch (final NFCException e) {
            _screen.add(new LabelField("Error: " + e.toString()));
        }

        rec2.setPayload("I am the 2nd payload".getBytes());
        ndefMessage.setRecords(new NDEFRecord[] { rec1, rec2 });

        // Create the VirtualNDEFTag to be emulated
        _ndefVirtualTarget =
                new VirtualNDEFTag(ndefMessage, new VirtualNDEFTagListener(
                        _screen));

        try {
            // Start the emulation
            _ndefVirtualTarget.startEmulation();
            _screen.setTagStatus("NDEF emulation started");
            _screen.setCurrentEmulation(NFCEmulatorScreen.NDEF);
        } catch (final NFCException e) {
            _screen.add(new LabelField("Error: " + e.toString()));
            _screen.setTagStatus("NDEF emulation NOT started");
        }
    }

    /**
     * Stops the NDEF tag emulation
     */
    public void stopNDEFEmulation() {
        if (_ndefVirtualTarget != null) {
            try {
                _ndefVirtualTarget.stopEmulation();

                // Clear the currentEmulation value on the screen since there
                // isn't
                // anything being emulated directly after emulation stops.
                _screen.setCurrentEmulation("");
            } catch (final NFCException e) {
                _screen.add(new LabelField("Error: " + e.toString()));
            }
        }
    }
}
