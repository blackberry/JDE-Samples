/*
 * TabEventHandler.java
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

package com.rim.samples.device.svg.svgformsdemo;

import javax.microedition.m2g.SVGAnimator;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.svg.SVGElement;

/**
 * Class to handle SVG elements representing tabs.
 */
public final class TabEventHandler implements org.w3c.dom.events.EventListener {
    // Reference to the main screen
    private final SVGFormsScreen _svgFormsScreen;

    // Reference to the animator
    private final SVGAnimator _animator;

    // Reference to the recently focused and clicked elements
    private SVGElement _currentFocusInElement, _currentClickedElement,
            _lastClickedTabElement;

    // The first and the last tab element of the SVG Panel
    private final SVGElement _lastElement, _firstElement;

    // Tab elements
    private SVGElement _buttonTab;
    private SVGElement _buttonTab_enable;

    private SVGElement _sliderTab;
    private SVGElement _sliderTab_enable;

    private SVGElement _listTab;
    private SVGElement _listTab_enable;

    private SVGElement _textTab;
    private SVGElement _textTab_enable;

    // Reference to various panels.
    private SVGElement _buttonview;
    private SVGElement _sliderview;
    private SVGElement _listview;
    private SVGElement _textview;

    /**
     * Constructs a new TabEventHandler
     * 
     * @param svgFormsScreen
     *            The application's main screen
     */
    public TabEventHandler(final SVGFormsScreen svgFormsScreen) {
        this._svgFormsScreen = svgFormsScreen;
        _animator = _svgFormsScreen.getAnimator();
        this.initializeTabs();
        this.activateTabs();
        _firstElement = _buttonTab;
        _lastElement = _textTab;
        _lastClickedTabElement = _buttonTab;
    }

    /**
     * Initializes the tabs
     */
    private void initializeTabs() {
        _buttonTab = _svgFormsScreen.getElementById("buttontab");
        _buttonTab_enable = _svgFormsScreen.getElementById("buttontabenabled");
        _buttonview = _svgFormsScreen.getElementById("buttonview");

        _sliderTab = _svgFormsScreen.getElementById("slidertab");
        _sliderTab_enable = _svgFormsScreen.getElementById("slidertabenabled");
        _sliderview = _svgFormsScreen.getElementById("sliderview");

        _listTab = _svgFormsScreen.getElementById("listtab");
        _listTab_enable = _svgFormsScreen.getElementById("listtabenabled");
        _listview = _svgFormsScreen.getElementById("listview");

        _textTab = _svgFormsScreen.getElementById("texttab");
        _textTab_enable = _svgFormsScreen.getElementById("texttabenabled");
        _textview = _svgFormsScreen.getElementById("textview");
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
    private void activateSVGElement(final SVGElement element,
            final EventListener eventListener) {
        element.addEventListener("click", eventListener, false);
        element.addEventListener("DOMFocusIn", eventListener, false);
    }

    /**
     * Activates the tab elements
     */
    private void activateTabs() {
        activateSVGElement(_buttonTab, this);
        activateSVGElement(_sliderTab, this);
        activateSVGElement(_listTab, this);
        activateSVGElement(_textTab, this);
    }

    /**
     * Check if the currently focused tab element is last
     * 
     * @return True if the currently focused tab element is last, otherwise
     *         false
     */
    boolean isLastTabElement() {
        if (_currentFocusInElement == _lastElement) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the first tab element
     * 
     * @return The first tab element
     */
    SVGElement getFirstTabElement() {
        return _firstElement;
    }

    /**
     * Updates the tab view
     * 
     * @param lastElement
     *            The most recently clicked tab element
     */
    private void updateTabView(final SVGElement lastElement) {
        if (lastElement == _buttonTab) {
            _buttonTab_enable.setTrait("visibility", "hidden");
            _buttonview.setTrait("display", "none");
        } else if (lastElement == _sliderTab) {
            _sliderTab_enable.setTrait("visibility", "hidden");
            _sliderview.setTrait("display", "none");
        } else if (lastElement == _listTab) {
            _listTab_enable.setTrait("visibility", "hidden");
            _listview.setTrait("display", "none");
        } else if (lastElement == _textTab) {
            _textTab_enable.setTrait("visibility", "hidden");
            _textview.setTrait("display", "none");
        }
    }

    /**
     * Handles the DOM event.
     * 
     * @param evt
     *            The event to be handled
     */
    public void handleEvent(final Event evt) {
        if (evt.getType().equals("DOMFocusIn")) {
            _currentFocusInElement = (SVGElement) evt.getCurrentTarget();
        }
        if (evt.getType().equals("click")) {
            _currentClickedElement = (SVGElement) evt.getCurrentTarget();
            if (_currentClickedElement == _lastClickedTabElement) {
                return;
            }

            _animator.invokeLater(new Runnable() {
                public void run() {
                    if (_currentClickedElement == _buttonTab) {
                        _svgFormsScreen.setView(SVGFormsScreen.BUTTON);
                        _buttonview.setTrait("display", "inline");
                        updateTabView(_lastClickedTabElement);
                        _buttonTab_enable.setTrait("visibility", "visible");
                    } else if (_currentClickedElement == _sliderTab) {
                        _svgFormsScreen.setView(SVGFormsScreen.SLIDERS);
                        _sliderview.setTrait("display", "inline");
                        updateTabView(_lastClickedTabElement);
                        _sliderTab_enable.setTrait("visibility", "visible");
                    } else if (_currentClickedElement == _listTab) {
                        _svgFormsScreen.setView(SVGFormsScreen.LISTS);
                        _listview.setTrait("display", "inline");
                        updateTabView(_lastClickedTabElement);
                        _listTab_enable.setTrait("visibility", "visible");
                    } else if (_currentClickedElement == _textTab) {
                        _svgFormsScreen.setView(SVGFormsScreen.TEXT);
                        _textview.setTrait("display", "inline");
                        updateTabView(_lastClickedTabElement);
                        _textTab_enable.setTrait("visibility", "visible");
                    }
                    _lastClickedTabElement = _currentClickedElement;
                }
            });
        }
    }
}
