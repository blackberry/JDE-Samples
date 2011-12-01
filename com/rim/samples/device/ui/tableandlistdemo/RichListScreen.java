/*
 * RichListScreen.java
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

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.table.RichList;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModel;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Screen demonstrating the use of the RichList object. Displays a list of
 * BlackBerry Smartphone devices with complex formatting and accompanying
 * images. Clicking or tapping on a row displays selected device in a pop up
 * dialog.
 */
public final class RichListScreen extends MainScreen {
    private final static int BITMAP = 0;
    private final static int DISPLAY_NAME = 1;
    private final static int OS = 2;
    private final static int YEAR = 3;
    private final static int INTERFACES = 4;

    /**
     * Creates a new RichListScreen object
     * 
     * @param deviceData
     *            Data read from file to be displayed in list
     */
    public RichListScreen(final DemoStringTokenizer deviceData) {
        super(Manager.NO_VERTICAL_SCROLL);

        setTitle("Rich List Screen");

        add(new LabelField("BlackBerry Devices", Field.FIELD_HCENTER));
        add(new SeparatorField());

        // Get this screen's main manager (VerticalFieldManager)
        final Manager mainManager = getMainManager();

        // Create a RichList which will be added to the provided manager
        final RichList richList = new RichList(mainManager, true, 4, 0);

        // Set the focus policy for the RichList
        richList.setFocusPolicy(TableController.ROW_FOCUS);

        // Populate the RichList with data from text file
        while (deviceData.hasMoreTokens()) {
            final String modelNumber = deviceData.nextToken().trim();

            final StringBuffer displayName = new StringBuffer(modelNumber);

            final String modelName = deviceData.nextToken().trim();
            if (!modelName.equals(modelNumber)) {
                displayName.append(" (");
                displayName.append(modelName);
                displayName.append(")");
            }

            final String os = deviceData.nextToken().trim();
            final String imageFileName = modelNumber + ".png";
            final Bitmap bitmap = Bitmap.getBitmapResource(imageFileName);
            final String year = deviceData.nextToken().trim();
            final String interfaces = deviceData.nextToken().trim();

            // Add data to the RichList
            final Object[] rowObjects = new Object[5];
            rowObjects[BITMAP] = bitmap;
            rowObjects[DISPLAY_NAME] = displayName.toString();
            rowObjects[OS] = os;
            rowObjects[YEAR] = year;
            rowObjects[INTERFACES] = interfaces;
            richList.add(rowObjects);
        }

        richList.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                // Display selected device in a pop up dialog
                final TableModel tableModel = richList.getModel();
                final Object[] objArray =
                        (Object[]) tableModel.getRow(richList.getFocusRow());
                final Dialog dialog =
                        new Dialog(Dialog.D_OK,
                                (String) objArray[DISPLAY_NAME], 0,
                                (Bitmap) objArray[BITMAP], 0);
                dialog.doModal();
            }
        }));
    }
}
