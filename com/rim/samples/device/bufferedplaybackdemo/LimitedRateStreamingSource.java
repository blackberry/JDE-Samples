/*
 * LimitedRateStreaminSource.java
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

package com.rim.samples.device.bufferedplaybackdemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.Control;
import javax.microedition.media.protocol.ContentDescriptor;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

import net.rim.device.api.io.SharedInputStream;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.TextField;

/**
 * The data source used by the BufferedPlayback's media player.
 */
public final class LimitedRateStreamingSource extends DataSource {
    /** The max size to be read from the stream at one time. */
    private static final int READ_CHUNK = 512; // bytes

    /** A reference to the field which displays the load status. */
    private TextField _loadStatusField;

    /** A reference to the field which displays the player status. */
    private TextField _playStatusField;

    /**
     * The minimum number of bytes that must be buffered before the media file
     * will begin playing.
     */
    private int _startBuffer = 200000;

    /** The maximum size (in bytes) of a single read. */
    private int _readLimit = 32000;

    /**
     * The minimum forward byte buffer which must be maintained in order for the
     * video to keep playing. If the forward buffer falls below this number, the
     * playback will pause until the buffer increases.
     */
    private int _pauseBytes = 64000;

    /**
     * The minimum forward byte buffer required to resume playback after a
     * pause.
     */
    private int _resumeBytes = 128000;

    /** The stream connection over which media content is passed. */
    private ContentConnection _contentConnection;

    /** An input stream shared between several readers. */
    private SharedInputStream _readAhead;

    /** A stream to the buffered resource. */
    private LimitedRateSourceStream _feedToPlayer;

    /** The MIME type of the remote media file. */
    private String _forcedContentType;

    /** A counter for the total number of buffered bytes */
    private volatile int _totalRead;

    /** A flag used to tell the connection thread to stop */
    private volatile boolean _stop;

    /**
     * A flag used to indicate that the initial buffering is complete. In other
     * words, that the current buffer is larger than the defined start buffer
     * size.
     */
    private volatile boolean _bufferingComplete;

    /** A flag used to indicate that the remote file download is complete. */
    private volatile boolean _downloadComplete;

    /** The thread which retrieves the remote media file. */
    private ConnectionThread _loaderThread;

    /** The local save file into which the remote file is written. */
    private FileConnection _saveFile;

    /** A stream for the local save file. */
    private OutputStream _saveStream;

    /**
     * Constructor.
     * 
     * @param locator
     *            The locator that describes the DataSource.
     */
    LimitedRateStreamingSource(final String locator) {
        super(locator);
    }

    /**
     * Open a connection to the locator.
     * 
     * @throws IOException
     */
    public void connect() throws IOException {
        // Open the connection to the remote file.
        _contentConnection =
                (ContentConnection) Connector
                        .open(getLocator(), Connector.READ);

        // Cache a reference to the locator.
        final String locator = getLocator();

        // Report status.
        System.out.println("Loading: " + locator);
        System.out.println("Size: " + _contentConnection.getLength());

        // The name of the remote file begins after the last forward slash.
        final int filenameStart = locator.lastIndexOf('/');

        // The file name ends at the first instance of a semicolon.
        int paramStart = locator.indexOf(';');

        // If there is no semicolon, the file name ends at the end of the line.
        if (paramStart < 0) {
            paramStart = locator.length();
        }

        // Extract the file name.
        final String filename = locator.substring(filenameStart, paramStart);
        System.out.println("Filename: " + filename);

        // Open a local save file with the same name as the remote file.
        _saveFile =
                (FileConnection) Connector.open(
                        "file:///SDCard/blackberry/music" + filename,
                        Connector.READ_WRITE);

        // If the file doesn't already exist, create it.
        if (!_saveFile.exists()) {
            _saveFile.create();
        }

        // Open the file for writing.
        _saveFile.setReadable(true);

        // Open a shared input stream to the local save file to
        // allow many simultaneous readers.
        final SharedInputStream fileStream =
                SharedInputStream.getSharedInputStream(_saveFile
                        .openInputStream());

        // Begin reading at the beginning of the file.
        fileStream.setCurrentPosition(0);

        // If the local file is smaller than the remote file...
        if (_saveFile.fileSize() < _contentConnection.getLength()) {
            // Did not get the entire file, set the system to try again.
            _saveFile.setWritable(true);

            // A non-null save stream is used as a flag later to indicate that
            // the file download was incomplete.
            _saveStream = _saveFile.openOutputStream();

            // Use a new shared input stream for buffered reading.
            _readAhead =
                    SharedInputStream.getSharedInputStream(_contentConnection
                            .openInputStream());
        } else {
            // The download is complete.
            _downloadComplete = true;

            // We can use the initial input stream to read the buffered media.
            _readAhead = fileStream;

            // We can close the remote connection.
            _contentConnection.close();
        }

        if (_forcedContentType != null) {
            // Use the user-defined content type if it is set.
            _feedToPlayer =
                    new LimitedRateSourceStream(_readAhead, _forcedContentType);
        } else {
            // Otherwise, use the MIME types of the remote file.
            _feedToPlayer =
                    new LimitedRateSourceStream(_readAhead, _contentConnection
                            .getType());
        }
    }

    /**
     * Destroy and close all existing connections.
     */
    public void disconnect() {
        try {
            if (_saveStream != null) {
                // Destroy the stream to the local save file.
                _saveStream.close();
                _saveStream = null;
            }

            // Close the local save file.
            _saveFile.close();

            if (_readAhead != null) {
                // Close the reader stream.
                _readAhead.close();
                _readAhead = null;
            }

            // Close the remote file connection.
            _contentConnection.close();

            // Close the stream to the player.
            _feedToPlayer.close();
        } catch (final Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Returns the content type of the remote file.
     * 
     * @return The content type of the remote file.
     */
    public String getContentType() {
        return _feedToPlayer.getContentDescriptor().getContentType();
    }

    /**
     * Returns a stream to the buffered resource.
     * 
     * @return A stream to the buffered resource.
     */
    public SourceStream[] getStreams() {
        return new SourceStream[] { _feedToPlayer };
    }

    /**
     * Starts the connection thread used to download the remote file.
     */
    public void start() throws IOException {
        // If the save stream is null, we have already completely downloaded
        // the file.
        if (_saveStream != null) {
            // Open the connection thread to finish downloading the file.
            _loaderThread = new ConnectionThread();
            _loaderThread.start();
        }
    }

    /**
     * Stop the connection thread.
     */
    public void stop() throws IOException {
        // Set the boolean flag to stop the thread.
        _stop = true;
    }

    /**
     * @see javax.microedition.media.Controllable#getControl(String)
     */
    public Control getControl(final String controlType) {
        // No implemented Controls.
        return null;
    }

    /**
     * @see javax.microedition.media.Controllable#getControls()
     */
    public Control[] getControls() {
        // No implemented Controls.
        return null;
    }

    /**
     * Force the lower level stream to a given content type. Must be called
     * before the connect function in order to work.
     * 
     * @param contentType
     *            The content type to use.
     */
    void setContentType(final String contentType) {
        _forcedContentType = contentType;
    }

    /**
     * A stream to the buffered media resource.
     */
    private final class LimitedRateSourceStream implements SourceStream {
        /** A stream to the local copy of the remote resource. */
        private final SharedInputStream _baseSharedStream;

        /** Describes the content type of the media file. */
        private final ContentDescriptor _contentDescriptor;

        /**
         * Constructor. Creates a LimitedRateSourceStream from the given
         * InputStream.
         * 
         * @param inputStream
         *            The input stream used to create a new reader.
         * @param contentType
         *            The content type of the remote file.
         */
        LimitedRateSourceStream(final InputStream inputStream,
                final String contentType) {
            _baseSharedStream =
                    SharedInputStream.getSharedInputStream(inputStream);
            _contentDescriptor = new ContentDescriptor(contentType);
        }

        /**
         * Returns the content descriptor for this stream.
         * 
         * @return The content descriptor for this stream.
         */
        public ContentDescriptor getContentDescriptor() {
            return _contentDescriptor;
        }

        /**
         * Returns the length provided by the connection.
         * 
         * @return long The length provided by the connection.
         */
        public long getContentLength() {
            return _contentConnection.getLength();
        }

        /**
         * Returns the seek type of the stream.
         */
        public int getSeekType() {
            return SEEKABLE_TO_START;
        }

        /**
         * Returns the maximum size (in bytes) of a single read.
         */
        public int getTransferSize() {
            return _readLimit;
        }

        /**
         * Writes bytes from the buffer into a byte array for playback.
         * 
         * @param bytes
         *            The buffer into which the data is read.
         * @param off
         *            The start offset in array b at which the data is written.
         * @param len
         *            The maximum number of bytes to read.
         * @return the total number of bytes read into the buffer, or -1 if
         *         there is no more data because the end of the stream has been
         *         reached.
         * @throws IOException
         */
        public int read(final byte[] bytes, final int off, final int len)
                throws IOException {
            System.out.println("Read Request for: " + len + " bytes");

            // Limit bytes read to our readLimit.
            int readLength = len;
            if (readLength > getReadLimit()) {
                readLength = getReadLimit();
            }

            // The number of available byes in the buffer.
            int available;

            // A boolean flag indicating that the thread should pause
            // until the buffer has increased sufficiently.
            boolean paused = false;

            for (;;) {
                available = _baseSharedStream.available();

                if (_downloadComplete) {
                    // Ignore all restrictions if downloading is complete.
                    System.out.println("Complete, Reading: " + len
                            + " - Available: " + available);
                    return _baseSharedStream.read(bytes, off, len);
                } else if (_bufferingComplete) {
                    if (paused && available > getResumeBytes()) {
                        // If the video is paused due to buffering, but the
                        // number of available byes is sufficiently high,
                        // resume playback of the media.
                        System.out
                                .println("Resuming - Available: " + available);
                        updatePlayStatus("Resuming " + available + " Bytes");
                        paused = false;
                        return _baseSharedStream.read(bytes, off, readLength);
                    } else if (!paused
                            && (available > getPauseBytes() || available > readLength)) {
                        // We have enough information for this media playback.

                        if (available < getPauseBytes()) {
                            // If the buffer is now insufficient, set the
                            // pause flag.
                            paused = true;
                            updatePlayStatus("Pausing " + available + " Bytes");
                        }

                        System.out.println("Reading: " + readLength
                                + " - Available: " + available);
                        return _baseSharedStream.read(bytes, off, readLength);
                    } else if (!paused) {
                        // Set pause until loaded enough to resume.
                        paused = true;
                        updatePlayStatus("Pausing " + available + " Bytes");
                    }
                } else {
                    // We are not ready to start yet, try sleeping to allow the
                    // buffer to increase.
                    try {
                        Thread.sleep(500);
                    } catch (final Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }

        /**
         * @see javax.microedition.media.protocol.SourceStream#seek(long)
         */
        public long seek(final long where) throws IOException {
            _baseSharedStream.setCurrentPosition((int) where);
            return _baseSharedStream.getCurrentPosition();
        }

        /**
         * @see javax.microedition.media.protocol.SourceStream#tell()
         */
        public long tell() {
            return _baseSharedStream.getCurrentPosition();
        }

        /**
         * Close the stream.
         * 
         * @throws IOException
         */
        void close() throws IOException {
            _baseSharedStream.close();
        }

        /**
         * @see javax.microedition.media.Controllable#getControl(String)
         */
        public Control getControl(final String controlType) {
            // No implemented controls.
            return null;
        }

        /**
         * @see javax.microedition.media.Controllable#getControls()
         */
        public Control[] getControls() {
            // No implemented controls.
            return null;
        }
    }

    /**
     * A thread which downloads the remote file and writes it to the local file.
     */
    private final class ConnectionThread extends Thread {
        /**
         * Download the remote media file, then write it to the local file.
         * 
         * @see java.lang.Thread#run()
         */
        public void run() {
            try {
                final byte[] data = new byte[READ_CHUNK];
                int len = 0;
                updateLoadStatus("Buffering");

                // Until we reach the end of the file.
                while (-1 != (len = _readAhead.read(data))) {
                    _totalRead += len;

                    updateLoadStatus(_totalRead + " Bytes");

                    if (!_bufferingComplete && _totalRead > getStartBuffer()) {
                        // We have enough of a buffer to begin playback.
                        _bufferingComplete = true;
                        System.out.println("Initial Buffering Complete");
                        updateLoadStatus("Buffering Complete");
                    }

                    if (_stop) {
                        // Stop reading.
                        return;
                    }

                }

                System.out.println("Downloading Complete");
                updateLoadStatus("Done " + _totalRead + " Bytes");
                System.out.println("Total Read: " + _totalRead);

                // If the downloaded data is not the same size
                // as the remote file, something is wrong.
                if (_totalRead != _contentConnection.getLength()) {
                    System.err.println("* Unable to Download entire file *");
                }

                _downloadComplete = true;
                _readAhead.setCurrentPosition(0);

                // Write downloaded data to the local file.
                while (-1 != (len = _readAhead.read(data))) {
                    _saveStream.write(data);
                }

            } catch (final Exception e) {
                System.err.println(e.toString());
            }
        }
    }

    /**
     * Gets the minimum forward byte buffer which must be maintained in order
     * for the video to keep playing.
     * 
     * @return The pause byte buffer.
     */
    int getPauseBytes() {
        return _pauseBytes;
    }

    /**
     * Sets the minimum forward buffer which must be maintained in order for the
     * video to keep playing.
     * 
     * @param pauseBytes
     *            The new pause byte buffer.
     */
    void setPauseBytes(final int pauseBytes) {
        _pauseBytes = pauseBytes;
    }

    /**
     * Gets the maximum size (in bytes) of a single read.
     * 
     * @return The maximum size (in bytes) of a single read.
     */
    int getReadLimit() {
        return _readLimit;
    }

    /**
     * Sets the maximum size (in bytes) of a single read.
     * 
     * @param readLimit
     *            The new maximum size (in bytes) of a single read.
     */
    void setReadLimit(final int readLimit) {
        _readLimit = readLimit;
    }

    /**
     * Gets the minimum forward byte buffer required to resume playback after a
     * pause.
     * 
     * @return The resume byte buffer.
     */
    int getResumeBytes() {
        return _resumeBytes;
    }

    /**
     * Sets the minimum forward byte buffer required to resume playback after a
     * pause.
     * 
     * @param resumeBytes
     *            The new resume byte buffer.
     */
    void setResumeBytes(final int resumeBytes) {
        _resumeBytes = resumeBytes;
    }

    /**
     * Gets the minimum number of bytes that must be buffered before the media
     * file will begin playing.
     * 
     * @return The start byte buffer.
     */
    int getStartBuffer() {
        return _startBuffer;
    }

    /**
     * Sets the minimum number of bytes that must be buffered before the media
     * file will begin playing.
     * 
     * @param startBuffer
     *            The new start byte buffer.
     */
    void setStartBuffer(final int startBuffer) {
        _startBuffer = startBuffer;
    }

    /**
     * Gets a reference to the text field where load status updates are written.
     * 
     * @return The load status text field.
     */
    TextField getLoadStatus() {
        return _loadStatusField;
    }

    /**
     * Sets a reference to the text field where load status updates are written.
     * 
     * @param loadStatus
     *            The new load status text field.
     */
    void setLoadStatus(final TextField loadStatus) {
        _loadStatusField = loadStatus;
    }

    /**
     * Gets a reference to the text field where player status updates are
     * written.
     * 
     * @return The player status text field.
     */
    TextField getPlayStatus() {
        return _playStatusField;
    }

    /**
     * Sets a reference to the text field where player status updates are
     * written.
     * 
     * @param playStatus
     *            The new player status text field.
     */
    void setPlayStatus(final TextField playStatus) {
        _playStatusField = playStatus;
    }

    /**
     * Update the player status field.
     * 
     * @param text
     *            The new message to be displayed.
     */
    void updatePlayStatus(final String text) {
        updateStatus(getPlayStatus(), text);
    }

    /**
     * Update the load status field.
     * 
     * @param text
     *            The new message to be displayed.
     */
    void updateLoadStatus(final String text) {
        updateStatus(getLoadStatus(), text);
    }

    /**
     * Update a given status field.
     * 
     * @param field
     *            The field to be updated.
     * @param text
     *            The message to be displayed in the field.
     */
    void updateStatus(final TextField field, final String text) {
        synchronized (Application.getEventLock()) {
            field.setText(text);
        }
    }
}
