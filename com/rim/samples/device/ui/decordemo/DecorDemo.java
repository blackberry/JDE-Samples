/**
 * DecorDemo.java
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

package com.rim.samples.device.ui.decordemo;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

/**
 * This sample application shows functionality for the
 * net.rim.device.api.ui.decor package and all of its classes.
 */
public final class DecorDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final DecorDemo theApp = new DecorDemo();
        theApp.enterEventDispatcher();
    }

    // Constructor
    public DecorDemo() {
        final DecorDemoScreen screen = new DecorDemoScreen();
        pushScreen(screen);
    }
}

/**
 * This class is the main screen of the application
 */
final class DecorDemoScreen extends MainScreen {
    /**
     * Creates a new DecorDemoScreen object
     */
    public DecorDemoScreen() {
        // XYEdge objects are used to represent different padding configurations
        // Each integer represents the amount of space between the box and
        // border
        // The four parameters of an XYEdge object represents each edge,
        // clockwise starting at the top
        final XYEdges thickPadding = new XYEdges(10, 10, 10, 10);
        final XYEdges noPadding = new XYEdges(0, 0, 0, 0);

        // Sample padding objects:
        // XYEdges verticalPadding = new XYEdges(10, 0, 10, 0);
        // XYEdges horizontalPadding = new XYEdges(0, 10, 0, 10);

        // XYEdges can also be used to represent colors
        final XYEdges multiColors =
                new XYEdges(Color.BLUEVIOLET, Color.AZURE, Color.DARKRED,
                        Color.KHAKI);
        final XYEdges pinkColors =
                new XYEdges(Color.HOTPINK, Color.HOTPINK, Color.HOTPINK,
                        Color.HOTPINK);
        final XYEdges blueColors =
                new XYEdges(Color.MIDNIGHTBLUE, Color.MIDNIGHTBLUE,
                        Color.MIDNIGHTBLUE, Color.MIDNIGHTBLUE);
        final XYEdges oliveColors =
                new XYEdges(Color.OLIVE, Color.OLIVE, Color.OLIVE, Color.OLIVE);

        // Set the title of the application
        setTitle("Décor Demo");

        add(new RichTextField(Field.NON_FOCUSABLE));

        // Sample text field with a thick and solid rounded border
        // and single solid color background.
        final RichTextField simpleField =
                new RichTextField("Solid rounded border, solid background");

        // Create border and background objects
        final Border roundedBorder =
                BorderFactory.createRoundedBorder(thickPadding,
                        Border.STYLE_SOLID);
        final Background solidBackground =
                BackgroundFactory.createSolidBackground(Color.LIGHTSTEELBLUE);

        // Set the objects for use
        simpleField.setBorder(roundedBorder);
        simpleField.setBackground(solidBackground);

        // Add the field to the screen
        add(simpleField);
        add(new RichTextField(Field.NON_FOCUSABLE));

        // Sample text field with a thick and dotted rounded border
        // and single color transparent background.
        final RichTextField transparentField =
                new RichTextField(
                        "Dotted rounded border, transparent background");
        final Border dottedBorder =
                BorderFactory.createRoundedBorder(thickPadding,
                        Border.STYLE_DOTTED);
        final Background transparentBackground =
                BackgroundFactory.createSolidTransparentBackground(
                        Color.LIGHTSTEELBLUE, 50);
        transparentField.setBorder(dottedBorder);
        transparentField.setBackground(transparentBackground);
        add(transparentField);
        add(new RichTextField(Field.NON_FOCUSABLE));

        // Sample text field with a thick and dashed border
        // and gradient background.
        final RichTextField gradientField =
                new RichTextField("Dashed simple border, gradient background");
        final Border dashedBorder =
                BorderFactory.createSimpleBorder(thickPadding, blueColors,
                        Border.STYLE_DASHED);
        final Background gradientBackground =
                BackgroundFactory.createLinearGradientBackground(Color.RED,
                        Color.GREEN, Color.BLUE, Color.WHITE);
        gradientField.setBorder(dashedBorder);
        gradientField.setBackground(gradientBackground);
        add(gradientField);
        add(new RichTextField(Field.NON_FOCUSABLE));

        // Sample text field with a no padding dotted border and no background.
        final RichTextField noPaddingField =
                new RichTextField("Dotted simple border, no padding");
        final Border noPaddingBorder =
                BorderFactory.createSimpleBorder(noPadding, oliveColors,
                        Border.STYLE_DOTTED);
        noPaddingField.setBorder(noPaddingBorder);
        add(noPaddingField);
        add(new RichTextField(Field.NON_FOCUSABLE));

        // Sample text field with a thick bevel border and bitmap background.
        final RichTextField bevelAndBitmapField =
                new RichTextField("Bevel border, bitmap background");
        final Border bevelBorder =
                BorderFactory.createBevelBorder(thickPadding, multiColors,
                        pinkColors);

        // Attempt to load a bitmap background
        try {
            final Background bitmapBackground =
                    BackgroundFactory.createBitmapBackground(Bitmap
                            .getBitmapResource("smiley.bmp"),
                            Background.POSITION_X_CENTER,
                            Background.POSITION_Y_CENTER,
                            Background.REPEAT_BOTH);
            bevelAndBitmapField.setBackground(bitmapBackground);
        } catch (final IllegalArgumentException e) {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert(e.toString());
                }
            });
        }
        bevelAndBitmapField.setBorder(bevelBorder);
        add(bevelAndBitmapField);
    }
}
