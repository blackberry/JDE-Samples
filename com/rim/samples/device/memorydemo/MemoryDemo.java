/*
 * MemoryDemo.java
 * 
 * An application that demonstrates the use of best memory practices for thousands
 * of records.  Makes use of the LowMemoryManager to free up memory from stale data,
 * and groups objects using the ObjectGroup API.
 * 
 * This application handles data for a fictional business.  It keeps records for each
 * of the business's customers, and records for each order the business places with 
 * other businesses.
 * 
 * In order to see the effects of running the Low Memory Manager without having to 
 * exhaust one of Flash Memory, Object Handles, or Persistent Object Handles, three
 * menu items are provided that cause the same methods to execute as would the 
 * Low Memory Manager.  The menu items are Simulate LMM {Low|Medium|High}.
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

package com.rim.samples.device.memorydemo;

import java.util.Random;

import net.rim.device.api.ui.UiApplication;

/**
 * Main class for this application.
 */
public final class MemoryDemo extends UiApplication {
    // Statics
    // -------------------------------------------------------------------------------------
    private static Random _random; // For generating random numbers for dates
                                   // and strings.
    private static byte[] _letters; // For generating random strings.

    // Constants
    // -----------------------------------------------------------------------------------
    private static final int MIN_STRING_LENGTH = 3; // Minimum length for random
                                                    // string.
    private static final int MAX_STRING_LENGTH = 10; // Maximum length for
                                                     // random string.

    static {
        _random = new Random();
        _letters = new byte[MAX_STRING_LENGTH];
    }

    /**
     * This constructor simply pushes the main screen onto the display stack.
     */
    private MemoryDemo() {
        pushScreen(new MemoryDemoMainScreen());
    }

    /**
     * Entry point for the application.
     * 
     * @param args
     *            Command-line parameters (unused).
     */
    public static void main(final String[] args) {
        new MemoryDemo().enterEventDispatcher();
    }

    /**
     * Helper method that creates a random string of text.
     * 
     * @param minLength
     *            The minimum length for the random string.
     * @param maxLength
     *            The maximum length for the random string.
     * @return A random string.
     */
    public static String randomString() {
        final int length =
                randomIntBetween(MIN_STRING_LENGTH, MAX_STRING_LENGTH);

        for (int i = 0; i < length; ++i) {
            _letters[i] = (byte) randomIntBetween('a', 'z');
        }

        return new String(_letters, 0, length);
    }

    /**
     * Helper method that creates a random integer between provided lower and
     * upper thresholds.
     * 
     * @param low
     *            The lower threshold.
     * @param high
     *            The upper threshold.
     * @return The random integer.
     */
    public static int randomIntBetween(final int low, final int high) {
        final int range = high - low + 1;
        return Math.abs(_random.nextInt() % range) + low;
    }

    /**
     * Helper method that creates a random long between provided lower and upper
     * thresholds.
     * 
     * @param low
     *            The lower threshold.
     * @param high
     *            The upper threshold.
     * @return The random date.
     */
    public static long randomLongBetween(final long low, final long high) {
        final long range = high - low + 1;
        return Math.abs(_random.nextLong() % range) + low;
    }
}
