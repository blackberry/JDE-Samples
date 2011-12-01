/**
 * ConsequenceImpl.java
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

import java.io.EOFException;

import net.rim.device.api.notification.Consequence;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.LED;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;

/**
 * Implementation of the Consequence interface. It must also implement
 * SyncConverter. A Consequence can be used to flash the LED, play a sound, and
 * vibrate the device when it is triggered.
 */
class ConsequenceImpl implements Consequence, SyncConverter {
    // net.rim.samples.device.notificationsdemo.NotificationsDemo.ConsequenceImpl
    public static final long ID = 0xbd2350c0dfda2a51L;
    private static final int TYPE = 'n' << 24 | 'o' << 16 | 't' << 8 | 'd';

    // Notd for NotificationsDemo.
    private static final byte[] DATA = new byte[] { 'm', 'y', '-', 'c', 'o',
            'n', 'f', 'i', 'g', '-', 'o', 'b', 'j', 'e', 'c', 't' };
    private static final Configuration CONFIG = new Configuration(DATA);

    // The TUNE (bar 1 and 2 of Islamey by Balakirev).
    private static final short BFlat = 466; // 466.16
    private static final short AFlat = 415; // 415.30
    private static final short A = 440; // 440.00
    private static final short GFlat = 370; // 369.99
    private static final short DFlat = 554; // 554.37
    private static final short C = 523; // 523.25
    private static final short F = 349; // 349.32
    private static final short TEMPO = 125;
    private static final short d16 = 1 * TEMPO; // Duration of a 16th note,
                                                // arbitrary, in ms.
    private static final short d8 = d16 << 1; // Duration of an eigth note,
                                              // arbitrary, in ms.
    private static final short dpause = 10; // 10 ms pause
    private static final short pause = 0; // Zero frequency pause

    private static final short[] TUNE = new short[] { BFlat, d16, pause,
            dpause, BFlat, d16, pause, dpause, BFlat, d16, pause, dpause,
            BFlat, d16, pause, dpause, A, d16, pause, dpause, BFlat, d16,
            pause, dpause, GFlat, d16, pause, dpause, GFlat, d16, pause,
            dpause, A, d16, pause, dpause, BFlat, d16, pause, dpause, DFlat,
            d16, pause, dpause, C,
            d16,
            pause,
            dpause, // Bar 1
            AFlat, d16, pause, dpause, AFlat, d16, pause, dpause, AFlat, d16,
            pause, dpause, AFlat, d16, pause, dpause, F, d16, pause, dpause,
            GFlat, d16, pause, dpause, AFlat, d16, pause, dpause, BFlat, d16,
            pause, dpause, AFlat, d16, pause, dpause, F, d8 + d16 // Bar 2
    };
    private static final int VOLUME = 80; // % volume

    /**
     * A static inner class, describing the Configuration information for this
     * consequence.
     * <p>
     * This implements the SyncObject interface, although returns a fixed value.
     */
    private static final class Configuration implements SyncObject, Persistable {
        public byte[] _data;

        public Configuration(final byte[] data) {
            _data = data;
        }

        public int getUID() {
            // We're not actually doing any synchronization (vs backup/restore)
            // so we don't care about this value.
            return 0;
        }
    }

    /**
     * @see net.rim.device.api.notification.Consequence#startNotification(long,long,long,Object,Object)
     */
    public void startNotification(final long consequenceID,
            final long sourceID, final long eventID,
            final Object configuration, final Object context) {
        if (sourceID == NotificationsDemo.NOTIFICATIONS_ID_1) {
            // Start the LED blinking.
            LED.setConfiguration(500, 250, LED.BRIGHTNESS_50);
            LED.setState(LED.STATE_BLINKING);

            Alert.startAudio(TUNE, VOLUME);
            Alert.startBuzzer(TUNE, VOLUME);
        }
    }

    /**
     * @see net.rim.device.api.notification.Consequence#stopNotification(long,long,long,Object,Object)
     */
    public void
            stopNotification(final long consequenceID, final long sourceID,
                    final long eventID, final Object configuration,
                    final Object context) {
        // We only want to respond if we initiated the event.
        if (sourceID == NotificationsDemo.NOTIFICATIONS_ID_1) {
            // Cancel the LED.
            LED.setState(LED.STATE_OFF);
            Alert.stopAudio();
            Alert.stopBuzzer();
        }
    }

    /**
     * It is likely that the following call will return a separate config object
     * for each SourceID, such as data that describes user set notification
     * settings. However, for this example, we return a trivial, arbitrary
     * config object.
     * 
     * @see net.rim.device.api.notification.Consequence#newConfiguration(long,long,byte,int,Object)
     */
    public Object newConfiguration(final long consequenceID,
            final long sourceID, final byte profileIndex, final int level,
            final Object context) {
        return CONFIG;
    }

    /**
     * Called when there is inbound (from the desktop) data to be converted to
     * object form.
     * 
     * @see net.rim.device.api.synchronization.SyncConverter#convert(DataBuffer,int,int)
     */
    public SyncObject convert(final DataBuffer data, final int version,
            final int UID) {
        // It's up to us to write and read the data. We apply a four byte type
        // and a 4 byte length, and then the raw data.
        try {
            final int type = data.readInt();
            final int length = data.readCompressedInt();

            if (type == TYPE) {
                final byte[] rawdata = new byte[length];
                data.readFully(rawdata);

                return new Configuration(rawdata);
            }
        }
        // We've prematurely reached the end of the DataBuffer.
        catch (final EOFException e) {
            System.err.println(e);
        }
        return null;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncConverter#convert(SyncObject,DataBuffer,int)
     */
    public boolean convert(final SyncObject object, final DataBuffer buffer,
            final int version) {
        boolean retval = false;

        if (object instanceof Configuration) {
            final Configuration c = (Configuration) object;
            buffer.writeInt(TYPE);
            buffer.writeCompressedInt(c._data.length);
            buffer.write(c._data);
            retval = true;
        }
        return retval;
    }
}
