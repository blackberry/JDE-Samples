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
import net.rim.device.api.ui.TouchEvent;
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
 * stats.
 * 
 * The attribute values of the SVG elements in sample.svg were hard-coded in a
 * way to display the image correctly on a 9500 device. However, one can
 * programmatically adjust those values by calling setFloatTrait() on the
 * SVGElement.
 */
final class SVGPickScreen extends MainScreen {
    private static final int CURSOR_X_DEFAULT = 30;
    private static final int CURSOR_Y_DEFAULT = 60;
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

    private int _displayWidth;
    private int _displayHeight;

    /**
     * Constructor.
     */
    SVGPickScreen() {
        setTitle("SVG Pick Demo");

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

            _displayWidth = Display.getWidth();
            _displayHeight = Display.getHeight();

            // Initialize our view bindings.
            intializeElements();

            // Initial position of cursor point.
            updateCursor(CURSOR_X_DEFAULT, CURSOR_Y_DEFAULT);
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
     * @see net.rim.device.api.ui.Screen#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(final TouchEvent message) {
        final int touchEvent = message.getEvent();
        if (touchEvent == TouchEvent.DOWN || touchEvent == TouchEvent.CLICK
                || touchEvent == TouchEvent.MOVE) {
            final int dx = message.getX(1) - _cursorX;
            final int dy = message.getY(1) - _cursorY;
            updateCursor(dx, dy);
        }

        return true;
    }

    /**
     * @see Manager#navigationMovement(int, int, int, int)
     */
    protected boolean navigationMovement(final int dx, final int dy,
            final int status, final int time) {
        final int deltaX = CURSOR_MOVE_FACTOR * dx;
        final int deltaY = CURSOR_MOVE_FACTOR * dy;

        // Update our new cursor position
        updateCursor(deltaX, deltaY);

        return true;
    }

    /**
     * Updates our cursor position.
     */
    private void updateCursor(final int deltaX, final int deltaY) {
        // Translate the cursor pointer.
        final int translateX = clampDelta(0, _displayWidth, _cursorX, deltaX);
        final int translateY = clampDelta(0, _displayHeight, _cursorY, deltaY);
        final SVGMatrix transform = _cursor.getMatrixTrait("transform");
        transform.mTranslate(translateX, translateY);
        _cursor.setMatrixTrait("transform", transform);

        // Update cursor position Text
        _cursorX = MathUtilities.clamp(0, _cursorX + translateX, _displayWidth);
        _cursorY =
                MathUtilities.clamp(0, _cursorY + translateY, _displayHeight);
        _cursorPosition.setTrait("#text", "" + _cursorX + ',' + _cursorY);

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
        _image.setViewportWidth(_displayWidth);
        _image.setViewportHeight(_displayHeight);

        // Render the svg image/ model
        _scalableGraphics.render(0, 0, _image);

        // Release bindings on Graphics
        _scalableGraphics.releaseTarget();
    }

    /**
     * @see net.rim.device.api.ui.container.FullScreen#sublayout(int, int)
     */
    protected void sublayout(final int width, final int height) {
        if (_displayWidth != width || _displayHeight != height) {
            _displayWidth = width;
            _displayHeight = height;
        }

        super.sublayout(width, height);
    }

}
