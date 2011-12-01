/*
 * FileHolder.java
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

package com.rim.samples.device.attachmentdemo;

/**
 * Helper class to store information about directories and files that are being
 * read from the system.
 */
public final class FileHolder {
    private final String _filename;
    private final String _path;
    private final boolean _isDir;

    /**
     * Creates a new FileHolder object
     * 
     * @param fileinfo
     *            File location
     */
    public FileHolder(final String fileinfo, final boolean isDir) {
        _isDir = isDir;

        // Pull the information from the URI provided from the original
        // FileConnection
        int slash = fileinfo.lastIndexOf('/');
        _path = fileinfo.substring(0, ++slash);
        _filename = fileinfo.substring(slash);
    }

    /**
     * Retrieves the file name
     * 
     * @return Name of the file, or null if this object represents a directory
     */
    String getFileName() {
        return _filename;
    }

    /**
     * Retrieves the path of the directory or file
     * 
     * @return The fully qualified path of the file
     */
    String getPath() {
        return _path;
    }

    /**
     * Determines if the FileHolder represents a directory
     * 
     * @return true If FileHolder represents a directory, otherwise false
     */
    boolean isDirectory() {
        return _isDir;
    }
}
