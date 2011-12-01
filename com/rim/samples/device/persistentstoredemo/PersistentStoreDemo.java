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

package com.rim.samples.device.persistentstoredemo;

import java.util.Vector;

import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

/**
 * A sample application to demonstrate persistence and content encryption. The
 * application allows a user to save Meeting objects which contain information
 * such as date, time and names of those in attendance. An additional GUI screen
 * allows existing meetings to be edited and re-saved. This application does not
 * allow for deletion of attendees.
 * 
 * The constructor for this class contains code to demonstrate the concept of
 * protecting an object stored in the persistent store with a code signing key.
 * To fully demonstrate this feature, the application will need to be run on a
 * secure BlackBerry device or simulator. Because this application leverages
 * controlled API's, it will need to be signed with the appropriate keys. See
 * the Blackberry Signature Tool Development Guide for more information on this
 * subject. You will also need to use the BlackBerry Signing Authority Admin
 * Tool to create a public/private key pair with the name "ACME". See the
 * BlackBerry Signing Authority Tool Administrator Guide for more information.
 * Replace the ACME public key contained in this project with the ACME public
 * key created with the BlackBerry Signing Authority Admin Tool. Build the
 * project and then use the BlackBerry Signing Authority Tool to sign the
 * resulting cod file with the ACME private key. When the
 * "Access controlled object" menu item code in the PersistentStoreDemoScreen
 * class is run, the module will be granted access to the controlled object by
 * virtue of the fact that it is signed with the ACME key.
 */
public final class PersistentStoreDemo extends UiApplication implements
        ListFieldCallback {
    private final Vector _meetings;
    private final PersistentObject _store;
    private final PersistentStoreDemoScreen _screen;

    // com.rim.samples.device.persistentstoredemo = 0x220d57d6848faeffL
    static final long PERSISTENT_STORE_DEMO_ID = 0x220d57d6848faeffL;

    // com.rim.samples.device.persistentstoredemo.PERSISTENT_STORE_DEMO_CONTROLLED_ID
    // = 0xbf768b0f3ae726daL
    static final long PERSISTENT_STORE_DEMO_CONTROLLED_ID = 0xbf768b0f3ae726daL;

    /**
     * Entry point for application
     * 
     * @param args
     *            Command-line arguments
     */
    public static void main(final String[] args) {
        if (args != null && args.length > 0 && args[0].equals("startup")) {
            final PersistentObject store =
                    PersistentStore
                            .getPersistentObject(PERSISTENT_STORE_DEMO_ID);

            // Synchronize on the PersistentObject so that no other object can
            // acquire the lock before we finish our commit operation.
            synchronized (store) {
                // If the PersistentObject is empty, initialize it
                if (store.getContents() == null) {
                    store.setContents(new Vector());
                    PersistentObject.commit(store);
                }
            }

            // Register a PersistentContentListener on device start-up.
            // The listener listens for changes to the device content
            // protection and compression settings as well as persistent
            // content state changes.
            PersistentContent.addListener(new PersistentStoreListener());
        } else {
            // Launch GUI version of the application.
            final PersistentStoreDemo theApp = new PersistentStoreDemo();

            // Make the currently running thread the application's event
            // dispatch thread and begin processing events.
            theApp.enterEventDispatcher();
        }
    }

    /**
     * Creates a new PersistentStoreDemo object
     */
    public PersistentStoreDemo() {
        // Persist an object protected by a code signing key. Please see
        // instructions above.
        final PersistentObject controlledStore =
                PersistentStore
                        .getPersistentObject(PERSISTENT_STORE_DEMO_CONTROLLED_ID);
        synchronized (controlledStore) {
            final CodeSigningKey codeSigningKey =
                    CodeSigningKey.get(CodeModuleManager
                            .getModuleHandle("PersistentStoreDemo"), "ACME");
            controlledStore.setContents(new ControlledAccess(new Vector(),
                    codeSigningKey));
            PersistentObject.commit(controlledStore);
        }

        // Retrieve the persistent object for this application
        _store = PersistentStore.getPersistentObject(PERSISTENT_STORE_DEMO_ID);

        // Retrieve the saved Meeting objects from the persistent store
        _meetings = (Vector) _store.getContents();

        // Create the main screen for the application and push it onto the UI
        // stack for rendering.
        _screen = new PersistentStoreDemoScreen(_meetings);
        pushScreen(_screen);
    }

    /**
     * Called by MeetingScreen. Saves new or updated meeting and refreshes the
     * list of meetings.
     * 
     * @param meeting
     *            The meeting to be saved
     * @param index
     *            The meeting's position in the _meetings Vector. A value of -1
     *            represents a new meeting.
     */
    void saveMeeting(final Meeting meeting, final int index) {
        if (index >= 0) {
            _meetings.setElementAt(meeting, index);
        } else {
            _meetings.addElement(meeting);
        }
        _screen.updateList();
    }

    /**
     * Returns collection of Meeting objects
     * 
     * @return A vector of Meeting objects
     */
    Vector getMeetings() {
        return _meetings;
    }

    /**
     * Commits the updated vector of Meeting objects to the persistent store.
     */
    void persist() {
        // Synchronize on the PersistentObject so that no other object can
        // acquire the lock before we finish the commit operation.
        synchronized (_store) {
            _store.setContents(_meetings);
            PersistentObject.commit(_store);
        }
    }

    // ListFieldCallback methods
    // ----------------------------------------------------------------------

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
     */
    public void drawListRow(final ListField list, final Graphics graphics,
            final int index, final int y, final int w) {
        final Meeting meeting = (Meeting) _meetings.elementAt(index);
        final String text = meeting.getField(Meeting.MEETING_NAME);
        graphics.drawText(text, 0, y, 0, w);
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField ,
     *      int)
     */
    public Object get(final ListField list, final int index) {
        return null; // Not implemented
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField
     *      , String , int)
     */
    public int indexOfList(final ListField list, final String p, final int s) {
        return 0; // Not implemented
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(final ListField list) {
        return Display.getWidth();
    }
}
