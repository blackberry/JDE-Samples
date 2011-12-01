/*
 * TableAndListDemo.java
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

package com.rim.samples.device.ui.tableandlistdemo;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.io.LineReader;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

/*
 * This application demonstrates the use of the Table and List APIs. There
 * are five screens accessible via the menu. The TableScreen is an example
 * of the use of a table, including layout management and the application
 * of styles. The ListScreen demonstrates the use of lists to display sequential
 * information. The RichListScreen is an example of displaying sequential
 * information with rich formatting and images. The TreeScreen demonstrates
 * the use of a SortedTableModel to nest data beneath headers. 
 * The TableAdapterScreen demonstrates the use of a TableAdapter to display
 * data stored in another structure in a table.
 */
public final class TableAndListDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final UiApplication app = new TableAndListDemo();
        app.enterEventDispatcher();
    }

    /**
     * Retrieves data from resource text file and stores contents in a string
     * tokenizer
     * 
     * @return A string tokenizer containing data from file
     */
    private DemoStringTokenizer getStringTokenizer() {

        final InputStream stream =
                getClass().getResourceAsStream("/resources/blackberry.txt");
        final LineReader lineReader = new LineReader(stream);
        final StringBuffer buffer = new StringBuffer();

        while (true) {
            try {
                buffer.append(new String(lineReader.readLine()));
                buffer.append("\n");
            } catch (final EOFException eof) {
                // We've reached the end of the file
                break;
            } catch (final IOException ioe) {
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        Dialog.alert("LineReader#readLine() threw "
                                + ioe.toString());
                    }
                });
            }
        }

        String data = buffer.toString();

        data = data.replace('\r', ',');
        data = data.replace('\n', ',');

        return new DemoStringTokenizer(data);
    }

    /**
     * Creates a new TableAndListDemo object
     */
    public TableAndListDemo() {

        // Push the main screen
        pushScreen(new TableAndListDemoScreen());
    }

    /**
     * Main screen for the application. Menu contains items to launch each of
     * the included demo screens.
     */
    private class TableAndListDemoScreen extends MainScreen {

        /**
         * Creates a new TableAndListDemoScreen object
         */
        public TableAndListDemoScreen() {
            add(new LabelField("Please select an item from the menu"));

            addMenuItem(new ListScreenMenuItem());
            addMenuItem(new RichListScreenMenuItem());
            addMenuItem(new TableScreenMenuItem());
            addMenuItem(new TableAdapterScreenMenuItem());
            addMenuItem(new TreeScreenMenuItem());
        }

        /**
         * A menu item to display a TableScreen
         */
        private class TableScreenMenuItem extends MenuItem {
            /**
             * Creates a new TableScreenMenuItem object
             */
            public TableScreenMenuItem() {
                super(new StringProvider("Table Screen"), 0x230010, 0);
                this.setCommand(new Command(new CommandHandler() {
                    /**
                     * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                     *      Object)
                     */
                    public void execute(final ReadOnlyCommandMetadata metadata,
                            final Object context) {
                        pushScreen(new TableScreen(getStringTokenizer()));
                    }
                }));
            }
        }

        /**
         * A menu item to display a ListScreen
         */
        private class ListScreenMenuItem extends MenuItem {
            /**
             * Creates a new ListScreenMenuItem object
             */
            public ListScreenMenuItem() {
                super(new StringProvider("List Screen"), 0x230020, 1);
                this.setCommand(new Command(new CommandHandler() {
                    /**
                     * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                     *      Object)
                     */
                    public void execute(final ReadOnlyCommandMetadata metadata,
                            final Object context) {
                        pushScreen(new SimpleListScreen(getStringTokenizer()));
                    }
                }));
            }
        }

        /**
         * A menu item to display a RichListScreen
         */
        private class RichListScreenMenuItem extends MenuItem {
            /**
             * Creates a new RichListScreenMenuItem object
             */
            public RichListScreenMenuItem() {
                super(new StringProvider("Rich List Screen"), 0x230030, 2);
                this.setCommand(new Command(new CommandHandler() {
                    /**
                     * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                     *      Object)
                     */
                    public void execute(final ReadOnlyCommandMetadata metadata,
                            final Object context) {
                        pushScreen(new RichListScreen(getStringTokenizer()));
                    }
                }));
            }
        }

        /**
         * A menu item to display a TreeScreen
         */
        private class TreeScreenMenuItem extends MenuItem {
            /**
             * Creates a TreeScreenMenuItem object
             */
            public TreeScreenMenuItem() {
                super(new StringProvider("Tree Screen"), 0x230040, 3);
                this.setCommand(new Command(new CommandHandler() {
                    /**
                     * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                     *      Object)
                     */
                    public void execute(final ReadOnlyCommandMetadata metadata,
                            final Object context) {
                        pushScreen(new TreeScreen(getStringTokenizer()));
                    }
                }));
            }
        }

        /**
         * A menu item to display a TableAdapterScreen
         */
        private class TableAdapterScreenMenuItem extends MenuItem {
            /**
             * Create a TableAdapterScreenMenuItem object
             */
            public TableAdapterScreenMenuItem() {
                super(new StringProvider("Table Adapter Screen"), 0x230050, 4);
                this.setCommand(new Command(new CommandHandler() {
                    /**
                     * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                     *      Object)
                     */
                    public void execute(final ReadOnlyCommandMetadata metadata,
                            final Object context) {
                        pushScreen(new TableAdapterScreen(getStringTokenizer()));
                    }
                }));
            }
        }
    }
}
