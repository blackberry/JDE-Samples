/*
 * UnifiedSearchDemoFileReader.java
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

package com.rim.samples.device.unifiedsearchdemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.unifiedsearch.searchables.SearchableContentTypeConstants;
import net.rim.device.api.unifiedsearch.searchables.SearchableContentTypeConstantsInfo;

/**
 * A class that reads data stored in a file
 */
public final class UnifiedSearchDemoFileReader {
    private static final String RECORD_SEPARATOR = "\r\n";
    private static final String FIELD_SEPARATOR = ",";
    private static final String CONTACT = "contact";
    private static final String LOCATION = "location";
    private static final String URL = "url";

    // Prevent instantiation
    private UnifiedSearchDemoFileReader() {
    }

    /**
     * Returns a Vector of data objects read from an InputStream
     * 
     * @param stream
     *            data read from file
     * @return a Vector of data objects
     * @throws IOException
     *             if reading from the given InputStream throws it or if the
     *             data read from the input stream has syntax errors
     */
    public static Vector getDataFromStream(final InputStream stream)
            throws IOException {
        final String data = readData(stream);

        if (data == null) {
            // The file is empty
            return null;
        } else {
            return buildDataList(data);
        }
    }

    /**
     * Reads data from an InputStream
     * 
     * @param stream
     *            Data read from file
     * @return A String containing the file's contents. Returns null if the file
     *         is empty.
     * @throws IOException
     *             if an I/O error occurs
     */
    private static String readData(final InputStream stream) throws IOException {
        final byte[] data = IOUtilities.streamToBytes(stream);

        return new String(data);
    }

    /**
     * Builds a Vector of data objects
     * 
     * @param rawData
     *            A string of data normally fetched from a file
     * @return A Vector of data objects
     * @throws IOException
     *             if a record is invalid
     */
    private static Vector buildDataList(final String rawData)
            throws IOException {
        final String[] records = split(rawData, RECORD_SEPARATOR);
        final Vector objects = new Vector();

        for (int i = 0; i < records.length; ++i) {
            if (records[i].length() > 0) {
                final String[] fields = split(records[i], FIELD_SEPARATOR);
                if (fields.length != 3) {
                    throw new IOException("Invalid record in file at line "
                            + (i + 1));
                }
                objects.addElement(new UnifiedSearchDemoDataObject(fields[0],
                        fields[1], getType(fields[2])));
            }
        }

        return objects;
    }

    /**
     * Returns the integer representation of the type. If the string is not
     * recognized, the default type is returned.
     * 
     * @param type
     *            The type of data
     * @return The integer representation of the type
     */
    private static long getType(final String type) {
        if (type.equals(CONTACT)) {
            return SearchableContentTypeConstants.CONTENT_TYPE_CONTACTS;
        }

        else if (type.equals(URL)) {
            return SearchableContentTypeConstants.CONTENT_TYPE_BROWSER;
        }

        else if (type.equals(LOCATION)) {
            return SearchableContentTypeConstants.CONTENT_TYPE_LOCATION;
        }

        else {
            return SearchableContentTypeConstantsInfo.getAllContentTypes();
        }
    }

    /**
     * Splits a string into tokens using a separator
     * 
     * @param original
     *            Given string
     * @param separator
     *            Token separator
     * @return Array of tokens
     */
    static final String[] split(String original, final String separator) {
        int index;
        final Vector tokens = new Vector();

        while ((index = original.indexOf(separator)) >= 0) {
            // Copy current token to vector
            tokens.addElement(original.substring(0, index).trim());

            // Remove copied data from the original string
            original = original.substring(index + separator.length());
        }

        tokens.addElement(original.trim());
        final String[] result = new String[tokens.size()];
        tokens.copyInto(result);

        return result;
    }
}
