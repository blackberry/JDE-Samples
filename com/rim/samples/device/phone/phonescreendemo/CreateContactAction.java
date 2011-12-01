/*
 * CreateContactAction.java
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

package com.rim.samples.device.phonescreendemo;

import java.util.Enumeration;

import javax.microedition.pim.Contact;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;

import net.rim.blackberry.api.pdap.BlackBerryContactList;
import net.rim.device.api.system.Application;

/**
 * This application is used to create a Contact object in the address book. When
 * the BlackBerry smartphone device receives or makes a call, the
 * {@link PhoneScreenDemo} and {@link PhoneScreenDemo2} sample applications will
 * look for a contact that has a matching phone number and display the data of
 * the matched contact on the phone screen.
 */
public class CreateContactAction extends Application {
    private static final String PHONE_NUMBER = "519-555-1111";

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line args (not used)
     */
    public static void main(final String[] args) throws PIMException {
        new CreateContactAction().enterEventDispatcher();
    }

    /**
     * Creates a new CreateContactAction object
     */
    public CreateContactAction() throws PIMException {

        final BlackBerryContactList contacts =
                (BlackBerryContactList) PIM.getInstance().openPIMList(
                        PIM.CONTACT_LIST, PIM.READ_WRITE);

        // Check if there is already an existing contact
        // with phone number 519-555-1111.
        final Enumeration enumeration =
                contacts.itemsByPhoneNumber(PHONE_NUMBER);
        if (!enumeration.hasMoreElements()) {
            final Contact contact = contacts.createContact();

            // Add first and last name to contact
            final String[] name =
                    new String[contacts.stringArraySize(Contact.NAME)];
            name[Contact.NAME_FAMILY] = "Warren";
            name[Contact.NAME_GIVEN] = "Clyde";
            contact.addStringArray(Contact.NAME, PIMItem.ATTR_NONE, name);

            // Add address info to contact
            final String[] addr =
                    new String[contacts.stringArraySize(Contact.ADDR)];
            addr[Contact.ADDR_LOCALITY] = "Waterloo";
            addr[Contact.ADDR_COUNTRY] = "Canada";
            contact.addStringArray(Contact.ADDR, Contact.ATTR_HOME, addr);

            // Add email and phone info to contact
            contact.addString(Contact.EMAIL, Contact.ATTR_WORK
                    | Contact.ATTR_PREFERRED, "cwarren@rim.com");
            contact.addString(Contact.TEL, Contact.ATTR_MOBILE, PHONE_NUMBER);

            contact.commit();
            contacts.close();
        }
    }
}
