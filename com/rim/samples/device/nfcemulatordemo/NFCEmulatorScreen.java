/*
 * NFCEmulatorScreen.java
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
import net.rim.device.api.io.nfc.NFCManager;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * MainScreen class for the NFC Emulator Demo application
 */
public class NFCEmulatorScreen extends MainScreen {
    public static final String ISO_14443_A = "ISO 14443 A";
    public static final String ISO_14443_B = "ISO 14443 B";
    public static final String NDEF = "NDEF";

    private WideButtonField _startEmulationButton;
    private WideButtonField _stopEmulationButton;
    private ObjectChoiceField _emulationTypeChoiceField;
    private String[] _techTypes;
    private String _emulating;
    private LabelField _tagStatus;
    private LabelField _emulationListenerStatus;
    private LabelField _currentEmulation;
    private LabelField _fieldPresentStatus;
    private NFCManager _manager;

    /**
     * Creates a new NFCEmulatorScreen object
     */
    public NFCEmulatorScreen() {
        setTitle("NFC Emulator Demo");

        try {
            _manager = NFCManager.getInstance();
            buildUI();
        } catch (final Exception e) {
            add(new LabelField("Error: " + e.toString()));
        }
    }

    /**
     * Builds the application's initial user interface and registers an
     * NFCFieldListener.
     */
    public void buildUI() {
        try {
            // Register an NFCFieldListener
            _manager.addNFCFieldListener(new NFCEmulatorFieldDetector(this));
        } catch (final NFCException e) {
            add(new LabelField("Error: " + e.toString()));
        }

        // Set up the screen's button fields
        _startEmulationButton = new WideButtonField("Start Emulation");
        _stopEmulationButton = new WideButtonField("Stop Emulation");
        _stopEmulationButton.setEnabled(false);
        _startEmulationButton
                .setChangeListener(new NFCEmulatorFieldChangeListener(this));
        _stopEmulationButton
                .setChangeListener(new NFCEmulatorFieldChangeListener(this));

        // Set up an ObjectChoiceField which will allow the end
        // user to select a NFC technology type to emulate.
        _techTypes =
                new String[] { NFCEmulatorScreen.ISO_14443_A,
                        NFCEmulatorScreen.ISO_14443_B, NFCEmulatorScreen.NDEF };
        _emulationTypeChoiceField =
                new ObjectChoiceField("Technology type to emulate: ",
                        _techTypes);

        // Set up the screen's label fields
        _fieldPresentStatus = new LabelField("NFC Field Present: ");
        _tagStatus = new LabelField();
        _emulationListenerStatus = new LabelField();
        _currentEmulation = new LabelField();

        // Add fields to the screen
        add(_emulationTypeChoiceField);
        add(_startEmulationButton);
        add(_stopEmulationButton);
        add(_fieldPresentStatus);
        add(_tagStatus);
        add(_currentEmulation);
        add(_emulationListenerStatus);
    }

    /**
     * Sets text to indicate presence of an NFC field
     * 
     * @param status
     *            Text to be displayed
     */
    public void setFieldPresentStatus(final String status) {
        _fieldPresentStatus.setText("NFC Field Present: " + status);
    }

    /**
     * Sets text to indicate listener status
     * 
     * @param status
     *            Text to be displayed
     */

    public void setEmulationListenerStatus(final String status) {
        _emulationListenerStatus.setText(status);
    }

    /**
     * Sets text to indicate tag status
     * 
     * @param status
     *            Text to be displayed
     */
    public void setTagStatus(final String status) {
        _tagStatus.setText(status);
    }

    /**
     * Retrieves the screen's start button
     * 
     * @return A ButtonField object
     */
    public ButtonField getStartButton() {
        return _startEmulationButton;
    }

    /**
     * Retrieves the screen's stop button
     * 
     * @return A ButtonField object
     */
    public ButtonField getStopButton() {
        return _stopEmulationButton;
    }

    /**
     * Retrieves the user selected emulation type
     * 
     * @return String representing the selected emulation type
     */
    public String getEmulationType() {
        return _techTypes[_emulationTypeChoiceField.getSelectedIndex()];
    }

    /**
     * Sets the text representing the technology type that is currently being
     * emulated.
     * 
     * @param emulation
     *            String value of the technology type currently being emulated
     */
    public void setCurrentEmulation(final String emulation) {
        _currentEmulation.setText("Currently being emulated: " + emulation);
        _emulating = emulation;
    }

    /**
     * Retrieves the current technology type being emulated
     * 
     * @return String value of the technology type currently being emulated
     */
    public String getCurrentEmulation() {
        return _emulating;
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Prevent the save dialog from being displayed
        return true;
    }

    /**
     * A ButtonField the width of which approaches the display width
     */
    private class WideButtonField extends ButtonField {
        /**
         * Creates a new WideButtonField object
         * 
         * @param label
         *            Label that will be displayed on the button
         */
        public WideButtonField(final String label) {
            super(label);
        }

        /**
         * @see net.rim.device.api.ui.component.ButtonField#getPreferredWidth()
         */
        public int getPreferredWidth() {
            return (int) (Display.getWidth() * 0.90);
        }
    }
}
