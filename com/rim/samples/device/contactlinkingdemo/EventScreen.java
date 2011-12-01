/**
 * EventScreen.java
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

package com.rim.samples.device.contactlinkingdemo;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import net.rim.blackberry.api.pdap.contactlinking.LinkableContact;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A screen for displaying events
 */
public final class EventScreen extends MainScreen {
    /**
     * Creates a new EventScreen object
     * 
     * @param linkableContact
     *            The contact for which to fire an event
     * @param time
     *            Time of the event associated with this screen
     */
    public EventScreen(final LinkableContact linkableContact, final String time) {
        // Initialize UI
        setTitle(linkableContact.getString(LinkableContact.NAME));
        final RichTextField testField = new RichTextField();
        testField.setText("Tested at " + time);
        add(new SeparatorField());
        add(testField);
    }

    /**
     * Retrieves the current time
     * 
     * @return Current time in String format
     */
    public static String getTime() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        final Date dt = cal.getTime();
        final String time = dt.toString();

        return time;
    }

    /**
     * Fires an event
     * 
     * @param linkableContact
     *            The contact for which to fire an event
     */
    public static void fireEvent(final LinkableContact linkableContact) {
        final UiApplication app = UiApplication.getUiApplication();
        app.invokeLater(new Runnable() {
            public void run() {
                app.pushGlobalScreen(
                        new EventScreen(linkableContact, getTime()), 0,
                        UiEngine.GLOBAL_MODAL);
            }
        });
    }
}
