/*
 * Meeting.java
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

package com.rim.samples.device.persistentstoredemo;

import java.util.Vector;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;

/**
 * This class represents a persistable meeting object. It contains information
 * such as the name of the meeting, a description, date and time as well as
 * names of those who were in attendance. This information is encoded and stored
 * in a pair of Vectors, _fields and _attendees. Classes to be persisted must
 * implement interface Persistable and can only can contain members which
 * themselves implement Persistable or are inherently persistable.
 */
public final class Meeting implements Persistable {
    static final int MEETING_NAME = 0;
    static final int DESC = 1;
    static final int DATE = 2;
    static final int TIME = 3;
    static final int NOTES = 4;

    // Change this value if any fields are added to or removed from this class
    private static final int NUM_FIELDS = 5;

    private final Vector _fields;
    private final Vector _attendees;

    // Primitive data types can be persisted. The following class members are
    // included for demonstration purposes only, they have no functional use in
    // this class.
    private int demoInt;
    private boolean demoBool;
    private byte demoByte;
    private short demoShort;
    private long demoLong;
    private float demoFloat;
    private double demoDouble;
    private char demoChar;

    /**
     * Creates a new Meeting object
     */
    public Meeting() {
        _attendees = new Vector();
        _fields = new Vector(NUM_FIELDS);
        for (int i = 0; i < NUM_FIELDS; ++i) {
            _fields.addElement("");
        }
    }

    /**
     * Retrieves an encoded object and returns it as a plaintext string.
     * 
     * @param id
     *            The ID of the field from which the encoding should be
     *            retrieved
     * @return A plaintext string
     */
    String getField(final int id) {
        final Object encoding = _fields.elementAt(id);

        // Acquiring a reference to a ticket guarantees access to encrypted data
        // even if the device locks during the decoding operation.
        final Object ticket = PersistentContent.getTicket();

        if (ticket != null) {
            return PersistentContent.decodeString(encoding);
        } else {
            return null;
        }
    }

    /**
     * Stores a string as an encoded object according to device content
     * protection/compression settings.
     * 
     * @param id
     *            The ID of the field where the encoding is to be stored
     * @param value
     *            The plaintext string to be encoded and stored
     */
    void setField(final int id, final String value) {
        final Object encoding = PersistentContent.encode(value);
        _fields.setElementAt(encoding, id);
    }

    /**
     * Encodes a string and adds it to the attendees vector
     * 
     * @param attendee
     *            String to be added to the attendees vector
     */
    void addAttendee(final String attendee) {
        final Object encoding = PersistentContent.encode(attendee);
        _attendees.addElement(encoding);
    }

    /**
     * Returns a vector containing all attendees.
     * 
     * @return Vector of decoded strings
     */
    Vector getAttendees() {
        final Object encoding;
        final Vector decodedAttendees = new Vector();

        // Acquiring a reference to a ticket guarantees access to encrypted data
        // even if the device locks during the decoding operation operation.
        final Object ticket = PersistentContent.getTicket();

        if (ticket != null) {
            for (int i = 0; i < _attendees.size(); i++) {
                decodedAttendees.addElement(PersistentContent
                        .decodeString(_attendees.elementAt(i)));
            }
        }
        return decodedAttendees;
    }

    /**
     * Forces a re-encoding of the information stored in this Meeting object.
     */
    void reEncode() {
        // Acquiring a reference to a ticket guarantees access to encrypted data
        // even if the device locks during the re-encoding operation.
        final Object ticket = PersistentContent.getTicket();

        if (ticket != null) {
            for (int i = 0; i < NUM_FIELDS; ++i) {
                Object encoding = _fields.elementAt(i);
                if (!PersistentContent.checkEncoding(encoding)) {
                    encoding = PersistentContent.reEncode(encoding);
                    _fields.setElementAt(encoding, i);
                }
            }
        }
    }
}
