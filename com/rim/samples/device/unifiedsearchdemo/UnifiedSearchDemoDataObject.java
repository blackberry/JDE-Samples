/*
 * UnifiedSearchDemoDataObject.java
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

/**
 * Encapsulates information related to various data types. Used primarily to
 * provide searchable information without having to obtain it from other
 * sources.
 */
public class UnifiedSearchDemoDataObject {
    private final String _name;
    private final String _data;
    private final long _type;

    /**
     * Creates a new UnifiedSearchDemoDataObject object
     * 
     * @param name
     *            Name of the data object
     * @param data
     *            Data for the object
     * @param type
     *            Type of data
     */
    public UnifiedSearchDemoDataObject(final String name, final String data,
            final long type) {
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
     * Returns the name of the data object
     * 
     * @return the name of the data object
     */
    public String getName() {
        return _name;
    }

    /**
     * Returns the data for this object
     * 
     * @return the data for this object
     */
    public String getData() {
        return _data;
    }

    /**
     * Returns this object's type
     * 
     * @return This object's type
     */
    public long getType() {
        return _type;
    }
}
