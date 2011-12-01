/*
 * BlackBerryBalanceDemoScreen.java
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

import java.util.Vector;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.MultiServicePlatformManager;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.table.AbstractTableModel;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModelAdapter;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.StringProvider;

/**
 * This screen displays a list of Memos
 */
public final class BlackBerryBalanceDemoScreen extends MainScreen {
    private BlackBerryBalanceDemo _uiApp;
    private Vector _memos;
    private AbstractTableModel _model;
    private TableView _view;
    private MenuItem _deleteItem;

    /**
     * Creates a new PersistentStoreDemoScreen object
     * 
     * @param memos
     *            A vector of persistable memo objects
     */
    public BlackBerryBalanceDemoScreen(final Vector memos) {
        super(Manager.NO_VERTICAL_SCROLL);

        _uiApp = (BlackBerryBalanceDemo) UiApplication.getUiApplication();
        _memos = memos;

        // Initialize UI components
        setTitle("BlackBerry Balance Demo");

        // Create an adapter to display memos list in a table
        _model = new MemoTableModelAdapter();

        // Create the view and controller
        _view = new TableView(_model);
        final TableController controller = new TableController(_model, _view);
        controller.setFocusPolicy(TableController.ROW_FOCUS);
        _view.setController(controller);

        _view.setDataTemplateFocus(BackgroundFactory
                .createLinearGradientBackground(Color.LIGHTBLUE,
                        Color.LIGHTBLUE, Color.BLUE, Color.BLUE));
        final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
            public Field[] getDataFields(final int modelRowIndex) {
                final String text =
                        ((Memo) _model.getRow(modelRowIndex))
                                .getField(Memo.MEMO_NAME);
                final Field[] fields =
                        { new LabelField(text, Field.NON_FOCUSABLE) };

                return fields;
            }
        };
        dataTemplate.createRegion(new XYRect(0, 0, 1, 1));
        dataTemplate.setColumnProperties(0, new TemplateColumnProperties(
                Display.getWidth()));
        dataTemplate.setRowProperties(0, new TemplateRowProperties(32));
        _view.setDataTemplate(dataTemplate);
        dataTemplate.useFixedHeight(true);

        add(_view);

        // Menu item to create a new memo
        final MenuItem newMemoItem =
                new MenuItem(new StringProvider("New memo"), 0x230010, 0);
        newMemoItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                // New memos are in UNDECIDED mode by default
                final Memo memo =
                        new Memo(MultiServicePlatformManager
                                .getDefaultUndecidedServiceUid());

                _uiApp.pushScreen(new MemoScreen(memo, -1));
            }
        }));

        _deleteItem = new MenuItem(new StringProvider("Delete"), 0x230040, 3);
        _deleteItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                // Retrieve the highlighted memo object and remove it from the
                // vector, then update the list field to reflect the change.
                final int i = _view.getRowNumberWithFocus();
                final String memoName =
                        ((Memo) _uiApp.getMemos().elementAt(i))
                                .getField(Memo.MEMO_NAME);
                final int result =
                        Dialog.ask(Dialog.DELETE, "Delete " + memoName + "?");
                if (result == Dialog.YES) {
                    _model.removeRowAt(i);
                }
            }
        }));

        addMenuItem(newMemoItem);
    }

    /**
     * Returns a reference to the table model
     * 
     * @return The table model
     */
    public AbstractTableModel getModel() {
        return _model;
    }

    /**
     * Pushes a MemoScreen to display the selected memo
     */
    public void displayMemo() {
        if (_model.getNumberOfRows() != 0) {
            final int index = _view.getRowNumberWithFocus();
            _uiApp.pushScreen(new MemoScreen((Memo) _model.getRow(index), index));
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#makeMenu(Menu,int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        if (_model.getNumberOfRows() > 0) {
            menu.add(_deleteItem);
        }

        super.makeMenu(menu, instance);
    }

    /**
     * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
     */
    protected boolean keyChar(final char key, final int status, final int time) {
        // Intercept the ENTER key
        if (key == Characters.ENTER) {
            displayMemo();
            return true;
        }

        return super.keyChar(key, status, time);
    }

    /**
     * @see net.rim.device.api.ui.Screen#onClose()
     */
    public boolean onClose() {
        _uiApp.persist();

        return super.onClose();
    }

    /**
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    protected boolean invokeAction(final int action) {
        switch (action) {
        case ACTION_INVOKE: // Trackpad click
            displayMemo();
            return true;
        }
        return super.invokeAction(action);
    }

    /**
     * @see net.rim.device.api.ui.Field#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(final TouchEvent message) {
        final TouchGesture touchGesture = message.getGesture();

        if (touchGesture != null && touchGesture.getEvent() == TouchGesture.TAP) {
            displayMemo();
            return true;
        }

        return super.touchEvent(message);
    }

    /**
     * Adapter to display memo data in table format
     */
    private class MemoTableModelAdapter extends TableModelAdapter {
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows() {
            return _memos.size();
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfColumns()
         */
        public int getNumberOfColumns() {
            return 1;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doGetRow(int)
         */
        protected Object doGetRow(final int index) {
            return _memos.elementAt(index);
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doInsertRowAt(int,
         *      Object)
         */
        protected boolean doInsertRowAt(final int index, final Object object) {
            if (_memos.size() == 0) {
                _memos.addElement(object);
            } else {
                _memos.setElementAt(object, index);
            }
            return true;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doAddRow(Object)
         */
        protected boolean doAddRow(final Object object) {
            _memos.addElement(object);
            return true;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doRemoveRowAt(int)
         */
        protected boolean doRemoveRowAt(final int index) {
            _memos.removeElementAt(index);
            return true;
        }
    };
}
