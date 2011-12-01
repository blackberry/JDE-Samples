/*
 * FileExplorerDemoJournalListener.java
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

package com.rim.samples.device.fileexplorerdemo;

import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

/**
 * Listener to determine when files have been added to the file system
 */
public final class FileExplorerDemoJournalListener implements
        FileSystemJournalListener {
    private final FileExplorerDemoScreen _screen;
    private long _lastUSN; // = 0;

    /**
     * Constructor
     * 
     * @param screen
     *            The screen to update when events occur.
     */
    public FileExplorerDemoJournalListener(final FileExplorerDemoScreen screen) {
        _screen = screen;
    }

    /**
     * Notified when FileSystem event occurs
     */
    public void fileJournalChanged() {
        final long nextUSN = FileSystemJournal.getNextUSN();
        String msg = null;

        for (long lookUSN = nextUSN - 1; lookUSN >= _lastUSN && msg == null; --lookUSN) {
            final FileSystemJournalEntry entry =
                    FileSystemJournal.getEntry(lookUSN);

            // We didn't find an entry
            if (entry == null) {
                break;
            }

            // Check if this entry was added or deleted
            final String path = entry.getPath();

            if (path != null) {
                switch (entry.getEvent()) {
                case FileSystemJournalEntry.FILE_ADDED:
                    msg = "File was added.";
                    break;

                case FileSystemJournalEntry.FILE_DELETED:
                    msg = "File was deleted.";
                    break;
                }
            }
        }

        // _lastUSN must be updated before calling showMessage() because that
        // method
        // pushes a modal screen onto the display stack, which blocks this
        // thread.
        // If the modal screen's thread then processes a file journal event on
        // this
        // application's behalf, the for loop above can end up processing the
        // same
        // event that we are blocking on. Updating _lastUSN before blocking
        // prevents
        // the same file journal event from being processed twice, and thus
        // prevents
        // the same dialog from being displayed twice.
        _lastUSN = nextUSN;

        if (msg != null) {
            showMessage(msg);
            _screen.updateList();
        }
    }

    /**
     * Displays the provided message in a dialog box
     * 
     * @param msg
     *            The message to display
     */
    private void showMessage(final String msg) {
        synchronized (Application.getApplication().getAppEventLock()) {
            Dialog.alert(msg);
        }
    }
}
