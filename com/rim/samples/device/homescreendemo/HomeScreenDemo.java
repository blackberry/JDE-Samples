/**
 * HomeScreenDemo.java
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

package com.rim.samples.device.homescreendemo;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import net.rim.blackberry.api.homescreen.HomeScreen;
import net.rim.blackberry.api.invoke.CameraArguments;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventInjector;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.table.SimpleList;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringProvider;

/**
 * Using the File Connection API, this application searches the root file
 * systems on the BlackBerry Smartphone device for "pictures" folder(s) and then
 * displays a list of the directories found. Clicking on a list item will
 * display a list of the pictures within that directory. Invoking the
 * "Set as Home Screen Image" menu item for a picture will set the selected
 * picture as the background image for the home screen.
 * 
 * This demo also allows the user to invoke the camera application to take a
 * picture, which can then be set as the background image for the home screen.
 * 
 * The application also allows for the creation of a shortcut on the home screen
 * leading directly to the picture selection screen
 * (HomeScreenDemoSecondaryScreen). This is done by specifying an alternate
 * entry point, defined in the HomeScreenAlternateEntryPoint project.
 */
class HomeScreenDemo extends UiApplication {
    // com.rim.samples.device.homescreendemo.HomeScreenDemo =
    // 0x23d84fce8b031333L
    static final long HOMESCREEN_DEMO_ID = 0x23d84fce8b031333L;

    // Contains the URLs of the "pictures" folders
    private Vector _pictureDirectoryURLs;

    // Contains the names of the pictures within a specific "pictures" folders
    private Vector _pictureFileNames;

    private static PersistentObject _store;

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        _store = PersistentStore.getPersistentObject(HOMESCREEN_DEMO_ID);

        synchronized (_store) {
            // If the PersistentObject is empty, initialize it
            if (_store.getContents() == null) {
                _store.setContents("");
                PersistentObject.commit(_store);
            }
        }

        boolean shortcut = false;

        // Check if the application was launched from the shortcut
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("shortcut")) {
                shortcut = true;
            }
        }

        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new HomeScreenDemo(shortcut).enterEventDispatcher();
    }

    /**
     * Create a new HomeScreenDemo object
     * 
     * @param shortcut
     *            True if the application was launched from the shortcut, false
     *            otherwise
     */
    private HomeScreenDemo(final boolean shortcut) {
        if (shortcut) {
            // If the application was launched from the shortcut, go directly to
            // list screen
            displayDirectoryPictures((String) _store.getContents());
        } else {
            final HSDemoScreen screen = new HSDemoScreen();
            pushScreen(screen);

            addFileSystemJournalListener(screen); // To detect when a camera
                                                  // image is added to a file
                                                  // system
        }
    }

    /**
     * When called by initPictureDirectories(), this method will look for
     * "pictures" folder(s) in <code>root</code> and save URL(s) in
     * _pictureDirectoryURLs vector. When called by displayDirectoryPicstures(),
     * the method will update the _pictureFileNames vector with the names of the
     * pictures within a "pictures" folder (passed as the String
     * <code>root</code>).
     * 
     * @param root
     *            Directory to be searched for the pictures
     * @param pictureSearch
     *            If false, search for "pictures" folder, if true then search
     *            for pictures within folder.
     */
    private void
            readDirectory(final String root, final boolean isPictureFolder) {
        FileConnection fc = null;

        try {
            fc =
                    (FileConnection) Connector.open("file:///" + root,
                            Connector.READ);

            if (fc.isDirectory()) {
                final Enumeration e = fc.list();
                while (e.hasMoreElements()) {
                    final String inner = (String) e.nextElement();

                    if (isPictureFolder) {
                        _pictureFileNames.addElement(inner);
                    } else if (inner.indexOf("pictures") != -1) {
                        final String URL = root + inner;
                        _pictureDirectoryURLs.addElement(URL);
                        return;
                    } else {
                        // Couldn't find "pictures" folder in root directory,
                        // go one level deeper.
                        final String URL = root + inner;
                        readDirectory(URL, false);
                    }
                }
            }
        } catch (final IOException ioe) {
            invokeLater(new Runnable() {
                /**
                 * @see Runnable#run()
                 */
                public void run() {
                    Dialog.alert("Connector.open() threw " + ioe.toString());
                }
            });
        }
    }

    /**
     * Launch a screen where the user can select from a list of pictures in a
     * given directory.
     * 
     * @param picDirectoryURL
     *            The directory to display
     */
    private void displayDirectoryPictures(final String pictureDirectoryURL) {
        // (Re)populate the vector of images in the directory
        if (_pictureFileNames == null) {
            _pictureFileNames = new Vector();
        } else {
            _pictureFileNames.removeAllElements();
        }

        // Search the directory for pictures. This updates the _pictureFileURLs
        // vector.
        readDirectory(pictureDirectoryURL, true);

        if (_pictureFileNames.size() > 0) {
            // Launch the picture selection screen
            pushScreen(new HomeScreenDemoSecondaryScreen(pictureDirectoryURL,
                    _pictureFileNames));
        } else {
            Dialog.alert("No pictures found");
        }
    }

    /**
     * Main screen for the application
     */
    class HSDemoScreen extends MainScreen implements FileSystemJournalListener {
        private final SimpleList _list;
        private ButtonField _invokeButtonField;
        private long _lastUSN;
        private boolean _displayDialog;

        /**
         * Create a new HSDemoScreen object
         */
        public HSDemoScreen() {
            super(Manager.NO_VERTICAL_SCROLL);

            setTitle("Home Screen Demo");

            _pictureDirectoryURLs = new Vector();
            _pictureFileNames = new Vector();

            // Populate _picDirectoryURLs with a list of directories
            // that store pictures.
            initPictureDirectories();

            add(new RichTextField(
                    "Click on a pictures directory to display a list of the pictures it contains.\n",
                    Field.NON_FOCUSABLE));

            final DemoVerticalFieldManager vfm =
                    new DemoVerticalFieldManager(Manager.NO_VERTICAL_SCROLL);

            add(vfm);

            _list = new SimpleList(vfm);

            for (int i = 0; i < _pictureDirectoryURLs.size(); i++) {
                _list.add((String) _pictureDirectoryURLs.elementAt(i));
            }

            // Set the list to display when a list item is activated
            _list.setCommand(new Command(new CommandHandler() {
                /**
                 * @see CommandHandler#execute(ReadOnlyCommandMetadata, Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    displayDirectoryPictures(_list.get(_list.getFocusRow()));
                }
            }));

            if (DeviceInfo.hasCamera()) {
                add(new RichTextField("\n...or invoke the camera application",
                        Field.NON_FOCUSABLE));
                _invokeButtonField =
                        new ButtonField("Invoke Camera App",
                                ButtonField.CONSUME_CLICK | Field.FIELD_HCENTER) {
                            /**
                             * @see net.rim.device.api.ui.component.ButtonField#fieldChangeNotify(int)
                             */
                            protected void fieldChangeNotify(final int context) {
                                Invoke.invokeApplication(
                                        Invoke.APP_TYPE_CAMERA,
                                        new CameraArguments());
                                super.fieldChangeNotify(context);
                            }
                        };

                add(_invokeButtonField);
            }
        }

        /**
         * Looks for all folders containing the substring 'pictures'
         */
        private void initPictureDirectories() {
            final Enumeration rootEnum = FileSystemRegistry.listRoots();

            while (rootEnum.hasMoreElements()) {
                final String root = (String) rootEnum.nextElement();
                readDirectory(root, false);
            }
        }

        /**
         * Invoked when a picture is taken by the Camera app
         * 
         * @see net.rim.device.api.io.file.FileSystemJournalListener#fileJournalChanged()
         */
        public void fileJournalChanged() {
            final long nextUSN = FileSystemJournal.getNextUSN();
            String cameraImagePath = null;

            for (long lookUSN = nextUSN - 1; lookUSN >= _lastUSN
                    && cameraImagePath == null; lookUSN--) {
                final FileSystemJournalEntry entry =
                        FileSystemJournal.getEntry(lookUSN);

                if (entry == null) {
                    // We didn't find an entry
                    break;
                }

                if (entry.getEvent() == FileSystemJournalEntry.FILE_ADDED) {
                    cameraImagePath = entry.getPath();
                    if (cameraImagePath != null
                            && (cameraImagePath.endsWith("png")
                                    || cameraImagePath.endsWith("jpg")
                                    || cameraImagePath.endsWith("bmp") || cameraImagePath
                                    .endsWith("gif"))) {
                        _displayDialog = true;
                        break;
                    }
                }
            }

            // _lastUSN must be updated before pushing a modal screen onto the
            // display stack
            _lastUSN = nextUSN;

            if (_displayDialog) {
                // Close the Camera application to return to Home Screen Demo
                final EventInjector.KeyEvent event =
                        new EventInjector.KeyEvent(
                                EventInjector.KeyEvent.KEY_DOWN,
                                Characters.ESCAPE, 0);
                event.post();
                event.post();

                _displayDialog = false;
                final int choice =
                        Dialog.ask(Dialog.D_YES_NO, "Set as Background?",
                                Dialog.YES);
                if (choice == Dialog.YES) {
                    HomeScreen.setBackgroundImage(cameraImagePath);
                }
            }
        }

        /**
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            if (HomeScreen.supportsIcons()) {
                final MenuItem setIconItem =
                        new MenuItem(
                                new StringProvider("Set Home Screen Icon"),
                                0x230010, 0);
                setIconItem.setCommand(new Command(new CommandHandler() {
                    /**
                     * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                     *      Object)
                     */
                    public void execute(final ReadOnlyCommandMetadata metadata,
                            final Object context) {
                        final Bitmap bitmap =
                                Bitmap.getBitmapResource("img/logo_blue.jpg");
                        HomeScreen.updateIcon(bitmap);
                    }
                }));
                menu.add(setIconItem);

                final MenuItem setRolloverItem =
                        new MenuItem(new StringProvider("Set Rollover Icon"),
                                0x230020, 1);
                setRolloverItem.setCommand(new Command(new CommandHandler() {
                    /**
                     * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                     *      Object)
                     */
                    public void execute(final ReadOnlyCommandMetadata metadata,
                            final Object context) {
                        final Bitmap bitmap =
                                Bitmap.getBitmapResource("img/logo_black.jpg");
                        HomeScreen.setRolloverIcon(bitmap);
                    }
                }));
                menu.add(setRolloverItem);
            }

            super.makeMenu(menu, instance);
        }

        /**
         * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
         */
        public boolean onSavePrompt() {
            // Supress the save dialog
            return true;
        }

        /**
         * A VerticalFieldManager which performs an action on an Enter key press
         */
        private final class DemoVerticalFieldManager extends
                VerticalFieldManager {
            /**
             * Creates a new DemoVerticalFieldManager object
             * 
             * @param style
             *            Style bit for this manager
             */
            DemoVerticalFieldManager(final long style) {
                super(style);
            }

            /**
             * @see net.rim.device.api.ui.Manager#keyChar(char, int, int)
             */
            protected boolean keyChar(final char c, final int status,
                    final int time) {
                if (c == Characters.ENTER) {
                    displayDirectoryPictures(_list.get(_list.getFocusRow()));
                    return true;
                }

                return super.keyChar(c, status, time);
            }
        }
    }
}
