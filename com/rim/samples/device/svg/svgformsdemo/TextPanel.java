/*
 * SlidersEventHandler.java
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

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Ui;

import org.w3c.dom.events.Event;
import org.w3c.dom.svg.SVGElement;

/**
 * Panel containing a Text box
 */
public final class TextPanel extends SVGPanel implements
        org.w3c.dom.events.EventListener {
    // Textbox panel elements
    private SVGElement _textbox;
    private SVGElement _textbox_cursor;
    private SVGElement _textbox_text;

    // Contains the textfield helper class
    private final SVGTextFieldHelper _svgTextFieldHelper;

    // Contains the textbox contents
    private String _textboxText;

    // Indicates if textbox is active or not
    private boolean _textFieldActive = false;

    /**
     * Constructs a new TextPanel
     * 
     * @param svgFormsScreen
     *            The applications's main screen
     */
    public TextPanel(final SVGFormsScreen svgFormsScreen) {
        super(svgFormsScreen);
        this.initializeTextbox();
        this.activateTextBox();
        super.setFirstElement(_textbox);
        super.setLastElement(_textbox);

        // Textfield helper class is associated with the text panel
        _svgTextFieldHelper = new SVGTextFieldHelper(_svgFormsScreen, this);
    }

    /**
     * Initializes the textbox panel elements
     */
    private void initializeTextbox() {
        _textbox = _svgFormsScreen.getElementById("textbox");
        _textbox_cursor = _svgFormsScreen.getElementById("textboxcursor");
        _textbox_text = _svgFormsScreen.getElementById("textboxtext");
    }

    /**
     * Activates the textbox on a click
     */
    private void activateTextBox() {
        activateSVGElement(_textbox, this);
    }

    /**
     * Checks if the text box is active
     * 
     * @return True if the text box is active, otherwise false
     */
    boolean isTextBoxActive() {
        return _textFieldActive;
    }

    /**
     * Adds the svgTextField that remains in the background
     */
    private void addTextField() {
        _svgTextFieldHelper.addTextField();
        _textFieldActive = true;
    }

    /**
     * Removes the text field
     */
    void removeTextField() {
        _svgTextFieldHelper.removeTextField();
        _svgFormsScreen.setFocus(this._currentClickedElement);
        _textFieldActive = false;
    }

    /**
     * Sets the text of the textbox element
     * 
     * @param text
     *            The text to display in the textbox
     */
    void setText(final String text) {
        _textboxText = text;
        _animator.invokeLater(new Runnable() {
            public void run() {
                if (_textbox_cursor.getTrait("display").equals("none")) {
                    _textbox_cursor.setTrait("display", "inline");
                }
                _textbox_text.setTrait("#text", _textboxText);

                try {
                    // Obtain the font object corresponding to the text element
                    final FontFamily family = FontFamily.forName("BBMillbank");
                    final Font font =
                            family.getFont(Font.PLAIN, 15, Ui.UNITS_px);

                    // Set the X and Y co-ordinates of the cursor rect
                    _textbox_cursor.setFloatTrait("x",
                            (Display.getWidth() / 2 - 83 + font
                                    .getAdvance(_textboxText)));
                    _textbox_cursor.setFloatTrait("y", 144.0f);
                } catch (final ClassNotFoundException cnfe) {
                    System.out.println(cnfe.toString());
                }
            }
        });
    }

    /**
     * Handles the DOMActivate event
     * 
     * @param evt
     *            The event to handle
     */
    public void handleEvent(final Event evt) {
        // Handle the DOMFocusIn event
        if (evt.getType().equals("DOMFocusIn")) {
            // Store the currently focused element
            _currentFocusInElement = (SVGElement) evt.getCurrentTarget();
            setCurrentFocusElement(_currentFocusInElement);
        }
        // Handle the click event
        else if (evt.getType().equals("click")) {
            _currentClickedElement = (SVGElement) evt.getCurrentTarget();
            System.out.println("current clicked element  = "
                    + _currentClickedElement.getId());

            if (_currentClickedElement.getId().equals("textbox")) {
                if (_textFieldActive == false) {
                    addTextField();
                }
            }
        }
    }
}
