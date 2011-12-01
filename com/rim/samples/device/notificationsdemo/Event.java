/**
 * Event.java
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

package com.rim.samples.device.notificationsdemo;

import net.rim.device.api.notification.NotificationsManager;

/**
 * A class representing a notification event. This class is used to start/stop
 * immediate and deferred events simultaneously.
 */
public final class Event {
    private final long _sourceId;
    long _eventId;
    int _priority;
    private final int _triggerIndex;
    private final long _timeout;

    /**
     * Creates a new Event object
     */
    public Event(final long sourceid, final long eventid, final int priority,
            final long timeout, final int triggerIndex) {
        _sourceId = sourceid;
        _eventId = eventid;
        _priority = priority;
        _triggerIndex = triggerIndex;
        _timeout = timeout;
    }

    /**
     * Invoke the event.
     */
    void fire() {
        // negotiateDeferredEvent() will cause the event to be queued.
        // Ultimately, NotificationsEngineListener.proceedWithDeferredEvent()
        // will be fired in response to the event.
        NotificationsManager.negotiateDeferredEvent(_sourceId, 0, this,
                _timeout, _triggerIndex, null);

        // triggerImmediateEvent() causes non-interactable events to fire, such
        // as tunes, vibrations and LED flashing as specified by the user in the
        // Profiles settings. This call will cause the startNotification()
        // method for all registered Consequence objects to be invoked.
        // By default, the Profiles application has a Consequence registered
        // and will look up the current profile's configurations to determine
        // what needs to be done (in terms of vibrate and tone) for a given
        // notifications source. This application's profile configuration
        // appears as "Notifications Demo" in the Profiles settings.
        NotificationsManager.triggerImmediateEvent(_sourceId, 0, this, null);
    }

    /**
     * Cancel the event.
     */
    void cancel() {
        // If event exists in the queue, it will be removed and
        // NotificationsEngineListener.deferredEventWasSuperseded()
        // will be fired.
        NotificationsManager.cancelDeferredEvent(_sourceId, 0, this,
                _triggerIndex, null);

        // The stopNotification() method for all registered Consequence objects
        // will be called.
        NotificationsManager.cancelImmediateEvent(_sourceId, 0, this, null);
    }
}
