/*
 * MediaPlayerActions.java
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

package com.rim.samples.device.mediakeysdemo.mediaplayerdemo.mediaplayerlib;

import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

import net.rim.device.api.ui.UiApplication;

import com.rim.samples.device.mediakeysdemo.mediaplayerdemo.mediaplayerlib.MediaPlayerDemo.MediaActionException;

/**
 * This class handles the individual media actions
 */
public class MediaPlayerActions {
    private final MediaPlayerDemo _handler;

    /**
     * Constructs a MediaPlayerActions object
     * 
     * @param handler
     *            The instance of the MediaPlayerDemo class
     */
    public MediaPlayerActions(final MediaPlayerDemo handler) {
        _handler = handler;
    }

    /**
     * Changes the track that is playing
     * 
     * @return true If track change operation was successful, false otherwise
     * @throws MediaActionException
     *             If an error occurs performing this action
     */
    public boolean doChangeTrack() throws MediaActionException {
        final Player player = _handler.getPlayer();

        if (MediaPlayerDemo.isPlaying(player)) {
            doStop();
        }

        doPlay();
        return true;
    }

    /**
     * Mutes and unmutes the application
     * 
     * @param mute
     *            Set true to mute, false to unmute
     * @return true If mute operation was successful, otherwise false
     * @throws MediaActionException
     *             If an error occurs performing this action
     */
    public boolean doMute(final boolean mute) throws MediaActionException {
        final VolumeControl volumeControl = _handler.getVolumeController();

        final boolean curMute = MediaPlayerDemo.isMuted(volumeControl);
        if (curMute == mute || volumeControl == null) {
            return false;
        }

        try {
            volumeControl.setMute(mute);
        } catch (final Exception e) {
            throw new MediaActionException("unable to " + (mute ? "" : "un")
                    + "mute audio: " + e);
        }

        final MediaPlayerDemoScreen screen = _handler.getScreen();
        if (screen != null) {
            final UiApplication app = UiApplication.getUiApplication();
            app.invokeLater(new Runnable() {
                public void run() {
                    screen.setMuted(mute);
                }
            });
        }

        return true;
    }

    /**
     * Plays another track in the playlist, relative to the currently-playing
     * track.
     * 
     * @param amount
     *            The amount to change tracks. A positive value will skip
     *            forwards the specified number of tracks and a negative value
     *            will skip backwards the specified number of tracks.
     * @param forcePlay
     *            If true, then start playing the new track no matter what.
     *            Otherwise, only start playing the new track if the old track
     *            was playing.
     * @return true If next track operation was successful, false otherwise
     * @throws MediaActionException
     *             If an error occurs performing this action
     */
    public boolean doIncTrack(final int amount, boolean forcePlay)
            throws MediaActionException {
        final int index = _handler.getCurrentTrackIndex() + amount;
        final int size = _handler.getTotalTracks();
        if (index < 0 || index >= size) {
            return false;
        }

        Player player = _handler.getPlayer();
        if (MediaPlayerDemo.isPlaying(player)) {
            forcePlay = true;
        }

        if (player != null) {
            doStop();
            player = null;
        }

        _handler.setCurrentTrackIndex(index);
        if (forcePlay) {
            doChangeTrack();
        }

        return true;
    }

    /**
     * Pauses the player
     * 
     * @return true If the pause operation was successful, false otherwise
     * @throws MediaActionException
     *             If an error occurs performing this action
     */
    public boolean doPause() throws MediaActionException {
        final Player player = _handler.getPlayer();

        if (player == null || player.getState() != Player.STARTED) {
            // Not playing, nothing to do
            return false;
        }

        if (player != null) {
            try {
                player.stop();
            } catch (final Exception e) {
                throw new MediaActionException("Unable to unpause player: " + e);
            }
        }

        return true;
    }

    /**
     * Plays the current track
     * 
     * @return true If the play operation was successful, false otherwise
     * @throws MediaActionException
     *             If an error occurs performing this action
     */
    public boolean doPlay() throws MediaActionException {
        Player player = _handler.getPlayer();

        if (player != null) {
            if (player.getState() == Player.STARTED) {
                // Already playing, nothing to do
                return false;
            }
        }

        if (player == null) {
            final MediaPlayerDemoScreen screen = _handler.getScreen();
            if (screen == null) {
                return false; // MediaPlayerDemo has closed
            }

            final PlaylistEntry entry = screen.getCurrentPlaylistEntry();
            if (entry == null) {
                return false; // No entry to play
            }

            final String url = entry.getURL();
            try {
                player = Manager.createPlayer(url);
            } catch (final Exception e) {
                throw new MediaActionException("unable to load media from "
                        + url + ": " + e);
            }
            _handler.setPlayer(player);
            player.addPlayerListener(_handler);

            try {
                player.realize();
            } catch (final Exception e) {
                throw new MediaActionException("unable to fetch media: " + e);
            }

            Control control = player.getControl("VolumeControl");
            if (control instanceof VolumeControl) {
                final VolumeControl volumeControl = (VolumeControl) control;
                _handler.setVolumeController(volumeControl);

                // Unmute the application on the assumption that if the user
                // asks to
                // play a track they actually want to hear it.
                try {
                    doMute(false);
                } catch (final Exception e) {
                    _handler.setVolumeController(null);
                    control = null;
                }

                // Set the volume to match the last-selected level
                try {
                    _handler.changeVolume(_handler.getVolume());
                } catch (final Exception e) {
                    _handler.setVolumeController(null);
                    control = null;
                }
            }
        }

        if (player != null) {
            try {
                player.start();
            } catch (final Exception e) {
                throw new MediaActionException("unable start player: " + e);
            }
        }
        return true;
    }

    /**
     * Plays the next track in the playlist. If there is currently no playing
     * song then the playback is not started. Otherwise, the current song is
     * stopped and the new song is started.
     * 
     * @param source
     *            The media action source that caused this action to occur
     * @param context
     *            The context of the media action that caused this action to
     *            occur
     * @return true If the track operation was successful, false otherwise
     * @throws MediaActionException
     *             If an error occurs performing this action
     */
    public boolean doNextTrack(final int source, final Object context)
            throws MediaActionException {
        boolean forcePlay;

        // Check if the action arose from the end of the current track being
        // reached
        if (source == MediaPlayerDemo.MEDIA_ACTION_SOURCE_PLAYER_UPDATE
                && context != null
                && context.equals(PlayerListener.END_OF_MEDIA)) {
            forcePlay = true;
        } else {
            forcePlay = false;
        }

        return doIncTrack(1, forcePlay);
    }

    /**
     * Plays the previous track in the playlist if the current track has been
     * playing for less than two seconds, otherwise replays the current track
     * from the beginning.
     * 
     * @return true If the track operation was successful, false otherwise
     * @throws MediaActionException
     *             If an error occurs performing this action
     */
    public boolean doPrevTrack() throws MediaActionException {
        final Player player = _handler.getPlayer();

        if (MediaPlayerDemo.isPlaying(player)) {
            final long timeInMicroSeconds = player.getMediaTime();

            // If more that 2 seconds has elapsed, then go back to the beginning
            if (timeInMicroSeconds > 2000000L) {
                try {
                    player.setMediaTime(0);
                    return true;
                } catch (final Exception e) {
                    throw new MediaActionException(
                            "unable to go back to beginning: " + e);
                }
            }
        }

        return doIncTrack(-1, false);
    }

    /**
     * Stops the player
     * 
     * @return true If the stop operation was successful, false otherwise
     * @throws MediaActionException
     *             If an error occurs performing this action
     */
    public boolean doStop() throws MediaActionException {
        final Player player = _handler.getPlayer();
        if (player == null) {
            // Already stopped, nothing to do
            return false;
        }

        _handler.setPlayer(null);
        _handler.setVolumeController(null);

        try {
            player.removePlayerListener(_handler);
        } catch (final Exception e) {
        }

        try {
            player.close();
        } catch (final Exception e) {
            throw new MediaActionException("closing Player failed: " + e);
        }

        return true;
    }

    /**
     * Decreases the audio level
     * 
     * @return true If the volume decrease operation was successful, false
     *         otherwise
     * @throws MediaActionException
     *             If an error occurs performing this action
     */
    public boolean doVolumeDown() throws MediaActionException {
        final int newVolume = Math.max(_handler.getVolume() - 10, 00);
        if (_handler.getVolume() == newVolume) {
            return false;
        }
        _handler.changeVolume(newVolume);

        return true;
    }

    /**
     * Increases the audio level
     * 
     * @return true If the volume increase operation was successful, false
     *         otherwise
     * @throws MediaActionException
     *             If an error occurs performing this action
     */
    public boolean doVolumeUp() throws MediaActionException {
        final int newVolume = Math.min(_handler.getVolume() + 10, 100);
        if (_handler.getVolume() == newVolume) {
            return false;
        }
        _handler.changeVolume(newVolume);

        return true;
    }
}
