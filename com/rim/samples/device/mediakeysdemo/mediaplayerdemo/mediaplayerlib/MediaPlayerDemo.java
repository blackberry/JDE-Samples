/*
 * MediaPlayerDemo.java
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

import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

import net.rim.device.api.media.MediaActionHandler;
import net.rim.device.api.media.MediaKeyListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * A media player application that demonstrates the use of media keys APIs. We
 * are creating a media player that has standard UI functions such as play,
 * pause, fast forward and reverse. These functions can also be invoked through
 * the use of the media keys that are available on certain devices. Most devices
 * have dedicated keys for volume up and volume down. Some devices have a toggle
 * play/pause key, whose functionality is mimicked though the mute key on
 * devices that do not. Moreover, the forward and reverse keys function is
 * invoked by holding the volume up and volume down key respectively on devices
 * that lack dedicated keys for those actions. This application also
 * demonstrates how one can control the media player with these keys when the
 * application is not in the foreground.
 */

public class MediaPlayerDemo implements Runnable, MediaActionHandler,
        PlayerListener {
    /** Represents a media action to change a track */
    public static final int MEDIA_ACTION_CHANGE_TRACK =
            MEDIA_ACTION_CUSTOM_OFFSET + 1;

    /** Represents a UI source for a media action */
    public static final int MEDIA_ACTION_SOURCE_UI = SOURCE_CUSTOM_OFFSET + 1;

    /** Represents a media player event source for a media action */
    public static final int MEDIA_ACTION_SOURCE_PLAYER_UPDATE =
            SOURCE_CUSTOM_OFFSET + 2;

    private final WeakReference _applicationRef;
    private MediaPlayerDemoScreen _screen;
    private Player _player;
    private VolumeControl _volumeController;
    private int _volume;

    private MediaAction _currentAction;
    private MediaPlayerActions _mediaActions;

    private int _currentTrackIndex = 0;
    private PlaylistEntry[] _playlist;

    /**
     * Constructs a new MediaPlayerDemo object
     */
    public MediaPlayerDemo() {
        _volume = 40;
        _mediaActions = new MediaPlayerActions(this);

        final UiApplication app = UiApplication.getUiApplication();
        _applicationRef = new WeakReference(app);

        _screen = new MediaPlayerDemoScreen(this);
        _screen.setOnCloseRunnable(new Runnable() {
            public void run() {
                close();
            }
        });
    }

    /**
     * Changes the volume to the required level
     * 
     * @param newVolume
     *            The new required volume level
     * @throws MediaActionException
     *             If an error occurs performing this action
     */
    public void changeVolume(final int newVolume) throws MediaActionException {
        final VolumeControl control = _volumeController;
        if (control != null) {
            try {
                control.setLevel(newVolume);
            } catch (final Exception e) {
                throw new MediaActionException("unable to set volume to "
                        + newVolume + ": " + e);
            }
        }

        _volume = newVolume;

        // Update the user interface
        final MediaPlayerDemoScreen screen = _screen;
        if (screen != null) {
            final UiApplication app = getApplication();
            if (app != null) {
                app.invokeLater(new Runnable() {
                    public void run() {
                        screen.setVolume(newVolume);
                    }
                });
            }
        }
    }

    /**
     * Returns the UiApplication object that represents the media player demo's
     * application
     * 
     * @return the UiApplication object that represents the media player demo's
     *         application. Returns null if the application has completed.
     */
    public UiApplication getApplication() {
        return (UiApplication) _applicationRef.get();
    }

    /**
     * Releases all resources held by this object
     */
    private void close() {
        final UiApplication app = getApplication();
        if (app != null) {
            app.removeMediaActionHandler(this);
        }

        _currentAction = null;
        _mediaActions = null;
        _screen = null;
        _volumeController = null;
        _playlist = null;

        final Player player = _player;
        _player = null;
        if (player != null) {
            try {
                player.stop();
            } catch (final Exception e) {
            }
            try {
                player.close();
            } catch (final Exception e) {
            }
        }
    }

    /**
     * Retrieves the current volume
     * 
     * @return The current volume level
     */
    public int getVolume() {
        return _volume;
    }

    /**
     * Retrieves the current instance of the player object
     * 
     * @return The player object
     */
    public Player getPlayer() {
        return _player;
    }

    /**
     * Retrieves the MediaPlayerDemoScreen object
     * 
     * @return The MediaPlayerDemoScreen object
     */
    public MediaPlayerDemoScreen getScreen() {
        return _screen;
    }

    /**
     * Retrieves the MediaPlayerActions object
     * 
     * @return The MediaPlayerActions object
     */
    public MediaPlayerActions getMediaActions() {
        return _mediaActions;
    }

    /**
     * Retrieves the VolumeController object
     * 
     * @return The VolumeController object
     */
    public VolumeControl getVolumeController() {
        return _volumeController;
    }

    /**
     * Returns the total number of tracks in the playlist
     * 
     * @return The total number of tracks in the playlist
     */
    public int getTotalTracks() {
        final PlaylistEntry[] playlist = _playlist;
        return playlist == null ? 0 : playlist.length;
    }

    /**
     * Retrieves the current track index
     * 
     * @return The index of the track currently playing
     */
    public int getCurrentTrackIndex() {
        return _currentTrackIndex;
    }

    /**
     * Sets the current track index
     * 
     * @param index
     *            The index of the track to be set as current
     */
    public void setCurrentTrackIndex(final int index) {
        _currentTrackIndex = index;

        final MediaPlayerDemoScreen screen = _screen;
        if (screen != null) {
            screen.setPlaylistIndex(index);
        }
    }

    /**
     * Sets the current volume level
     * 
     * @param volume
     *            The new volume level
     */
    public void setVolume(final int volume) {
        _volume = volume;
    }

    /**
     * Sets the current player
     * 
     * @param player
     *            The Player to be set as current
     */
    public void setPlayer(final Player player) {
        _player = player;
    }

    /**
     * Sets the current VolumeController
     * 
     * @param volumeController
     *            The VolumeController to be set as current
     */
    public void setVolumeController(final VolumeControl volumeController) {
        _volumeController = volumeController;
    }

    /**
     * Sets the current MediaAction
     * 
     * @param mediaAction
     *            The MediaAction to be set as current
     */
    public void setCurrentMediaAction(final MediaAction mediaAction) {
        _currentAction = mediaAction;
    }

    /**
     * Invokes a media action
     * 
     * @param action
     *            The action to invoke
     * @param source
     *            The source of the media action as defined in
     *            MediaActionHandler
     * @param context
     *            An object providing additional information about the media
     *            action to perform
     * @return true If the action was completed successfully, false otherwise
     */
    public boolean mediaAction(final int action, final int source,
            final Object context) {
        if (_currentAction != null) {
            // Another action is currently in progress, discard this one
            return false;
        }

        final MediaPlayerActions mediaActions = _mediaActions;
        if (mediaActions == null) {
            return false; // close() has been invoked
        }

        MediaAction actionRunnable;
        try {
            switch (action) {
            case MEDIA_ACTION_VOLUME_UP:
                return mediaActions.doVolumeUp();
            case MEDIA_ACTION_VOLUME_DOWN:
                return mediaActions.doVolumeDown();
            case MEDIA_ACTION_MUTE:
                return mediaActions.doMute(true);
            case MEDIA_ACTION_UNMUTE:
                return mediaActions.doMute(false);
            case MEDIA_ACTION_MUTE_TOGGLE:
                return mediaActions.doMute(!isMuted(_volumeController));
            case MEDIA_ACTION_PLAYPAUSE_TOGGLE:
                if (isPlaying(_player)) {
                    actionRunnable =
                            new MediaAction(MEDIA_ACTION_PAUSE, source,
                                    context, MediaPlayerDemo.this);
                } else {
                    actionRunnable =
                            new MediaAction(MEDIA_ACTION_PLAY, source, context,
                                    MediaPlayerDemo.this);
                }
                break;
            case MEDIA_ACTION_CHANGE_TRACK:
            case MEDIA_ACTION_NEXT_TRACK:
            case MEDIA_ACTION_PAUSE:
            case MEDIA_ACTION_PLAY:
            case MEDIA_ACTION_PREV_TRACK:
            case MEDIA_ACTION_STOP:
                actionRunnable =
                        new MediaAction(action, source, context,
                                MediaPlayerDemo.this);
                break;
            default:
                actionRunnable = null;
            }
        } catch (final MediaActionException e) {
            return true;
        }

        if (actionRunnable == null) {
            return false;
        }

        actionRunnable.start();
        actionRunnable.updateUI();

        return true;
    }

    /*
     * @see javax.microedition.media.PlayerListener#playerUpdate(Player, String,
     * Object)
     */
    public void playerUpdate(final Player player, final String event,
            final Object eventData) {
        if (player == _player && event != null && event.equals(END_OF_MEDIA)) {
            mediaAction(MEDIA_ACTION_NEXT_TRACK,
                    MEDIA_ACTION_SOURCE_PLAYER_UPDATE, event);
        }
    }

    /**
     * Executes the demo
     */
    public void run() {
        final UiApplication app = getApplication();
        if (app == null) {
            return;
        }

        // Register the MediaActionHandler and MediaKeyListener
        app.addMediaActionHandler(this);
        app.addKeyListener(new MyMediaKeyListener());

        // Push the main screen onto the display stack
        final MediaPlayerDemoScreen screen = _screen;
        if (screen != null) {
            app.invokeLater(new Runnable() {
                public void run() {
                    app.pushScreen(screen);
                }
            });
        }

        // Load the playlist, copying the files into the filesystem if they do
        // not exist
        final StatusScreen statusScreen =
                new StatusScreen("Initializing media");
        app.invokeLater(new Runnable() {
            public void run() {
                app.pushScreen(statusScreen);
            }
        });

        PlaylistEntry[] playlist;
        try {
            playlist = PlayList.getPlaylistEntries();
        } catch (final IOException e) {
            MediaPlayerDemo.errorDialog("ERROR: " + e.getMessage());
            playlist = null;
        } finally {
            app.invokeLater(new Runnable() {
                public void run() {
                    app.popScreen(statusScreen);
                }
            });
        }

        // Update the screen to show the playlist
        this._playlist = playlist;
        if (playlist != null && screen != null) {
            app.invokeAndWait(new Runnable() {
                public void run() {
                    screen.setPlaylist(_playlist);
                }
            });
        }
    }

    /**
     * Indicates whether the application is muted
     * 
     * @param control
     *            The volume controller to check
     * @return true If application is muted, false otherwise
     */
    public static boolean isMuted(final VolumeControl control) {
        return control != null && control.isMuted();
    }

    /**
     * Indicates if the application is paused
     * 
     * @param player
     *            The Player object to check
     * @return true If track is paused, false otherwise
     */
    public static boolean isPaused(final Player player) {
        return player != null && player.getState() != Player.STARTED;
    }

    /**
     * Indicates if the application is playing
     * 
     * @param player
     *            The Player object to check
     * @return true If a track is playing, false otherwise
     */
    public static boolean isPlaying(final Player player) {
        return player != null && player.getState() == Player.STARTED;
    }

    /**
     * Separate exception class to distinguish media errors
     */
    public static class MediaActionException extends Exception {
        /**
         * Constructs a new MediaActionException object
         * 
         * @param message
         *            A descriptive error message
         */
        public MediaActionException(final String message) {
            super(message);
        }
    }

    /**
     * Presents a dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     */
    public static void errorDialog(final String message) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(message);
            }
        });
    }

    /**
     * This class listens for media key presses and returns the action that the
     * key corresponded to.
     */
    private final class MyMediaKeyListener extends MediaKeyListener {
        /**
         * @param action
         *            The action to invoke
         * @param source
         *            The source of the media action as defined in
         *            MediaActionHandler
         * @param context
         *            An object providing additional information about the media
         *            action to perform
         */
        public boolean mediaAction(final int action, final int source,
                final Object context) {
            return MediaPlayerDemo.this.mediaAction(action, source, context);
        }
    }

    /**
     * A popup screen that simply contains a string message.
     */
    private static class StatusScreen extends PopupScreen {
        /**
         * Creates a new instance of StatusScreen.
         * 
         * @param message
         *            the message to display; may be null.
         */
        public StatusScreen(final String message) {
            super(new VerticalFieldManager());
            this.add(new RichTextField(message));
        }
    }
}
