/*
 * DemoContentHandler.java
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

import javax.microedition.content.ActionNameMap;
import javax.microedition.content.ContentHandler;
import javax.microedition.content.ContentHandlerException;
import javax.microedition.content.ContentHandlerServer;
import javax.microedition.content.Invocation;
import javax.microedition.content.Registry;
import javax.microedition.content.RequestListener;

import net.rim.device.api.content.DefaultContentHandlerRegistry;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.component.Dialog;

/**
 * A content handler for demonstration purposes only.
 * 
 * The handler does nothing except display a dialog when invoked. This class
 * also contains utility methods for registering and unregistering handlers.
 */
public class DemoContentHandler implements RequestListener {
    static final String ID =
            "com.rim.samples.device.chapidemo.DemoContentHandler";
    static final String[] TYPES = { "application/rimchapidemo" };
    static final String[] SUFFIXES = { "chapidemo" };
    static final String[] ACTIONS = { ContentHandler.ACTION_OPEN };

    /**
     * Creates a new DemoContentHandler object
     */
    DemoContentHandler() {
    }

    /**
     * Utility method for registering a handler. The parameters are the same as
     * <code>Registry.register</code> except for the <code>handlerName</code>
     * and <code>requestListener</code>. The <code>handlerName</code>
     * demonstrates how to use an <code>ApplicationDescriptor</code> to set the
     * name of a handler. The <code>requestListener</code> parameter is used to
     * set the listener on the registered content handler server.
     */
    static void register(final String classname, final String[] types,
            final String[] suffixes, final String[] actions,
            final ActionNameMap[] actionnames, final String id,
            final String[] accessAllowed, String handlerName,
            final RequestListener requestListener)
            throws ContentHandlerException, ClassNotFoundException {

        // Get access to the registry and register as a content handler
        final Registry registry = Registry.getRegistry(classname);

        registry.register(classname, types, suffixes, actions, null, id, null);

        // When this content handler gets requests,
        // invocationRequestNotify() will be called
        final ContentHandlerServer contentHandlerServer =
                Registry.getServer(classname);
        contentHandlerServer.setListener(requestListener);

        // Set the name of the content handler by updating the
        // ApplicationDescriptor
        final DefaultContentHandlerRegistry defaultRegistry =
                DefaultContentHandlerRegistry
                        .getDefaultContentHandlerRegistry(registry);
        final ApplicationDescriptor currentDescriptor =
                ApplicationDescriptor.currentApplicationDescriptor();
        handlerName =
                handlerName != null ? handlerName : currentDescriptor.getName();

        final ApplicationDescriptor descriptor =
                new ApplicationDescriptor(currentDescriptor, handlerName, null);
        defaultRegistry.setApplicationDescriptor(descriptor, id);
    }

    /**
     * Utility method for unregistering a content handler class. This method
     * also sets the request listener to null on the content handler server.
     */
    static void unregister(final String classname)
            throws ContentHandlerException {

        if (classname == null) {
            return;
        }

        final ContentHandlerServer contentHandlerServer =
                Registry.getServer(classname);
        contentHandlerServer.setListener(null);

        final Registry registry = Registry.getRegistry(classname);
        registry.unregister(classname);
    }

    /**
     * 
     * This method is called when the handler receives a request. This class
     * needs to be set as the listener on the <code>ContentHandlerServer</code>.
     * See {@link #register}.
     * 
     * @see RequestListener#invocationRequestNotify(ContentHandlerServer)
     */
    public void invocationRequestNotify(final ContentHandlerServer server) {
        // Retrieve Invocation from the content handler server
        final Invocation invoc = server.getRequest(false);

        if (invoc == null) {
            return; // Nothing to do
        }

        int invocationStatus = invoc.getStatus();

        try {
            final Registry registry =
                    Registry.getRegistry(getClass().getName());
            final DefaultContentHandlerRegistry defaultRegistry =
                    DefaultContentHandlerRegistry
                            .getDefaultContentHandlerRegistry(registry);
            final ApplicationDescriptor descriptor =
                    defaultRegistry.getApplicationDescriptor(server.getID());

            Dialog.alert(descriptor.getName() + " invoked for: "
                    + invoc.getURL());

            // ...
            // Other processing could be done here
            // ...

            // If there are errors or exceptions, the invocation status
            // should be updated. In this case everything is OK.
            invocationStatus = Invocation.OK;
        } finally {
            server.finish(invoc, invocationStatus);
        }
    }
}
