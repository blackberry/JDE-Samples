/**
 * MessageListDemoViewer.java
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
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

final class DemoMessageScreen extends MainScreen {
    DemoMessageScreen(final ApplicationMessage applicationMessage) {
        final LabelField title = new LabelField("Demo Message Screen");
        setTitle(title);
        final DemoMessage demoMessage = (DemoMessage) applicationMessage;
        add(new LabelField("From: " + demoMessage.getContact()));
        add(new LabelField("Subject: " + demoMessage.getSubject()));
        add(new LabelField("Received: " + new Date(demoMessage.getTimestamp())));
        add(new LabelField("Replied? : "
                + (demoMessage.hasReplied() ? "YES" : "NO")));
        add(new SeparatorField());

        if (demoMessage.getMessage() != null) {
            add(new LabelField(demoMessage.getMessage()));
        }
    }
}
