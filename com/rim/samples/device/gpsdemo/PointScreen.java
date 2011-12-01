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

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

import com.rim.samples.device.gpsdemo.GPSDemo.WayPoint;

/*
 * PointScreen is a screen derivative that renders the saved WayPoints.
 */
public class PointScreen extends MainScreen implements ListFieldCallback {
    private final Vector _points;
    private final ListField _listField;

    public PointScreen(final Vector points) {

        final LabelField title =
                new LabelField("Previous WayPoints", DrawStyle.ELLIPSIS
                        | Field.USE_ALL_WIDTH);
        setTitle(title);

        _points = points;
        _listField = new ListField();
        _listField.setCallback(this);
        add(_listField);

        reloadWayPointList();
    }

    private void reloadWayPointList() {
        // Refreshes wayPoint list on screen.
        _listField.setSize(_points.size());
    }

    // ListFieldCallback methods
    // -----------------------------------------------------------------------------------
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField
     *      , Graphics , int , int , int)
     */
    public void drawListRow(final ListField listField, final Graphics graphics,
            final int index, final int y, final int width) {
        if (listField == _listField && index < _points.size()) {
            final String name = "Waypoint " + index;
            graphics.drawText(name, 0, y, 0, width);
        }
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField ,
     *      int)
     */
    public Object get(final ListField listField, final int index) {
        if (listField == _listField) {
            // If index is out of bounds an exception will be thrown, but that's
            // the behaviour we want in that case.
            return _points.elementAt(index);
        }

        return null;
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(final ListField listField) {
        // Use all the width of the current LCD.
        return Display.getWidth();
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField
     *      , String , int)
     */
    public int indexOfList(final ListField listField, final String prefix,
            final int start) {
        return -1; // Not implemented.
    }

    // Menu items
    // ---------------------------------------------------------------
    private class ViewPointAction extends MenuItem {
        private final int _index;

        public ViewPointAction(final int index) {
            super("View", 100000, 10);
            _index = index;
        }

        public void run() {
            final ViewScreen screen =
                    new ViewScreen((WayPoint) _points.elementAt(_index), _index);
            UiApplication.getUiApplication().pushModalScreen(screen);
        }
    }

    private class DeletePointAction extends MenuItem {
        private final int _index;

        public DeletePointAction(final int index) {
            super("Delete", 100000, 10);
            _index = index;
        }

        public void run() {
            GPSDemo.removeWayPoint((WayPoint) _points.elementAt(_index));
            reloadWayPointList();
        }
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        if (_points.size() > 0) {
            final ViewPointAction viewPointAction =
                    new ViewPointAction(_listField.getSelectedIndex());
            menu.add(viewPointAction);
            menu.addSeparator();

            final DeletePointAction deletePointAction =
                    new DeletePointAction(_listField.getSelectedIndex());
            menu.add(deletePointAction);
        }

        super.makeMenu(menu, instance);
    }

    /**
     * Renders a particular Waypoint
     */
    private static class ViewScreen extends MainScreen {
        private MenuItem _cancel;

        public ViewScreen(final WayPoint point, final int count) {
            super();

            final LabelField title =
                    new LabelField("Waypoint" + count, DrawStyle.ELLIPSIS
                            | Field.USE_ALL_WIDTH);
            setTitle(title);

            Date date = new Date(point._startTime);
            final String startTime = date.toString();
            date = new Date(point._endTime);
            final String endTime = date.toString();

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
        }

        private class CancelMenuItem extends MenuItem {
            public CancelMenuItem() {
                // Reuse an identical resource below.
                super("Cancel", 300000, 10);
            }

            public void run() {
                final UiApplication uiapp = UiApplication.getUiApplication();
                uiapp.popScreen(ViewScreen.this);
            }
        };

        protected void makeMenu(final Menu menu, final int instance) {
            if (_cancel == null) {
                _cancel = new CancelMenuItem(); // Create on demand.
            }

            menu.add(_cancel);

            super.makeMenu(menu, instance);
        }
    }
}
