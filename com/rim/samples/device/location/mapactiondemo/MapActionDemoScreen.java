/**
 * MapActionDemoScreen.java
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

package com.rim.samples.device.mapactiondemo;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.lbs.maps.MapConstants;
import net.rim.device.api.lbs.maps.MapDimensions;
import net.rim.device.api.lbs.maps.MapFactory;
import net.rim.device.api.lbs.maps.model.MapPoint;
import net.rim.device.api.lbs.maps.ui.MapAction;
import net.rim.device.api.lbs.maps.ui.RichMapField;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Touchscreen;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.input.InputSettings;
import net.rim.device.api.ui.input.TouchscreenSettings;
import net.rim.device.api.util.StringProvider;

/**
 * MainScreen class for the MapActionDemo. Menu items allow map panning and/or
 * zooming to be disabled or enabled.
 */
public class MapActionDemoScreen extends MainScreen implements
        FieldChangeListener {

    private final LabelField _latField;
    private final LabelField _lonField;
    private final LabelField _zoomField;

    private final RichMapField _map;
    private final RestrictedMapAction _restrictedMapAction;

    /**
     * Creates a new MapActionDemoScreen object
     */
    public MapActionDemoScreen() {
        super(Screen.DEFAULT_CLOSE | Screen.DEFAULT_MENU
                | Manager.NO_VERTICAL_SCROLL);

        setTitle("Map Action Demo");

        // Activate pinch gesturing
        if (Touchscreen.isSupported()) {
            final InputSettings is = TouchscreenSettings.createEmptySet();
            is.set(TouchscreenSettings.DETECT_PINCH, 1);
            this.addInputSettings(is);
        }

        _map = MapFactory.getInstance().generateRichMapField();
        _map.getMapField()
                .setDimensions(
                        new MapDimensions(Display.getWidth(), Display
                                .getHeight() - 130));

        // Register for field updates (when the center and zoom changes)
        _map.getMapField().addChangeListener(this);

        // Change the MapAction class the MapField uses
        _restrictedMapAction = new RestrictedMapAction();
        _map.getMapField().setAction(_restrictedMapAction);

        // Disable shared mode
        _map.getAction().disableOperationMode(MapConstants.MODE_SHARED_FOCUS);

        // Label fields to show the user location info
        _latField = new LabelField("Latitude: ");
        _lonField = new LabelField("Longitude: ");
        _zoomField = new LabelField("Zoom: ");

        add(_map);
        add(_latField);
        add(_lonField);
        add(_zoomField);
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu, int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        final StringBuffer buffer = new StringBuffer();

        if (_restrictedMapAction.isZoomingAllowed()) {
            buffer.append(Characters.CHECK_MARK).append(' ');
        }

        buffer.append("Allow Zooming");

        final MenuItem zoomItem =
                new MenuItem(new StringProvider(buffer.toString()), 0x230010, 0);
        zoomItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                _restrictedMapAction.toggleZooming();
            }

        }));
        menu.add(zoomItem);

        // Reset buffer
        buffer.setLength(0);

        if (_restrictedMapAction.isPanningAllowed()) {
            buffer.append(Characters.CHECK_MARK).append(' ');
        }

        buffer.append("Allow Panning");

        final MenuItem setCenterItem =
                new MenuItem(new StringProvider(buffer.toString()), 0x230020, 1);
        setCenterItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                _restrictedMapAction.togglePanning();
            }

        }));
        menu.add(setCenterItem);
    }

    /**
     * @see net.rim.device.api.ui.Screen#onUiEngineAttached(boolean)
     */
    protected void onUiEngineAttached(final boolean attached) {
        super.onUiEngineAttached(attached);
        if (attached) {
            // Set the location of the map to some random
            // location and set zoom to 5 (this zoom level
            // will be overriden by this application's
            // RestrictedMapAction class).
            _map.getMapField().getAction().setCenterAndZoom(new MapPoint(0, 0),
                    5);
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#onClose()
     */
    public boolean onClose() {
        try {
            // Properly clean up any resources used
            _map.close();
        } catch (final Exception e) {
        }

        return super.onClose();
    }

    /**
     * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field == _map.getMapField()) {
            // Get the map's current dimensions
            final MapDimensions dim = _map.getMapField().getDimensions();

            switch (context) {
            case MapAction.ACTION_CENTER_CHANGE:
                _latField.setText("Latitude: " + dim.getCenter().getLat());
                _lonField.setText("Longitude: " + dim.getCenter().getLon());
                break;
            case MapAction.ACTION_ZOOM_CHANGE:
                _zoomField.setText("Zoom Level: " + dim.getZoom());
                break;
            }
        }
    }
}
