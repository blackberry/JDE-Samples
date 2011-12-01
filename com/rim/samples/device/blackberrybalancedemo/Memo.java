/**
 * Memo.java
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

package com.rim.samples.device.blackberrybalancedemo;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;

/**
 * Encapsulates the title and contents of a persistable memo. The memo also has
 * a service mode property which is UNDECIDED by default and may be changed
 * should the memo be exposed to data controlled by another service mode (e.g.
 * CORPORATE).
 */
public final class Memo implements Persistable {
    public static final int MEMO_NAME = 0;
    public static final int MEMO_CONTENT = 1;

    private String _mode;

    // Change this value if any fields are added to or removed from this class
    private static final int NUM_FIELDS = 2;

    private final Object[] _fields;

    /**
     * Creates a new Memo object
     * 
     * @param mode
     *            Indicates which mode this Memo should be associated with
     */
    public Memo(final String mode) {
        _mode = mode;

        _fields = new Object[NUM_FIELDS];

        for (int i = 0; i < NUM_FIELDS; ++i) {

            _fields[i] = "";
        }
    }

    /**
     * Retrieves an encoded object and returns it as a plaintext string
     * 
     * @param id
     *            The ID of the field from which the encoding should be
     *            retrieved
     * @return A plaintext string for the field corresponding to the given id or
     *         null if the device is locked
     */
    public String getField(final int id) {
        final Object encoding = _fields[id];

        // Acquiring a reference to a ticket guarantees access to encrypted data
        // even if the device locks during the decoding operation.
        final Object ticket = PersistentContent.getTicket();

        if (ticket != null) {
            return PersistentContent.decodeString(encoding);
        } else {
            return null;
        }
    }

    /**
     * Stores a string as an encoded object according to device content
     * protection/compression settings.
     * 
     * @param id
     *            The ID of the field where the encoding is to be stored
     * @param value
     *            The plaintext string to be encoded and stored
     */
    public void setField(final int id, final String value) {
        final Object encoding = PersistentContent.encode(value);
        _fields[id] = encoding;
    }

    /**
     * Forces a re-encoding of the information stored in this Memo object.
     * Callers of this method should obtain a ticket using
     * PersistentContent.getTicket().
     */
    public void reEncode() {
        for (int i = 0; i < NUM_FIELDS; ++i) {
            Object encoding = _fields[i];
            if (!PersistentContent.checkEncoding(encoding)) {
                encoding = PersistentContent.reEncode(encoding);
                _fields[i] = encoding;
            }
        }
    }

    /**
     * Sets the mode for this Memo
     * 
     * @param mode
     *            The mode to set
     */
    public void setMode(final String mode) {
        _mode = mode;
    }

    /**
     * Retrieves this Memo's mode
     */
    public String getMode() {
        return _mode;
    }
}
