/**
 * NotificationsDemo.java
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

package com.rim.samples.device.notificationsdemo;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.notification.NotificationsConstants;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

/**
 * An example of use of the Notifications API. This application has an alternate
 * entry point in which the app is registered as a notification source on device
 * startup. Immediate and deferred events are generated via a UI menu item and
 * the Event.fire() method.
 */
public final class NotificationsDemo extends UiApplication {
    // com.rim.samples.device.notificationsdemo.NOTIFICATIONS_ID_1
    static final long NOTIFICATIONS_ID_1 = 0xdc5bf2f81374095L;

    /**
     * Entry point for application
     * 
     * @param args
     *            Command-line arguments
     */
    public static void main(final String[] args) {
        if (args.length > 0 && args[0].equals("autostartup")) {
            final NotificationsDemo nd = new NotificationsDemo();
            nd.registerNotificationObjects();

            // Keep this instance around for rendering
            // Notification dialogs.
            nd.enterEventDispatcher();
        } else {
            // Start a new app instance for GUI operations
            new NotificationsDemo().showGui();
        }
    }

    /**
     * Displays the NotificationDemoScreen
     */
    private void showGui() {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        pushScreen(new NotificationsDemoScreen());
        enterEventDispatcher();
    }

    /**
     * Registers this application as the notification manager
     */
    private void registerNotificationObjects() {
        // A source is registered to tell the system that the application will
        // be sending notification events. This will will cause a new user
        // editable configuration to be added to the Profiles application.
        NotificationsManager.registerSource(NOTIFICATIONS_ID_1, new Object() {
            public String toString() {
                return "Notifications Demo";
            }
        }, NotificationsConstants.IMPORTANT);

        // Our NotificationsEngineListener implementation will display a dialog
        // to the user when a deferred event is triggered.
        NotificationsManager.registerNotificationsEngineListener(
                NOTIFICATIONS_ID_1, new NotificationsEngineListenerImpl(this));

        // Our Consequence implementation will be invoked whenever an immediate
        // event occurs.
        NotificationsManager.registerConsequence(ConsequenceImpl.ID,
                new ConsequenceImpl());
    }

    /**
     * The MainScreen class for the Notifications Demo application
     */
    private static class NotificationsDemoScreen extends MainScreen {
        private long _eventId;

        /**
         * Creates a new NotificationsDemoScreen object
         */
        private NotificationsDemoScreen() {
            // Initialize UI components
            setTitle("Notifications Demo");
            add(new RichTextField("Trigger notification from menu."));

            // A menu item to generate immediate and deferred events
            final MenuItem notifyItem =
                    new MenuItem(new StringProvider("Notify (ID1)"), 0x230010,
                            0);
            notifyItem.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    final int trigger = NotificationsConstants.MANUAL_TRIGGER;

                    // The timeout parameter is IGNORED unless the TRIGGER
                    // is OUT_OF_HOLSTER_TRIGGER.
                    final long timeout = -1;

                    final Event e =
                            new Event(NotificationsDemo.NOTIFICATIONS_ID_1,
                                    _eventId, 500, timeout, trigger);
                    _eventId++;
                    e.fire();
                }
            }));
            addMenuItem(notifyItem);
        }
    }
}
