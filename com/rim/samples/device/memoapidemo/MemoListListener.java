/*
 * MemoListListener.java
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

package com.rim.samples.device.memoapidemo;

import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;

import net.rim.blackberry.api.pdap.BlackBerryMemo;
import net.rim.blackberry.api.pdap.PIMListListener;

/**
 * Simple listener that prints status message to standard output when PIMList
 * events occur.
 */
/* package */final class MemoListListener implements PIMListListener {

    /**
     * Called when a memo is added to the memo list.
     * 
     * @param item
     *            The memo that was added.
     */
    public void itemAdded(final PIMItem item) {
        final StringBuffer buffer =
                new StringBuffer("The following memo was added:");
        addMemoInfo(buffer, item);
        System.out.println(buffer.toString());
    }

    /**
     * Called when a memo is removed from the memo list.
     * 
     * @param item
     *            The memo that was removed.
     */
    public void itemRemoved(final PIMItem item) {
        final StringBuffer buffer =
                new StringBuffer("The following memo was removed:");
        addMemoInfo(buffer, item);
        System.out.println(buffer.toString());
    }

    /**
     * Called when a memo is replaced with another memo.
     * 
     * @param oldItem
     *            The memo being replaced.
     * @param newItem
     *            The replacement memo.
     */
    public void itemUpdated(final PIMItem oldItem, final PIMItem newItem) {
        final StringBuffer buffer =
                new StringBuffer("The following memo was replaced:");
        addMemoInfo(buffer, oldItem);
        buffer.append("\n\n");
        buffer.append("The replacement memo is:");
        addMemoInfo(buffer, newItem);
        System.out.println(buffer.toString());
    }

    /**
     * Convenience method to add a memo's information to a string buffer.
     * 
     * @param buffer
     *            The string buffer to hold the memo's information.
     * @param memo
     *            The memo whose information is being added to the string
     *            buffer.
     */
    private void addMemoInfo(final StringBuffer buffer, final PIMItem memo) {
        final PIMList list = memo.getPIMList();

        if (memo.countValues(BlackBerryMemo.TITLE) > 0) {
            buffer.append("\n");
            buffer.append(list.getFieldLabel(BlackBerryMemo.TITLE) + ": ");
            buffer.append(memo.getString(BlackBerryMemo.TITLE, 0));
        }

        if (memo.countValues(BlackBerryMemo.UID) > 0) {
            buffer.append("\n");
            buffer.append(list.getFieldLabel(BlackBerryMemo.UID) + ": ");
            buffer.append(memo.getString(BlackBerryMemo.UID, 0));
        }

        if (memo.countValues(BlackBerryMemo.NOTE) > 0) {
            buffer.append("\n");
            buffer.append(list.getFieldLabel(BlackBerryMemo.NOTE) + ": ");
            buffer.append(memo.getString(BlackBerryMemo.NOTE, 0));
        }
    }
}
