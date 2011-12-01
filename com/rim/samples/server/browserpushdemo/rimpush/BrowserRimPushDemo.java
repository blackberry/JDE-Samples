/**
 * BrowserRimPushDemo.java
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

package com.rim.samples.server.browserpushdemo.rimpush;

import java.io.*;
import java.net.*;
import java.util.*;

public class BrowserRimPushDemo
{
    public static final String CHANNEL = "Browser-Channel";
    public static final String CHANNEL_DELETE = "Browser-Channel-Delete";
    private static final String PROPERTIES_FILE = "com/rim/samples/server/browserpushdemo/rimpush/rim_browserpush.properties";
    
    
    /**
     * Main method to read in properties file and invoke the push method.
     * 
     * @param args Command-line arguments (not used).
     */    
    public static void main( String[] args ) {
        // load the properties file
        Properties prop = new Properties();
        try {
            prop.load( new FileInputStream( PROPERTIES_FILE ) );
        } catch ( FileNotFoundException fnfe ) {
            throw new RuntimeException( "Properties file not found: " + fnfe.getMessage() );
        } catch ( IOException ioe ) {
            throw new RuntimeException( "Error reading properties file: " + ioe.getMessage() );
        }

        // The host name and port of Mobile Data Service to perform the push.
        String mdsHostName = prop.getProperty( "mdsHostName" );
        int mdsPort = Integer.parseInt( prop.getProperty( "mdsPort" ) );

        // Handheld email to which to push the page.
        String email = prop.getProperty( "email" );
        
        // The channel ID.
        String channelID = prop.getProperty( "channelID" );
        
        // The type of push.
        String pushType = prop.getProperty( "pushType" );
        
        // The title of the page to push.
        String pushTitle = prop.getProperty( "pushTitle" );

        // The actual page (and icons) to push.
        String contentUrlString = prop.getProperty( "contentUrlString" );
        String unreadIconUrl = prop.getProperty( "unreadIconUrl" );
        String readIconUrl = prop.getProperty( "readIconUrl" );

        // The reliability settings.
        String pushReliability = prop.getProperty( "pushReliability" );
        String notifyUrl = prop.getProperty( "notifyUrl" );
        
        // The push ID.
        String pushID = prop.getProperty( "pushID" );

        // Push the page to the handheld.
        pushPage( mdsHostName, mdsPort, email, channelID, contentUrlString, pushType,
                  pushTitle, unreadIconUrl, readIconUrl, pushReliability, notifyUrl, pushID );
    }
    
    
    /**
     * Pushes a webpage to a BlackBerry handheld.
     */
    public static void pushPage( String mdsHostName, int mdsPort, String email,
                                 String channelID, String contentUrlString,
                                 String pushType, String pushTitle,
                                 String unreadIconUrl, String readIconUrl,
                                 String pushReliability, String notifyUrl,
                                 String pushID ) {
        
        // Start the notification thread to receive push notifications from the MDS.
        new NotificationThread().start();
        
        // Two HttpURLConnections are used.  One connects to the content server to
        // retrieve the information to be pushed to the handheld.  The other connects
        // to the Mobile Data Service to deliver that information for pushing down
        // to the handheld.
        try {
            URL mdsUrl = new URL( "http", mdsHostName, mdsPort, "/push?DESTINATION=" + email + "&PORT=7874&REQUESTURI=/" );
            HttpURLConnection mdsConn = (HttpURLConnection) mdsUrl.openConnection();

            // Set additional header properties for the push.
            mdsConn.setRequestProperty( "Content-Location", contentUrlString );
            mdsConn.setRequestProperty( "X-Rim-Push-Title", pushTitle );
            mdsConn.setRequestProperty( "X-Rim-Push-Type", pushType );
            mdsConn.setRequestProperty( "X-Rim-Push-ID", pushID );
            if ( pushType.equals( CHANNEL ) || pushType.equals( CHANNEL_DELETE ) ) {
                mdsConn.setRequestProperty( "X-Rim-Push-Channel-ID", contentUrlString );
                if ( pushType.equals( CHANNEL ) ) {
                    mdsConn.setRequestProperty( "X-Rim-Push-Unread-Icon-URL", unreadIconUrl );
                    mdsConn.setRequestProperty( "X-Rim-Push-Read-Icon-URL", readIconUrl );
                }
            }
            mdsConn.setRequestProperty( "X-Rim-Push-Reliability", pushReliability );
            mdsConn.setRequestProperty( "X-Rim-Push-NotifyURL", notifyUrl );
            
            try {
                mdsConn.setRequestMethod( "POST" ); 
            } catch ( ProtocolException e ) {
                throw new RuntimeException( "Error setting request method: " + e.getMessage() ); 
            }
            
            mdsConn.setAllowUserInteraction( false );
            mdsConn.setDoInput( true );

            if ( pushType.equals( CHANNEL_DELETE ) ) {
                mdsConn.setDoOutput( false );
            } else {
                mdsConn.setDoOutput( true );
                // Channel is not being deleted, so get content from the content server.
                URL contentUrl;
                try {
                    contentUrl = new URL( contentUrlString );
                } catch ( MalformedURLException e ) {
                    throw new RuntimeException( "Invalid content URL: " + e.getMessage() );
                }
                HttpURLConnection contentConn = (HttpURLConnection) contentUrl.openConnection();
                contentConn.setAllowUserInteraction( false );
                contentConn.setDoInput( true );
                contentConn.setDoOutput( false );
                contentConn.setRequestMethod( "GET" );
                contentConn.connect();
    
                // Read the header properties from the push connection and 
                // write them to Mobile Data Service connection.
                String name;
                String value;
                for ( int i = 0; true; ++i ) {
                    name = contentConn.getHeaderFieldKey( i );
                    value = contentConn.getHeaderField( i );
                    if ( name == null && value == null ) break;
                    if ( name == null || value == null ) continue;
                    if ( name.equals( "X-Rim-Push-Type" ) ) continue;
                    if ( name.equals( "Transfer-Encoding" ) ) continue;
                    System.out.println( "Setting header property: " + name + " = " + value );
                    mdsConn.setRequestProperty( name, value );
                }

                // Read content from the push connection and write it to the 
                // MDS connection.
                //
                // NOTE: This step can be skipped (except for Browser-Content type)
                //       and then the browser will fetch the page itself.
                // NOTE: If the code is modified to push the same page to multiple
                //       devices, the content should be copied to a temporary file
                //       first to avoid multiple requests to the content URL.
                copyStreams( contentConn.getInputStream(), mdsConn.getOutputStream() );
            }
            System.out.println( "Connecting to " + mdsHostName + ':' + mdsPort );
            mdsConn.connect();
            int rescode = mdsConn.getResponseCode();
            if ( rescode != HttpURLConnection.HTTP_OK ) {
                throw new RuntimeException( "Cannot push data; received bad response code from Mobile Data Service: "
                                            + rescode + ", " + mdsConn.getResponseMessage() );
            }
            System.out.println( "Pushed page to the handheld." );
            
            // Display the values read from the properties file.
            displayProperties( mdsHostName, mdsPort, email, channelID, pushType, pushTitle, contentUrlString,
                unreadIconUrl, readIconUrl, pushReliability, notifyUrl, pushID );
        } catch ( IOException e ) {
            throw new RuntimeException( "Cannot push page: " + e.toString() );
        }
    }
    
    
    /**
     * Displays the values read from the properties file.
     */
    private static void displayProperties( String mdsHostName, int mdsPort,
                                           String email, String channelID,
                                           String pushType, String pushTitle,
                                           String contentUrlString, String unreadIconUrl,
                                           String readIconUrl, String pushReliability,
                                           String notifyUrl, String pushID ) {
        System.out.println( "\n------------------------------------" );
        System.out.println( "\nProperties:\n" );
        System.out.println( "mdsHostName = " + mdsHostName );
        System.out.println( "mdsPort = " + mdsPort );
        System.out.println( "email = " + email );
        System.out.println( "channelID = " + channelID );
        System.out.println( "pushType = " + pushType );
        System.out.println( "pushTitle = " + pushTitle );
        System.out.println( "contentUrlString = " + contentUrlString );
        System.out.println( "unreadIconUrl = " + unreadIconUrl );
        System.out.println( "readIconUrl = " + readIconUrl );
        System.out.println( "pushReliability = " + pushReliability );
        System.out.println( "notifyUrl = " + notifyUrl );
        System.out.println( "pushID = " + pushID );
    }
    
    
    /**
     * Method to read data from the input stream and copy it to the output stream.
     * 
     * @param ins The input stream to copy from.
     * @param outs The output stream to copy to.
     */
    private static void copyStreams( InputStream ins, OutputStream outs ) throws IOException {
        int maxRead = 1024;
        byte [] buffer = new byte[1024];
        int bytesRead;

        for ( ; ; ) {
            bytesRead = ins.read( buffer );
            if ( bytesRead <= 0 ) break;
            outs.write( buffer, 0, bytesRead );
        }
    }
    
    
    /**
     * Thread that receives push notifications from the MDS.
     */
    private static class NotificationThread extends Thread
    {
        private static final int NOTIFY_PORT = 7778;
        
        
        /**
         * Receives push notification data from the MDS and displays it on screen.
         */
        public void run() 
        {
            try {
                System.out.println( "Waiting for notification on port " + NOTIFY_PORT + "..." );
                while ( true ) {
                    ServerSocket serverSocket = new ServerSocket( NOTIFY_PORT );
                    serverSocket.setSoTimeout( 120000 );
                    try {
                        Socket clientSocket = serverSocket.accept();
                        InputStream input = clientSocket.getInputStream();
                        StringBuffer buffer = new StringBuffer();
                        int byteRead = input.read();
                        while ( byteRead != -1 && input.available() > 0 ) {
                            buffer.append( (char) byteRead );
                            byteRead = input.read();
                        }
                        clientSocket.close();
                    
                        // Display the push notification received from the MDS.
                        System.out.println( "\n------------------------------------" );
                        System.out.println( "\nPush notification received from MDS:" );
                        System.out.println ( '\n' + buffer.toString() );
                        break;  // received notification...thread's work is done
                    } catch ( SocketTimeoutException ste ) {
                        System.out.println( "Notification connection timeout.  Restarting..." );
                    }               
                    serverSocket.close();
                }
            } catch ( Exception exception ) {
                exception.printStackTrace();
            }
        }
    }
}
