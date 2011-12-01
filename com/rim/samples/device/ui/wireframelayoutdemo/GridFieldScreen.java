/*
 * GridFieldScreen.java
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

package com.rim.samples.device.ui.wireframelayoutdemo;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.GridFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * A screen which displays number of buttons in a GridFieldManager in a keypad
 * configuration.
 */
public class GridFieldScreen extends MainScreen {
    private final ButtonField _buttonFieldOne;
    private final ButtonField _buttonFieldTwo;
    private final ButtonField _buttonFieldThree;
    private final ButtonField _buttonFieldFour;
    private final ButtonField _buttonFieldFive;
    private final ButtonField _buttonFieldSix;
    private final ButtonField _buttonFieldSeven;
    private final ButtonField _buttonFieldEight;
    private final ButtonField _buttonFieldNine;
    private final ButtonField _buttonFieldStar;
    private final ButtonField _buttonFieldZero;
    private final ButtonField _buttonFieldPound;

    private final BasicEditField _phoneNumberField;

    /**
     * Creates a new GridFieldScreen object
     */
    public GridFieldScreen() {
        super(NO_VERTICAL_SCROLL);

        setTitle("Grid Field Demo");

        // Define rows and columns
        final int rows = 4;
        final int cols = 3;

        // Instantiate a GridFieldManager with 4 rows and 3 columns
        final GridFieldManager gridFieldManager =
                new GridFieldManager(rows, cols, Field.FIELD_HCENTER);

        // Add a field to the screen to display a phone number
        _phoneNumberField =
                new BasicEditField("Phone number: ", "", 15,
                        Field.NON_FOCUSABLE);
        add(_phoneNumberField);

        add(new SeparatorField());

        // Instantiate button fields
        _buttonFieldOne = new ButtonField("1", ButtonField.NEVER_DIRTY);
        _buttonFieldTwo = new ButtonField("2", ButtonField.NEVER_DIRTY);
        _buttonFieldThree = new ButtonField("3", ButtonField.NEVER_DIRTY);
        _buttonFieldFour = new ButtonField("4", ButtonField.NEVER_DIRTY);
        _buttonFieldFive = new ButtonField("5", ButtonField.NEVER_DIRTY);
        _buttonFieldSix = new ButtonField("6", ButtonField.NEVER_DIRTY);
        _buttonFieldSeven = new ButtonField("7", ButtonField.NEVER_DIRTY);
        _buttonFieldEight = new ButtonField("8", ButtonField.NEVER_DIRTY);
        _buttonFieldNine = new ButtonField("9", ButtonField.NEVER_DIRTY);
        _buttonFieldStar = new ButtonField("*", ButtonField.NEVER_DIRTY);
        _buttonFieldZero = new ButtonField("0", ButtonField.NEVER_DIRTY);
        _buttonFieldPound = new ButtonField("#", ButtonField.NEVER_DIRTY);

        // Add button fields to the GridFieldManager
        gridFieldManager.add(_buttonFieldOne);
        gridFieldManager.add(_buttonFieldTwo);
        gridFieldManager.add(_buttonFieldThree);
        gridFieldManager.add(_buttonFieldFour);
        gridFieldManager.add(_buttonFieldFive);
        gridFieldManager.add(_buttonFieldSix);
        gridFieldManager.add(_buttonFieldSeven);
        gridFieldManager.add(_buttonFieldEight);
        gridFieldManager.add(_buttonFieldNine);
        gridFieldManager.add(_buttonFieldStar);
        gridFieldManager.add(_buttonFieldZero);
        gridFieldManager.add(_buttonFieldPound);

        // Add the GridFieldManager to a VerticalFieldManager
        final VerticalFieldManager vfm =
                new VerticalFieldManager(USE_ALL_WIDTH);
        vfm.add(gridFieldManager);

        // Add the HorizontalFieldManager to the screen
        add(vfm);
    }

    /**
     * @see Screen#keyChar(char, int, int)
     */
    protected boolean keyChar(final char key, final int status, final int time) {
        final int textLength = _phoneNumberField.getText().length();

        if ((key == Characters.BACKSPACE || key == Characters.ESCAPE)
                && textLength > 0) {
            final String oldText = _phoneNumberField.getText();
            final String newText = oldText.substring(0, textLength - 1);
            _phoneNumberField.setText(newText);
            return true;
        }
        return super.keyChar(key, status, time);
    }

    /**
     * @see Screen#navigationClick(int, int)
     */
    protected boolean navigationClick(final int status, final int time) {
        doUpdate();
        return true;
    }

    /**
     * Appends digit to the phone number field text corresponding to the
     * selected keypad button.
     */
    void doUpdate() {
        if (_buttonFieldOne.isFocus()) {
            _phoneNumberField.setText(_phoneNumberField.getText() + "1");
        } else if (_buttonFieldTwo.isFocus()) {
            _phoneNumberField.setText(_phoneNumberField.getText() + "2");
        } else if (_buttonFieldThree.isFocus()) {
            _phoneNumberField.setText(_phoneNumberField.getText() + "3");
        } else if (_buttonFieldFour.isFocus()) {
            _phoneNumberField.setText(_phoneNumberField.getText() + "4");
        } else if (_buttonFieldFive.isFocus()) {
            _phoneNumberField.setText(_phoneNumberField.getText() + "5");
        } else if (_buttonFieldSix.isFocus()) {
            _phoneNumberField.setText(_phoneNumberField.getText() + "6");
        } else if (_buttonFieldSeven.isFocus()) {
            _phoneNumberField.setText(_phoneNumberField.getText() + "7");
        } else if (_buttonFieldEight.isFocus()) {
            _phoneNumberField.setText(_phoneNumberField.getText() + "8");
        } else if (_buttonFieldNine.isFocus()) {
            _phoneNumberField.setText(_phoneNumberField.getText() + "9");
        } else if (_buttonFieldStar.isFocus()) {
            _phoneNumberField.setText(_phoneNumberField.getText() + "*");
        } else if (_buttonFieldZero.isFocus()) {
            _phoneNumberField.setText(_phoneNumberField.getText() + "0");
        } else if (_buttonFieldPound.isFocus()) {
            _phoneNumberField.setText(_phoneNumberField.getText() + "#");
        }
    }
}
