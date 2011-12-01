/*
 * SVGPanel.java
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

package com.rim.samples.device.svgformsdemo;

import javax.microedition.m2g.SVGAnimator;

import org.w3c.dom.events.EventListener;
import org.w3c.dom.svg.SVGElement;

/**
 * This class represents an SVG panel
 */
class SVGPanel {
    // Reference to the main screen
    SVGFormsScreen _svgFormsScreen;

    // Reference to the animator
    SVGAnimator _animator;

    SVGElement _currentFocusInElement, _currentFocusOutElement,
            _currentClickedElement;

    // The currently focused element
    private SVGElement _currentFocusedElement;

    // The first and the last element of the SVG Panel
    private SVGElement _lastElement, _firstElement;

    // The last event that occurred in the panel
    String _currentEvent = "";

    /**
     * Constructs a new SVGPanel
     * 
     * @param screen
     *            The applications's main screen
     */
    SVGPanel(final SVGFormsScreen screen) {
        _svgFormsScreen = screen;
        _animator = screen.getAnimator();
    }

    /**
     * Sets the currently focused element
     * 
     * @param svgElement
     *            The SVGElement for which to set the focus
     */
    void setCurrentFocusElement(final SVGElement svgElement) {
        this._currentFocusedElement = svgElement;
    }

    /**
     * Returns the currently focused element
     * 
     * @return The currently focused element
     */
    SVGElement getCurrentFocusElement() {
        return this._currentFocusedElement;
    }

    /**
     * Indicates whether the currently focused element is the first element in
     * the panel
     * 
     * @return True if the currently focused element is the first element in the
     *         panel, otherwise false
     */
    boolean isFirstPanelElement() {
        if (this._currentFocusedElement == this._firstElement) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the first focusable element in the panel
     * 
     * @param svgElement
     *            The element to be the first to receive focus
     */
    void setFirstElement(final SVGElement svgElement) {
        _firstElement = svgElement;
    }

    /**
     * Returns the first focusable element of the panel
     * 
     * @return SVGElement The first focusable element of the panel
     */
    SVGElement getFirstElement() {
        return _firstElement;
    }

    /**
     * Sets the last focusable element in the panel
     * 
     * @param svgElement
     *            The SVGElement to be the last to receive focus
     */
    void setLastElement(final SVGElement svgElement) {
        _lastElement = svgElement;
    }

    /**
     * Returns the last focusable element of the panel
     * 
     * @return SVGElement The last focusable element of the panel
     */
    SVGElement getLastElement() {
        return _lastElement;
    }

    /**
     * Activates the given svg element by registering an event listener for
     * click and focus events
     * 
     * @param element
     *            The SVGElement to activate
     * @param eventListener
     *            The EventListener implementation that handles events for the
     *            given element
     */
    void activateSVGElement(final SVGElement element,
            final EventListener eventListener) {
        element.addEventListener("click", eventListener, false);
        element.addEventListener("DOMFocusIn", eventListener, false);
        element.addEventListener("DOMFocusOut", eventListener, false);
    }

    /**
     * Deactivates the given svg element by de-registering it's EventListener
     * 
     * @param element
     *            The SVGElement to de-activate
     * @param eventListener
     *            The EventListener implementation that handles events for the
     *            given element
     */
    void deActivateSVGElement(final SVGElement element,
            final EventListener eventListener) {
        element.removeEventListener("click", eventListener, false);
        element.removeEventListener("DOMFocusIn", eventListener, false);
        element.removeEventListener("DOMFocusOut", eventListener, false);
    }
}
