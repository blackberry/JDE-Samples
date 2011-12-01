/*
 * PointOfInterest.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.unifiedsearchdemo;

/**
 * Encapsulates information related to points of interest. Used primarily to
 * provide searchable information without having to obtain it from other
 * sources.
 */
public class PointOfInterest {
    private final String _name;
    private final String _data;
    private final long _type;

    /**
     * Creates a new PointOfInterest object
     * 
     * @param name
     *            Name of the point of interest
     * @param data
     *            Data for the point of interest
     * @param type
     *            Type of the point of interest
     */
    public PointOfInterest(final String name, final String data, final long type) {
        _name = name;
        _data = data;
        _type = type;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(getName()).append(", ").append(getData());
        return buffer.toString();
    }

    /**
     * Returns the name of the point of interest
     * 
     * @return the name of the point of interest
     */
    public String getName() {
        return _name;
    }

    /**
     * Returns the data for the point of interest
     * 
     * @return the data for the point of interest
     */
    public String getData() {
        return _data;
    }

    /**
     * Returns the type of the point of interest
     * 
     * @return the type of the point of interest
     */
    public long getType() {
        return _type;
    }
}
