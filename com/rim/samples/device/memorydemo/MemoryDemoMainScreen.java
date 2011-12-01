/*
 * MemoryDemoMainScreen.java
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

package com.rim.samples.device.memorydemo;

import java.util.Calendar;
import java.util.Date;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.util.StringProvider;

/**
 * The main screen for the application.
 */
public final class MemoryDemoMainScreen extends MainScreen implements
        ListFieldCallback, LowMemoryListener {
    private final OrderList _orderList;
    private final OrderListField _orderListField;
    private final UiApplication _app;
    private ProgressBarDialog _progressDialog;

    private static final int MAX_RECORDS = 1000;

    /**
     * Creates a new MemoryDemoMainScreen object
     */
    public MemoryDemoMainScreen() {
        setTitle("Order Records");

        _app = UiApplication.getUiApplication();

        // Get and display the order list.
        _orderList = OrderList.getInstance();
        _orderListField = new OrderListField(_orderList.getNumOrderRecords());
        _orderListField.setCallback(this);
        add(_orderListField);

        LowMemoryManager.addLowMemoryListener(this);
    }

    /**
     * @see net.rim.device.api.ui.Screen#onClose()
     */
    public boolean onClose() {
        // Remove this screen as a low memory listener
        LowMemoryManager.removeLowMemoryListener(this);

        // Commit the order list to persistent store
        _orderList.commit();

        return super.onClose();
    }

    /**
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    protected boolean invokeAction(final int action) {
        if (action == ACTION_INVOKE) {
            viewRecord(_orderListField.getSelectedIndex());
            return true;
        }

        return super.invokeAction(action);
    }

    /**
     * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
     */
    protected boolean keyChar(final char key, final int status, final int time) {
        if (key == Characters.ENTER) {
            viewRecord(_orderListField.getSelectedIndex());
            return true;
        }

        return super.keyChar(key, status, time);
    }

    /**
     * Displays selected record in view mode
     */
    private void viewRecord(final int index) {
        OrderRecord orderRecord =
                (OrderRecord) /* outer. */get(_orderListField, index);
        final MemoryDemoOrderScreen screen =
                new MemoryDemoOrderScreen(orderRecord, false);
        _app.pushModalScreen(screen);
        orderRecord = screen.getUpdatedOrderRecord();

        if (orderRecord != null) {
            _orderList.replaceOrderRecordAt(_orderListField.getSelectedIndex(),
                    orderRecord);
        }
    }

    // ListFieldCallback methods
    // -------------------------------------------------------------------

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
     */
    public void drawListRow(final ListField listField, final Graphics graphics,
            final int index, final int y, final int width) {
        final Object object = get(listField, index);
        graphics.drawText(object.toString(), 0, y, 0, width);
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(final ListField listField) {
        return Display.getWidth();
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField ,
     *      int)
     */
    public Object get(final ListField listField, final int index) {
        return _orderList.getOrderRecordAt(index);
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField
     *      , String , int)
     */
    public int indexOfList(final ListField listField, final String prefix,
            final int start) {
        return -1; // Not implemented.
    }

    // LowMemoryListener methods
    // -------------------------------------------------------------------

    /**
     * This method is called when the system is running low on memory.
     * Applications should mark some objects as recoverable, depending on the
     * priority, in order to free up some memory.
     * 
     * @param priority
     *            The priority of the memory cleanup.
     * 
     * @return True if any objects were marked as recoverable; otherwise false.
     * 
     * @see net.rim.device.api.lowmemory.LowMemoryListener#freeStaleObject(int)
     */
    public boolean freeStaleObject(final int priority) {
        boolean freedData = false;

        switch (priority) {
        case LowMemoryListener.LOW_PRIORITY: {
            // Low priority; application should consider releasing transitory
            // variables and
            // any variables that are currently unnecessary for complete
            // functionality, such as
            // cached data. This sample application does not cache any
            // variables, so no data
            // is recovered.
            break;
        }

        case LowMemoryListener.MEDIUM_PRIORITY: {
            // Medium priority; application should consider removing stale data,
            // such as
            // very old email messages or old calendar appointments. This
            // application traverses
            // the list of order records, and removes any that occurred more
            // than 15 years ago,
            // i.e., very old (stale) order records.
            final int numYearsAgo = 15;

            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            final int year = calendar.get(Calendar.YEAR);
            calendar.set(Calendar.YEAR, year - numYearsAgo);
            freedData =
                    _orderList.removeStaleOrderRecords(calendar.getTime()
                            .getTime());
            _orderListField.setSize(_orderList.getNumOrderRecords());

            break;
        }

        case LowMemoryListener.HIGH_PRIORITY: {
            // High priority; application should remove objects on a Least
            // Recently Used basis.
            // The application should remove *all* its stale objects to reduce
            // the amount of
            // memory consumed on the handheld. This application traverses the
            // list of customer
            // records, and removes any that haven't been accessed in the last
            // 10 years,
            // i.e., the least recently used customer records.
            final int numYearsAgo = 10;

            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            final int year = calendar.get(Calendar.YEAR);
            calendar.set(Calendar.YEAR, year - numYearsAgo);
            freedData =
                    _orderList.removeStaleOrderRecords(calendar.getTime()
                            .getTime());
            _orderListField.setSize(_orderList.getNumOrderRecords());

            break;
        }
        }

        return freedData;
    }

    /**
     * List field that has a custom context menu
     */
    private final class OrderListField extends ListField {
        OrderListField(final int numEntries) {
            super(numEntries);
        }

        /**
         * Displays this list field's custom context menu. If there is at least
         * one item in the list, there are options to act upon the selected item
         * or run LowMemoryManager tasks. If there are less than the maximum
         * number of records, there is an option to fill up the list with random
         * data.
         * 
         * @return The newly-created context menu
         */
        public ContextMenu getContextMenu() {
            final ContextMenu contextMenu = super.getContextMenu();

            if (getSize() > 0) {
                final int index = getSelectedIndex();
                contextMenu.addItem(new View(index));
                contextMenu.addItem(new Edit(index));
                contextMenu.addItem(new Delete(index));
                contextMenu.addItem(new DeleteAll());
                contextMenu.addItem(new SimulateLmmLow());
                contextMenu.addItem(new SimulateLmmMedium());
                contextMenu.addItem(new SimulateLmmHigh());
            }

            if (getSize() < /* outer. */MAX_RECORDS) {
                contextMenu.addItem(new Populate());
            }

            return contextMenu;
        }
    }

    /**
     * A menu item to view a record
     */
    private final class View extends MenuItem {
        private final int _index;

        private View(final int index) {
            super(new StringProvider("View"), 0x230020, 1);
            _index = index;
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    viewRecord(_index);
                }
            }));
        }
    }

    /**
     * A menu item to edit a record
     */
    private final class Edit extends MenuItem {
        private final int _index;

        private Edit(final int index) {
            super(new StringProvider("Edit"), 0x230030, 2);
            _index = index;
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    OrderRecord orderRecord =
                            (OrderRecord) /* outer. */get(_orderListField,
                                    _index);
                    final MemoryDemoOrderScreen screen =
                            new MemoryDemoOrderScreen(orderRecord, true);
                    _app.pushModalScreen(screen);
                    orderRecord = screen.getUpdatedOrderRecord();

                    if (orderRecord != null) {
                        _orderList.replaceOrderRecordAt(_index, orderRecord);
                    }
                }
            }));
        }
    }

    /**
     * A menu item to delete a record
     */
    private final class Delete extends MenuItem {
        private final int _index;

        private Delete(final int index) {
            super(new StringProvider("Delete"), 0x230040, 3);
            _index = index;
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {

                        final OrderRecord orderRecord =
                                (OrderRecord) get(_orderListField, _index);
                        _orderList.deleteOrderRecord(orderRecord);
                        _orderListField
                                .setSize(_orderList.getNumOrderRecords());
                    }
                }
            }));
        }
    }

    /**
     * A menu item to delete all records in the list field
     */
    private final class DeleteAll extends MenuItem {
        private DeleteAll() {
            super(new StringProvider("Delete All"), 0x230050, 4);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
                        _orderList.deleteAllOrderRecords();
                        _orderListField.setSize(0);
                    }
                }
            }));
        }
    }

    /**
     * A menu item to populate the list field
     */
    private final class Populate extends MenuItem {
        private Populate() {
            super(new StringProvider("Populate"), 0x230010, 0);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    _progressDialog =
                            new ProgressBarDialog(
                                    "Generating order records...", _orderList
                                            .getNumRecordsToAdd(MAX_RECORDS));

                    new Thread(new Runnable() {
                        public void run() {
                            _orderList.populate(MAX_RECORDS, _progressDialog);

                            UiApplication.getUiApplication().invokeLater(
                                    new Runnable() {
                                        public void run() {
                                            _orderListField.setSize(_orderList
                                                    .getNumOrderRecords());
                                        }
                                    });
                        }
                    }).start();
                }
            }));
        }
    }

    /**
     * A clas that creates a popup dialog box containing a gauge field to
     * display the progress as the list is populated.
     */
    static class ProgressBarDialog implements CountAndSortListener {
        private final DialogFieldManager _manager;
        private final PopupScreen _popupScreen;
        private final GaugeField _gaugeField;
        private final LabelField _lbfield;
        private final int _max;
        private final int _stepSize;

        /**
         * Creates a new ProgressBarDialog object
         * 
         * @param title
         *            Text to display on _popupScreen.
         * @param max
         *            Maximum value of the range _gaugeField can display.
         */
        private ProgressBarDialog(final String title, final int max) {
            _max = max;

            // Make sure that step size is at least one
            _stepSize = Math.max(_max / 100, 1);

            _manager = new DialogFieldManager();
            _popupScreen = new PopupScreen(_manager);
            _gaugeField = new GaugeField(null, 0, max, 0, GaugeField.PERCENT);
            _lbfield = new LabelField(title, Field.USE_ALL_WIDTH);

            _manager.addCustomField(_lbfield);
            _manager.addCustomField(_gaugeField);

            UiApplication.getUiApplication().pushScreen(_popupScreen);
        }

        /**
         * @see com.rim.samples.device.memorydemo.CountAndSortListener#counterUpdated(int)
         */
        public void counterUpdated(final int counter) {
            // Update _gaugeField if at least one percent of the records have
            // been processed
            // since the last time _gaugeField was updated e.g. if we have
            // 60,000 records
            // to add , _gaugeField is updated once every 600 calls to
            // counterUpdated().
            // Similarly , if we have 50 records to add , an update occurs once
            // every
            // 50/100 = 0.5 calls = every call to counterUpdated().
            // This is done to optimize the code, when the progress dialog is
            // called.
            if (counter % _stepSize == 0) {
                _gaugeField.setValue(counter + 1);
            }
        }

        /**
         * @see com.rim.samples.device.memorydemo.CountAndSortListener#sortingStarted()
         */
        public void sortingStarted() {
            // Remove _gaugeField and change the text displayed on _popupScreen
            // to
            // "Sorting records..." .
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    _manager.deleteCustomField(_gaugeField);
                    _lbfield.setText("Sorting records...");
                }
            });
        }

        /**
         * @see com.rim.samples.device.memorydemo.CountAndSortListener#sortingStarted()
         */
        public void sortingFinished() {
            // Remove _popupScreen from the stack
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    UiApplication.getUiApplication().popScreen(_popupScreen);
                }
            });
        }
    }

    /**
     * A menu item to simulate the execution of the Low Memory Manager with Low
     * priority.
     */
    private final class SimulateLmmLow extends MenuItem {
        /**
         * Creates a new SimulateLmmLow object
         */
        private SimulateLmmLow() {
            super(new StringProvider("Simulate LMM Low"), 0x330000, 5);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    freeStaleObject(LowMemoryListener.LOW_PRIORITY);
                }
            }));
        }
    }

    /**
     * A menu item to simulate the execution of the Low Memory Manager with
     * Medium priority.
     */
    private final class SimulateLmmMedium extends MenuItem {
        /**
         * Creates a new SimulateLmmMedium object
         */
        private SimulateLmmMedium() {
            super(new StringProvider("Simulate LMM Medium"), 0x330010, 6);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    freeStaleObject(LowMemoryListener.MEDIUM_PRIORITY);
                }
            }));
        }
    }

    /**
     * A menu item to simulate the execution of the Low Memory Manager with
     * Highpriority.
     */
    private final class SimulateLmmHigh extends MenuItem {
        /**
         * Creates a new SimulateLmmHigh object
         */
        private SimulateLmmHigh() {
            super(new StringProvider("Simulate LMM High"), 0x330020, 7);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    freeStaleObject(LowMemoryListener.HIGH_PRIORITY);
                }
            }));
        }
    }
}

/**
 * Listener for when a count is updated, when sorting has started and when
 * sorting had finished.
 */
interface CountAndSortListener {
    /**
     * Called when the counter is updated
     * 
     * @param counter
     *            The new counter
     */
    public void counterUpdated(int counter);

    /**
     * Called when sorting is started
     */
    public void sortingStarted();

    /**
     * Called when sorting is finished
     */
    public void sortingFinished();
}
