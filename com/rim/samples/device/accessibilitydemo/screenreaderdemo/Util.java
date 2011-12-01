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

package com.rim.samples.device.accessibilitydemo.screenreaderdemo;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.accessibility.AccessibleState;

/**
 * A class containing utitlity methods for the Screen Reader
 */
class Util {
    /**
     * Determines whether state has transitioned to state represented by the
     * state flag value
     * 
     * @param oldStateSet
     *            The previous state
     * @param newStateSet
     *            The new state
     * @param stateFlag
     *            The state to check against
     * @return True if parameters indicate a change to the state represented by
     *         the state flag, otherwise false
     */
    static boolean hasTransitionedToState(final int oldStateSet,
            final int newStateSet, final int stateFlag) {
        return (newStateSet & ~oldStateSet & stateFlag) != 0;
    }

    /**
     * Determines whether state has transitioned from state represented by the
     * state flag value
     * 
     * @param oldStateSet
     *            The previous state
     * @param newStateSet
     *            The new state
     * @param stateFlag
     *            The state to check against
     * @return True if parameters indicate a change from the state represented
     *         by the state flag, otherwise false
     */
    static boolean hasTransitionedFromState(final int oldStateSet,
            final int newStateSet, final int stateFlag) {
        return (~newStateSet & oldStateSet & stateFlag) != 0;
    }

    /**
     * This method is the "voice" of the screen reader. Obviously, a bona fide
     * reader would actually output audio. In this case, we simply use the
     * console output to show the information that should be output.
     * 
     * @param text
     *            The text to be "spoken"
     */
    static void speak(final String text) {
        if (text.length() == 0) {
            return;
        }

        System.out.println("---------------- SOUND: " + text);
    }

    /**
     * Determines whether an accessible component supports vertical or
     * horizontal navigation or both.
     * 
     * @param stateSet
     *            Flag containing bits representing supported orientation(s)
     * @return <description>
     */
    static String getOrientation(final int stateSet) {
        final boolean horizontal = (stateSet & AccessibleState.HORIZONTAL) != 0;
        final boolean vertical = (stateSet & AccessibleState.VERTICAL) != 0;

        if (horizontal && vertical) {
            return "both way navigation ";
        } else if (vertical) {
            return "vertical ";
        } else if (horizontal) {
            return "horizontal ";
        } else {
            return "";
        }
    }

    /**
     * Provides a string literal given a date sub-field type
     * 
     * @param subFieldType
     *            Date sub-field type for which to provide a string description
     * @return A string representation of the date sub-field type.
     */
    static String getDateSubfieldString(final int subFieldType) {
        switch (subFieldType) {
        case DateFormat.AM_PM_FIELD:
            return "AM PM field";
        case DateFormat.DATE_FIELD:
            return "Day of month field";
        case DateFormat.DAY_OF_WEEK_FIELD:
            return "Day of week field";
        case DateFormat.HOUR_FIELD:
            return "Hour field";
        case DateFormat.HOUR_OF_DAY_FIELD:
            return "Hour of day field";
        case DateFormat.MINUTE_FIELD:
            return "Minute field";
        case DateFormat.MONTH_FIELD:
            return "Month field";
        case DateFormat.SECOND_FIELD:
            return "Second field";
        case DateFormat.YEAR_FIELD:
            return "Year field";
        default:
            return "Unknown date subfield";
        }
    }
}
