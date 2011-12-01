/*
 * UnifiedSearchDemoFileReader.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.unifiedsearchdemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.unifiedsearch.searchables.SearchableContentTypeConstants;

/**
 * A class that reads points of interest stored in a file
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
     * Returns a Vector of PointOfInterest objects read from an InputStream
     * 
     * @param stream
     *            POI data read from file
     * @return a Vector of PointOfInterest objects
     * @throws IOException
     *             if reading from the given InputStream throws it or if the
     *             data read from the input stream has syntax errors.
     */
    public static Vector getPoisFromStream(final InputStream stream)
            throws IOException {
        final String poiData = readPoiData(stream);

        if (poiData == null) {
            // The file is empty
            return null;
        } else {
            return buildPoiList(poiData);
        }
    }

    /**
     * Reads POI data from an InputStream
     * 
     * @param stream
     *            POI data read from file
     * @return A String containing the file's contents. Returns null if the file
     *         is empty.
     * @throws IOException
     *             if an I/O error occurs
     */
    private static String readPoiData(final InputStream stream)
            throws IOException {
        final byte[] data = IOUtilities.streamToBytes(stream);

        return new String(data);
    }

    /**
     * Builds a Vector of PointOfInterest objects
     * 
     * @param rawPoiData
     *            A string of POI data normally fetched from a file
     * @return A Vector of PointOfInterest objects
     * @throws IOException
     *             if a POI record is invalid
     */
    private static Vector buildPoiList(final String rawPoiData)
            throws IOException {
        final String[] poiRecords = split(rawPoiData, RECORD_SEPARATOR);
        final Vector newPois = new Vector();

        for (int i = 0; i < poiRecords.length; ++i) {
            if (poiRecords[i].length() > 0) {
                final String[] poiFields =
                        split(poiRecords[i], FIELD_SEPARATOR);
                if (poiFields.length != 3) {
                    throw new IOException("Invalid record in file at line "
                            + (i + 1));
                }
                newPois.addElement(new PointOfInterest(poiFields[0],
                        poiFields[1], getType(poiFields[2])));
            }
        }

        return newPois;
    }

    /**
     * Returns the integer representation of the type. If the string is not
     * recognized, the default type is returned.
     * 
     * @param type
     *            The type of the point of interest
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
            return SearchableContentTypeConstants.CONTENT_TYPE_DEFAULT_ALL;
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
