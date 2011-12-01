/*
 * OTAContactData.java
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

package com.rim.samples.device.otasyncdemo;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.Persistable;

/**
 * This class represents a contact, encapsulating the contact's information.
 * Note: the GUID is identical to the UID.
 */
public class OTAContactData implements SyncObject, Persistable {
    private int _guid = 0;
    private String _first, _last, _email, _phone;

    /**
     * Default constructor.
     */
    public OTAContactData() {
        _first = "";
        _last = "";
        _email = "";
        _phone = "";
    }

    /**
     * Constructor
     * 
     * @param guid
     *            The GUID of the contact
     */
    public OTAContactData(final int guid) {
        this();
        _guid = guid;
    }

    /**
     * Gets the GUID.
     * 
     * @return The contact's GUID
     */
    int getGUID() {
        return _guid;
    }

    /**
     * Gets the UID.
     * 
     * @return The contact's UID
     */
    public int getUID() {
        return _guid;
    }

    /**
     * Sets the GUID.
     * 
     * @param guid
     *            The contact's GUID
     */
    void setGUID(final int guid) {
        _guid = guid;
    }

    /**
     * Sets the contact's first name.
     * 
     * @param first
     *            The contact's first name
     */
    void setFirst(final String first) {
        _first = first;
    }

    /**
     * Gets the contact's first name.
     * 
     * @return The contact's first name
     */
    String getFirst() {
        return _first;
    }

    /**
     * Sets the contact's last name.
     * 
     * @param last
     *            The contact's last name
     */
    void setLast(final String last) {
        _last = last;
    }

    /**
     * Gets the contact's last name.
     * 
     * @return The contact's last name
     */
    String getLast() {
        return _last;
    }

    /**
     * Sets the contact's email address.
     * 
     * @param email
     *            The contact's email address
     */
    void setEmail(final String email) {
        _email = email;
    }

    /**
     * Gets the contact's email address.
     * 
     * @return The contact's email address
     */
    String getEmail() {
        return _email;
    }

    /**
     * Sets the contact's phone number.
     * 
     * @param phone
     *            Sets the contact's phone number
     */
    void setPhone(final String phone) {
        _phone = phone;
    }

    /**
     * Gets the contact's phone number.
     * 
     * @return The contact's phone number
     */
    String getPhone() {
        return _phone;
    }

    /**
     * Determines equality by matching the GUIDs
     * 
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(final Object o) {
        if (o instanceof OTAContactData) {
            if (getGUID() == ((OTAContactData) o).getGUID()) {
                return true;
            }
        }

        return false;
    }
}
