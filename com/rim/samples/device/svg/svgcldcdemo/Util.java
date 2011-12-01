/*
 * Util.java
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

package com.rim.samples.device.svg.svgcldcdemo;

import net.rim.device.api.system.Display;

/**
 * Utility class to convert the default hard-coded attribute values for the 9000
 * device to values that will render the SVG images properly on the current
 * device.
 */
public final class Util {
    private static final float DISPLAY_WIDTH_9000 = 480.0f;
    private static final float DISPLAY_HEIGHT_9000 = 320.0f;

    /**
     * Converts the hard-coded attributes intended for the 9000 device to values
     * for the current device.
     * 
     * @param toConvert
     *            The attributes to convert
     * @return The adjusted attributes (unchanged if this device is 9000)
     */
    public static float[] convertDefaultValue(final float[] toConvert) {
        final float currentDisplayWidth = Display.getWidth();

        if (currentDisplayWidth == DISPLAY_WIDTH_9000) {
            // Current device is 9000, no need to convert.
            return toConvert;
        }

        final float[] converted = new float[toConvert.length];
        for (int i = 0; i < toConvert.length; i++) {
            converted[i] = convertDefaultValue(toConvert[i], true);
        }

        return converted;
    }

    /**
     * Adjusts the default value for the 9000 device a value which match the
     * current device.
     * 
     * @param toConvert
     *            The value to convert
     * @param relativeToWidth
     *            True if the coverted value should be based on the relative
     *            width, otherwise the value will be converted based on relative
     *            height.
     * @return The converted value
     */
    public static float convertDefaultValue(final float toConvert,
            final boolean relativeToWidth) {
        final float currentDisplayWidth = Display.getWidth();
        final float currentDisplayHeight = Display.getHeight();

        float ratio = 0;
        float newAbsolute = 0;

        if (relativeToWidth) {
            ratio = toConvert / DISPLAY_WIDTH_9000;
            newAbsolute = ratio * currentDisplayWidth;
        } else {
            // Relative to height.
            ratio = toConvert / DISPLAY_HEIGHT_9000;
            newAbsolute = ratio * currentDisplayHeight;
        }

        return newAbsolute;
    }
}
