/*
 * BrowserFieldScreen.java
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
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.browser.field2.BrowserFieldHistory;
import net.rim.device.api.browser.field2.BrowserFieldListener;
import net.rim.device.api.browser.field2.BrowserFieldRequest;
import net.rim.device.api.script.ScriptEngine;
import net.rim.device.api.script.Scriptable;
import net.rim.device.api.script.ScriptableFunction;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

import org.w3c.dom.Document;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

/**
 * The MainScreen class for the Browser Field 2 Demo application
 */
public class BrowserFieldScreen extends MainScreen {
    private final BrowserField _browserField;
    private boolean _documentLoaded = false;
    private final BrowserFieldRequest _request;

    /**
     * Creates a new BrowserFieldScreen object
     * 
     * @param request
     *            The URI of the content to display in this BrowserFieldScreen
     * @param enableScriptMenu
     *            True if a context menu is to be created for this
     *            BrowserFieldScreen instance, false otherwise
     */
    public BrowserFieldScreen(final BrowserFieldRequest request,
            final boolean enableScriptMenu) {
        addKeyListener(new BrowserFieldScreenKeyListener());

        final BrowserFieldConfig config = new BrowserFieldConfig();
        config.setProperty(BrowserFieldConfig.ALLOW_CS_XHR, Boolean.TRUE);
        _browserField = new BrowserField(config);
        _browserField.addListener(new InnerBrowserListener());
        add(_browserField);
        _request = request;
    }

    /**
     * @see Screen#onUiEngineAttached(boolean)
     */
    protected void onUiEngineAttached(final boolean attached) {
        if (attached) {
            try {
                _browserField.requestContent(_request);
            } catch (final Exception e) {
                deleteAll();
                add(new LabelField("ERROR:\n\n"));
                add(new LabelField(e.getMessage()));
            }
        }
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Prevent the save dialog from being displayed
        return true;
    }

    /**
     * Returns this screen's BrowserField object
     * 
     * @return This screen's BrowserField object
     */
    public BrowserField getBrowserField() {
        return _browserField;
    }

    /**
     * @see MainScreen#makeMenu(Menu, int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        super.makeMenu(menu, instance);
        if (_documentLoaded
                && _browserField.getDocumentUrl().equals("local:///index.html")) {
            try {
                final Scriptable contextMenuItems =
                        (Scriptable) _browserField.getScriptEngine()
                                .executeScript("makeContextMenu()", null);
                if (contextMenuItems != null) {
                    MenuItem defaultItem = null;
                    final Integer length =
                            (Integer) contextMenuItems.getField("length");
                    for (int i = 0; i < length.intValue(); i++) {
                        final Scriptable menuItem =
                                (Scriptable) contextMenuItems.getElement(i);
                        if (menuItem != null) {
                            final String label =
                                    (String) menuItem.getField("label");
                            final Object action = menuItem.getField("action");
                            MenuItem item = null;
                            if (action instanceof String) {
                                item =
                                        new ScriptableMenuItem(label,
                                                new SimpleScriptableFunction(
                                                        (String) action));
                            } else if (action instanceof ScriptableFunction) {
                                item =
                                        new ScriptableMenuItem(label,
                                                (ScriptableFunction) action);
                            }
                            if (item != null) {
                                menu.add(item);
                                final Object isDefault =
                                        menuItem.getField("defaultItem");
                                if (isDefault != null
                                        && Scriptable.UNDEFINED
                                                .equals(isDefault) == false
                                        && ((Boolean) isDefault).booleanValue()) {
                                    defaultItem = item;
                                }
                            }
                        }
                    }
                    if (defaultItem != null) {
                        menu.setDefault(defaultItem);
                    }
                }
            } catch (final Exception e) {
                BrowserField2Demo.errorDialog(
                        "Error calling javascript script makeContextMenu().."
                                + e.getMessage(), false);
            }
        }
    }

    /**
     * A class to listen for BrowserField events
     */
    private class InnerBrowserListener extends BrowserFieldListener {
        /**
         * @see BrowserFieldListener#documentCreated(BrowserField, ScriptEngine,
         *      Document)
         */
        public void documentCreated(final BrowserField browserField,
                final ScriptEngine scriptEngine, final Document document)
                throws Exception {
            ((EventTarget) document).addEventListener("load",
                    new EventListener() {
                        public void handleEvent(final Event evt) {
                            _documentLoaded = true;
                        }
                    }, false);
        }
    }

    /**
     * A MenuItem class used to launch various scriptable functions
     */
    private static class ScriptableMenuItem extends MenuItem {
        ScriptableFunction _function;

        /**
         * Creates a new ScriptableMenuItem object
         * 
         * @param label
         *            The label for this MenuItem
         * @param function
         *            The ScriptableFunction to be executed by this MenuItem
         */
        public ScriptableMenuItem(final String label,
                final ScriptableFunction function) {
            super(label, 0, 0);
            _function = function;
        }

        public void run() {
            if (Application.isEventDispatchThread()) {
                new Thread(this).start();
            } else {
                try {
                    _function.invoke(null, null);
                } catch (final Exception e) {
                    BrowserField2Demo.errorDialog(
                            "Error invoking ScriptableFunction: "
                                    + e.getMessage(), false);
                }
            }
        }
    }

    /**
     * A class representing a function in the script environment
     */
    private class SimpleScriptableFunction extends ScriptableFunction {
        String _action;

        /**
         * Creates a new SimpleScriptableFunction object
         * 
         * @param action
         *            The action to be executed by this ScriptableFunction
         */
        public SimpleScriptableFunction(final String action) {
            _action = action;
        }

        /**
         * @see ScriptableFunction#invoke(Object, Object[])
         */
        public Object invoke(final Object thiz, final Object[] args)
                throws Exception {
            _browserField.getScriptEngine().executeScript(_action, null);
            return UNDEFINED;
        }
    }

    /**
     * A KeyListener implementation
     */
    private class BrowserFieldScreenKeyListener implements KeyListener {
        /**
         * @see KeyListener#keyChar(char, int, int)
         */
        public boolean
                keyChar(final char key, final int status, final int time) {
            if (key == 'n') {
                final Runnable nextRunnable = new Runnable() {
                    public void run() {
                        try {
                            final BrowserFieldHistory browserFieldHistory =
                                    getBrowserField().getHistory();
                            if (browserFieldHistory.canGoForward()) {
                                browserFieldHistory.goForward();
                            }
                        } catch (final Exception e) {
                            System.out.println("Error executing js:next(): "
                                    + e.getMessage());
                        }
                    }
                };
                new Thread(nextRunnable).start();
                return true;
            } else if (key == 'p' || key == Characters.ESCAPE) {
                final Runnable previousRunnable = new Runnable() {
                    public void run() {
                        try {
                            final BrowserFieldHistory browserFieldHistory =
                                    getBrowserField().getHistory();
                            if (browserFieldHistory.canGoBack()) {
                                browserFieldHistory.goBack();
                            } else {
                                if (key == Characters.ESCAPE) {
                                    synchronized (Application.getEventLock()) {
                                        close();
                                    }
                                }
                            }
                        } catch (final Exception e) {
                            System.out
                                    .println("Error executing js:previous(): "
                                            + e.getMessage());
                        }
                    }
                };
                new Thread(previousRunnable).start();
                return true;
            } else if (key == Characters.ENTER) {
                final Runnable submitRunnable = new Runnable() {
                    public void run() {
                        try {
                            getBrowserField().getScriptEngine().executeScript(
                                    "submitSearch()", null);
                        } catch (final Exception e) {
                            System.out
                                    .println("Error executing js:submitSearch(): "
                                            + e.getMessage());
                        }
                    }
                };
                new Thread(submitRunnable).start();
                return true;
            }
            return false;
        }

        /**
         * @see KeyListener#keyDown(int, int)
         */
        public boolean keyDown(final int keycode, final int time) {
            return false;
        }

        /**
         * @see KeyListener#keyRepeat(int, int)
         */
        public boolean keyRepeat(final int keycode, final int time) {
            return false;
        }

        /**
         * @see KeyListener#keyStatus(int, int)
         */
        public boolean keyStatus(final int keycode, final int time) {
            return false;
        }

        /**
         * @see KeyListener#keyUp(int, int)
         */
        public boolean keyUp(final int keycode, final int time) {
            return false;
        }
    }
}
