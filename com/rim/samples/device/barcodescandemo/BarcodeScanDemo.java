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

import net.rim.device.api.amms.control.camera.ImageDecoder;
import net.rim.device.api.amms.control.camera.ImageDecoderListener;
import net.rim.device.api.amms.control.camera.ImageScanner;
import net.rim.device.api.barcodelib.BarcodeDecoder;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.TransitionContext;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngineInstance;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

/*
 * This application demonstrates the use of the Barcode API. It
 * presents a simple interface for accessing and displaying a window
 * in which the user can center a bar code. When a code is scanned, 
 * the application will display the code's type and embedded data.
 */
public final class BarcodeScanDemo extends UiApplication {

    /**
     * Entry point for the application
     * 
     * @param args
     *            Command-line argument (not used)
     */
    public static void main(final String[] args) {
        new BarcodeScanDemo().enterEventDispatcher();
    }

    /**
     * Creates a new BarcodeScanDemo object
     */
    public BarcodeScanDemo() {
        pushScreen(new ScanScreen());
    }
}

/**
 * A UI screen to display the camera display and buttons
 */
final class ScanScreen extends MainScreen {
    private ImageScanner _scanner;
    private MainScreen _vfScreen;
    private ButtonField _QRButton;
    private ButtonField _UPCButton;
    private ButtonField _dataMatrixButton;
    private ButtonField _code128Button;

    /**
     * Creates a new ScanScreen object
     */
    public ScanScreen() {
        // Set the title of the screen
        setTitle("Barcode Scan Demo");
        buildUi();
    }

    /**
     * Builds the user interface. Adds buttons and registers MyFieldListeners
     * for each.
     */
    private void buildUi() {

        _QRButton =
                new MyButtonField("Scan QR", ButtonField.CONSUME_CLICK
                        | ButtonField.NEVER_DIRTY | Field.FIELD_HCENTER
                        | Field.FIELD_VCENTER);
        _QRButton.setChangeListener(new MyFieldListener());

        _dataMatrixButton =
                new MyButtonField("Scan DataMatrix", ButtonField.CONSUME_CLICK
                        | ButtonField.NEVER_DIRTY | Field.FIELD_HCENTER
                        | Field.FIELD_VCENTER);
        _dataMatrixButton.setChangeListener(new MyFieldListener());

        _UPCButton =
                new MyButtonField("Scan UPC", ButtonField.CONSUME_CLICK
                        | ButtonField.NEVER_DIRTY | Field.FIELD_HCENTER
                        | Field.FIELD_VCENTER);
        _UPCButton.setChangeListener(new MyFieldListener());

        _code128Button =
                new MyButtonField("Scan Code 128", ButtonField.CONSUME_CLICK
                        | ButtonField.NEVER_DIRTY | Field.FIELD_HCENTER
                        | Field.FIELD_VCENTER);
        _code128Button.setChangeListener(new MyFieldListener());

        add(_QRButton);
        add(_dataMatrixButton);
        add(_UPCButton);
        add(_code128Button);
    }

    /**
     * Allows for larger than normal ButtonFields
     */
    private class MyButtonField extends ButtonField {
        public MyButtonField(final String label, final long style) {
            super(label, style);
        }

        /**
         * Retreives the buttons perferred width (60% of Display.getWidth())
         * 
         * @return The preferred width of the button
         */
        public int getPreferredWidth() {
            return (int) (Display.getWidth() * 0.60);
        }
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Prevent the save dialog from being displayed
        return true;
    }

    /**
     * Displays the decoded barcode text and the type of barcode that was just
     * scanned.
     * 
     * @param result
     *            The results from scanning a barcode
     */
    public void inform(final Result result) {
        // Close the viewfinder first
        UiApplication.getUiApplication().popScreen(_vfScreen);

        final MainScreen resultsScreen = new MainScreen();

        final LabelField text =
                new LabelField("Barcode Text: " + result.getText());
        text.setPadding(4, 4, 4, 4);

        final LabelField type =
                new LabelField("Barcode Type: "
                        + result.getBarcodeFormat().toString());
        type.setPadding(4, 4, 4, 4);

        final TransitionContext context =
                new TransitionContext(TransitionContext.TRANSITION_SLIDE);
        context.setIntAttribute(TransitionContext.ATTR_DIRECTION,
                TransitionContext.KIND_OUT);

        Ui.getUiEngineInstance().setTransition(null, resultsScreen,
                UiEngineInstance.TRIGGER_PUSH, context);

        resultsScreen.add(text);
        resultsScreen.add(type);

        UiApplication.getUiApplication().pushScreen(resultsScreen);
    }

    /**
     * Initializes the camara to begin scanning the barcode
     * 
     * @param decoder
     *            The decoder that is going to be used to decode the barcode
     * @param listener
     *            The listner that is going to be used to tell when the barcode
     *            is scanned
     * @return An initialized Viewfinder screen to be displayed
     */
    private Field initializeCamera(final ImageDecoder decoder,
            final ImageDecoderListener listener) {
        try {
            // Check if the ImageScanner has already been initialized. If it
            // has,
            // make sure to close its player, so that a new one can be created.
            if (_scanner != null) {
                _scanner.getPlayer().close();
            }

            _scanner = new ImageScanner(decoder, listener);
            _scanner.getVideoControl().setDisplayFullScreen(true);
            _scanner.startScan();
            return _scanner.getViewfinder();
        } catch (final Exception e) {
        }
        return null;
    }

    /**
     * Handles field change events
     */
    private class MyFieldListener implements FieldChangeListener {

        final ImageDecoderListener _imageDecoderListener =
                new ImageDecoderListener() {

                    /**
                     * @see net.rim.device.api.amms.control.camera.ImageDecoderListener#imageDecoded(Object)
                     */
                    public void imageDecoded(final Object decoded) {
                        UiApplication.getUiApplication().invokeLater(
                                new Runnable() {
                                    public void run() {
                                        inform((Result) decoded);
                                    }
                                });
                    }
                };

        /**
         * Listens for when a button is pressed
         * 
         * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field,
         *      int)
         */
        public void fieldChanged(final Field field, final int context) {
            final Vector formats = new Vector();

            // Find what button was pressed and add its format(s) to the vector
            if (field == _QRButton) {
                System.out.println("QR button");
                formats.addElement(BarcodeFormat.QR_CODE);
            } else if (field == _dataMatrixButton) {
                formats.addElement(BarcodeFormat.DATAMATRIX);
            } else if (field == _UPCButton) {
                formats.addElement(BarcodeFormat.UPC_A);
                formats.addElement(BarcodeFormat.UPC_E);
                formats.addElement(BarcodeFormat.EAN_13);
                formats.addElement(BarcodeFormat.EAN_8);
            } else if (field == _code128Button) {
                formats.addElement(BarcodeFormat.CODE_128);
            }

            // Allows for the decoder to look for a specific type of barcode.
            // Can increase the decoders speed or accuracy.
            final Hashtable hints = new Hashtable();
            hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);

            // Uncomment the following line of java code if you wish to
            // "try harder",
            // but expect that as a result the decoding time for each image
            // processed
            // may take upwards of 20 times as long. It is recommended that this
            // setting
            // not be used in most cases.
            // hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

            // Initialize the camera object and video field
            final Field cameraField =
                    initializeCamera(new BarcodeDecoder(hints),
                            _imageDecoderListener);

            // If the field was constructed successfully, create the UI
            if (cameraField != null) {
                _vfScreen = new MainScreen();
                _vfScreen.add(cameraField);
                UiApplication.getUiApplication().pushScreen(_vfScreen);
            }
        }
    }
}
