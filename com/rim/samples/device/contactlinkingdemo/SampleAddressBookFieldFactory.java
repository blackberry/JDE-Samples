/*
 * SampleAddressBookFieldFactory.java
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

import net.rim.blackberry.api.pdap.contactlinking.AddressBookFieldFactory;
import net.rim.blackberry.api.pdap.contactlinking.LinkableContact;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.TextField;

/**
 * A factory class for creating fields in which to display the sample
 * application link data on an address card.
 */
public final class SampleAddressBookFieldFactory implements
        AddressBookFieldFactory {
    private final String _applicationName;

    /**
     * Creates a newSampleAddressBookFieldFactory object
     * 
     * @param appName
     *            The name of a linked application to display in a field
     */
    public SampleAddressBookFieldFactory(final String appName) {
        _applicationName = appName;
    }

    /**
     * Creates a field for displaying a linked contact's name on an address
     * card.
     */
    public Field createAddressBookField(final String contactID) {
        final LinkableContact user = ContactListScreen.getUserForID(contactID);
        if (user == null) {
            return null;
        }
        return new BasicEditField(_applicationName + ": ", user
                .getString(LinkableContact.NAME), TextField.DEFAULT_MAXCHARS,
                Field.USE_ALL_WIDTH | Field.READONLY);
    }
}
