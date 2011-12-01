/*
 * FileExplorerScreen.java
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

package com.rim.samples.device.attachmentdemo;

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A MainScreen class to show the listing of all directories/files
 */
public class FileExplorerScreen extends MainScreen {
    private static String ROOT = "file:///SDCard/";

    private final FileExplorerListField _list;
    private String _parentRoot;
    private final UiApplication _app;

    /**
     * Constructs a new FileExplorerScreen object
     * 
     * @param app
     *            Reference to the UiApplication instance
     */
    public FileExplorerScreen(final UiApplication app) {
        _app = app;

        // Instantiate and add ListField
        _list = new FileExplorerListField();
        add(_list);

        readRoots(ROOT);
    }

    /**
     * @see Screen#keyChar(char, int, int)
     */
    public boolean keyChar(final char c, final int status, final int time) {
        // Enter key will take action on directory/file. Escape key will go up
        // one directory or close screen if at top level.
        switch (c) {
        case Characters.ENTER:
            selectAction();
            return true;
        case Characters.ESCAPE:
            if (goBack()) {
                return true;
            }
        }
        return super.keyChar(c, status, time);
    }

    /**
     * @see MainScreen#makeMenu(Menu, int)
     */
    public void makeMenu(final Menu menu, final int instance) {
        // Only display our menu items if no actions are performed
        if (instance == Menu.INSTANCE_DEFAULT) {
            menu.add(_selectItem);

            if (_parentRoot != null) {
                menu.add(_backItem);
            }
        }
        super.makeMenu(menu, instance);
    }

    /**
     * @see Screen#invokeAction(int)
     */
    protected boolean invokeAction(final int action) {
        if (action == ACTION_INVOKE) {
            selectAction();
            return true;
        }
        return false;
    }

    /**
     * Reads the given path and enumerates through it
     * 
     * @param root
     *            Path to be read
     */
    private void readRoots(final String root) {
        _parentRoot = root;
        setTitle(root);

        // Reset the list field
        _list.removeAll();

        FileConnection fc = null;
        Enumeration rootEnum = null;

        if (root != null) {
            // Open the file system and get the list of directories/files
            try {
                fc = (FileConnection) Connector.open(root);
                rootEnum = fc.list();
            } catch (final Exception e) {
                AttachmentDemo.errorDialog(e.toString());
                return;
            } finally {
                if (fc != null) {
                    // Everything is read, make sure to close the connection
                    try {
                        fc.close();
                        fc = null;
                    } catch (final IOException e) {
                    }
                }
            }
        }

        // There was no root to read, so read the system roots
        if (rootEnum == null) {
            rootEnum = FileSystemRegistry.listRoots();
        }

        // Read through the list of directories/files
        while (rootEnum.hasMoreElements()) {
            String file = (String) rootEnum.nextElement();

            if (root != null) {
                file = root + file;
            }

            readSubroots(file);
        }
    }

    /**
     * Reads all the directories and files from the provided path
     * 
     * @param file
     *            Upper directory to be read
     */
    private void readSubroots(final String file) {
        FileConnection fc = null;

        try {
            fc = (FileConnection) Connector.open(file);

            // Create a file holder from the FileConnection so that the
            // connection is not left open
            final FileHolder fileholder =
                    new FileHolder(file, fc.isDirectory());
            _list.add(fileholder);
        } catch (final IOException e) {
            AttachmentDemo
                    .errorDialog("Connector.open() threw " + e.toString());
        } finally {
            if (fc != null) {
                // Everything is read, make sure to close the connection
                try {
                    fc.close();
                    fc = null;
                } catch (final Exception ioex) {
                }
            }
        }
    }

    /**
     * Performs an action on a selected directory or file
     */
    private void selectAction() {
        final FileHolder fileholder =
                (FileHolder) _list.get(_list, _list.getSelectedIndex());

        if (fileholder != null) {
            // If the FileHolder represents a directory, then show what's
            // in the directory. Otherwise prompt user to upload.
            if (fileholder.isDirectory()) {
                readRoots(fileholder.getPath());
            } else {
                _app.pushModalScreen(new FileUploadDialog(fileholder, this));
            }
        }
    }

    /**
     * Goes back one directory in the directory hierarchy, if possible
     * 
     * @return True if we went back a directory, otherwise false
     */
    private boolean goBack() {
        if (_parentRoot != null) {
            if (_parentRoot.equals(ROOT)) {
                return false;
            }

            String backParentRoot =
                    _parentRoot.substring(0, _parentRoot.lastIndexOf('/'));
            backParentRoot =
                    backParentRoot.substring(0,
                            backParentRoot.lastIndexOf('/') + 1);

            if (backParentRoot.length() > 0) {
                readRoots(backParentRoot);
                return true;
            }
        }

        return false;
    }

    /**
     * Method to present a message to the user
     * 
     * @param msg
     *            The text to display
     */
    public void displayStatus(final String msg) {
        _app.invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(msg);
            }
        });
    }

    /**
     * Menu item for displaying information on a selected file
     */
    private final MenuItem _selectItem = new MenuItem("Select", 500, 500) {
        public void run() {
            selectAction();
        }
    };

    /**
     * Menu item for going back one directory in the directory hierarchy
     */
    private final MenuItem _backItem = new MenuItem("Go Back", 500, 500) {
        public void run() {
            goBack();
        }
    };
}
