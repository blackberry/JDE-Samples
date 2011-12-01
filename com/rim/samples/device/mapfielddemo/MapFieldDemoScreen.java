/*
 * MapFieldDemoScreen.java
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

package com.rim.samples.device.mapfielddemo;

import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.location.Coordinates;

import net.rim.device.api.io.LineReader;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Handles the display of MapFieldDemo's UI elements.
 */
class MapFieldDemoScreen extends MainScreen {

    // For display
    private final DemoMapField _map;
    private LabelField _addressBar1;
    private LabelField _addressBar2;

    // For campuses
    private Vector _campusCoordinates; // <Coordinates>
    private Vector _campusMenuItems; // <SiteDisplayMenuItem>
    private Vector _campuses; // <String>
    private String _currentCampus;
    private String _currentMenuCampus;

    // For sites
    private Vector _sites; // <Site>
    private Vector _siteNames; // <String>
    private MapFieldDemoSite _currentSite;

    // For keypad/trackball
    private boolean _reducedKeypad = false;
    private final boolean _checkedTrackballSupport = false;

    /**
     * Sets up screen elements and initializes the map.
     */
    MapFieldDemoScreen() {
        final String siteName;
        final MapFieldDemoSite defaultSite;
        Coordinates defaultLocation;
        final byte[] line;

        // Reads data from file.
        final LineReader lineReader =
                new LineReader(getClass().getResourceAsStream("/MapsData.txt"));

        // Determine keypad type.
        if (Keypad.getHardwareLayout() == Keypad.HW_LAYOUT_REDUCED
                || Keypad.getHardwareLayout() == Keypad.HW_LAYOUT_REDUCED_24) {
            _reducedKeypad = true;
        }

        // Maps instance for screen.
        _map = new DemoMapField(_reducedKeypad);

        storeAllSites("/MapsData.txt");
        storeAllCampuses("/MapsData.txt");

        // Look for the default site.
        try {
            for (;;) {
                if (MapFieldDemoTokenizer.getString(lineReader.readLine())
                        .equals("Default Campus:")) {
                    _currentCampus =
                            MapFieldDemoTokenizer.getString(lineReader
                                    .readLine());

                    // Find campus location.
                    for (;;) {
                        if (MapFieldDemoTokenizer.getString(
                                lineReader.readLine()).equals(
                                "Campus Display Name:")) {
                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals(
                                    _currentCampus)) {
                                if (MapFieldDemoTokenizer.getString(
                                        lineReader.readLine()).equals(
                                        "Default Campus Location:")) {
                                    defaultLocation =
                                            MapFieldDemoTokenizer
                                                    .getCoordinates(lineReader
                                                            .readLine());
                                    break;
                                }
                            }
                        }
                    }

                    _map.moveTo(defaultLocation);
                    break;
                }
            }

            // Add address labels.
            _addressBar1 = new LabelField();
            _addressBar2 = new LabelField();

            // Display all UI.
            add(_map);
            add(_addressBar1);
            add(_addressBar2);

            displayTitle(_currentCampus);
            createMenuItems();
        } catch (final IOException e) {
        }
    }

    /**
     * Finds all sites in file and stores them.
     * 
     * @param MapData
     *            Location of file containing site information.
     */
    private void storeAllSites(final String MapData) {
        final LineReader lineReader =
                new LineReader(getClass().getResourceAsStream(MapData));
        byte[] line;

        // For sites
        _siteNames = new Vector();
        _sites = new Vector();
        String currentSiteName;

        for (;;) {
            try {
                line = lineReader.readLine();

                if (MapFieldDemoTokenizer.getString(line).equals("Site Name:")) {
                    // Initialize site variables.
                    currentSiteName =
                            MapFieldDemoTokenizer.getString(lineReader
                                    .readLine());
                    _siteNames.addElement(currentSiteName);
                    _currentSite =
                            new MapFieldDemoSite(MapData, currentSiteName, _map);
                    _map.addSite(_currentSite);
                    _sites.addElement(_currentSite);

                    // Set campus.
                    _currentCampus = _currentSite.getCampus();
                }
            } catch (final EOFException eof) {
                // We've reached the end of the file.
                break;
            } catch (final IOException ioe) {
                System.out.println("Error reading data from file");
                break;
            }
        }
    }

    /**
     * Finds all campuses in file and stores them
     * 
     * @param MapData
     *            Location of file containing site information.
     */
    private void storeAllCampuses(final String MapData) {
        final LineReader lineReader =
                new LineReader(getClass().getResourceAsStream(MapData));
        byte[] line;

        // For campuses
        String campusName;
        _campuses = new Vector();
        _campusCoordinates = new Vector();

        for (;;) {
            try {
                line = lineReader.readLine();

                if (MapFieldDemoTokenizer.getString(line).equals(
                        "Campus Display Name:")) {
                    campusName =
                            MapFieldDemoTokenizer.getString(lineReader
                                    .readLine());

                    // Make sure that there are no duplicate campuses in vector.
                    if (!_campuses.contains(campusName)) {
                        _campuses.addElement(campusName);

                        // Adds the campus' location.
                        if (MapFieldDemoTokenizer.getString(
                                lineReader.readLine()).equals(
                                "Default Campus Location:")) {
                            _campusCoordinates.addElement(MapFieldDemoTokenizer
                                    .getCoordinates(lineReader.readLine()));
                        }
                    }
                }
            } catch (final EOFException eof) {
                // We've reached the end of the file.
                break;
            } catch (final IOException ioe) {
                System.out.println("Error reading data from file");
                break;
            }
        }
    }

    /**
     * Creates menu items according to the campuses present.
     */
    private void createMenuItems() {
        final int defaultPriority = 1000;
        int order = 1000;
        _campusMenuItems = new Vector();

        for (int count = 0; count < _campuses.size(); count++) {
            _currentMenuCampus = (String) _campuses.elementAt(count);
            final String menuDisplay = "Go to " + _currentMenuCampus;

            _campusMenuItems.addElement(new MapFieldDemoMenuItem(menuDisplay,
                    _currentMenuCampus, order++, defaultPriority) {

                // If the map displays a current campus, that campus' menu item
                // will set isValid to false.
                protected boolean isValid() {
                    return !_currentMenuCampus.equals(_currentCampus);
                }

                public void run() {
                    _map.turnOffText();
                    _currentCampus = this.getCampus();

                    // Moves the map to the current campus.
                    _map.moveTo((Coordinates) _campusCoordinates
                            .elementAt(_campuses.indexOf(_currentCampus)));
                    displayTitle(_currentCampus);
                }
            });
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#makeMenu(Menu, int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        for (int count = 0; count < _campusMenuItems.size(); count++) {
            // Resets the current campus.
            _currentMenuCampus =
                    ((MapFieldDemoMenuItem) _campusMenuItems.elementAt(count))
                            .getCampus();

            if (((MapFieldDemoMenuItem) _campusMenuItems.elementAt(count))
                    .isValid()) {
                menu.add((MapFieldDemoMenuItem) _campusMenuItems
                        .elementAt(count));
            }
        }

        super.makeMenu(menu, instance);
    }

    /**
     * Displays the title according to the current campus.
     * 
     * @param campus
     *            Name of the campus.
     */
    public void displayTitle(final String campus) {
        setTitle(new LabelField("Welcome to " + campus, Field.USE_ALL_WIDTH
                | DrawStyle.HCENTER));
    }

    /**
     * Displays information at the bottom of the screen when a site is hovered
     * over.
     */
    private void displayText() {
        final MapFieldDemoSite highlightedSite = _map.getHighlightedSite();

        if (highlightedSite != null && highlightedSite.isHighlighted()) {
            // Displays site name if it's not a stand-alone site.
            if (!highlightedSite.isStandAloneSite()) {
                _addressBar1.setText(highlightedSite.getSiteName() + ": "
                        + highlightedSite.getStreetNumber() + " "
                        + highlightedSite.getStreetName());
            } else {
                _addressBar1.setText(highlightedSite.getStreetNumber() + " "
                        + highlightedSite.getStreetName());
            }

            _addressBar2.setText(highlightedSite.getCity() + ", "
                    + highlightedSite.getProvince() + ", "
                    + highlightedSite.getCountry());
        } else {
            // Displays the following if no site is currently highlighted.
            _addressBar1.setText("No highlighted site...");
            _addressBar2.setText("");
        }
    }

    /**
     * @see net.rim.device.api.ui.Field#paint(Graphics)
     */
    protected void paint(final Graphics g) {
        super.paint(g);

        displayText();
    }

    /**
     * @see net.rim.device.api.ui.Field#keyChar(char, int, int)
     */
    protected boolean keyChar(final char character, final int status,
            final int time) {
        // 'i' will zoom in.
        if (character == 'i') {
            if (!_reducedKeypad) {

                if (_map.getZoom() != _map.getMinZoom()) {
                    _map.setZoom(_map.getZoom() - 1);
                }

                return true;
            }
        }

        // 'l' will zoom in on reduced layouts.
        if (character == 'l') {
            if (_reducedKeypad) {
                if (_map.getZoom() != _map.getMinZoom()) {
                    _map.setZoom(_map.getZoom() - 1);
                }

                return true;
            }
        }

        // 'o' will zoom out
        if (character == 'o') {
            if (_map.getZoom() != _map.getMaxZoom()) {
                _map.setZoom(_map.getZoom() + 1);
            }

            return true;
        }

        return super.keyChar(character, status, time);
    }

    /**
     * @see net.rim.device.api.ui.Field#navigationMovement(int, int, int, int)
     */
    protected boolean navigationMovement(final int dx, final int dy,
            final int status, final int time) {
        if (!_checkedTrackballSupport) {
            // Allows smoother panning on the map.
            if (Trackball.isSupported()) {
                // Adjust the filter.
                getScreen().setTrackballFilter(
                        Trackball.FILTER_NO_TIME_WINDOW
                                | Trackball.FILTER_ACCELERATION);
            }
        }

        // Delegates this task to the map instance.
        return _map.navigationMovement(dx, dy, status, time);
    }
}
