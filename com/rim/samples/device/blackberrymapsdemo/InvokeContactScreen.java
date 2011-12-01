/*
 * InvokeContactScreen.java
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

package com.rim.samples.device.blackberrymapsdemo;

import java.util.Enumeration;

import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This example looks for the first Contact in the address book with a valid
 * city and region and displays a map for this address. If the address book
 * contains no addresses this program will simply display the default map
 * location. Also, this example makes no attempt to exhaustively check all
 * exception cases when retrieving a Contact. For more information on retrieving
 * and manipulating Contact information see the BlackBerry Development Guide.
 */
final class InvokeContactScreen extends MainScreen {
    /**
     * Constructor
     */
    InvokeContactScreen() {
        setTitle("Invoke Contact");

        final LabelField instructions =
                new LabelField(
                        "Select 'View Map' from the menu.  The first Contact in the address book that has a valid address (at least a city and state/province defined) will be displayed.  If there are no valid addresses, map view will default to last view.");
        add(instructions);

        addMenuItem(viewMapItem);
    }

    /**
     * Displays a map based on an address from the address book.
     */
    private final MenuItem viewMapItem = new MenuItem("View Map", 1000, 10) {
        /**
         * Run() method creates a list of Contacts from the address book and
         * searches list for first occurrence of a valid address.
         */
        public void run() {
            Contact c = null;
            boolean foundAddress = false;

            try {
                // Create list of Contacts.
                final ContactList contactList =
                        (ContactList) PIM.getInstance().openPIMList(
                                PIM.CONTACT_LIST, PIM.READ_WRITE);
                final Enumeration enumContact = contactList.items();

                // Search for a valid address.
                while (enumContact.hasMoreElements() && !foundAddress) {
                    c = (Contact) enumContact.nextElement();
                    final int id;

                    if (c.countValues(Contact.ADDR) > 0) {
                        final String address[] =
                                c.getStringArray(Contact.ADDR, 0);

                        if (address[Contact.ADDR_LOCALITY] != null
                                && address[Contact.ADDR_REGION] != null) {
                            // Invoke maps application for current Contact.
                            Invoke.invokeApplication(Invoke.APP_TYPE_MAPS,
                                    new MapsArguments(c, 0));
                            foundAddress = true;
                        }
                    }
                }
            } catch (final PIMException e) {
                e.printStackTrace();
            }

            // Invoke maps application with default map.
            if (!foundAddress) {
                Invoke.invokeApplication(Invoke.APP_TYPE_MAPS,
                        new MapsArguments());
            }
        }
    };
}
