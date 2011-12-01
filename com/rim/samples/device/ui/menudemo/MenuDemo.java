/*
 * MenuDemo.java
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

package com.rim.samples.device.ui.menudemo;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.ui.image.Image;
import net.rim.device.api.ui.image.ImageFactory;

/**
 * This sample application demonstrates the ability to customize the background,
 * border and font of a menu, and the ability to add an image to a menu item.
 */
public class MenuDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final MenuDemo app = new MenuDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new MenuDemo object
     */
    public MenuDemo() {
        pushScreen(new MenuDemoScreen());
    }

    /**
     * MainScreen class for the MenuDemo application
     */
    static class MenuDemoScreen extends MainScreen implements
            FieldChangeListener {
        private final Background _menuBackground;
        private final Border _menuBorder;
        private Font _menuFont;
        private final FontFamily[] _fontFamilies;
        private final RadioButtonField _radioButtonImage;
        private final RadioButtonField _radioButtonDecor;
        private final ImageMenuItem _imageMenuItem;
        private final ObjectChoiceField _fontChoiceField;

        // Creates a new MenuDemoScreen object
        MenuDemoScreen() {
            setTitle("Menu Demo");
            add(new LabelField(
                    "Press the menu key to view the customized menu."));
            add(new SeparatorField());

            // Initialize font family and menu font
            _fontFamilies = FontFamily.getFontFamilies();
            _menuFont = _fontFamilies[0].getFont(FontFamily.SCALABLE_FONT, 20);

            // Initialize radio buttons for menu customization and add to screen
            final RadioButtonGroup radioButtonGroup = new RadioButtonGroup();
            _radioButtonImage =
                    new RadioButtonField("Menu with image", radioButtonGroup,
                            true);
            _radioButtonDecor =
                    new RadioButtonField(
                            "Menu with custom border, background and font",
                            radioButtonGroup, false);
            _radioButtonDecor.setChangeListener(this);
            add(_radioButtonImage);
            add(_radioButtonDecor);

            add(new SeparatorField());

            // Initialize choice field for font selection and add to screen
            _fontChoiceField =
                    new ObjectChoiceField("Select a font:", _fontFamilies, 0,
                            Field.FIELD_HCENTER);
            _fontChoiceField.setEditable(false);
            _fontChoiceField.setChangeListener(this);
            add(_fontChoiceField);

            // Create an ImageMenuItem
            _imageMenuItem = new ImageMenuItem();

            // Initialize menu background
            _menuBackground =
                    BackgroundFactory.createLinearGradientBackground(
                            Color.BLUE, Color.BLUE, Color.LIGHTBLUE,
                            Color.LIGHTBLUE);

            // Initialize menu border
            final XYEdges thickPadding = new XYEdges(10, 10, 10, 10);
            final XYEdges colors =
                    new XYEdges(Color.DARKBLUE, Color.DARKBLUE, Color.DARKBLUE,
                            Color.DARKBLUE);
            _menuBorder =
                    BorderFactory.createBevelBorder(thickPadding, colors,
                            colors);
        }

        /**
         * @see FieldChangeListener#fieldChanged(Field, int)
         */
        public void fieldChanged(final Field field, final int context) {
            if (field instanceof ObjectChoiceField) {
                // Get the font selected in the ObjectChoiceField and set the
                // menu font to match what is selected.
                final int selectedIndex =
                        ((ObjectChoiceField) field).getSelectedIndex();

                _menuFont =
                        _fontFamilies[selectedIndex].getFont(
                                FontFamily.SCALABLE_FONT, 20);
            }
            if (field == _radioButtonDecor) {
                if (_radioButtonDecor.isSelected()) {
                    _fontChoiceField.setEditable(true);
                } else {
                    _fontChoiceField.setEditable(false);
                }
            }
        }

        /**
         * @see MainScreen#makeMenu(Menu, context)
         */
        protected void makeMenu(final Menu menu, final int context) {
            if (_radioButtonImage.isSelected()) {
                // Add the image menu item to the menu
                menu.add(_imageMenuItem);
            } else if (_radioButtonDecor.isSelected()) {
                // Set the background, border, and font for the menu
                menu.setBackground(_menuBackground);
                menu.setBorder(_menuBorder);
                menu.setFont(_menuFont);
            }

            super.makeMenu(menu, context);
        }

        /**
         * @see MainScreen#onSavePrompt()
         */
        public boolean onSavePrompt() {
            // Suppress the save dialog
            return true;
        }

        /**
         * Concrete implementation of abstract class MenuItem
         */
        class ImageMenuItem extends MenuItem {
            /**
             * Creates a new MenuDemoMenuItem object
             */
            ImageMenuItem() {
                super("Image menu item", 0, 0);

                // Create Image object from project resource
                final Bitmap bitmap = Bitmap.getBitmapResource("img.png");
                final Image image = ImageFactory.createImage(bitmap);

                // Set image as this menu item's icon
                setIcon(image);
            }

            public void run() {
                // Not implemented
            }
        }
    }
}
