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

package com.rim.samples.device.httpfilterdemo.precanned;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.FilterBaseInterface;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.util.StringUtilities;

/**
 * A custom Connection protocol. This class provides precanned html content. The
 * PackageManager class in this sample project registers a filter to filter URLs
 * containing a fully qualified domain name of na.blackberry.com for use with
 * this protocol. The class name must be "Protocol", as this name is appended to
 * the package name registered in PackageManager so as to create an instance of
 * this class.
 */
public final class Protocol implements FilterBaseInterface, HttpConnection {
    private String _url;
    private String _requestMethod;
    private HttpHeaders _requestHeaders;
    private HttpHeaders _responseHeaders;
    private byte[] _resultData;

    private final static byte[] PAGE_DATA =
            "<html><body>This is a simple page that contains two links to precanned <a href=\"http://na.blackberry.com/eng/developers/italic.html\">Italic</a> and <a href=\"http://na.blackberry.com/eng/developers/bold.html\">Bold</a> content.</body></html>"
                    .getBytes();
    private final static byte[] BOLD_PAGE_DATA =
            "<html><body>This is a simple page that contains <b>bold</b> content.</body></html>"
                    .getBytes();
    private final static byte[] ITALIC_PAGE_DATA =
            "<html><body>This is a simple page that contains <i>italic</i> content.</body></html>"
                    .getBytes();

    /**
     * This method will open a filtered Http Connection.
     * 
     * @see net.rim.device.api.io.FilterBaseInterface#openFilter(String, int,
     *      boolean)
     */
    public Connection openFilter(final String name, final int mode,
            final boolean timeouts) throws IOException {
        _url = name.substring(2);
        _requestHeaders = new HttpHeaders();
        _responseHeaders = new HttpHeaders();
        _responseHeaders.setProperty(HttpProtocolConstants.HEADER_CONTENT_TYPE,
                "text/html");

        // Attempt to parse for the file name
        final int slashIndex = name.lastIndexOf('/');
        if (slashIndex != -1) {

            // There is a slash - now get the path and file name and match
            // against predefined strings.
            final String file = name.substring(slashIndex + 1);
            if (StringUtilities.startsWithIgnoreCase(file, "italic.html")) {
                _resultData = ITALIC_PAGE_DATA;
            } else if (StringUtilities.startsWithIgnoreCase(file, "bold.html")) {
                _resultData = BOLD_PAGE_DATA;
            }
        }

        if (_resultData == null) {
            // We haven't found a match, return default page
            _resultData = PAGE_DATA;
        }

        return this;
    }

    /**
     * @see javax.microedition.io.HttpConnection#getURL()
     */
    public String getURL() {
        return "http://" + _url;
    }

    /**
     * @see javax.microedition.io.HttpConnection#getProtocol()
     */
    public String getProtocol() {
        return "http";
    }

    /**
     * @see javax.microedition.io.HttpConnection#getHost()
     */
    public String getHost() {
        return "www.blackberry.net";
    }

    /**
     * @see javax.microedition.io.HttpConnection#getFile()
     */
    public String getFile() {
        return "";
    }

    /**
     * @see javax.microedition.io.HttpConnection#getRef()
     */
    public String getRef() {
        return "";
    }

    /**
     * @see javax.microedition.io.HttpConnection#getQuery()
     */
    public String getQuery() {
        return "";
    }

    /**
     * @see javax.microedition.io.HttpConnection#getPort()
     */
    public int getPort() {
        return 80;
    }

    /**
     * @see javax.microedition.io.HttpConnection#getRequestMethod()
     */
    public String getRequestMethod() {
        return _requestMethod == null ? "GET" : _requestMethod;
    }

    /**
     * @see javax.microedition.io.HttpConnection#setRequestMethod(String)
     */
    public void setRequestMethod(final String method) throws IOException {
        _requestMethod = method;
    }

    /**
     * @see javax.microedition.io.HttpConnection#getRequestProperty(String)
     */
    public String getRequestProperty(final String key) {
        return _requestHeaders.getPropertyValue(key);
    }

    /**
     * @see javax.microedition.io.HttpConnection#setRequestProperty(String,
     *      String)
     */
    public void setRequestProperty(final String key, final String value)
            throws IOException {
        _requestHeaders.setProperty(key, value);
    }

    /**
     * @see javax.microedition.io.HttpConnection#getResponseCode()
     */
    public int getResponseCode() throws IOException {
        return 200;
    }

    /**
     * @see javax.microedition.io.HttpConnection#getResponseMessage()
     */
    public String getResponseMessage() throws IOException {
        return "OK";
    }

    /**
     * @see javax.microedition.io.HttpConnection#getExpiration()
     */
    public long getExpiration() throws IOException {
        final String value =
                getHeaderField(HttpProtocolConstants.HEADER_EXPIRES);
        if (value != null) {
            try {
                return HttpDateParser.parse(value);
            } catch (final Exception e) {
            }
        }

        return 0;
    }

    /**
     * @see javax.microedition.io.HttpConnection#getDate()
     */
    public long getDate() throws IOException {
        final String value = getHeaderField(HttpProtocolConstants.HEADER_DATE);
        if (value != null) {
            try {
                return HttpDateParser.parse(value);
            } catch (final Exception e) {
            }
        }

        return 0;
    }

    /**
     * @see javax.microedition.io.HttpConnection#getLastModified()
     */
    public long getLastModified() throws IOException {
        // Return current time.
        return System.currentTimeMillis();
    }

    /**
     * @see javax.microedition.io.HttpConnection#getHeaderField(String)
     */
    public String getHeaderField(final String name) throws IOException {
        return _responseHeaders.getPropertyValue(name);
    }

    /**
     * @see javax.microedition.io.HttpConnection#getHeaderFieldInt(String, int)
     */
    public int getHeaderFieldInt(final String name, final int def)
            throws IOException {
        final String value = _responseHeaders.getPropertyValue(name);
        try {
            if (value != null) {
                return Integer.parseInt(value);
            }
        } catch (final NumberFormatException e) {
        }

        return def;
    }

    /**
     * @see javax.microedition.io.HttpConnection#getHeaderFieldDate(String,
     *      long)
     */
    public long getHeaderFieldDate(final String name, final long def)
            throws IOException {
        final String value = _responseHeaders.getPropertyValue(name);

        try {
            if (value != null) {
                return HttpDateParser.parse(value);
            }
        } catch (final Exception e) {
        }

        return def;
    }

    /**
     * @see javax.microedition.io.HttpConnection#getHeaderField(int)
     */
    public String getHeaderField(final int n) throws IOException {
        return _responseHeaders.getPropertyValue(n);
    }

    /**
     * @see javax.microedition.io.HttpConnection#getHeaderFieldKey(int)
     */
    public String getHeaderFieldKey(final int n) throws IOException {
        return _responseHeaders.getPropertyKey(n);
    }

    /**
     * @see javax.microedition.io.HttpConnection#getType()
     */
    public String getType() {
        try {
            return getHeaderField(HttpProtocolConstants.HEADER_CONTENT_TYPE);
        } catch (final IOException e) {
            return null;
        }
    }

    /**
     * @see javax.microedition.io.HttpConnection#getEncoding()
     */
    public String getEncoding() {
        try {
            return getHeaderField(HttpProtocolConstants.HEADER_CONTENT_ENCODING);
        } catch (final IOException e) {
            return null;
        }
    }

    /**
     * @see javax.microedition.io.HttpConnection#getLength()
     */
    public long getLength() {
        try {
            return getHeaderFieldInt(
                    HttpProtocolConstants.HEADER_CONTENT_LENGTH, -1);
        } catch (final IOException e) {
        }

        return -1;
    }

    /**
     * @see javax.microedition.io.HttpConnection#openInputStream()
     */
    public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(_resultData);
    }

    /**
     * @see javax.microedition.io.HttpConnection#openDataInputStream()
     */
    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    /**
     * @see javax.microedition.io.HttpConnection#openOutputStream()
     */
    public OutputStream openOutputStream() throws IOException {
        return new ByteArrayOutputStream();
    }

    /**
     * @see javax.microedition.io.HttpConnection#openDataOutputStream()
     */
    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(openOutputStream());
    }

    /**
     * @see javax.microedition.io.HttpConnection#close()
     */
    public void close() throws IOException {
    }
}
