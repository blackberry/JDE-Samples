/*
 * MyCryptoTokenData.java
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

package com.rim.samples.device.smartcarddriverdemo;

import net.rim.device.api.crypto.CryptoTokenPrivateKeyData;
import net.rim.device.api.smartcard.SmartCardID;
import net.rim.device.api.util.Persistable;

/**
 * Stores the location of the private key file on the smart card, as well as
 * which smart card the private key is stored on.
 */
final class MyCryptoTokenData implements CryptoTokenPrivateKeyData, Persistable {
    /**
     * The smart card the private key is stored on.
     */
    private final SmartCardID _id;

    /**
     * The file location of the private key on the smart card.
     */
    private final byte _file;

    /**
     * Constructs a <code>MyCryptoTokenData</code> object.
     * 
     * @param id
     *            which smart card contains the private key
     * @param file
     *            which file on the smart card contains the private key
     */
    public MyCryptoTokenData(final SmartCardID id, final byte file) {
        _id = id;
        _file = file;
    }

    /**
     * Get the <code>SmartCardID</code>, which indicates which smart card the
     * private key is stored on.
     * 
     * @return the <code>SmartCardID</code> of the smart card which the private
     *         key is stored on.
     */
    public SmartCardID getSmartCardID() {
        return _id;
    }

    /**
     * Gets the private key file location on the card.
     * 
     * @return the private key file location on the card.
     */
    public byte getFile() {
        return _file;
    }
}
