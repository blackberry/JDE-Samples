/**
 * AccelerometerDemo.java
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

package com.rim.samples.device.accelerometerdemo;

import java.util.Random;

import net.rim.device.api.system.AccelerometerSensor;
import net.rim.device.api.system.AccelerometerSensor.Channel;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This sample demonstrates the Accelerometer API. The DrawThread opens the
 * accelerometer channel and periodically queries for the current data reading.
 * The data is then used to apply corresponding force to a ball drawn on the
 * screen.
 */
public final class AccelerometerDemo extends UiApplication {
    private AccelerometerDemoScreen _screen;
    private DrawThread _thread;

    private Bitmap _ball;
    private int _ballWidth;
    private int _ballHeight;

    private int _x;
    private int _y;
    private float _xSpeed;
    private float _ySpeed;

    private Random _r;

    private boolean _simulated;
    private Channel _accChannel;
    private final short[] _xyz = new short[3];

    private static final float G_NORM =
            9.8066f / AccelerometerSensor.G_FORCE_VALUE;
    private static final float TABLE_FRICTION = 0.98f;
    private static final float BOUNCE_SLOWDOWN = 0.6f;

    private static final int DEFAULT_ORIENTATION = Display.DIRECTION_NORTH;

    private int _tick = 0;

    /**
     * Entry point for application.
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final AccelerometerDemo app = new AccelerometerDemo();
        app.enterEventDispatcher();
    }

    // Constructor
    public AccelerometerDemo() {
        if (AccelerometerSensor.isSupported()) {
            // Initialize UI
            _screen = new AccelerometerDemoScreen();
            _screen.addMenuItem(_startMenuItem);
            _screen.addMenuItem(_stopMenuItem);
            pushScreen(_screen);

            _ball = Bitmap.getBitmapResource("img/ball.png");
            _r = new Random();

            if (_ball != null) {
                _ballWidth = _ball.getWidth();
                _ballHeight = _ball.getHeight();
            }

            // Prevent UI from rotating our screen.
            Ui.getUiEngineInstance().setAcceptableDirections(
                    DEFAULT_ORIENTATION);
        } else {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert("This device does not support accelerometer.");
                    System.exit(0);
                }
            });
        }
    }

    /**
     * Calculates ball position.
     * 
     * @param xAcc
     *            x axis acceleration
     * @param yAcc
     *            y axis acceleration
     */
    private void applyForce(final int xAcc, final int yAcc) {
        // Calculate new speed.
        _xSpeed += xAcc * G_NORM;
        _ySpeed += yAcc * G_NORM;

        // Apply table friction.
        _xSpeed *= TABLE_FRICTION;
        _ySpeed *= TABLE_FRICTION;

        // Move the ball.
        _x += _xSpeed;
        _y += _ySpeed;

        if (_x < 0) {
            _x = 0;
            _xSpeed = -(_xSpeed * BOUNCE_SLOWDOWN);
        } else {
            final int screenWidth = _screen.getWidth();
            if (_x > screenWidth - _ballWidth) {
                _x = screenWidth - _ballHeight;
                _xSpeed = -(_xSpeed * BOUNCE_SLOWDOWN);
            }
        }

        if (_y < 0) {
            _y = 0;
            _ySpeed = -(_ySpeed * BOUNCE_SLOWDOWN);
        } else {
            final int screenHeight = _screen.getHeight();
            if (_y > screenHeight - _ballHeight) {
                _y = screenHeight - _ballHeight;
                _ySpeed = -(_ySpeed * BOUNCE_SLOWDOWN);
            }
        }
    }

    /**
     * A thread class to handle screen updates.
     */
    private class DrawThread extends Thread {
        private boolean _running;

        public void run() {
            _running = true;

            // Start querying the accelerometer sensor.
            openAccelerometerConnection();

            while (_running) {
                _tick++;

                // Get current acceleration.
                readAcceleration();

                // Apply force to the ball.
                applyForce(-_xyz[0], _xyz[1]);

                try {
                    synchronized (this) {
                        wait(50);
                    }
                } catch (final InterruptedException e) {
                    UiApplication.getUiApplication().invokeLater(
                            new Runnable() {
                                public void run() {
                                    Dialog.alert("wait(long) threw InterruptedException");
                                }
                            });
                }

                if (!_running) {
                    break;
                }

                _screen.invalidate();
            }

            // Stop querying the sensor to save battery charge.
            closeAccelerometerConnection();
        }
    }

    /**
     * Opens the data channel.
     */
    private void openAccelerometerConnection() {
        if (DeviceInfo.isSimulator()) {
            _simulated = true;
        } else {
            _accChannel =
                    AccelerometerSensor
                            .openRawDataChannel(AccelerometerDemo.this);
            _simulated = false;
        }
    }

    /**
     * Gets the latest acceleromenter data.
     */
    private void readAcceleration() {
        if (_simulated) {
            // Running in a simulator, simulate random.
            if (_tick % 10 == 0) {
                _xyz[0] = (short) (_r.nextInt(400) - 200);
                _xyz[1] = (short) (_r.nextInt(400) - 200);
            }
        } else {
            // Real device, call the API for samples.
            _accChannel.getLastAccelerationData(_xyz);
        }
    }

    /**
     * Closes the data channel.
     */
    private void closeAccelerometerConnection() {
        if (_accChannel != null) {
            _accChannel.close();
            _accChannel = null;
        }
    }

    /**
     * Menu item to start the ball moving.
     */
    private final MenuItem _startMenuItem = new MenuItem("Start", 0, 0) {
        public void run() {
            if (_thread == null) {
                // Start drawing
                _thread = new DrawThread();
                _thread.start();
            }

        }
    };

    /**
     * Menu item to stop the ball moving.
     */
    private final MenuItem _stopMenuItem = new MenuItem("Stop", 0, 0) {
        public void run() {
            if (_thread != null) {
                synchronized (_thread) {
                    _thread._running = false;
                    _thread.notifyAll();
                    _thread = null;
                }
            }
        }
    };

    /**
     * A screen on which to display the ball.
     */
    private class AccelerometerDemoScreen extends MainScreen {
        /**
         * @see Screen#paint(Graphics)
         */
        protected void paint(final Graphics graphics) {
            if (_ball != null) {
                graphics.drawBitmap(_x, _y, _ballWidth, _ballHeight, _ball, 0,
                        0);
            }
        }

        /**
         * @see Screen#onClose()
         */
        public boolean onClose() {
            if (_thread != null) {
                synchronized (_thread) {
                    _thread._running = false;
                    _thread.notifyAll();
                }
                Thread.yield();
            }
            return super.onClose();
        }
    }
}
