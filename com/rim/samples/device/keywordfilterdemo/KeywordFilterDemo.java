/**
 * KeywordFilterDemo.java
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

package com.rim.samples.device.keywordfilterdemo;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import net.rim.device.api.io.LineReader;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.KeywordFilterField;

/**
 * An example of how to use the KeywordFilterField API. The KeywordFilterField
 * consists of a single text input field(top) and a list of selectable elements
 * (bottom). Keywords entered into the text field filter elements found in the
 * list. The keyword input field can be accessed via getKeywordField() and/or
 * overridden with a custom TextField via setKeywordField(). See the javadocs in
 * Help/API Reference for more information on how to use this API. In this
 * sample app we add country data from a text file to a SortedReadableList and
 * use this object to construct a KeywordFilterField containing names of
 * specific countries. We specify a customized TextField object of our own
 * design as the input field to be used. When a country in the list is selected,
 * additional information for the selected country is displayed. A menu item
 * allows for additional elements to be added to the list. This application does
 * not provide functionality for adding or displaying secondary information for
 * newly added list elements.
 */

final class KeywordFilterDemo extends UiApplication {
    private KeywordFilterField _keywordFilterField;
    private CountryList _countryList;
    private final Vector _countries;

    /**
     * Entry point for application.
     * 
     * @param args
     *            Command line arguments.
     */
    public static void main(final String[] args) {
        // Create a new instance of the application
        // and start the application on the event thread.
        final KeywordFilterDemo app = new KeywordFilterDemo();
        app.enterEventDispatcher();
    }

    // Constructor
    private KeywordFilterDemo() {
        // Populate vector with data from file.
        _countries = getDataFromFile();

        if (_countries != null) {
            // Create an instance of our SortedReadableList class.
            _countryList = new CountryList(_countries);

            // Add our list to a KeywordFilterField object.
            _keywordFilterField = new KeywordFilterField();
            _keywordFilterField.setSourceList(_countryList, _countryList);

            // We're providing a customized edit field for
            // the KeywordFilterField.
            final CustomKeywordField customSearchField =
                    new CustomKeywordField();
            _keywordFilterField.setKeywordField(customSearchField);

            // Create main screen.
            final KeywordFilterDemoScreen screen =
                    new KeywordFilterDemoScreen(this);

            // We need to explicitly add the search/title field via
            // MainScreen.setTitle().
            screen.setTitle(_keywordFilterField.getKeywordField());

            // Add our KeywordFilterField to the screen and push the screen
            // onto the stack.
            screen.add(_keywordFilterField);
            pushScreen(screen);
        } else {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert("Error reading data file.");
                    System.exit(0);
                }
            });
        }
    }

    /**
     * Method to access the KeywordFilterField object.
     * 
     * @return This applications KeywordFilterField.
     */
    KeywordFilterField getKeywordFilterField() {
        return _keywordFilterField;
    }

    /**
     * Method populates and returns a vector of Country objects containing data
     * read from text file.
     * 
     * @return A vector containing Country objects.
     */
    private Vector getDataFromFile() {
        final Vector countries = new Vector();

        // Get an input stream from the file.
        final InputStream stream =
                getClass().getResourceAsStream("/Data/CountryData.txt");

        if (stream != null) {
            final LineReader lineReader = new LineReader(stream);

            // We read data from input stream one line at a time until we
            // reach end of file. Each line is parsed to extract data used
            // to construct Country objects.
            for (;;) {
                try {
                    final String line = new String(lineReader.readLine());

                    // Parse the current line.
                    final int comma1 = line.indexOf(',');
                    final String country = line.substring(0, comma1);
                    final int comma2 = line.indexOf(',', comma1 + 1);
                    final String population =
                            line.substring(comma1 + 1, comma2);
                    final String capital =
                            line.substring(comma2 + 1, line.length());

                    // Create a new Country object with data from current line.
                    countries.addElement(new Country(country, population,
                            capital));
                } catch (final EOFException eof) {
                    // We've reached the end of the file.
                    break;
                } catch (final IOException ioe) {
                    System.out.println("Error reading data from file");
                    return null;
                }
            }
            return countries;
        } else {
            System.out.println("Could not find resource");
            return null;
        }
    }

    /**
     * Adds a new element and updates the country list.
     * 
     * @param str
     *            The string to be added to the element list.
     */
    void addElementToList(final Country country) {
        _countryList.addElement(country);
        _keywordFilterField.updateList();
    }

    /**
     * Inner Class: A custom keyword input field for the KeywordFilterField. We
     * want to prevent a save dialog from being presented to the user when
     * exiting the application as the ability to persist data is not relevent to
     * this application. We are also using the paint() method to customize the
     * appearance of the cursor in the input field.
     */
    final static class CustomKeywordField extends BasicEditField {
        // Contructor
        CustomKeywordField() {
            // Custom style.
            super(USE_ALL_WIDTH | NON_FOCUSABLE | NO_LEARNING | NO_NEWLINE);

            setLabel("Search: ");
        }

        /**
         * Intercepts ESCAPE key.
         * 
         * @see net.rim.device.api.ui.component.TextField#keyChar(char,int,int)
         */
        protected boolean keyChar(final char ch, final int status,
                final int time) {
            switch (ch) {
            case Characters.ESCAPE:
                // Clear keyword.
                if (super.getTextLength() > 0) {
                    setText("");
                    return true;
                }
            }
            return super.keyChar(ch, status, time);
        }

        /**
         * Overriding super to add custom painting to our class.
         * 
         * @see net.rim.device.api.ui.Field#paint(Graphics)
         */
        protected void paint(final Graphics graphics) {
            super.paint(graphics);

            // Draw caret.
            getFocusRect(new XYRect());
            drawFocus(graphics, true);
        }
    }
}
