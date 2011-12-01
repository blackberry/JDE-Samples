/**
 * DemoMessage.java
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

package com.rim.samples.device.messagelistdemo;

import java.util.Date;

import net.rim.blackberry.api.messagelist.ApplicationMessage;
import net.rim.device.api.system.EncodedImage;

final class DemoMessage implements ApplicationMessage {
    static final int DEMO_MESSAGE_TYPE = 0x01;

    private String _sender;
    private String _subject;
    private String _message;
    private long _receivedTime;
    private boolean _isNew;
    private boolean _deleted;
    private String _replyMessage;
    private long _replyTime;
    private EncodedImage _previewPicture;

    DemoMessage() {
        _isNew = true;
    }

    DemoMessage(final String sender, final String subject,
            final String message, final long receivedTime) {
        _sender = sender;
        _subject = subject;
        _message = message;
        _receivedTime = receivedTime;
        _isNew = true;
    }

    void reply(final String message) {
        markRead();
        _replyMessage = message;
        _replyTime = System.currentTimeMillis();
    }

    void messageDeleted() {
        _isNew = false;
        _deleted = true;
    }

    void markAsNew() {
        _isNew = true;
        _replyMessage = null;
    }

    void markRead() {
        _isNew = false;
    }

    boolean isNew() {
        return _isNew;
    }

    boolean hasReplied() {
        return _replyMessage != null;
    }

    void setSender(final String sender) {
        _sender = sender;
    }

    void setSubject(final String subject) {
        _subject = subject;
    }

    void setReceivedTime(final long receivedTime) {
        _receivedTime = receivedTime;
    }

    void setMessage(final String message) {
        _message = message;
    }

    String getMessage() {
        return _message;
    }

    void setPreviewPicture(final EncodedImage image) {
        _previewPicture = image;
    }

    // Implementation of ApplicationMessage ------------------------------------
    /**
     * @return Contact
     * 
     * @see net.rim.blackberry.api.messagelist.ApplicationMessage#getContact()
     */
    public String getContact() {
        return _sender;
    }

    /**
     * @return Message status
     * 
     * @see net.rim.blackberry.api.messagelist.ApplicationMessage#getStatus()
     */
    public int getStatus() {
        // Form message list status based on current message state.
        if (_isNew) {
            return MessageListDemo.STATUS_NEW;
        }
        if (_deleted) {
            return MessageListDemo.STATUS_DELETED;
        }
        if (_replyMessage != null) {
            return MessageListDemo.STATUS_REPLIED;
        }
        return MessageListDemo.STATUS_OPENED;
    }

    /**
     * @return Subject
     * 
     * @see net.rim.blackberry.api.messagelist.ApplicationMessage#getSubject()
     */
    public String getSubject() {
        if (_replyMessage != null) {
            return "Re: " + _subject;
        } else {
            return _subject;
        }
    }

    /**
     * @return Non-zero timestamp
     * 
     * @see net.rim.blackberry.api.messagelist.ApplicationMessage#getTimestamp()
     */
    public long getTimestamp() {
        return _receivedTime;
    }

    /**
     * @return Message type
     * 
     * @see net.rim.blackberry.api.messagelist.ApplicationMessage#getType()
     */
    public int getType() {
        // All messages have the same type.
        return DEMO_MESSAGE_TYPE;
    }

    /**
     * @return Preview text if defined, null otherwise.
     * 
     * @see net.rim.blackberry.api.messagelist.ApplicationMessage#getPreviewText()
     */
    public String getPreviewText() {
        if (_message == null) {
            return null;
        }

        final StringBuffer buffer = new StringBuffer(_message);

        if (_replyMessage != null) {
            buffer.append(". You replied on ").append(new Date(_replyTime))
                    .append(": ").append(_replyMessage);
        }

        return buffer.length() > 100 ? buffer.toString().substring(0, 100)
                + " ..." : buffer.toString();
    }

    /**
     * @return Cookie value if provided by the message, null otherwise.
     * 
     * @see net.rim.blackberry.api.messagelist.ApplicationMessage#getCookie()
     */
    public Object getCookie(final int cookieId) {
        return null;
    }

    /**
     * @return Preview picture if provided by the message, null otherwise.
     * 
     * @see net.rim.blackberry.api.messagelist.ApplicationMessage#getPreviewPicture()
     */
    public Object getPreviewPicture() {
        return _previewPicture;
    }
}
