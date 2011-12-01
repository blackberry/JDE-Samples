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

import net.rim.device.api.io.transport.CoverageStatusListener;
import net.rim.device.api.io.transport.TransportInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * A screen that shows which network transports are available and in coverage
 */
public final class TransportInfoScreen extends MainScreen implements
        CoverageStatusListener {
    private final CheckboxField[] _availabilityCheckboxes;
    private final CheckboxField[] _coverageCheckboxes;
    private final UiApplication _uiApp;

    private static final int NUM_TRANSPORTS = 6;

    /**
     * Creates a new TransportInfoScreen object
     */
    public TransportInfoScreen() {
        setTitle("Transport Info");

        // Regions to display "Availability" status in the left column and
        // "Coverage" status in the right column
        final HorizontalFieldManager hfm =
                new HorizontalFieldManager(Manager.NO_VERTICAL_SCROLL);
        final VerticalFieldManager lVfm =
                new HalfWidthVerticalFieldManager(Manager.NO_HORIZONTAL_SCROLL
                        | Field.FIELD_LEFT);
        final VerticalFieldManager rVfm =
                new HalfWidthVerticalFieldManager(Manager.NO_HORIZONTAL_SCROLL
                        | Field.FIELD_RIGHT);

        // Calculate column with (half of screen width)
        final int halfDisplayWidth = Display.getWidth() / 2;

        // Display "Availability" status for all transports
        _availabilityCheckboxes = new CheckboxField[NUM_TRANSPORTS];
        _coverageCheckboxes = new CheckboxField[NUM_TRANSPORTS];
        lVfm.add(new FixedWidthLabelField("Availability", halfDisplayWidth));
        for (int i = 0; i < NUM_TRANSPORTS; i++) {
            final int currentTransport = i + 1;
            final String transportName =
                    TransportInfo.getTransportTypeName(currentTransport);
            final boolean transportAvailable =
                    TransportInfo.isTransportTypeAvailable(currentTransport);
            _availabilityCheckboxes[i] =
                    new CheckboxField(transportName, transportAvailable,
                            Field.NON_FOCUSABLE);
            _availabilityCheckboxes[i].setEnabled(false);
            lVfm.add(_availabilityCheckboxes[i]);
        }

        // Display "Coverage" status for all transports
        rVfm.add(new FixedWidthLabelField("Coverage", halfDisplayWidth));
        for (int i = 0; i < NUM_TRANSPORTS; i++) {
            final int currentTransport = i + 1;
            final String transportName =
                    TransportInfo.getTransportTypeName(currentTransport);
            final boolean coverageAvailable =
                    TransportInfo.hasSufficientCoverage(currentTransport);
            _coverageCheckboxes[i] =
                    new CheckboxField(transportName, coverageAvailable,
                            Field.NON_FOCUSABLE);
            _coverageCheckboxes[i].setEnabled(false);
            rVfm.add(_coverageCheckboxes[i]);
        }

        hfm.add(lVfm);
        hfm.add(rVfm);

        add(hfm);

        add(new SeparatorField());

        // "Ok" button to close this screen
        final ButtonField okBtn =
                new ButtonField("Ok", Field.FIELD_HCENTER
                        | ButtonField.CONSUME_CLICK);
        okBtn.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(final Field field, final int context) {
                // Close this screen
                close();
            }
        });
        add(okBtn);

        TransportInfo.addListener(this);

        // Cache a reference to the current application instance
        _uiApp = UiApplication.getUiApplication();
    }

    /**
     * @see CoverageStatusListener#coverageStatusChanged(int[])
     */
    public void coverageStatusChanged(final int[] transportsInCoverage) {
        _uiApp.invokeLater(new Runnable() {
            public void run() {
                showTransports();
                Status.show(getTransportsInCoverage(transportsInCoverage));
            }
        });
    }

    /**
     * Converts transports int array to String of transport names
     */
    private static String getTransportsInCoverage(
            final int[] transportsInCoverage) {
        if (transportsInCoverage == null) {
            return "No transports are in coverage";
        }
        final StringBuffer buffer = new StringBuffer("Transports in coverage:");
        final int size = transportsInCoverage.length;
        for (int i = 0; i < size; i++) {
            buffer.append('\n')
                    .append(TransportInfo
                            .getTransportTypeName(transportsInCoverage[i]));
        }
        return buffer.toString();
    }

    /**
     * Sets checkboxes to indicate transport coverage and availability
     */
    private void showTransports() {
        for (int i = 0; i < NUM_TRANSPORTS; i++) {
            final int currentTransport = i + 1;
            _availabilityCheckboxes[i].setChecked(TransportInfo
                    .isTransportTypeAvailable(currentTransport));
            _coverageCheckboxes[i].setChecked(TransportInfo
                    .hasSufficientCoverage(currentTransport));
        }
    }

    /**
     * A custom LabelField that has a fixed width
     */
    private static final class FixedWidthLabelField extends LabelField {
        private final int _width;

        /**
         * Creates a new FixedWidthLabelField object
         * 
         * @param text
         *            The text for this label
         * @param width
         *            The width for this label
         */
        public FixedWidthLabelField(final String text, final int width) {
            super(text);
            _width = width;
        }

        /**
         * @see LabelField#getPreferredWidth()
         */
        public int getPreferredWidth() {
            return _width;
        }

        /**
         * @see LabelField#layout(int, int)
         */
        protected void layout(int width, int height) {
            width = getPreferredWidth();
            height = getPreferredHeight();
            super.layout(width, height);
            super.setExtent(width, height);
        }
    }

    /**
     * A VerticalFieldManager that uses half of the available screen width
     */
    private static final class HalfWidthVerticalFieldManager extends
            VerticalFieldManager {
        /**
         * Creates a new HalfWidthVerticalFieldManager object
         * 
         * @param style
         *            Stye bit for this Manager
         */
        HalfWidthVerticalFieldManager(final long style) {
            super(style);
        }

        /**
         * @see Manager#sublayout(int, int)
         */
        protected void sublayout(final int maxWidth, final int maxHeight) {
            super.sublayout(Math.min(Display.getWidth() / 2, maxWidth),
                    maxHeight);
        }
    }
}
