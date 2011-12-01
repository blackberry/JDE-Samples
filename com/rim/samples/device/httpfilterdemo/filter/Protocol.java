/**
 * Protocol.java
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

package com.rim.samples.device.httpfilterdemo.filter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.FilterBaseInterface;

/**
 * An example of the HttpFilterRegistry apis. This class implements a simple
 * pass through mechanism that writes out the http response headers to
 * System.out
 */
public final class Protocol implements FilterBaseInterface, HttpConnection {
    private HttpConnection _subConnection;

    /**
     * This function will open a filtered Http Connection.
     * 
     * @see net.rim.device.api.io.FilterBaseInterface#openFilter(String, int,
     *      boolean)
     */
    public Connection openFilter(final String name, final int mode,
            final boolean timeouts) throws IOException {
        _subConnection =
                (HttpConnection) Connector.open("http:" + name
                        + ";usefilter=false", mode, timeouts);
        if (_subConnection != null) {
            return this;
        }

        // Failed to open the sub connection; so let us fail too.
        return null;
    }

    /**
     * @see javax.microedition.io.HttpConnection#getURL()
     */
    public String getURL() {
        return _subConnection.getURL();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getProtocol()
     */
    public String getProtocol() {
        return _subConnection.getProtocol();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getHost()
     */
    public String getHost() {
        return _subConnection.getHost();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getFile()
     */
    public String getFile() {
        return _subConnection.getFile();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getRef()
     */
    public String getRef() {
        return _subConnection.getRef();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getQuery()
     */
    public String getQuery() {
        return _subConnection.getQuery();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getPort()
     */
    public int getPort() {
        return _subConnection.getPort();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getRequestMethod()
     */
    public String getRequestMethod() {
        return _subConnection.getRequestMethod();
    }

    /**
     * @see javax.microedition.io.HttpConnection#setRequestMethod(String)
     */
    public void setRequestMethod(final String method) throws IOException {
        _subConnection.setRequestMethod(method);
    }

    /**
     * @see javax.microedition.io.HttpConnection#getRequestProperty(String)
     */
    public String getRequestProperty(final String key) {
        return _subConnection.getRequestProperty(key);
    }

    /**
     * @see javax.microedition.io.HttpConnection#setRequestProperty(String,
     *      String)
     */
    public void setRequestProperty(final String key, final String value)
            throws IOException {
        System.out.println("Request property <key, value>: " + key + ", "
                + value);
        _subConnection.setRequestProperty(key, value);
    }

    /**
     * @see javax.microedition.io.HttpConnection#getResponseCode()
     */
    public int getResponseCode() throws IOException {
        return _subConnection.getResponseCode();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getResponseMessage()
     */
    public String getResponseMessage() throws IOException {
        return _subConnection.getResponseMessage();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getExpiration()
     */
    public long getExpiration() throws IOException {
        return _subConnection.getExpiration();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getDate()
     */
    public long getDate() throws IOException {
        return _subConnection.getDate();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getLastModified()
     */
    public long getLastModified() throws IOException {
        return _subConnection.getLastModified();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getHeaderField(String)
     */
    public String getHeaderField(final String name) throws IOException {
        final String value = _subConnection.getHeaderField(name);
        System.out.println("Response property <key, value>: " + name + ", "
                + value);
        return value;
    }

    /**
     * @see javax.microedition.io.HttpConnection#getHeaderFieldInt(String, int)
     */
    public int getHeaderFieldInt(final String name, final int def)
            throws IOException {
        return _subConnection.getHeaderFieldInt(name, def);
    }

    /**
     * @see javax.microedition.io.HttpConnection#getHeaderFieldDate(String,
     *      long)
     */
    public long getHeaderFieldDate(final String name, final long def)
            throws IOException {
        return _subConnection.getHeaderFieldDate(name, def);
    }

    /**
     * @see javax.microedition.io.HttpConnection#getHeaderField(int)
     */
    public String getHeaderField(final int n) throws IOException {
        return _subConnection.getHeaderField(n);
    }

    /**
     * @see javax.microedition.io.HttpConnection#getHeaderFieldKey(int)
     */
    public String getHeaderFieldKey(final int n) throws IOException {
        return _subConnection.getHeaderFieldKey(n);
    }

    /**
     * @see javax.microedition.io.HttpConnection#getType()
     */
    public String getType() {
        return _subConnection.getType();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getEncoding()
     */
    public String getEncoding() {
        return _subConnection.getEncoding();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getLength()
     */
    public long getLength() {
        return _subConnection.getLength();
    }

    /**
     * @see javax.microedition.io.HttpConnection#openInputStream()
     */
    public InputStream openInputStream() throws IOException {
        return _subConnection.openInputStream();
    }

    /**
     * @see javax.microedition.io.HttpConnection#openDataInputStream()
     */
    public DataInputStream openDataInputStream() throws IOException {
        return _subConnection.openDataInputStream();
    }

    /**
     * @see javax.microedition.io.HttpConnection#openOutputStream()
     */
    public OutputStream openOutputStream() throws IOException {
        return _subConnection.openOutputStream();
    }

    /**
     * @see javax.microedition.io.HttpConnection#openDataOutputStream()
     */
    public DataOutputStream openDataOutputStream() throws IOException {
        return _subConnection.openDataOutputStream();
    }

    /**
     * @see javax.microedition.io.HttpConnection#close()
     */
    public void close() throws IOException {
        _subConnection.close();
    }

}
