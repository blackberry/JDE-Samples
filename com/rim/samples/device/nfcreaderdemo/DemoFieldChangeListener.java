/*
 * DemoFieldChangeListener.java
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

import net.rim.device.api.io.nfc.NFCException;
import net.rim.device.api.io.nfc.NFCManager;
import net.rim.device.api.io.nfc.ndef.NDEFRecord;
import net.rim.device.api.io.nfc.readerwriter.ReaderWriterManager;
import net.rim.device.api.io.nfc.readerwriter.Target;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;

/**
 * FieldChangeListener class that is notified when the application screen's
 * "Start" and "Stop" buttons are invoked.
 */
public class DemoFieldChangeListener implements FieldChangeListener {
    private final NFCReaderScreen _screen;

    private final ReaderWriterManager _rwManager;
    private final NFCReaderTargetDetector _targetDetector;
    private final DemoNDEFMessageListener _messageListener;
    private final NFCManager _nfcManager;

    /**
     * Creates a new DemoFieldChangeListener object
     * 
     * @param screen
     *            The application's main screen
     */
    public DemoFieldChangeListener(final NFCReaderScreen screen,
            final NFCManager nfcManager) throws NFCException {
        if (screen == null) {
            throw new IllegalArgumentException("screen==null");
        }
        _screen = screen;

        if (nfcManager == null) {
            throw new IllegalArgumentException("nfcManager==null");
        }

        _nfcManager = nfcManager;
        _rwManager = ReaderWriterManager.getInstance();

        _targetDetector = new NFCReaderTargetDetector(_screen, nfcManager);
        _messageListener = new DemoNDEFMessageListener(_screen);
    }

    /**
     * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field == _screen.getStartButton()) {
            startDetecting();

            final ButtonField startButton = _screen.getStartButton();
            final ButtonField stopButton = _screen.getStopButton();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            stopButton.setFocus();
        } else if (field == _screen.getStopButton()) {
            stopDetecting();

            final ButtonField stopButton = _screen.getStopButton();
            final ButtonField startButton = _screen.getStartButton();
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
            startButton.setFocus();
        }
    }

    /**
     * Initializes and registers a DetectionListener and a NDEFMessageListener
     */
    public void startDetecting() {
        try {
            // Register a DetectionListener that will detect either
            // ISO 14443 4, ISO 14443 3, or NDEF targets.
            _rwManager.addDetectionListener(_targetDetector, new int[] {
                    Target.NDEF_TAG, Target.ISO_14443_4, Target.ISO_14443_3 });

            // Register a NDEFMessageListener that will detect an NDEFMessage
            // that has a
            // TypeNameFormat of 'Any', and a recordType of "text/plain".
            _rwManager.addNDEFMessageListener(_messageListener,
                    NDEFRecord.TNF_ANY, "text/plain", false);
            _screen.deleteListenerStatus();
            _screen.addListenerStatus("Listening for targets...");
        } catch (final NFCException e) {
            _nfcManager.playNFCOperationFailureSound();
            _screen.add(new LabelField("Error: " + e.toString()));
        }
    }

    /**
     * Deregisters the DetectionListener and the NDEFMessageListener registered
     * by this class.
     */
    public void stopDetecting() {
        // Remove the DetectionListener
        if (_targetDetector != null) {
            try {
                _rwManager.removeDetectionListener(_targetDetector);
                _screen.deleteListenerStatus();
                _screen.addListenerStatus("No longer listening for targets...");
            } catch (final Exception e) {
                _nfcManager.playNFCOperationFailureSound();
                _screen.add(new LabelField("Error: " + e.toString()));
            }
        }

        // Remove the NDEFMessageListener
        if (_messageListener != null) {
            try {
                _rwManager.removeNDEFMessageListener(NDEFRecord.TNF_ANY,
                        "text/plain");
                _screen.addListenerStatus("No longer listening for NDEF Messages...");
            } catch (final Exception e) {
                _nfcManager.playNFCOperationFailureSound();
                _screen.add(new LabelField("Error: " + e.toString()));
            }
        }
    }
}
