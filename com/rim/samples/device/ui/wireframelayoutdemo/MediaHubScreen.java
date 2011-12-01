/*
 * MediaHubScreen.java
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

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.extension.component.PictureScrollField;

/**
 * A screen with a PictureScrollField at the bottom and a centered BitmapField
 * above. Changing the selected image in the PictureScrollField causes a
 * corresponding image to be displayed in the BitmapField.
 */
public class MediaHubScreen extends MainScreen implements FieldChangeListener {
    private static final String CALL_OUT_TEXT = "Call-out ";

    private final Bitmap[] _bitmapArray = new Bitmap[4];
    private final BitmapField _bitmapField;
    private final PictureScrollField _pictureScrollField;

    /**
     * Creates a new MediaHubScreenScreen object
     */
    public MediaHubScreen() {
        super(NO_VERTICAL_SCROLL);

        setTitle("Media Hub Screen");

        // Create the centered top content
        final HorizontalFieldManager topCenteredArea =
                new HorizontalFieldManager(USE_ALL_HEIGHT | USE_ALL_WIDTH
                        | NO_HORIZONTAL_SCROLL);
        final VerticalFieldManager horizontalPositioning =
                new VerticalFieldManager(USE_ALL_WIDTH | NO_VERTICAL_SCROLL
                        | Field.FIELD_VCENTER);
        topCenteredArea.add(horizontalPositioning);

        // Initialize the bitmap array
        _bitmapArray[0] = Bitmap.getBitmapResource("berry.jpg");
        _bitmapArray[1] = Bitmap.getBitmapResource("logo_blue.jpg");
        _bitmapArray[2] = Bitmap.getBitmapResource("logo_black.jpg");
        _bitmapArray[3] = Bitmap.getBitmapResource("building.jpg");

        // Add a bitmap field to the centered top content
        _bitmapField = new BitmapField(_bitmapArray[0], Field.FIELD_HCENTER);
        horizontalPositioning.add(_bitmapField);

        // Initialize an array of scroll entries
        final PictureScrollField.ScrollEntry[] entries =
                new PictureScrollField.ScrollEntry[4];
        entries[0] =
                new PictureScrollField.ScrollEntry(_bitmapArray[0],
                        "BlackBerry", CALL_OUT_TEXT + 1);
        entries[1] =
                new PictureScrollField.ScrollEntry(_bitmapArray[1],
                        "Blue logo", CALL_OUT_TEXT + 2);
        entries[2] =
                new PictureScrollField.ScrollEntry(_bitmapArray[2],
                        "Black logo", CALL_OUT_TEXT + 3);
        entries[3] =
                new PictureScrollField.ScrollEntry(_bitmapArray[3], "Building",
                        CALL_OUT_TEXT + 4);

        // Initialize the picture scroll field
        _pictureScrollField = new PictureScrollField(150, 50);
        _pictureScrollField.setData(entries, 0);
        _pictureScrollField
                .setHighlightStyle(PictureScrollField.HighlightStyle.ILLUMINATE);
        _pictureScrollField.setHighlightBorderColor(Color.RED);
        _pictureScrollField.setBackground(BackgroundFactory
                .createSolidBackground(Color.LIGHTBLUE));
        _pictureScrollField.setLabelsVisible(true);
        _pictureScrollField.setCenteredLens(true);
        _pictureScrollField.setChangeListener(this);

        // Combine the top and bottom elements using a
        // JustifiedVerticalFieldManager
        final JustifiedVerticalFieldManager bodyManager =
                new JustifiedVerticalFieldManager(topCenteredArea,
                        _pictureScrollField, false);

        // Add the justified manager to the screen
        add(bodyManager);
    }

    /**
     * @see FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field == _pictureScrollField) {
            // Set the centered bitmap to be that which is selected
            // in the picture scroll field.
            final int currentIndex = _pictureScrollField.getCurrentImageIndex();
            _bitmapField.setBitmap(_bitmapArray[currentIndex]);
        }
    }
}
