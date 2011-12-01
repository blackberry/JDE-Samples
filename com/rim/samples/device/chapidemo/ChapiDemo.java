/**
 * ChapiDemo.java
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

package com.rim.samples.device.chapidemo;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.content.ContentHandler;
import javax.microedition.content.ContentHandlerException;
import javax.microedition.content.ContentHandlerServer;
import javax.microedition.content.Invocation;
import javax.microedition.content.Registry;
import javax.microedition.content.RequestListener;
import javax.microedition.io.StreamConnection;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This sample demonstrates the JSR 211 Content Handler API. Our application is
 * acting as both the invoking application and the handler application. You will
 * need to configure the 'Data' folder of this project as the location of the
 * csv file for which this application will be invoked as a handler. Under
 * Edit/Preferences/Simulator/Memory select the 'Use PC filesystem for SD Card
 * files' checkbox and browse for the 'Data' folder (e.g. C:\Program
 * Files\Research In Motion\BlackBerry JDE
 * 4.5.0\samples\com\rim\samples\device\chapidemo\Data).
 */
public class ChapiDemo extends UiApplication implements RequestListener {
    // The Content Handler ID.
    static final String ID = "com.rim.samples.device.chapidemo";

    // The content handler class name.
    static final String CLASSNAME = ID + ".ChapiDemo";

    // The URL pointing to the location of the file we want to open.
    static final String URL = "file:///SDCard/rim.csv";

    // Entry point for application
    public static void main(final String[] args) {
        if (args != null && args.length > 0 && args[0].equals("startup")) {
            registerApp(); // Register this app as a content handler on startup.
        } else {
            new ChapiDemo().enterEventDispatcher(); // GUI
        }
    }

    /**
     * Registers this application with specified types, suffixes, actions and
     * classname. This method will run on startup.
     */
    static void registerApp() {
        try {
            // This app will be a handler for csv files and will be invoked
            // with ACTION_OPEN.
            final String[] types = { "text/csv" };
            final String[] suffixes = { ".csv" };
            final String[] actions = { ContentHandler.ACTION_OPEN };

            // Get access to the registry and register as a content handler.
            final Registry registry = Registry.getRegistry(CLASSNAME);
            registry.register(CLASSNAME, types, suffixes, actions, null, ID,
                    null);

        } catch (final ContentHandlerException che) {
            System.out.print(che.toString());
        } catch (final ClassNotFoundException cnfe) {
            System.out.print(cnfe.toString());
        }
    }

    // Constructor for GUI app.
    ChapiDemo() {
        try {
            // Get access to the ContentHandlerServer for this application and
            // register as a listener.
            final ContentHandlerServer contentHandlerServer =
                    Registry.getServer(CLASSNAME);

            // Register as a RequestListener.
            contentHandlerServer.setListener(this);

            // Push a new GUI screen.
            final ChapiDemoScreen chapiDemoScreen = new ChapiDemoScreen();
            pushScreen(chapiDemoScreen);
        } catch (final ContentHandlerException che) {
            System.out.print(che.toString());
        }
    }

    /**
     * Creates an Invocation object and passes it to the Registry. Called by
     * 'Invoke' menu item.
     */
    void doInvoke() {
        try {
            // Create the Invocation with our hard-coded URL.
            final Invocation invoc = new Invocation(URL);
            invoc.setResponseRequired(false); // We don't require a response.

            // We want to invoke a handler that has registered with ACTION_OPEN.
            invoc.setAction(ContentHandler.ACTION_OPEN);

            // Get access to the Registry and pass it the Invocation.
            final Registry registry = Registry.getRegistry(CLASSNAME);
            registry.invoke(invoc);
        } catch (final IOException ioe) {
            System.out.print(ioe.toString());
        }
    }

    // Reads text from the file pointed to by the URL contained
    // in the Invocation.
    static String getViaStreamConnection(final Invocation invoc) {
        // Create a StreamConnection and use it to open an
        // InputStream for reading.
        StreamConnection streamConnection = null;
        InputStream inputStream = null;
        String text = "";
        try {
            streamConnection = (StreamConnection) invoc.open(false);
            inputStream = streamConnection.openInputStream();
            int ch;

            // Read from the InputStream and build text string.
            while ((ch = inputStream.read()) != -1) {
                text = text.concat(String.valueOf((char) ch));
            }
        } catch (final IOException ioe) {
            System.out.print(ioe.toString());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException ioe) {
                    System.out.print(ioe.toString());
                }
            }
            if (streamConnection != null) {
                try {
                    streamConnection.close();
                } catch (final IOException ioe) {
                    System.out.print(ioe.toString());
                }
            }
        }
        return text; // Return the text string.
    }

    /**
     * Implementation of RequestListener
     * 
     * @param handler
     *            <description>
     */
    public void invocationRequestNotify(final ContentHandlerServer handler) {
        final Invocation invoc = handler.getRequest(false);
        if (invoc != null) {
            Dialog.alert("Handler has been invoked for: " + invoc.getURL());
            final String content = getViaStreamConnection(invoc);

            if (!"".equals(content)) {
                final DisplayContentScreen displayContentScreen =
                        new DisplayContentScreen();
                displayContentScreen.displayContent(content);
                pushScreen(displayContentScreen);
            }
        }
    }

    // Simple GUI screen from which to create an Invocation.
    final class ChapiDemoScreen extends MainScreen {
        ChapiDemoScreen() {
            // Initialize UI components.
            setTitle(new LabelField("Chapi Demo Screen", Field.FIELD_HCENTER));
            add(new LabelField("Select Invoke from the menu"));
            addMenuItem(new MenuItem("Invoke", 5, 5) {
                public void run() {
                    doInvoke();
                }
            });
        }
    }

    /**
     * This screen will be used to display the content we are handling.
     */
    static class DisplayContentScreen extends MainScreen {
        // A field to display content.
        TextField _contentField = new TextField(Field.NON_FOCUSABLE);

        // Constructor
        DisplayContentScreen() {
            setTitle(new LabelField("Display Content Screen",
                    Field.FIELD_HCENTER));
            add(_contentField);
        }

        // Parses the comma separated values in the content string
        // and displays the text.
        void displayContent(final String content) {
            // Let's count the number of commas in the content string.
            int commaCount = 0;
            for (int i = 0; i < content.length(); i++) {
                if (content.charAt(i) == ',') {
                    commaCount++;
                }
            }

            // Now we extract the text values from the string and build a new
            // string to display in our text field.
            int begin = 0;
            int end = content.indexOf(',');
            String text = content.substring(begin, end);
            for (int i = 0; i < commaCount - 1; i++) {
                begin = end + 1;
                end = content.indexOf(',', begin);
                text = text + "\n" + content.substring(begin, end);
            }
            begin = end + 1;
            text = text + "\n" + content.substring(begin, content.length());

            // Display the content.
            _contentField.setText(text);
        }
    }
}
