/*
 * SVGPickScreen.java
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

package com.rim.samples.device.svgpickdemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.m2g.SVGImage;
import javax.microedition.m2g.ScalableGraphics;
import javax.microedition.m2g.ScalableImage;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.MathUtilities;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLocatableElement;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * SVG cursor program to select from a group of selectable shapes and display
 * stats
 */
final class SVGPickScreen extends MainScreen {
    private static final int DISPLAY_WIDTH = Display.getWidth();
    private static final int DISPLAY_HEIGHT = Display.getHeight();

    private static final int CURSOR_X_DEFAULT = 0;
    private static final int CURSOR_Y_DEFAULT = 0;
    private static final int CURSOR_MOVE_FACTOR = 10;

    private ScalableGraphics _scalableGraphics;
    private SVGImage _image;
    private SVGSVGElement _root;
    private Document _document;

    private int _cursorX;
    private int _cursorY;
    private SVGElement _cursor;
    private SVGElement _cursorPosition;

    private SVGElement _selectableShapesGroup;
    private final Vector _selectableShapes = new Vector();
    private SVGElement _selectedShapeBounds;

    private SVGElement _display;
    private SVGElement _displayBorder;
    private SVGElement _displayName;
    private SVGElement _displayBounds;

    /**
     * Constructor.
     */
    SVGPickScreen() {
        setTitle("SVG Pick Demo");

        // Initial position of cursor point
        _cursorX = CURSOR_X_DEFAULT;
        _cursorY = CURSOR_Y_DEFAULT;

        try {
            // Load SVG from svg
            final InputStream inputStream =
                    getClass().getResourceAsStream("/sample.svg");

            // Create SVGImage
            _image = (SVGImage) ScalableImage.createImage(inputStream, null);
            // Create Document
            _document = _image.getDocument();
            // Get SVG root element.
            _root = (SVGSVGElement) _document.getDocumentElement();
            // Create ScalableGraphics Instance
            _scalableGraphics = ScalableGraphics.createInstance();

            // Initialize our view bindings.
            intializeElements();

            updateCursor(CURSOR_X_DEFAULT, CURSOR_Y_DEFAULT, 0);
        } catch (final IOException e) {
            System.exit(1);
        }
    }

    /**
     * Gets anhd initializes any view elements.
     */
    private void intializeElements() {
        // Get the cursor related elements.
        _cursor = (SVGElement) _document.getElementById("cursor");
        _cursorPosition =
                (SVGElement) _document.getElementById("cursorPosition");

        // Get the display elements which we will interact with
        _display = (SVGElement) _document.getElementById("display");
        _displayName = (SVGElement) _document.getElementById("displayName");
        _displayBounds = (SVGElement) _document.getElementById("displayBounds");

        // Get the selected shape elements and get the child elements of the
        // group
        // adding them to the array which is being managed.
        _selectedShapeBounds =
                (SVGElement) _document.getElementById("selectedShapeBounds");
        _selectableShapesGroup =
                (SVGElement) _document.getElementById("selectableShapesGroup");

        // Append all our selectable elements to the managed set.
        SVGLocatableElement element =
                (SVGLocatableElement) _selectableShapesGroup.getFirstChild();
        _selectableShapes.addElement(element);

        while ((element = (SVGLocatableElement) element.getNextSibling()) != null) {
            _selectableShapes.addElement(element);
        }
    }

    /**
     * @see Manager#navigationMovement(int, int, int, int)
     */
    protected boolean navigationMovement(final int dx, final int dy,
            final int status, final int time) {
        // Update our new cursor position
        updateCursor(dx, dy, CURSOR_MOVE_FACTOR);

        // If a shape is selected then update the display.
        final SVGLocatableElement selected = findFirstSelectedShape();

        if (selected != null) {
            // Get the bounding box of selected object
            final SVGRect rect = selected.getBBox();

            // Update the display for the selected shape.
            updateDisplay(true, selected.getTrait("id"), rect);

            // Updated the bounding box for the selected shape.
            updateBounds(true, rect);
        } else {
            updateDisplay(false, null, null);
            updateBounds(false, null);
        }

        invalidate();

        return true;
    }

    /**
     * Updates our cursor position.
     * 
     * @param dx
     *            Delta x for the cursor.
     * @param dy
     *            Delta y for the cursor.
     */
    private void updateCursor(final int dx, final int dy, final int factor) {
        final int deltaX = factor * dx;
        final int deltaY = factor * dy;

        // Translate the cursor pointer.
        final int translateX = clampDelta(0, DISPLAY_WIDTH, _cursorX, deltaX);
        final int translateY = clampDelta(0, DISPLAY_HEIGHT, _cursorY, deltaY);
        final SVGMatrix transform = _cursor.getMatrixTrait("transform");
        transform.mTranslate(translateX, translateY);
        _cursor.setMatrixTrait("transform", transform);

        // Update cursor position Text
        _cursorX = MathUtilities.clamp(0, _cursorX + translateX, DISPLAY_WIDTH);
        _cursorY =
                MathUtilities.clamp(0, _cursorY + translateY, DISPLAY_HEIGHT);
        _cursorPosition.setTrait("#text", "" + _cursorX + ',' + _cursorY);

    }

    private int clampDelta(final int min, final int max, final int current,
            final int delta) {
        final int currentAndDelta = current + delta;
        if (currentAndDelta < min) {
            return current - min;
        } else if (currentAndDelta > max) {
            return max - current;
        } else {
            return delta;
        }
    }

    /**
     * Searches for the first selected shape.
     * 
     * @return The shape that is located or <code>null</code> if no shape is
     *         found.
     */
    private SVGLocatableElement findFirstSelectedShape() {
        // Scroll through shapes, determine if cursor point
        // is within shape's bounding box, and update.
        SVGRect box;

        for (int i = 0; i < _selectableShapes.size(); i++) {
            final SVGLocatableElement element =
                    (SVGLocatableElement) _selectableShapes.elementAt(i);
            box = element.getBBox();

            // Do a bounding box to point test.
            if (_cursorX >= box.getX()
                    && _cursorX <= box.getX() + box.getWidth()
                    && _cursorY >= box.getY()
                    && _cursorY <= box.getY() + box.getHeight()) {
                return element;
            }
        }
        return null;
    }

    /**
     * Updates our display id and bounds region.
     * 
     * @param enabled
     *            <code>true</code> if the display is enabled.
     *            <code>false</code> if disabled.
     * @param id
     *            The id of the selected shape.
     * @param bounds
     *            The bounds of the selected shape.
     */
    private void updateDisplay(final boolean enabled, final String id,
            final SVGRect bounds) {
        if (enabled) {
            _display.setTrait("display", "inline");

            if (id != null) {
                // Update the id.
                _displayName.setTrait("#text", id);
            }

            if (bounds != null) {
                // Update the bounds region.
                final StringBuffer boundsText = new StringBuffer();
                boundsText.append(bounds.getX());
                boundsText.append(',');
                boundsText.append(bounds.getY());
                boundsText.append(',');
                boundsText.append(bounds.getWidth());
                boundsText.append(',');
                boundsText.append(bounds.getHeight());
                _displayBounds.setTrait("#text", boundsText.toString());
            }
        } else {
            _display.setTrait("display", "none");
        }
    }

    /**
     * Updates the selected shape bounding box.
     * 
     * @param enabled
     *            <code>true</code> if the display is enabled.
     *            <code>false</code> if disabled.
     * @param rect
     */
    private void updateBounds(final boolean enabled, final SVGRect rect) {
        if (enabled) {
            // Show the bounding box.
            _selectedShapeBounds.setTrait("display", "inline");

            if (rect != null) {
                // Updates the actually selected shapes bounding box.
                _selectedShapeBounds.setFloatTrait("x", rect.getX());
                _selectedShapeBounds.setFloatTrait("y", rect.getY());
                _selectedShapeBounds.setFloatTrait("width", rect.getWidth());
                _selectedShapeBounds.setFloatTrait("height", rect.getHeight());
            }
        } else {
            // Hide the bounding box.
            _selectedShapeBounds.setTrait("display", "none");
        }
    }

    /**
     * @see Manager#paint(Graphics)
     */
    public void paint(final Graphics graphics) {
        super.paint(graphics);

        if (_image == null) {
            return;
        }

        // Bind target Graphics
        _scalableGraphics.bindTarget(graphics);

        // Set the viewport dimensions
        _image.setViewportWidth(DISPLAY_WIDTH);
        _image.setViewportHeight(DISPLAY_HEIGHT);

        // Render the svg image/ model
        _scalableGraphics.render(0, 0, _image);

        // Release bindings on Graphics
        _scalableGraphics.releaseTarget();
    }
}
