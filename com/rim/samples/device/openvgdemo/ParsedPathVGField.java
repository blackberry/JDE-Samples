/**
 * ParsedPathVGField.java
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

package com.rim.samples.device.openvgdemo;

import net.rim.device.api.openvg.VG;
import net.rim.device.api.openvg.VG10;
import net.rim.device.api.openvg.VG11;
import net.rim.device.api.openvg.VGField;
import net.rim.device.api.openvg.VGUtils;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontSpec;

/**
 * A VGField class that demonstrates drawing of path information read from a
 * text file and displays the resulting image on the screen. The class also uses
 * Open VG to draw a textual description on the screen.
 */
public class ParsedPathVGField extends VGField {
    private static final int TEXT_OFFSET = 30;

    private static final float PATHS_IMAGE_HEIGHT = 600.0f;
    private static final float PATHS_IMAGE_WIDTH = 600.0f;
    private static final float[] MY_CLEAR_COLOR = new float[] { 0.6f, 0.8f,
            1.0f, 1.0f };

    private int _textImage;
    private int _displayWidth;
    private int _displayHeight;

    private Path[] _svgPathsArray;

    /**
     * Creates a new ParsedPathVGField object
     */
    public ParsedPathVGField() {
        super(VGField.VERSION_1_1);
    }

    /**
     * @see net.rim.device.api.openvg.VGField#initialize(VG)
     */
    protected void initialize(final VG vg) {
        // Code to initialize an OpenVG resource
        final VG11 vg11 = (VG11) vg;
        vg11.vgSetfv(VG10.VG_CLEAR_COLOR, 4, MY_CLEAR_COLOR, 0);

        // Read paths from file
        final PathFileReader pathReader = new PathFileReader();
        _svgPathsArray = pathReader.getPaths(vg11, "/res/paths.txt");

        // Get the default Font and its FontSpec
        final Font font = Font.getDefault();
        final FontSpec fontSpec = font.getFontSpec();

        // Create text images
        _textImage =
                VGUtils.vgCreateTextAsImage(vg, fontSpec,
                        "Tap or click to swap screens", null, null);
    }

    /**
     * @see net.rim.device.api.openvg.VGField#render(VG)
     */
    protected void render(final VG renderer) {
        final VG11 vg11 = (VG11) renderer;

        // Clear the display from the last time it was rendered
        vg11.vgClear(0, 0, getWidth(), getHeight());

        // Draw the text images on this field
        drawText(vg11);

        // Set VG_MATRIX_MODE to VG_MATRIX_PATH_USER_TO_SURFACE
        vg11.vgSeti(VG10.VG_MATRIX_MODE, VG10.VG_MATRIX_PATH_USER_TO_SURFACE);

        // Load a clean identity matrix
        vg11.vgLoadIdentity();

        // Calculate scale factor
        final float scaleFactor =
                Math.min(_displayHeight / PATHS_IMAGE_HEIGHT, _displayWidth
                        / PATHS_IMAGE_WIDTH);

        // Calculate x position
        final float xPos =
                (_displayWidth - PATHS_IMAGE_WIDTH * scaleFactor) / 2;

        // Move the y position to just below the text that was drawn
        final int yPos = _displayHeight - (TEXT_OFFSET + 5);

        // Translate to origin corresponding to SVG (top left)
        vg11.vgTranslate(xPos, yPos);

        // Scale the image
        vg11.vgScale(scaleFactor, -scaleFactor);

        // Draw the image on the screen
        drawPaths(vg11, _svgPathsArray);
    }

    /**
     * Draws text at the top of the VGField
     * 
     * @param vg
     *            The object that will be used to render the text
     */
    public void drawText(final VG11 vg) {
        vg.vgSeti(VG10.VG_MATRIX_MODE, VG10.VG_MATRIX_IMAGE_USER_TO_SURFACE);

        // Load a clean identity matrix
        vg.vgLoadIdentity();

        // Translate to correct drawing location
        vg.vgTranslate(0.0f, _displayHeight - TEXT_OFFSET);

        // Draw the text on the display
        vg.vgDrawImage(_textImage);
    }

    /**
     * @see net.rim.device.api.openvg.VGField#layout(int, int)
     */
    protected void layout(final int width, final int height) {
        _displayWidth = Display.getWidth();
        _displayHeight = Display.getHeight();

        setExtent(Math.min(_displayWidth, width), Math.min(_displayHeight,
                height));
    }

    /**
     * Draws the paths read from the paths.txt file onto the display
     * 
     * @param vg
     *            VG object that will be used to render the paths
     * @param paths
     *            Paths read from paths.txt that will be drawn to the display
     */
    private void drawPaths(final VG11 vg, final Path[] paths) {
        // Go through the array containing all paths
        // and draw each one to the screen.
        for (int i = 0, length = paths.length; i < length; i++) {
            final Path path = paths[i];
            path.draw(vg);
        }
    }
}
