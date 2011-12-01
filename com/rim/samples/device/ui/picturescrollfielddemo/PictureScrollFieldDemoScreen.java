/**
 * PictureScrollFieldDemoScreen.java
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

package com.rim.samples.device.picturescrollfielddemo;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.extension.component.PictureScrollField;
import net.rim.device.api.ui.extension.component.PictureScrollField.HighlightStyle;
import net.rim.device.api.ui.extension.component.PictureScrollField.ScrollEntry;

/**
 * The main screen class for the the Picture Scroll Field Demo application
 */
public final class PictureScrollFieldDemoScreen extends MainScreen implements
        FieldChangeListener {
    private static String CALL_OUT_TEXT = "Call-out ";

    private final PictureScrollField _pictureScrollField;

    /**
     * Creates a new PictureScrollFieldDemoScreen object
     */
    public PictureScrollFieldDemoScreen() {
        setTitle("Picture Scroll Field Demo");

        // Initialize an array of scroll entries
        final ScrollEntry[] entries = new ScrollEntry[4];
        entries[0] =
                new ScrollEntry(Bitmap.getBitmapResource("berry.jpg"),
                        "BlackBerry", CALL_OUT_TEXT + 1);
        entries[1] =
                new ScrollEntry(Bitmap.getBitmapResource("logo_blue.jpg"),
                        "Blue logo", CALL_OUT_TEXT + 2);
        entries[2] =
                new ScrollEntry(Bitmap.getBitmapResource("logo_black.jpg"),
                        "Black logo", CALL_OUT_TEXT + 3);
        entries[3] =
                new ScrollEntry(Bitmap.getBitmapResource("building.jpg"),
                        "Building", CALL_OUT_TEXT + 4);

        // Initialize the picture scroll field
        _pictureScrollField = new PictureScrollField(150, 100);
        _pictureScrollField.setData(entries, 0);
        _pictureScrollField.setHighlightStyle(HighlightStyle.ILLUMINATE);
        _pictureScrollField.setHighlightBorderColor(Color.RED);
        _pictureScrollField.setBackground(BackgroundFactory
                .createSolidBackground(Color.LIGHTBLUE));
        _pictureScrollField.setLabelsVisible(true);
        add(_pictureScrollField);

        // Initialize a choice field for highlight style selection
        final String[] choices =
                new String[] { "No highlight", "Illuminate",
                        "Illuminate with round border",
                        "Illuminate with square border", "Square border",
                        "Magnify lens", "Shrink lens",
                        "Illuminate with magnify lens",
                        "Illuminate with shrink lens" };
        add(new SeparatorField());
        add(new LabelField("Select highlight style", Field.FIELD_HCENTER));
        final ObjectChoiceField choiceField =
                new ObjectChoiceField("", choices, HighlightStyle.ILLUMINATE,
                        Field.FIELD_HCENTER);
        choiceField.setChangeListener(this);
        add(choiceField);

        add(new SeparatorField());

        // Initialize a check box for toggling the center lens
        final CheckboxField checkBox =
                new CheckboxField("Enable center lens", false,
                        Field.FIELD_HCENTER);
        checkBox.setChangeListener(this);
        add(checkBox);
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }

    /**
     * @see Screen#navigationClick(int, int)
     */
    protected boolean navigationClick(final int status, final int time) {
        if (_pictureScrollField.isFocus()) {
            Dialog.inform("You selected item "
                    + _pictureScrollField.getCurrentImageIndex());
            return true;
        }

        return super.navigationClick(status, time);
    }

    /**
     * @see Screen#touchEvent()
     */
    protected boolean touchEvent(final TouchEvent message) {
        if (message.getEvent() == TouchEvent.CLICK) {
            if (_pictureScrollField.isFocus()) {
                Dialog.inform("You selected item "
                        + _pictureScrollField.getCurrentImageIndex());
                return true;
            }
        }

        return super.touchEvent(message);
    }

    /**
     * @see FieldChangeListener(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        // if(field == _choiceField)
        if (field instanceof ObjectChoiceField) {
            final int index = ((ObjectChoiceField) field).getSelectedIndex();
            switch (index) {
            case HighlightStyle.NO_HIGHLIGHT:
                _pictureScrollField
                        .setHighlightStyle(HighlightStyle.NO_HIGHLIGHT);
                break;
            case HighlightStyle.ILLUMINATE:
                _pictureScrollField
                        .setHighlightStyle(HighlightStyle.ILLUMINATE);
                break;
            case HighlightStyle.ILLUMINATE_WITH_ROUND_BORDER:
                _pictureScrollField
                        .setHighlightStyle(HighlightStyle.ILLUMINATE_WITH_ROUND_BORDER);
                break;
            case HighlightStyle.ILLUMINATE_WITH_SQUARE_BORDER:
                _pictureScrollField
                        .setHighlightStyle(HighlightStyle.ILLUMINATE_WITH_SQUARE_BORDER);
                break;
            case HighlightStyle.SQUARE_BORDER:
                _pictureScrollField
                        .setHighlightStyle(HighlightStyle.SQUARE_BORDER);
                break;
            case HighlightStyle.MAGNIFY_LENS:
                _pictureScrollField
                        .setHighlightStyle(HighlightStyle.MAGNIFY_LENS);
                break;
            case HighlightStyle.SHRINK_LENS:
                _pictureScrollField
                        .setHighlightStyle(HighlightStyle.SHRINK_LENS);
                break;
            case HighlightStyle.ILLUMINATE_WITH_MAGNIFY_LENS:
                _pictureScrollField
                        .setHighlightStyle(HighlightStyle.ILLUMINATE_WITH_MAGNIFY_LENS);
                break;
            case HighlightStyle.ILLUMINATE_WITH_SHRINK_LENS:
                _pictureScrollField
                        .setHighlightStyle(HighlightStyle.ILLUMINATE_WITH_SHRINK_LENS);
                break;

            }
            _pictureScrollField.setFocus();
        } else if (field instanceof CheckboxField) {
            _pictureScrollField.setCenteredLens(!_pictureScrollField
                    .hasCenteredLens());
        }
        _pictureScrollField.setFocus();
    }
}
