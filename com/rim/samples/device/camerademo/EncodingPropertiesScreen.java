/*
 * EncodingPropertiesScreen.java
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

package com.rim.samples.device.camerademo;

import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This MainScreen class allows a user to specify an encoding to be used for
 * taking a picture.
 */
public class EncodingPropertiesScreen extends MainScreen {
    RadioButtonGroup _radioButtonGroup;
    CameraScreen _parentScreen;

    /**
     * Constructs a new EncodingPropertiesScreen object
     * 
     * @param encodingProperties
     *            The array of encoding properties available
     * @param parentScreen
     *            The parent screen of the application
     * @param currentSelectedIndex
     *            The index of the encoding that is currently selected
     */
    public EncodingPropertiesScreen(
            final EncodingProperties[] encodingProperties,
            final CameraScreen parentScreen, final int currentSelectedIndex) {
        _parentScreen = parentScreen;
        _radioButtonGroup = new RadioButtonGroup();
        for (int i = 0; i < encodingProperties.length; i++) {
            final RadioButtonField buttonField =
                    new RadioButtonField(encodingProperties[i].toString());
            _radioButtonGroup.add(buttonField);
            this.add(buttonField);
        }
        _radioButtonGroup.setSelectedIndex(currentSelectedIndex);
    }

    /**
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close() {
        // Set the index of the selected encoding
        _parentScreen.setIndexOfEncoding(_radioButtonGroup.getSelectedIndex());
        super.close();
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Prevent the save dialog from being displayed
        return true;
    }
}
