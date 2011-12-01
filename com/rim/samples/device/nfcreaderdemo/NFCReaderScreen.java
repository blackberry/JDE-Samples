/*
 * NFCReaderScreen.java
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
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * Screen class for the NFC Reader Demo application. Displays target information
 * when an NDEF target is detected.
 */
public class NFCReaderScreen extends MainScreen {

    private VerticalFieldManager _targetInfo;
    private VerticalFieldManager _responseStatus;
    private VerticalFieldManager _listenerStatus;
    private WideButtonField _startButton;
    private WideButtonField _stopButton;
    private final NFCManager _nfcManager;

    /**
     * Creates a new NFCReaderScreen object
     */
    public NFCReaderScreen(final NFCManager nfcManager) {
        setTitle("NFC Reader Demo");

        if (nfcManager == null) {
            throw new IllegalArgumentException("nfcManager==null");
        }
        _nfcManager = nfcManager;
        buildUI();
    }

    /**
     * Initializes the UI for the NFC Reader Demo application
     */
    private void buildUI() {
        // Initialize managers and buttons
        _targetInfo = new VerticalFieldManager();
        _responseStatus = new VerticalFieldManager();
        _listenerStatus = new VerticalFieldManager();

        _startButton = new WideButtonField("Start");
        _stopButton = new WideButtonField("Stop");
        _stopButton.setEnabled(false);
        DemoFieldChangeListener changeListener;
        try {
            changeListener = new DemoFieldChangeListener(this, _nfcManager);
            _startButton.setChangeListener(changeListener);
            _stopButton.setChangeListener(changeListener);
        } catch (final NFCException e) {
            _nfcManager.playNFCOperationFailureSound();
            add(new LabelField("Error: " + e.toString()));
            return;
        }

        // Add fields to the screen
        add(_startButton);
        add(_stopButton);
        add(new LabelField("Detected targets will automatically be read"));

        add(new SeparatorField());
        add(new LabelField("Detected :"));
        add(_targetInfo);
        add(new SeparatorField());

        add(new LabelField("Response Status :"));
        add(_responseStatus);
        add(new SeparatorField());
        add(_listenerStatus);
    }

    /**
     * Adds a LabelField to the screen to display target information
     * 
     * @param info
     *            Target information that will be displayed on the screen
     */
    public void addTargetInfo(final String info) {
        _targetInfo.add(new LabelField(info));
    }

    /**
     * Adds a LabelField to the screen to display target content
     * 
     * @param contents
     *            Target content that will be displayed on the screen
     */
    public void addResponseStatus(final String contents) {
        _responseStatus.add(new LabelField(contents));
    }

    /**
     * Adds a LabelField to the screen to display that status of the
     * NDEFMessageListener and DetectionListener.
     * 
     * @param status
     *            Status to display on the screen
     */
    public void addListenerStatus(final String status) {
        _listenerStatus.add(new LabelField(status));
    }

    /**
     * Deletes all the fields in the _listenerStatus manager
     */
    public void deleteListenerStatus() {
        _listenerStatus.deleteAll();
    }

    /**
     * Removes all the fields in the _targetInfo and _responseStatus managers
     * when a target is detected.
     */
    public void deleteFields() {
        _targetInfo.deleteAll();
        _responseStatus.deleteAll();
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Prevent the save dialog from being displayed
        return true;
    }

    /**
     * Retrieves the screen's start button
     * 
     * @return start button
     */
    public WideButtonField getStartButton() {
        return _startButton;
    }

    /**
     * Retrieves the screen's stop button
     * 
     * @return stop button
     */
    public WideButtonField getStopButton() {
        return _stopButton;
    }

    /**
     * ButtonField class with width 90% of the screen width
     */
    public class WideButtonField extends ButtonField {

        /**
         * Creates a new WideButtonField object
         * 
         * @param label
         *            Label text for the button
         */
        public WideButtonField(final String label) {
            super(label);
        }

        /**
         * @see net.rim.device.api.ui.component.ButtonField#getPreferredWidth()
         */
        public int getPreferredWidth() {
            return (int) (Display.getWidth() * 0.9);
        }
    }
}
