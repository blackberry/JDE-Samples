/*
 * PhoneMultiLineScreen.java
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

package com.rim.samples.device.phonemultilinedemo;

import net.rim.blackberry.api.phone.InvalidIDException;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * The MainScreen class for the Phone Multi Line Demo application
 */
public final class PhoneMultiLineScreen extends MainScreen {
    private final MenuItem _callWithSelectedLineMenuItem;
    private final MenuItem _switchLineMenuItem;
    private final BasicEditField _basicEditField;
    private final PhoneMultiLineAction _action;
    private final ObjectChoiceField _choiceField;
    private final UiApplication _app;
    private Choice[] _choices;

    /**
     * Constructs a new PhoneMultiLineScreen object
     * 
     * @param app
     *            A reference to the UiApplication instance
     */
    public PhoneMultiLineScreen(final UiApplication app) {
        _app = app;
        setTitle("Phone Multi Line Demo");
        populateChoices();

        _choiceField =
                new ObjectChoiceField("Select a line", _choices,
                        getActiveChoice());
        add(_choiceField);

        // Menu item to initiate outgoing call with the selected line
        _callWithSelectedLineMenuItem =
                new MenuItem("Call With Selected Line", 110, 10) {
                    public void run() {
                        final String text = _basicEditField.getText();
                        if (text == null || text.trim().length() == 0) {
                            Dialog.alert("Please enter phone number");
                        } else {
                            final Choice choice =
                                    (Choice) _choiceField
                                            .getChoice(_choiceField
                                                    .getSelectedIndex());
                            try {
                                // If the selected line is not currently active,
                                // the
                                // following method call will set it as active
                                // as
                                // well as place the call.
                                Phone.initiateCall(choice.getLineId(), text);
                            } catch (final RadioException re) {
                                PhoneMultiLineDemo
                                        .messageDialog("Phone.initiateCall(int, String) threw "
                                                + re.toString());
                            }
                        }
                    }
                };
        addMenuItem(_callWithSelectedLineMenuItem);

        _switchLineMenuItem =
                new MenuItem("Switch To The Selected Line", 110, 10) {
                    public void run() {
                        final Choice choice =
                                (Choice) _choiceField.getChoice(_choiceField
                                        .getSelectedIndex());
                        Phone.setPreferredLine(choice.getLineId());
                    }
                };
        addMenuItem(_switchLineMenuItem);

        // Edit field for users to enter a phone number
        _basicEditField =
                new BasicEditField("Phone number: ", "", 10,
                        BasicEditField.FILTER_PHONE);
        add(_basicEditField);

        // Instantiate the PhoneMultiLineAction object, which registers itself
        // as the phone multi-line listener.
        _action = new PhoneMultiLineAction(this);
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Suppress the save prompt
        return true;
    }

    /**
     * @see Screen#onClose()
     */
    public boolean onClose() {
        Phone.removePhoneListener(_action);
        return super.onClose();
    }

    /**
     * Populates the choice field with the line IDs
     */
    private void populateChoices() {
        final int[] lineIds = Phone.getLineIds();
        if (lineIds != null && lineIds.length > 0) {
            _choices = new Choice[lineIds.length];

            for (int i = lineIds.length - 1; i >= 0; --i) {
                String lineNumber;
                String lineLabel;
                int lineType;
                try {
                    lineNumber = Phone.getLineNumber(lineIds[i]);
                    lineLabel = Phone.getLineLabel(lineIds[i]);
                    lineType = Phone.getLineType(lineIds[i]);
                } catch (final InvalidIDException e) {
                    PhoneMultiLineDemo.messageDialog("Error: " + e.toString());
                    continue;
                }

                final StringBuffer buf = new StringBuffer();
                buf.append(lineLabel);
                switch (lineType) {
                case Phone.MOBILE_TYPE:
                    buf.append(" (Mobile): ");
                    break;
                case Phone.PBX_TYPE:
                    buf.append(" (PBX): ");
                    break;
                }
                buf.append(lineNumber);
                _choices[i] = new Choice(lineIds[i], buf.toString());
            }
        }
    }

    /**
     * Sets the selected choice object to be that which corresponds to the
     * active line.
     */
    void setSelectedChoice() {
        _app.invokeAndWait(new Runnable() {
            public void run() {
                _choiceField.setSelectedIndex(getActiveChoice());
            }
        });
    }

    /**
     * Returns the choice object whose line id is the active line id
     * 
     * @return Choice object corresponding to the active line
     */
    private Choice getActiveChoice() {
        final int lineId = Phone.getActiveLineId();
        if (_choices != null && _choices.length > 0) {
            for (int i = 0; i < _choices.length; i++) {
                if (_choices[i].getLineId() == lineId) {
                    return _choices[i];
                }
            }
        }
        return null;
    }

    /**
     * A class used to encapsulate the line ids and the line descriptions
     */
    private static final class Choice {
        private final int _lineId;
        private final String _choiceLabel;

        public Choice(final int lineId, final String choiceLabel) {
            _lineId = lineId;
            _choiceLabel = choiceLabel;
        }

        public String toString() {
            return _choiceLabel;
        }

        public int getLineId() {
            return _lineId;
        }
    }
}
