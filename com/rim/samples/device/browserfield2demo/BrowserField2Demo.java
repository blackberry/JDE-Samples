/*
 * BrowserField2Demo.java
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

package com.rim.samples.device.browserfield2demo;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldRequest;
import net.rim.device.api.script.ScriptableFunction;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * A sample application demonstrating the usage of the BrowserField2 API. This
 * sample allows you to toggle between two different search engines using menu
 * items. The UI is an HTML file with a form for entering a search string.
 */
class BrowserField2Demo extends UiApplication {
    BrowserFieldScreen _browserScreen;

    public static final int YAHOO_SEARCH = 0;
    public static final int WIKIPEDIA_SEARCH = 1;

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new BrowserField2Demo().enterEventDispatcher();
    }

    /**
     * Creates a new BrowserField2Demo object
     */
    BrowserField2Demo() {
        try {
            final BrowserFieldRequest request =
                    new BrowserFieldRequest("local:///index.html");
            _browserScreen = new BrowserFieldScreen(request, true);
            extendJavaScript(_browserScreen.getBrowserField());
            pushScreen(_browserScreen);
        } catch (final Exception e) {
            errorDialog("An error occurred, exiting Browser Field 2 Demo: "
                    + e.toString(), true);
        }
    }

    /**
     * Maps a javascript function to a BlackBerry device app menu item
     * 
     * @param browserField
     *            The BrowserField displayed by this applications's screen
     */
    public void extendJavaScript(final BrowserField browserField)
            throws Exception {
        final ScriptableFunction toggleSearch = new ScriptableFunction() {
            public Object invoke(final Object thiz, final Object[] args)
                    throws Exception {
                if (args.length > 0) {
                    // _currentSearch = Integer.parseInt(args[0].toString());
                }
                return Boolean.FALSE;
            }
        };
        browserField.extendScriptEngine("bb.toggleSearch", toggleSearch);

        final ScriptableFunction submitSearch = new ScriptableFunction() {
            public Object invoke(final Object thiz, final Object[] args)
                    throws Exception {
                executeSearch(Integer.parseInt(args[0].toString()), args[1]
                        .toString());
                return Boolean.FALSE;
            }
        };
        browserField.extendScriptEngine("bb.submitSearch", submitSearch);
    }

    /**
     * Executes a search query
     * 
     * @param searchEngine
     *            Integer value indicating which search type to use
     * @param query
     *            Query string to search on
     */
    private void executeSearch(final int searchEngine, final String query) {
        if (searchEngine == YAHOO_SEARCH) {
            _browserScreen.getBrowserField().requestContent(
                    "http://search.yahoo.com/search?p=" + query);
        } else if (searchEngine == WIKIPEDIA_SEARCH) {
            _browserScreen.getBrowserField().requestContent(
                    "http://en.wikipedia.org/wiki/Special:Search?search="
                            + query + "&go=Go");
        }
    }

    /**
     * Presents a dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     * @param exit
     *            True if the application should exit, false otherwise
     */
    public static void errorDialog(final String message, final boolean exit) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(message);
                if (exit) {
                    System.exit(1);
                }
            }
        });
    }
}
