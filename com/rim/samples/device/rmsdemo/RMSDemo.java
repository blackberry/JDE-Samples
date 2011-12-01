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

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
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
 * sample uses an RMS back end to store a collection of CDs. CD objects can be
 * added, deleted, and edited.
 */
public final class RMSDemo extends MIDlet implements CommandListener {
    private CDdb _db; // RMS reference
    private RecordEnumeration _enum; // Enumeration for the RMS

    private Display _display;

    private Form _addForm; // The Add CD form

    // Input text fields for adding a new CD
    private TextField _artistCD;
    private TextField _titleCD;

    // Menu items for add CD form
    private Command _addSave; // Save menu item
    private Command _addCancel; // Cancel menu item

    private List _list; // The My CD Collection list

    // Menu items for list
    private Command _mainAdd; // Add CD menu item
    private Command _mainDelete; // Delete CD menu item
    private Command _mainEdit; // Edit CD menu item
    private Command _mainExit; // Exit app menu item

    // Id of CD being edited. -1 if no CD is being edited.
    private int _editCDRecordId = -1;

    // Constructor
    public RMSDemo() {
        try {
            // Initialize members
            _db = new CDdb("My Music");
            _enum = _db.enumerate();
            _list = new List("My CD Collection", Choice.IMPLICIT);

            _mainAdd = new Command("Add CD", Command.ITEM, 1);
            _mainEdit = new Command("Edit CD", Command.ITEM, 2);
            _mainDelete = new Command("Delete CD", Command.ITEM, 3);
            _mainExit = new Command("Close", Command.EXIT, 4);

            _addSave = new Command("Save", Command.SCREEN, 1);
            _addCancel = new Command("Cancel", Command.BACK, 2);

            refreshList();

            _list.addCommand(_mainAdd);
            _list.addCommand(_mainExit);
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
            errorDialog("Exception thrown!", e.toString(), _list);
        }
    }

    /**
     * Refreshes the 'My CD Collection' list
     * 
     * @throws RecordStoreNotOpenException
     *             Thrown if the records holding the cd information is not open
     * @throws RecordStoreException
     *             Thrown if an error occurs when accessing the record store
     * @throws IOException
     *             Thrown if a read error occurs
     */
    private void refreshList() throws RecordStoreNotOpenException,
            RecordStoreException, java.io.IOException {
        // Clear list
        _list.deleteAll();

        _enum.rebuild();

        int recordId;

        // Loop through the RMS and add records to list
        while (_enum.hasNextElement()) {
            recordId = _enum.nextRecordId();
            _list.append(_db.getCD(recordId).toString(), null);
        }

        // If there are CDs in the RMS, add edit and delete CD menu items
        if (_list.size() > 0) {
            _list.addCommand(_mainEdit);
            _list.addCommand(_mainDelete);
        } else {
            _list.removeCommand(_mainEdit);
            _list.removeCommand(_mainDelete);
        }
    }

    /**
     * Retrieves the RMS record id
     * 
     * @param index
     *            The index of the record for which to to retrieve the record
     *            ID.
     * @return The RMS record id.
     */
    private int getRecordId(final int index)
            throws RecordStoreNotOpenException, RecordStoreException {
        _enum.rebuild();

        int recordId = -1;
        int count = 0;

        while (_enum.hasNextElement()) {
            recordId = _enum.nextRecordId();
            if (count == index) {
                return recordId;
            }
            count++;
        }

        return recordId;
    }

    /**
     * Command listener implementation
     * 
     * @param c
     *            The menu item clicked
     * @param d
     *            The current displayable
     * @see javax.microedition.lcdui.CommandListener#commandAction(Command,
     *      Displayable)
     */
    public void commandAction(final Command c, final Displayable d) {
        // A list command has been executed
        if (d == _list) {
            if (c == _mainAdd) {
                // Add CD
                _addForm.setTitle("Add CD");
                _display.setCurrent(_addForm);
                _display.setCurrentItem(_artistCD);
            } else if (c == _mainDelete) {
                // Delete CD
                try {
                    final int i = getRecordId(_list.getSelectedIndex());
                    _db.delete(i);
                    refreshList();
                } catch (final Exception e) {
                    errorDialog("Exception thrown!", e.toString(), _list);
                }

            } else if (c == _mainEdit) {
                // Edit CD
                try {
                    _addForm.setTitle("Edit CD");
                    _editCDRecordId = getRecordId(_list.getSelectedIndex());
                    final CD selectedCD = _db.getCD(_editCDRecordId);

                    _artistCD.setString(selectedCD.getArtist());
                    _titleCD.setString(selectedCD.getTitle());

                    _display.setCurrent(_addForm);
                    _display.setCurrentItem(_artistCD);
                } catch (final Exception e) {
                    errorDialog("Exception thrown!", e.toString(), _addForm);
                }
            } else if (c == _mainExit) {
                notifyDestroyed();
            }
        }
        // An add form command has been clicked
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
                    errorDialog("Exception thrown!", e.toString(), _list);
                }
            } else if (c == _addCancel) {
                _editCDRecordId = -1;
            }

            // Clear text fields
            _artistCD.setString("");
            _titleCD.setString("");

            // Switch to list
            _display.setCurrent(_list);
        }
    }

    /**
     * <p>
     * Signals the MIDlet that it has entered the Active state
     * 
     * @see javax.microedition.midlet.MIDlet#startApp()
     */
    public void startApp() {
        _display.setCurrent(_list);
    }

    /**
     * Not implemented
     * 
     * @see javax.microedition.midlet.MIDlet#pauseApp()
     */
    public void pauseApp() {
        // Not implemented.
    }

    /**
     * Not implemented.
     * 
     * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
     */
    public void destroyApp(final boolean unconditional) {
        // Not implemented
    }

    /**
     * Presents an alert to the user with a given message
     * 
     * @param methodThrown
     *            The source for the exception
     * @param errorThrown
     *            The exception thrown by the method
     * @param nextDisplayable
     *            The Form appearing after the alert is dismissed
     */
    public void errorDialog(final String methodThrown,
            final String errorThrown, final Displayable nextDisplayable) {
        final Alert exceptionThrown =
                new Alert(methodThrown, errorThrown, null, AlertType.ERROR);
        _display.setCurrent(exceptionThrown, nextDisplayable);
    }
}
