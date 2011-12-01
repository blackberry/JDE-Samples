/*
 * SVGTextFieldHelper.java
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

/**
 * Helper class for SVG text fields
 */
public final class SVGTextFieldHelper {
    // The main screen
    private final SVGFormsScreen _svgFormsScreen;

    // The text panel
    private final TextPanel _textPanel;

    // Text field that functions for the SVG text field
    private final SVGTextField _svgTextfield;

    // Manager that hides the SVGTextField
    private final TextBoxManager _textBoxManager;

    /**
     * Creates a new SVGTextFieldHelper object
     * 
     * @param svgFormsScreen
     *            The applications's main screen
     * @param textPanel
     *            The panel that contains the text field
     */
    public SVGTextFieldHelper(final SVGFormsScreen svgFormsScreen,
            final TextPanel textPanel) {
        _svgFormsScreen = svgFormsScreen;
        _textPanel = textPanel;
        _textBoxManager = new TextBoxManager();
        _svgTextfield = new SVGTextField(_textPanel);
    }

    /**
     * Adds a text field to the screen and initializes it
     */
    void addTextField() {
        // Add the text field to the TextBoxManager
        _textBoxManager.add(_svgTextfield);

        // Add the manager to the screen
        _svgFormsScreen.add(_textBoxManager);
        _textBoxManager.setFocus();

        // Set the maximum length of the TextField
        _svgTextfield.setMaxSize(20);

        // Set the cursor position at the end of the text
        final String text = _svgTextfield.getText();
        final int size = text.length();
        _svgTextfield.setCursorPosition(size);
    }

    /**
     * Removes the text field from the screen and sets the focus back to the
     * panel
     */
    void removeTextField() {
        _textBoxManager.delete(_svgTextfield);
        _svgFormsScreen.delete(_textBoxManager);
        _svgFormsScreen.setFocus(_textPanel.getFirstElement());
    }
}
