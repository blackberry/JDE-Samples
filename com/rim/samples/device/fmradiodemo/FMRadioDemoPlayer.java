/**
 * FMRadioDemoPlayer.java
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

import java.io.IOException;

import javax.microedition.amms.control.tuner.RDSControl;
import javax.microedition.amms.control.tuner.TunerControl;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

/**
 * A class which interacts with the FM Radio API
 */
public class FMRadioDemoPlayer implements PlayerListener {
    private Player _player;
    private FmTunerControl _fmTunerControl;
    private RDSControl _rdsControl;
    private VolumeControl _volumeControl;

    private final FMRadioDemoScreen _screen;

    private int _rssi = -90;
    private int _volume = 50;

    /**
     * Creates a new FMRadioDemo Player object
     */
    public FMRadioDemoPlayer(final FMRadioDemoScreen screen) {
        _screen = screen;

        // Set up the Player object
        createPlayer();
        realizePlayer();
        prefetchPlayer();
    }

    /**
     * Switches reception for RDS/RBDS on or off
     * 
     * @param on
     *            True, if the reception for RDS/RBDS should be switched on;
     *            false, if reception should be switched off.
     */
    public void switchRbds(final boolean on) {
        final int state = _player.getState();

        if (_fmTunerControl != null
                && (state == Player.STARTED || state == Player.PREFETCHED)) {
            try {
                _fmTunerControl.setRdsRbdsReceptionEnabled(on);
            } catch (final MediaException m) {
                FMRadioDemo.errorDialog("MediaException on switching RDBS:"
                        + m.getMessage());
            }
        }
    }

    /**
     * Takes the startFrequency as a basis, adds/subtracts an offset of 200kHz
     * and seeks in the specified direction (up->higher frequencies / down ->
     * lower frequencies) until a new station is found.
     * 
     * @param startFrequency
     *            The frequency to start searching from.
     * @param directionUp
     *            The direction to in which to seek: true, seek in upward
     *            direction; false, seek in downward direction
     * 
     */
    public void seek(final int startFrequency, final boolean directionUp) {
        if (_player != null && _fmTunerControl != null
                && checkFrequencyInRange(startFrequency)) {
            // Add an offset to <code>startfrequency</code> to move off
            // currently tuned frequency
            final int fr = startFrequency + (directionUp ? 2000 : -2000);
            int found = 0;
            try {
                // Start a seek in the given direction with the current RSSI
                // value
                found = _fmTunerControl.seek(fr, directionUp, _rssi)[0];

                if (found == 0) // This happens if no station is found and seek
                                // times out after 5 seconds
                {
                    _screen.updateLog("seek failed: f=" + fr);
                    _fmTunerControl.setMute(false); // Seek without station
                                                    // stays muted
                    tuneTo(startFrequency); // Tune back to previous frequency
                }
            } catch (final MediaException e) {
                // No station found, tune to previous frequency
                tuneTo(startFrequency);

                try {
                    _fmTunerControl.setMute(false); // Seek without station
                                                    // stays muted
                } catch (final MediaException e1) {
                    FMRadioDemo
                            .errorDialog("MediaException while seeking and trying to mute:"
                                    + e1.getMessage());
                }
            }
        }
    }

    /**
     * Tunes the radio to a given frequency. The frequency can only be set if it
     * is within the band limits of the given locale.
     * 
     * @param frequency
     *            The frequency to tune the radio to in 100Hz
     */
    public void tuneTo(final int frequency) {
        if (_player != null && _fmTunerControl != null) {
            if (checkFrequencyInRange(frequency)) {
                _fmTunerControl.setFrequency(frequency,
                        TunerControl.MODULATION_FM);
            }
        }
    }

    /**
     * Utility method to check if a given frequency value is in the range of the
     * current locale's band limits.
     * 
     * @param frequency
     *            The frequency for which to check validity
     * @return True, if the given frequency is within the currently supported
     *         band limits; false, otherwise.
     */
    public boolean checkFrequencyInRange(final int frequency) {
        if (_player != null && _fmTunerControl != null) {
            final int min =
                    _fmTunerControl.getMinFreq(TunerControl.MODULATION_FM);
            final int max =
                    _fmTunerControl.getMaxFreq(TunerControl.MODULATION_FM);
            return frequency >= min && frequency <= max;
        }
        return false;
    }

    /**
     * Get the currently tuned frequency from the tuner
     * 
     * @return The frequency the tuner is set to in 100Hz or -1 if the
     *         Player/FmTunerControl is null
     */
    public int getFrequency() {
        if (_player != null && _fmTunerControl != null) {
            return _fmTunerControl.getFrequency();
        }
        return -1;
    }

    /**
     * Returns the station name identified via RDS/RBDS
     * 
     * @return The station name decoded via RDS/RBDS or the String "NO RDS" if
     *         RDS is not used
     */
    public String getRdsPS() {
        if (_rdsControl != null) {
            return _rdsControl.getPS();
        }
        return "NO RDS";
    }

    /**
     * Gets the decoded group type as String for the current station. This works
     * only if the radio plays back from that station and RDS is turned on.
     * 
     * @return The genre String as defined in the RDS/RBDS standard for the RDS
     *         group type
     */
    public String getRdsPty() {
        if (_rdsControl != null) {
            return _rdsControl.getPTYString(true);
        }
        return "NO RDS!";
    }

    /**
     * Creates a player for FM radio playback
     */
    public void createPlayer() {
        try {
            _player = Manager.createPlayer("capture://radio");
            _player.addPlayerListener(this);
        } catch (final IOException e) {
            FMRadioDemo.errorDialog("IOException on createPlayer: "
                    + e.getMessage());
            closePlayer();
        } catch (final SecurityException e) {
            FMRadioDemo.errorDialog("SecurityException on createPlayer: "
                    + e.getMessage());
            closePlayer();
        } catch (final MediaException m) {
            FMRadioDemo.errorDialog("MediaException on createPlayer: "
                    + m.getMessage());
            closePlayer();
        }
    }

    /**
     * Realizes the player for FM radio playback. The player needs to be in
     * Player.UNREALIZED state. This call also instantiates an FmTunerControl
     * and an RDSControl.
     */
    public void realizePlayer() {
        if (_player != null) {
            if (_player.getState() == Player.UNREALIZED) {
                try {
                    _player.realize();

                    _fmTunerControl =
                            (FmTunerControl) _player.getControl("TunerControl");

                    _rdsControl = (RDSControl) _player.getControl("RDSControl");
                } catch (final MediaException m) {
                    FMRadioDemo.errorDialog("MediaException on realizePlayer: "
                            + m.getMessage());
                    closePlayer();
                }
            }
        }
    }

    /**
     * Prefetches the Player. The Player needs to be in the Player.REALIZED
     * state before calling the method.
     */
    public void prefetchPlayer() {
        if (_player != null) {
            if (_player.getState() == Player.REALIZED) {
                try {
                    _player.prefetch();
                } catch (final MediaException m) {
                    FMRadioDemo
                            .errorDialog("MediaException on prefetchPlayer: "
                                    + m.getMessage());
                    closePlayer();
                }
            }
        }
    }

    /**
     * Starts the Player. The Player needs to be in Player.PREFETCHED state.
     */
    public void startPlayer() {
        if (_player != null) {
            if (_player.getState() == Player.PREFETCHED) {
                try {
                    tuneTo(_screen.getFrequency());
                    _player.start();
                    _volumeControl =
                            (VolumeControl) _player
                                    .getControl("javax.microedition.media.control.VolumeControl");
                    setVolume();
                } catch (final MediaException m) {
                    FMRadioDemo.errorDialog("MediaException on startPlayer: "
                            + m.getMessage());
                    closePlayer();
                }
            }
        }
    }

    /**
     * Stops the Player. The Player needs to be in the Player.STARTED state.
     */
    public void stopPlayer() {
        if (_player != null) {
            if (_player.getState() == Player.STARTED) {
                try {
                    _player.stop();
                } catch (final MediaException m) {
                    FMRadioDemo.errorDialog("MediaException on stopPlayer: "
                            + m.getMessage());
                    closePlayer();
                }
            }
        }
    }

    /**
     * Closes the Player
     */
    public void closePlayer() {
        _player.close();
    }

    /**
     * Increases the volume
     */
    public void increaseVolume() {
        if (_volume < 100) {
            _volume += 10;
            setVolume();
        }
    }

    /**
     * Decreases the volume
     */
    public void decreaseVolume() {
        if (_volume > 0) {
            _volume -= 10;
            setVolume();
        }
    }

    /**
     * Sets the current volume
     */
    private void setVolume() {
        if (_player != null) {
            if (_volumeControl != null) {
                _volumeControl.setLevel(_volume);
            }
        }
    }

    /**
     * @see javax.microedition.media.PlayerListener#playerUpdate(Player, String,
     *      Object)
     */
    public void playerUpdate(final Player player, final String event,
            final Object eventData) {
        if (player != _player) {
            return;
        }

        _screen.updateLog("playerUpdate() event = " + event);

        if (event == PlayerListener.STARTED
                && _player.getState() != Player.STARTED) {
            _screen.updateLog("playerUpdate() event = " + event
                    + ", eventData = " + eventData);
        } else if (event.equals(PlayerListener.STOPPED)) {
            _screen.updateLog("playerUpdate() event = STOPPED, eventData = "
                    + eventData);
        } else if (event.equals(PlayerListener.DEVICE_UNAVAILABLE)) {
            final int status = _player.getState();
            if (status == Player.STARTED) {
                _screen.updateLog("FMRadio: playerUpdate() event = DEVICE_UNAVAILABLE, eventData = "
                        + eventData);
            }
        } else if (event.equals(PlayerListener.DEVICE_AVAILABLE)) {
            _screen.updateLog("FMRadio: playerUpdate() event = DEVICE_AVAILABLE");
        } else if (event.equals(RDSControl.RDS_NEW_DATA) && eventData != null) {
            _screen.updateRDS();
            _screen.updateLog("FMRadio: playerUpdate() event = RDS_NEW_DATA");
        } else if (event.equals(PlayerListener.ERROR)) {
            // This happens if the device is switched off while playing back
            _screen.updateLog("FMRadio: playerUpdate() event = ERROR, eventData = "
                    + eventData);
        }
    }

    /**
     * Gets the current RSSI value as String for representation in a Field
     * 
     * @return The current RSSI value as String
     */
    public String getRSSI() {
        return Integer.toString(_rssi);
    }

    /**
     * Increases the RSSI value by one
     */
    public void increaseRSSI() {
        _rssi++;
    }

    /**
     * Decreases the RSSI value by one
     */
    public void decreaseRSSI() {
        _rssi--;
    }

    /**
     * Gets the current Player state
     * 
     * @return The player state as defined in the Player interface as one of:
     *         UNREALIZED, REALIZED, PREFETCHED, STARTED, CLOSED, or -1 if the
     *         Player is null
     */
    public int getPlayerState() {
        if (_player != null) {
            return _player.getState();
        }

        return -1;
    }
}
