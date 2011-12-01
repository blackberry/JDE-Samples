/*
 * ProgressIndicatorDemo.java
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

package com.rim.samples.device.ui.progressindicatordemo;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A sample application demonstrating ProgressIndicatorView and
 * ActivityIndicatorView and related classes in the
 * net.rim.device.api.ui.component.progressIndicator package.
 * ProgressIndicatorView is used to represent work being performed where the
 * percentage of work completed at a given point in time is known.
 * ActivityIndicatorView is used when the duration of an operation or series of
 * operations is not known.
 */
public class ProgressIndicatorDemo extends UiApplication {
    /**
     * Creates a new ProgressIndicatorDemo object
     */
    public ProgressIndicatorDemo() {
        final ProgressIndicatorDemoScreen screen =
                new ProgressIndicatorDemoScreen();
        pushScreen(screen);
    }

    // Make the currently running thread the application's event
    // dispatch thread and begin processing events.
    public static void main(final String[] args) {
        final ProgressIndicatorDemo app = new ProgressIndicatorDemo();
        app.enterEventDispatcher();
    }

    /**
     * MainScreen class for the ProgressIndicatorDemo sample application
     */
    private static class ProgressIndicatorDemoScreen extends MainScreen
            implements FieldChangeListener {
        private final ButtonField _progressButton;
        private final ButtonField _activityButton;

        /**
         * Creates a new ProgressIndicatorDemoScreen object
         */
        public ProgressIndicatorDemoScreen() {
            super(Field.FIELD_HCENTER);
            setTitle("Progress Indicator Demo");

            _progressButton =
                    new ButtonField("Show Progress Indicator Screen",
                            ButtonField.NEVER_DIRTY | ButtonField.CONSUME_CLICK);
            _progressButton.setChangeListener(this);
            add(_progressButton);

            _activityButton =
                    new ButtonField("Show Activity Indicator Screen",
                            ButtonField.NEVER_DIRTY | ButtonField.CONSUME_CLICK);
            _activityButton.setChangeListener(this);
            add(_activityButton);
        }

        /**
         * @see FieldChangeListener#fieldChanged(Field, int)
         */
        public void fieldChanged(final Field field, final int context) {
            final UiApplication uiApp = UiApplication.getUiApplication();

            if (field == _activityButton) {
                uiApp.pushScreen(new ActivityIndicatorScreen());

            } else if (field == _progressButton) {
                uiApp.pushScreen(new ProgressIndicatorScreen());
            }
        }
    }
}
