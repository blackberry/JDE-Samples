/*
 * EnhancedMapFieldDemo.java
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

package com.rim.samples.device.enhancedmapfielddemo;

import net.rim.device.api.lbs.maps.MapFactory;
import net.rim.device.api.lbs.maps.model.MapDataModel;
import net.rim.device.api.lbs.maps.model.MapLocation;
import net.rim.device.api.lbs.maps.model.MapPoint;
import net.rim.device.api.lbs.maps.model.MapSimplePolygon;
import net.rim.device.api.lbs.maps.model.geospatial.GsImage;
import net.rim.device.api.lbs.maps.ui.MapAction;
import net.rim.device.api.lbs.maps.ui.MapField;
import net.rim.device.api.lbs.maps.ui.RichMapField;
import net.rim.device.api.lbs.maps.view.Style;
import net.rim.device.api.lbs.maps.view.StyleSet;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
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
        private static final double ORIGIN_LATITUDE = 43.47583;
        private static final double ORIGIN_LONGITUDE = -80.54019;

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
                    new MapLocation(ORIGIN_LATITUDE, ORIGIN_LONGITUDE,
                            "Research In Motion", "Head office");

            // Set styles for MapLocation using class based style
            final StyleSet styles = _bigMap.getMapField().getDrawingStyles();
            final Style classBasedStyle = new Style();
            classBasedStyle.setLabelFillColor(Color.BLACK);
            classBasedStyle.setLabelFontColor(Color.WHITE);
            classBasedStyle.setLabelFontStyle(Font.BOLD);
            styles.addClassBasedStyle(MapLocation.class, classBasedStyle);

            final int rimOfficeID = data.add(rimOffice, "rim");

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

            addDataToMap();

            add(_bigMap);
        }

        private void addDataToMap() {
            // Polygon will be rendered solid white with a black border if we
            // don't change the style.
            final MapPoint[] points = new MapPoint[6];
            points[0] = new MapPoint(ORIGIN_LATITUDE, ORIGIN_LONGITUDE);
            points[1] =
                    new MapPoint(ORIGIN_LATITUDE - 0.05,
                            ORIGIN_LONGITUDE + 0.05);
            points[2] =
                    new MapPoint(ORIGIN_LATITUDE - 0.1,
                            ORIGIN_LONGITUDE + 0.025);
            points[3] =
                    new MapPoint(ORIGIN_LATITUDE - 0.1,
                            ORIGIN_LONGITUDE - 0.025);
            points[4] =
                    new MapPoint(ORIGIN_LATITUDE - 0.05,
                            ORIGIN_LONGITUDE - 0.05);

            // Close the polygon
            points[5] = new MapPoint(ORIGIN_LATITUDE, ORIGIN_LONGITUDE);

            final MapSimplePolygon poly = new MapSimplePolygon(points);
            poly.setStyleId("poly");
            _bigMap.getModel().add(poly, "Polygon", true);

            // Add a GsImage
            final GsImage img = new GsImage();
            img.setIconUri("http://na.blackberry.com/eng/developers/logo_black.jpg");
            img.setLat(43.49000);
            img.setLon(-80.53919);
            img.setName("Logo");
            img.setDescription("Rim logo");
            _bigMap.getModel().add(img, "Image", true);

            // Change the style so we can see the content under the polygon.
            // Use ID based style.
            final StyleSet styles = _bigMap.getMapField().getDrawingStyles();
            final Style idBasedStyle = new Style();
            idBasedStyle.setEdgeColor(Color.RED);
            idBasedStyle.setEdgeOpacity(255);
            idBasedStyle.setFillColor(Color.RED);
            idBasedStyle.setFillOpacity(50);
            styles.addIdBasedStyle("poly", idBasedStyle);
        }

        /**
         * @see Screen#onUiEngineAttached(boolean)
         */
        protected void onUiEngineAttached(final boolean attached) {
            super.onUiEngineAttached(attached);

            if (attached) {
                final MapAction mapAction = _bigMap.getMapField().getAction();

                // Set the location of both the big map and the PIP map
                mapAction.setCenter(new MapPoint(43.47483, -80.53919));

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
                case MapAction.ACTION_CENTER_CHANGE:
                    _pipMap.getAction().setCenter(
                            _bigMap.getMapField().getDimensions().getCenter());
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
