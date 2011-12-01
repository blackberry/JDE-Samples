/*
 * SendNonBlockJsonScreen.java
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

package com.rim.samples.device.communicationapidemo.view;

import net.rim.device.api.io.messaging.Message;
import net.rim.device.api.io.messaging.MessageProcessorException;
import net.rim.device.api.io.parser.json.JSONMessageProcessor;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.ui.component.TreeFieldCallback;
import net.rim.device.api.ui.container.MainScreen;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.rim.samples.device.communicationapidemo.CommunicationController;
import com.rim.samples.device.communicationapidemo.ResponseCallback;
import com.rim.samples.device.communicationapidemo.ui.FullWidthButton;

public final class SendNonBlockJsonScreen extends MainScreen {
    private final TreeField _treeField;
    private final EditField _uriSenderField;
    private final ResponseCallback _callback;
    private boolean _pending;

    /**
     * Creates a new SendNonBlockJsonScreen object
     */
    public SendNonBlockJsonScreen(final CommunicationController controller) {
        setTitle("JSON");

        _callback = new SendNonBlockJsonScreenCallback(this);

        _uriSenderField =
                new EditField("URI:", CommunicationController.ECHO_SERVER_URI
                        + "JSON", 140, 0);
        final FullWidthButton postButton = new FullWidthButton("Get Data");
        postButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                if (!_pending) {
                    _pending = true;
                    clearTree("* No JSON Message *");
                    controller.sendNonBlocking(_uriSenderField.getText(), true,
                            _callback);
                } else {
                    Dialog.alert("Previous request pending state...");
                }
            }
        });

        _treeField = new TreeField(new MyTreeFieldCallback(), Field.FOCUSABLE);
        _treeField.setDefaultExpanded(false);
        clearTree("* No JSON Message *");

        add(_uriSenderField);
        add(postButton);
        add(_treeField);
    }

    private void updateTree(final Message message) {
        if (process(message)) {
            displayJsonTree(message);
        } else {
            clearTree("* No JSON Message *");
        }
    }

    private boolean process(final Message message) {
        final JSONMessageProcessor jsonMP = new JSONMessageProcessor();

        try {
            if (message != null) {
                jsonMP.process(message);
            }
        } catch (final MessageProcessorException mpe) {
            Dialog.alert(mpe.toString());
            return false;
        }

        return true;
    }

    /**
     * Display the JSON message as a tree
     */
    private void displayJsonTree(final Message message) {
        int parentNode = 0;

        _treeField.deleteAll();

        final Object obj = message.getObjectPayload();

        try {
            if (obj instanceof JSONArray) {
                parentNode =
                        populateTreeArray(_treeField, (JSONArray) obj,
                                parentNode);
            } else if (obj instanceof JSONObject) {
                parentNode =
                        populateTreeObject(_treeField, (JSONObject) obj,
                                parentNode);
            }
        } catch (final JSONException e) {
            System.out.println(e.toString());
        }

        _treeField.setCurrentNode(parentNode);
    }

    // Populate the trees with JSON arrays
    int populateTreeArray(final TreeField tree, final JSONArray o, final int p)
            throws JSONException {
        Object temp;
        int newParent;

        newParent = tree.addChildNode(p, "Array " + p);

        for (int i = 0; i < o.length(); ++i) {
            temp = o.get(i);

            if (temp == null || temp.toString().equalsIgnoreCase("null")) {
                continue;
            }

            if (temp instanceof JSONArray) {
                // Array of arrays
                populateTreeArray(tree, (JSONArray) temp, newParent);
            } else if (temp instanceof JSONObject) {
                // Array of objects
                populateTreeObject(tree, (JSONObject) temp, newParent);
            } else { // other values
                newParent = tree.addSiblingNode(newParent, temp.toString());
            }
        }

        return newParent;
    }

    // Populate the tree with JSON objects
    int
            populateTreeObject(final TreeField tree, final JSONObject o,
                    final int p) throws JSONException {
        Object temp;

        final int newParent = tree.addChildNode(p, "Object" + p);

        final JSONArray a = o.names();

        for (int i = 0; i < a.length(); ++i) {
            temp = o.get(a.getString(i));

            if (temp == null || temp.toString().equalsIgnoreCase("null")) {
                continue;
            }
            if (temp instanceof JSONArray) {
                populateTreeArray(tree, (JSONArray) temp, newParent);
            } else if (temp instanceof JSONObject) {
                populateTreeObject(tree, (JSONObject) temp, newParent);
            } else {
                tree.addSiblingNode(newParent, a.getString(i) + ": "
                        + temp.toString());
            }
        }

        return newParent;
    }

    private void clearTree(final String statusString) {
        _treeField.deleteAll();
        _treeField.setEmptyString(statusString, 0);
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }

    private final class MyTreeFieldCallback implements TreeFieldCallback {
        /**
         * @see TreeFieldCallback#drawTreeItem(TreeField, Graphics, int, int,
         *      int, int)
         */
        public void drawTreeItem(final TreeField treeField,
                final Graphics graphics, final int node, final int y,
                final int width, final int indent) {
            if (treeField == _treeField) {
                final Object cookie = _treeField.getCookie(node);
                if (cookie instanceof String) {
                    final String text = (String) cookie;
                    graphics.drawText(text, indent, y, DrawStyle.ELLIPSIS,
                            width);
                }
            }
        }
    }

    private static final class SendNonBlockJsonScreenCallback extends
            ResponseCallback {
        private final SendNonBlockJsonScreen _screen;

        private SendNonBlockJsonScreenCallback(
                final SendNonBlockJsonScreen screen) {
            _screen = screen;
        }

        public void onResponse(final Message message) {
            if (message != null) {
                _screen.updateTree(message);
            }
            _screen._pending = false;
        }

        public void onTimeout(final int timeout) {
            _screen._pending = false;
            final String timeoutMessage =
                    "Wait for response: timed out after " + timeout + " sec";
            Dialog.alert(timeoutMessage);
        }
    }
}
