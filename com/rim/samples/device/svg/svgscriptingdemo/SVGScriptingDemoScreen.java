/*
 * SVGScriptingDemoScreen.java
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

package com.rim.samples.device.svg.svgscriptingdemo;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.m2g.SVGAnimator;
import javax.microedition.m2g.SVGImage;
import javax.microedition.m2g.ScalableImage;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGElement;

/**
 * This class resizes the SVG document to fit the device's display screen and
 * then displays the SVG image.
 */
public class SVGScriptingDemoScreen extends MainScreen {
    // As per the quadrant size of 150 in the svg script
    private static final int ORIGINAL_DISPLAY_SIZE = 300;

    // As per the button size in the svg script
    private static final int ORIGINAL_BUTTON_SIZE = 80;

    private static final int DISPLAY_Y_OFFSET = 10;

    /**
     * Creates a new SVGScriptingDemoScreen object
     */
    public SVGScriptingDemoScreen() {
        super(Manager.NO_VERTICAL_SCROLL);

        try {
            // Load SVG from sample.svg
            final InputStream inputStream =
                    getClass().getResourceAsStream("/sample.svg");

            // Create SVGImage and resize it according to the dimensions of the
            // device screen.
            final SVGImage image =
                    (SVGImage) ScalableImage.createImage(inputStream, null);
            resize(image.getDocument());

            // Retrieve the SVG animator, add it to the screen, and start the
            // SVG
            final SVGAnimator animator =
                    SVGAnimator.createAnimator(image,
                            "net.rim.device.api.ui.Field");
            final Field svgField = (Field) animator.getTargetComponent();

            add(svgField);
            animator.play();
        } catch (final IOException e) {
            add(new RichTextField("Could not load the svg file: " + e));
        }
    }

    /**
     * Resizes the SVG document to fit onto the device screen
     * 
     * Note: this method is specific to the sample svg only
     * 
     * @param doc
     *            The SVG document to resize
     */
    public static void resize(final Document doc) {
        // Calculate the new dimension of the display based on the minimum
        // display dimension size.
        final int displayWidth = Display.getWidth();
        final int displayHeight = Display.getHeight();
        final int minDimension = Math.min(displayWidth, displayHeight);
        final float newDimension = 0.9f * minDimension;

        // Scale and center the display element to a portion of the device's
        // minimum display dimension.
        final float translateX = (displayWidth - newDimension) / 2;
        final float scaleWidth = newDimension / ORIGINAL_DISPLAY_SIZE;
        final float scaleHeight = newDimension / ORIGINAL_DISPLAY_SIZE;
        final SVGElement display = (SVGElement) doc.getElementById("display");
        display.setAttribute("transform", "translate(" + translateX + " "
                + DISPLAY_Y_OFFSET + ") " + "scale(" + scaleWidth + " "
                + scaleHeight + ")");

        // Scale and position the play and stop buttons
        final float newBtnSize = 0.2f * (minDimension - DISPLAY_Y_OFFSET);
        final float scaleBtn = newBtnSize / ORIGINAL_BUTTON_SIZE;
        final SVGElement playBtn = (SVGElement) doc.getElementById("play");
        playBtn.setAttribute("transform", "translate(" + newBtnSize + " "
                + (displayHeight - newBtnSize) + ") " + "scale(" + scaleBtn
                + ")");

        final SVGElement stopBtn = (SVGElement) doc.getElementById("stop");
        stopBtn.setAttribute("transform", "translate("
                + (displayWidth - newBtnSize) + " "
                + (displayHeight - newBtnSize) + ") " + "scale(" + scaleBtn
                + ")");
    }
}
