/**
 * PackageManager.java
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

package com.rim.samples.device.httpfilterdemo;

import net.rim.device.api.io.http.HttpFilterRegistry;
import net.rim.device.api.system.ControlledAccessException;

/**
 * The HTTP Filter Demo demonstrates implementation of custom protocols which
 * are registered with HttpFilterRegistry and associated with specific URLs.
 * 
 * The PackageManager class runs on device startup and registers the necessary
 * http filters.
 */
public final class PackageManager {
    /**
     * Entry point for this application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        try {
            HttpFilterRegistry.registerFilter("na.blackberry.com",
                    "com.rim.samples.device.httpfilterdemo.precanned", true);
            HttpFilterRegistry.registerFilter("www.rim.com",
                    "com.rim.samples.device.httpfilterdemo.filter");
        } catch (final ControlledAccessException cae) {
            // Re-throw exception with explicit message
            throw new ControlledAccessException(
                    cae
                            + " Http Filter Demo attempted to access API governed by Interactions/Browser Filtering "
                            + "application permission.  Please set this permission to 'allow' under Options/Security Options/Application Permissions");
        }
    }
}
