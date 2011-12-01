/*
 * MediaListenerImpl.java
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

package com.rim.samples.device.mediaenginedemo;

import net.rim.plazmic.mediaengine.MediaException;
import net.rim.plazmic.mediaengine.MediaListener;
import net.rim.plazmic.mediaengine.io.LoadingStatus;

/**
 * The MediaListener implementation.
 */
final class MediaListenerImpl implements MediaListener {
    /**
     * @see net.rim.plazmic.mediaengine.MediaListener#mediaEvent(Object, int,
     *      int, Object)
     */
    public void mediaEvent(final Object sender, final int event,
            final int eventParam, final Object data) {
        switch (event) {
        case MEDIA_REQUESTED:
            System.out.println("Media requested");
            break;

        case MEDIA_COMPLETE:
            System.out.println("Media completed");
            break;

        case MEDIA_REALIZED:
            System.out.println("Media realized");
            break;

        case MEDIA_IO: {
            final LoadingStatus s = (LoadingStatus) data;

            switch (s.getStatus()) {
            case LoadingStatus.LOADING_STARTED:
                System.out.println("Loading in progress");
                break;

            case LoadingStatus.LOADING_READING:
                System.out.println("Parsing in progress");
                break;

            case LoadingStatus.LOADING_FINISHED:
                System.out.println("Loading completed");
                break;

            case LoadingStatus.LOADING_FAILED:
                String errorName = null;
                final int code = s.getCode();

                switch (code) {
                case MediaException.INVALID_HEADER:
                    errorName = "nvalid header" + "/n" + s.getSource();
                    break;

                case MediaException.REQUEST_TIMED_OUT:
                    errorName = "Request timed out" + "\n" + s.getSource();
                    break;

                case MediaException.INTERRUPTED_DOWNLOAD:
                    break;

                case MediaException.UNSUPPORTED_TYPE:
                    errorName =
                            "Unsupported type" + s.getMessage() + "\n"
                                    + s.getSource();
                    break;

                default: {
                    if (code > 200) {
                        // A code > 200 indicates an HTTP error.
                        errorName = "URL not found";
                    } else {
                        // Default unidentified error.
                        errorName = "Loading Failed";
                    }
                    errorName +=
                            "\n" + s.getSource() + "\n" + s.getCode() + ": "
                                    + s.getMessage();
                    break;
                }
                }

                System.out.println(errorName);
                break;

            } // End switch s.getStatus().

            break;
        }
        }
    }
}
