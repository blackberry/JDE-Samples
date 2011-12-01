/**
 * TransportInfoScreen.java
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

package com.rim.samples.device.networkapidemo;

import net.rim.device.api.io.transport.TransportInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * A screen that shows which network transports are available and in coverage
 */
public class TransportInfoScreen extends MainScreen {
    /**
     * Creates a new TransportInfoScreen object
     */
    public TransportInfoScreen() {
        // Sets screen's title to "Network API Demo"
        setTitle("Transport Info");

        // Regions to display "Availability" status in the left column and
        // "Coverage" status in the right column
        final HorizontalFieldManager hfm =
                new HorizontalFieldManager(Manager.NO_VERTICAL_SCROLL);

        final VerticalFieldManager lVfm =
                new VerticalFieldManager(Manager.NO_HORIZONTAL_SCROLL
                        | Field.FIELD_LEFT);
        final VerticalFieldManager rVfm =
                new VerticalFieldManager(Manager.NO_HORIZONTAL_SCROLL
                        | Field.FIELD_RIGHT);

        // Calculate column with (half of screen width)
        final int halfDispayWidth = Display.getWidth() / 2;

        // Display "Availability" status for all transports
        lVfm.add(new FixedWidthLabelField("Availability", halfDispayWidth));
        lVfm.add(new CheckboxField(
                "TCP Cellular",
                TransportInfo
                        .isTransportTypeAvailable(TransportInfo.TRANSPORT_TCP_CELLULAR),
                Field.NON_FOCUSABLE));
        lVfm.add(new CheckboxField("Wap", TransportInfo
                .isTransportTypeAvailable(TransportInfo.TRANSPORT_WAP),
                Field.NON_FOCUSABLE));
        lVfm.add(new CheckboxField("Wap2", TransportInfo
                .isTransportTypeAvailable(TransportInfo.TRANSPORT_WAP2),
                Field.NON_FOCUSABLE));
        lVfm.add(new CheckboxField("Mds", TransportInfo
                .isTransportTypeAvailable(TransportInfo.TRANSPORT_MDS),
                Field.NON_FOCUSABLE));
        lVfm.add(new CheckboxField("BisB", TransportInfo
                .isTransportTypeAvailable(TransportInfo.TRANSPORT_BIS_B),
                Field.NON_FOCUSABLE));
        lVfm.add(new CheckboxField("TCP Wifi", TransportInfo
                .isTransportTypeAvailable(TransportInfo.TRANSPORT_TCP_WIFI),
                Field.NON_FOCUSABLE));

        // Display "Coverage" status for all transports
        rVfm.add(new FixedWidthLabelField("Coverage", halfDispayWidth));
        rVfm.add(new CheckboxField("TCP Cellular", TransportInfo
                .hasSufficientCoverage(TransportInfo.TRANSPORT_TCP_CELLULAR),
                Field.NON_FOCUSABLE));
        rVfm.add(new CheckboxField("Wap", TransportInfo
                .hasSufficientCoverage(TransportInfo.TRANSPORT_WAP),
                Field.NON_FOCUSABLE));
        rVfm.add(new CheckboxField("Wap2", TransportInfo
                .hasSufficientCoverage(TransportInfo.TRANSPORT_WAP2),
                Field.NON_FOCUSABLE));
        rVfm.add(new CheckboxField("Mds", TransportInfo
                .hasSufficientCoverage(TransportInfo.TRANSPORT_MDS),
                Field.NON_FOCUSABLE));
        rVfm.add(new CheckboxField("BisB", TransportInfo
                .hasSufficientCoverage(TransportInfo.TRANSPORT_BIS_B),
                Field.NON_FOCUSABLE));
        rVfm.add(new CheckboxField("TCP Wifi", TransportInfo
                .hasSufficientCoverage(TransportInfo.TRANSPORT_TCP_WIFI),
                Field.NON_FOCUSABLE));

        hfm.add(lVfm);
        hfm.add(rVfm);

        add(hfm);

        // "Ok" button to close this screen
        final ButtonField okBtn =
                new ButtonField("Ok", ButtonField.CONSUME_CLICK
                        | Field.FIELD_HCENTER);
        okBtn.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(final Field field, final int context) {
                // Close this screen
                close();
            }
        });

        add(new SeparatorField());
        add(okBtn);
    }
}
