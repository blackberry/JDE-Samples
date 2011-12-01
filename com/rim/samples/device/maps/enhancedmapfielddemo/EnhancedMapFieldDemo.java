/*
 * EnhancedMapFieldDemo.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.enhancedmapfielddemo;

import net.rim.device.api.lbs.maps.MapFactory;
import net.rim.device.api.lbs.maps.model.MapDataModel;
import net.rim.device.api.lbs.maps.model.MapLocation;
import net.rim.device.api.lbs.maps.model.MapPoint;
import net.rim.device.api.lbs.maps.model.Mappable;
import net.rim.device.api.lbs.maps.ui.MapAction;
import net.rim.device.api.lbs.maps.ui.MapField;
import net.rim.device.api.lbs.maps.ui.RichMapField;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.decor.BorderFactory;

/**
 * A sample application demonstrating RichMapField and MapDataModel APIs
 */
public final class EnhancedMapFieldDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final EnhancedMapFieldDemo app = new EnhancedMapFieldDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new EnhancedMapFieldDemo object
     */
    public EnhancedMapFieldDemo() {
        pushScreen(new EnhancedMapFieldDemoScreen());
    }

    /**
     * MainScreen class for the EnhancedMapFieldDemo application
     */
    static final class EnhancedMapFieldDemoScreen extends FullScreen implements
            FieldChangeListener {
        private static final int ZOOM_LEVEL = 4;

        private RichMapField _bigMap;
        private MapField _pipMap;

        /**
         * Creates a new EnhancedMapFieldDemoScreen object
         */
        public EnhancedMapFieldDemoScreen() {
            super(Screen.DEFAULT_CLOSE | Screen.DEFAULT_MENU);

            _bigMap = MapFactory.getInstance().generateRichMapField();

            final MapDataModel data = _bigMap.getModel();

            // Create RIM head office location and add it with appropriate tags
            final MapLocation rimOffice =
                    new MapLocation(43.47483, -80.53919, "Research In Motion",
                            "Head office");

            final int rimOfficeID = data.add((Mappable) rimOffice, "rim");

            data.tag(rimOfficeID, "head"); // Locations can have more than one
                                           // tag
            data.setVisible("head");

            final int displayWidth = Display.getWidth();
            final int displayHeight = Display.getHeight();

            // Initialize PIP map
            _pipMap = new MapField(displayWidth / 3, displayHeight / 3) {
                public boolean isFocusable() {
                    return false;
                }
            };
            _pipMap.setBorder(BorderFactory.createSimpleBorder(new XYEdges(2,
                    2, 2, 2)));

            // Add PIP map to the big map
            _bigMap.add(_pipMap, displayWidth
                    - _pipMap.getDimensions().getPixelWidth(), 0);

            _bigMap.getMapField().addChangeListener(this);

            add(_bigMap);
        }

        /**
         * @see Screen#onUiEngineAttached(boolean)
         */
        protected void onUiEngineAttached(final boolean attached) {
            super.onUiEngineAttached(attached);

            if (attached) {
                final MapAction mapAction = _bigMap.getMapField().getAction();

                // Set the location of both the big map and the PIP map
                mapAction.setCentre(new MapPoint(4347483, -8053919));

                mapAction.setZoom(ZOOM_LEVEL);
                _bigMap.setFocus();
            }
        }

        /**
         * @see FieldChangeListener#fieldChanged(Field, int)
         */
        public void fieldChanged(final Field field, final int actionId) {
            if (field == _bigMap.getMapField()) {
                switch (actionId) {
                case MapAction.ACTION_CENTRE_CHANGE:
                    _pipMap.getAction().setCentre(
                            _bigMap.getMapField().getDimensions().getCentre());
                    break;
                case MapAction.ACTION_ZOOM_CHANGE:
                    _pipMap.getAction()
                            .setZoom(
                                    _bigMap.getMapField().getDimensions()
                                            .getZoom() + 2);
                    break;
                }
            }
        }
    }
}
