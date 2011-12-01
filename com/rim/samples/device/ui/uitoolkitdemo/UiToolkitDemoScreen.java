/*
 * UiToolkitDemoScreen.java
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

package com.rim.samples.device.ui.uitoolkitdemo;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * MainScreen class for the UiToolkitDemo application
 */
public final class UiToolkitDemoScreen extends MainScreen implements
        FieldChangeListener {
    private final ButtonField _speedBumpBtn;
    private final ButtonField _adjustmentBtn;
    private final ButtonField _titleBarBtn;
    private final ButtonField _trackpadBtn;

    /**
     * Creates a new UiToolkitDemoScreen object
     */
    public UiToolkitDemoScreen() {
        setTitle("UI Toolkit Demo");

        _speedBumpBtn =
                new ButtonField("Speed Bump Screen", Field.FIELD_HCENTER
                        | ButtonField.NEVER_DIRTY);
        _speedBumpBtn.setChangeListener(this);
        add(_speedBumpBtn);

        _adjustmentBtn =
                new ButtonField("Adjustment Screen", Field.FIELD_HCENTER
                        | ButtonField.NEVER_DIRTY);
        _adjustmentBtn.setChangeListener(this);
        add(_adjustmentBtn);

        _titleBarBtn =
                new ButtonField("Title Bar Screen", Field.FIELD_HCENTER
                        | ButtonField.NEVER_DIRTY);
        _titleBarBtn.setChangeListener(this);
        add(_titleBarBtn);

        _trackpadBtn =
                new ButtonField("Trackpad Gestures Screen", Field.FIELD_HCENTER
                        | ButtonField.NEVER_DIRTY);
        _trackpadBtn.setChangeListener(this);
        add(_trackpadBtn);
    }

    /**
     * @see FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        Screen screen = null;

        if (field == _speedBumpBtn) {
            screen = new SpeedBumpScreen();
        } else if (field == _adjustmentBtn) {
            screen = new AdjustmentScreen();
        } else if (field == _titleBarBtn) {
            screen = new TitleBarScreen();
        } else if (field == _trackpadBtn) {
            screen = new TrackpadGesturesScreen();
        }

        if (screen != null) {
            UiApplication.getUiApplication().pushScreen(screen);
        }
    }
}
