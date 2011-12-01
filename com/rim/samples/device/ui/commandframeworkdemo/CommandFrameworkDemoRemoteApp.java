/*
 * CommandFrameworkDemoRemoteApp.java
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
