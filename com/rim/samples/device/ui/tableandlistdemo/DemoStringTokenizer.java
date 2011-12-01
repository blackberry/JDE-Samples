/*
 * DemoStringTokenizer.java
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

package com.rim.samples.device.ui.tableandlistdemo;

/**
 * A class which encapsulates and parses a String delimited by commas
 */
public class DemoStringTokenizer {
    private int _currentPosition;
    private final int _maxPosition;
    private final String _str;
    private final String _delimiter;

    /**
     * Creates a new DemoStringTokenizer object
     * 
     * @param str
     *            The string to be parsed
     */
    public DemoStringTokenizer(final String str) {
        // Initialize members
        _currentPosition = 0;
        _str = str;
        _delimiter = ",";
        _maxPosition = str.length();
    }

    /**
     * Tests whether there are more tokens available from this tokenizer's
     * string
     * 
     * @return True if there is at least one token in the string after the
     *         current position, otherwise false
     */
    boolean hasMoreTokens() {
        return _currentPosition < _maxPosition;
    }

    /**
     * Returns the next token from this string tokenizer
     * 
     * @return The next token from this string tokenizer or null if there are no
     *         more tokens
     */
    String nextToken() {
        if (_currentPosition >= _maxPosition) {
            return null;
        }
        final int start = _currentPosition;

        _currentPosition = _str.indexOf(_delimiter, start);

        if (_currentPosition == -1) {
            _currentPosition = _maxPosition;
        }

        _currentPosition += 1;

        return _str.substring(start, _currentPosition - 1);
    }
}
