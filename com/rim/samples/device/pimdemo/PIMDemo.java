/**
 * PIMDemo.java
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

package com.rim.samples.device.blackberry.pim;

import javax.microedition.pim.Contact;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * Sample to demonstrate functionality of Personal Information Management (PIM)
 * API's. EventScreen class allows an event to be saved and alerts invitees via
 * email. ContactListScreen class displays a list of potential invitees.
 * ContactScreen screen allows additional contacts to be added to the Address
 * Book.
 */
public final class PIMDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new PIMDemo().enterEventDispatcher();
    }

    /**
     * Creates a new PIMDemo object
     */
    public PIMDemo() {
        // Push a new EventScreen onto the display stack.
        pushScreen(new EventScreen());
    }

    /**
     * Returns the name to be displayed for a given Contact
     * 
     * @param contact
     *            The Contact for which to extract the display name
     * @return The name to be displayed in the list field
     */
    public static String getDisplayName(final Contact contact) {
        if (contact == null) {
            return null;
        }

        String displayName = null;

        // First, see if there is a meaningful name set for the contact.
        if (contact.countValues(Contact.NAME) > 0) {
            final String[] name = contact.getStringArray(Contact.NAME, 0);
            final String firstName = cleanString(name[Contact.NAME_GIVEN]);
            final String lastName = cleanString(name[Contact.NAME_FAMILY]);

            if (firstName != null && lastName != null) {
                displayName = firstName + " " + lastName;
            } else if (firstName != null) {
                displayName = firstName;
            } else if (lastName != null) {
                displayName = lastName;
            }

            if (displayName != null) {
                final String namePrefix =
                        cleanString(name[Contact.NAME_PREFIX]);

                if (namePrefix != null) {
                    displayName = namePrefix + " " + displayName;
                }
                return displayName;
            }
        }

        // If not, use the company name.
        if (contact.countValues(Contact.ORG) > 0) {
            final String companyName =
                    cleanString(contact.getString(Contact.ORG, 0));

            if (companyName != null) {
                return companyName;
            }
        }
        return displayName;
    }

    // Trims white space from a string.
    public static String cleanString(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        return value.length() > 0 ? value : null;
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
