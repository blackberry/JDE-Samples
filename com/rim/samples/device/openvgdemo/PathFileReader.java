/*
 * PathFileReader.java
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

package com.rim.samples.device.openvgdemo;

import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;

import net.rim.device.api.io.LineReader;
import net.rim.device.api.openvg.VG10;
import net.rim.device.api.openvg.VG11;
import net.rim.device.api.openvg.VGUtils;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * Class for reading VG path and fill information from a text file
 */
public final class PathFileReader {
    /**
     * Returns an array of Path objects defined in the specified file
     * 
     * @param vg
     *            OpenVG renderer
     * @param file
     *            Name of file from which to obtain path data
     * @return An array of Path objects
     */
    public Path[] getPaths(final VG11 vg, final String file) {
        final Vector vector = new Vector();

        // Read data from file
        final LineReader lineReader =
                new LineReader(getClass().getResourceAsStream(file));

        for (;;) {
            try {
                final String pathString = new String(lineReader.readLine());

                final int vgPath = VGUtils.vgCreatePath(vg, pathString);

                if (vgPath != VG10.VG_INVALID_HANDLE) {
                    // Create the Path object
                    final Path path = new Path(vgPath);

                    int fillPaint;

                    final String fillString = new String(lineReader.readLine());

                    try {
                        // Create the colored fill value
                        fillPaint = VGUtils.vgCreateColorPaint(vg, fillString);
                    } catch (final IllegalArgumentException e) {
                        fillPaint = VGUtils.vgCreateColorPaint(vg, 0xFF000000);
                    }

                    // Set the path's fill value
                    path.setFill(fillPaint);

                    vector.addElement(path);
                }
            } catch (final EOFException eof) {
                // We've reached the end of the file
                break;
            } catch (final IOException ioe) {
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        Dialog.alert(ioe.toString());
                    }
                });

                break;
            }
        }

        // Convert vector to array
        final Path[] svgPathsArray = new Path[vector.size()];
        vector.copyInto(svgPathsArray);

        return svgPathsArray;
    }
}
