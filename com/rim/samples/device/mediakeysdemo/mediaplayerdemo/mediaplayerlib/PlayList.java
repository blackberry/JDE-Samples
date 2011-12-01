/*
 * PlayList.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringComparator;

/**
 * This class copies the sound files from the compiled cod file into the device
 * file system. This allows for the tracks to be seekable.
 */
public class PlayList {
    /**
     * The names of the songs stored in the COD file as resources
     */
    private static final String[] SONG_FILENAMES = { "BlackBerryBold.mp3",
            "BlackBerryCurve.mp3", "BlackBerryPowerPlay.mp3",
            "BlackBerryStorm.mp3", };

    /**
     * Copies the sound files from the COD file to the device file system
     * 
     * @throws IOException
     *             If the operation fails
     */
    private static void init() throws IOException {
        final String[] dirURLs = getAudioFileSearchURLs();

        // Stores the exceptions thrown from any failed operations
        final Throwable[] exceptions = new Throwable[dirURLs.length];

        for (int dirURLIndex = 0; dirURLIndex < dirURLs.length; dirURLIndex++) {
            final String dirURL = dirURLs[dirURLIndex];
            if (dirURL == null) {
                continue;
            }

            Connection con;
            try {
                con = Connector.open(dirURL);
            } catch (final Exception e) {
                exceptions[dirURLIndex] = e;
                continue;
            }

            try {
                final FileConnection fcon = (FileConnection) con;
                if (!fcon.exists()) {
                    throw new IOException("directory does not exist");
                } else if (!fcon.isDirectory()) {
                    throw new IOException("not a directory");
                }

                for (int i = 0; i < SONG_FILENAMES.length; i++) {
                    final String filename = SONG_FILENAMES[i];
                    final String fileURL = fcon.getURL() + filename;

                    Connection con2;
                    try {
                        con2 = Connector.open(fileURL);
                    } catch (final Exception e) {
                        throw new IOException("unable to open connection to "
                                + fileURL + ": " + e);
                    }

                    try {
                        final FileConnection fcon2 = (FileConnection) con2;
                        if (fcon2.exists()) {
                            continue;
                        }

                        try {
                            fcon2.create();
                        } catch (final Exception e) {
                            throw new IOException("unable to create " + fileURL
                                    + ": " + e);
                        }

                        final String resourcePath = "/sounds/" + filename;
                        copyCODResourceToFileConnection(resourcePath, fcon2);
                    } finally {
                        try {
                            con2.close();
                        } catch (final Exception e) {
                        }
                    }
                }

                return;
            } catch (final Exception e) {
                exceptions[dirURLIndex] = e;
                continue;
            } finally {
                try {
                    con.close();
                } catch (final Exception e) {
                }
            }
        }

        // Generate a failure message to indicate what went wrong, then throw
        // an IOException.
        final StringBuffer sb = new StringBuffer();
        sb.append("no suitable locations for audio files found: ");
        for (int i = 0; i < dirURLs.length; i++) {
            if (i > 0) {
                sb.append("; ");
            }

            final String url = dirURLs[i];
            final Throwable exception = exceptions[i];
            sb.append(url).append(": ");

            if (exception instanceof IOException) {
                sb.append(exception.getMessage());
            } else {
                sb.append(exception.toString());
            }
        }

        final String failMessage = sb.toString();
        throw new IOException(failMessage);
    }

    /**
     * Copies the contents of a resource of the COD file in which this class is
     * located into a file on the filesystem.
     * 
     * @param resourcePath
     *            The path of the resource to copy - will be specified to
     *            Class.getResourceAsStream()
     * @param con
     *            The FileConnection representing the file to copy to. The file
     *            must already exist.
     * @throws IOException
     *             If the operation fails
     * @throws NullPointerException
     *             If any argument is null
     */
    private static void copyCODResourceToFileConnection(
            final String resourcePath, final FileConnection con)
            throws IOException {
        if (resourcePath == null) {
            throw new NullPointerException("resourcePath==null");
        } else if (con == null) {
            throw new NullPointerException("con==null");
        }

        final InputStream in = PlayList.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IOException("resource not found: " + resourcePath);
        }

        try {
            OutputStream out;
            try {
                out = con.openOutputStream();
            } catch (final Exception e) {
                throw new IOException("unable to open output stream to "
                        + con.getURL() + ": " + e);
            }

            try {
                final byte[] bytes = IOUtilities.streamToBytes(in);
                out.write(bytes);
            } catch (final Exception e) {
                throw new IOException("unable to copy data from "
                        + resourcePath + " to " + con.getURL() + ": " + e);
            } finally {
                try {
                    out.close();
                } catch (final Exception e) {
                }
            }
        } finally {
            try {
                in.close();
            } catch (final Exception e) {
            }
        }
    }

    /**
     * Returns the list of file:/// URLs to search for audio files
     * 
     * @return a newly-created array of strings whose values are the URLs of the
     *         folders on the BlackBerry's filesystem in which to search for
     *         audio files. The method never returns null but individual
     *         elements may be null.
     */
    private static String[] getAudioFileSearchURLs() {
        return new String[] {
                System.getProperty("fileconn.dir.memorycard.music"),
                System.getProperty("fileconn.dir.music"), };
    }

    /**
     * Searches the filesystem for entries to put in the playlist. This method
     * also extracts the audio files included in this COD file into the
     * filesystem, if they are not already there.
     * 
     * @return The list of playlist entries - never returns null.
     * @throws IOException
     *             If an error occurs.
     */
    public static PlaylistEntry[] getPlaylistEntries() throws IOException {
        // Copy the MP3 files from the COD file into the filesystem
        init();

        // Search the music folders for MP3 files to add to the playlist
        final Vector vector = new Vector();
        final String[] dirURLs = getAudioFileSearchURLs();

        for (int dirURLIndex = 0; dirURLIndex < dirURLs.length; dirURLIndex++) {
            final String dirURL = dirURLs[dirURLIndex];
            if (dirURL == null) {
                continue;
            }

            // Open a connection to the folder so that we can list its files
            Connection con;
            try {
                con = Connector.open(dirURL);
            } catch (final Exception e) {
                System.err.println("WARNING: unable to open connection to "
                        + dirURL + ": " + e);
                continue; // Skip this directory
            }

            try {
                final FileConnection fcon = (FileConnection) con;
                if (!fcon.isDirectory()) {
                    continue; // Not a directory, so skip it
                }

                // Search for files whose names end with ".mp3" and add them to
                // the playlist
                Enumeration enumeration;
                try {
                    enumeration = fcon.list();
                } catch (final Exception e) {
                    System.err.println("WARNING: unable to list files in "
                            + dirURL + ": " + e);
                    continue; // Skip this directory
                }

                while (enumeration.hasMoreElements()) {
                    final String filename = (String) enumeration.nextElement();
                    if (filename != null
                            && filename.toLowerCase().endsWith(".mp3")) {
                        final String fileURL = fcon.getURL() + filename;
                        final String name =
                                filename.substring(0, filename.length() - 4);
                        final PlaylistEntry playlistEntry =
                                new PlaylistEntry(fileURL, name);
                        vector.addElement(playlistEntry);
                    }
                }
            } finally {
                try {
                    con.close();
                } catch (final Exception e) {
                }
            }
        }

        // Put the PlaylistEntry objects for the discovered MP3 files into an
        // array
        final PlaylistEntry[] array = new PlaylistEntry[vector.size()];
        vector.copyInto(array);

        // Sort the array so that the songs are presented in alphabetical order
        Arrays.sort(array, PlaylistEntryComparator.INSTANCE);

        return array;
    }

    /**
     * A comparator that orders PlaylistEntry objects in ascending alphabetical
     * order by name.
     */
    private static class PlaylistEntryComparator implements Comparator {
        public static PlaylistEntryComparator INSTANCE =
                new PlaylistEntryComparator();

        private static final Comparator STRING_COMPARATOR = StringComparator
                .getInstance(true);

        /**
         * Compares two PlaylistEntry objects for order, based on their names.
         * 
         * @param o1
         *            The first PlaylistEntry object to compare
         * @param o2
         *            The second PlaylistEntry object to compare
         * @return A negative integer if the first PlaylistEntry should be
         *         ordered before the second, a positive integer if it should be
         *         ordered after the second, or 0 if they should be ordered in
         *         the same position.
         */
        public int compare(final Object o1, final Object o2) {
            final String name1 = ((PlaylistEntry) o1).getName();
            final String name2 = ((PlaylistEntry) o2).getName();
            return STRING_COMPARATOR.compare(name1, name2);
        }
    }
}
