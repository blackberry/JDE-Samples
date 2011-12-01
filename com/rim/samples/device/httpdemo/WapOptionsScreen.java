/**
 * WapOptionsScreen.java
 * The options screen for the httpdemo.
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

package com.rim.samples.device.httpdemo;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A simple options screen that allows the user to specify the WAP parameters to
 * use when opening an HTTP connection.
 */
final class WapOptionsScreen extends MainScreen {
    // Cconstants
    // ----------------------------------------------------------------
    private static String WAP_PARAMETERKEY_GWAYIP = ";WapGatewayIP=";
    private static String WAP_PARAMETERKEY_GWAYPORT = ";WapGatewayPort=";
    private static String WAP_PARAMETERKEY_APN = ";WapGatewayAPN=";
    private static String WAP_PARAMETERKEY_SRCIP = ";WapSourceIP=";
    private static String WAP_PARAMETERKEY_SRCPORT = ";WapSourcePort=";
    private static String WAP_DEFAULT_GWAYPORT = "9201";
    private static String WAP_DEFAULT_SOURCEIP = "127.0.0.1";
    private static String WAP_DEFAULT_SOURCEPORT = "8205";

    // Members
    // ------------------------------------------------------------------
    private final UiApplication _app;
    private final EditField _gateway;
    private final EditField _gatewayPort;
    private final EditField _apn;
    private final EditField _sourceIP;
    private final EditField _sourcePort;
    private String _wapParameters = "";
    private final MainScreen _this;
    private final MenuItem _save;

    // Constructors
    // -------------------------------------------------------------
    WapOptionsScreen(final UiApplication uiapp) {
        super();
        _this = this;
        _app = uiapp;

        // Instantiate some cached menu items.
        _save = new MenuItem("Ok", 105, 10) {
            public void run() {
                formatWapParameters();
                _app.popScreen(_this);
            }
        };

        setTitle(new LabelField("Wap Options", DrawStyle.ELLIPSIS
                | Field.USE_ALL_WIDTH));

        _gateway = new EditField("Gateway Port: ", null);
        _gatewayPort =
                new EditField("Gateway Port: ", WAP_DEFAULT_GWAYPORT,
                        TextField.DEFAULT_MAXCHARS,
                        BasicEditField.FILTER_INTEGER);
        _sourcePort =
                new EditField("Source Port: ", WAP_DEFAULT_SOURCEPORT,
                        TextField.DEFAULT_MAXCHARS,
                        BasicEditField.FILTER_INTEGER);
        _apn = new EditField("APN: ", null);
        _sourceIP = new EditField("Source IP: ", WAP_DEFAULT_SOURCEIP);

        add(_gateway);
        add(_gatewayPort);
        add(_apn);
        add(_sourcePort);
        add(_sourceIP);

        addMenuItem(_save);
    }

    // Methods
    // ------------------------------------------------------------------
    void display() {
        _app.pushScreen(this);
    }

    /**
     * Formats all the fields into the wap parameter string, ready for inclusion
     * in a Connector.open call.
     */
    private void formatWapParameters() {
        final StringBuffer sb = new StringBuffer();

        sb.append(WAP_PARAMETERKEY_GWAYIP);
        sb.append(_gateway.getText());
        sb.append(WAP_PARAMETERKEY_GWAYPORT);
        sb.append(_gatewayPort.getText());
        sb.append(WAP_PARAMETERKEY_APN);
        sb.append(_apn.getText());
        sb.append(WAP_PARAMETERKEY_SRCIP);
        sb.append(_sourceIP.getText());
        sb.append(WAP_PARAMETERKEY_SRCPORT);
        sb.append(_sourcePort.getText());

        _wapParameters = sb.toString();
    }

    /**
     * Returns a preformatted wap parameter string, appropriate to append to an
     * HTTP Connector.open string.
     * 
     * @return a preformatted wap parameter string.
     */
    String getWapParameters() {
        return _wapParameters;
    }

    /**
     * Called when there have been changes and users saves screen.
     */
    public void save() {
        formatWapParameters();
    }
}
