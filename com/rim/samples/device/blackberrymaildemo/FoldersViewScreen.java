/*
 * FoldersViewScreen.java
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

import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.util.Arrays;

/**
 * This class displays the messages organized by the folders they belong to
 */
public final class FoldersViewScreen extends BlackBerryMailDemoScreen {
    private Folder _currentFolder;

    /**
     * Creates a new FoldersViewScreen object
     * 
     * @param currentFolder
     *            The folder for which to display the folder view
     */
    public FoldersViewScreen(final Folder currentFolder) {
        setTitle("Folders View Screen");

        _currentFolder = currentFolder;

        _changeViewMenuItem = new MenuItem("Messages View", 110, 10) {
            public void run() {
                _currentDisplayMode = MESSAGES_VIEW_MODE;
                close();
            }
        };

        _listField = new FolderObjectListField();
        add(_listField);

        // Fill the list with the files and subfolders in the current folder
        populateFolderViewList();
    }

    /**
     * Fills the ListField with the objects in the current folder
     */
    private void populateFolderViewList() {
        // Retrieve the messages and subfolders residing in the current folder
        Message[] messages;
        try {
            messages = _currentFolder.getMessages();
        } catch (final MessagingException me) {
            BlackBerryMailDemo.errorDialog("Folder#getMessages() threw "
                    + me.toString());
            return;
        }
        final Folder[] subfolders = _currentFolder.list();

        // Calculate the number of objects to display and allocate the array
        // of objects to display.
        int noOfObjects = subfolders.length + messages.length;
        if (_currentFolder.getParent() != null) {
            noOfObjects++;
        }

        final Object[] objectsToDisplay = new Object[noOfObjects];

        // Add the different objects to the array of objects to display
        int indexToAddNewObjects = 0;

        // Add an entry to go back up a level if we are not in the root folder
        final Folder parent = _currentFolder.getParent();
        if (parent != null) {
            objectsToDisplay[0] = "..";
            indexToAddNewObjects++;
        }

        // Add the folders to the ObjectListField if there is something to add
        System.arraycopy(subfolders, 0, objectsToDisplay, indexToAddNewObjects,
                subfolders.length);
        indexToAddNewObjects += subfolders.length;

        // Add the messages to the ObjectListField in sorted order
        Arrays.sort(messages, Util.SORT_BY_MOST_RECENT_DATE);
        System.arraycopy(messages, 0, objectsToDisplay, indexToAddNewObjects,
                messages.length);

        // Set the list field to display the objects gathered in this method
        final ObjectListField objectListField = (ObjectListField) _listField;
        objectListField.set(objectsToDisplay);
    }

    /**
     * Opens an object selected from the list field
     */
    protected void openAction() {
        // Get the currently selected item.
        final int[] selection = _listField.getSelection();

        // MultiSelect is not enabled, so only one item will be returned.
        final Object folderOrMessageOrString =
                ((ObjectListField) _listField).get(_listField, selection[0]);

        if (folderOrMessageOrString instanceof String) {
            // The ".." entry is a string. Go back up one level.
            _currentFolder = _currentFolder.getParent();
            populateFolderViewList();
        } else if (folderOrMessageOrString instanceof Folder) {
            _currentFolder = (Folder) folderOrMessageOrString;
            populateFolderViewList();
        } else if (folderOrMessageOrString instanceof Message) {
            openMessage((Message) folderOrMessageOrString);
        }
    }

    /**
     * This is a custom ObjectListField class which draws the objects stored by
     * the FoldersViewScreen on the list field. It handles drawing Strings,
     * Folders and Messages.
     */
    private static class FolderObjectListField extends ObjectListField {
        public void drawListRow(final ListField list, final Graphics g,
                final int index, final int y, final int w) {
            // Display the name of the object, depending on its type
            final Object obj = get(list, index);
            String text = null;
            if (obj instanceof String) {
                text = (String) obj;
            } else if (obj instanceof Folder) {
                text =
                        Characters.WHITE_RIGHT_POINTING_SMALL_TRIANGLE + "  "
                                + ((Folder) obj).getName();
            } else if (obj instanceof Message) {
                final Message message = (Message) obj;
                text =
                        Util.getStatusIcon(message) + "  "
                                + message.getSubject();
            }

            g.drawText(text, 0, y, 0, w);
        }
    }
}
