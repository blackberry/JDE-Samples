/*
 * MemoScreen.java
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

package com.rim.samples.device.blackberrybalancedemo;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.MultiServicePlatformManager;
import net.rim.device.api.system.ServiceMode;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

/**
 * A MainScreen for displaying a new or existing Memo
 */
public final class MemoScreen extends MainScreen {
    private final EditField _nameField;
    private final EditField _contentField;
    private final Memo _memo;
    private final BlackBerryBalanceDemo _uiApp;
    private final int _index;

    /**
     * Creates a new MemoScreen object
     * 
     * @param memo
     *            The Memo object associated with this screen
     * @param index
     *            The position of the memo in the list. A value of -1 represents
     *            a new memo.
     */
    public MemoScreen(final Memo memo, final int index) {
        super(Manager.NO_VERTICAL_SCROLL);

        setTitle("Memo Screen");

        _memo = memo;
        _index = index;

        // Cache a reference to the application
        _uiApp = (BlackBerryBalanceDemo) UiApplication.getUiApplication();

        _nameField = new EditField("Title: ", _memo.getField(Memo.MEMO_NAME));
        add(_nameField);

        add(new SeparatorField());

        _contentField = new EditField("", _memo.getField(Memo.MEMO_CONTENT));
        add(_contentField);

        // Menu item to save the displayed memo
        final MenuItem saveItem =
                new MenuItem(new StringProvider("Save"), 0x230020, 0);
        saveItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                if (onSave()) {
                    close();
                }
            }
        }));

        addMenuItem(saveItem);
    }

    /**
     * @see net.rim.device.api.ui.Screen#suggestServiceMode(ServiceMode)
     */
    public boolean suggestServiceMode(final ServiceMode serviceMode) {
        // Do not modify if current service mode is corporate
        if (!MultiServicePlatformManager.isCorporateServiceUid(_memo.getMode())) {
            setServiceMode(serviceMode);

            return true;
        }

        return false;
    }

    /**
     * @see net.rim.device.api.ui.Screen#setServiceModeImpl(ServiceMode)
     */
    public void setServiceModeImpl(final ServiceMode serviceMode) {
        /* This method is called by setServiceMode() */

        _memo.setMode(serviceMode.getServiceUid());
    }

    /**
     * @see net.rim.device.api.ui.Screen#getServiceMode()
     */
    public ServiceMode getServiceMode() {
        return new ServiceMode(_memo.getMode());
    }

    /**
     * @see net.rim.device.api.ui.Screen#onSave()
     */
    protected boolean onSave() {
        if (!_nameField.getText().equals("")) {
            _memo.setField(Memo.MEMO_NAME, _nameField.getText());
            _memo.setField(Memo.MEMO_CONTENT, _contentField.getText());

            _uiApp.saveMemo(_memo, _index);
            return super.onSave();
        } else {
            Dialog.alert("Memo name required");
            return false;
        }
    }

    /**
     * Retrieves the index of the currently displayed Memo
     * 
     * @return Index of the currently displayed Memo
     */
    public int getMemoIndex() {
        return _index;
    }

    /**
     * Retrieves the currently displayed Memo
     * 
     * @return The currently displayed Memo
     */
    public Memo getMemo() {
        return _memo;
    }
}
