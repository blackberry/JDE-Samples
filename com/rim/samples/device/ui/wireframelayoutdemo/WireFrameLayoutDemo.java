/*
 * WireFrameLayoutDemo.java
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

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This sample application demonstrates how to create screens using typical
 * layout configurations.
 */
public class WireFrameLayoutDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final WireFrameLayoutDemo app = new WireFrameLayoutDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new WireFrameLayoutDemo object
     */
    public WireFrameLayoutDemo() {
        pushScreen(new WireFrameLayoutDemoScreen());
    }

    /**
     * MainScreen class for the WireFrameLayoutDemo application
     */
    class WireFrameLayoutDemoScreen extends MainScreen implements
            FieldChangeListener {
        private final ButtonField _basicScrollingButton;
        private final ButtonField _invertedScrollingButton;
        private final ButtonField _searchButton;
        private final ButtonField _horizontalVerticalCenteredButton;
        private final ButtonField _gridFieldButton;
        private final ButtonField _mediaHubButton;

        /**
         * Creates a new WireFrameLayoutDemoScreen object
         */
        WireFrameLayoutDemoScreen() {
            setTitle("Wire Frame Layout Demo");

            // Instantiate buttons
            _basicScrollingButton =
                    new ButtonField("Basic Scrolling Screen",
                            ButtonField.NEVER_DIRTY | ButtonField.CONSUME_CLICK);
            _invertedScrollingButton =
                    new ButtonField("Inverted Scrolling Screen",
                            ButtonField.NEVER_DIRTY | ButtonField.CONSUME_CLICK);
            _searchButton =
                    new ButtonField("Search Screen", ButtonField.NEVER_DIRTY
                            | ButtonField.CONSUME_CLICK);
            _horizontalVerticalCenteredButton =
                    new ButtonField("Horizontal Vertical Centered Screen",
                            ButtonField.NEVER_DIRTY | ButtonField.CONSUME_CLICK);
            _gridFieldButton =
                    new ButtonField("Grid Field Screen",
                            ButtonField.NEVER_DIRTY | ButtonField.CONSUME_CLICK);
            _mediaHubButton =
                    new ButtonField("Media Hub Screen", ButtonField.NEVER_DIRTY
                            | ButtonField.CONSUME_CLICK);

            // Make this class a change listener for the buttons
            _basicScrollingButton.setChangeListener(this);
            _invertedScrollingButton.setChangeListener(this);
            _searchButton.setChangeListener(this);
            _horizontalVerticalCenteredButton.setChangeListener(this);
            _gridFieldButton.setChangeListener(this);
            _mediaHubButton.setChangeListener(this);

            // Add buttons to the screen
            add(_basicScrollingButton);
            add(_invertedScrollingButton);
            add(_searchButton);
            add(_horizontalVerticalCenteredButton);
            add(_gridFieldButton);
            add(_mediaHubButton);
        }

        /**
         * @see FieldChangeListener#fieldChanged(field, int)
         */
        public void fieldChanged(final Field field, final int context) {
            // Push appropriate screen depending on which button was clicked
            if (field == _basicScrollingButton) {
                WireFrameLayoutDemo.this.pushScreen(new BasicScrollingScreen());
            } else if (field == _invertedScrollingButton) {
                WireFrameLayoutDemo.this
                        .pushScreen(new InvertedScrollingScreen());
            } else if (field == _searchButton) {
                WireFrameLayoutDemo.this.pushScreen(new SearchScreen());
            } else if (field == _horizontalVerticalCenteredButton) {
                WireFrameLayoutDemo.this
                        .pushScreen(new HorizontalVerticalCenteredScreen());
            } else if (field == _gridFieldButton) {
                WireFrameLayoutDemo.this.pushScreen(new GridFieldScreen());
            } else if (field == _mediaHubButton) {
                WireFrameLayoutDemo.this.pushScreen(new MediaHubScreen());
            }
        }
    }
}
