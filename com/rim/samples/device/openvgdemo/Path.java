/*
 * Path.java
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

package com.rim.samples.device.openvgdemo;

import net.rim.device.api.openvg.VG10;
import net.rim.device.api.openvg.VG11;

/**
 * A class that encaspsulates a path read from a text file
 */
public class Path {
    private final int _path;
    private int _fill;

    /**
     * Creates a new Path object
     * 
     * @param path
     *            Path data read from text file
     */
    public Path(final int path) {
        _path = path;
    }

    /**
     * Sets the fill value to the specified color
     * 
     * @param fill
     *            Desired color to use
     */
    public void setFill(final int fill) {
        _fill = fill;
    }

    /**
     * Draws the path onto the display
     * 
     * @param vg
     *            Object used to render the path
     */
    public void draw(final VG11 vg) {
        final int paintModes = VG10.VG_FILL_PATH;

        // Set fill value
        vg.vgSetPaint(_fill, VG10.VG_FILL_PATH);

        // Draw the path onto the display
        vg.vgDrawPath(_path, paintModes);
    }
}
