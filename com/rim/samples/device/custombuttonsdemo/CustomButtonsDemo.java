/**
 * CustomButtonsDemo.java
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

package com.rim.samples.device.custombuttonsdemo;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * <p>
 * The CustomButtonsDemo sample demonstrates custom UI fields.
 */
public class CustomButtonsDemo extends UiApplication {

    public static void main(final String[] args) {
        final CustomButtonsDemo theApp = new CustomButtonsDemo();
        theApp.enterEventDispatcher();
    }

    /**
     * <p>
     * The default constructor. Creates all the RIM UI components and pushes the
     * application's root screen onto the UI stack.
     */
    public CustomButtonsDemo() {
        // MainScreen is the basic screen or frame class of the RIM UI.
        final MainScreen mainScreen = new MainScreen();

        // Add a field to the title region of the MainScreen. We use a simple
        // LabelField
        // here. The ELLIPSIS option truncates the label text with "..." if the
        // text was
        // too long for the space available.
        mainScreen.setTitle(new LabelField("Custom Buttons Demo",
                DrawStyle.ELLIPSIS | Field.USE_ALL_WIDTH));

        // Add a vertical field manager containing sample custom button fields.
        final VerticalFieldManager vfm = new VerticalFieldManager();
        vfm.add(new CustomButtonField("rectangle", CustomButtonField.RECTANGLE,
                Field.FOCUSABLE));
        vfm.add(new RichTextField(Field.NON_FOCUSABLE));
        vfm.add(new CustomButtonField("triangle", CustomButtonField.TRIANGLE,
                Field.FOCUSABLE));
        vfm.add(new RichTextField(Field.NON_FOCUSABLE));
        vfm.add(new CustomButtonField("octagon", CustomButtonField.OCTAGON,
                Field.FOCUSABLE));
        vfm.add(new RichTextField(Field.NON_FOCUSABLE));
        vfm.add(new CustomButtonField("larger rectangle",
                CustomButtonField.RECTANGLE, Field.FOCUSABLE));
        vfm.add(new RichTextField(Field.NON_FOCUSABLE));
        vfm.add(new CustomButtonField("larger triangle",
                CustomButtonField.TRIANGLE, Field.FOCUSABLE
                        | DrawStyle.ELLIPSIS));
        vfm.add(new RichTextField(Field.NON_FOCUSABLE));
        vfm.add(new CustomButtonField("larger octagon",
                CustomButtonField.OCTAGON, Field.FOCUSABLE | DrawStyle.ELLIPSIS));

        mainScreen.add(vfm);

        // We've completed construction of our UI objects. Push the MainScreen
        // instance
        // onto the UI stack for rendering.
        pushScreen(mainScreen);
    }
}
