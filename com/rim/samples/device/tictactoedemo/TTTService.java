/*
 * TTTService.java
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

package com.rim.samples.device.tictactoedemo;

import net.rim.blackberry.api.blackberrymessenger.MessengerContact;
import net.rim.blackberry.api.blackberrymessenger.Service;
import net.rim.device.api.system.Application;

/**
 * A class to be registered with BlackBerry Messenger. The Service will appear
 * in BlackBerry Messenger's menu under the "Start Service..." menu item. This
 * will allow you to start a game of Tic Tac Toe with the contact you're in a
 * conversation with.
 * 
 * @see net.rim.blackberry.api.blackberrymessenger.Service
 */
public final class TTTService implements Service {
    private static TTTService _instance;

    /**
     * Hidden default constructor
     */
    private TTTService() {
    }

    /**
     * Gets the singleton instance of the TTTService class.
     * 
     * @return The singleton instance of the TTTService class.
     */
    public synchronized static TTTService getInstance() {
        if (_instance == null) {
            _instance = new TTTService();
        }

        return _instance;
    }

    /**
     * Implements Service interface method.
     * 
     * @see net.rim.blackberry.api.blackberrymessenger.Service#start(MessengerContact)
     */
    public void start(final MessengerContact contact) {
        Application.getApplication().requestForeground();
        TicTacToeDemo.getGame().requestTwoPlayerGame(contact);
    }
}
