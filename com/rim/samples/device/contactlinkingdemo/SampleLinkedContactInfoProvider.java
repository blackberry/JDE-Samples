/*
 * SampleLinkedContactInfoProvider.java
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

import net.rim.blackberry.api.pdap.contactlinking.LinkableContact;
import net.rim.blackberry.api.pdap.contactlinking.LinkedContactCallback;
import net.rim.blackberry.api.pdap.contactlinking.LinkedContactInfoProvider;
import net.rim.device.api.ui.image.Image;

/**
 * A class which can supply requested linked contact information. This provider
 * supports LinkedContactInfoProvider.STATUS,
 * LinkedContactInfoProvider.USER_NAME and
 * LinkedContactInfoProvider.AVAILABILITY fields.
 */
public class SampleLinkedContactInfoProvider extends LinkedContactInfoProvider {
    private final Image _image;
    private final String _appName;

    /**
     * Creates a new SampleLinkedContactInfoProvider object
     * 
     * @param image
     *            Image to associate with registering application
     * @param appName
     *            Name of the registering application
     */
    public SampleLinkedContactInfoProvider(final Image image,
            final String appName) {
        _image = image;
        _appName = appName;
    }

    /**
     * @see LinkedContactInfoProvider#getAppImage()
     */
    public Image getAppImage() {
        return _image;
    }

    /**
     * @see LinkedContactInfoProvider#getAppName()
     */
    public String getAppName() {
        return _appName;
    }

    /**
     * @see LinkedContactInfoProvider#requestFields(String,
     *      LinkedContactCallback, int)
     */
    public void requestFields(final String contactID,
            final LinkedContactCallback adapter, final int fields) {
        final LinkableContact contact =
                ContactListScreen.getUserForID(contactID);

        if ((fields & LinkedContactInfoProvider.AVAILABILITY) != 0) {
            adapter.setAvailability(LinkedContactInfoProvider.AVAILABILITY_ONLINE);
        }
        if ((fields & LinkedContactInfoProvider.STATUS) != 0) {
            adapter.setStatusString("<Sample contact status>");
        }
        if ((fields & LinkedContactInfoProvider.USER_NAME) != 0) {
            adapter.setUserName(contact.toString());
        }
    }

    /**
     * @see LinkedContactInfoProvider#isSupported(int)
     */
    public boolean isSupported(final int capability) {
        switch (capability) {
        case LinkedContactInfoProvider.STATUS:
        case LinkedContactInfoProvider.AVAILABILITY:
        case LinkedContactInfoProvider.USER_NAME:
            return true;
        default:
            return false;
        }
    }
}
