/**
 * OptionsProviderDemo.java
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

package com.rim.samples.device.blackberry.options;

import net.rim.blackberry.api.options.OptionsManager;
import net.rim.blackberry.api.options.OptionsProvider;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.util.Persistable;

/**
 * A simple library class to demonstrate the use of the options facilities
 */
public final class OptionsProviderDemo implements OptionsProvider {
    private ObjectChoiceField _objectChoiceField;
    private CheckboxField _checkboxField;
    private EditField _editField;
    private final OptionsDemoData _data;
    private final String _title;
    private final int MAX_CHARS = 100;

    private static OptionsProviderDemo _instance;

    /**
     * Creates a new OptionsProviderDemo object
     * 
     * @param title
     *            Title for the options screen
     */
    private OptionsProviderDemo(final String title) {
        _title = title;
        _data = OptionsDemoData.load();

    }

    /**
     * Only allow one instance of this class
     */
    public static OptionsProviderDemo getInstance() {
        if (_instance == null) {
            _instance = new OptionsProviderDemo("Options Demo");
        }

        return _instance;
    }

    /**
     * On start-up, create the instance and register it
     */
    public static void libMain(final String[] args) {
        // Runs on startup - create the instance and register it
        OptionsManager.registerOptionsProvider(getInstance());
    }

    /**
     * Get the title for the option screen
     * 
     * @see net.rim.blackberry.api.options.OptionsProvider#getTitle()
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Add our fields to the screen
     * 
     * @see net.rim.blackberry.api.options.OptionsProvider#populateMainScreen(MainScreen)
     * 
     */
    public void populateMainScreen(final MainScreen screen) {
        final int index = _data.getSelected();
        final boolean checked = _data.getChecked();
        final String oldText = _data.getOldText();

        final XYEdges xyEdges = new XYEdges(2, 2, 2, 2);

        final FieldSet fieldSetOne =
                new FieldSet("Field Set 1", BorderFactory
                        .createBevelBorder(xyEdges), BorderFactory
                        .createSimpleBorder(xyEdges), Border.STYLE_SOLID);
        _objectChoiceField =
                new ObjectChoiceField("Choices: ", new String[] { "RIM",
                        "Options", "Demo" }, index);
        _checkboxField =
                new CheckboxField("Checkbox: ", checked, Field.FIELD_TRAILING
                        | Field.USE_ALL_WIDTH);
        fieldSetOne.add(_objectChoiceField);
        fieldSetOne.add(_checkboxField);
        fieldSetOne.setMargin(2, 5, 5, 5);
        fieldSetOne.setBackground(BackgroundFactory
                .createSolidBackground(0x00FFFFFF));

        final FieldSet fieldSetTwo =
                new FieldSet("Field Set 2", BorderFactory
                        .createBevelBorder(xyEdges), BorderFactory
                        .createRoundedBorder(xyEdges), Border.STYLE_SOLID);
        _editField =
                new EditField("", oldText, MAX_CHARS, Field.EDITABLE
                        | TextField.NO_NEWLINE);
        _editField.setBackground(BackgroundFactory
                .createSolidBackground(0xf6f6f6));
        _editField.setBorder(BorderFactory.createRoundedBorder(new XYEdges(4,
                4, 4, 4), 0xc6c6c6, Border.STYLE_SOLID));
        fieldSetTwo.add(new LabelField("Input: "));
        fieldSetTwo.add(_editField);
        fieldSetTwo
                .add(new RichTextField(
                        "This is the OptionsProviderDemo implemented using the FieldSet class"));
        fieldSetTwo.setMargin(2, 5, 5, 5);
        fieldSetTwo.setBackground(BackgroundFactory
                .createSolidBackground(0x00FFFFFF));

        screen.add(fieldSetOne);
        screen.add(fieldSetTwo);
    }

    /**
     * Save our data
     * 
     * @see net.rim.blackberry.api.options.OptionsProvider#save()
     */
    public void save() {
        _data.setSelected(_objectChoiceField.getSelectedIndex());
        _data.setChecked(_checkboxField.getChecked());
        _data.setOldText(_editField.getText());
        _data.commit();
    }

    /**
     * Retrieve the data. Used by other applications to access the options data
     * for this provider.
     */
    public OptionsDemoData getData() {
        return _data;
    }

    /**
     * Assists OptionsProviderDemo in loading and saving the data to/from the
     * fields
     */
    public static final class OptionsDemoData implements Persistable {
        private static final long ID = 0x6af0b5eb44dc5164L; // "net.rim.device.bb.samples.options.OptionsDemoData"
        private int _selectedOption;
        private boolean _checkedBox;
        private String _textEntered;

        /**
         * Retreives the saved text from the AutoTextEditField
         * 
         * @return The text that was saved in the AutoTextEditField
         */
        public String getOldText() {
            return _textEntered;
        }

        /**
         * Sets the text to save from the AutoTextEditField to be saved
         * 
         * @param text
         *            The text that was entered in the AutoTextEditField
         */
        public void setOldText(final String text) {
            _textEntered = text;
        }

        /**
         * Retreives the saved state of the CheckboxField
         * 
         * @return <code>true</code> if the Checkbox was checked,
         *         <code>false</code> if the Checkbox was not checked
         */
        public boolean getChecked() {
            return _checkedBox;
        }

        /**
         * Sets the state of the CheckboxField
         * 
         * @param checked
         *            <code>true</code> if the CheckboxField was checked,
         *            <code>false</code> if the CheckboxField was not checked
         */
        public void setChecked(final boolean checked) {
            _checkedBox = checked;
        }

        /**
         * Retreives the saved choice index of the ObjectChoiceField
         * 
         * @return The saved index choice of the ObjectChoiceField
         */
        public int getSelected() {
            return _selectedOption;
        }

        /**
         * Sets the user's ObjectChoiceField choice to save
         * 
         * @param index
         *            The index of the choice the user made
         */
        public void setSelected(final int index) {
            _selectedOption = index;
        }

        /**
         * Commits all the data that was saved
         */
        public void commit() {
            PersistentObject.commit(this);
        }

        /**
         * Loads the data that was saved to the fields
         * 
         * @return An OptionsDemoData instance with all the saved field vales.
         *         If there is no data that has been saved, it returns an empty
         *         instance
         */
        private static OptionsDemoData load() {
            final PersistentObject persist =
                    PersistentStore.getPersistentObject(OptionsDemoData.ID);

            synchronized (persist) {
                if (persist.getContents() == null) {
                    persist.setContents(new OptionsDemoData());
                    persist.commit();
                }
            }

            return (OptionsDemoData) persist.getContents();
        }
    }
}
