/*
 * TransitionScreen.java
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

package com.rim.samples.device.ui.screentransitionsdemo;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;

/**
 * A screen to display a Bitmap in a BitmapField, used to demonstrate screen
 * transitions
 */
public class TransitionScreen extends MainScreen {
    /**
     * Creates a new TransitionScreen object
     */
    public TransitionScreen(final String title, final int color) {
        setTitle(title);
        final VerticalFieldManager manager =
                (VerticalFieldManager) getMainManager();
        manager.setBackground(BackgroundFactory.createSolidBackground(color));
    }

    /**
     * @see Screen#invokeAction(int)
     */
    protected boolean invokeAction(final int action) {
        switch (action) {
        case ACTION_INVOKE: {
            final ScreenTransitionsDemo app =
                    (ScreenTransitionsDemo) UiApplication.getUiApplication();
            app.startOrStopThread();
            return true;
        }
        }
        return super.invokeAction(action);
    }

    /**
     * @see Screen#TouchEvent(TouchEvent)
     */
    public boolean touchEvent(final TouchEvent event) {
        if (event.getEvent() == TouchEvent.UNCLICK) {
            invokeAction(ACTION_INVOKE);
            return true;
        }
        return super.touchEvent(event);
    }
}
