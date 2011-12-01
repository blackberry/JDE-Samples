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
import net.rim.blackberry.api.pdap.contactlinking.LinkedContactConstants;
import net.rim.blackberry.api.pdap.contactlinking.LinkedContactUtilities;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.image.Image;
import net.rim.device.api.ui.image.ImageFactory;

/**
 * A sample application demonstrating the ability to link application specific
 * contacts with contacts in the BlackBerry address book. Note that an address
 * book contact can be linked to by more than one application.
 */
public final class ContactLinkingDemo extends UiApplication {
    /**
     * The application id for this application
     */
    public static final long APPLICATION_ID = 0x819417e94b6ca3b7L; // com.rim.samples.device.contactlinkingdemo.APPLICATION_ID

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line args
     */
    public static void main(final String[] args) {
        if (args != null && args.length > 0) {
            if (args[0].equals("autostartup")) {
                // Create image
                final EncodedImage encodedImage =
                        EncodedImage
                                .getEncodedImageResource("img/logo_blue.jpg");
                final Image image = ImageFactory.createImage(encodedImage);

                // Create an application descriptor for this application
                final ApplicationDescriptor applicationDescriptor =
                        new ApplicationDescriptor(ApplicationDescriptor
                                .currentApplicationDescriptor(),
                                "Contact Linking Demo",
                                new String[] { "menu-invoked" });
                final ApplicationMenuItem[] items = new ApplicationMenuItem[2];
                items[0] = new SampleMenuItem(APPLICATION_ID, image);
                items[1] = new SampleMenuItem(APPLICATION_ID, image) {
                    public String toString() {
                        return "Test item 2";
                    }
                };

                LinkedContactUtilities.registerMenuItems(items, APPLICATION_ID,
                        LinkedContactConstants.COMPOSE_SN_MENU_GROUP,
                        applicationDescriptor);

                // Register info provider
                LinkedContactUtilities.registerLinkedContactInfoProvider(
                        new SampleLinkedContactInfoProvider(image, "Demo App"),
                        APPLICATION_ID,
                        LinkedContactConstants.COMPOSE_SN_MENU_GROUP);
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
