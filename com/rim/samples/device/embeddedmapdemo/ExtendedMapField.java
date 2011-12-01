/**
 * ExtendedMapField.java
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

package com.rim.samples.device.embeddedmapdemo;

import java.util.Vector;

import net.rim.device.api.lbs.MapField;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

final class ExtendedMapField extends MapField {
    private static final int INITIAL_ZOOM = 2;
    private static final double MARGIN_OF_ERROR = 0.00001;

    private final MapLocation _initialLocation;
    private final Bitmap _cursor; // Central pointer.
    private final Bitmap _marker; // Marker at selected location.
    private final Vector _mapLocations; // Vector from EmbeddedMapDemo class.

    private final Border _toggledBorder;
    private final Border _untoggledBorder;

    private boolean _clicked; // Clicked by trackball.

    ExtendedMapField(final MapLocation initialLocation,
            final Vector mapLocations) {
        super(Field.FOCUSABLE | Field.FIELD_HCENTER);

        // Leave space for other UI fields.
        final int preferredHeight = (int) (Display.getHeight() * 0.65);
        setPreferredSize(getPreferredWidth(), preferredHeight);

        _initialLocation = initialLocation;
        _mapLocations = mapLocations;
        _cursor = Bitmap.getBitmapResource("pointer.png");
        _marker = Bitmap.getBitmapResource("marker.PNG");

        _initialLocation.setVisible(true);

        final XYEdges thickBorder = new XYEdges(5, 5, 5, 5);
        final XYEdges toggledColour =
                new XYEdges(Color.HOTPINK, Color.HOTPINK, Color.HOTPINK,
                        Color.HOTPINK);
        final XYEdges untoggledColour =
                new XYEdges(Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE);
        _toggledBorder =
                BorderFactory.createSimpleBorder(thickBorder, toggledColour,
                        Border.STYLE_SOLID);
        _untoggledBorder =
                BorderFactory.createSimpleBorder(thickBorder, untoggledColour,
                        Border.STYLE_SOLID);

        resetMap();
    }

    void resetMap() {
        moveTo(_initialLocation);
        setZoom(INITIAL_ZOOM);
    }

    /* Toggles the map on or off and changes the border to match. */
    private void toggleMap() {
        _clicked = !_clicked;
        if (_clicked) {
            setBorder(_toggledBorder);
        } else {
            setBorder(_untoggledBorder);
        }
        invalidate();
    }

    void activatePan() {
        _clicked = true;
        invalidate();
    }

    /**
     * @see net.rim.device.api.ui.Field#keyChar(char, int, int)
     */
    protected boolean keyChar(final char character, final int status,
            final int time) {
        // 'i' or 'l' will zoom in.
        if (character == 'i' || character == 'l') {
            setZoom(Math.max(getZoom() - 1, getMinZoom()));
            return true;
        } else if (character == 'o') { // 'o' will zoom out
            setZoom(Math.min(getZoom() + 1, getMaxZoom()));
            return true;
        } else if (character == Characters.ENTER) {
            // Toggle activation.
            toggleMap();
            return true;
        }

        return super.keyChar(character, status, time);
    }

    /**
     * @see net.rim.device.api.ui.Field#navigationMovement(int, int, int, int)
     */
    protected boolean navigationMovement(final int dx, final int dy,
            final int status, final int time) {
        // Shift only if trackball was clicked (panning activated).
        if (_clicked) {
            move(dx << 3, dy << 3);

            return true;
        }

        return super.navigationMovement(dx, dy, status, time);
    }

    /**
     * Override the onUnfocus method to get rid of the border when losing focus.
     * 
     * @see net.rim.device.api.ui.Field#onUnfocus()
     */
    protected void onUnfocus() {
        super.onUnfocus();

        setBorder(null);
    }

    /**
     * Override the onFocus method to add the border upon getting focus.
     * 
     * @see net.rim.device.api.ui.Field#onFocus(int)
     */
    protected void onFocus(final int direction) {
        super.onFocus(direction);

        setBorder(_untoggledBorder);
    }

    /**
     * @see net.rim.device.api.lbs.MapField#paint(Graphics)
     */
    public void paint(final Graphics g) {
        super.paint(g);

        // DRAWING THE POINTER.
        // Place the cursor permanently at the center of the map.
        // Logical right shift ">> 1" is equivalent to division by 2.
        final int width = getWidth();
        final int height = getHeight();
        g.drawBitmap(width >> 1, height >> 1, width, height, _cursor, 0, 0);

        // DRAWING RED MARKER & TEXT.
        if (_mapLocations != null) {

            for (int i = _mapLocations.size() - 1; i >= 0; --i) {
                final MapLocation mapLocation =
                        (MapLocation) _mapLocations.elementAt(i);

                if (mapLocation.isVisible()) {
                    final XYPoint relativePlacement = new XYPoint();
                    convertWorldToField(mapLocation, relativePlacement);
                    final int scale = (getZoom() + 1) * 8; // +1 in case zoom ==
                                                           // 0
                    g.drawBitmap(relativePlacement.x, relativePlacement.y,
                            width / scale, height / (scale / 2), _marker, 0, 0);

                    if (getZoom() == getMinZoom()) {
                        g.setColor(Color.WHITE);
                        final String text = mapLocation.getName();
                        g.drawText(text, relativePlacement.x + 10,
                                relativePlacement.y + 12);
                    }

                    break;
                }
            }
        }
    }

    /**
     * Handles a trackball click.
     * 
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    public boolean invokeAction(final int action) {
        switch (action) {
        case ACTION_INVOKE: // Trackball click.
            toggleMap();
            return true; // We've consumed the event.
        }

        return super.invokeAction(action);
    }

    void displayLocation(final int index) {
        MapLocation mapLocation;
        for (int i = _mapLocations.size() - 1; i >= 0; --i) {
            mapLocation = (MapLocation) _mapLocations.elementAt(i);

            if (i == index) {
                mapLocation.setVisible(true);
                moveTo(mapLocation);
            } else {
                mapLocation.setVisible(false);
            }
        }

        setZoom(INITIAL_ZOOM);
    }

    /**
     * Checks the current vector for the given location, based on latitude and
     * longitude.
     * 
     * @param location
     *            - the location we want to search for.
     * @return - the index of the location if found, -1 if not.
     */
    int checkForLocation(final MapLocation location) {
        final double locLatitude = location.getLatitude();
        final double locLongitude = location.getLongitude();

        for (int i = 0; i < _mapLocations.size(); ++i) {
            final MapLocation cur = (MapLocation) _mapLocations.elementAt(i);

            final double curLatitude = cur.getLatitude();
            final double curLongitude = cur.getLongitude();

            if (curLatitude > locLatitude - MARGIN_OF_ERROR
                    && curLatitude < locLatitude + MARGIN_OF_ERROR
                    && curLongitude > locLongitude - MARGIN_OF_ERROR
                    && curLongitude < locLongitude + MARGIN_OF_ERROR) {
                return i;
            }
        }

        return -1;
    }
}
