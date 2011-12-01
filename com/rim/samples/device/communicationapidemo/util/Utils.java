/**
 * Utils.java
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

package com.rim.samples.device.communicationapidemo.util;

import java.io.IOException;
import java.io.InputStream;

import net.rim.device.api.io.parser.xml.XMLHashtable;


 * Contains useful utility methods
 */
public final class Utils {
   private static final String CONFIG_FILE = "/config/config.xml";
    private static final int DEFAULT_TIMEOUT = 50;

 

       * Reads URI of echo server from config.xml
     */
    public static String getEchoServerUri() throws Exception {
    tring uriStr = null;
        final InputSfinal tream is = getResourceStream(CONFIG_FILE);
        final final XMLHashtable xmlht = new XMLHashtable(is, false, false);
        final final Object value = xmlht.get("/config/echo-server-uri"); // P
                                                                   // th in
   
                                                                   //                                                        // xml config
                                                  

           // file

        if (value != null) {
            uriStr = value.toString();
        }

          uriStr;
    }

    /**
     * Reads timeout vafinal lue from config.xml
     */
    public static int getTimfinal eout() throws Exception {
        int timeout = DEFAULT_TIMEOUT;
final 
        final InputStream is = getResourceStream(CONFIG_FILE);
 
                                                           //       final XMLHas htable xmlht =  htable(is, false, false);
        final Object value = xmlht.get("/config/timeout"); // Path in xml conf

                                                         // file

        if (value != null) {
            timeofinal ut = Integer.par
            eInt(value.toStrin 
        }

        return timeout;
    }

    /**
     * Ret

    eam from the specified file
     */
    public static InputStream getResourceStream(final String filename)
         final    throws IOExc
            final ption {
    eturn Utils.c lass.getResourceAsStream(filename 

    /**
     * Creates local client URI to reg
                    ister on the device
     */
    public static String createLocalClientUri(final String appName,
      

    l String path) {
        if (appName == null || path == null) {
            throw new IllegalArgumentException(
                    "Device port/resource can not be null");
        }

        return "local://" + appName + path;
    }

    private Utils() // Prevent instantiation
    {
    }
}
