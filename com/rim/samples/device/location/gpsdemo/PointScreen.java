/*
 * PointScreen.java
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

package com.rim.samples.device.gpsdemo;

import java.util.Date;
import java.util.Vector;

import javax.microedition.location.Coordinates;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.lbs.MapField;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.table.AbstractTableModel;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModelAdapter;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.StringProvider;

import com.rim.samples.device.gpsdemo.GPSDemo.WayPoint;

/**
 * A screen to render saved WayPoints
 */
public class PointScreen extends MainScreen {
    private AbstractTableModel _model = null;
    private TableView _view;
    private TableController _controller;

    private Vector _points;

    /**
     * Creates a new PointScreen object
     * 
     * @param points
     *            Vector of WayPoints
     */
    public PointScreen(final Vector points) {
        super(Manager.NO_VERTICAL_SCROLL);

        setTitle("Previous waypoints");

        _points = points;

        // Construct and populate a model with the WayPoints and their
        // associated indices.
        _model = new WayPointTableModelAdapter();

        final VerticalFieldManager vfm = new VerticalFieldManager() {
            /**
             * @see net.rim.device.api.ui.Screen#keyChar(char, int, int)
             */
            protected boolean keyChar(final char key, final int status,
                    final int time) {
                if (key == Characters.ENTER) {
                    displayWayPoint();
                    return true;
                }

                return super.keyChar(key, status, time);
            }
        };

        // Create the view
        _view = new TableView(_model);

        // Create the controller
        _controller = new TableController(_model, _view);
        _controller.setCommand(new Command(new CommandHandler() {
            /**
             * @see CommandHandler#execute(ReadOnlyCommandMetadata, Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                displayWayPoint();
            }

        }));

        _view.setController(_controller);

        // Set the highlight style for the view
        _view.setDataTemplateFocus(BackgroundFactory
                .createLinearGradientBackground(Color.LIGHTBLUE,
                        Color.LIGHTBLUE, Color.BLUE, Color.BLUE));

        // Create a data template that will format the model data as an array of
        // LabelFields
        final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
            public Field[] getDataFields(final int modelRowIndex) {
                final Field[] fields =
                        { new LabelField("Waypoint " + modelRowIndex,
                                DrawStyle.ELLIPSIS | Field.NON_FOCUSABLE) };

                return fields;
            }
        };

        // Define the regions of the data template and column/row size
        dataTemplate.createRegion(new XYRect(0, 0, 1, 1));
        dataTemplate.setColumnProperties(0, new TemplateColumnProperties(
                Display.getWidth()));
        dataTemplate.setRowProperties(0, new TemplateRowProperties(24));

        _view.setDataTemplate(dataTemplate);
        dataTemplate.useFixedHeight(true);

        vfm.add(_view);
        add(vfm);

        _viewPointAction =
                new MenuItem(new StringProvider("View"), 0x230010, 0);
        _viewPointAction.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                displayWayPoint();
            }
        }));

        _deletePointAction =
                new MenuItem(new StringProvider("Delete"), 0x230020, 1);
        _deletePointAction.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                final int index = _view.getRowNumberWithFocus();
                final int result =
                        Dialog.ask(Dialog.DELETE, "Delete Waypoint " + index
                                + "?");
                if (result == Dialog.YES) {
                    final Object[] row = (Object[]) _model.getRow(index);
                    final GPSDemo.WayPoint wayPoint = (GPSDemo.WayPoint) row[0];
                    GPSDemo.removeWayPoint(wayPoint);

                    _model.removeRowAt(index);
                }
            }
        }));
    }

    /**
     * Displays the selected WayPoint in a new screen
     */
    private void displayWayPoint() {
        final int index = _view.getRowNumberWithFocus();
        final GPSDemo.WayPoint wayPoint =
                (GPSDemo.WayPoint) _model.getRow(index);
        final ViewScreen screen = new ViewScreen(wayPoint, 0);
        UiApplication.getUiApplication().pushScreen(screen);
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu, int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        // Menu items should only be displayed if there are items in the table
        if (_model.getNumberOfRows() > 0) {
            menu.add(_viewPointAction);
            menu.add(_deletePointAction);
        }
        super.makeMenu(menu, instance);
    }

    /**
     * Displays the selected WayPoint
     */
    MenuItem _viewPointAction;

    /**
     * Deletes the selected WayPoint
     */
    MenuItem _deletePointAction;

    /**
     * Adapter to display WayPoint data in table format
     */
    private class WayPointTableModelAdapter extends TableModelAdapter {
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows() {
            return _points.size();
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
        public Object doGetRow(final int rowIndex) {
            return _points.elementAt(rowIndex);
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doRemoveRowAt(int)
         */
        public boolean doRemoveRowAt(final int rowIndex) {
            _points.removeElementAt(rowIndex);
            return true;
        }
    };

    /**
     * A screen to render a particular WayPoint's information
     */
    private static class ViewScreen extends MainScreen {
        /**
         * Constructs a ViewScreen to view a specified WayPoint
         * 
         * @param point
         *            The WayPoint to view
         * @param count
         *            The WayPoint number
         */
        ViewScreen(final WayPoint point, final int count) {
            setTitle("Waypoint" + count);

            Date date = new Date(point._startTime);
            final String startTime = date.toString();
            date = new Date(point._endTime);
            final String endTime = date.toString();

            // Calculate average speed travelled enroute to WayPoint
            final float avgSpeed =
                    point._distance / (point._endTime - point._startTime);

            add(new RichTextField("Start: " + startTime, Field.NON_FOCUSABLE));
            add(new RichTextField("End: " + endTime, Field.NON_FOCUSABLE));
            add(new RichTextField("Horizontal Distance (m): "
                    + Float.toString(point._distance), Field.NON_FOCUSABLE));
            add(new RichTextField("Vertical Distance (m): "
                    + Float.toString(point._verticalDistance),
                    Field.NON_FOCUSABLE));
            add(new RichTextField("Average Speed(m/s): "
                    + Float.toString(avgSpeed), Field.NON_FOCUSABLE));

            // Display the WayPoint on a map
            final MapField map = new MapField();
            map.moveTo(new Coordinates(point._latitude, point._longitude,
                    Float.NaN));
            add(map);
        }
    }
}
