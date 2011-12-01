/*
 * CommandFrameworkDemoRemoteApp.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.commandframeworkdemo;

import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.CommandMetadata;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.command.registrar.RemoteCommandRegistrarConnection;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * This class consists of an entry point which runs on startup and registers the
 * nested class RemoteAppCommandHandler with RemoteCommandRegistrarConnection.
 * An instance of the handler class can then be retrieved by remote applications
 * through RemoteCommandRegistrarConnection.
 */
public class CommandFrameworkDemoRemoteApp {
    public static final String COMMAND_ID =
            "CommandFrameworkDemoRemoteAppCommand";

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        final CommandMetadata metadata = new CommandMetadata(COMMAND_ID);
        metadata.setClassname(RemoteAppCommandHandler.class.getName());
        final RemoteCommandRegistrarConnection connection =
                new RemoteCommandRegistrarConnection();
        connection.registerCommand(null, metadata);
    }

    /**
     * A CommandHandler implementation
     */
    public static class RemoteAppCommandHandler extends CommandHandler {
        /**
         * @see CommandHandler#execute(ReadOnlyCommandMetadata, Object)
         */
        public void execute(final ReadOnlyCommandMetadata metadata,
                final Object context) {
            if (context instanceof UiApplication) {
                // Display a Dialog on the caller's event thread
                ((UiApplication) context).invokeLater(new Runnable() {
                    public void run() {
                        Dialog.alert("Executed remote app command handler");
                    }
                });
            }
        }
    }
}
