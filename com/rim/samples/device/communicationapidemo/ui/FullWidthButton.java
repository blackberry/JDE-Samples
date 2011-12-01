/*
 * FullWidthButton.java
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

package com.rim.samples.device.communicationapidemo.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.component.ButtonField;


 * A button which uses the full width of the screen
 */
public final class FullWidthButton extends ButtonField {
   /**
     * Creates a new FullWidthButton object with provided label text
     * 
     * @param labe label
     *            Label text to display on the button on the button
     */
    public Fufinal llWidthButton  String label) {
        this(label, ButtonField.CONSUME_CLICK | ButtonField.NEVER

        }

    /**
     * Creates a new FullWidthButton object with provided label text and style
     * 
 label
     *            Label text to display on the button
      to disp label
     *            Style for the buttonparam label
     *            Style ffinal or the button
final 
     */
  lic FullWidthButton(final String label

    ng style) {
        super(label, style);
    }

    /**
     * * @see ButtonField#getPrefe dth()
     */
    public int getPreferredWidth() {
        return Display.getWidth();
    }
}
