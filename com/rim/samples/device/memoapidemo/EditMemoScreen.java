/*
 * EditMemoScreen.java
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

import java.io.IOException;

import net.rim.blackberry.api.pdap.BlackBerryMemo;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

/**
 * Screen that allows a memo to be edited. Used for both new and existing memos.
 */
public final class EditMemoScreen extends MainScreen {
    private final MemoController _controller;

    /**
     * Constructor. Adds an appropriate title, depending on whether we're
     * creating a new memo or editing an existing one. It then adds the memo's
     * fields to the screen so the user can edit the memo content.
     * 
     * @param memo
     *            The memo being created or edited.
     * @param newMemo
     *            True if a new memo is being created; false if an existing memo
     *            is being edited.
     */
    public EditMemoScreen(final BlackBerryMemo memo, final boolean newMemo) {
        super();

        _controller = new MemoController(memo);

        String title;

        if (newMemo) {
            title = "Add Memo";
        } else {
            title = "Edit Memo";
        }

        setTitle(title);

        final Field[] fields =
                (Field[]) _controller.render(newMemo ? MemoController.FOR_ADD
                        : MemoController.FOR_EDIT);

        for (int i = 0; i < fields.length; ++i) {
            add(fields[i]);
        }

        // Represents a menu item for saving the screen's memo.
        final MenuItem saveMenuItem =
                new MenuItem(new StringProvider("Save Memo"), 0x230010, 100);
        saveMenuItem.setCommand(new Command(new CommandHandler() {

            /**
             * Attempts to save the screen's data to its associated memo. If
             * successful, the edit screen is popped from the display stack.
             * 
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                if (EditMemoScreen.this.onSave()) {
                    UiApplication.getUiApplication().popScreen(
                            EditMemoScreen.this);
                }
            }
        }));

        // Add the menu item.
        addMenuItem(saveMenuItem);
    }

    /**
     * Override superclass's method to provide custom validation of screen data.
     * We could have done this by extending LabelField and overriding
     * isDataValid() there. In that case, this method would not be necessary
     * since Screen.isDataValid() calls all of it's child field's isDataValid()
     * methods.
     * 
     * @return True if screen data is valid; otherwise false;
     */
    public boolean isDataValid() {
        // Can't save a memo without a title.
        final LabelField title =
                (LabelField) _controller.render(MemoController.FOR_TITLE);

        if (title.getText().length() == 0) {
            Dialog.alert("Title must have a value.");
            return false;
        }
        return true;
    }

    /**
     * Saves the screen's data to its associated memo, and commits the memo to
     * persistent storage.
     * 
     * @throws IOException
     *             Thrown if a problem was encountered while committing the
     *             memo.
     */
    public void save() throws IOException {
        _controller.updateMemo();

        if (!_controller.commitMemo()) {
            throw new IOException();
        }
    }
}
