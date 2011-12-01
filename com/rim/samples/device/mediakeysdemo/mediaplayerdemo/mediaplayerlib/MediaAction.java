/*
 * MediaAction.java
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

import net.rim.device.api.media.MediaActionHandler;

import com.rim.samples.device.mediakeysdemo.mediaplayerdemo.mediaplayerlib.MediaPlayerDemo.MediaActionException;

/**
 * This class handles the action performed by the media keys
 */
public class MediaAction {
    protected int _source;
    protected Object _context;
    protected MediaPlayerDemo _handler;
    protected int _type;

    /**
     * Constructs a new MediaAction object
     * 
     * @param type
     *            The type of the MediaAction to create
     * @param source
     *            The source of the media action as defined in
     *            MediaActionHandler
     * @param context
     *            An object providing additional information about the media
     *            action to perform
     * @param handler
     *            The application the action is to be added to
     */
    public MediaAction(final int type, final int source, final Object context,
            final MediaPlayerDemo handler) {
        _source = source;
        _context = context;
        _handler = handler;
        _type = type;
    }

    /**
     * Creates a new thread to do the specified media action
     */
    public void start() {
        new Thread() {
            public void run() {
                final MediaPlayerActions mediaActions =
                        _handler.getMediaActions();
                if (mediaActions == null) {
                    return; // MediaPlayerDemo has been closed
                }

                _handler.setCurrentMediaAction(MediaAction.this);
                try {
                    switch (_type) {
                    case MediaPlayerDemo.MEDIA_ACTION_CHANGE_TRACK:
                        mediaActions.doChangeTrack();
                        break;
                    case MediaActionHandler.MEDIA_ACTION_NEXT_TRACK:
                        mediaActions.doNextTrack(_source, _context);
                        break;
                    case MediaActionHandler.MEDIA_ACTION_PREV_TRACK:
                        mediaActions.doPrevTrack();
                        break;
                    case MediaActionHandler.MEDIA_ACTION_PAUSE:
                        mediaActions.doPause();
                        break;
                    case MediaActionHandler.MEDIA_ACTION_PLAY:
                        mediaActions.doPlay();
                        break;
                    case MediaActionHandler.MEDIA_ACTION_STOP:
                        mediaActions.doStop();
                    }
                } catch (final Exception e) {
                    String message = "ERROR: ";
                    if (e instanceof MediaActionException) {
                        message += e.getMessage();
                    } else {
                        message += e.toString();
                    }

                    MediaPlayerDemo.errorDialog(message);
                } finally {
                    _handler.setCurrentMediaAction(null);
                }
            }
        }.start();
    }

    /**
     * Updates the UI Buttons
     */
    public void updateUI() {
        final MediaPlayerDemoScreen screen = _handler.getScreen();
        if (screen == null) {
            return; // MediaPlayerDemo has been closed
        }

        switch (_type) {
        case MediaActionHandler.MEDIA_ACTION_PAUSE:
            screen.setPlayState(MediaPlayerDemoScreen.STATE_PAUSED);
            break;
        case MediaActionHandler.MEDIA_ACTION_PLAY:
            screen.setPlayState(MediaPlayerDemoScreen.STATE_PLAYING);
            break;
        case MediaActionHandler.MEDIA_ACTION_STOP:
            screen.setPlayState(MediaPlayerDemoScreen.STATE_STOPPED);
        }
    }
}
