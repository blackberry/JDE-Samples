/*
 * HelpScreen.java
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

package com.rim.samples.device.mediakeysdemo.mediaplayerdemo.mediaplayerlib;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * A screen that displays this application's help to the user
 */
public class HelpScreen extends PopupScreen {
    private final Manager _manager;
    private final int _scrollAmount;

    /**
     * Creates a new HelpScreen object with help text
     * 
     * @param title
     *            the string for the title of this screen - may be null
     * @param text
     *            the help text to display - may be null
     */
    public HelpScreen(final String title, final String text) {
        this(title, text, new VerticalFieldManager(Manager.VERTICAL_SCROLL
                | Manager.VERTICAL_SCROLLBAR));
    }

    /**
     * Creates a new HelpScreen object with a specified manager
     * 
     * @param title
     *            The string for the title of this screen - may be null
     * @param text
     *            The help text to display - may be null
     * @param manager
     *            The Manager for this screen
     */
    private HelpScreen(String title, String text, final Manager manager) {
        super(manager, DEFAULT_CLOSE);

        // Clean up leading and trailing whitespace in the title
        if (title != null) {
            title = title.trim();
            if (title.length() == 0) {
                title = null;
            }
        }

        // Clean up leading and trailing whitespace in the help text
        if (text != null) {
            text = text.trim();
            if (text.length() == 0) {
                text = null;
            }
        }

        // Setup the screen
        final LabelField titleField =
                new LabelField(title != null ? title : "Test Application");
        titleField.setFont(titleField.getFont().derive(
                Font.BOLD | Font.UNDERLINED));
        this.add(titleField);

        if (text != null) {
            this.add(new SeparatorField());
            this.add(new LabelField(text));
        }

        this._manager = manager;
        this._scrollAmount = titleField.getFont().getHeight();
    }

    /**
     * Invokes {@link #onClose()} if the ENTER or ESCAPE keys are pressed;
     * otherwise, passes the keypress on to the superclass.
     * 
     * @see Screen#keyDown(int, int)
     */
    protected boolean keyDown(final int keycode, final int time) {
        final int key = Keypad.key(keycode);
        if (key == Keypad.KEY_ENTER || key == Keypad.KEY_ESCAPE) {
            this.onClose();
            return true;
        } else {
            return super.keyDown(keycode, time);
        }
    }

    /**
     * @see Screen#navigationClick(int, int)
     */
    protected boolean navigationClick(final int status, final int time) {
        this.onClose();
        return true;
    }

    /**
     * @see Screen#navigationMovement(int, int, int, int)
     */
    protected boolean navigationMovement(final int dx, final int dy,
            final int status, final int time) {
        int newScrollPosition =
                this._manager.getVerticalScroll() + dy * this._scrollAmount;

        final int maxScrollPosition =
                this._manager.getVirtualHeight()
                        - this._manager.getExtent().height;

        // Avoid scrolling up past the top
        if (newScrollPosition < 0) {
            newScrollPosition = 0;

            // Avoid scrolling down past the bottom
        } else if (newScrollPosition > maxScrollPosition) {
            newScrollPosition = maxScrollPosition;

            // Snap to the top if we're scrolling up and we're within 1 scroll
        } else if (dy < 0 && newScrollPosition - this._scrollAmount < 0) {
            newScrollPosition = 0;

            // Snap to the bottom if we're scrolling down and we're within 1
            // scroll
        } else if (dy > 0
                && newScrollPosition + this._scrollAmount > maxScrollPosition) {
            newScrollPosition = maxScrollPosition;
        }

        if (newScrollPosition != this._manager.getVerticalScroll()) {
            this._manager.setVerticalScroll(newScrollPosition);
        }

        return true;
    }
}
