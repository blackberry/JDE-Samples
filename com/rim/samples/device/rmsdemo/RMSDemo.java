/*
 * RMSDemo.java
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

package com.rim.samples.device.rmsdemo;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * Sample to demonstrate the usage of a Record Management Store in a MIDlet. The
 * sample uses an RMS backend to store a collection of CDs. CD objects can be
 * added, deleted, and edited.
 */
public class RMSDemo extends MIDlet implements CommandListener {
    private CDdb _db; // RMS reference
    private RecordEnumeration _enum; // Enumeration for the RMS.

    private Display _display;

    private Form _addForm; // The Add CD form.

    // Input text fields for adding a new CD.
    private TextField _artistCD;
    private TextField _titleCD;

    // Menu items for add CD form.
    private Command _addSave; // Save menu item.
    private Command _addCancel; // Cancel menu item.

    private List _list; // The My CD Collection list.

    // Menu items for list.
    private Command _mainAdd; // Add CD menu item
    private Command _mainDelete; // Delete CD menu item
    private Command _mainEdit; // Edit CD menu item

    private int _editCDRecordId = -1; // Id of CD being edited. -1 if no CD is
                                      // being edited.

    public RMSDemo() {
        try {
            // Initialize members.
            _db = new CDdb("My Music");
            _enum = _db.enumerate();
            _list = new List("My CD Collection", Choice.IMPLICIT);

            _mainAdd = new Command("Add CD", Command.ITEM, 1);
            _mainDelete = new Command("Delete CD", Command.ITEM, 2);
            _mainEdit = new Command("Edit CD", Command.ITEM, 3);

            _addSave = new Command("Save", Command.SCREEN, 1);
            _addCancel = new Command("Cancel", Command.SCREEN, 2);

            refreshList();

            _list.addCommand(_mainAdd);
            _list.setSelectCommand(_mainEdit);

            _list.setCommandListener(this);

            _addForm = new Form("Add CD");

            _artistCD = new TextField("Artist: ", null, 20, TextField.ANY);
            _titleCD = new TextField("Title: ", null, 20, TextField.ANY);

            _addForm.append(_artistCD);
            _addForm.append(_titleCD);

            _addForm.addCommand(_addSave);
            _addForm.addCommand(_addCancel);

            _addForm.setCommandListener(this);

            _display = Display.getDisplay(this);

        } catch (final Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Refresh the My CD Collection list
    public void refreshList() throws RecordStoreNotOpenException,
            RecordStoreException, java.io.IOException {
        // Clear list.
        _list.deleteAll();

        _enum.rebuild();

        int recordId;

        // Loop through the RMS and add records to list.
        while (_enum.hasNextElement()) {
            recordId = _enum.nextRecordId();
            _list.append(_db.getCD(recordId).toString(), null);
        }

        // If there are CDs in the RMS, add delete CD menu item. Otherwise,
        // don't.
        if (_list.size() > 0) {
            _list.addCommand(_mainDelete);
        } else {
            _list.removeCommand(_mainEdit);
            _list.removeCommand(_mainDelete);
        }
    }

    /**
     * Get the RMS record id from the index of the list
     * 
     * @param index
     *            the index of the list
     * @return the RMS record id of the item at index
     */
    public int getRecordIdFromIndex(final int index)
            throws RecordStoreNotOpenException, RecordStoreException {
        _enum.rebuild();

        int recordId = -1;

        while (_enum.hasNextElement()) {
            recordId = _enum.nextRecordId();
        }

        return recordId;
    }

    /**
     * Command listener
     * 
     * @param c
     *            the menu item clicked
     * @param d
     *            the current displayable
     */
    public void commandAction(final Command c, final Displayable d) {
        // A list command has been executed.
        if (d == _list) {
            if (c == _mainAdd) {
                // Add CD
                _addForm.setTitle("Add CD");
                _display.setCurrent(_addForm);
                _display.setCurrentItem(_artistCD);
            } else if (c == _mainDelete) {
                // Delete CD
                try {
                    final int i =
                            getRecordIdFromIndex(_list.getSelectedIndex());
                    _db.delete(i);
                    refreshList();
                } catch (final Exception e) {
                    System.out.println(e.getMessage());
                }

            } else if (c == _mainEdit) {
                // Edit CD
                try {
                    _addForm.setTitle("Edit CD");
                    _editCDRecordId =
                            getRecordIdFromIndex(_list.getSelectedIndex());
                    final CD selectedCD = _db.getCD(_editCDRecordId);

                    _artistCD.setString(selectedCD.getArtist());
                    _titleCD.setString(selectedCD.getTitle());

                    _display.setCurrent(_addForm);
                    _display.setCurrentItem(_artistCD);
                } catch (final Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        // An add form comamnd has been clicked.
        else if (d == _addForm) {
            if (c == _addSave) {
                // Save
                try {
                    if (_editCDRecordId == -1) {
                        _db.add(_artistCD.getString(), _titleCD.getString());
                    } else {
                        _db.edit(_editCDRecordId, _artistCD.getString(),
                                _titleCD.getString());
                        _editCDRecordId = -1;
                    }
                    refreshList();
                } catch (final Exception e) {
                    System.out.println(e.getMessage());
                }
            } else if (c == _addCancel) {
                _editCDRecordId = -1;
            }

            // Clear text fields.
            _artistCD.setString("");
            _titleCD.setString("");

            // Switch to list.
            _display.setCurrent(_list);
        }
    }

    /**
     * <p>
     * Signals the MIDlet that it has entered the Active state.
     */
    public void startApp() {
        _display.setCurrent(_list);
    }

    /**
     * <p>
     * Signals the MIDlet to stop and enter the Pause state.
     */
    public void pauseApp() {
        // Not implemented.
    }

    /**
     * <p>
     * Signals the MIDlet to terminate and enter the Destroyed state.
     * 
     * @param unconditional
     *            When set to true, the MIDlet must cleanup and release all
     *            resources. Otherwise, the MIDlet may throw a
     *            MIDletStateChangeException to indicate it does not want to be
     *            destroyed at this time.
     */
    public void destroyApp(final boolean unconditional) {
        // Not implemented.
    }
}
