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
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * The CustomButtonsDemo sample demonstrates custom UI fields.
 */
class CustomButtonsDemo extends UiApplication implements FieldChangeListener {

    /**
     * Entry point.
     */
    public static void main(final String[] args) {
        final CustomButtonsDemo theApp = new CustomButtonsDemo();
        theApp.enterEventDispatcher();
    }

    /**
     * The default constructor. Creates all the RIM UI components and pushes the
     * application's root screen onto the UI stack.
     */
    CustomButtonsDemo() {
        CustomButtonField rectangle;
        CustomButtonField triangle;
        CustomButtonField octagon;
        CustomButtonField fixedWidth1;
        CustomButtonField fixedWidth2;
        CustomButtonField fullscreen;
        CustomButtonField colour;
        PictureBackgroundButtonField picture;

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

        // Rectangular button
        vfm.add(new RichTextField(Field.NON_FOCUSABLE));
        rectangle =
                new CustomButtonField("Rectangle", CustomButtonField.RECTANGLE,
                        Field.FOCUSABLE);
        rectangle.setChangeListener(this);
        vfm.add(rectangle);

        // Triangular button
        vfm.add(new RichTextField(Field.NON_FOCUSABLE));
        triangle =
                new CustomButtonField("Triangle", CustomButtonField.TRIANGLE,
                        Field.FOCUSABLE);
        triangle.setChangeListener(this);
        vfm.add(triangle);

        // Octagonal button
        vfm.add(new RichTextField(Field.NON_FOCUSABLE));
        octagon =
                new CustomButtonField("Octagon", CustomButtonField.OCTAGON,
                        Field.FOCUSABLE);
        octagon.setChangeListener(this);
        vfm.add(octagon);

        // The next two buttons showcase the ability to hold a fixed width
        // for a button regardless of how long the text in the button is.
        vfm.add(new RichTextField(Field.NON_FOCUSABLE));
        fixedWidth1 =
                new CustomButtonField("Fixed Width",
                        CustomButtonField.FIXED_WIDTH, Field.FOCUSABLE);
        fixedWidth1.setChangeListener(this);
        vfm.add(fixedWidth1);

        vfm.add(new RichTextField(Field.NON_FOCUSABLE));
        fixedWidth2 =
                new CustomButtonField("Fixed Width: Long!",
                        CustomButtonField.FIXED_WIDTH, Field.FOCUSABLE);
        fixedWidth2.setChangeListener(this);
        vfm.add(fixedWidth2);

        // Button that will always stretch the entire width of the screen.
        vfm.add(new RichTextField(Field.NON_FOCUSABLE));
        fullscreen =
                new CustomButtonField("Full Screen",
                        CustomButtonField.FULLSCREEN, Field.FOCUSABLE);
        fullscreen.setChangeListener(this);
        vfm.add(fullscreen);

        // Button with a coloured background
        vfm.add(new RichTextField(Field.NON_FOCUSABLE));
        colour =
                new CustomButtonField("Colour",
                        CustomButtonField.COLOUR_BACKGROUND, Field.FOCUSABLE);
        colour.setChangeListener(this);
        vfm.add(colour);

        // Button using a picture as a background. The picture will change
        // when the button recieves focus.
        vfm.add(new RichTextField(Field.NON_FOCUSABLE));
        picture = new PictureBackgroundButtonField("Picture", Field.FOCUSABLE);
        picture.setChangeListener(this);
        vfm.add(picture);

        mainScreen.add(vfm);

        // We've completed construction of our UI objects. Push the MainScreen
        // instance onto the UI stack for rendering.
        pushScreen(mainScreen);
    }

    /**
     * FieldChangeListener implementation. Displays a popup informing the user
     * of what button was clicked.
     * 
     * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {

        String text = "Button";

        if (field instanceof CustomButtonField) {
            text = ((CustomButtonField) field).getText();
        }

        else if (field instanceof PictureBackgroundButtonField) {
            text = ((PictureBackgroundButtonField) field).getText();
        }

        Dialog.inform(text + " was clicked.");

    }
}
