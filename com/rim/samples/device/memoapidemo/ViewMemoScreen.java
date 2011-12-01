/*
 * ViewMemoScreen.java
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

import net.rim.blackberry.api.pdap.BlackBerryMemo;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

/**
 * Screen for viewing a memo.
 */
public final class ViewMemoScreen extends MainScreen {
    private final MemoController _controller;

    /**
     * Constructor. Displays the provided memo's information on the screen.
     * 
     * @param memo
     *            The memo to view.
     */
    public ViewMemoScreen(final BlackBerryMemo memo) {
        super();

        _controller = new MemoController(memo);

        // Set the screen's title.
        final Field title =
                (Field) _controller.render(MemoController.FOR_TITLE);
        setTitle(title);

        // Add the various memo fields to the screen.
        final Field[] fields =
                (Field[]) _controller.render(MemoController.FOR_VIEW);

        for (int i = 0; i < fields.length; ++i) {
            add(fields[i]);
        }

        // Menu item to edit this screen's memo.
        final MenuItem editItem =
                new MenuItem(new StringProvider("Edit Memo"), 100, 100);
        editItem.setCommand(new Command(new CommandHandler() {

            /**
             * Pushes an edit screen to the display stack, passing it the memo
             * to edit. Upon returning from the edit screen, the view screen is
             * popped as well. The user is returned to the main screen.
             * 
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                final UiApplication app = UiApplication.getUiApplication();
                app.pushModalScreen(new EditMemoScreen(_controller.getMemo(),
                        false)); // Blocks until edit screen is popped.
                app.popScreen(ViewMemoScreen.this); // Now that edit screen is
                                                    // popped, pop this view
                                                    // screen as well.
            }
        }));
        // Add the menu item to the screen.
        addMenuItem(editItem);
    }
}
