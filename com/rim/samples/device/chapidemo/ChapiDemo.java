/**
 * CHAPIDemo.java
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
import java.util.Vector;

import javax.microedition.content.ContentHandler;
import javax.microedition.content.ContentHandlerException;
import javax.microedition.content.Invocation;
import javax.microedition.content.Registry;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.registrar.CommandRegistrarConnection;
import net.rim.device.api.command.registrar.CommandRequest;
import net.rim.device.api.command.registrar.RemoteCommandRegistrarConnection;
import net.rim.device.api.content.ContentHandlerMenu;
import net.rim.device.api.content.DefaultContentHandlerRegistry;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.menu.CommandItem;
import net.rim.device.api.ui.menu.CommandItemProvider;
import net.rim.device.api.ui.menu.DefaultContextMenuProvider;
import net.rim.device.api.util.StringProvider;

/**
 * This sample application demonstrates the JSR 211 Content Handler API. The
 * application is acting as both an invoking application and a handler
 * application.
 * <p>
 * This application demonstrates the following:
 * <ol>
 * <li>Registering a handler: See {@link DemoContentHandler#register}</li>
 * <li>Unregistering a handler: See {@link DemoContentHandler#unregister}</li>
 * <li>Invoking a handler: See {@link DemoContentHandler#doInvoke}</li>
 * <li>Implementing a handler: See {@link DemoContentHandler#register} and
 * {@link DemoContentHandler#invocationRequestNotify}</li>
 * <li>Setting a default handler: See {@link #toggleDefaultHandler()}</li>
 * <li>Using the content handler menu: See {@link CHAPIDemoScreen#makeMenu}</li>
 * <li>Using the content handler graphical context menu: See
 * {@link CHAPIDemoScreen#getItems} and {@link CHAPIDemoScreen#getContext}</li>
 * </ol>
 */
public class CHAPIDemo extends UiApplication {
    private final Registry _registry = Registry.getRegistry(getClass()
            .getName());
    private final DefaultContentHandlerRegistry _defaultRegistry =
            DefaultContentHandlerRegistry
                    .getDefaultContentHandlerRegistry(_registry);

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        try {
            // Register multiple handlers for the same types, suffixes,
            // and actions. These handlers are unregistered when the
            // screen closes.
            DemoContentHandler.register(DemoContentHandler.class.getName(),
                    DemoContentHandler.TYPES, DemoContentHandler.SUFFIXES,
                    DemoContentHandler.ACTIONS, null, DemoContentHandler.ID,
                    null, "Handler 1", new DemoContentHandler());

            DemoContentHandler.register(DemoContentHandler2.class.getName(),
                    DemoContentHandler.TYPES, DemoContentHandler.SUFFIXES,
                    DemoContentHandler.ACTIONS, null, DemoContentHandler2.ID,
                    null, "Handler 2", new DemoContentHandler2());

            // Create a new instance of the application and make the currently
            // running thread the application's event dispatch thread.
            final CHAPIDemo app = new CHAPIDemo();
            app.enterEventDispatcher();
        } catch (final ContentHandlerException che) {
            System.out.println("Registry#register() threw " + che.toString());
        } catch (final ClassNotFoundException cnfe) {
            System.out.println("Registry#register() threw " + cnfe.toString());
        }
    }

    /**
     * Creates a new CHAPIDemo object
     */
    public CHAPIDemo() {
        // Push a new GUI screen
        final CHAPIDemoScreen CHAPIDemoScreen = new CHAPIDemoScreen();
        pushScreen(CHAPIDemoScreen);
    }

    /**
     * Creates a demo invocation. The URL of the invocation actually does not
     * exist. The types, actions, and suffixes are from the
     * <code>DemoContentHandler</code>.
     * 
     * @return A demo invocation whose URL does not point to a real file
     */
    private Invocation getInvocation() {
        // The URL pointing to the location of the file we want to open
        // This file doesn't actually exist.
        final String url = "file:///rim." + DemoContentHandler.SUFFIXES[0];

        final Invocation invoc = new Invocation(url);
        invoc.setType(DemoContentHandler.TYPES[0]);
        invoc.setResponseRequired(false); // We don't require a response

        // We want to invoke a handler that has registered with ACTION_OPEN
        invoc.setAction(DemoContentHandler.ACTIONS[0]);

        return invoc;
    }

    /**
     * Creates an Invocation object and passes it to the Registry. Called by
     * 'Invoke' menu item.
     */
    private void doInvoke() {
        try {
            final Invocation invoc = getInvocation();

            // Get access to the Registry and pass it the Invocation
            _registry.invoke(invoc);
        } catch (final IOException ioe) {
            errorDialog("Registry#invoke() threw " + ioe.toString());
        }
    }

    /**
     * Toggles the default handler between DemoContentHandler and
     * DemoContentHandler2.
     * 
     * @return The new default handler name pulled from the
     *         ApplicationDescriptor
     */
    private String toggleDefaultHandler() {
        final Invocation invocation = getInvocation();
        String newDefaultHandlerName = null;

        try {
            final ContentHandler defaultHandler =
                    _defaultRegistry.getDefaultContentHandler(invocation);

            // Toggle the new handler ID based on the current one
            final String newDefaultHandlerId =
                    defaultHandler != null
                            && defaultHandler.getID().equals(
                                    DemoContentHandler.ID) ? DemoContentHandler2.ID
                            : DemoContentHandler.ID;

            // Set the default handler using the types, suffixes, and actions
            // defined in DemoContentHandler
            _defaultRegistry.setDefaultContentHandler(DemoContentHandler.TYPES,
                    DemoContentHandler.SUFFIXES, DemoContentHandler.ACTIONS,
                    newDefaultHandlerId);

            // Get the handler app name from the descriptor
            final ApplicationDescriptor handlerDescriptor =
                    _defaultRegistry
                            .getApplicationDescriptor(newDefaultHandlerId);
            newDefaultHandlerName = handlerDescriptor.getName();
        } catch (final IOException e) {
            errorDialog("Can't set default handler: " + e.getMessage());
        }

        return newDefaultHandlerName;
    }

    /**
     * Presents a dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     */
    private static void errorDialog(final String message) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(message);
            }
        });
    }

    /**
     * The MainScreen class for the CHAPI Demo sample application
     */
    final class CHAPIDemoScreen extends MainScreen implements
            CommandItemProvider {
        private final ButtonField _invokeButton;
        private final ButtonField _toggleButton;
        private final LabelField _contentHandlerMenuLabel;

        /**
         * Creates a new CHAPIDemoScreen object
         */
        CHAPIDemoScreen() {
            setTitle(new LabelField("CHAPI Demo Screen", Field.FIELD_HCENTER));

            final LabelField toggleDescription =
                    new LabelField(
                            "Press this button to change the default handler:");

            String defaultHandlerName;

            // Set the default handler using the types, suffixes, and actions
            // defined in DemoContentHandler
            _defaultRegistry.setDefaultContentHandler(DemoContentHandler.TYPES,
                    DemoContentHandler.SUFFIXES, DemoContentHandler.ACTIONS,
                    DemoContentHandler.ID);

            // Get the app name for the current default handler
            try {
                final Invocation invocation = getInvocation();
                final ContentHandler defaultHandler =
                        _defaultRegistry.getDefaultContentHandler(invocation);
                final String id = defaultHandler.getID();
                final ApplicationDescriptor handlerDescriptor =
                        _defaultRegistry.getApplicationDescriptor(id);
                defaultHandlerName = handlerDescriptor.getName();
            } catch (final IOException e) {
                defaultHandlerName = toggleDefaultHandler();
            }

            _toggleButton =
                    new ButtonField("Default: " + defaultHandlerName,
                            ButtonField.NEVER_DIRTY | ButtonField.CONSUME_CLICK);

            _toggleButton.setChangeListener(new FieldChangeListener() {
                public void fieldChanged(final Field field, final int context) {
                    final String newDefaultHandlerName = toggleDefaultHandler();
                    if (newDefaultHandlerName != null) {
                        _toggleButton.setLabel("default: "
                                + newDefaultHandlerName);
                    }
                }
            });

            _invokeButton =
                    new ButtonField("Invoke Handler", ButtonField.NEVER_DIRTY
                            | ButtonField.CONSUME_CLICK);

            _invokeButton.setChangeListener(new FieldChangeListener() {
                public void fieldChanged(final Field field, final int context) {
                    doInvoke();
                }
            });

            _contentHandlerMenuLabel =
                    new LabelField("Focus here and open the menu",
                            Field.FOCUSABLE);

            add(toggleDescription);
            add(_toggleButton);
            add(new SeparatorField());
            add(_invokeButton);
            add(new SeparatorField());
            add(_contentHandlerMenuLabel);

            setContextMenuProvider(new DefaultContextMenuProvider());
            _contentHandlerMenuLabel.setCommandItemProvider(this);
        }

        /**
         * @see MainScreen#makeMenu(Menu, int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            super.makeMenu(menu, instance);
            final Field focusField = getLeafFieldWithFocus();

            // Create the "open with" context menu item
            if (focusField == _contentHandlerMenuLabel) {
                final ContentHandlerMenu contentHandlerMenu =
                        new ContentHandlerMenu(getInvocation(), _registry,
                                "Open with", 0, 0);
                menu.add(contentHandlerMenu);
            }
        }

        /**
         * @see CommandItemProvider#getContext(Field)
         */
        public Object getContext(final Field field) {
            Object context = null;

            if (field == _contentHandlerMenuLabel) {
                context = getInvocation();
            }

            return context;
        }

        /**
         * @see CommandItemProvider#getItems(Field)
         */
        public Vector getItems(final Field field) {
            Vector items = null;

            if (field == _contentHandlerMenuLabel) {
                final CommandRegistrarConnection connection =
                        new RemoteCommandRegistrarConnection();
                final CommandRequest request =
                        new CommandRequest("ContentHandlerCommand");
                final Command command = connection.getCommand(request);
                items = new Vector();
                items.addElement(new CommandItem(
                        new StringProvider("Open with"), null, command));
            }

            return items;
        }

        /**
         * @see Screen#close()
         */
        public void close() {
            try {
                // Deregister the handlers
                DemoContentHandler.unregister(DemoContentHandler.class
                        .getName());
                DemoContentHandler.unregister(DemoContentHandler2.class
                        .getName());
            } catch (final ContentHandlerException che) {
                errorDialog("Registry.getServer() threw " + che.toString());
            }

            super.close();
        }
    }
}
