/*
 * VideoRecordingSetup.java
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

package com.rim.samples.device.videorecordingdemo;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

/**
 * Helper class for retrieving the necessary device information for video
 * recording.
 */
public class VideoRecordingSetup {
    /**
     * Retrieves a list of all the video encodings available on the current
     * device
     * 
     * @return Newly created array of Strings whose elements are the video
     *         encodings supported by this device. Returns <code>null</code> if
     *         this device does not support video encoding.
     */
    public static String[] getVideoEncodings() {
        // Retrieve the supported video encodings available on this device
        final String encodingsString = System.getProperty("video.encodings");

        // Return null if this device does not support video encoding
        if (encodingsString == null) {
            return null;
        }

        // Split the whitespace delimited encodingsString into a
        // String array of encodings.
        final Vector encodings = new Vector();
        int start = 0;
        int space = encodingsString.indexOf(' ');
        while (space != -1) {
            encodings.addElement(encodingsString.substring(start, space));
            start = space + 1;
            space = encodingsString.indexOf(' ', start);
        }
        encodings.addElement(encodingsString.substring(start, encodingsString
                .length()));

        // Copy the encodings into a String array
        final String[] encodingArray = new String[encodings.size()];
        encodings.copyInto(encodingArray);
        return encodingArray;
    }

    /**
     * Retrieves a list of all the file systems larger than
     * {@link VideoRecordingDemo#MIN_FILE_SYSTEM_SIZE} available on this device.
     * These file systems will be suitable for recording videos.
     * 
     * @return Newly created array of Strings whose elements are the file
     *         systems larger than
     *         {@link VideoRecordingDemo#MIN_FILE_SYSTEM_SIZE}
     */
    public static String[] getFileSystems(final int minSize) {
        final Vector fileSystems = new Vector();
        final Enumeration fileSystemList = FileSystemRegistry.listRoots();
        while (fileSystemList.hasMoreElements()) {
            final String fileSystemName = (String) fileSystemList.nextElement();

            // Cannot write data to "system/" partition
            if (!fileSystemName.equals("system/")) {
                try {
                    final FileConnection fconn =
                            (FileConnection) Connector.open("file:///"
                                    + fileSystemName);
                    if (fconn.availableSize() >= minSize) {
                        fileSystems.addElement(fileSystemName);
                    }
                } catch (final IOException e) {
                    // If an exception occurs, just ignore the file system
                }
            }
        }
        final String[] fileSystemsArray = new String[fileSystems.size()];
        fileSystems.copyInto(fileSystemsArray);
        return fileSystemsArray;
    }
}
