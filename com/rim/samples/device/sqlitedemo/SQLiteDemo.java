/*
 * SQLiteDemo.java
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

package com.rim.samples.device.sqlitedemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.database.DatabaseOptions;
import net.rim.device.api.database.DatabaseSecurityOptions;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * This sample application demonstrates the use of a SQLite database and the
 * 'net.rim.device.api.database package'. A pre-configured database is included
 * with the project and will be copied to the default root location (provided an
 * SDCard is available) if a database does not already exist at that location.
 * The default root for SQLite databases is
 * 'file:///SDCard/databases/*project-name*'. Certain BlackBerry Smartphone
 * devices are capable of creating databases in eMMC memory which is of a fixed
 * capacity. Storage location for SQLite databases should be based on the
 * availability of eMMC memory, potential size of the database, and whether a
 * portable solution is required for a given application.
 * 
 * This sample is a business directory application which uses the SQLite back
 * end database for persistent storage of its data. The database contains a
 * DirectoryItems table and an associated table, Category. The DirectoryItems
 * table contains a foreign key constraint on its category_id field which
 * references the corresponding field in the Category table. The applications's
 * user interface consists of a tree field which displays categories as nodes
 * and directory items as child nodes. The application allows for the
 * displaying, editing, adding, saving and deleting of items. Categories can
 * also be added or deleted. Deleting a category will result in all directory
 * items belonging to the category being deleted as well.
 * 
 * The database created by this application is encrypted and access is
 * controlled by a code signing key. You will need to use the BlackBerry Signing
 * Authority Admin Tool to create a public/private key pair with the name "XYZ"
 * (See the BlackBerry Signing Authority Tool Administrator Guide for more
 * information). Replace the XYZ public key contained in this project with the
 * XYZ public key created with the BlackBerry Signing Authority Admin Tool.
 * Build the project and then use the BlackBerry Signing Authority Tool to sign
 * the resulting cod file with the XYZ private key.
 */
public final class SQLiteDemo extends UiApplication {
    private static final String DB_NAME = "SQLiteDemoDirectory";

    /**
     * Entry point for this application
     * 
     * @param args
     *            Command line arguments (not used)
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final SQLiteDemo app = new SQLiteDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new SQLiteDemo object
     * 
     * @throws Exception
     */
    public SQLiteDemo() throws Exception {
        // Determine if an SDCard is present
        boolean sdCardPresent = false;
        String root = null;
        final Enumeration e = FileSystemRegistry.listRoots();
        while (e.hasMoreElements()) {
            root = (String) e.nextElement();
            if (root.equalsIgnoreCase("sdcard/")) {
                sdCardPresent = true;
            }
        }
        if (!sdCardPresent) {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert("This application requires an SD card to be present. Exiting application...");
                    System.exit(0);
                }
            });
        } else {
            final String dbLocation = "/SDCard/databases/SQLite Demo/";

            // Create URI
            final URI uri = URI.create(dbLocation + DB_NAME);

            // Open or create a plain text database. This will create the
            // directory and file defined by the URI (if they do not already
            // exist).
            Database db = DatabaseFactory.openOrCreate(uri);

            // Close the database in case it is blank and we need to write to
            // the file
            db.close();

            // Open a connection to the database file
            final FileConnection fileConnection =
                    (FileConnection) Connector.open("file://" + dbLocation
                            + DB_NAME);

            // If the file is blank, copy the pre-defined database from this
            // module to the SDCard.
            if (fileConnection.exists() && fileConnection.fileSize() == 0) {
                readAndWriteDatabaseFile(fileConnection);
            }

            // Retrieve the code signing key for the XYZ key file
            final CodeSigningKey codeSigningKey =
                    CodeSigningKey.get(CodeModuleManager
                            .getModuleHandle("SQLiteDemo"), "XYZ");

            try {
                // Encrypt and protect the database. If the database is already
                // encrypted, the method will exit gracefully.
                DatabaseFactory.encrypt(uri, new DatabaseSecurityOptions(
                        codeSigningKey));
            } catch (final DatabaseException dbe) {
                errorDialog("Encryption failed - " + dbe.toString());
            }

            // Create a new DatabaseOptions object forcing foreign key
            // constraints
            final DatabaseOptions databaseOptions = new DatabaseOptions();
            databaseOptions.set("foreign_key_constraints", "on");

            // Open the database
            db = DatabaseFactory.open(uri, databaseOptions);

            // Create a new main screen and push it onto the display stack
            final SQLiteDemoScreen screen =
                    new SQLiteDemoScreen(new SQLManager(db));
            pushScreen(screen);
        }
    }

    /**
     * Copies the pre-defined database from this module to the location
     * specified by the fileConnection argument.
     * 
     * @param fileConnection
     *            File connection to the database location
     */
    public void readAndWriteDatabaseFile(final FileConnection fileConnection)
            throws IOException {
        OutputStream outputStream = null;
        InputStream inputStream = null;

        // Open an input stream to the pre-defined encrypted database bundled
        // within this module.
        inputStream = getClass().getResourceAsStream("/" + DB_NAME);

        // Open an output stream to the newly created file
        outputStream = fileConnection.openOutputStream();

        // Read data from the input stream and write the data to the
        // output stream.
        final byte[] bytes = IOUtilities.streamToBytes(inputStream);
        outputStream.write(bytes);

        // Close the connections
        if (fileConnection != null) {
            fileConnection.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }

    /**
     * Presents a dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     */
    public static void errorDialog(final String message) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(message);
            }
        });
    }
}
