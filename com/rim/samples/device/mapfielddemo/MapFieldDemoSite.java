/*
 * MapFieldDemoSite.java
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

import java.io.IOException;

import javax.microedition.location.Coordinates;

import net.rim.device.api.io.LineReader;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;

/**
 * Site defines what a site is and how it is displayed.
 */
class MapFieldDemoSite {

    // Colors
    private static final int DEFAULT_COLOR = 0x00666666;
    private static final int HIGHLIGHT_COLOR = 0x00FF6666;
    int _color = DEFAULT_COLOR;

    // Address Information
    private int _streetNumber;
    private String _streetName; // Includes street type eg.: Main St., Parklane
                                // Ave.
    private String _city;
    private String _province;
    // private String _code;
    private String _country;

    // Visual Information
    private int _numberOfPoints; // Regarding building shape.
    private int _siteNumber; // Applicable only to sites with more than one
                             // building - 'N\A' otherwise.
    private Coordinates _siteNumberPlacement; // Positioning of the site number
                                              // on map - 'N\A' if there's no
                                              // site number.
    private Coordinates[] _shape;
    private Coordinates[] _highlightableArea; // The area will be highlighted if
                                              // cursor is touching.

    // Other Information
    private boolean _standAloneSite;
    private int _numberOfSites;
    // private Coordinates _campusLocation;
    private final String _siteName; // 'N\A' if there is no name.
    private String _campus;

    // Map associated with the instance of this class.
    private final DemoMapField _map;

    /**
     * Initializes the given parameters.
     * 
     * @param filePath
     *            The file that contains all the site information.
     * @param siteName
     *            The name of this particular site.
     * @param map
     *            The map associated with the instance of this class.
     */
    MapFieldDemoSite(final String filePath, final String siteName,
            final DemoMapField map) {
        _siteName = siteName;
        _map = map;
        formatSite(filePath);
    }

    /**
     * Initializes all the elements that make up a site, determined by the
     * instance variables.
     * 
     * @param filePath
     *            The document that describes all the site's properties.
     */
    public void formatSite(final String filePath) {
        final LineReader lineReader =
                new LineReader(getClass().getResourceAsStream(filePath));

        byte[] line;

        try {
            for (;;) {
                line = lineReader.readLine();

                // Check for end of file.
                if (MapFieldDemoTokenizer.getString(line).equals("EOF")) {
                    break;
                } else {
                    if (MapFieldDemoTokenizer.getString(line).equals(
                            "Site Name:")) {

                        if (MapFieldDemoTokenizer.getString(
                                lineReader.readLine()).equals(_siteName)) {

                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals(
                                    "Is Stand-alone Site:")) {
                                _standAloneSite =
                                        MapFieldDemoTokenizer.getString(
                                                lineReader.readLine()).equals(
                                                "true");
                            }

                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals(
                                    "Campus Display Name:")) {
                                _campus =
                                        MapFieldDemoTokenizer
                                                .getString(lineReader
                                                        .readLine());
                            }

                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals(
                                    "Default Campus Location:")) {
                                Coordinates campusLocation;
                                campusLocation =
                                        MapFieldDemoTokenizer
                                                .getCoordinates(lineReader
                                                        .readLine());
                            }

                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals(
                                    "Street Number:")) {
                                _streetNumber =
                                        MapFieldDemoTokenizer.getInt(lineReader
                                                .readLine());
                            }

                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals(
                                    "Street Name:")) {
                                _streetName =
                                        MapFieldDemoTokenizer
                                                .getString(lineReader
                                                        .readLine());
                            }

                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals("City:")) {
                                _city =
                                        MapFieldDemoTokenizer
                                                .getString(lineReader
                                                        .readLine());
                            }

                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals("Province:")) {
                                _province =
                                        MapFieldDemoTokenizer
                                                .getString(lineReader
                                                        .readLine());
                            }

                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals("Country:")) {
                                _country =
                                        MapFieldDemoTokenizer
                                                .getString(lineReader
                                                        .readLine());
                            }

                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals(
                                    "Site Number:")) { // N\A if not applicable.
                                line = lineReader.readLine();

                                if (!MapFieldDemoTokenizer.getString(line)
                                        .equals("N\\A")) {
                                    _siteNumber =
                                            MapFieldDemoTokenizer.getInt(line);
                                }
                            }

                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals(
                                    "Site Number Placement:")) {
                                // N\A if not applicable.
                                line = lineReader.readLine();

                                if (!MapFieldDemoTokenizer.getString(line)
                                        .equals("N\\A")) {
                                    _siteNumberPlacement =
                                            MapFieldDemoTokenizer
                                                    .getCoordinates(line);
                                }
                            }

                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals(
                                    "Number of Points:")) {
                                _numberOfPoints =
                                        MapFieldDemoTokenizer.getInt(lineReader
                                                .readLine());
                            }

                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals("Shape:")) {
                                _shape = new Coordinates[_numberOfPoints];

                                for (int pointsCount = 0; pointsCount < _numberOfPoints; pointsCount++) {
                                    _shape[pointsCount] =
                                            MapFieldDemoTokenizer
                                                    .getCoordinates(lineReader
                                                            .readLine());
                                }
                            }

                            if (MapFieldDemoTokenizer.getString(
                                    lineReader.readLine()).equals(
                                    "Highlightable Area:")) {
                                _highlightableArea = new Coordinates[4];

                                for (int pointsCount = 0; pointsCount < 4; pointsCount++) {
                                    _highlightableArea[pointsCount] =
                                            MapFieldDemoTokenizer
                                                    .getCoordinates(lineReader
                                                            .readLine());
                                }
                            }

                            break;
                        }
                    }
                }
            }
        } catch (final IOException e) {
        }
    }

    /**
     * Determines how the site is to be displayed.
     * 
     * @param g
     *            A Graphics obeject.
     */
    public void drawSite(final Graphics g) {
        if (_map != null) {
            final XYPoint[] point = new XYPoint[_numberOfPoints];
            final int[] xPts = new int[_numberOfPoints];
            final int[] yPts = new int[_numberOfPoints];

            // Converts the world coordinates of the map shape into pixels.
            for (int count = 0; count < _numberOfPoints; count++) {
                point[count] = new XYPoint();
                _map.convertWorldToField(_shape[count], point[count]);
            }

            // For use in 'drawFilledPath' below.
            for (int count = 0; count < _numberOfPoints; count++) {
                xPts[count] = point[count].x;
                yPts[count] = point[count].y;
            }

            g.setColor(_color);
            g.drawFilledPath(xPts, yPts, null, null);

            // Will only attempt to display site number if the zoom is 0 or 1.
            if (_map.getZoom() == 0) {
                // Display site number if available.
                if (_siteNumberPlacement != null) {
                    g.setColor(Color.WHITE);

                    final XYPoint relativePlacement = new XYPoint();
                    _map.convertWorldToField(_siteNumberPlacement,
                            relativePlacement);
                    g.drawText("" + _siteNumber, relativePlacement.x,
                            relativePlacement.y);
                }
            }
        }
    }

    /**
     * Returns a boolean determining whether the site is highlighted or not.
     * 
     * @return True if the site is highlighted, otherwise false.
     */
    public boolean isHighlighted() {
        return _color == HIGHLIGHT_COLOR;
    }

    /**
     * Highlights or removes the highlight on the site.
     * 
     * @param value
     *            True sets the highlight and false removes it.
     */
    public void setHighlight(final boolean value) {
        if (value) {
            _color = HIGHLIGHT_COLOR;
        } else {
            _color = DEFAULT_COLOR;
        }
    }

    /**
     * Returns the site name.
     * 
     * @return Site name.
     */
    public String getSiteName() {
        return _siteName;
    }

    /**
     * Returns the street number.
     * 
     * @return Street number.
     */
    public int getStreetNumber() {
        return _streetNumber;
    }

    /**
     * Returns the street name.
     * 
     * @return Street name.
     */
    public String getStreetName() {
        return _streetName;
    }

    /**
     * Returns the city.
     * 
     * @return City.
     */
    public String getCity() {
        return _city;
    }

    /**
     * Returns the province
     * 
     * @return Province
     */
    public String getProvince() {
        return _province;
    }

    /**
     * Returns the country.
     * 
     * @return Country
     */
    public String getCountry() {
        return _country;
    }

    /**
     * Returns the area that would cause the site to be highlighted if hovered
     * over as real world coordinates in degrees.
     * 
     * @return Highlightable area.
     */
    public Coordinates[] getHighlightableArea() {
        return _highlightableArea;
    }

    /**
     * Returns the campus associated with the site.
     * 
     * @return Campus.
     */
    public String getCampus() {
        return _campus;
    }

    public boolean isStandAloneSite() {
        return _standAloneSite;
    }
}
