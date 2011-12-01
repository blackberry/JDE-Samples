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

import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;

import net.rim.blackberry.api.pdap.BlackBerryMemo;
import net.rim.blackberry.api.pdap.BlackBerryMemoList;
import net.rim.blackberry.api.pdap.BlackBerryPIM;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModelAdapter;
import net.rim.device.api.ui.component.table.TableModelChangeEvent;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.StringProvider;

/**
 * The main screen for the Memo API demo application
 */
public final class MemoMainScreen extends MainScreen {
    private BlackBerryMemoList _memoList;
    private BlackBerryMemoTableModel _model;
    private TableView _view;
    private MenuItem _addItem;

    /**
     * Creates a new MemoMainScreen object
     */
    public MemoMainScreen() {
        super(Manager.NO_VERTICAL_SCROLL);

        try {
            _memoList =
                    (BlackBerryMemoList) PIM.getInstance().openPIMList(
                            BlackBerryPIM.MEMO_LIST, PIM.READ_WRITE);
        } catch (final PIMException pe) {
            // Can't open the Memo PIM List. Exit the application
            System.exit(1);
        }

        _model = new BlackBerryMemoTableModel();

        // Create the view and the controller
        _view = new TableView(_model);
        final TableController controller = new TableController(_model, _view);
        controller.setFocusPolicy(TableController.ROW_FOCUS);
        _view.setController(controller);

        _view.setDataTemplateFocus(BackgroundFactory
                .createLinearGradientBackground(Color.LIGHTBLUE,
                        Color.LIGHTBLUE, Color.BLUE, Color.BLUE));
        final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
            public Field[] getDataFields(final int modelRowIndex) {
                final BlackBerryMemo memo =
                        (BlackBerryMemo) _model.getRow(modelRowIndex);

                final Field[] fields =
                        { new LabelField(memo
                                .getString(BlackBerryMemo.TITLE, 0),
                                Field.NON_FOCUSABLE) };
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

        _addItem = new AddItem();
    }

    /**
     * Returns the memo that is highlighted in the list, or null if no memo is
     * highlighted.
     * 
     * @return The currently selected memo, or null if there is no currently
     *         selected memo
     */
    private BlackBerryMemo getSelectedMemo() {
        final int selectedIndex = _view.getRowNumberWithFocus();

        if (selectedIndex == -1) {
            return null;
        }

        return (BlackBerryMemo) _model.getRow(selectedIndex);
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        super.makeMenu(menu, instance);

        if (_model.getNumberOfRows() > 0) {
            final BlackBerryMemo memo = getSelectedMemo();

            // There is a currently selected memo, add menu items to manipulate
            // it
            menu.add(new ViewItem(memo));
            menu.add(new EditItem(memo));
            menu.add(new CopyItem(memo));
            menu.add(new DeleteItem(memo));
        }

        // "Add" item is always available
        menu.add(_addItem);
    }

    /**
     * @see net.rim.device.api.ui.Screen#keyChar(char, int, int)
     */
    protected boolean keyChar(final char key, final int status, final int time) {

        // Open the "add memo" screen if no memo is selected, otherwise the
        // currently selected memo is shown in the "view memo" screen.
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
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    public boolean invokeAction(final int action) {
        if (action == ACTION_INVOKE) {
            final BlackBerryMemo memo = getSelectedMemo();

            if (memo == null) {
                _addItem.run();
            } else {
                new ViewItem(memo).run();
            }

            return true;
        }

        return super.invokeAction(action);
    }

    // //////////////////// INNER CLASSES //////////////////////

    /**
     * Adapter to display memo list in table format
     */
    private class BlackBerryMemoTableModel extends TableModelAdapter {
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows() {
            return _memoList.size();
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
        public Object doGetRow(final int modelRowIndex) {
            try {
                final Enumeration memoEnum = _memoList.items();
                int i = 0;
                while (memoEnum.hasMoreElements()) {
                    final BlackBerryMemo memo =
                            (BlackBerryMemo) memoEnum.nextElement();
                    if (i == modelRowIndex) {
                        return memo;
                    }
                    i++;
                }
            } catch (final javax.microedition.pim.PIMException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doRemoveRowAt(int)
         */
        public boolean doRemoveRowAt(final int index) {
            try {
                _memoList.removeMemo((BlackBerryMemo) _model.getRow(index));
                return true;
            } catch (final javax.microedition.pim.PIMException e) {
                MemoApiDemo.errorDialog("PIM#doRemoveRowAt() threw "
                        + e.toString());
            }
            return false;
        }

        /**
         * Notifies the view that the table data has been updated.
         */
        public void refresh() {
            notifyListeners(new TableModelChangeEvent(
                    TableModelChangeEvent.COLUMN_UPDATED, this, -1, 0));
        }

        /**
         * Creates and adds a new memo to the list, prompting the user for data
         * and notifying listeners.
         */
        public void addNewMemo() {
            final BlackBerryMemo memo = _memoList.createMemo();

            // Launch screen for user-supplied memo information
            UiApplication.getUiApplication().pushModalScreen(
                    new EditMemoScreen(memo, true));
            try {
                memo.commit();
            } catch (final PIMException e) {
                MemoApiDemo.errorDialog("addNewMemo() threw " + e.toString());
            }
            refresh();
        }
    }

    /**
     * A menu item for adding a new memo
     */
    private final class AddItem extends MenuItem {
        /**
         * Creates a new AddItem object
         */
        private AddItem() {
            super(new StringProvider("Add Memo"), 0x230010, 100);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    _model.addNewMemo();
                }
            }));
        }
    }

    /**
     * Menu item for making a copy of a memo
     */
    private final class CopyItem extends MenuItem {
        private final BlackBerryMemo _memo;

        /**
         * Creates a new CopyItem object
         * 
         * @param memo
         *            The memo to copy
         */
        private CopyItem(final BlackBerryMemo memo) {
            super(new StringProvider("Add Copy of Memo"), 0x230020, 200);
            _memo = memo;
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    final BlackBerryMemo copy =
                            MemoMainScreen.this._memoList.importMemo(_memo);

                    try {
                        copy.commit();
                        _model.refresh();
                    } catch (final PIMException e) {
                        MemoApiDemo
                                .errorDialog("BlackBerryMemo#commit() threw "
                                        + e.toString());
                    }
                }
            }));
        }
    }

    /**
     * Menu item for viewing a memo
     */
    private static final class ViewItem extends MenuItem {
        private final BlackBerryMemo _memo;

        /**
         * Creates a new ViewItem object
         * 
         * @param memo
         *            The memo to view
         */
        private ViewItem(final BlackBerryMemo memo) {
            super(new StringProvider("View Memo"), 0x230030, 50);
            _memo = memo;
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    // Push a modal screen, because user may go on to edit the
                    // memo
                    // and therefore we need to know when they return.
                    UiApplication.getUiApplication().pushModalScreen(
                            new ViewMemoScreen(_memo));
                }
            }));
        }
    }

    /**
     * Menu item for editing a memo
     */
    private final class EditItem extends MenuItem {
        private final BlackBerryMemo _memo;

        /**
         * Creates a new EditItem object
         * 
         * @param memo
         *            The memo to edit
         */
        private EditItem(final BlackBerryMemo memo) {
            super(new StringProvider("Edit Memo"), 0x230040, 400);
            _memo = memo;
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    UiApplication.getUiApplication().pushModalScreen(
                            new EditMemoScreen(_memo, false));
                    try {
                        _memo.commit();
                    } catch (final PIMException e) {
                        MemoApiDemo
                                .errorDialog("BlackBerryMemo#commit() threw "
                                        + e.toString());
                    }
                    _model.refresh();
                }
            }));
        }
    }

    /**
     * Menu item to delete a memo
     */
    private final class DeleteItem extends MenuItem {

        /**
         * Creates a new DeleteItem object
         * 
         * @param memo
         *            The memo to delete
         */
        private DeleteItem(final BlackBerryMemo memo) {
            super(new StringProvider("Delete Memo"), 0x230050, 500);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    if (Dialog.ask(Dialog.D_DELETE, "Delete memo?",
                            Dialog.CANCEL) == Dialog.DELETE) {
                        _model.removeRowAt(_view.getRowNumberWithFocus());
                    }
                }
            }));
        }
    }

}
