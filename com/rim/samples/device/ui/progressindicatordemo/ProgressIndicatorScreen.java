/*
 * ProgressIndicatorScreen.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.ui.progressindicatordemo;

import net.rim.device.api.ui.Adjustment;
import net.rim.device.api.ui.AdjustmentListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.progressindicator.ProgressIndicatorController;
import net.rim.device.api.ui.component.progressindicator.ProgressIndicatorListener;
import net.rim.device.api.ui.component.progressindicator.ProgressIndicatorModel;
import net.rim.device.api.ui.component.progressindicator.ProgressIndicatorView;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A screen displaying a progress indicator visually representing the processing
 * of some imaginary data.
 */
public class ProgressIndicatorScreen extends MainScreen implements
        FieldChangeListener {
    private final ProgressIndicatorModel _model;
    private ProgressThread _progressThread;
    private final ButtonField _processButton;
    private final ButtonField _cancelButton;
    private final ButtonField _resumeButton;

    /**
     * Creates a new ProgressIndicatorScreen object
     */
    public ProgressIndicatorScreen() {
        setTitle("Progress Indicator Screen");

        // Initialize progress indicator
        final ProgressIndicatorView view = new ProgressIndicatorView(0);
        _model = new ProgressIndicatorModel(0, 100, 0);
        final ProgressIndicatorController controller =
                new ProgressIndicatorController();
        _model.setController(controller);
        _model.addListener(new DemoProgressIndicatorListener());
        view.setModel(_model);
        view.setController(controller);
        controller.setModel(_model);
        controller.setView(view);
        view.setLabel("Progress");
        view.createProgressBar(Field.FIELD_HCENTER);

        // Initialize buttons
        _processButton =
                new ButtonField("Process data", ButtonField.NEVER_DIRTY
                        | ButtonField.CONSUME_CLICK);
        _processButton.setChangeListener(this);
        _cancelButton =
                new ButtonField("Cancel", ButtonField.NEVER_DIRTY
                        | ButtonField.CONSUME_CLICK);
        _cancelButton.setChangeListener(this);
        _resumeButton =
                new ButtonField("Resume", ButtonField.NEVER_DIRTY
                        | ButtonField.CONSUME_CLICK);
        _resumeButton.setChangeListener(this);

        // Add buttons to manager
        final HorizontalFieldManager hfm =
                new HorizontalFieldManager(Field.FIELD_HCENTER);
        hfm.add(_processButton);
        hfm.add(_cancelButton);
        hfm.add(_resumeButton);

        add(hfm);
        add(new SeparatorField());
        add(view);
    }

    /**
     * @see FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field == _processButton) {
            if (_progressThread != null) {
                // Kill the currently running progress thread
                _progressThread.stopThread();
            }

            // Start a new progress thread
            _progressThread = new ProgressThread();
            _progressThread.start();
        } else if (field == _cancelButton) {
            _model.cancel();
        } else if (field == _resumeButton) {
            _model.resume();
        }
    }

    /**
     * A thread which simulates the processing of some imaginary data
     */
    class ProgressThread extends Thread {
        private boolean _paused;
        private boolean _stop;

        /**
         * @see Runnable#run()
         */
        public void run() {
            // Run an arbitrary number of dummy operations to simulate the
            // processing of a collection of data, e.g. 100 database records.
            for (int i = 0; i <= 100; ++i) {
                synchronized (this) {
                    if (_stop) {
                        break;
                    }
                    if (_paused) {
                        try {
                            wait();
                        } catch (final InterruptedException ie) {
                        }
                    }
                }
                ProgressIndicatorScreen.this._model.setValue(i);
                try {
                    // Simulate 250 ms of work
                    sleep(250);
                } catch (final InterruptedException ie) {
                }
            }
        }

        /**
         * Sets the paused flag
         * 
         * @param paused
         *            True if thread should be paused, false if thread should be
         *            resumed
         */
        public synchronized void setPaused(final boolean paused) {
            _paused = paused;
            this.notify();
        }

        /**
         * Sets the stop flag to true and notifies the thread if it is paused
         */
        public synchronized void stopThread() {
            _stop = true;
            if (_paused) {
                // If the thread is in a paused state, wake it up
                this.notify();
            }
        }
    }

    /**
     * Listener for processing notifications of changes in the data model
     */
    private final class DemoProgressIndicatorListener implements
            ProgressIndicatorListener {
        /**
         * @see ProgressIndicatorListener#cancelled()
         */
        public void cancelled() {
            _progressThread.setPaused(true);

        }

        /**
         * @see ProgressIndicatorListener#reset()
         */
        public void reset() {
            // Not implemented
        }

        /**
         * @see ProgressIndicatorListener#resumed()
         */
        public void resumed() {
            _progressThread.setPaused(false);
        }

        /**
         * @see ProgressIndicatorListener#setNonProgrammaticValue(int)
         */
        public void setNonProgrammaticValue(final int value) {
            // Not implemented
        }

        /**
         * @see AdjustmentListener#configurationChanged(Adjustment)
         */
        public void configurationChanged(final Adjustment source) {
            // Not implemented
        }

        /**
         * @see AdjustmentListener#valueChanged(Adjustment)
         */
        public void valueChanged(final Adjustment source) {
            // Not implemented
        }
    }
}
