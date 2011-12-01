/*
 * BarcodeScanDemo.java
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

package com.rim.samples.device.barcodescandemo;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.media.MediaException;

import net.rim.device.api.barcodelib.BarcodeDecoder;
import net.rim.device.api.barcodelib.BarcodeDecoderListener;
import net.rim.device.api.barcodelib.BarcodeScanner;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;

/*
 * This application demonstrates the use of the Barcode API. It
 * presents a simple interface for accessing and displaying a window
 * in which the user can center a QR (Quick Response) Code. When a 
 * code is scanned, the application will return to the original 
 * screen and display the code's embedded data.
 */
public final class BarcodeScanDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command-line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new BarcodeScanDemo().enterEventDispatcher();
    }

    /**
     * Creates a new BarcodeScanDemo object
     */
    public BarcodeScanDemo() {
        pushScreen(new BarcodeScanDemoScreen());
    }
}

/**
 * A UI screen to display the camera display and buttons
 */
final class BarcodeScanDemoScreen extends MainScreen {
    private ViewFinderScreen _viewFinderScreen;
    private final LabelField _scannedText;

    /**
     * Creates a new BarcodeScanDemoScreen object
     */
    public BarcodeScanDemoScreen() {
        // Set the title of the screen
        setTitle("Barcode Scan Demo");

        // Create a button which will launch a viewfinder screen
        final ButtonField buttonField =
                new ButtonField("Scan QR Barcode", ButtonField.CONSUME_CLICK
                        | ButtonField.NEVER_DIRTY | Field.FIELD_HCENTER
                        | Field.FIELD_VCENTER);
        buttonField.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(final Field field, final int context) {
                // If no screen exists, create one before displaying
                if (_viewFinderScreen == null) {
                    _viewFinderScreen = new ViewFinderScreen();
                }

                // Push view finder screen onto the display stack
                UiApplication.getUiApplication().pushScreen(_viewFinderScreen);

                // Begin the scanning process
                _viewFinderScreen.startScan();
            }
        });

        buttonField.setPadding(2, 2, 2, 2);
        add(buttonField);

        _scannedText = new LabelField();
        _scannedText.setPadding(5, 5, 5, 5);
        add(_scannedText);
    }

    /**
     * A MainScreen subclass to display a view finder which presents camera
     * input to the user and uses a BarcodeScanner to periodically check for the
     * presence of a QR code.
     */
    private final class ViewFinderScreen extends MainScreen {
        private BarcodeScanner _scanner;
        private final short _frequency = 1046;
        private final short _duration = 200;
        private final int _volume = 100;

        /**
         * Creates a new ViewFinderScreen object
         */
        public ViewFinderScreen() {
            // Initialize Hashtable used to inform the scanner how to
            // recognize the QR code format.
            final Hashtable hints = new Hashtable();
            final Vector formats = new Vector(1);
            formats.addElement(BarcodeFormat.QR_CODE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);

            // Initialize the BarcodeDecoder
            final BarcodeDecoder decoder = new BarcodeDecoder(hints);

            // Create a custom instance of a BarcodeDecoderListener to pop the
            // screen and display results when a QR code is recognized.
            final BarcodeDecoderListener decoderListener =
                    new BarcodeDecoderListener() {
                        /**
                         * @see BarcodeDecoderListener#barcodeDecoded(String)
                         */
                        public void barcodeDecoded(final String rawText) {
                            displayMessage(rawText);
                            beep();
                        }
                    };

            try {
                // Initialize the BarcodeScanner object and add the associated
                // view finder.
                _scanner = new BarcodeScanner(decoder, decoderListener);
                _scanner.getVideoControl().setDisplayFullScreen(true);
                add(_scanner.getViewfinder());

            } catch (final Exception e) {
                displayMessage("Error: " + e.getMessage());
            }
        }

        /**
         * Informs the BarcodeScanner that it should begin scanning for QR Codes
         */
        public void startScan() {
            try {
                _scanner.startScan();
            } catch (final MediaException me) {
                displayMessage("Error: " + me.getMessage());
            }
        }

        /**
         * @see net.rim.device.api.ui.Screen#keyChar()
         */
        protected boolean keyChar(final char key, final int status,
                final int time) {
            if (key == Characters.ESCAPE) {
                // Manually stop the scanning process and pop the screen
                try {
                    _scanner.stopScan();
                    UiApplication.getUiApplication().popScreen(
                            ViewFinderScreen.this);
                } catch (final MediaException me) {
                    displayMessage("Error: " + me.getMessage());
                }
            }

            return super.keyChar(key, status, time);
        }

        /**
         * @see net.rim.device.api.ui.Screen#close()
         */
        public void close() {
            try {
                _scanner.stopScan();
            } catch (final MediaException me) {
                displayMessage("Error: " + me.getMessage());
            }

            super.close();
        }

        /**
         * Pops the ViewFinderScreen and displays text on the main screen
         * 
         * @param text
         *            Text to display on the screen
         */
        private void displayMessage(final String text) {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    _scannedText.setText(text);
                    UiApplication.getUiApplication().popScreen(
                            ViewFinderScreen.this);
                }
            });
        }

        /**
         * Beeps to notify the user that a scan was successful
         */
        private void beep() {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Alert.startAudio(new short[] { _frequency, _duration },
                            _volume);
                }
            });
        }
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }
}
