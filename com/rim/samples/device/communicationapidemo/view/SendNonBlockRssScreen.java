/*
 * SendNonBlockRssScreen.java
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

import java.util.Vector;

import net.rim.device.api.io.messaging.Message;
import net.rim.device.api.io.messaging.MessageProcessorException;
import net.rim.device.api.io.parser.rss.RSSMessageProcessor;
import net.rim.device.api.io.parser.rss.model.RSSChannel;
import net.rim.device.api.io.parser.rss.model.RSSItem;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.ui.component.TreeFieldCallback;
import net.rim.device.api.ui.container.MainScreen;

import com.rim.samples.device.communicationapidemo.CommunicationController;
import com.rim.samples.device.communicationapidemo.ResponseCallback;
import com.rim.samples.device.communicationapidemo.ui.FullWidthButton;

public final class SendNonBlockRssScreen extends MainScreen {
    private static final int MAX_ITEMS = 5;

    private final TreeField _treeField;
    private final EditField _uriSenderField;
    private final ResponseCallback _callback;
    private boolean _pending;

    /**
     * Creates a new SendNonBlockRssScreen object
     */
    public SendNonBlockRssScreen(final CommunicationController controller) {
        setTitle("RSS");

        _callback = new SendNonBlockRssScreenCallback(this);

        _uriSenderField =
                new EditField("URI:", CommunicationController.ECHO_SERVER_URI
                        + "RSS", 140, 0);
        final FullWidthButton postButton = new FullWidthButton("Get Data");
        postButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                if (!_pending) {
                    _pending = true;
                    clearTree("* No RSS Message *");
                    controller.sendNonBlocking(_uriSenderField.getText(), true,
                            _callback);
                } else {
                    Dialog.alert("Previous request pending state...");
                }
            }
        });

        _treeField = new TreeField(new MyTreeFieldCallback(), Field.FOCUSABLE);
        _treeField.setDefaultExpanded(false);
        clearTree("* No RSS Message *");

        add(_uriSenderField);
        add(postButton);
        add(_treeField);

    }

    private void updateTree(final Message message) {
        if (process(message)) {
            displayRSSTree(message);
        } else {
            clearTree("* No RSS Message *");
        }
    }

    private boolean process(final Message message) {
        final RSSMessageProcessor rssMP = new RSSMessageProcessor(MAX_ITEMS);

        try {
            if (message != null) {
                rssMP.process(message);
            }
        } catch (final MessageProcessorException mpe) {
            Dialog.alert(mpe.toString());
            return false;
        }

        return true;
    }

    private void displayRSSTree(final Message message) {
        final int parentNode = 0;
        int childNode;
        RSSChannel channel;
        RSSItem item;
        Vector items;

        _treeField.deleteAll();

        final Object obj = message.getObjectPayload();

        if (obj instanceof RSSChannel) {
            channel = (RSSChannel) obj;

            items = channel.getRSSItems();

            for (int i = 0; i < items.size(); ++i) {
                item = (RSSItem) items.elementAt(i);
                final String key = item.getTitle();
                final String val = item.getDescription();
                childNode = _treeField.addChildNode(parentNode, key);
                _treeField.addChildNode(childNode, val);
            }
        }
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

    private static final class SendNonBlockRssScreenCallback extends
            ResponseCallback {

        private final SendNonBlockRssScreen _screen;

        private SendNonBlockRssScreenCallback(final SendNonBlockRssScreen screen) {
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
