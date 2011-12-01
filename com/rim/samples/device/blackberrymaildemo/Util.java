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

package com.rim.samples.device.blackberrymaildemo;

import java.util.Date;

import net.rim.blackberry.api.mail.Message;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Characters;
import net.rim.device.api.util.Comparator;

/**
 * This class acts as a utility class for the BlackBerry Mail Demo. The class
 * provides functionality to retrieve an icon associated with a message based on
 * the message status, format a Date in String format, and sort messages in
 * reverse chronological order.
 */
public final class Util {
    /**
     * Retrieves an icon associated with a message status
     * 
     * @param message
     *            The message for which to retrieve an icon respresenting its
     *            status
     * @return The character icon associated with the mesage status, ' ' if none
     *         found
     * 
     */
    public static char getStatusIcon(final Message message) {
        char icon = ' ';

        switch (message.getStatus()) {
        case Message.Status.TX_COMPOSING:
            icon = Characters.BALLOT_BOX;
        case Message.Status.TX_COMPRESSING:
            icon = Characters.BLACK_DOWN_POINTING_SMALL_TRIANGLE;
        case Message.Status.TX_ENCRYPTING:
            icon = Message.Icons.TX_ENCRYPTING;
        case Message.Status.TX_PENDING:
            icon = Characters.BLACK_RIGHT_POINTING_POINTER;
        case Message.Status.TX_SENDING:
            icon = Characters.BLACK_RIGHT_POINTING_POINTER;
        case Message.Status.TX_SENT:
            icon = Characters.BALLOT_BOX_WITH_CHECK;
        case Message.Status.TX_DELIVERED:
            icon = Characters.BALLOT_BOX_WITH_CHECK;
        case Message.Status.TX_READ:
            icon = Characters.CHECK_MARK;
        case Message.Status.TX_ERROR:
            icon = Characters.BALLOT_X;
        case Message.Status.TX_GENERAL_FAILURE:
            icon = Characters.BALLOT_X;
        case Message.Status.RX_ERROR:
            icon = Characters.BALLOT_X;
        case Message.Status.RX_RECEIVED:
            icon = Characters.ENVELOPE;
        }
        return icon;
    }

    /**
     * A utility function to render dates into string form using the
     * DateFormat.DATE_DEFAULT style.
     * 
     * @param date
     *            A date instance
     * @return The String representation of the provided date, in the specified
     *         style
     */
    public static String getDateAsString(final Date date) {
        return getDateAsString(date, DateFormat.DATE_DEFAULT);
    }

    /**
     * A utility function to render dates into string form
     * 
     * @param date
     *            A date instance
     * @param style
     *            One of the DateFormat styles (@see
     *            net.rim.device.api.i18n.DateFormat)
     * @return The String representation of the provided date, in the specified
     *         style
     */
    public static String getDateAsString(final Date date, final int style) {
        String dateString = null;

        if (date != null) {
            final DateFormat dateFormat = DateFormat.getInstance(style);
            dateString = dateFormat.formatLocal(date.getTime());
        }

        return dateString;
    }

    // Compares in terms of most recent 'Sent' date
    public final static Comparator SORT_BY_MOST_RECENT_DATE = new Comparator() {
        public int compare(final Object first, final Object second) {
            if (!(first instanceof Message) || !(second instanceof Message)) {
                throw new IllegalArgumentException(
                        "Arguments to compare must be a Message");
            }

            final Date firstDate = ((Message) first).getSentDate();
            final Date secondDate = ((Message) second).getSentDate();

            // Deal with the null case by setting the time to Long.MAX_VALUE,
            // ensuring a null date will never be less than a non-null date.
            long firstTime;
            if (firstDate != null) {
                firstTime = firstDate.getTime();
            } else {
                firstTime = Long.MAX_VALUE;
            }

            long secondTime;
            if (secondDate != null) {
                secondTime = secondDate.getTime();
            } else {
                secondTime = Long.MAX_VALUE;
            }

            // Compare the times by most recent
            if (firstTime < secondTime) {
                return 1;
            } else if (firstTime > secondTime) {
                return -1;
            }
            return 0;
        }
    };

    // Default constructor
    public Util() {
    }
}
