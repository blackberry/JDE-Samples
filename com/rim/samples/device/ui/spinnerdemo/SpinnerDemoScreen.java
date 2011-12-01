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
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
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
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
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

    private static final int SELECTION_FIELD_HEIGHT = 50;

    private final DateField _dateField;

    private final SelectionField _editFieldDefault;
    private final SelectionField _editFieldDate;
    private final SelectionField _editFieldTime;
    private final SelectionField _editFieldLong;
    private final SelectionField _editFieldExpiry;
    private final SelectionField _editFieldSpinbox;

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

        final Background background =
                BackgroundFactory.createSolidBackground(Color.LIGHTBLUE);

        final LabelField dateLabel =
                new LabelField("Date Field", Field.FIELD_HCENTER);
        dateLabel.setBackground(background);
        fieldManager.add(dateLabel);

        // Add a typical date field to the VerticalFieldManager
        _dateField =
                new DateField("Date/time: ", System.currentTimeMillis(),
                        DateField.DATE_TIME);
        fieldManager.add(_dateField);

        fieldManager.add(new SeparatorField());

        // Add UI elements to the VerticalFieldManager, including fields
        // which will allow a user to make selections from date/time and
        // custom spinners.
        final LabelField dateTimeLabel =
                new LabelField("Date Time Picker", Field.FIELD_HCENTER);
        dateTimeLabel.setBackground(background);
        fieldManager.add(dateTimeLabel);

        _editFieldDefault = new SelectionField("Default: ");
        fieldManager.add(_editFieldDefault);

        _editFieldDate = new SelectionField("Date only: ");
        fieldManager.add(_editFieldDate);

        _editFieldTime = new SelectionField("Time only: ");
        fieldManager.add(_editFieldTime);

        _editFieldLong = new SelectionField("Long date: ");
        fieldManager.add(_editFieldLong);

        _editFieldExpiry = new SelectionField("Expiry date: ");
        fieldManager.add(_editFieldExpiry);

        fieldManager.add(new SeparatorField());

        final LabelField textSpinLabel =
                new LabelField("Text Spin Box Field", Field.FIELD_HCENTER);
        textSpinLabel.setBackground(background);
        fieldManager.add(textSpinLabel);

        _editFieldSpinbox = new SelectionField("City: ");
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
        TouchGesture touchGesture = null;

        final int event = message.getEvent();
        if (event == TouchEvent.GESTURE) {
            touchGesture = message.getGesture();
        }

        if (message.getEvent() == TouchEvent.CLICK || touchGesture != null
                && touchGesture.getEvent() == TouchGesture.TAP) {
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
            if (!_dateField.isFocus()) {
                return true;
            }
        }
        return super.touchEvent(message);
    }

    /**
     * @see Screen#navigationClick(int, int)
     */
    protected boolean navigationClick(final int status, final int time) {
        int type = 0;
        boolean eventConsumed = false;

        if (_editFieldDefault.isFocus()) {
            type = DEFAULT;
            eventConsumed = true;
        } else if (_editFieldDate.isFocus()) {
            type = DATE;
            eventConsumed = true;
        } else if (_editFieldTime.isFocus()) {
            type = TIME;
            eventConsumed = true;
        } else if (_editFieldLong.isFocus()) {
            type = LONG;
            eventConsumed = true;
        } else if (_editFieldExpiry.isFocus()) {
            type = EXPIRY;
            eventConsumed = true;
        } else if (_editFieldSpinbox.isFocus()) {
            type = SPINBOX;
            eventConsumed = true;
        }
        if (type > 0) {
            showSpinnerDialog(type);
        }
        return eventConsumed;
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }

    /**
     * @see Screen#keyChar(char, int, int)
     */
    protected boolean keyChar(final char key, final int status, final int time) {
        if (key == Keypad.KEY_ENTER) {
            return navigationClick(status, time);
        }

        return super.keyChar(key, status, time);
    }

    /**
     * A BasicEditField which can not be edited manually
     */
    static class SelectionField extends BasicEditField {
        /**
         * Creates a new SelectionField object
         * 
         * @param label
         *            Label for this field
         * @param text
         *            Display text for this field
         */
        SelectionField(final String label) {
            super(label, "Click or tap to select");
        }

        /**
         * @see BasicEditField#layout(int, int)
         */
        protected void layout(final int width, final int height) {
            super.layout(width, height);
            setExtent(width, SELECTION_FIELD_HEIGHT);
        }

        /**
         * @see Field#keyChar(char, int, int)
         */
        protected boolean keyChar(final char key, final int status,
                final int time) {
            if (key == Keypad.KEY_ENTER) {
                return false;
            }
            return super.keyChar(key, status, time);
        }
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
         * @see Screen#touchEvent(TouchEvent)
         */
        protected boolean touchEvent(final TouchEvent message) {
            if (message.getEvent() == TouchEvent.GESTURE) {
                final TouchGesture touchGesture = message.getGesture();
                if (touchGesture.getEvent() == TouchGesture.TAP) {
                    _isSet = true;
                    close();
                    return true;
                }
            }
            return super.touchEvent(message);
        }

        /**
         * @see Screen#invokeAction(int)
         */
        protected boolean invokeAction(final int action) {
            if (action == ACTION_INVOKE) {
                _isSet = true;
                close();
                return true;
            }
            return super.invokeAction(action);
        }

        /**
         * @see Screen#keyChar(char, int, int)
         */
        protected boolean keyChar(final char key, final int status,
                final int time) {
            if (key == Keypad.KEY_ENTER) {
                _isSet = true;
                close();
                return true;
            }
            return super.keyChar(key, status, time);
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
