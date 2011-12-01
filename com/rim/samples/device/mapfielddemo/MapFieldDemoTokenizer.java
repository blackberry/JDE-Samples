/*
 * MapFieldDemoTokenizer.java
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

package com.rim.samples.device.mapfielddemo;

import javax.microedition.location.Coordinates;

/**
 * MapFieldDemoTokenizer takes care of the string manipulations needed in
 * MapFieldDemo. It has the ability to retrieve ints, doubles, Strings and
 * Coordinates from a byte array.
 */
class MapFieldDemoTokenizer {

    /**
     * Converts a byte array into its corresponding int value.
     * 
     * @param line
     *            The byte array to be converted.
     * @return The converted integer.
     */
    static int getInt(final byte[] line) {
        final boolean isNegative = line[0] == '-';
        final int arrayLength = getEffectiveLength(line);
        int power = arrayLength - 1;
        int number = 0;

        // Places a particular number in its correct position.
        for (int count = isNegative ? 1 : 0; count < arrayLength; count++) {
            number += (line[count] - 48) * pow(10, power--);
        }

        return number;
    }

    /**
     * Converts a byte array into its corresponding double value.
     * 
     * @param line
     *            The byte array to be converted.
     * @return The converted double.
     */
    private static double getDouble(final byte[] line) {
        final boolean isNegative = line[0] == '-';
        final int arrayLength = getEffectiveLength(line);
        int power;
        double number = 0;

        // About decimal point.
        int countBeforeDecimal = 0;
        int positionAfterDecimal = 0;

        // Counts the number of place before the decimal.
        for (int count = 0; count < arrayLength; count++) {
            if (line[count] != '.') {
                countBeforeDecimal++;
            } else {
                break;
            }
        }

        // Places a particular number in it's correct position.
        if (isNegative) {
            power = countBeforeDecimal - 2;
        } else {
            power = countBeforeDecimal - 1;
        }
        for (int count = isNegative ? 1 : 0; count < countBeforeDecimal; count++) {
            number += (line[count] - 48) * pow(10, power--);
        }

        // Places the decimal numbers in their positions.
        if (arrayLength > countBeforeDecimal) {
            for (int count = countBeforeDecimal + 1; count < arrayLength; count++) {
                number +=
                        (line[count] - 48) * pow(10.0, -++positionAfterDecimal);
            }
        }

        if (isNegative) {
            return number *= -1;
        } else {
            return number;
        }
    }

    /**
     * Converts a byte array into its corresponding String value.
     * 
     * @param line
     *            The byte array to be converted.
     * @return The converted String.
     */
    static String getString(final byte[] line) {
        final int arrayLength = getEffectiveLength(line);

        // If there is no white space, return the string equivalent of 'line'.
        if (arrayLength == line.length) {
            return new String(line);
        }

        // If there is white space, return truncated version of 'line'.
        final byte[] newByte = new byte[arrayLength];

        for (int count = 0; count < arrayLength; count++) {
            newByte[count] = line[count];
        }

        return new String(newByte);
    }

    /**
     * Converts a byte array into its corresponding Coordinates value.
     * <p>
     * Note that the array must have two double values separated by a space in
     * order to yield correct results.
     * 
     * @param line
     *            The byte array to be converted.
     * @return The converted Coordinates.
     */
    static Coordinates getCoordinates(final byte[] line) {
        double latitude, longitude;
        byte[] partialLine;
        int locationOfSpace;
        final int arrayLength = getEffectiveLength(line);

        for (locationOfSpace = 0; locationOfSpace < arrayLength; locationOfSpace++) {
            if (line[locationOfSpace] == ' ') {
                break;
            }
        }

        // Separates the first number in the line and gets it's value.
        partialLine = new byte[locationOfSpace];
        for (int count = 0; count < locationOfSpace; count++) {
            partialLine[count] = line[count];
        }

        latitude = getDouble(partialLine);

        // Gets the value of the second number.
        partialLine = new byte[arrayLength - locationOfSpace - 1];
        for (int count = locationOfSpace + 1; count < arrayLength; count++) {
            partialLine[count - locationOfSpace - 1] = line[count];
        }

        longitude = getDouble(partialLine);

        return new Coordinates(latitude, longitude, 0);
    }

    /**
     * Determines the value of an exponential equation.
     * 
     * @param base
     *            The base of the exponential equation.
     * @param exponent
     *            The exponent of the exponential equation.
     * @return The value exponential equation.
     */
    private static double pow(final double base, final int exponent) {
        int result = 0;

        if (exponent == 0) {
            return 1;
        }

        if (exponent == 1) {
            return base;
        }

        if (exponent == -1) {
            return 1.0 / base;
        }

        for (int count = Math.abs(exponent); count > 1; count--) {
            if (result == 0) {
                result += base * base;
            } else {
                result *= base;
            }
        }

        if (exponent < 0) {
            return 1.0 / result;
        }

        return result;
    }

    /**
     * Returns the number of elements in the array minus trailing white space.
     * 
     * @param array
     *            Array to determine length of.
     * @return The effective length of the array.
     */
    private static int getEffectiveLength(final byte[] array) {
        int arrayLength;

        for (arrayLength = array.length - 1; arrayLength >= 0; arrayLength--) {
            if (array[arrayLength] != 32) {
                break;
            }
        }

        return arrayLength + 1;
    }
}
