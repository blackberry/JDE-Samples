/*
 * BrowserFieldDemo.java 
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

import javax.microedition.io.HttpConnection;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.BrowserContentChangedEvent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RedirectEvent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingException;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.MainScreen;

final class BrowserFieldDemo extends UiApplication implements
        RenderingApplication {

    private static final String REFERER = "referer";

    private final RenderingSession _renderingSession;
    private HttpConnection _currentConnection;
    private final MainScreen _mainScreen;

    /***************************************************************************
     * Main.
     **************************************************************************/
    public static void main(final String[] args) {
        final BrowserFieldDemo app = new BrowserFieldDemo();
        app.enterEventDispatcher();
    }

    private BrowserFieldDemo() {

        _mainScreen = new MainScreen();
        pushScreen(_mainScreen);
        _renderingSession = RenderingSession.getNewInstance();

        // Enable javascript.
        // _renderingSession.getRenderingOptions().setProperty(RenderingOptions.CORE_OPTIONS_GUID,
        // RenderingOptions.JAVASCRIPT_ENABLED, true);

        final PrimaryResourceFetchThread thread =
                new PrimaryResourceFetchThread("http://www.google.com", null,
                        null, null, this);
        thread.start();

    }

    void processConnection(final HttpConnection connection, final Event e) {
        // Cancel previous request.
        if (_currentConnection != null) {
            try {
                _currentConnection.close();
            } catch (final IOException e1) {
            }
        }

        _currentConnection = connection;

        BrowserContent browserContent = null;

        try {
            browserContent =
                    _renderingSession.getBrowserContent(connection, this, e);

            if (browserContent != null) {
                final Field field = browserContent.getDisplayableContent();

                if (field != null) {
                    synchronized (Application.getEventLock()) {
                        _mainScreen.deleteAll();
                        _mainScreen.add(field);
                    }
                }

                browserContent.finishLoading();
            }

        } catch (final RenderingException re) {
        } finally {
            SecondaryResourceFetchThread.doneAddingImages();
        }

    }

    /**
     * @see net.rim.device.api.browser.RenderingApplication#eventOccurred(net.rim.device.api.browser.Event)
     */
    public Object eventOccurred(final Event event) {
        final int eventId = event.getUID();

        switch (eventId) {
        case Event.EVENT_URL_REQUESTED: {
            final UrlRequestedEvent urlRequestedEvent =
                    (UrlRequestedEvent) event;

            final PrimaryResourceFetchThread thread =
                    new PrimaryResourceFetchThread(urlRequestedEvent.getURL(),
                            urlRequestedEvent.getHeaders(), urlRequestedEvent
                                    .getPostData(), event, this);
            thread.start();

            break;

        }
        case Event.EVENT_BROWSER_CONTENT_CHANGED: {
            // Browser field title might have changed update title.
            final BrowserContentChangedEvent browserContentChangedEvent =
                    (BrowserContentChangedEvent) event;

            if (browserContentChangedEvent.getSource() instanceof BrowserContent) {
                final BrowserContent browserField =
                        (BrowserContent) browserContentChangedEvent.getSource();
                final String newTitle = browserField.getTitle();
                if (newTitle != null) {
                    synchronized (getAppEventLock()) {
                        _mainScreen.setTitle(newTitle);
                    }
                }
            }

            break;

        }
        case Event.EVENT_REDIRECT: {
            final RedirectEvent e = (RedirectEvent) event;
            String referrer = e.getSourceURL();

            switch (e.getType()) {
            case RedirectEvent.TYPE_SINGLE_FRAME_REDIRECT:
                // Show redirect message.
                Application.getApplication().invokeAndWait(new Runnable() {
                    public void run() {
                        Status.show("You are being redirected to a different page...");
                    }
                });

                break;

            case RedirectEvent.TYPE_JAVASCRIPT:
                break;

            case RedirectEvent.TYPE_META:
                // MSIE and Mozilla don't send a Referer for META Refresh.
                referrer = null;
                break;

            case RedirectEvent.TYPE_300_REDIRECT:
                // MSIE, Mozilla, and Opera all send the original
                // request's Referer as the Referer for the new
                // request.
                final Object eventSource = e.getSource();
                if (eventSource instanceof HttpConnection) {
                    referrer =
                            ((HttpConnection) eventSource)
                                    .getRequestProperty(REFERER);
                }

                break;
            }

            final HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setProperty(REFERER, referrer);
            final PrimaryResourceFetchThread thread =
                    new PrimaryResourceFetchThread(e.getLocation(),
                            requestHeaders, null, event, this);
            thread.start();
            break;

        }
        case Event.EVENT_CLOSE:
            // TODO: close the appication
            break;

        case Event.EVENT_SET_HEADER: // No cache support.
        case Event.EVENT_SET_HTTP_COOKIE: // No cookie support.
        case Event.EVENT_HISTORY: // No history support.
        case Event.EVENT_EXECUTING_SCRIPT: // No progress bar is supported.
        case Event.EVENT_FULL_WINDOW: // No full window support.
        case Event.EVENT_STOP: // No stop loading support.
        default:
        }

        return null;
    }

    /**
     * @see net.rim.device.api.browser.RenderingApplication#getAvailableHeight(net.rim.device.api.browser.BrowserContent)
     */
    public int getAvailableHeight(final BrowserContent browserField) {
        // Field has full screen.
        return Display.getHeight();
    }

    /**
     * @see net.rim.device.api.browser.RenderingApplication#getAvailableWidth(net.rim.device.api.browser.BrowserContent)
     */
    public int getAvailableWidth(final BrowserContent browserField) {
        // Field has full screen.
        return Display.getWidth();
    }

    /**
     * @see net.rim.device.api.browser.RenderingApplication#getHistoryPosition(net.rim.device.api.browser.BrowserContent)
     */
    public int getHistoryPosition(final BrowserContent browserField) {
        // No history support.
        return 0;
    }

    /**
     * @see net.rim.device.api.browser.RenderingApplication#getHTTPCookie(java.lang.String)
     */
    public String getHTTPCookie(final String url) {
        // No cookie support.
        return null;
    }

    /**
     * @see net.rim.device.api.browser.RenderingApplication#getResource(net.rim.device.api.browser.RequestedResource,
     *      net.rim.device.api.browser.BrowserContent)
     */
    public HttpConnection getResource(final RequestedResource resource,
            final BrowserContent referrer) {
        if (resource == null) {
            return null;
        }

        // Check if this is cache-only request.
        if (resource.isCacheOnly()) {
            // No cache support.
            return null;
        }

        final String url = resource.getUrl();

        if (url == null) {
            return null;
        }

        // If referrer is null we must return the connection.
        if (referrer == null) {
            final HttpConnection connection =
                    Utilities.makeConnection(resource.getUrl(), resource
                            .getRequestHeaders(), null);

            return connection;

        } else {
            // If referrer is provided we can set up the connection on a
            // separate thread.
            SecondaryResourceFetchThread.enqueue(resource, referrer);
        }

        return null;
    }

    /**
     * @see net.rim.device.api.browser.RenderingApplication#invokeRunnable(java.lang.Runnable)
     */
    public void invokeRunnable(final Runnable runnable) {
        new Thread(runnable).start();
    }
}

class PrimaryResourceFetchThread extends Thread {

    private final BrowserFieldDemo _application;
    private final Event _event;
    private final byte[] _postData;
    private final HttpHeaders _requestHeaders;
    private final String _url;

    PrimaryResourceFetchThread(final String url,
            final HttpHeaders requestHeaders, final byte[] postData,
            final Event event, final BrowserFieldDemo application) {
        _url = url;
        _requestHeaders = requestHeaders;
        _postData = postData;
        _application = application;
        _event = event;
    }

    public void run() {
        final HttpConnection connection =
                Utilities.makeConnection(_url, _requestHeaders, _postData);
        _application.processConnection(connection, _event);
    }
}
