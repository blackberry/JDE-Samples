/*
 * CommandFrameworkDemo.java
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

package com.rim.samples.device.ui.commandframeworkdemo;

import java.util.Hashtable;

import net.rim.device.api.command.AlwaysExecutableCommand;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.CommandMetadata;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.command.registrar.CategoryCollection;
import net.rim.device.api.command.registrar.CommandRegistrarConnection;
import net.rim.device.api.command.registrar.CommandRequest;
import net.rim.device.api.command.registrar.LocalCommandRegistrarConnection;
import net.rim.device.api.command.registrar.RemoteCommandRegistrarConnection;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * A sample application to demonstrate the BlackBerry Smartphone command
 * framework. The application registers various commands with the framework and
 * provides UI elements for which the commands will be added to the context menu
 * or main menu of the application's MainScreen. A command is also registered on
 * startup by the CommandFrameworkDemoRemoteApp application and is retrieved and
 * executed in the constructor of the CommandFrameworkDemo class.
 */
public class CommandFrameworkDemo extends UiApplication {
    static final String EMAIL_ADDR = "emailaddr";

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final CommandFrameworkDemo app = new CommandFrameworkDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new CommandFrameworkDemo object
     */
    public CommandFrameworkDemo() {
        // Register a ViewContactCommand instance
        registerCommand(new ViewContactCommand(),
                "com.example.contacts.view.emailaddr", new String[] {
                        "app.contacts", "action.view" },
                new String[] { EMAIL_ADDR }, null);

        // Register an AddContactCommand instance
        registerCommand(new AddContactCommand(),
                "com.example.contacts.add.emailaddr", new String[] {
                        "app.contacts", "action.add" },
                new String[] { EMAIL_ADDR }, null);

        // Register a command which will be added to the screen's main menu
        registerCommand(new PopupDialogCommand(), "PopupDialogCommand", null,
                null, null);

        // Execute a command retrieved from RemoteCommandRegistrarConnection
        final CommandRegistrarConnection connection =
                new RemoteCommandRegistrarConnection();
        final CommandRequest request =
                new CommandRequest("CommandFrameworkDemoRemoteAppCommand");
        final Command command = connection.getCommand(request);

        // The Command reference may be null if the
        // CommandFrameworkDemoRemoteApp
        // project has not been loaded.
        if (command != null) {
            command.execute(this);
        }

        // Push a MainScreen onto the display stack
        pushScreen(new CommandFrameworkDemoScreen());
    }

    /**
     * Registers a command with LocalCommandRegistrarConnection
     * 
     * @param handler
     *            A CommandHandler implementation
     * @param id
     *            ID used to construct a CommandMetadata object
     * @param commandCategories
     *            String array containing command categories
     * @param contextCategories
     *            String array containing context categories
     * @param classname
     *            Classname of CommandHandler implementation to register, should
     *            be null if <code>handler</code> parameter is not null
     */
    private static void registerCommand(final CommandHandler handler,
            final String id, final String[] commandCategories,
            final String[] contextCategories, final String classname) {
        // Initialize CommandMetadata
        final CommandMetadata metadata = new CommandMetadata(id);
        metadata.setCommandCategories(new CategoryCollection(commandCategories));
        metadata.setContextCategories(new CategoryCollection(contextCategories));
        if (classname != null) {
            metadata.setClassname(classname);
        }

        // Register the CommandHandler and CommandMetadata with
        // LocalCommandRegistrarConnection
        final LocalCommandRegistrarConnection connection =
                new LocalCommandRegistrarConnection();
        connection.registerCommand(handler, metadata);
    }

    /**
     * Helper to stub in Contacts application functionality
     * 
     * @param emailaddr
     *            Email address for which to query contact
     * @return True if a contact exists with given email address, otherwise
     *         false
     */
    public static boolean queryContactExists(final String emailaddr) {
        // Entry exists in Contacts application with email address
        // jgraham@rim.com
        return "jgraham@rim.com".equals(emailaddr);
    }

    /**
     * Retrieves a value from provided context object for a given key
     * 
     * @param contextObject
     *            Context object from which to retrieve a value
     * @param key
     *            Key for which to retrieve a value
     * @return The value associated with given key
     */
    public static String getFromContextObject(final Object contextObject,
            final String key) {
        String str = null;

        if (contextObject instanceof Hashtable) {
            final Object object = ((Hashtable) contextObject).get(key);
            if (object != null) {
                str = object.toString();
            }
        }

        return str;
    }

    /**
     * A CommandHandler implementation for viewing an existing contact in the
     * Contacts application.
     */
    private static class ViewContactCommand extends CommandHandler {
        /**
         * @see CommandHandler#canExecute(ReadOnlyCommandMetadata, Object)
         */
        public boolean canExecute(final ReadOnlyCommandMetadata metadata,
                final Object context) {
            // Return true if the email address contained in the context object
            // exists in the Contacts application
            return queryContactExists(getFromContextObject(context, EMAIL_ADDR));
        }

        /**
         * @see CommandHandler#canExecute(ReadOnlyCommandMetadata, Object)
         */
        public void execute(final ReadOnlyCommandMetadata metadata,
                final Object context) {
            // Excecute an action on the contact
            final String msg =
                    "Viewing " + getFromContextObject(context, EMAIL_ADDR)
                            + " in Contacts application";
            Dialog.alert(msg);
        }
    }

    /**
     * A CommandHandler implementation which will be executed unconditionally
     */
    private static class PopupDialogCommand extends AlwaysExecutableCommand {
        public void execute(final ReadOnlyCommandMetadata metadata,
                final Object context) {
            // This command merely demonstrates the adding of a command to the
            // screen's menu directly.
            final Dialog dialog =
                    new Dialog(Dialog.D_OK, "Popped up dialog", Dialog.D_OK,
                            null, 100);
            dialog.doModal();
        }
    }

    /**
     * A CommandHandler implementation for adding an email address as a new
     * contact in the Contacts application.
     */
    private static class AddContactCommand extends CommandHandler {
        /**
         * @see CommandHandler#canExecute(ReadOnlyCommandMetadata, Object)
         */
        public boolean canExecute(final ReadOnlyCommandMetadata metadata,
                final Object context) {
            // Return false if a contact already exists with the email address
            // in question.
            return !queryContactExists(getFromContextObject(context, EMAIL_ADDR));
        }

        /**
         * @see CommandHandler#execute(ReadOnlyCommandMetadata, Object)
         */
        public void execute(final ReadOnlyCommandMetadata metadata,
                final Object context) {
            final String emailAddress =
                    getFromContextObject(context, EMAIL_ADDR);
            final String msg =
                    "Adding " + emailAddress + " to Contacts application";
            Dialog.alert(msg);
        }
    }
}
