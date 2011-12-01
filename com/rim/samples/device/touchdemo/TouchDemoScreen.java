/*
 * TouchDemoScreen.java
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

package com.rim.samples.device.touchdemo;

import javax.microedition.m2g.SVGImage;
import javax.microedition.m2g.ScalableGraphics;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * A screen which listens for touch events and draws lines that follow the drawn
 * path.
 */
public class TouchDemoScreen extends MainScreen {
    /** The namespace for SVG. */
    private static final String SVG_NAMESPACE_URI =
            "http://www.w3.org/2000/svg";

    /** The image to be painted. */
    private final SVGImage _image;

    /** Document to hold SVGImage contents. */
    private final Document _document;

    /** A scalable graphics instance. */
    private final ScalableGraphics _scalableGraphics;

    /** The SVG root element. */
    private final SVGSVGElement _svg;

    /** The color of the lines currently being drawn. */
    private String _color;

    /** The width of the lines currently being drawn. */
    private int _width = 2;

    /** The x coordinate of the last visited point. */
    private int _lastX = -1;

    /** The y coordinate of the last visited point. */
    private int _lastY = -1;

    /** The color array index for the current color */
    private int _colorIndex = 0;

    /** The amount of space taken up by the title field. */
    private int _titleHeight;

    // SVG constants
    private static final String SVG_LINE = "line";
    private static final String X1 = "x1";
    private static final String X2 = "x2";
    private static final String Y1 = "y1";
    private static final String Y2 = "y2";
    private static final String STROKE = "stroke";
    private static final String STROKE_WIDTH = "stroke-width";

    // Color constants
    private static final String COLOR_BLACK = "Black";
    private static final String COLOR_RED = "Red";
    private static final String COLOR_BLUE = "Blue";
    private static final String COLOR_GREEN = "Green";
    private static final String COLOR_YELLOW = "Yellow";

    /**
     * Constructor.
     */
    TouchDemoScreen() {
        setTitle("Touch Demo");

        // Initialize SVG.
        _image = SVGImage.createEmptyImage(null);
        _document = _image.getDocument();
        _svg = (SVGSVGElement) _document.getDocumentElement();
        _scalableGraphics = ScalableGraphics.createInstance();

        // Add menu items.
        addMenuItem(_eraseMenu);
        addMenuItem(_colourMenu);
        addMenuItem(_widthMenu);

        // Set the initial line color to black;
        _color = COLOR_BLACK;
    }

    /**
     * @see Field#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(final TouchEvent message) {
        // Retrieve the new x and y touch positions.
        final int x = message.getX(1);
        final int y = message.getY(1);

        final int eventCode = message.getEvent();

        if (eventCode == TouchEvent.DOWN) {
            // If this event is followed by a move event we'll need
            // to know our starting point.
            _lastX = x;
            _lastY = y;
        }

        if (eventCode == TouchEvent.MOVE) {
            if (_lastY > _titleHeight && _lastY < Display.getHeight()) {
                // Add a new line and repaint the screen.
                drawLine(_color, _width, x, y);
                invalidate();
            }

            // x and y will be the starting point for the next segment.
            _lastX = x;
            _lastY = y;
        } else if (eventCode == TouchEvent.UP) {
            // We have lost contact with the screen, reset the last x and y.
            _lastX = -1;
            _lastY = -1;
        }

        /*
         * In this case we are consuming the touch event as we don't require or
         * desire any of the screen's inherent behaviour to be executed as a
         * result of the touch event. In general, you should return
         * super.touchEvent(message) after handling the event so that the screen
         * can provide additional event handling such as scrolling
         */
        return true;
    }

    /**
     * Draws a line on the canvas between the last touch point and the current
     * one.
     * 
     * @param color
     *            The color of the line to be drawn.
     * @param width
     *            The width of the line to be drawn
     * @param x
     *            The ending x coordinate.
     * @param y
     *            The ending y coordinate.
     */
    private void drawLine(final String color, final int width, final int x,
            final int y) {
        // Create new element.
        final SVGElement newElement =
                (SVGElement) _document.createElementNS(SVG_NAMESPACE_URI,
                        SVG_LINE);
        newElement.setFloatTrait(X1, _lastX);
        newElement.setFloatTrait(Y1, _lastY);
        newElement.setFloatTrait(X2, x);
        newElement.setFloatTrait(Y2, y);
        newElement.setFloatTrait(STROKE_WIDTH, width);
        newElement.setTrait(STROKE, color.toLowerCase());
        _svg.appendChild(newElement);
    }

    /**
     * A menu item used to erase all elements on the screen.
     */
    private final MenuItem _eraseMenu = new MenuItem("Erase Canvas", 2, 0) {
        public void run() {
            // Reset the last x and y coordinates.
            _lastX = -1;
            _lastY = -1;

            // Remove all children from the svg node.
            SVGElement line = (SVGElement) _svg.getFirstElementChild();
            while (line != null) {
                _svg.removeChild(line);
                line = (SVGElement) _svg.getFirstElementChild();
            }

            // Indicate that the screen requires re-painting.
            invalidate();
        }
    };

    /**
     * A menu item used to select a line color.
     */
    private final MenuItem _colourMenu = new MenuItem("Change Colour", 0, 0) {
        public void run() {
            final ColorChangeDialog dialog = new ColorChangeDialog(_colorIndex);

            if (dialog.doModal() == Dialog.OK) {
                _colorIndex = dialog.getColorIndex();
                _color = dialog.getColor(_colorIndex);
            }
        }
    };

    /**
     * A menu item used to select a line color.
     */
    private final MenuItem _widthMenu = new MenuItem("Change Width", 1, 0) {
        public void run() {
            final WidthChangeDialog dialog = new WidthChangeDialog(_width);

            if (dialog.doModal() == Dialog.OK) {
                _width = dialog.getLineWidth() * 2;
            }
        }
    };

    /**
     * @see Screen#sublayout
     */
    public void sublayout(final int height, final int width) {
        super.sublayout(height, width);

        // Calculate the title height.
        final Manager manager = getMainManager();
        final XYRect extent = manager.getExtent(); // Scrollable section of the
                                                   // screen.
        _titleHeight = extent.y; // Top of the scrollable section.

        // Set the viewport dimensions for the current orientation.
        _image.setViewportHeight(Display.getHeight() - _titleHeight);
        _image.setViewportWidth(Display.getWidth());
    }

    /**
     * @see Field#paint
     */
    public void paint(final Graphics graphics) {
        super.paint(graphics);

        // Make sure image is non-null.
        if (_image == null) {
            return;
        }

        // Bind target Graphics.
        _scalableGraphics.bindTarget(graphics);

        // Render the svg image/ model.
        _scalableGraphics.render(0, _titleHeight, _image);

        // Release bindings on Graphics.
        _scalableGraphics.releaseTarget();
    }

    /**
     * A dialog window used to change the line color.
     */
    private static class ColorChangeDialog extends Dialog {
        /** An array of pre-defined color names. */
        private static final String[] _colors = { COLOR_BLACK, COLOR_RED,
                COLOR_BLUE, COLOR_GREEN, COLOR_YELLOW };

        /** A field used to choose a color. */
        private final ObjectChoiceField _colorChooser;

        /**
         * Constructor.
         */
        public ColorChangeDialog(final int index) {
            super(Dialog.D_OK_CANCEL, "Choose Colour", Dialog.OK, null,
                    Dialog.GLOBAL_STATUS);
            _colorChooser = new ObjectChoiceField("Colour: ", _colors, 0);
            _colorChooser.setSelectedIndex(index);
            add(_colorChooser);
        }

        /**
         * Retrieve the color selection.
         * 
         * @return The selected line color.
         */
        public int getColorIndex() {
            return _colorChooser.getSelectedIndex();
        }

        /**
         * Retrieve the color selection.
         * 
         * @param index
         *            The color array index for the selected color.
         * @return The selected line color.
         */
        public String getColor(final int index) {
            return _colors[index].toLowerCase();
        }
    }

    /**
     * A dialog window used to change the line width.
     */
    private static class WidthChangeDialog extends Dialog {
        /** A field used to choose a color. */
        private final NumericChoiceField _widthChooser;

        /**
         * Constructor.
         */
        public WidthChangeDialog(final int previousWidth) {
            super(Dialog.D_OK_CANCEL, "Choose Width", Dialog.OK, null,
                    Dialog.GLOBAL_STATUS);
            _widthChooser = new NumericChoiceField("Width: ", 1, 5, 1);
            _widthChooser.setSelectedIndex(previousWidth / 2 - 1);
            add(_widthChooser);
        }

        /**
         * Retrieve the width selection.
         * 
         * @return The selected line width.
         */
        public int getLineWidth() {
            return _widthChooser.getSelectedValue();
        }
    }
}
