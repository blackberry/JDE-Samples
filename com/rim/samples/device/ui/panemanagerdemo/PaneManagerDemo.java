/*
 * PaneManagerDemo.java
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

package com.rim.samples.device.ui.panemanagerdemo;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.pane.HorizontalScrollableController;
import net.rim.device.api.ui.component.pane.HorizontalScrollableTitleView;
import net.rim.device.api.ui.component.pane.HorizontalTabController;
import net.rim.device.api.ui.component.pane.HorizontalTabTitleView;
import net.rim.device.api.ui.component.pane.Pane;
import net.rim.device.api.ui.component.pane.PaneManagerController;
import net.rim.device.api.ui.component.pane.PaneManagerModel;
import net.rim.device.api.ui.component.pane.PaneManagerView;
import net.rim.device.api.ui.component.pane.PaneView;
import net.rim.device.api.ui.component.pane.TitleView;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.util.StringProvider;

/**
 * The PaneManager API allows you to add UI components to your application which
 * are displayed separately in multiple views, selectable by tabs on the top of
 * the screen. There are two display styles available, scrolling headings and
 * distinct tabs.
 */
public class PaneManagerDemo extends UiApplication {
    /**
     * Entry point for the application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        final UiApplication app = new PaneManagerDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new PaneManagerDemo object
     */
    public PaneManagerDemo() {
        pushScreen(new PaneManagerDemoScreen());
    }

    /**
     * Main screen for the application. Displays three panes switchable via
     * horizontal scroll field or tabs, depending on user selection.
     */
    private final static class PaneManagerDemoScreen extends MainScreen {
        /**
         * Creates a new PaneManagerDemoScreen object
         */
        public PaneManagerDemoScreen() {
            super(Field.FOCUSABLE);

            // Instantiate the model for the pane manager and enable looping
            final PaneManagerModel model = new PaneManagerModel();
            model.enableLooping(true);

            // Create a pane
            VerticalFieldManager vfm = new VerticalFieldManager();
            vfm.add(new LabelField("Data 1"));
            final XYEdges edgesOne = new XYEdges(1, 1, 1, 1);
            vfm.setBorder(BorderFactory.createRoundedBorder(edgesOne));
            final Pane pane =
                    new Pane(new LabelField("Pane 1", Field.FOCUSABLE
                            | Field.FIELD_HCENTER), vfm);

            // Add the pane to the model
            model.addPane(pane);

            // Create a second pane
            vfm = new VerticalFieldManager();
            for (int i = 0; i < 30; i++) {
                vfm.add(new LabelField("Data " + i, Field.FOCUSABLE));
            }
            final LabelField iconTextLabelField =
                    new LabelField("Pane 2", Field.FOCUSABLE
                            | Field.FIELD_HCENTER);
            model.addPane(new Pane(iconTextLabelField, vfm));

            // Create a third pane
            vfm = new VerticalFieldManager();
            final ButtonField button =
                    new ButtonField("Button", ButtonField.CONSUME_CLICK
                            | ButtonField.NEVER_DIRTY);
            button.setChangeListener(new FieldChangeListener() {
                public void fieldChanged(final Field field, final int context) {
                    Dialog.inform("Button activated.");
                }
            });
            vfm.add(button);
            model.addPane(new Pane(new LabelField("Pane 3", Field.FOCUSABLE
                    | Field.FIELD_HCENTER), vfm));

            // Choose which pane the model is displaying
            model.setCurrentlySelectedIndex(1);

            // Create the header and initialize the model and visual properties
            TitleView header;
            PaneManagerController controller;
            header = new HorizontalScrollableTitleView(Field.FOCUSABLE);
            ((HorizontalScrollableTitleView) header).enableArrows(true);
            controller = new HorizontalScrollableController();
            header.setModel(model);
            final XYEdges edgesFour = new XYEdges(4, 4, 4, 4);
            header.setBorder(BorderFactory.createRoundedBorder(edgesFour));

            // Set arrow images
            final Bitmap leftArrow = Bitmap.getBitmapResource("leftArrow.png");
            final Bitmap rightArrow =
                    Bitmap.getBitmapResource("rightArrow.png");
            if (leftArrow != null) {
                header.setLeftArrow(leftArrow);
            }
            if (rightArrow != null) {
                header.setRightArrow(rightArrow);
            }

            // Create the PaneView object, which will display the panes and is
            // controlled by the model.
            final PaneView paneView = new PaneView(Field.FOCUSABLE);
            paneView.setBorder(BorderFactory.createSimpleBorder(edgesOne));
            paneView.setModel(model);

            // Initialize the PaneManagerView
            final PaneManagerView paneManagerView =
                    new PaneManagerView(Field.FOCUSABLE, header, paneView);
            paneManagerView.setModel(model);
            paneManagerView.setBorder(BorderFactory
                    .createRoundedBorder(edgesFour));
            model.setView(paneManagerView);

            // Initialize the Controller
            controller.setModel(model);
            controller.setView(paneManagerView);
            model.setController(controller);
            paneManagerView.setController(controller);

            add(paneManagerView);

            final MenuItem myItem =
                    new MenuItem(new StringProvider("Switch View"), 0x230010, 0);
            myItem.setCommand(new Command(new SwitchViewCommandHandler(
                    paneManagerView, leftArrow, rightArrow, edgesFour)));
            addMenuItem(myItem);
        }
    }

    /**
     * A CommandHandler that toggles the view mode
     */
    private final static class SwitchViewCommandHandler extends CommandHandler {
        private final PaneManagerView _paneManagerView;
        private final Bitmap _leftArrow;
        private final Bitmap _rightArrow;
        private final XYEdges _edgesFour;

        /**
         * Create a SwitchViewCommandHandler object
         * 
         * @param paneManagerView
         *            The PaneManagerView
         * @param leftArrow
         *            The left arrow for the scroll view
         * @param rightArrow
         *            The right arrow for the scroll view
         * @param edgesFour
         *            XYEdges object representing the thickness of the border in
         *            pixels
         */
        public SwitchViewCommandHandler(final PaneManagerView paneManagerView,
                final Bitmap leftArrow, final Bitmap rightArrow,
                final XYEdges edgesFour) {
            _paneManagerView = paneManagerView;
            _leftArrow = leftArrow;
            _rightArrow = rightArrow;
            _edgesFour = edgesFour;
        }

        /**
         * @see CommandHandler#execute(ReadOnlyCommandMetadata, Object)
         */
        public void execute(final ReadOnlyCommandMetadata metadata,
                final Object context) {
            // Check to be sure everything is instantiated
            if (_paneManagerView == null) {
                return;
            }
            final PaneManagerModel model = _paneManagerView.getModel();
            TitleView header = _paneManagerView.getTitle();
            if (header == null || model == null) {
                return;
            }

            PaneManagerController controller = null;

            // Switch the header style based on current style
            if (header instanceof HorizontalScrollableTitleView) {
                header = new HorizontalTabTitleView(Field.FOCUSABLE);
                ((HorizontalTabTitleView) header)
                        .setNumberOfDisplayedTabs(model.numberOfPanes());
                controller = new HorizontalTabController();
            } else if (header instanceof HorizontalTabTitleView) {
                header = new HorizontalScrollableTitleView(Field.FOCUSABLE);
                ((HorizontalScrollableTitleView) header).enableArrows(true);
                controller = new HorizontalScrollableController();

                if (_leftArrow != null) {
                    header.setLeftArrow(_leftArrow);
                }
                if (_rightArrow != null) {
                    header.setRightArrow(_rightArrow);
                }
            }

            // Set up references between objects
            header.setModel(model);
            header.setBorder(BorderFactory.createRoundedBorder(_edgesFour));
            if (controller != null) {
                _paneManagerView.setController(controller);
                model.setController(controller);
                controller.setView(_paneManagerView);
                controller.setModel(model);
            }

            _paneManagerView.setTitle(header);
        }
    }
}
