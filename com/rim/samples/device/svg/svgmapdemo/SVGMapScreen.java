/*
 * SVGMapScreen.java
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

package com.rim.samples.device.svg.svgmapdemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.microedition.m2g.SVGAnimator;
import javax.microedition.m2g.SVGImage;
import javax.microedition.m2g.ScalableImage;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.MainScreen;

import org.w3c.dom.Document;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * This is a custom screen where an interactive svg map would be used in
 * conjuction with cldc checkboxes to interact with the svg model.
 */
public final class SVGMapScreen extends MainScreen implements
        FieldChangeListener, EventListener {
    // Actual SVG map width and height
    private static final int SVG_WIDTH = 1024;
    private static final int SVG_HEIGHT = 768;

    // Display/Map width and height
    private static final int MAP_DISPLAY_WIDTH = 480;
    private static final int MAP_DISPLAY_HEIGHT = 240;

    // Desired scroll movement
    private static final int SCROLL_CHANGE = 15;

    // For touch screen
    private static final int SCROLL_FACTOR = 3;

    // SVGImage instance to store the parsed SVG data.
    private SVGImage _image;

    // SVGAnimator to obtain an SVGField.
    private SVGAnimator _animator;

    // Document to hold SVGImage contents.
    private Document _document;

    // Field to hold the animated svg context.
    private Field _svgField;

    // Manager to lay out the SVG Map item.
    private SVGMapManager _mapManager;

    // HashTable to store the checkboxes to their svg elements.
    private Hashtable _checkboxes;

    // Manager to lay out checkboxes.
    private FlowFieldManager _checkboxManager;

    // The svg root element.
    private SVGSVGElement _svg;

    // the svg viewbox.
    private SVGRect _svgViewBox;
    private SVGElement _viewportElement;

    // Current display position of the SVG map.
    private int _positionX = 0;
    private int _positionY = 0;

    /**
     * Constructor.
     */
    public SVGMapScreen() {
        setTitle("SVG Map Demo");

        try {
            // Obtains an input stream to the SVG file URL.
            final InputStream is =
                    getClass().getResourceAsStream("/sample.svg");

            // Loads the SVG image using the input stream connection.
            _image = (SVGImage) ScalableImage.createImage(is, null);
            // Obtain the images document.
            _document = _image.getDocument();
            // Create an interactive SVG animator that hosts SVG field.
            _animator =
                    SVGAnimator.createAnimator(_image,
                            "net.rim.device.api.ui.Field");

            // Initialize our screens user interface.
            initializeUI();

            // There may be timed interactive content here so start the
            // animator.
            _animator.play();
        } catch (final IOException ex) {
            System.exit(1);
        }
    }

    /**
     * @see Screen#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(final TouchEvent message) {
        final TouchGesture touchGesture = message.getGesture();
        if (touchGesture != null) {
            if (_mapManager.isPanning()) {
                // If the user has performed a swipe gesture within the map area
                // we will move the map accordingly.
                if (touchGesture.getEvent() == TouchGesture.SWIPE
                        && message.getY(1) < 240) {
                    int horizontal = 0;
                    int vertical = 0;

                    // Retrieve the swipe magnitude so we know how
                    // far to move the map.
                    final int magnitude = touchGesture.getSwipeMagnitude();

                    // Move the map in the direction of the swipe.
                    switch (touchGesture.getSwipeDirection()) {
                    case TouchGesture.SWIPE_NORTH:
                        vertical = magnitude / SCROLL_FACTOR;
                        // vertical = SCROLL_CHANGE;
                        break;
                    case TouchGesture.SWIPE_SOUTH:
                        vertical = -(magnitude / SCROLL_FACTOR);
                        // vertical = -(SCROLL_CHANGE * SCROLL_FACTOR);
                        break;
                    case TouchGesture.SWIPE_EAST:
                        horizontal = -(magnitude / SCROLL_FACTOR);
                        // horizontal = -(SCROLL_CHANGE);
                        break;
                    case TouchGesture.SWIPE_WEST:
                        horizontal = magnitude / SCROLL_FACTOR;
                        // horizontal = -SCROLL_CHANGE;
                        break;
                    }
                    update(horizontal, vertical);
                }
            }
            // Performing a double tap gesture on the map will toggle panning
            // mode
            if (touchGesture.getEvent() == TouchGesture.DOUBLE_TAP
                    && message.getY(1) < 240) {
                togglePanMode();
            }
        }

        return super.touchEvent(message);
    }

    /**
     * Initialize the SVG Map and the remaining screen components.
     */
    private void initializeUI() {
        // Initialize a FlowFieldManager to have the checkboxes layed out.
        _checkboxManager = new FlowFieldManager(Manager.HORIZONTAL_SCROLL);

        // Add the SVG Map to a custom manager to handle its behaviour as a
        // custom
        // togglable field.
        _mapManager = new SVGMapManager();
        add(_mapManager);
        _svgField = (Field) _animator.getTargetComponent();
        _mapManager.add(_svgField);

        // Initialize a hashtable for our check boxes.
        _checkboxes = new Hashtable(3);
        // Populates the hashtable, associates listeners with the checkbox
        final CheckboxField roadsCheckBox = new CheckboxField("Roads  ", true);
        final SVGElement roadsGroup =
                (SVGElement) _document.getElementById("roads");
        _checkboxes.put(roadsCheckBox, roadsGroup);

        final CheckboxField railsCheckBox =
                new CheckboxField("Railways  ", true);
        final SVGElement railsGroup =
                (SVGElement) _document.getElementById("railways");
        _checkboxes.put(railsCheckBox, railsGroup);

        final CheckboxField interestsCheckBox =
                new CheckboxField("Points of interest ", true);
        final SVGElement interestsGroup =
                (SVGElement) _document.getElementById("interests");
        _checkboxes.put(interestsCheckBox, interestsGroup);

        final CheckboxField restaurantCheckBox =
                new CheckboxField("Restaurants ", true);
        final SVGElement restaurantGroup =
                (SVGElement) _document.getElementById("restaurants");
        _checkboxes.put(restaurantCheckBox, restaurantGroup);

        // Set this class as the handler of the change listeners.
        roadsCheckBox.setChangeListener(this);
        railsCheckBox.setChangeListener(this);
        restaurantCheckBox.setChangeListener(this);
        interestsCheckBox.setChangeListener(this);

        // Adds the checkboxes to the layout manager.
        _checkboxManager.add(roadsCheckBox);
        _checkboxManager.add(railsCheckBox);
        _checkboxManager.add(restaurantCheckBox);
        _checkboxManager.add(interestsCheckBox);

        // Adds the layout manager to the screen.
        add(_checkboxManager);

        // Obtain the root element and the view box settings.
        _svg = (SVGSVGElement) _document.getDocumentElement();
        _svgViewBox = _svg.getRectTrait("viewBox");

        // Get the border element.
        _viewportElement = (SVGElement) _document.getElementById("viewport");
        _viewportElement.addEventListener("DOMActivate", this, false);
    }

    /**
     * Toggles the pan mode.
     */
    private void togglePanMode() {
        _mapManager.togglePanning();

        if (_mapManager.isPanning()) {
            _animator.invokeLater(new Runnable() {
                public void run() {
                    _viewportElement.setTrait("stroke-width", "12");
                    _viewportElement.setTrait("stroke", "red");
                }
            });
        } else {
            _animator.invokeLater(new Runnable() {
                public void run() {
                    _viewportElement.setTrait("stroke-width", " 7");
                    _viewportElement.setTrait("stroke", "black");
                }
            });
        }
    }

    /**
     * Handles the DOMActivate event. Locks the control to the SVG element if
     * SVG map field is selected for panning.
     * <p>
     * 
     * @param evt
     *            rg.w3c.dom.events.Event.
     */
    public void handleEvent(final Event evt) {
        if (evt.getCurrentTarget() == _viewportElement) {
            togglePanMode();
        }
    }

    /**
     * Updates current size of the Rectangle and position and value of text
     * fields.
     * <p>
     * 
     * @param horizontal
     *            - viewport and border width update.
     * @param vertical
     *            - viewport and border height update.
     */
    private void update(final int horizontal, final int vertical) {
        // Calculate the new position of the map.
        _positionX += horizontal;
        _positionY += vertical;

        // Update the new position of the map only if new panning
        // position is within the SVG map space.
        if (_positionX <= 0) {
            _positionX = 0;
        }
        if (_positionX >= SVG_WIDTH - MAP_DISPLAY_WIDTH) {
            _positionX = SVG_WIDTH - MAP_DISPLAY_WIDTH;
        }

        if (_positionY <= 0) {
            _positionY = 0;
        }
        if (_positionY >= SVG_HEIGHT - MAP_DISPLAY_HEIGHT) {
            _positionY = SVG_HEIGHT - MAP_DISPLAY_HEIGHT;
        }

        // Update the viewbox transformation and border.
        _animator.invokeLater(new Runnable() {
            public void run() {
                // Update the viewbox co-ordinates.
                _svgViewBox.setX(_positionX);
                _svgViewBox.setY(_positionY);
                _svgViewBox.setWidth(MAP_DISPLAY_WIDTH);
                _svgViewBox.setHeight(MAP_DISPLAY_HEIGHT);
                _svg.setRectTrait("viewBox", _svgViewBox);

                // Update the viewport
                _viewportElement.setFloatTrait("x", _positionX);
                _viewportElement.setFloatTrait("y", _positionY);
            }
        });
    }

    /**
     * @see Screen#navigationMovement(int, int, int, int)
     */
    protected boolean navigationMovement(final int dx, final int dy,
            final int status, final int time) {
        if (_mapManager.isPanning()) {
            int horizontal = 0;
            int vertical = 0;

            if (dx < 0) {
                horizontal = -SCROLL_CHANGE;
            } else if (dx > 0) {
                horizontal = SCROLL_CHANGE;
            }
            if (dy < 0) {
                vertical = -SCROLL_CHANGE;
            } else if (dy > 0) {
                vertical = SCROLL_CHANGE;
            }

            update(horizontal, vertical);

            return true;
        }
        return super.navigationMovement(dx, dy, status, time);
    }

    /**
     * @see Screen#onMenu (int)
     */
    public boolean onMenu(final int instance) {
        if (instance == Menu.INSTANCE_CONTEXT) {
            return true;
        } else {
            return super.onMenu(instance);
        }
    }

    /**
     * @see FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        final CheckboxField cbf = (CheckboxField) field;
        final SVGElement svgElement = (SVGElement) _checkboxes.get(cbf);

        // If the checkbox is checked change svg display trait.
        if (cbf.getChecked() == true) {
            _animator.invokeLater(new Runnable() {
                public void run() {
                    svgElement.setTrait("display", "inline");
                }
            });
        } else {
            _animator.invokeLater(new Runnable() {
                public void run() {
                    svgElement.setTrait("display", "none");
                }
            });
        }
    }

    /**
     * Override this method to prevent save dialog from being displayed.
     * 
     * @see net.rim.device.api.ui.Screen#onClose()
     */
    public boolean onClose() {
        close();
        return true;
    }
}
