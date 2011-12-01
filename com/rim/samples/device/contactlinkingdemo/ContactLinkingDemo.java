/**
 * ContactLinkingDemo.java
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

import net.rim.blackberry.api.menuitem.ApplicationMenuItem;
import net.rim.blackberry.api.pdap.contactlinking.AddressBookFieldFactory;
import net.rim.blackberry.api.pdap.contactlinking.LinkedContactConstants;
import net.rim.blackberry.api.pdap.contactlinking.LinkedContactUtilities;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * A sample application demonstrating the ability to link application specific
 * contacts with contacts in the BlackBerry address book.
 */
public final class ContactLinkingDemo extends UiApplication {
    /**
     * The primary application id for this application
     */
    public static final long APPLICATION_ID = 0x819417e94b6ca3b7L; // com.rim.samples.device.contactlinkingdemo.APPLICATION_ID

    /**
     * A secondary application id used to demonstrate how a BlackBerryContact
     * can be linked to by more than one application.
     */
    public static final long SECONDARY_APPLICATION_ID = 0x62501e608346866eL; // com.rim.samples.device.contactlinkingdemo.SECONDARY_APPLICATION_ID

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line args
     */
    public static void main(final String[] args) {
        if (args != null && args.length > 0) {
            if (args[0].equals("autostartup")) {
                // Create an application descriptor for this application
                final ApplicationDescriptor applicationDescriptor =
                        new ApplicationDescriptor(ApplicationDescriptor
                                .currentApplicationDescriptor(),
                                "Contact Linking Demo 1",
                                new String[] { "menu-invoked" });
                final ApplicationMenuItem[] items1 = new ApplicationMenuItem[2];
                items1[0] = new SampleMenuItem(APPLICATION_ID);
                items1[1] = new SampleMenuItem(APPLICATION_ID) {
                    public String toString() {
                        return "Test item 2";
                    }
                };
                LinkedContactUtilities.registerMenuItems(items1,
                        APPLICATION_ID,
                        LinkedContactConstants.COMPOSE_SN_MENU_GROUP,
                        applicationDescriptor);

                // Creating a second descriptor to demonstrate how a given
                // BlackBerryContact can be linked to more than one application.
                final ApplicationDescriptor appDesc2 =
                        new ApplicationDescriptor(applicationDescriptor,
                                "Contact Linking Demo 2",
                                new String[] { "menu-invoked" });

                final ApplicationMenuItem[] items2 = new ApplicationMenuItem[1];
                items2[0] = new SampleMenuItem(SECONDARY_APPLICATION_ID) {
                    public String toString() {
                        return "App 2 item";
                    }
                };

                LinkedContactUtilities.registerMenuItems(items2,
                        SECONDARY_APPLICATION_ID,
                        LinkedContactConstants.COMPOSE_SN_MENU_GROUP, appDesc2);

                final AddressBookFieldFactory factory1 =
                        new SampleAddressBookFieldFactory("Demo App 1");
                final AddressBookFieldFactory factory2 =
                        new SampleAddressBookFieldFactory("Demo App 2");

                LinkedContactUtilities.registerAddressBookFieldFactory(
                        factory1, APPLICATION_ID);
                LinkedContactUtilities.registerAddressBookFieldFactory(
                        factory2, SECONDARY_APPLICATION_ID);

            } else if (args[0].equals("menu-invoked")) {
                // Create a new instance of the application and make the
                // currently
                // running thread the application's event dispatch thread.
                final Application app = new ContactLinkingDemo(true);
                app.enterEventDispatcher();
            }
        } else {
            // Create a new instance of the application and make the currently
            // running thread the application's event dispatch thread.
            final Application app = new ContactLinkingDemo(false);
            app.enterEventDispatcher();
        }
    }

    /**
     * Creates a new ContactLinkingDemo object
     * 
     * @param menuInvoked
     *            True if invoked from ApplicationMenuItem, otherwise false
     */
    public ContactLinkingDemo(final boolean menuInvoked) {
        if (!menuInvoked) {
            // If we were launched from the home screen, push a UI screen
            pushScreen(new ContactListScreen());
        }
    }

    /**
     * Presents a dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     */
    public static void errorDialog(final String message) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(message);
            }
        });
    }
}
