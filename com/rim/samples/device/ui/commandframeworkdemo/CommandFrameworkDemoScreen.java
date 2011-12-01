/*
 * CommandFrameworkDemoScreen.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.ui.commandframeworkdemo;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.command.registrar.CommandRequest;
import net.rim.device.api.command.registrar.LocalCommandRegistrarConnection;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.menu.DefaultContextMenuProvider;
import net.rim.device.api.util.StringProvider;

/**
 * MainScreen class for the CommandFrameworkDemo application
 */
public class CommandFrameworkDemoScreen extends MainScreen {
    /**
     * Creates a new CommandFrameworkDemoScreen object
     */
    public CommandFrameworkDemoScreen() {
        setTitle("Command Framework Demo");

        // Set a DefaultContextMenuProvider for this screen
        setContextMenuProvider(new DefaultContextMenuProvider());

        // Add LabelFeld with on screen info
        add(new LabelField(
                "For the purpose of this demo, mli@rim.com is not an existing contact "
                        + "and jgraham@rim.com is assumed to be an existing contact."));

        add(new SeparatorField());

        // Add new label for email address heading
        final LabelField labelField =
                new LabelField("Email addresses (invoke context menu)");
        add(labelField);

        // Add field for first email address
        final LabelField emailField1 =
                new LabelField("mli@rim.com", Field.FOCUSABLE);
        add(emailField1);

        // Add field for second email address
        final LabelField emailField2 =
                new LabelField("jgraham@rim.com", Field.FOCUSABLE);
        add(emailField2);

        // Set a new CommandItemProvider for email address fields
        final ItemProvider itemProvider = new ItemProvider();
        emailField1.setCommandItemProvider(itemProvider);
        emailField2.setCommandItemProvider(itemProvider);

        add(new SeparatorField());

        // Create ButtonField with command context
        final ButtonField buttonField =
                new ButtonField("Test Button", ButtonField.CONSUME_CLICK
                        | Field.FIELD_HCENTER);
        buttonField.setCommandContext(new Object() {
            public String toString() {
                return "My Context Object";
            }
        });

        // Set command to be invoked by the ButtonField
        buttonField.setCommand(new Command(new CommandHandler() {
            /**
             * @see CommandHandler#Execute(ReadOnlyCommandMetadata, Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                Dialog.alert("Executing command for " + context.toString());
            }
        }));

        // Add the ButtonField to the screen
        add(buttonField);
    }

    /**
     * @see MainScreen#makeMenu(Menu, context)
     */
    protected void makeMenu(final Menu menu, final int context) {
        final LocalCommandRegistrarConnection connection =
                new LocalCommandRegistrarConnection();

        // Add a MenuItem for the PopupDialogCommand to the menu
        final MenuItem menuItem =
                new MenuItem(new StringProvider("Popup Dialog"), 0x230010, 0);
        menuItem.setCommand(connection.getCommand(new CommandRequest(
                "PopupDialogCommand")));
        menu.add(menuItem);

        super.makeMenu(menu, context);
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }
}
