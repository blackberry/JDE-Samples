/*
 * ButtonPanel.java
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

import java.util.Vector;

import org.w3c.dom.events.Event;
import org.w3c.dom.svg.SVGElement;

/**
 * A panel containing various SVG buttons
 */
public final class ButtonPanel extends SVGPanel implements
        org.w3c.dom.events.EventListener {
    // Button panel elements
    private SVGElement _checkbox1;
    private SVGElement _checkbox1_check;
    private SVGElement _checkbox1_text;
    private SVGElement _checkbox2;
    private SVGElement _checkbox2_check;
    private SVGElement _checkbox2_text;
    private SVGElement _radiobutton1;
    private SVGElement _radiobutton1_check;
    private SVGElement _radiobutton1_text;
    private SVGElement _radiobutton2;
    private SVGElement _radiobutton2_check;
    private SVGElement _radiobutton2_text;
    private SVGElement _radiobutton3;
    private SVGElement _radiobutton3_check;
    private SVGElement _radiobutton3_text;
    private SVGElement _button1;
    private SVGElement _dialogbox;
    private SVGElement _dialogboxbutton;
    private SVGElement _dialogbox_text1;
    private SVGElement _dialogbox_text2;

    // Stores the Radiobutton states
    private Vector _radioButtonVector;

    // Stores the Checkbox states
    private Vector _checkBoxVector;

    /**
     * Constructs a new ButtonPanel
     * 
     * @param svgFormsScreen
     *            The applications's main screen
     */
    public ButtonPanel(final SVGFormsScreen svgFormsScreen) {
        super(svgFormsScreen);
        this.initializeButtons();
        this.activateButtons();
        this.populateVectors();
        super.setFirstElement(_checkbox1);
        super.setLastElement(_button1);
    }

    /**
     * Initializes the DOM elements
     */
    private void initializeButtons() {
        _checkbox1 = _svgFormsScreen.getElementById("checkbox1");
        _checkbox1_check = _svgFormsScreen.getElementById("check1");
        _checkbox1_text = _svgFormsScreen.getElementById("checkboxtext1");

        _checkbox2 = _svgFormsScreen.getElementById("checkbox2");
        _checkbox2_check = _svgFormsScreen.getElementById("check2");
        _checkbox2_text = _svgFormsScreen.getElementById("checkboxtext2");

        _radiobutton1 = _svgFormsScreen.getElementById("radiobutton1");
        _radiobutton1_check = _svgFormsScreen.getElementById("radiocheck1");
        _radiobutton1_text = _svgFormsScreen.getElementById("radiobuttontext1");

        _radiobutton2 = _svgFormsScreen.getElementById("radiobutton2");
        _radiobutton2_check = _svgFormsScreen.getElementById("radiocheck2");
        _radiobutton2_text = _svgFormsScreen.getElementById("radiobuttontext2");

        _radiobutton3 = _svgFormsScreen.getElementById("radiobutton3");
        _radiobutton3_check = _svgFormsScreen.getElementById("radiocheck3");
        _radiobutton3_text = _svgFormsScreen.getElementById("radiobuttontext3");

        _button1 = _svgFormsScreen.getElementById("button1");

        _dialogbox = _svgFormsScreen.getElementById("dialogbox");
        _dialogboxbutton = _svgFormsScreen.getElementById("dialogboxbutton");
        _dialogbox_text1 = _svgFormsScreen.getElementById("dialogboxtext1");
        _dialogbox_text2 = _svgFormsScreen.getElementById("dialogboxtext2");
    }

    /**
     * Populates checkbox and radiobutton states in the corresponding vectors
     */
    private void populateVectors() {
        _checkBoxVector = new Vector();
        _checkBoxVector.addElement(_checkbox1_check);
        _checkBoxVector.addElement(_checkbox2_check);

        _radioButtonVector = new Vector();
        _radioButtonVector.addElement(_radiobutton1_check);
        _radioButtonVector.addElement(_radiobutton2_check);
        _radioButtonVector.addElement(_radiobutton3_check);
    }

    /**
     * Activates buttons and check boxes
     */
    private void activateButtons() {
        // Activate the buttons and add the check elements in the corresponding
        // vectors(if applicable)
        activateSVGElement(_checkbox1, this);
        activateSVGElement(_checkbox2, this);
        activateSVGElement(_radiobutton1, this);
        activateSVGElement(_radiobutton2, this);
        activateSVGElement(_radiobutton3, this);
        activateSVGElement(_button1, this);
    }

    /**
     * Handles the DOMActivate event
     * 
     * @param evt
     *            The event to be handled
     */
    public void handleEvent(final Event evt) {
        // Handle the DOMFocusIn event.
        if (evt.getType().equals("DOMFocusIn")) {
            // Store the currently focused element
            _currentFocusInElement = (SVGElement) evt.getCurrentTarget();
            setCurrentFocusElement(_currentFocusInElement);

        }
        // Handle the click event
        if (evt.getType().equals("click")) {
            _currentClickedElement = (SVGElement) evt.getCurrentTarget();

            // Handle the "buy now!" button click
            if (_currentClickedElement == _button1) {
                activateSVGElement(_dialogboxbutton, this); // Ativate dialog
                                                            // button
                _svgFormsScreen.dialogboxMode(this, true); // Switch to dialog
                                                           // box mode

                _animator.invokeLater(new Runnable() {
                    public void run() {
                        _svgFormsScreen.setFocus(null);
                        String items = "Items: ";
                        String quantity = "quantity: ";
                        for (int i = 0; i < _checkBoxVector.size(); i++) {
                            final SVGElement checkBox =
                                    (SVGElement) _checkBoxVector.elementAt(i);
                            final String checkTrait =
                                    checkBox.getTrait("display");
                            if (checkTrait.equals("inline")) {
                                String itemName = "";
                                if (checkBox == _checkbox1_check) {
                                    itemName =
                                            _checkbox1_text.getTrait("#text");
                                } else if (checkBox == _checkbox2_check) {
                                    itemName =
                                            _checkbox2_text.getTrait("#text");
                                }
                                items = items + itemName + " ";
                            }
                        }
                        for (int i = 0; i < _radioButtonVector.size(); i++) {
                            final SVGElement radiobuttoncheck =
                                    (SVGElement) _radioButtonVector
                                            .elementAt(i);
                            final String radioCheckTrait =
                                    radiobuttoncheck.getTrait("display");
                            if (radioCheckTrait.equals("inline")) {
                                String quantityInKg = "";
                                if (radiobuttoncheck == _radiobutton1_check) {
                                    quantityInKg =
                                            _radiobutton1_text
                                                    .getTrait("#text");
                                } else if (radiobuttoncheck == _radiobutton2_check) {
                                    quantityInKg =
                                            _radiobutton2_text
                                                    .getTrait("#text");
                                } else if (radiobuttoncheck == _radiobutton3_check) {
                                    quantityInKg =
                                            _radiobutton3_text
                                                    .getTrait("#text");
                                }
                                quantity = quantity + quantityInKg;
                                break;
                            }
                        }

                        _dialogbox.setTrait("display", "inline");
                        _dialogbox_text1.setTrait("#text", items);
                        _dialogbox_text2.setTrait("#text", quantity);
                    }
                });
            }

            // Handle the dialog box button
            else if (_currentClickedElement == _dialogboxbutton) {
                deActivateSVGElement(_dialogboxbutton, this);
                _svgFormsScreen.dialogboxMode(this, false);
                _animator.invokeLater(new Runnable() {
                    public void run() {
                        _dialogbox.setTrait("display", "none");
                    }
                });
            }

            // Handle the checkboxes and radiobutton events
            else {
                _animator.invokeLater(new Runnable() {
                    public void run() {
                        if (_currentClickedElement == _checkbox1) {
                            final String checkBoxState =
                                    _checkbox1_check.getTrait("display");
                            if (checkBoxState.equals("none")) {
                                _checkbox1_check.setTrait("display", "inline");
                            } else if (checkBoxState.equals("inline")) {
                                _checkbox1_check.setTrait("display", "none");
                            }
                        } else if (_currentClickedElement == _checkbox2) {
                            final String checkBoxState =
                                    _checkbox2_check.getTrait("display");
                            if (checkBoxState.equals("none")) {
                                _checkbox2_check.setTrait("display", "inline");
                            } else if (checkBoxState.equals("inline")) {
                                _checkbox2_check.setTrait("display", "none");
                            }
                        } else if (_currentClickedElement == _radiobutton1) {
                            _radiobutton1_check.setTrait("display", "inline");

                            for (int i = 0; i < _radioButtonVector.size(); i++) {
                                final SVGElement radiobutton =
                                        (SVGElement) _radioButtonVector
                                                .elementAt(i);
                                if (!(radiobutton == _radiobutton1_check)) {
                                    radiobutton.setTrait("display", "none");
                                }
                            }
                        } else if (_currentClickedElement == _radiobutton2) {
                            _radiobutton2_check.setTrait("display", "inline");
                            for (int i = 0; i < _radioButtonVector.size(); i++) {
                                final SVGElement radiobutton =
                                        (SVGElement) _radioButtonVector
                                                .elementAt(i);
                                if (!(radiobutton == _radiobutton2_check)) {
                                    radiobutton.setTrait("display", "none");
                                }
                            }
                        } else if (_currentClickedElement == _radiobutton3) {
                            _radiobutton3_check.setTrait("display", "inline");
                            for (int i = 0; i < _radioButtonVector.size(); i++) {
                                final SVGElement radiobutton =
                                        (SVGElement) _radioButtonVector
                                                .elementAt(i);
                                if (!(radiobutton == _radiobutton3_check)) {
                                    radiobutton.setTrait("display", "none");
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}
