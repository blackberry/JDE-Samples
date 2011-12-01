/*
 * SimpleListScreen.java
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

package com.rim.samples.device.ui.tableandlistdemo;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.table.SimpleList;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A screen presenting a typical use for the SimpleList class. Populates a list
 * with data items read from a file.
 */
public final class SimpleListScreen extends MainScreen {
    /**
     * Creates a new SimpleListScreen object
     * 
     * @param deviceData
     *            Data read from file to be displayed in list
     */
    public SimpleListScreen(final DemoStringTokenizer deviceData) {
        super(Manager.NO_VERTICAL_SCROLL);

        setTitle("Simple List Screen");

        add(new LabelField("BlackBerry Devices", Field.FIELD_HCENTER));
        add(new SeparatorField());

        // Get this screen's main manager (VerticalFieldManager)
        final Manager mainManager = getMainManager();

        // Instantiate the SimpleList
        final SimpleList simpleList = new SimpleList(mainManager);

        // Iterate over the string and add comma delimited strings to the list
        while (deviceData.hasMoreTokens()) {
            deviceData.nextToken(); // Consume unwanted input
            final String modelName = deviceData.nextToken().trim();
            deviceData.nextToken();
            deviceData.nextToken();
            deviceData.nextToken();

            simpleList.add(modelName);
        }
    }
}
