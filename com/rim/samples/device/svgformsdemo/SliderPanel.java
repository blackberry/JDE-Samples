/*
 * SliderPanel.java
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

import net.rim.device.api.system.Display;

import org.w3c.dom.events.Event;
import org.w3c.dom.svg.SVGElement;

/**
 * Panel containing sliders and progress bars
 */
class SliderPanel extends SVGPanel implements org.w3c.dom.events.EventListener {
    // Slider panel elements
    private SVGElement _slider1;
    private SVGElement _slider1_control;
    private SVGElement _slider1_bar;

    private SVGElement _slider2;
    private SVGElement _slider2_control;

    private SVGElement _slider3;
    private SVGElement _slider3_control;

    private SVGElement _slider4;
    private SVGElement _slider4_control;

    private SVGElement _colorbox;
    private SVGElement _colorSlider_bar;
    private SVGElement _progressbar_line;
    private SVGElement _progressbar_value;

    // Status of the panel
    private boolean _sliderActivated = false;

    // Holds the reference to the current slider control
    private SVGElement _sliderControl;

    // Holds the position of slider
    private float _sliderPosition = 50;
    private float _slider1Position;
    private float _slider2Position;
    private float _slider3Position;
    private float _slider4Position;

    // Holds the color components as strings
    private String _redComponent = "00", _greenComponent = "00",
            _blueComponent = "00";

    /**
     * Constructs a new SliderPanel
     * 
     * @param svgFormsScreen
     *            The applications's main screen
     */
    SliderPanel(final SVGFormsScreen svgFormsScreen) {
        super(svgFormsScreen);
        this.initializeSliders();
        this.activateSliders();
        super.setFirstElement(_svgFormsScreen.getElementById("slider1"));
        super.setLastElement(_svgFormsScreen.getElementById("slider4"));
    }

    /**
     * Initialize the slider panel elements
     */
    private void initializeSliders() {
        _slider1 = _svgFormsScreen.getElementById("slider1");
        _slider1_control = _svgFormsScreen.getElementById("slider1control");
        _slider1_bar = _svgFormsScreen.getElementById("slider1bar");

        _slider2 = _svgFormsScreen.getElementById("slider2");
        _slider2_control = _svgFormsScreen.getElementById("slider2control");

        _slider3 = _svgFormsScreen.getElementById("slider3");
        _slider3_control = _svgFormsScreen.getElementById("slider3control");

        _slider4 = _svgFormsScreen.getElementById("slider4");
        _slider4_control = _svgFormsScreen.getElementById("slider4control");

        _colorbox = _svgFormsScreen.getElementById("colorbox");
        _colorSlider_bar = _svgFormsScreen.getElementById("slider2bar");

        _progressbar_line = _svgFormsScreen.getElementById("progressbarline");
        _progressbar_value =
                _svgFormsScreen.getElementById("progressbarstatusvalue");
    }

    /**
     * Check if the slider is activated
     * 
     * @return True if the slider is activated, otherwise false
     */
    boolean isSliderActivated() {
        return _sliderActivated;
    }

    /**
     * Activates the sliders
     */
    private void activateSliders() {
        activateSVGElement(_slider1, this);
        activateSVGElement(_slider2, this);
        activateSVGElement(_slider3, this);
        activateSVGElement(_slider4, this);
    }

    /**
     * Handles the DOMActivate event
     * 
     * @param evt
     *            The event to be handled
     */
    public void handleEvent(final Event evt) {
        // Handles the DOMFocusIn event
        if (evt.getType().equals("DOMFocusIn")) {
            // Stores the currently focused element
            _currentFocusInElement = (SVGElement) evt.getCurrentTarget();
            System.out.println("_currentFocusInElement = "
                    + _currentFocusInElement.getId());
            setCurrentFocusElement(_currentFocusInElement);
        }

        // Handle the click event
        else if (evt.getType().equals("click")) {
            _currentClickedElement = (SVGElement) evt.getCurrentTarget();
            System.out.println("_currentClickedElement = "
                    + _currentClickedElement.getId());
            if (isSliderActivated() == false) {
                _sliderActivated = true;
            } else {
                _sliderActivated = false;
            }
        }
    }

    /**
     * @see Manager#navigationMovement(int, int, int, int)
     */
    void sliderMovementNavigation(final int dx, final int dy, final int status,
            final int time) {
        if (_currentFocusInElement == _slider1) {
            _sliderPosition = _slider1_control.getFloatTrait("cx") + dx * 4;
            if (_sliderPosition > _slider1_bar.getFloatTrait("x1")
                    && _sliderPosition <= _slider1_bar.getFloatTrait("x2")) {
                _animator.invokeLater(new Runnable() {
                    public void run() {
                        _slider1_control.setFloatTrait("cx", _sliderPosition);
                        _progressbar_line.setFloatTrait("x2", _sliderPosition);
                        String percentage = "";

                        percentage =
                                percentage
                                        + (int) (_sliderPosition - Display
                                                .getWidth() / 14);
                        _progressbar_value.setTrait("#text", percentage);
                    }
                });
            } else {
                _sliderPosition = _slider1_control.getFloatTrait("cx");
            }
        } else if (_currentFocusInElement == _slider2
                || _currentFocusInElement == _slider3
                || _currentFocusInElement == _slider4) {

            // Increment the componenet values in multiples of 4
            if (_currentFocusInElement == _slider2) {
                _sliderControl = _slider2_control;
                _sliderPosition = _slider2_control.getFloatTrait("cx") + dx * 4;
                if (_sliderPosition > _colorSlider_bar.getFloatTrait("x1")
                        && _sliderPosition <= _colorSlider_bar
                                .getFloatTrait("x2")) {
                    final int colorValue = (int) (_sliderPosition - 300) * 4;

                    // If the colour value < 16 (i.e. a single hex digit) then
                    // append a 0 to the Hex colour value
                    if (colorValue < 16) {
                        _redComponent = "0" + Integer.toHexString(colorValue);
                    } else {
                        _redComponent = Integer.toHexString(colorValue);
                    }
                }
            } else if (_currentFocusInElement == _slider3) {
                _sliderControl = _slider3_control;
                _sliderPosition = _slider3_control.getFloatTrait("cx") + dx * 4;
                if (_sliderPosition >= 300 && _sliderPosition < 360) {
                    final int colorValue = (int) (_sliderPosition - 300) * 4;

                    // If the colour value < 16 (i.e. a single hex digit) then
                    // append a 0 to the Hex colour value
                    if (colorValue < 16) {
                        _greenComponent = "0" + Integer.toHexString(colorValue);
                    } else {
                        _greenComponent = Integer.toHexString(colorValue);
                    }
                }
            } else if (_currentFocusInElement == _slider4) {
                _sliderControl = _slider4_control;
                _sliderPosition = _slider4_control.getFloatTrait("cx") + dx * 4;
                if (_sliderPosition >= 300 && _sliderPosition < 360) {
                    final int colorValue = (int) (_sliderPosition - 300) * 4;

                    // If the colour value < 16 (i.e. a single hex digit) then
                    // append a 0 to the Hex colour value
                    if (colorValue < 16) {
                        _blueComponent = "0" + Integer.toHexString(colorValue);
                    } else {
                        _blueComponent = Integer.toHexString(colorValue);
                    }
                }
            }

            // Set the corresponding Hex value to the box color
            if (_sliderPosition >= 300 && _sliderPosition < 360) {
                _animator.invokeLater(new Runnable() {
                    public void run() {
                        final String rgb =
                                "#" + _redComponent + _greenComponent
                                        + _blueComponent;
                        _sliderControl.setFloatTrait("cx", _sliderPosition);
                        _colorbox.setTrait("fill", rgb);
                    }
                });
            } else {
                _sliderPosition = _sliderControl.getFloatTrait("cx");
            }
        }
    }

    /**
     * TouchEvent processing for sliders
     * 
     * @param x
     *            current x - co-ordinate. position.
     */
    void SliderTouchEvent(final int x) {
        if (_currentFocusInElement == _slider1) {
            final int slider1barX1 = Display.getWidth() / 14;
            if (x > slider1barX1 && x <= slider1barX1 + 100) {
                _slider1Position = x;
                _animator.invokeLater(new Runnable() {
                    public void run() {
                        _slider1_control.setFloatTrait("cx", _slider1Position);
                        _progressbar_line.setFloatTrait("x2", _slider1Position);
                        String percentage = "";
                        percentage =
                                percentage
                                        + (int) (_slider1Position - Display
                                                .getWidth() / 14);
                        _progressbar_value.setTrait("#text", percentage);

                    }
                });
            }
        } else if (_currentFocusInElement == _slider2
                || _currentFocusInElement == _slider3
                || _currentFocusInElement == _slider4) {
            final float miniSliderBarX1 = Display.getWidth() / (float) 1.60;
            if (x >= miniSliderBarX1 && x < miniSliderBarX1 + 60) {
                // Increment the componenet values in multiples of 4
                if (_currentFocusInElement == _slider2) {
                    _slider2Position = x;
                    final int colorValue =
                            (int) (_slider2Position - miniSliderBarX1) * 4;

                    // If the colour value < 16 (i.e. a single hex digit) then
                    // append a 0 to the Hex colour value.
                    if (colorValue < 16) {
                        _redComponent = "0" + Integer.toHexString(colorValue);
                    } else {
                        _redComponent = Integer.toHexString(colorValue);
                    }

                    _animator.invokeLater(new Runnable() {
                        public void run() {
                            final String rgb =
                                    "#" + _redComponent + _greenComponent
                                            + _blueComponent;
                            _slider2_control.setFloatTrait("cx",
                                    _slider2Position);
                            _colorbox.setTrait("fill", rgb);
                        }
                    });

                } else if (_currentFocusInElement == _slider3) {
                    _slider3Position = x;
                    final int colorValue =
                            (int) (_slider3Position - miniSliderBarX1) * 4;

                    // If the colour value < 16 (i.e. a single hex digit) then
                    // append a 0 to the Hex colour value.
                    if (colorValue < 16) {
                        _greenComponent = "0" + Integer.toHexString(colorValue);
                    } else {
                        _greenComponent = Integer.toHexString(colorValue);
                    }

                    _animator.invokeLater(new Runnable() {
                        public void run() {
                            final String rgb =
                                    "#" + _redComponent + _greenComponent
                                            + _blueComponent;
                            _slider3_control.setFloatTrait("cx",
                                    _slider3Position);
                            _colorbox.setTrait("fill", rgb);
                        }
                    });

                } else if (_currentFocusInElement == _slider4) {
                    _slider4Position = x;

                    final int colorValue =
                            (int) (_slider4Position - miniSliderBarX1) * 4;

                    // If the colour value < 16 (i.e. a single hex digit) then
                    // append a 0 to the Hex colour value.
                    if (colorValue < 16) {
                        _blueComponent = "0" + Integer.toHexString(colorValue);
                    } else {
                        _blueComponent = Integer.toHexString(colorValue);
                    }

                    _animator.invokeLater(new Runnable() {
                        public void run() {
                            final String rgb =
                                    "#" + _redComponent + _greenComponent
                                            + _blueComponent;
                            _slider4_control.setFloatTrait("cx",
                                    _slider4Position);
                            _colorbox.setTrait("fill", rgb);
                        }
                    });
                }
            }
        }
    }
}
