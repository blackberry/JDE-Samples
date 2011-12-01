/*
 * ActivityIndicatorScreen.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.ui.progressindicatordemo;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.progressindicator.ActivityIndicatorView;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.util.StringProvider;

/**
 * A screen displaying a number of activity indicators
 */
public class ActivityIndicatorScreen extends MainScreen {
    private final ActivityIndicatorView[] _views;

    /**
     * Creates a new ActivityIndicatorScreen object
     */
    public ActivityIndicatorScreen() {
        setTitle("Activity Indicator Screen");

        _views = new ActivityIndicatorView[7];

        Bitmap bitmap; // Reuse this reference

        // Add an ActivityIndicatorView featuring a rounded red border
        bitmap = Bitmap.getBitmapResource("spinner.png");
        final XYEdges edges = new XYEdges(2, 2, 2, 2);
        final Border border =
                BorderFactory.createRoundedBorder(edges, Color.CRIMSON,
                        Border.STYLE_SOLID);
        _views[0] =
                ActivityIndicatorFactory.createActivityIndicator(0, bitmap, 5,
                        0, "with border", Field.FIELD_HCENTER);
        _views[0].setBorder(border);
        add(_views[0]);
        add(new SeparatorField());

        // Add an ActivityIndicatorView with label and animation centered in a
        // horizontal layout
        bitmap = Bitmap.getBitmapResource("spinner.png");
        _views[1] =
                ActivityIndicatorFactory.createActivityIndicator(
                        new HorizontalFieldManager(), Field.FIELD_HCENTER,
                        bitmap, 5, 0, "horizontal centered layout",
                        Field.FIELD_HCENTER);
        add(_views[1]);
        add(new SeparatorField());

        // Add a centered ActivityIndicatorView between two focusable fields
        add(new LabelField("focusable field", Field.FOCUSABLE));
        bitmap = Bitmap.getBitmapResource("spinner2.png");
        _views[2] =
                ActivityIndicatorFactory.createActivityIndicator(
                        Field.FIELD_HCENTER, bitmap, 6, Field.FIELD_HCENTER,
                        "centered between focusable fields",
                        Field.FIELD_HCENTER);
        add(_views[2]);
        add(new LabelField("focusable field", Field.FOCUSABLE));
        add(new SeparatorField());

        // Add a right justified ActivityIndicatorView
        bitmap = Bitmap.getBitmapResource("spinner.png");
        _views[3] =
                ActivityIndicatorFactory.createActivityIndicator(
                        Field.USE_ALL_WIDTH, bitmap, 5, Field.FIELD_RIGHT,
                        "right justified layout", Field.FIELD_VCENTER);
        add(_views[3]);
        add(new SeparatorField());

        Background background; // Reuse this reference

        // Add an ActivityIndicatorView featuring a solid black background
        bitmap = Bitmap.getBitmapResource("orchid.png");
        background = BackgroundFactory.createSolidBackground(Color.BLACK);
        _views[4] =
                ActivityIndicatorFactory.createActivityIndicator(0, bitmap, 6,
                        0);
        _views[4].setBackground(background);
        add(_views[4]);
        add(new LabelField("solid background"));
        add(new SeparatorField());

        // Add an ActivityIndicatorView to another ActivityIndicatorView
        bitmap = Bitmap.getBitmapResource("progress.png");
        background = BackgroundFactory.createSolidBackground(Color.DARKGRAY);
        _views[5] =
                ActivityIndicatorFactory.createActivityIndicator(0, bitmap, 12,
                        0, "view added to another", 0);
        _views[5].setBackground(background);
        bitmap = Bitmap.getBitmapResource("spinner.png");
        _views[6] =
                ActivityIndicatorFactory.createActivityIndicator(0, bitmap, 5,
                        0);
        _views[5].add(_views[6]);
        add(_views[5]);

        final MenuItem cancelItem =
                new MenuItem(new StringProvider("Cancel spinners "), 0x230020,
                        0);
        cancelItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                for (int i = _views.length - 1; i >= 0; --i) {
                    _views[i].getModel().cancel();
                }
            }
        }));

        final MenuItem resetItem =
                new MenuItem(new StringProvider("Reset spinners "), 0x230030, 0);
        resetItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                for (int i = _views.length - 1; i >= 0; --i) {
                    _views[i].getModel().reset();
                }
            }
        }));

        final MenuItem resumeItem =
                new MenuItem(new StringProvider("Resume spinners "), 0x230040,
                        0);
        resumeItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                for (int i = _views.length - 1; i >= 0; --i) {
                    _views[i].getModel().resume();
                }
            }
        }));

        final MenuItem delayedStop =
                new MenuItem(new StringProvider("Delayed start"), 0x230050, 0);
        delayedStop.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                for (int i = _views.length - 1; i >= 0; --i) {
                    final DelayedStart ds = new DelayedStart(_views[i]);
                    ds.start();
                }
            }
        }));

        final MenuItem delayedStart =
                new MenuItem(new StringProvider("Delayed stop"), 0x230060, 0);
        delayedStart.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                for (int i = _views.length - 1; i >= 0; --i) {
                    final DelayedStop ds = new DelayedStop(_views[i]);
                    ds.start();
                }
            }
        }));

        final MenuItem showPopupItem =
                new MenuItem(new StringProvider("Show Popup"), 0x230010, 0);
        showPopupItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                Bitmap bitmap = Bitmap.getBitmapResource("orchid2.png");
                final ActivityIndicatorView view =
                        ActivityIndicatorFactory.createActivityIndicator(0,
                                bitmap, 6, Field.FIELD_HCENTER);
                bitmap = Bitmap.getBitmapResource("progress2.png");
                final ActivityIndicatorView view2 =
                        ActivityIndicatorFactory.createActivityIndicator(
                                new HorizontalFieldManager(),
                                Field.FIELD_HCENTER, bitmap, 6, 0,
                                "horizontal", Field.FIELD_HCENTER);

                final ActivityPopupScreen activityPopupScreen =
                        new ActivityPopupScreen(view);
                activityPopupScreen.add(view2);

                UiApplication.getUiApplication()
                        .pushScreen(activityPopupScreen);
            }
        }));

        // Add menu items
        addMenuItem(showPopupItem);
        addMenuItem(cancelItem);
        addMenuItem(resetItem);
        addMenuItem(resumeItem);
        addMenuItem(delayedStart);
        addMenuItem(delayedStop);
    }

    /**
     * Thread class which stops an ActivityIndicatorView animation after an
     * arbitrary delay.
     */
    private static class DelayedStop extends Thread {
        private final ActivityIndicatorView _view;

        /**
         * Creates a new DelayedStop object
         * 
         * @param view
         *            The ActivityIndicatorView to stop
         */
        public DelayedStop(final ActivityIndicatorView view) {
            _view = view;
        }

        /**
         * @see Runnable#run()
         */
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (final InterruptedException e) {
            }
            _view.getModel().cancel();
        }
    }

    /**
     * Thread class which starts an ActivityIndicatorView animation after an
     * arbitrary delay.
     */
    private static class DelayedStart extends Thread {
        private final ActivityIndicatorView _view;

        /**
         * Creates a new DelayedStart object
         * 
         * @param view
         *            The ActivityIndicatorView to start
         */
        public DelayedStart(final ActivityIndicatorView view) {
            _view = view;
        }

        /**
         * @see Runnable#run()
         */
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (final InterruptedException e) {
            }
            _view.getModel().reset();
        }
    }

    /**
     * A popup screen class demonstrating the displaying of activity indicators
     * on a modal screen.
     */
    private static class ActivityPopupScreen extends PopupScreen implements
            FieldChangeListener {
        private final ActivityIndicatorView _view;
        private final ButtonField _buttonFieldStopStart;
        private final ButtonField _buttonFieldClose;
        private boolean _topSpinning;

        /**
         * Creates a new ProgressPopupScreen object
         * 
         * @param view
         *            The ActivityIndicatorView to display on this screen
         */
        public ActivityPopupScreen(final ActivityIndicatorView view) {
            super(view);
            setChangeListener(this);
            _view = view;
            _topSpinning = true;

            _buttonFieldStopStart =
                    new ButtonField("Stop Top Spinner", Field.FIELD_HCENTER
                            | ButtonField.CONSUME_CLICK);
            _buttonFieldStopStart.setChangeListener(this);
            _view.add(_buttonFieldStopStart);

            _buttonFieldClose =
                    new ButtonField("Close Popup", Field.FIELD_HCENTER
                            | ButtonField.CONSUME_CLICK);
            _buttonFieldClose.setChangeListener(this);
            _view.add(_buttonFieldClose);
        }

        /**
         * @see FieldChangeListener#fieldChanged(Field, int)
         */
        public void fieldChanged(final Field field, final int context) {
            if (field == _buttonFieldStopStart) {
                if (_topSpinning) {
                    _view.getModel().cancel();
                    _buttonFieldStopStart.setLabel("Restart Top Spinner");
                    _topSpinning = false;
                } else {
                    _view.getModel().reset();
                    _buttonFieldStopStart.setLabel("Stop Top Spinner");
                    _topSpinning = true;
                }
            } else if (field == _buttonFieldClose) {
                close();
            }
        }

        /**
         * @see Screen#keyChar(char, int, int)
         */
        protected boolean keyChar(final char ch, final int status,
                final int time) {
            if (ch == Characters.ESCAPE) {
                close();
                return true;
            }
            return super.keyChar(ch, status, time);
        }
    }
}
