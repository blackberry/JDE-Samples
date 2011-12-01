/*
 * Country.java
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

/**
 * A class to encapsulate data related to a given country of the world.
 */
class Country {
    private final String _countryName;
    private final String _population;
    private final String _capitalCity;

    // Constructor
    Country(final String countryName, final String population,
            final String capitalCity) {
        _countryName = countryName;
        _population = population;
        _capitalCity = capitalCity;
    }

    // Accessor methods---------------------------------------------------------

    String getPopulation() {
        return _population;
    }

    String getCapitalCity() {
        return _capitalCity;
    }

    public String toString() {
        return _countryName;
    }
}
