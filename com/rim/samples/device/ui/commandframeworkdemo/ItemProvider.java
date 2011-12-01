/*
 * ItemProvider.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.ui.commandframeworkdemo;

import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.registrar.CategoryCollection;
import net.rim.device.api.command.registrar.CommandRequest;
import net.rim.device.api.command.registrar.LocalCommandRegistrarConnection;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.menu.CommandItem;
import net.rim.device.api.ui.menu.CommandItemProvider;
import net.rim.device.api.util.StringProvider;

/**
 * ItemProvider implementation for the Command Framework Demo
 */
public class ItemProvider implements CommandItemProvider {
    /**
     * @see CommandItemProvider#getContext(Field)
     */
    public Object getContext(final Field field) {
        Hashtable context = null;

        if (field instanceof LabelField) {
            context = new Hashtable();
            context.put(CommandFrameworkDemo.EMAIL_ADDR, ((LabelField) field)
                    .getText());
        }

        return context;
    }

    /**
     * @see CommandItemProvider#getItems(Field)
     */
    public Vector getItems(final Field field) {
        final Vector items = new Vector();

        final Object context = getContext(field);

        // Try to find a command for this field associated with a contact
        Command cmd =
                queryCommand(new String[] { "app.contacts", "action.view" },
                        new String[] { CommandFrameworkDemo.EMAIL_ADDR },
                        context);

        if (cmd != null) {
            items.addElement(new CommandItem(
                    new StringProvider("View Contact"), null, cmd));
        } else {
            // Try the add contact command instead
            cmd =
                    queryCommand(new String[] { "app.contacts", "action.add" },
                            new String[] { CommandFrameworkDemo.EMAIL_ADDR },
                            context);
            if (cmd != null) {
                items.addElement(new CommandItem(new StringProvider(
                        "Add Contact"), null, cmd));
            }
        }

        return items;
    }

    /**
     * Searches for a Command based on given criteria
     * 
     * @param commandCategories
     *            String containing command criteria
     * @param contextCategories
     *            String containing context criteria
     * @param context
     *            Context for which to look for a Command
     * @return A Command object if one is found for the given criteria,
     *         otherwise null
     */
    private Command queryCommand(final String[] commandCategories,
            final String[] contextCategories, final Object context) {
        final CommandRequest request = new CommandRequest();

        CategoryCollection categoryCollection =
                new CategoryCollection(commandCategories);
        if (categoryCollection != null) {
            request.setCommandCategories(categoryCollection);
        }

        categoryCollection = new CategoryCollection(contextCategories);
        if (categoryCollection != null) {
            request.setContextCategories(categoryCollection);
        }

        final LocalCommandRegistrarConnection connection =
                new LocalCommandRegistrarConnection();
        final Command command = connection.getCommand(request);

        if (command != null && command.canExecute(context)) {
            return command;
        }

        return null;
    }
}
