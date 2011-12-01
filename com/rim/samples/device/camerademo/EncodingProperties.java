/*
 * Encoding.java
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

package com.rim.samples.device.camerademo;

/**
 * A wrapper for the various encoding properties available for use with the
 * VideoControl.getSnapshot() method.
 */
public final class EncodingProperties {
    /** The file format of the picture */
    private String _format;

    /** The width of the picture */
    private String _width;

    /** The height of the picture */
    private String _height;

    /** Booleans that indicate whether the values have been set */
    private boolean _formatSet;
    private boolean _widthSet;
    private boolean _heightSet;

    /**
     * Set the file format to be used in snapshots
     * 
     * @param format
     *            The file format to be used in snapshots
     */
    public void setFormat(final String format) {
        _format = format;
        _formatSet = true;
    }

    /**
     * Set the width to be used in snapshots
     * 
     * @param width
     *            The width to be used in snapshots
     */
    void setWidth(final String width) {
        _width = width;
        _widthSet = true;
    }

    /**
     * Set the height to be used in snapshots
     * 
     * @param height
     *            The height to be used in snapshots
     */
    void setHeight(final String height) {
        _height = height;
        _heightSet = true;
    }

    /**
     * Return the encoding as a coherent String to be used in menus
     * 
     * @see Object#toString()
     */
    public String toString() {
        final StringBuffer display = new StringBuffer();

        display.append(_width);
        display.append(" x ");
        display.append(_height);
        display.append(" ");
        display.append(_format);

        return display.toString();
    }

    /**
     * Return the encoding as a properly formatted string to be used by the
     * VideoControl.getSnapshot() method.
     * 
     * @return The encoding expressed as a formatted string.
     */
    String getFullEncoding() {
        final StringBuffer fullEncoding = new StringBuffer();

        fullEncoding.append("encoding=");
        fullEncoding.append(_format);

        fullEncoding.append("&width=");
        fullEncoding.append(_width);

        fullEncoding.append("&height=");
        fullEncoding.append(_height);

        return fullEncoding.toString();
    }

    /**
     * Checks whether all the fields been set
     * 
     * @return true if all fields have been set.
     */
    boolean isComplete() {
        return _formatSet && _widthSet && _heightSet;
    }
}
