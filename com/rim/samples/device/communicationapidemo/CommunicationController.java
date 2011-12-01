/**
 * CommunicationController.java
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

package com.rim.samples.device.communicationapidemo;

import java.io.ByteArrayInputStream;
import java.util.Hashtable;

import net.rim.device.api.io.URI;
import net.rim.device.api.io.messaging.BlockingReceiverDestination;
import net.rim.device.api.io.messaging.BlockingSenderDestination;
import net.rim.device.api.io.messaging.BpsSubscriptionMessageBuilder;
import net.rim.device.api.io.messaging.ByteMessage;
import net.rim.device.api.io.messaging.Context;
import net.rim.device.api.io.messaging.CredentialsCollector;
import net.rim.device.api.io.messaging.Destination;
import net.rim.device.api.io.messaging.DestinationFactory;
import net.rim.device.api.io.messaging.FireAndForgetDestination;
import net.rim.device.api.io.messaging.HttpMessage;
import net.rim.device.api.io.messaging.InboundDestinationConfiguration;
import net.rim.device.api.io.messaging.InboundDestinationConfigurationFactory;
import net.rim.device.api.io.messaging.Message;
import net.rim.device.api.io.messaging.MessageFailureException;
import net.rim.device.api.io.messaging.MessageListener;
import net.rim.device.api.io.messaging.NonBlockingReceiverDestination;
import net.rim.device.api.io.messaging.NonBlockingSenderDestination;
import net.rim.device.api.io.messaging.SenderDestination;
import net.rim.device.api.io.messaging.StreamMessage;
import net.rim.device.api.io.messaging.UsernamePasswordCredentials;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import com.rim.samples.device.communicationapidemo.util.Utils;

/**
 * Contains methods which demonstrate Communication API use cases
 */
public final class CommunicationController {

    // URI of a simple echo server which responds to http
    // get requests sent from the application by sending
    // back requested resource (xml, text, json, etc).
    public static String ECHO_SERVER_URI;

    public static int TIMEOUT; // Response timeout
    private static UiApplication _app;
    private static int _listenerId = 1; // Used to generate id

    private final Context _context;
    private NonBlockingSenderDestination _cancellationDest;

    /**
     * Creates a new CommunicationController object
     */
    public CommunicationController() {
        _context = new Context("MyContext");
        _app = UiApplication.getUiApplication();

        try {
            // Read settings from config.xml
            ECHO_SERVER_URI = Utils.getEchoServerUri();
            TIMEOUT = Utils.getTimeout();
        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Displays a pop-up dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     */
    public static void alertDialog(final String message) {
        _app.invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(message);
            }
        });
    }

    /**
     * Sends get request message using non-blocking destination and listens for
     * the response in the created listener.
     * 
     * @param uriStr
     *            Uri where the request is sent to
     * @param releaseDestination
     *            Release destination on completion of the method
     * @param callback
     *            Callback object responsible for updating UI upon message
     *            delivery / request timeout
     */
    public void sendNonBlocking(final String uriStr,
            final boolean releaseDestination, final ResponseCallback callback) {
        MonitoredDestinationListener responseListener;

        try {
            final URI uri = URI.create(uriStr);

            responseListener =
                    new MonitoredDestinationListener(
                            "SendNonBlocking MessageListener [id: "
                                    + _listenerId++ + "]");
            final SenderDestination senderDestination =
                    DestinationFactory.createNonBlockingSenderDestination(
                            _context, uri, responseListener);

            final TimeoutMonitor timeoutThread =
                    new TimeoutMonitor(senderDestination, releaseDestination,
                            callback, responseListener);
            final Thread t1 = new Thread(timeoutThread, "monitor");
            t1.start();

        } catch (final Exception e) {
            alertDialog(e.toString());
        }

    }

    /**
     * Sends get request message to the URI and blocks until response received
     * or we timed out.
     * 
     * @param uriSenderStr
     *            Sender destination URI
     * @param listener
     *            Listener for callback
     */
    public void sendBlocking(final String uriSenderStr,
            final CommunicationControllerListener listener) {
        new Thread(new Runnable() {
            /**
             * @see Runnable#run()
             */
            public void run() {
                try {

                    final SendBlockingThread ts =
                            new SendBlockingThread(uriSenderStr);
                    ts.start();

                    // Blocking this thread until message received or timeout
                    for (int i = 0; i < TIMEOUT; i++) {
                        if (ts.isCompleted()) {
                            listener.onWaitTimerCompleted();
                            return; // Thread finished execution
                        }

                        Thread.sleep(1000); // Sleep 1 sec
                        listener.onWaitTimerCounterChanged(i + 1);
                    }

                    if (ts.getResponse() == null && ts.isAlive()) // Timeout
                    {
                        ts.interrupt(); // Force thread to stop
                        listener.onWaitTimerCompleted();
                        alertDialog("Timed out after " + TIMEOUT + " sec");
                        return;
                    }

                } catch (final Exception e) {
                    alertDialog(e.toString());
                }
            }
        }).start();
    }

    /**
     * Sends message to the URI using FireAndForget destination
     * 
     * @param uriSenderStr
     *            Sender destination URI
     */
    public void sendFireForget(final String uriSenderStr) {
        FireAndForgetDestination fireForgetDest = null;
        try {
            fireForgetDest =
                    (FireAndForgetDestination) DestinationFactory
                            .getSenderDestination(_context.getName(), URI
                                    .create(uriSenderStr));

            if (fireForgetDest == null) {
                fireForgetDest =
                        DestinationFactory.createFireAndForgetDestination(
                                _context, URI.create(uriSenderStr));
            }
            final int msgId = fireForgetDest.sendNoResponse();

            alertDialog("Message [id:" + msgId + "] has been sent!");
        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Creates receiver destination (BES) for the URI and registers a listener
     * 
     * @param uriStr
     *            URI of the receiver destination
     * @param autoStartEnabled
     *            Auto start application on incoming messages
     */
    public void startNonBlockingReceiverBES(final String uriStr,
            final boolean autoStartEnabled) {
        NonBlockingReceiverDestination nonBlockRecvDest = null;
        try {
            nonBlockRecvDest =
                    (NonBlockingReceiverDestination) DestinationFactory
                            .getReceiverDestination(URI.create(uriStr));
            if (nonBlockRecvDest == null) // Not registered yet
            {
                final MessageListener responseListener =
                        new DestinationListener("NonBlockingReceiverBES", true);

                // Prepare the in-bound destination for incoming messages
                // (BES/http)
                final InboundDestinationConfiguration config =
                        InboundDestinationConfigurationFactory
                                .createBESConfiguration(autoStartEnabled, true,
                                        false);

                nonBlockRecvDest =
                        DestinationFactory
                                .createNonBlockingReceiverDestination(config,
                                        URI.create(uriStr), responseListener);
            }
        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Creates blocking receiver destination (BES) in a separate thread which
     * blocks till message arrives or times out. This method should be called
     * every time you expect a new message, since after receiving one message
     * the thread listening for message completes.
     * 
     * @param uriStr
     *            Receiver destination URI
     * @param autoStartEnabled
     *            Auto-start application on incoming messages
     * @param timeout
     *            Timeout for receiving the incoming push message
     */
    public void startBlockingReceiverBES(final String uriStr,
            final boolean autoStartEnabled, final int timeout) {
        // Create and start thread
        final ReceiveBlockingThread t =
                new ReceiveBlockingThread(uriStr,
                        InboundDestinationConfiguration.CONFIG_TYPE_BES,
                        autoStartEnabled, timeout);
        t.start();
    }

    /**
     * Creates non-blocking receiver destination (IPC) for the URI and registers
     * a listener.
     * 
     * @param uriStr
     * @param autoStartEnabled
     *            Auto-start application on incoming messages
     */
    public void startNonBlockingReceiverIPC(final String uriStr,
            final boolean autoStartEnabled) {
        NonBlockingReceiverDestination nonBlockRecvDest = null;

        try {
            nonBlockRecvDest =
                    (NonBlockingReceiverDestination) DestinationFactory
                            .getReceiverDestination(URI.create(uriStr));

            if (nonBlockRecvDest == null) // Not registered yet
            {
                final SimpleListener responseListener =
                        new SimpleListener("NonBlockingReceiverIPC");

                // Prepare the inbound destination for incoming
                // messages (Inter Process Communication).
                final InboundDestinationConfiguration config =
                        InboundDestinationConfigurationFactory
                                .createIPCConfiguration(autoStartEnabled, true,
                                        false);

                nonBlockRecvDest =
                        DestinationFactory
                                .createNonBlockingReceiverDestination(config,
                                        URI.create(uriStr), null,
                                        responseListener);
            }
        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Creates blocking receiver destination (IPC) in a separate thread which
     * blocks till message arrives or times out. This method should be called
     * every time you expect a new message, since after receiving one message
     * the thread listening for the message completes.
     * 
     * @param uriStr
     *            Receiver destination URI
     * @param autoStartEnabled
     *            Auto-start application on incoming messages
     * @param timeout
     *            Timeout for receiving the incoming push message
     */
    public void startBlockingReceiverIPC(final String uriStr,
            final boolean autoStartEnabled, final int timeout) {
        // create and start thread
        final ReceiveBlockingThread t =
                new ReceiveBlockingThread(uriStr,
                        InboundDestinationConfiguration.CONFIG_TYPE_IPC,
                        autoStartEnabled, timeout);
        t.start();
    }

    /**
     * Pauses destination from receiving incoming push messages
     */
    public void pauseReceiver(final String uriStr) {
        try {
            final Destination dest =
                    DestinationFactory.getReceiverDestination(URI
                            .create(uriStr));
            if (dest != null) {
                dest.pause();
            }
        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Resumes delivery of messages to destination specified by provided URI
     */
    public void resumeReceiver(final String uriStr) {
        try {
            final Destination dest =
                    DestinationFactory.getReceiverDestination(URI
                            .create(uriStr));
            if (dest != null) {
                dest.resume();
            }
        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Releases destination
     * 
     * @see Destination#release()
     */
    public void releaseReceiver(final String uriStr) {
        try {
            final Destination dest =
                    DestinationFactory.getReceiverDestination(URI
                            .create(uriStr));
            if (dest != null) {
                dest.release();
            }
        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Destroys destination - should be executed when the app is uninstalled
     */
    public void destroyReceiver(final String uriStr) {
        try {
            final Destination dest =
                    DestinationFactory.getReceiverDestination(URI
                            .create(uriStr));
            if (dest != null) {
                dest.destroy();
            }
        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Sets the destination. Used by message cancellation test.
     */
    public void setNonBlockingSenderDestination(final String uriStr) {
        final MessageListener responseListener =
                new DestinationListener("Cancellation Listener", true);

        try {
            final URI uri = URI.create(uriStr);
            SenderDestination destination =
                    DestinationFactory.getSenderDestination(_context.getName(),
                            uri);
            if (destination == null) {
                // Destination not registered yet
                destination =
                        DestinationFactory.createNonBlockingSenderDestination(
                                _context, uri, responseListener);
            }
            _cancellationDest = (NonBlockingSenderDestination) destination;

        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Sends cancellable/non-cancellable message and tries to cancel using
     * destination.
     * 
     * @param msgString
     *            Message string to be sent with the message
     * @param isCancellable
     *            True if message can be canceled, otherwise false
     */
    public void testMessageCancellable(final String msgString,
            final boolean isCancellable) {
        if (_cancellationDest == null) {
            // Check if destination is created
            alertDialog("Please register destination first!");
            return;
        }

        final ByteMessage msg = _cancellationDest.createByteMessage();

        try {
            msg.setStringPayload(msgString);
            msg.setCancellable(isCancellable); // Cancellable
            final int msgId = _cancellationDest.send(msg);
            _cancellationDest.cancel(msgId);
        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /*
     * Sends non-cancellable and cancellable messages, then tries to cancel them
     * on best effort.
     */
    public void testCancelAllCancellable() {
        try {
            if (_cancellationDest == null) // check if destination is created
            {
                alertDialog("Please register destination first!");
                return;
            }

            final int numMsg = 5; // number of messages to be sent

            final ByteMessage[] messages = new ByteMessage[numMsg];
            final int[] msgIds = new int[numMsg];

            // Create messages
            for (int i = 0; i < messages.length; i++) {
                messages[i] = _cancellationDest.createByteMessage();
                messages[i].setCancellable(true);
                messages[i].setStringPayload("Message " + i + "[cancellable]");
            }

            // Reset two first messages as non-cancellable
            messages[0].setCancellable(false);
            messages[0].setStringPayload("Message 0 [non-cancellable]");
            messages[1].setCancellable(false);
            messages[1].setStringPayload("Message 1 [non-cancellable]");

            // Send messages and read IDs
            for (int i = 0; i < messages.length; i++) {
                msgIds[i] = _cancellationDest.send(messages[i]);
            }

            // Use another method to cancel all
            _cancellationDest.cancelAllCancellable();

            // NOTE: If a message has already been sent, then it
            // can not be canceled.

        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Gets response from a URI which requires credentials
     * 
     * @param callback
     *            Callback object responsible for updating UI upon message
     *            delivery / request timeout
     */
    public void authenticate(final String uriStr, final String username,
            final String password, final ResponseCallback callback) {
        MonitoredDestinationListener responseListener;

        try {
            final URI uri = URI.create(uriStr);
            final String user = username;
            final String pass = password;

            final CredentialsCollector credentialsCollector =
                    new CredentialsCollector() {
                        public UsernamePasswordCredentials
                                getBasicAuthenticationCredentials(
                                        final String authenticatedEntityID,
                                        final Hashtable properties) {
                            return new UsernamePasswordCredentials(user, pass);
                        }
                    };

            final Context context =
                    new Context("Authenticate", credentialsCollector);

            responseListener =
                    new MonitoredDestinationListener(
                            "Authentification MessageListener [id: "
                                    + _listenerId++ + "]");
            final SenderDestination senderDestination =
                    DestinationFactory.createNonBlockingSenderDestination(
                            context, uri, responseListener);

            final TimeoutMonitor timeoutThread =
                    new TimeoutMonitor(senderDestination, true, callback,
                            responseListener);
            final Thread t1 = new Thread(timeoutThread, "monitor");
            t1.start();

        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Registers/subscribes the application for receiving BPS pushes
     */
    public void registerBPSPush(final String appId, final String BPDSUri,
            final String contentUri, final String listenUri) {
        NonBlockingReceiverDestination listenDest = null;
        NonBlockingSenderDestination bpsServerDest = null;
        try {
            final MessageListener bpsListener =
                    new DestinationListener("BPS", true);

            final InboundDestinationConfiguration config1 =
                    InboundDestinationConfigurationFactory
                            .createBPSConfiguration(true, true, false, appId,
                                    BPDSUri); // serverUrl

            listenDest =
                    DestinationFactory.createNonBlockingReceiverDestination(
                            config1, URI.create(listenUri), bpsListener);
            final MessageListener subscriptionListener =
                    new SubscriptionResponseListener("Subscription");
            bpsServerDest =
                    DestinationFactory.createNonBlockingSenderDestination(
                            _context, URI.create(contentUri),
                            subscriptionListener);

            final ByteMessage subscrMsg =
                    BpsSubscriptionMessageBuilder
                            .createByteSubscriptionMessage(bpsServerDest,
                                    listenDest, "user", "pwd");

            ((HttpMessage) subscrMsg).setQueryParam("field1", "xxxxx");
            ((HttpMessage) subscrMsg).setQueryParam("osversion", "6.0");
            bpsServerDest.send(subscrMsg);
        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Uploads data in the form of streams to a server
     * 
     * @param callback
     *            Callback object responsible for updating UI upon message
     *            delivery / request timeout
     */
    public void uploadStream(final String uploadUri,
            final ResponseCallback callback) {

        MonitoredDestinationListener responseListener;

        try {
            final URI uri = URI.create(uploadUri + ";deviceSide=true");

            responseListener =
                    new MonitoredDestinationListener(
                            "StreamData MessageListener [id: " + _listenerId++
                                    + "]");
            final SenderDestination senderDestination =
                    DestinationFactory.createNonBlockingSenderDestination(
                            _context, uri, responseListener);

            // Create a byte array of size 10000
            final byte[] input = new byte[10000];
            for (int i = 0; i < input.length; i++) {
                input[i] = '5';
            }

            final ByteArrayInputStream bais = new ByteArrayInputStream(input);

            final StreamMessage message =
                    senderDestination.createStreamMessage();
            ((HttpMessage) message).setMethod(HttpMessage.POST);

            message.setStreamPayload(bais);
            message.setPriority(2);
            message.setTransportHeader("Transfer-Encoding", "chunked");

            final TimeoutMonitor timeoutThread =
                    new TimeoutMonitor(senderDestination, true, callback,
                            responseListener, message);
            final Thread t1 = new Thread(timeoutThread, "monitor");

            t1.start();

        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Sender thread to sent a message using BlockingSenderDestination
     */
    private final class SendBlockingThread extends Thread {
        String _uriStr;
        Message _response;
        boolean _completed;

        /**
         * Creates new SendBlockingThread object
         * 
         * @param uriStr
         *            URI to create BlockingSenderDestination
         */
        public SendBlockingThread(final String uriStr) {
            _uriStr = uriStr;
            _response = null;
        }

        /**
         * Gets the response received on get request sent using
         * BlockingSenderDestination.
         * 
         * @return Response message
         */
        public Message getResponse() {
            return _response;
        }

        /**
         * Sends get request and receives response using
         * BlockingSenderDestination
         * 
         * @see Thread#run()
         */
        public void run() {
            BlockingSenderDestination blockSendDest = null;
            try {
                blockSendDest =
                        (BlockingSenderDestination) DestinationFactory
                                .getSenderDestination(_context.getName(), URI
                                        .create(_uriStr));
                if (blockSendDest == null) {
                    blockSendDest =
                            DestinationFactory.createBlockingSenderDestination(
                                    _context, URI.create(_uriStr));
                }

                // Send message and wait for response
                _response = blockSendDest.sendReceive();

                if (_response != null) {
                    _completed = true;

                    // For "http"
                    final String alertString =
                            "received Message [id:"
                                    + _response.getMessageId()
                                    + "]\n"
                                    + "Response Code: "
                                    + ((HttpMessage) _response)
                                            .getResponseCode();
                    alertDialog(alertString);
                }
            } catch (final Exception e) {
                _completed = true;
                alertDialog(e.toString());
            } finally {
                if (blockSendDest != null) {
                    blockSendDest.release();
                }
            }
        }

        public boolean isCompleted() {
            return _completed;
        }
    }

    private static final class ReceiveBlockingThread extends Thread {
        private Message _response;
        private final String _uriStr;
        private final int _configType;
        private final int _timeout;
        private final boolean _autoStartEnabled;

        /**
         * Create new ReceiveBlockingThread object
         * 
         * @param uriStr
         *            URI of the receiver
         * @param configType
         *            Inbound destination configuration type
         * @param autoStartEnabled
         *            Enable auto-start of the application when message received
         *            and application is closed
         * @param timeout
         *            Response timeout
         */
        public ReceiveBlockingThread(final String uriStr, final int configType,
                final boolean autoStartEnabled, final int timeout) {
            _uriStr = uriStr;
            _configType = configType;
            _autoStartEnabled = autoStartEnabled;
            _timeout = timeout;
            _response = null;
        }

        /**
         * @see Thread#run()
         */
        public void run() {
            BlockingReceiverDestination blockRecvDest = null;
            try {
                blockRecvDest =
                        (BlockingReceiverDestination) DestinationFactory
                                .getReceiverDestination(URI.create(_uriStr));

                if (blockRecvDest == null) // Not registered yet
                {
                    InboundDestinationConfiguration config;

                    // Prepare the inbound destination for incoming messages
                    if (_configType == InboundDestinationConfiguration.CONFIG_TYPE_BES) {
                        config =
                                InboundDestinationConfigurationFactory
                                        .createBESConfiguration(
                                                _autoStartEnabled, true, false);
                    } else if (_configType == InboundDestinationConfiguration.CONFIG_TYPE_IPC) {
                        config =
                                InboundDestinationConfigurationFactory
                                        .createIPCConfiguration(
                                                _autoStartEnabled, true, false);
                    } else {
                        throw new IllegalArgumentException(
                                "Invalid InboundDestinationConfiguration type! Implemented support of IPC and BES only.");
                    }

                    blockRecvDest =
                            DestinationFactory
                                    .createBlockingReceiverDestination(config,
                                            URI.create(_uriStr));
                }

                String alertString = "";
                _response = blockRecvDest.receive(_timeout * 1000);

                if (_response != null) {
                    alertString =
                            "RECEIVED[id: " + _response.getMessageId() + "]:";
                    final String stringPayload =
                            ((ByteMessage) _response).getStringPayload();
                    if (stringPayload != null) {
                        alertString += "\n" + stringPayload;
                    }
                } else {
                    // No response received
                    alertString =
                            "No message has been received during timeout of "
                                    + _timeout + " sec.";
                }

                alertDialog(alertString);
            } catch (final Exception e) {
                alertDialog(e.toString());
            }
        }
    }

    /**
     * Message Listener to be used with non-blocking destinations for
     * asynchronous message processing.
     */
    private static class DestinationListener implements MessageListener {
        private final String _name;
        private boolean _onMessage = false;
        private Message _response = null;
        private final boolean _alertOnMessage;

        /**
         * Create new DestinationListener object
         * 
         * @param name
         *            Name of the listener
         */
        private DestinationListener(final String name) {
            this(name, false);
        }

        /**
         * Creates a new DestinationListener object
         * 
         * @param name
         *            Name of the listener
         * @param alertOnMessage
         *            Display alert dialog when message is received
         */
        private DestinationListener(final String name,
                final boolean alertOnMessage) {
            _name = name;
            _alertOnMessage = alertOnMessage;
        }

        /**
         * @see MessageListener#onMessage(Destination, Message)
         */
        public void onMessage(final Destination destination,
                final Message message) {
            _response = message;
            _onMessage = true;

            if (_alertOnMessage) {
                String alertString =
                        _name + " :RECEIVED[id: " + message.getMessageId()
                                + "]:";

                final String stringPayload =
                        ((ByteMessage) message).getStringPayload();
                if (stringPayload != null) {
                    alertString += "\n" + stringPayload;
                }
                alertDialog(alertString);
            }
        }

        /**
         * @see MessageListener#onMessageCancelled(Destination, int)
         */
        public void onMessageCancelled(final Destination destination,
                final int cancelledMessageId) {
            _onMessage = true;
            alertDialog("CANCELLED - " + destination.getUri().toString()
                    + " id: " + cancelledMessageId);
        }

        /**
         * @see MessageListener#onMessageFailed(Destination,
         *      MessageFailureException)
         */
        public void onMessageFailed(final Destination destination,
                final MessageFailureException exception) {
            _onMessage = true;
            alertDialog("FAILED -  " + exception.toString());
        }

        /**
         * Checks if message event methods were called
         * 
         * @return <code>true</code> if one of the message events happened
         */
        public boolean isOnMessage() {
            return _onMessage;
        }

        /**
         * Gets response message
         * 
         * @return message Response
         */
        public Message getResponse() {
            return _response;
        }
    }

    /**
     * Message Listener for processing subscription responses using BPS
     */
    private final class SubscriptionResponseListener extends
            DestinationListener {
        /**
         * Creates a new SubscriptionResponseListener object
         * 
         * @param name
         *            Name of the listener
         */
        private SubscriptionResponseListener(final String name) {
            super(name);
        }

        /**
         * @see DestinationListener#onMessage(Destination, Message)
         */
        public void onMessage(final Destination destination,
                final Message message) {
            super.onMessage(destination, message);
            final String s = ((ByteMessage) message).getStringPayload();
            if (s.equalsIgnoreCase("rc=200")) {
                alertDialog("Subscription successful.");
            } else {
                alertDialog("Subscription failed.");
            }
        }
    }

    /**
     * MessageListener to be used with destinations - displays information about
     * message when message arrives, fails or is canceled.
     */
    private static final class SimpleListener implements MessageListener {
        private final String _name;

        /**
         * Creates a new SimpleListener object
         * 
         * @param name
         *            Name of the listener
         */
        private SimpleListener(final String name) {
            _name = name;
        }

        /**
         * @see MessageListener#onMessage(Destination, Message)
         */
        public void onMessage(final Destination destination,
                final Message message) {
            String alertString =
                    _name + " :RECEIVED[id: " + message.getMessageId() + "]:";

            // Read payload
            if (message instanceof ByteMessage) {
                final String stringPayload =
                        ((ByteMessage) message).getStringPayload();
                if (stringPayload != null) {
                    alertString += "\n" + stringPayload;
                }
            }

            alertDialog(alertString);
        }

        /**
         * @see MessageListener#onMessageCancelled(Destination, int)
         */
        public void onMessageCancelled(final Destination destination,
                final int cancelledMessageId) {
            alertDialog("CANCELLED - " + destination.getUri().toString()
                    + " id: " + cancelledMessageId);
        }

        /**
         * @see MessageListener#onMessageFailed(Destination,
         *      MessageFailureException)
         */
        public void onMessageFailed(final Destination destination,
                final MessageFailureException exception) {
            alertDialog("FAILED - " + exception.toString());
        }

    }

    private final class MonitoredDestinationListener extends
            DestinationListener {
        private final Object _lock;

        /**
         * Creates a new DestinationListener object
         * 
         * @param name
         *            Name of the listener
         */
        private MonitoredDestinationListener(final String name) {
            super(name);
            _lock = "str_lock";
        }

        public Object getLock() {
            return _lock;
        }

        public void onMessage(final Destination destination,
                final Message message) {
            super.onMessage(destination, message);

            synchronized (_lock) {
                _lock.notifyAll();
            }
        }

    }

    private static final class TimeoutMonitor implements Runnable {

        private final SenderDestination _destination;
        private final MonitoredDestinationListener _responseListener;
        private final boolean _releaseDestination;
        private final ResponseCallback _responseCallback;
        private Message _message;

        public TimeoutMonitor(final SenderDestination destination,
                final boolean releaseDestination,
                final ResponseCallback callback,
                final MonitoredDestinationListener responseListener) {
            _destination = destination;
            _releaseDestination = releaseDestination;
            _responseCallback = callback;
            _responseListener = responseListener;
        }

        public TimeoutMonitor(final SenderDestination destination,
                final boolean releaseDestination,
                final ResponseCallback callback,
                final MonitoredDestinationListener responseListener,
                final Message message) {
            this(destination, releaseDestination, callback, responseListener);
            _message = message;

        }

        public void run() {

            try {
                final Object lock = _responseListener.getLock();

                // Send message to retrieve the response
                if (_message == null) {

                    ((NonBlockingSenderDestination) _destination).send();
                } else {
                    ((NonBlockingSenderDestination) _destination)
                            .send(_message);
                }

                // Wait till a response is received by the message listener
                synchronized (lock) {
                    try {
                        lock.wait(TIMEOUT * 1000);
                    } catch (final InterruptedException ex) {
                        //
                    }
                }

                if (_responseListener.isOnMessage()) {
                    final Message responseMsg = _responseListener.getResponse();
                    _responseCallback.updateUI(responseMsg);
                } else {
                    _responseCallback.timeoutDialog(TIMEOUT);
                }

            } catch (final Exception e) {
                alertDialog(e.toString());
            } finally {
                if (_releaseDestination && _destination != null) {
                    _destination.release();
                }
            }
        }
    }
}
