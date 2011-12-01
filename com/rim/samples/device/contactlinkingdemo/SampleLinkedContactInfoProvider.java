/*
 * SampleLinkedContactInfoProvider.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
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
