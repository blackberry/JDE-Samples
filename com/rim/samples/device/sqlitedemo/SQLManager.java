/*
 * SQLManager.java
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

import java.util.Vector;

import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.DataTypeException;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.Row;
import net.rim.device.api.database.Statement;
import net.rim.device.api.util.IntHashtable;

/**
 * A class to handle SQLite database logic
 */
public class SQLManager {
    private final Database _db;

    /**
     * Constructs a new SQLManager object
     * 
     * @param db
     *            Database to manage
     */
    public SQLManager(final Database db) {
        _db = db;
    }

    /**
     * Adds a new category to the Category database table
     * 
     * @param name
     *            The name of the Category to be added
     * @return A new Category object
     */
    Category addCategory(final String name) {
        Category category = null;
        try {
            // INSERT a row into the Category table for the new category
            Statement statement =
                    _db.createStatement("INSERT INTO Category VALUES(null, ?)");
            statement.prepare();
            statement.bind(1, name);
            statement.execute();
            statement.close();

            // Query the database for the auto-generated ID of the category just
            // added
            // and create a new Category object.
            statement =
                    _db.createStatement("SELECT category_id FROM Category WHERE category_name = ?");
            statement.prepare();
            statement.bind(1, name);
            final Cursor cursor = statement.getCursor();
            if (cursor.next()) {
                final Row row = cursor.getRow();
                final int id = row.getInteger(0);
                category = new Category(id, name);
            }
            cursor.close();
            statement.close();
        } catch (final DatabaseException dbe) {
            SQLiteDemo.errorDialog(dbe.toString());
        } catch (final DataTypeException dte) {
            SQLiteDemo.errorDialog(dte.toString());
        }

        return category;
    }

    /**
     * Adds an item to the Items table in the database
     * 
     * @param name
     *            The name of the business represented by the directory item to
     *            be added
     * @param location
     *            The street address of the business represented by the
     *            directory item to be added
     * @param phone
     *            The phone number for the business represented by the directory
     *            item to be added
     * @param categoryID
     *            The category to which the directory item to be added belongs
     * @return The id of the new directory item
     */
    int addItem(final String name, final String location, final String phone,
            final int categoryID) {
        long id = -1;

        try {
            // Insert a new record in the DirectoryItems table
            final Statement statement =
                    _db.createStatement("INSERT INTO DirectoryItems VALUES(null, ?, ?, ?, ?)");
            statement.prepare();
            statement.bind(1, categoryID);
            statement.bind(2, name);
            statement.bind(3, location);
            statement.bind(4, phone);
            statement.execute();
            statement.close();

            // Retrieve the auto-generated ID of the item just added
            id = _db.lastInsertedRowID();
        } catch (final DatabaseException dbe) {
            SQLiteDemo.errorDialog(dbe.toString());
        }

        return (int) id;
    }

    /**
     * Updates an existing record in the DirectoryItems table
     * 
     * @param id
     *            The id of the item to update
     * @param name
     *            The text with which to update the name field
     * @param location
     *            The text with which to update the location field
     * @param phone
     *            The text with which to update the phone field
     */
    void updateItem(final int id, final String name, final String location,
            final String phone) {
        try {
            // Update the record in the DirectoryItems table for the given id
            final Statement statement =
                    _db.createStatement("UPDATE DirectoryItems SET item_name = ?, location = ?, phone = ? WHERE id = ?");
            statement.prepare();
            statement.bind(1, name);
            statement.bind(2, location);
            statement.bind(3, phone);
            statement.bind(4, id);
            statement.execute();
            statement.close();
        } catch (final DatabaseException dbe) {
            SQLiteDemo.errorDialog(dbe.toString());
        }
    }

    /**
     * Deletes a category from the Category table and all corresponding records
     * in the DirectoryItems table.
     * 
     * @param id
     *            The id of the category to delete
     */
    void deleteCategory(final int id) {
        try {
            // Delete all items in the DirectoryItems database
            // table belonging to the highlighted category.
            Statement statement =
                    _db.createStatement("DELETE FROM DirectoryItems WHERE category_id = ?");
            statement.prepare();
            statement.bind(1, id);
            statement.execute();
            statement.close();

            // Delete the record in the Category database table
            // corresponding to the highlighted category.
            statement =
                    _db.createStatement("DELETE FROM Category WHERE category_id = ?");
            statement.prepare();
            statement.bind(1, id);
            statement.execute();
            statement.close();
        } catch (final DatabaseException dbe) {
            SQLiteDemo.errorDialog(dbe.toString());
        }
    }

    /**
     * Deletes a directory item record from the database
     * 
     * @param id
     *            The id of the directory item to delete
     */
    void deleteItem(final int id) {
        try {
            // Delete the record in the DirectoryItems table for the given id
            final Statement statement =
                    _db.createStatement("DELETE FROM DirectoryItems WHERE id = ?");
            statement.prepare();
            statement.bind(1, id);
            statement.execute();
            statement.close();
        } catch (final DatabaseException dbe) {
            SQLiteDemo.errorDialog(dbe.toString());
        }
    }

    /**
     * Retrieves all records in the Category database table and returns a hash
     * table of Category objects.
     * 
     * @return A hash table of Category objects, one for each record in the
     *         Category table
     */
    IntHashtable getCategories() {
        final IntHashtable categories = new IntHashtable();
        try {
            // Read in all records from the Category table
            final Statement statement =
                    _db.createStatement("SELECT * FROM Category");
            statement.prepare();
            final Cursor cursor = statement.getCursor();

            Row row;
            int id;
            String name;
            Category category;

            // Iterate through the result set. For each row, create a new
            // Category object and add it to the hash table.
            while (cursor.next()) {
                row = cursor.getRow();
                id = row.getInteger(0);
                name = row.getString(1);
                category = new Category(id, name);
                categories.put(id, category);
            }
            statement.close();
            cursor.close();
        } catch (final DatabaseException dbe) {
            SQLiteDemo.errorDialog(dbe.toString());
        } catch (final DataTypeException dte) {
            SQLiteDemo.errorDialog(dte.toString());
        }

        return categories;
    }

    /**
     * Retrieves all records in the DirectoryItems database table and returns a
     * vector of DirectoryItem objects.
     * 
     * @return A vector of DirectoryItem objects, one for each record in the
     *         DirectoryItem table
     */
    Vector getItems() {
        final Vector directoryItems = new Vector();

        try {
            // Read in all records from the DirectoryItems table
            final Statement statement =
                    _db.createStatement("SELECT * FROM DirectoryItems");
            statement.prepare();
            final Cursor cursor = statement.getCursor();

            // Iterate through the the result set. For each row, add a
            // new DirectoryItem object to the vector.
            while (cursor.next()) {
                final Row row = cursor.getRow();

                final int id = row.getInteger(0);
                final int categoryId = row.getInteger(1);
                final String name = row.getString(2);
                final String location = row.getString(3);
                final String phone = row.getString(4);

                final DirectoryItem item =
                        new DirectoryItem(id, name, location, phone, categoryId);
                directoryItems.addElement(item);
            }
            statement.close();
            cursor.close();
        } catch (final DatabaseException dbe) {
            SQLiteDemo.errorDialog(dbe.toString());
        } catch (final DataTypeException dte) {
            SQLiteDemo.errorDialog(dte.toString());
        }

        return directoryItems;
    }

    /**
     * Closes the database
     */
    void closeDB() {
        try {
            _db.close();
        } catch (final DatabaseException dbe) {
            SQLiteDemo.errorDialog(dbe.toString());
        }
    }
}
