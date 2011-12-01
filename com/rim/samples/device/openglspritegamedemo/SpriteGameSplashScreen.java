/*
 * SpriteGameSplashScreen.java
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

package com.rim.samples.device.openglspritegamedemo;

import net.rim.device.api.opengles.GLField;
import net.rim.device.api.opengles.GLUtils;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.ui.menu.ContextMenuProvider;

public class SpriteGameSplashScreen extends FullScreen implements
        FieldChangeListener {
    private ObjectChoiceField _glVersionField = null;
    private ButtonField _startButton = null;
    private ButtonField _controlsButton = null;
    private LabelField _labelField = null;
    private BitmapField _bitmapField = null;

    public SpriteGameSplashScreen() {
        super(Screen.DEFAULT_CLOSE);
        displaySplashScreen();
    }

    /**
     * Displays the applications splash screen. Explains the game's controls and
     * objective. Allows the user to choose between using OpenGL v1.1 or v2.0.
     */
    private void displaySplashScreen() {
        final VerticalFieldManager vfm =
                new VerticalFieldManager(Manager.VERTICAL_SCROLL);

        // Initialize and add the logo to the splash screen
        final Bitmap bm = Bitmap.getBitmapResource("RandOmazeLogo.png");
        _bitmapField = new BitmapField(bm, Field.FIELD_HCENTER);
        _bitmapField.setMargin(0, 0, 0, 0);
        vfm.add(_bitmapField);

        // Initialize and add the instructions to the splash screen
        final String instructions =
                "The goal of the game is to move the character to the "
                        + "special end block without being hit by any of the obstacles.\n";
        _labelField = new LabelField(instructions);
        _labelField.setBorder(BorderFactory.createRoundedBorder(new XYEdges(10,
                10, 10, 10), Color.RED, Border.STYLE_SOLID));
        _labelField.setMargin(0, 10, 5, 10);
        vfm.add(_labelField);

        // Check if the device supports OpenGL v2.0
        if (GLUtils.isSupported(GLField.VERSION_2_0)) {
            // Set up the version array for the different GL
            // versions that the device supports.
            final String[] versions = new String[2];
            versions[0] = "OpenGL ES 1.1";

            versions[1] = "OpenGL ES 2.0";

            // Initialize and add the ObjectChoiceField that lets the users
            // choose what version of OpenGL they want to use.
            _glVersionField =
                    new ObjectChoiceField("", new String[] { "OpenGL ES 1.1",
                            "OpenGL ES 2.0" }, 1, Field.FIELD_HCENTER);
            vfm.add(_glVersionField);
        }

        // Initialize "Controls" button
        _controlsButton = new ButtonField("Controls", Field.FIELD_HCENTER);
        _controlsButton.setChangeListener(this);

        // Initialize "Start" button
        _startButton = new ButtonField("Start", Field.FIELD_HCENTER);
        _startButton.setChangeListener(this);

        // Add buttons
        final HorizontalFieldManager hfm =
                new HorizontalFieldManager(Field.FIELD_HCENTER);
        hfm.add(_controlsButton);
        hfm.add(_startButton);
        vfm.add(hfm);

        add(vfm);

        // Disable screen rotation and context menu
        Ui.getUiEngineInstance().setAcceptableDirections(
                Display.DIRECTION_NORTH);
        setContextMenuProvider(new DisableContextMenuProvider());
    }

    // Handle start button click
    public void fieldChanged(final Field field, final int context) {
        if (field == _startButton) {
            int version = SpriteGame.OPENGLES11;

            if (_glVersionField != null) {
                version = _glVersionField.getSelectedIndex();
            }

            final SpriteGameScreen gameScreen = new SpriteGameScreen(version);
            UiApplication.getUiApplication().pushScreen(gameScreen);

        } else if (field == _controlsButton) {
            net.rim.device.api.system.Application.getApplication()
                    .invokeAndWait(new Runnable() {
                        public void run() {
                            Dialog.inform("Move: Press Q and W, tap the left and right sides of the screen or use the trackpad.\n\n"
                                    + "Jump: Press the spacebar, tap the center of the screen, tap the screen with both fingers, or click the trackpad.\n\n"
                                    + "Shrink and grow: Press S, or swipe up or down the center of the screen.");
                        }
                    });
        }
    }

    // Context menu provider that just disables the context menu.
    private static final class DisableContextMenuProvider implements
            ContextMenuProvider {
        public boolean showContextMenu(final Screen screen) {
            return true;
        }
    }
}
