/*
 * SpellCheckDemoScreen.java
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

package com.rim.samples.device.spellcheckdemo;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * The MainScreen class for our application.
 */
final class SpellCheckDemoScreen extends MainScreen {
    private final TestField _testField;
    private EditField _correction;
    private PopupScreen _popUp;
    private final SpellCheckDemo _app;

    /**
     * Constructor
     * 
     * @param app
     *            Reference to the SpellCheckDemo UiApplication.
     */
    SpellCheckDemoScreen(final SpellCheckDemo app) {
        _app = app;

        // Add UI components to the screen.
        setTitle(new LabelField("Spell Check Demo", DrawStyle.ELLIPSIS
                | Field.USE_ALL_WIDTH));
        final RichTextField infoField =
                new RichTextField(
                        "Type a misspelled word into the test field (eg. blackbery).  Select menu items to perform spell check operations.",
                        Field.NON_FOCUSABLE);
        add(infoField);
        final SeparatorField separator = new SeparatorField();
        add(separator);
        _testField = new TestField("Test Field: ", "");
        add(_testField);
    }

    /**
     * Prevent the save dialog from being displayed.
     * 
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        return true;
    }

    // Menu item classes
    // ------------------------------------------------------------------------------------------------

    /**
     * Menu item to invoke the spellCheck() method of the SpellCheckDemo
     * application, passing in our test field as the field to be spell checked.
     */
    private final MenuItem _spellCheckItem = new MenuItem("Spell check", 0, 0) {
        public void run() {
            if (_testField.getText().length() == 0) {
                Dialog.alert("Test field cannot be empty");
            } else {
                _app.spellCheck(_testField);
            }
        }
    };

    /**
     * The run() method of this menu item calls the learnWord() method of the
     * SpellCheckDemo application, passing in the word specified in the test
     * field.
     */
    private final MenuItem _learnWordItem = new MenuItem("Learn word", 0, 0) {
        public void run() {
            if (_testField.getText().length() == 0) {
                Dialog.alert("Test field cannot be empty");
            } else {
                _app.learnWord(_testField.getText());
            }
        }
    };

    /**
     * This menu item displays a PopupScreen containing an EditField in which to
     * enter a correction for the word specified in the test field.
     */
    private final MenuItem _learnCorrectionItem = new MenuItem(
            "Learn correction", 0, 0) {
        public void run() {
            if (_testField.getText().length() == 0) {
                Dialog.alert("Test field cannot be empty");
            } else {
                final VerticalFieldManager vfm = new VerticalFieldManager();
                _popUp = new PopupScreen(vfm);
                final LabelField popUpLabel =
                        new LabelField("Correction for " + _testField.getText()
                                + ":");
                _correction = new EditField();
                _popUp.add(popUpLabel);
                _popUp.add(_correction);
                final HorizontalFieldManager hfm =
                        new HorizontalFieldManager(Field.FIELD_HCENTER);
                hfm.add(new OkButton());
                hfm.add(new CancelButton());
                _popUp.add(hfm);
                _app.pushScreen(_popUp);
            }
        }
    };

    /**
     * This inner class represents the OK button in our 'Learn correction'
     * PopupScreen.
     * 
     */
    final class OkButton extends ButtonField {
        public OkButton() {
            super("OK", ButtonField.CONSUME_CLICK);
        }

        protected void fieldChangeNotify(final int context) {
            if ((context & FieldChangeListener.PROGRAMMATIC) == 0) {
                if (_correction.getText().length() == 0) {
                    Dialog.alert("Correction field cannot be empty");
                    _correction.setFocus();
                } else {
                    _app.learnCorrection(_testField.getText(), _correction
                            .getText());
                    _popUp.close();
                }
            }
        }
    }

    /**
     * This inner class simply closes our 'Learn correction' PopupScreen.
     */
    final class CancelButton extends ButtonField {
        public CancelButton() {
            super("Cancel", ButtonField.CONSUME_CLICK);
        }

        protected void fieldChangeNotify(final int context) {
            if ((context & FieldChangeListener.PROGRAMMATIC) == 0) {
                _popUp.close();
            }
        }
    }

    /**
     * We are extending the EditField class in order to provide our customized
     * context menu.
     */
    final class TestField extends EditField {
        public TestField(final String label, final String initialValue) {
            super(label, initialValue);
        }

        /**
         * @see net.rim.device.api.ui.Field#makeContextMenu(ContextMenu
         *      contextMenu)
         */
        public void makeContextMenu(final ContextMenu contextMenu) {
            contextMenu.addItem(_spellCheckItem);
            contextMenu.addItem(_learnWordItem);
            contextMenu.addItem(_learnCorrectionItem);
        }

        /**
         * @see net.rim.device.api.ui.Field#getContextMenu()
         */
        public ContextMenu getContextMenu() {
            final ContextMenu contextMenu = ContextMenu.getInstance();
            contextMenu.setTarget(this);
            makeContextMenu(contextMenu);
            return contextMenu;
        }
    }
}
