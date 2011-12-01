/**
 * ZoomScreenDemo.java
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

package com.rim.samples.device.zoomscreendemo;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.extension.container.ZoomScreen;

/**
 * A sample application to demonstrate the ZoomScreen class
 */
public final class ZoomScreenDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command-line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final UiApplication app = new ZoomScreenDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new ZoomScreenDemo object
     */
    public ZoomScreenDemo() {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert("Click trackpad or screen to zoom");
            }
        });

        pushScreen(new ZoomScreenDemoScreen());
    }

    /**
     * A main screen class for the Zoom Screen Demo application
     */
    public final static class ZoomScreenDemoScreen extends MainScreen {
        private final EncodedImage _image;

        /**
         * Creates a new ZoomScreenDemoScreen object
         */
        public ZoomScreenDemoScreen() {
            setTitle("Zoom Screen Demo");

            _image = EncodedImage.getEncodedImageResource("img/building.jpg");
            final BitmapField bitmapField =
                    new BitmapField(_image.getBitmap(), FIELD_HCENTER
                            | FOCUSABLE);
            add(bitmapField);
        }

        /**
         * @see net.rim.device.api.ui.Screen#navigationClick(int, int)
         */
        protected boolean navigationClick(final int status, final int time) {
            // Push a new ZoomScreen if trackpad or screen is clicked
            UiApplication.getUiApplication().pushScreen(
                    new DemoZoomScreen(_image));
            return true;
        }

        /**
         * @see net.rim.device.api.ui.Screen#touchEvent(TouchEvent)
         */
        protected boolean touchEvent(final TouchEvent message) {
            if (message.getEvent() == TouchEvent.CLICK) {
                UiApplication.getUiApplication().pushScreen(
                        new DemoZoomScreen(_image));
                return true;
            }
            return super.touchEvent(message);
        }
    }

    /**
     * A ZoomScreen sub-class. The zoomedOutNearToFit() method is overidden to
     * close the ZoomScreen when the image size is at or near the original size.
     */
    static class DemoZoomScreen extends ZoomScreen {
        /**
         * Creates a new DemoZoomScreen object
         * 
         * @param image
         *            The image to display in the ZoomScreen
         */
        DemoZoomScreen(final EncodedImage image) {
            super(image);
        }

        /**
         * @see ZoomScreen#zoomedOutNearToFit()
         */
        public void zoomedOutNearToFit() {
            close();
        }
    }
}
