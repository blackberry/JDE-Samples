/*
 * BrowserPlugin.java
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

package com.rim.samples.device.blackberry.browser;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.HttpConnection;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.RenderingException;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.browser.plugin.BrowserPageContext;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * To test the plugin - create a file with xxxtest extension and associate that
 * type with application/x-vnd.rim.xxxtest mime type on any web server.
 */
final class BrowserPlugin extends BrowserContentProvider implements
        BrowserPageContext {

    private static final String[] ACCEPT = { "application/x-vnd.rim.xxxtest" };

    /**
     * @see net.rim.device.api.browser.plugin.BrowserContentProvider#getAccept(net.rim.device.api.browser.field.RenderingOptions)
     */
    public String[] getAccept(final RenderingOptions context) {
        // Return subset of getSupportedMimeTypes() if accept depends in
        // rendering options
        // for example HTML can be disabled in the rendering options, and
        // HTMLConverter would remove
        // html mime types.
        return ACCEPT;
    }

    /**
     * @see net.rim.device.api.browser.plugin.BrowserContentProvider#getBrowserContent(net.rim.device.api.browser.plugin.BrowserContentProviderContext)
     */
    public BrowserContent getBrowserContent(
            final BrowserContentProviderContext context)
            throws RenderingException {
        if (context == null) {
            throw new RenderingException("No Context is passed into Provider");
        }

        final BrowserContentBaseImpl browserContentBaseImpl =
                new BrowserContentBaseImpl(
                        context.getHttpConnection().getURL(), null, context
                                .getRenderingApplication(), context
                                .getRenderingSession().getRenderingOptions(),
                        context.getFlags());

        final VerticalFieldManager vfm =
                new VerticalFieldManager(Manager.VERTICAL_SCROLL);

        vfm.add(new LabelField("Mime type: "));
        vfm.add(new LabelField(ACCEPT[0]));
        vfm.add(new SeparatorField());
        vfm.add(new LabelField("Content of the resource file: \n"));
        vfm.add(new SeparatorField());

        try {
            final HttpConnection conn = context.getHttpConnection();
            final InputStream in = conn.openInputStream();
            final int numBytes = in.available();
            final byte[] data = new byte[numBytes];
            in.read(data, 0, numBytes);
            vfm.add(new LabelField(new String(data)));

        } catch (final IOException e) {
            System.out.println(e.toString());
        }

        browserContentBaseImpl.setContent(vfm);
        browserContentBaseImpl.setTitle(ACCEPT[0]);
        // Set browser page context, this will tell the browser how to display
        // this field.
        browserContentBaseImpl.setBrowserPageContext(this);

        return browserContentBaseImpl;
    }

    /**
     * @see net.rim.device.api.browser.plugin.BrowserContentProvider#getSupportedMimeTypes()
     */
    public String[] getSupportedMimeTypes() {
        return ACCEPT;
    }

    /**
     * @see net.rim.device.api.browser.plugin.BrowserPageContext#getPropertyWithBooleanValue(int,
     *      boolean)
     */
    public boolean getPropertyWithBooleanValue(final int id,
            final boolean defaultValue) {
        return false;
    }

    /**
     * @see net.rim.device.api.browser.plugin.BrowserPageContext#getPropertyWithIntValue(int,
     *      int)
     */
    public int getPropertyWithIntValue(final int id, final int defaultValue) {
        if (id == BrowserPageContext.DISPLAY_STYLE) {
            // Disable the scroll bar .
            return BrowserPageContext.STYLE_NO_VERTICAL_SCROLLBAR;
        }

        return 0;
    }

    /**
     * @see net.rim.device.api.browser.plugin.BrowserPageContext#getPropertyWithObjectValue(int,
     *      java.lang.Object)
     */
    public Object getPropertyWithObjectValue(final int id,
            final Object defaultValue) {
        return null;
    }

    /**
     * @see net.rim.device.api.browser.plugin.BrowserPageContext#getPropertyWithStringValue(int,
     *      java.lang.String)
     */
    public String getPropertyWithStringValue(final int id,
            final String defaultValue) {
        return null;
    }
}
