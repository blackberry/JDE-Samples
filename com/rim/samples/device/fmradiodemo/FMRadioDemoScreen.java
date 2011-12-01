/**
 * FMRadioDemoScreen.java
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

package com.rim.samples.device.fmradiodemo;

import javax.microedition.media.Player;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.media.MediaActionHandler;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.StandardTitleBar;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * The UI screen for the FM Radio Demo
 */
public class FMRadioDemoScreen extends MainScreen {
    private static final int INITIAL_FREQUENCY = 990000;

    private FMRadioDemoPlayer _fmRadioDemoPlayer;

    private ButtonField _setFrequencyButton;
    private ButtonField _seekUpButton;
    private ButtonField _seekDownButton;
    private ButtonField _increaseRSSIButton;
    private ButtonField _decreaseRSSIButton;
    private ButtonField _playButton;

    private LabelField _rssiLabel;

    private TextField _rdsTextField;
    private TextField _frequencyTextField;
    private TextField _logTextField;

    private final MediaHandler _mediaHandler;

    /**
     * Creates a new FMRadioDemoScreen object
     */
    public FMRadioDemoScreen() {
        initializePlayer();

        final StandardTitleBar standardTitleBar = new StandardTitleBar();
        standardTitleBar.addTitle("FM Radio Demo");
        standardTitleBar.addNotifications();
        standardTitleBar.addSignalIndicator();
        setTitleBar(standardTitleBar);

        _mediaHandler = new MediaHandler();
        Application.getApplication().addMediaActionHandler(_mediaHandler);

        initializeUiComponents();
        createLayout();
    }

    /**
     * Initializes the Player
     */
    void initializePlayer() {
        _fmRadioDemoPlayer = new FMRadioDemoPlayer(this);
    }

    /**
     * Updates the RDS data after an event has indicated that there is new data
     * available
     */
    void updateRDS() {
        final StringBuffer rdsBuffer = new StringBuffer();
        rdsBuffer.append("RDS_PS:");
        rdsBuffer.append(_fmRadioDemoPlayer.getRdsPS());
        rdsBuffer.append(" PTY:");
        rdsBuffer.append(_fmRadioDemoPlayer.getRdsPty());
        _rdsTextField.setText(rdsBuffer.toString());
    }

    /**
     * Initializes the screen's UI elements and defines the button actions
     */
    private void initializeUiComponents() {
        _frequencyTextField = new TextField("Frequency [MHz]: ", "");

        _setFrequencyButton = new ButtonField("Set Frequency");
        _seekUpButton = new ButtonField("Seek >");
        _seekDownButton = new ButtonField("< Seek");
        _increaseRSSIButton = new ButtonField("RSSI +");
        _decreaseRSSIButton = new ButtonField("- RSSI");
        _playButton = new ButtonField("Play");

        _rssiLabel = new LabelField(_fmRadioDemoPlayer.getRSSI());

        _rdsTextField = new TextField(Field.NON_FOCUSABLE);
        _rdsTextField.setLabel("RDS: ");

        _logTextField = new TextField(Field.NON_FOCUSABLE);
        _logTextField.setLabel("Log: ");

        _playButton.setCommand(new Command(new CommandHandler() {
            public void execute(final ReadOnlyCommandMetadata data,
                    final Object context) {
                // Check whether the Player has already been started
                if (_fmRadioDemoPlayer.getPlayerState() == Player.STARTED) {
                    _fmRadioDemoPlayer.switchRbds(false);
                    _fmRadioDemoPlayer.stopPlayer();
                    _playButton.setLabel("Play");
                } else {
                    _fmRadioDemoPlayer.startPlayer();
                    _fmRadioDemoPlayer.switchRbds(true);
                    _playButton.setLabel("Stop");
                }
            }
        }));

        _seekDownButton.setCommand(new Command(new CommandHandler() {
            public void execute(final ReadOnlyCommandMetadata data,
                    final Object context) {
                try {
                    final int frequency =
                            stringToFrequency(_frequencyTextField.getText());
                    _fmRadioDemoPlayer.seek(frequency, false);
                    _frequencyTextField
                            .setText(frequencyToString(_fmRadioDemoPlayer
                                    .getFrequency()));
                } catch (final NumberFormatException n) {
                    // If the TextField cannot be parsed as a valid frequency,
                    // set the TextField text to a valid frequency.
                    _frequencyTextField
                            .setText(frequencyToString(INITIAL_FREQUENCY));
                }
            }
        }));

        _seekUpButton.setCommand(new Command(new CommandHandler() {
            public void execute(final ReadOnlyCommandMetadata data,
                    final Object context) {
                try {
                    final int frequency =
                            stringToFrequency(_frequencyTextField.getText());
                    _fmRadioDemoPlayer.seek(frequency, true);
                    _frequencyTextField
                            .setText(frequencyToString(_fmRadioDemoPlayer
                                    .getFrequency()));
                } catch (final NumberFormatException n) {
                    // If the TextField cannot be parsed as a valid frequency,
                    // set the TextField text to a valid frequency.
                    _frequencyTextField
                            .setText(frequencyToString(INITIAL_FREQUENCY));
                }
            }
        }));

        _increaseRSSIButton.setCommand(new Command(new CommandHandler() {
            public void execute(final ReadOnlyCommandMetadata data,
                    final Object context) {
                // Increase the internally stored value
                // for RSSI that is used for seeking.
                _fmRadioDemoPlayer.increaseRSSI();
                _rssiLabel.setText(_fmRadioDemoPlayer.getRSSI());
            }
        }));

        _decreaseRSSIButton.setCommand(new Command(new CommandHandler() {
            public void execute(final ReadOnlyCommandMetadata data,
                    final Object context) {
                // Decrease the internally stored value
                // for RSSI that is used for seeking.
                _fmRadioDemoPlayer.decreaseRSSI();
                _rssiLabel.setText(_fmRadioDemoPlayer.getRSSI());
            }
        }));

        _setFrequencyButton.setCommand(new Command(new CommandHandler() {
            public void execute(final ReadOnlyCommandMetadata data,
                    final Object context) {
                try {
                    final String text = _frequencyTextField.getText();
                    if (text.indexOf(".") > -1) {
                        // Convert String to an integer frequency value
                        final int frequency =
                                stringToFrequency(_frequencyTextField.getText());

                        if (_fmRadioDemoPlayer.checkFrequencyInRange(frequency)) {
                            // Send the given frequency to the API for tuning
                            _fmRadioDemoPlayer.tuneTo(frequency);
                        }
                    }
                } catch (final NumberFormatException n) {

                    FMRadioDemo.errorDialog("Integer.parseInt() threw: "
                            + n.toString());
                } finally {
                    String text;

                    if (_fmRadioDemoPlayer.getPlayerState() == Player.STARTED) {
                        text =
                                frequencyToString(_fmRadioDemoPlayer
                                        .getFrequency());
                    } else {
                        text = frequencyToString(INITIAL_FREQUENCY);
                    }

                    _frequencyTextField.setText(text);
                }
            }
        }));

        _frequencyTextField.setMaxSize(5);
        _frequencyTextField.setText(frequencyToString(INITIAL_FREQUENCY));
    }

    /**
     * Lays out the UI elements on the screen
     */
    private void createLayout() {
        final VerticalFieldManager verticalFieldManager =
                new VerticalFieldManager(Field.FIELD_HCENTER);
        final HorizontalFieldManager horizontalFieldManager1 =
                new HorizontalFieldManager(Field.FIELD_HCENTER);
        final HorizontalFieldManager horizontalFieldManager2 =
                new HorizontalFieldManager(Field.FIELD_HCENTER);
        final HorizontalFieldManager horizontalFieldManager3 =
                new HorizontalFieldManager(Field.FIELD_HCENTER);
        final HorizontalFieldManager horizontalFieldManager4 =
                new HorizontalFieldManager(Field.FIELD_HCENTER);

        horizontalFieldManager1.add(_setFrequencyButton);

        horizontalFieldManager2.add(_seekDownButton);
        horizontalFieldManager2.add(_seekUpButton);

        horizontalFieldManager3.add(_decreaseRSSIButton);
        horizontalFieldManager3.add(_rssiLabel);
        horizontalFieldManager3.add(_increaseRSSIButton);

        horizontalFieldManager4.add(_playButton);

        verticalFieldManager.add(_frequencyTextField);
        verticalFieldManager.add(horizontalFieldManager1);
        verticalFieldManager.add(horizontalFieldManager2);
        verticalFieldManager.add(horizontalFieldManager3);
        verticalFieldManager.add(horizontalFieldManager4);
        verticalFieldManager.add(new SeparatorField());
        verticalFieldManager.add(_rdsTextField);
        verticalFieldManager.add(_logTextField);

        add(verticalFieldManager);
    }

    /**
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close() {
        try {
            Application.getApplication()
                    .removeMediaActionHandler(_mediaHandler);
            _fmRadioDemoPlayer.closePlayer();
        } finally {
            super.close();
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Supress the save dialog
        return true;
    }

    /**
     * Formats a frequency in KHz to a String in MHz with a dot after the MHz
     * number and one digit after the dot to represent the 100kHz value.
     * 
     * @param frequency
     *            Frequency in KHz units
     * @return A String representing the frequency in MHz (e.g. "102.5")
     */
    private String frequencyToString(final int frequency) {
        // The frequency should have a maximum of 5 digits in MHz
        final StringBuffer buffer = new StringBuffer(5);

        // Append MHz
        buffer.append(frequency / 10000);
        buffer.append(".");

        // Append digit after the dot
        buffer.append(frequency % 10000 / 1000);

        return buffer.toString();
    }

    /**
     * Utility method to convert a frequency in String format and MHz into a
     * frequency in 100kHz.
     * 
     * @param frequncy
     *            The frequency representation in MHz with following dot and
     *            digit for kHz
     * @return The frequency in 100Hz
     */
    private int stringToFrequency(final String frequency) {
        // Parse MHz
        int f =
                Integer.parseInt(frequency.substring(0, frequency.indexOf('.'))) * 10000;

        // Parse value after the dot
        f +=
                Integer.parseInt(frequency
                        .substring(frequency.indexOf('.') + 1)) * 1000;

        return f;
    }

    /**
     * Retrieves frequency indicated in the frequency text field
     * 
     * @return Currently set frequency as an integer
     */
    public int getFrequency() {
        return stringToFrequency(_frequencyTextField.getText());
    }

    /**
     * Logs information to the screen
     * 
     * @param log
     *            Information of interest
     */
    void updateLog(final String log) {
        _logTextField.setText(log);
    }

    /**
     * A MediaActionHandler implementation
     */
    class MediaHandler implements MediaActionHandler {
        /**
         * @see net.rim.device.api.media.MediaActionHandler#mediaAction(int,
         *      int, Object)
         */
        public boolean mediaAction(final int action, final int src,
                final Object context) {
            switch (action) {
            case MEDIA_ACTION_PLAYPAUSE_TOGGLE:
            case MEDIA_ACTION_MUTE:
            case MEDIA_ACTION_UNMUTE:
            case MEDIA_ACTION_MUTE_TOGGLE:
                break;
            case MEDIA_ACTION_NEXT_TRACK:

                // Use the next-track-button as a seek-up function
                _fmRadioDemoPlayer
                        .seek(_fmRadioDemoPlayer.getFrequency(), true);

                // Display the new frequency
                _frequencyTextField
                        .setText(frequencyToString(_fmRadioDemoPlayer
                                .getFrequency()));

                break;
            case MEDIA_ACTION_PREV_TRACK:

                // Use the prev-track-button as a seek-down function
                _fmRadioDemoPlayer.seek(_fmRadioDemoPlayer.getFrequency(),
                        false);

                // Display the new frequency
                _frequencyTextField
                        .setText(frequencyToString(_fmRadioDemoPlayer
                                .getFrequency()));

                break;
            case MEDIA_ACTION_VOLUME_DOWN:
                _fmRadioDemoPlayer.decreaseVolume();
                break;
            case MEDIA_ACTION_VOLUME_UP:
                _fmRadioDemoPlayer.increaseVolume();
                break;
            default:
                break;
            }
            return false;
        }
    }
}
