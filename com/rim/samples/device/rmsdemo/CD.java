/*
 * CD.java
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

package com.rim.samples.device.rmsdemo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;

/**
 * The CD class. The toByteArray() and fromByteArray() methods are required
 * because the RMS that serves as the backend to the CDdb class stores each
 * record as a byte array. The converted byte array is in tag-length format to
 * allow the scalability of this class while still allowing the CDdb to store
 * and read previous versions of the CD class.
 */
public class CD {
    private String _artist;
    private String _title;
    private String _fullName;

    private static final short ARTIST = 0;
    private static final short TITLE = 1;

    /**
     * Constructor for the URL object, given byte array.
     * 
     * @param data
     *            Byte array for the CD.
     * @exception java.io.IOException
     *                IO error.
     */
    public CD(final byte[] data) throws java.io.IOException {
        fromByteArray(data);
    }

    /**
     * Constructor for the URL object, given protocol and path.
     * 
     * @param artist
     *            The name of the CD artist.
     * @param title
     *            The title of the CD.
     */
    public CD(final String artist, final String title) {
        _artist = artist;
        _title = title;
        _fullName = _artist + ": " + _title;
    }

    /**
     * Provide the CD in the form of a String.
     * 
     * @return The CD as a properly formed String.
     */
    public String toString() {
        return _fullName;
    }

    /**
     * Gets the artist attribute of the CD object.
     * 
     * @return The arist name of the CD.
     */
    public String getArtist() {
        return _artist;
    }

    /**
     * Gets the title attribute of the CD object.
     * 
     * @return The title of the CD.
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Convert a CD to a byte array.
     * 
     * @return Byte encoded version of the CD.
     * @exception java.io.IOException
     *                IO error.
     */
    public byte[] toByteArray() throws java.io.IOException {
        byte[] data;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);

        dos.writeShort(ARTIST);
        dos.writeUTF(_artist);
        dos.writeShort(TITLE);
        dos.writeUTF(_title);

        data = baos.toByteArray();

        return data;
    }

    /**
     * Convert a byte array to a CD.
     * 
     * @param array
     *            CD encoded as a byte array.
     * @exception java.io.IOException
     *                IO error.
     */
    private void fromByteArray(final byte[] array) throws java.io.IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(array);
        final DataInputStream dis = new DataInputStream(bais);

        short tag;
        final int length;

        try {
            while (true) {
                tag = dis.readShort();

                if (tag == ARTIST) {
                    _artist = dis.readUTF();
                } else if (tag == TITLE) {
                    _title = dis.readUTF();
                } else {
                    // Unrecognized tag, skip value.
                    dis.readUTF();
                }
            }
        } catch (final EOFException e) {
            _fullName = _artist + ": " + _title;
        }
    }
}
