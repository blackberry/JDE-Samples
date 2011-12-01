/*
 * SpinnerDemoScreen.java
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

package com.rim.samples.device.ui.spinnerdemo;

import java.util.Calendar;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.TextSpinBoxField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.picker.DateTimePicker;

/**
 * MainScreen class for the Date Time Picker Spinner Demo application
 */
public class SpinnerDemoScreen extends MainScreen {
    private static final int DEFAULT = 1;
    private static final int DATE = 2;
    private static final int TIME = 3;
    private static final int LONG = 4;
    private static final int EXPIRY = 5;
    private static final int SPINBOX = 6;

    private final BasicEditField _editFieldDefault;
    private final BasicEditField _editFieldDate;
    private final BasicEditField _editFieldTime;
    private final BasicEditField _editFieldLong;
    private final BasicEditField _editFieldExpiry;
    private final BasicEditField _editFieldSpinbox;

    private final SimpleDateFormat _defaultDateFormat = new SimpleDateFormat(
            SimpleDateFormat.DATETIME_DEFAULT | SimpleDateFormat.TIME_DEFAULT);
    private final SimpleDateFormat _dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd");
    private final SimpleDateFormat _timeFormat = new SimpleDateFormat(
            "hh:mm:ss aa");
    private final SimpleDateFormat _longDateFormat = new SimpleDateFormat(
            SimpleDateFormat.DATE_LONG | SimpleDateFormat.TIME_DEFAULT);
    private final SimpleDateFormat _expiryDateFormat = new SimpleDateFormat(
            "MM/yyyy");

    private Calendar _defaultCal;
    private Calendar _dateCal;
    private Calendar _timeCal;
    private Calendar _longDateCal;
    private Calendar _expiryDateCal;

    private CustomSpinnerPopup _customSpinnerPopup;

    /**
     * Creates a new SpinnerDemoScreen object
     */
    public SpinnerDemoScreen() {
        setTitle("Spinner Demo");

        // Initialize a VerticalFieldManager
        final VerticalFieldManager fieldManager = new VerticalFieldManager();

        // Add a typical date field to the VerticalFieldManager
        fieldManager.add(new LabelField("Date Field\n", Field.FIELD_HCENTER));
        final DateField dateField =
                new DateField("Date/time: ", System.currentTimeMillis(),
                        DateField.DATE_TIME);
        fieldManager.add(dateField);

        // Add UI elements to the VerticalFieldManager, including fields
        // which will allow a user to make selections from date/time and
        // custom spinners.
        fieldManager.add(new SeparatorField());

        fieldManager.add(new LabelField("Date Time Picker\n",
                Field.FIELD_HCENTER));

        _editFieldDefault = new BasicEditField("Default: ", "Click to select");
        fieldManager.add(_editFieldDefault);

        _editFieldDate = new BasicEditField("Date only: ", "Click to select");
        fieldManager.add(_editFieldDate);

        _editFieldTime = new BasicEditField("Time only: ", "Click to select");
        fieldManager.add(_editFieldTime);

        _editFieldLong = new BasicEditField("Long date: ", "Click to select");
        fieldManager.add(_editFieldLong);

        _editFieldExpiry =
                new BasicEditField("Expiry date: ", "Click to select");
        fieldManager.add(_editFieldExpiry);

        fieldManager.add(new SeparatorField());

        fieldManager.add(new LabelField("Text Spin Box Field\n",
                Field.FIELD_HCENTER));

        _editFieldSpinbox = new BasicEditField("City: ", "Click to select");
        fieldManager.add(_editFieldSpinbox);

        // Add the VerticalFieldManager to the screen
        add(fieldManager);
    }

    /**
     * Displays a spinner dialog for a given type
     * 
     * @param type
     *            Type of spinner to display
     */
    void showSpinnerDialog(final int type) {
        switch (type) {
        case DEFAULT:
            final DateTimePicker datePicker =
                    DateTimePicker.createInstance(_defaultCal);
            if (datePicker.doModal()) {
                final StringBuffer dateStr = new StringBuffer();
                _defaultCal = datePicker.getDateTime();
                _defaultDateFormat.format(_defaultCal, dateStr, null);
                _editFieldDefault.setText(dateStr.toString());
            }
            break;
        case DATE:
            final DateTimePicker datePickerDate =
                    DateTimePicker.createInstance(_dateCal, "yyyy-MM-dd", null);
            if (datePickerDate.doModal()) {
                final StringBuffer dateStrDate = new StringBuffer();
                _dateCal = datePickerDate.getDateTime();
                _dateFormat.format(_dateCal, dateStrDate, null);
                _editFieldDate.setText(dateStrDate.toString());
            }
            break;
        case TIME:
            final DateTimePicker datePickerTime =
                    DateTimePicker
                            .createInstance(_timeCal, null, "hh:mm:ss aa");
            if (datePickerTime.doModal()) {
                final StringBuffer dateStrTime = new StringBuffer();
                _timeCal = datePickerTime.getDateTime();
                _timeFormat.format(_timeCal, dateStrTime, null);
                _editFieldTime.setText(dateStrTime.toString());
            }
            break;
        case LONG:
            final DateTimePicker datePickerLong =
                    DateTimePicker.createInstance(_longDateCal,
                            DateFormat.DATE_LONG, DateFormat.TIME_DEFAULT);
            if (datePickerLong.doModal()) {
                final StringBuffer dateStrLong = new StringBuffer();
                _longDateCal = datePickerLong.getDateTime();
                _longDateFormat.format(_longDateCal, dateStrLong, null);
                _editFieldLong.setText(dateStrLong.toString());
            }
            break;
        case EXPIRY:
            final DateTimePicker datePickerExpiry =
                    DateTimePicker.createInstance(_expiryDateCal,
                            _expiryDateFormat.toPattern(), null);
            if (datePickerExpiry.doModal()) {
                final StringBuffer dateStrExpiry = new StringBuffer();
                _expiryDateCal = datePickerExpiry.getDateTime();
                _expiryDateFormat.format(_expiryDateCal, dateStrExpiry, null);
                _editFieldExpiry.setText(dateStrExpiry.toString());
            }
            break;
        case SPINBOX:
            if (_customSpinnerPopup == null) {
                _customSpinnerPopup = new CustomSpinnerPopup();
            }
            UiApplication.getUiApplication().pushModalScreen(
                    _customSpinnerPopup);
            if (_customSpinnerPopup.isSet()) {
                final String choice = _customSpinnerPopup.getChoice();
                _editFieldSpinbox.setText(choice);
            }
            break;
        }
    }

    /**
     * @see Screen#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(final TouchEvent message) {
        if (message.getEvent() == TouchEvent.CLICK) {
            int type = 0;

            if (_editFieldDefault.isFocus()) {
                type = DEFAULT;
            } else if (_editFieldDate.isFocus()) {
                type = DATE;
            } else if (_editFieldTime.isFocus()) {
                type = TIME;
            } else if (_editFieldLong.isFocus()) {
                type = LONG;
            } else if (_editFieldExpiry.isFocus()) {
                type = EXPIRY;
            } else if (_editFieldSpinbox.isFocus()) {
                type = SPINBOX;
            }
            if (type > 0) {
                showSpinnerDialog(type);
            }
            return true;
        }
        return super.touchEvent(message);
    }

    /**
     * @see Screen#navigationClick(int, int)
     */
    protected boolean navigationClick(final int status, final int time) {
        int type = 0;
        boolean returnValue = false;

        if (_editFieldDefault.isFocus()) {
            type = DEFAULT;
            returnValue = true;
        } else if (_editFieldDate.isFocus()) {
            type = DATE;
            returnValue = true;
        } else if (_editFieldTime.isFocus()) {
            type = TIME;
            returnValue = true;
        } else if (_editFieldLong.isFocus()) {
            type = LONG;
            returnValue = true;
        } else if (_editFieldExpiry.isFocus()) {
            type = EXPIRY;
            returnValue = true;
        } else if (_editFieldSpinbox.isFocus()) {
            type = SPINBOX;
            returnValue = true;
        }
        if (type > 0) {
            showSpinnerDialog(type);
        }
        return returnValue;
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }

    /**
     * A PopupScreen to display a TextSpinBoxField
     */
    public final static class CustomSpinnerPopup extends PopupScreen {
        private final TextSpinBoxField _spinBoxField;
        private boolean _isSet;

        /**
         * Creates a new CustomSpinnerPopup object
         */
        public CustomSpinnerPopup() {
            super(new VerticalFieldManager(), Screen.DEFAULT_CLOSE);
            final String[] choices =
                    { "New York", "Paris", "Barcelona", "Beijing", "Moscow",
                            "Brasilia", "Melbourne" };
            _spinBoxField = new TextSpinBoxField(choices);
            _spinBoxField.setVisibleRows(3);
            add(new LabelField("Choose city:"));
            add(new SeparatorField());
            final HorizontalFieldManager hfm =
                    new HorizontalFieldManager(Field.FIELD_HCENTER);
            hfm.add(_spinBoxField);
            add(hfm);
        }

        /**
         * Retrieves the currently selected choice
         * 
         * @return The currently selected choice
         */
        public String getChoice() {
            return (String) _spinBoxField.get(_spinBoxField.getSelectedIndex());
        }

        /**
         * Indicates whether the TextSpinBoxField has changed from its initial
         * state.
         * 
         * @return True if the selected choice has been modified, otherwise
         *         false
         */
        public boolean isSet() {
            return _isSet;
        }

        /**
         * @see Screen#invokeAction(int)
         */
        protected boolean invokeAction(final int action) {
            if (action == ACTION_INVOKE) {
                if (!_isSet) {
                    _isSet = true;
                }
                close();
                return true;
            }
            return false;
        }

        /**
         * @see Screen#close()
         */
        public void close() {
            if (!_isSet) {
                _spinBoxField.setSelectedIndex(0);
            }
            super.close();
        }
    }
}
