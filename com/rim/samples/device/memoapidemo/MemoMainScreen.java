/*
 * MemoMainScreen.java
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

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;

import net.rim.blackberry.api.pdap.BlackBerryMemo;
import net.rim.blackberry.api.pdap.BlackBerryMemoList;
import net.rim.blackberry.api.pdap.BlackBerryPIM;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

/**
 * The main screen for the Memo API demo application.
 */
/* package */final class MemoMainScreen extends MainScreen implements
        ListFieldCallback {
    private Vector _memos;
    private BlackBerryMemoList _memoList;
    private final ListField _memoListField;

    private final MenuItem _addItem = new AddItem();

    /**
     * Constructor. Opens the Memo PIM List and displays a list of memos on the
     * screen in list format.
     */
    /* package */MemoMainScreen() {
        super();

        try {
            _memoList =
                    (BlackBerryMemoList) PIM.getInstance().openPIMList(
                            BlackBerryPIM.MEMO_LIST, PIM.READ_WRITE);
        } catch (final PIMException pe) {
            // Can't open the Memo PIM List. Nothing we can do...exiting the
            // application.
            System.exit(1);
        }

        _memoListField = new ListField();
        _memoListField.setCallback(this);
        loadMemos();
        add(_memoListField);
    }

    /**
     * Loads the current list of memos into a vector for easy retrieval.
     */
    private void loadMemos() {
        loadMemos(null);
    }

    /**
     * Loads the current list of memos into a vector for easy retrieval, and
     * sets the focus of ListField to the provided memo (if it's not null).
     */
    private void loadMemos(final BlackBerryMemo memo) {
        try {
            _memos = new Vector();

            final Enumeration memoEnum = _memoList.items();

            while (memoEnum.hasMoreElements()) {
                _memos.addElement(memoEnum.nextElement());
            }

            _memoListField.setSize(_memos.size()); // Causes the list to be
                                                   // updated and painted.

            if (memo != null) {
                final int index = _memos.indexOf(memo);

                if (index != -1) {
                    _memoListField.setSelectedIndex(index);
                }
            }
        } catch (final PIMException pe) {
            // Had a problem retrieving the memos...
        }
    }

    /**
     * Returns the memo that is highlighted in the list, or null if no memo is
     * highlighted.
     * 
     * @return The currently selected memo, or null if there is no currently
     *         selected memo.
     */
    private BlackBerryMemo getSelectedMemo() {
        final int selectedIndex = _memoListField.getSelectedIndex();

        if (selectedIndex == -1) {
            return null;
        }

        return (BlackBerryMemo) _memos.elementAt(selectedIndex);
    }

    /**
     * @see net.rim.device.api.ui.Screen#makeMenu(Menu,int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        super.makeMenu(menu, instance);

        final BlackBerryMemo memo = getSelectedMemo();

        if (memo != null) {
            // There is a currently selected memo; add menu items to manipulate
            // it.
            menu.add(new ViewItem(memo));
            menu.add(new EditItem(memo));
            menu.add(new CopyItem(memo));
            menu.add(new DeleteItem(memo));
        }

        menu.add(_addItem); // "Add" item is always available.
    }

    /**
     * Override Screen.keyChar() to handle the user pressing ENTER. Opens the
     * "add memo" screen if no memo is selected; otherwise, the currently
     * selected memo is shown in the "view memo" screen.
     * 
     * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
     */
    protected boolean keyChar(final char key, final int status, final int time) {
        if (key == Characters.ENTER) {
            final BlackBerryMemo memo = getSelectedMemo();

            if (memo == null) {
                _addItem.run();
            } else {
                new ViewItem(memo).run();
            }

            return true;
        }

        return super.keyChar(key, status, time);
    }

    /**
     * Overrides Screen.invokeAction(). Handles a trackball click and provides
     * identical behavior to an ENTER keypress event.
     * 
     * @see net.rim.device.api.ui#invokeAction(int)
     */
    public boolean invokeAction(final int action) {
        switch (action) {
        case ACTION_INVOKE: // Trackball click.
            final BlackBerryMemo memo = getSelectedMemo();

            if (memo == null) {
                _addItem.run();
            } else {
                new ViewItem(memo).run();
            }

            return true; // We've consumed the event.
        }

        return super.invokeAction(action);
    }

    // ////////////////////////////////////
    // ListFieldCallback methods
    // ////////////////////////////////////

    /**
     * Draws a row in the list of memos.
     * 
     * @param listField
     *            The ListField whose row is being drawn.
     * @param graphics
     *            The graphics context to use for drawing.
     * @param index
     *            The index of the row being drawn.
     * @param y
     *            The distance from the top of the screen where the row is being
     *            drawn.
     * @param width
     *            The width of the row being drawn.
     * 
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
     */
    public void drawListRow(final ListField listField, final Graphics graphics,
            final int index, final int y, final int width) {
        final BlackBerryMemo memo = (BlackBerryMemo) get(listField, index);

        graphics.drawText(memo.getString(BlackBerryMemo.TITLE, 0), 0, y, 0,
                width);
    }

    /**
     * Retrieves the element from the specified ListField at the specified
     * index.
     * 
     * @param listField
     *            The ListField from which to retrieve the element.
     * @param index
     *            The index into the ListField from which to retrieve the
     *            element.
     * @return The requested element.
     * 
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField ,
     *      int)
     */
    public Object get(final ListField listField, final int index) {
        return _memos.elementAt(index);
    }

    /**
     * Returns the preferred width of the provided ListField.
     * 
     * @param listField
     *            The ListField whose preferred width is being retrieved.
     * @return The ListField's preferred width.
     * 
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(final ListField listField) {
        return Display.getWidth();
    }

    /**
     * Retrieves the first occurrence of the provided prefix in the list (not
     * implemented).
     * 
     * @param listField
     *            The ListField being searched.
     * @param prefix
     *            The prefix to search for.
     * @param start
     *            List item at which to start the search.
     * @return -1 (not implemented).
     * 
     * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField,String,int)
     */
    public int indexOfList(final ListField listField, final String prefix,
            final int start) {
        return -1;
    }

    // //////////////////// INNER CLASSES //////////////////////

    /**
     * A menu item for adding a new memo.
     */
    private final class AddItem extends MenuItem {
        /**
         * Constructor.
         */
        private AddItem() {
            super("Add Memo", 100, 100);
        }

        /**
         * Pushes a modal edit screen to the display stack, passing it a new
         * memo to edit. Upon popping the edit screen from the stack, the memo
         * list is re-loaded.
         */
        public void run() {
            final BlackBerryMemo newMemo = _memoList.createMemo();
            UiApplication.getUiApplication().pushModalScreen(
                    new EditMemoScreen(newMemo, true));
            MemoMainScreen.this.loadMemos(newMemo);
        }
    }

    /**
     * Menu item for making a copy of a memo.
     */
    private final class CopyItem extends MenuItem {
        private final BlackBerryMemo _memo;

        /**
         * Constructor.
         * 
         * @param memo
         *            The memo to copy.
         */
        private CopyItem(final BlackBerryMemo memo) {
            super("Add Copy of Memo", 200, 200);
            _memo = memo;
        }

        /**
         * Makes a copy of the memo and re-loads the memo list.
         */
        public void run() {
            final BlackBerryMemo copy =
                    MemoMainScreen.this._memoList.importMemo(_memo);

            try {
                copy.commit();
                MemoMainScreen.this.loadMemos(copy);
            } catch (final PIMException e) {
                // Oh well...
            }
        }
    }

    /**
     * Menu item for viewing a memo.
     */
    private final class ViewItem extends MenuItem {
        private final BlackBerryMemo _memo;

        /**
         * Constructor.
         * 
         * @param memo
         *            The memo to view.
         */
        private ViewItem(final BlackBerryMemo memo) {
            super("View Memo", 300, 50);
            _memo = memo;
        }

        /**
         * Pushes a view screen to the display stack, passing it the memo to
         * view.
         */
        public void run() {
            // Push a modal screen, because user may go on to edit the memo and
            // therefore
            // we need to know when they return.
            UiApplication.getUiApplication().pushModalScreen(
                    new ViewMemoScreen(_memo));

            loadMemos(_memo); // User may have edited the memo; re-load the memo
                              // list.
        }
    }

    /**
     * Menu item for editing a memo.
     */
    private final class EditItem extends MenuItem {
        private final BlackBerryMemo _memo;

        /**
         * Constructor.
         * 
         * @param memo
         *            The memo to edit.
         */
        private EditItem(final BlackBerryMemo memo) {
            super("Edit Memo", 400, 400);
            _memo = memo;
        }

        /**
         * Pushes a modal edit screen to the display stack, passing it the memo
         * to edit. Upon popping the edit screen off the display stack, the memo
         * list is re-loaded.
         */
        public void run() {
            UiApplication.getUiApplication().pushModalScreen(
                    new EditMemoScreen(_memo, false));
            MemoMainScreen.this.loadMemos(_memo);
        }
    }

    /**
     * Menu item to delete a memo.
     */
    private final class DeleteItem extends MenuItem {
        private final BlackBerryMemo _memo;

        /**
         * Constructor.
         * 
         * @param memo
         *            The memo to delete.
         */
        private DeleteItem(final BlackBerryMemo memo) {
            super("Delete Memo", 500, 500);
            _memo = memo;
        }

        /**
         * Displays a dialog asking the user to confirm the delete. If
         * confirmed, the memo is deleted and the memo list re-loaded.
         */
        public void run() {
            try {
                if (Dialog.ask(Dialog.D_DELETE, "Delete memo?", Dialog.CANCEL) == Dialog.DELETE) {
                    _memoList.removeMemo(_memo);
                    MemoMainScreen.this.loadMemos();
                }
            } catch (final PIMException e) {
                // Shouldn't happen...
            }
        }
    }
}
