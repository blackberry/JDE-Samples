/**
 * CountryList.java
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

package com.rim.samples.device.keywordfilterdemo;

import java.util.Vector;

import net.rim.device.api.collection.util.SortedReadableList;
import net.rim.device.api.ui.component.KeywordProvider;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

/**
 * Instances of this SortedReadableList class will contain a list of countries
 * derived from a vector of Country objects passed into the constructor. The
 * class is also a KeywordProvider implementation.
 */
class CountryList extends SortedReadableList implements KeywordProvider {
    // Constructor
    CountryList(final Vector countries) {
        super(new CountryListComparator());

        loadFrom(countries.elements());
    }

    /**
     * Adds a new element to the list.
     * 
     * @param element
     *            The element to be added.
     */
    void addElement(final Object element) {
        doAdd(element);
    }

    /**
     * @see net.rim.device.api.ui.component.KeywordProvider#getKeywords(Object
     *      element)
     */
    public String[] getKeywords(final Object element) {
        if (element instanceof Country) {
            return StringUtilities.stringToWords(element.toString());
        }
        return null;
    }

    /**
     * A Comparator class used for sorting our Country objects by name.
     */
    final static class CountryListComparator implements Comparator {

        public int compare(final Object o1, final Object o2) {
            if (o1.toString().compareTo(o2.toString()) < 0) {
                return -1;
            }
            if (o1.toString().compareTo(o2.toString()) > 0) {
                return 1;
            } else {
                return 0;
            }

        }
    }
}
