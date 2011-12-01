/*
 * FileExplorerDemoFileHolder.java
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

package com.rim.samples.device.fileexplorerdemo;

/**
 * Helper class to store information about directories and files that are being
 * read from the system.
 */
/* package */final class FileExplorerDemoFileHolder {
    private final String _filename;
    private final String _path;
    private boolean _isDir;

    /**
     * Constructor. Pulls the path and file name from the provided string.
     * 
     * @param fileinfo
     *            The path and file name provided from the FileConnection.
     */
    FileExplorerDemoFileHolder(final String fileinfo) {
        // Pull the information from the URI provided from the original
        // FileConnection.
        int slash = fileinfo.lastIndexOf('/');

        if (slash == -1) {
            throw new IllegalArgumentException("fileinfo must have a slash");
        }

        _path = fileinfo.substring(0, ++slash);
        _filename = fileinfo.substring(slash);
    }

    /**
     * Retrieves the file name.
     * 
     * @return Name of the file, or null if it's a directory.
     */
    String getFileName() {
        return _filename;
    }

    /**
     * Retrieves the path of the directory or file.
     * 
     * @return Fully qualified path.
     */
    String getPath() {
        return _path;
    }

    /**
     * Determins if the FileHolder is a directory.
     * 
     * @return true if FileHolder is directory, otherwise false.
     */
    boolean isDirectory() {
        return _isDir;
    }

    /**
     * Enables setting of directory for FileHolder.
     * 
     * @param isDir
     *            true if FileHolder should be a directory.
     */
    void setDirectory(final boolean isDir) {
        _isDir = isDir;
    }
}
