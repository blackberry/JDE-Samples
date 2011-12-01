/**
 * MenuManager.java
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

import net.rim.device.api.ui.UiApplication;

import com.rim.samples.device.communicationapidemo.CommunicationController;

/**
 * Manages menus and screens
 */
public final class MenuManager {
    private final UiApplication _app;
    private final CommunicationController _controller;
    private final SendBlockScreen _sendBlockScreen;
    private final SendNonBlockJsonScreen _sendNonBlockJsonScreen;
    private final SendNonBlockRssScreen _sendNonBlockRssScreen;
    private final SendNonBlockAtomScreen _sendNonBlockAtomScreen;
    private final SendNonBlockSoapScreen _sendNonBlockSoapScreen;
    private final SendNonBlockXmlScreen _sendNonBlockXmlScreen;
    private final AuthenticationScreen _authenticationScreen;
    private final StreamDataScreen _streamDataScreen;

    /**
     * Creates a new MenuManager object
     */
    public MenuManager() {
        _controller = new CommunicationController();
        _app = UiApplication.getUiApplication();
        _sendBlockScreen = new SendBlockScreen(_controller);
        _sendNonBlockJsonScreen = new SendNonBlockJsonScreen(_controller);
        _sendNonBlockRssScreen = new SendNonBlockRssScreen(_controller);
        _sendNonBlockAtomScreen = new SendNonBlockAtomScreen(_controller);
        _sendNonBlockSoapScreen = new SendNonBlockSoapScreen(_controller);
        _sendNonBlockXmlScreen = new SendNonBlockXmlScreen(_controller);
        _authenticationScreen = new AuthenticationScreen(_controller);
        _streamDataScreen = new StreamDataScreen(_controller);
    }

    /**
     * Pushes a new MenuMainScreen
     */
    public void showMenuMainScreen() {
        _app.pushScreen(new MenuMainScreen(this));
    }

    /**
     * Pushes a new MenuSendScreen
     */
    public void showMenuSendScreen() {
        _app.pushScreen(new MenuSendScreen(this));
    }

    /**
     * Pushes a new MenuSendNonBlockScreen
     */
    public void showMenuSendNonBlockScreen() {
        _app.pushScreen(new MenuSendNonBlockScreen(this));
    }

    /**
     * Pushes a new MenuReceiveScreen
     */
    public void showMenuReceiveScreen() {
        _app.pushScreen(new MenuReceiveScreen(this));
    }

    /**
     * Pushes a new ReceivePushScreen
     */
    public void showReceivePushScreen() {
        _app.pushScreen(new ReceivePushScreen(_controller));
    }

    /**
     * Pushes a new MessageCancellationScreen
     */
    public void showCancellationScreen() {
        _app.pushScreen(new MessageCancellationScreen(_controller));
    }

    /**
     * Pushes a new ReceiveIPCScreen
     */
    public void showReceiveIPCScreen() {
        _app.pushScreen(new ReceiveIPCScreen(_controller));
    }

    /**
     * Pushes a new ReceiveBPSScreen
     */
    public void showReceiveBPSScreen() {
        _app.pushScreen(new ReceiveBPSScreen(_controller));
    }

    /**
     * Pushes a new AuthenticationScreen
     */
    public void showAuthenticationScreen() {
        _app.pushScreen(_authenticationScreen);
    }

    /**
     * Pushes a new StreamDataScreen
     */
    public void showStreamDataScreen() {
        _app.pushScreen(_streamDataScreen);
    }

    /**
     * Pushes a new SendBlockScreen
     */
    public void showSendBlockScreen() {
        _app.pushScreen(_sendBlockScreen);
    }

    /**
     * Pushes a new SendFireForgetScreen
     */
    public void showSendFireForgetScreen() {
        _app.pushScreen(new SendFireForgetScreen(_controller));
    }

    /**
     * Pushes a new SendNonBlockXmlScreen
     */
    public void showSendNonBlockXmlScreen() {
        _app.pushScreen(_sendNonBlockXmlScreen);
    }

    /**
     * Pushes a new SendNonBlockAtomScreen
     */
    public void showSendNonBlockAtomScreen() {
        _app.pushScreen(_sendNonBlockAtomScreen);
    }

    /**
     * Pushes a new SendNonBlockJsonScreen
     */
    public void showSendNonBlockJsonScreen() {
        _app.pushScreen(_sendNonBlockJsonScreen);
    }

    /**
     * Pushes a new SendNonBlockRssScreen
     */
    public void showSendNonBlockRssScreen() {
        _app.pushScreen(_sendNonBlockRssScreen);
    }

    /**
     * Pushes a new SendNonBlockSoapScreen
     */
    public void showSendNonBlockSoapScreen() {
        _app.pushScreen(_sendNonBlockSoapScreen);
    }
}
