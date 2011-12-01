/*
 * MultiServiceListener.java
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

package com.rim.samples.device.blackberrybalancedemo;

import java.util.Vector;

import net.rim.device.api.system.MultiServicePlatformConstants;
import net.rim.device.api.system.MultiServicePlatformListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.UiApplication;

/**
 * This implementation of MultiServiceListener checks the persistent store for
 * data stored by the BlackBerry Balance Demo application that needs to be
 * deleted (wiped) from the device.
 */
public final class MultiServiceListener implements MultiServicePlatformListener {
    /**
     * @see net.rim.device.api.system.MultiServicePlatformListener#wipe(String,
     *      long)
     */
    public int wipe(final String serviceUid, final long dataType) {
        final UiApplication uiApp = UiApplication.getUiApplication();

        if (uiApp instanceof BlackBerryBalanceDemo) {
            // Take care of any in memory data first
            ((BlackBerryBalanceDemo) uiApp).onWipe(serviceUid, dataType);
        }

        // Retrieve the persistent object for this application
        final PersistentObject store =
                PersistentStore
                        .getPersistentObject(BlackBerryBalanceDemo.BLACKBERRY_BALANCE_DEMO_ID);

        // Retrieve the saved Memo objects from the persistent store
        final Vector memos = (Vector) store.getContents();

        final int size = memos.size();

        for (int i = 0; i < size; ++i) {
            final Memo memo = (Memo) memos.elementAt(i);

            if (serviceUid.equals(memo.getMode())) {
                // Wipe the memo
                memos.removeElementAt(i);
            }
        }

        synchronized (store) {
            // Persist the updated collection
            store.setContents(memos);
            PersistentObject.commit(store);
        }

        return MultiServicePlatformConstants.SUCCESS;
    }
}
