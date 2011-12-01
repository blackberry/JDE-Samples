/*
 * MemoApiDemo.java
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

package com.rim.samples.device.memoapidemo;

import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;

import net.rim.blackberry.api.pdap.BlackBerryPIM;
import net.rim.blackberry.api.pdap.BlackBerryPIMList;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Demo application that shows how to use BlackBerry's memo extension of the PIM
 * API.
 */
public final class MemoApiDemo extends UiApplication {

    /**
     * Application entry point. Determines if the application is being run
     * automatically as a system module at startup, or as a GUI from desktop. In
     * the case of the former, it registers a PIMListListener; otherwise, it
     * launches the GUI.
     * 
     * @param args
     *            Command-line arguments.
     */
    public static void main(final String[] args) {
        if (args != null && args.length > 0 && args[0].equals("init")) {
            // Running as a system module automatically at startup; register
            // a PIMListListener.
            final PIM p = PIM.getInstance();

            try {
                final BlackBerryPIMList memoList =
                        (BlackBerryPIMList) p.openPIMList(
                                BlackBerryPIM.MEMO_LIST, PIM.READ_WRITE);
                memoList.addListener(new MemoListListener());
            } catch (final PIMException e) {
                // Can't add listener.
            }
        } else {
            // Running normally; start the GUI.
            new MemoApiDemo().enterEventDispatcher();
        }
    }

    /**
     * Constructor. Creates and displays the application's main screen.
     */
    private MemoApiDemo() {
        final MainScreen mainScreen = new MemoMainScreen();

        mainScreen.setTitle(new LabelField("Memo API Demo", Field.USE_ALL_WIDTH
                | DrawStyle.ELLIPSIS));
        pushScreen(mainScreen);
    }
}
