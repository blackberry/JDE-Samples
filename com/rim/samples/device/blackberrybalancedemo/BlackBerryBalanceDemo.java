/*
 * PersistentStoreDemo.java
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
import net.rim.device.api.system.MultiServicePlatformManager;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.ServiceMode;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * A sample application to demonstrate the BlackBerry Balance service. This
 * project is a simple GUI memo application in which memos are created and saved
 * in persistent store. Memos encapsulate a service mode property which is set
 * when the multi service framework suggests a relevant service mode for this
 * application to operate in. The application registers a
 * MultiServicePlatformListener with the framework so as to be notified when
 * controlled data should be wiped from the device.
 */
public final class BlackBerryBalanceDemo extends UiApplication {
    private static Vector _memos;
    private static PersistentObject _store;
    private static BlackBerryBalanceDemoScreen _screen;

    // com.rim.samples.device.blackberrybalancedemo = 0x58202aef09586a38L
    static final long BLACKBERRY_BALANCE_DEMO_ID = 0x58202aef09586a38L;

    /**
     * Entry point for application
     * 
     * @param args
     *            Command-line arguments (not used)
     */
    public static void main(final String[] args) {
        final BlackBerryBalanceDemo app = new BlackBerryBalanceDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new PersistentStoreDemo object
     */
    public BlackBerryBalanceDemo() {
        MultiServicePlatformManager.addListener(new MultiServiceListener(),
                MultiServicePlatformConstants.LOW_PRIORITY);

        // Retrieve the persistent object for this application
        _store =
                PersistentStore.getPersistentObject(BLACKBERRY_BALANCE_DEMO_ID);

        // Synchronize on the PersistentObject so that no other object can
        // acquire the lock before we finish our commit operation.
        synchronized (_store) {
            // If the PersistentObject is empty, initialize it
            if (_store.getContents() == null) {
                _store.setContents(new Vector());
            }
        }

        // Retrieve the saved Memo objects from the persistent store
        _memos = (Vector) _store.getContents();

        // Push the main screen onto the display stack
        _screen = new BlackBerryBalanceDemoScreen(_memos);
        pushScreen(_screen);
    }

    /**
     * Saves new or updated memo
     * 
     * @param memo
     *            The memo to be saved
     * @param index
     *            The memo's position in the collection. A value of -1
     *            represents a new meeting.
     */
    public void saveMemo(final Memo memo, final int index) {
        if (index >= 0) {
            _screen.getModel().removeRowAt(index);
            _screen.getModel().insertRowAt(index, memo);
        } else {
            _screen.getModel().addRow(memo);
        }
    }

    /**
     * Returns collection of Memo objects
     * 
     * @return A vector of Memo objects
     */
    public Vector getMemos() {
        return _memos;
    }

    /**
     * Commits the updated vector of Memo objects to the persistent store.
     */
    public void persist() {
        // Synchronize on the PersistentObject so that no other object can
        // acquire the lock before the commit operation is finished.
        synchronized (_store) {
            _store.setContents(_memos);
            PersistentObject.commit(_store);
        }
    }

    /**
     * @see net.rim.device.api.system.Application#suggestServiceMode(ServiceMode)
     */
    public boolean suggestServiceMode(final ServiceMode serviceMode) {
        // This particular application is only concerned with protecting data
        // that is flagged as corporate. Other implementations may want to
        // make a further distinction between personal/undecided data.
        if (!MultiServicePlatformManager.isCorporateServiceUid(serviceMode
                .getServiceUid())) {
            final Screen activeScreen = getActiveScreen();

            if (activeScreen instanceof MemoScreen) {
                return activeScreen.suggestServiceMode(serviceMode);
            }
        }

        return super.suggestServiceMode(serviceMode);
    }

    /**
     * @see net.rim.device.api.system.Application#getServiceMode()
     */
    public ServiceMode getServiceMode() {
        final Screen activeScreen = getActiveScreen();

        if (activeScreen instanceof MemoScreen) {
            return activeScreen.getServiceMode();
        }

        return super.getServiceMode();
    }

    /**
     * Called by the MultiServiceListener class when this application needs to
     * discard any controlled data. For testing purposes this method could be
     * called explicitly by the application. For a real world testing scenario
     * the running Blackberry device would be activated against a BES server and
     * a command to delete organization data would be issued by the BES
     * administrator.
     */
    public void onWipe(final String serviceUid, final long dataType) {
        final int size = _memos.size();
        for (int i = size - 1; i >= 0; --i) {
            final Memo memo = (Memo) _memos.elementAt(i);
            if (serviceUid.equals(memo.getMode())) {
                _screen.getModel().removeRowAt(i);
            }
        }

        final Screen activeScreen = getActiveScreen();

        if (activeScreen instanceof MemoScreen) {
            final Memo activeMemo = ((MemoScreen) activeScreen).getMemo();
            final int index = ((MemoScreen) activeScreen).getMemoIndex();

            if (index == -1 || !activeMemo.getMode().equals(serviceUid)) {
                // New memo with no wipable data, OK to save it.
                activeScreen.onClose();
            } else {
                final Dialog dialog =
                        new Dialog(
                                Dialog.D_OK,
                                "Application has been instructed to wipe corporate data."
                                        + " This memo will be deleted and this screen closed.",
                                0, null, 0);
                dialog.doModal();
                this.popScreen(activeScreen);
            }
        }

        persist();

        MultiServicePlatformManager.removeListener(new MultiServiceListener());
    }
}
