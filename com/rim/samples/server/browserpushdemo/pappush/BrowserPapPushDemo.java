/**
 * BrowserPapPushDemo.java
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

package com.rim.samples.server.browserpushdemo.pappush;

import java.io.*;
import java.net.*;
import java.util.*;

public class BrowserPapPushDemo
{
    private static final String CHANNEL = "Browser-Channel";
    private static final String CHANNEL_DELETE = "Browser-Channel-Delete";
    private static final String PROPERTIES_FILE = "com/rim/samples/server/browserpushdemo/pappush/pap_browserpush.properties";
    private static final String BOUNDARY = "asdlfkjiurwghasf";
    
    // possible push commands
    private static final String CANCEL = "cancel";
    private static final String PUSH = "push";
    private static final String REPLACE = "replace";
    private static final String STATUS = "status";

    
    /**
     * Main method that reads the properties file and performs the push.
     */
    public static void main( String[] args ) {
        // Load the properties file.
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
        
        // The push command.
        String pushCommand = prop.getProperty( "pushCommand" );
        
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
        
        // The push ID to replace.
        String replaceID = prop.getProperty( "replaceID" );
        
        String papFilename = null;
        if ( pushCommand.equals( CANCEL ) ) {
            papFilename = "com/rim/samples/server/browserpushdemo/pappush/pap_cancel.txt";
        } else if ( pushCommand.equals( PUSH ) ) {
            papFilename = "com/rim/samples/server/browserpushdemo/pappush/pap_push.txt";
        } else if ( pushCommand.equals( REPLACE ) ) {
            papFilename = "com/rim/samples/server/browserpushdemo/pappush/pap_replace.txt";
        } else if ( pushCommand.equals( STATUS ) ) {
            papFilename = "com/rim/samples/server/browserpushdemo/pappush/pap_status.txt";
        }
        
        // Push the page to the handheld.
        pushPage( mdsHostName, mdsPort, email, channelID, pushCommand, pushType, pushTitle, contentUrlString,
                  unreadIconUrl, readIconUrl, pushReliability, notifyUrl, pushID, replaceID, papFilename );
    }
    

    /**
     * Pushes a webpage to a BlackBerry handheld.
     */
    public static void pushPage( String mdsHostName, int mdsPort,
                                 String email, String channelID,
                                 String pushCommand, String pushType,
                                 String pushTitle, String contentUrlString,
                                 String unreadIconUrl, String readIconUrl,
                                 String pushReliability, String notifyUrl,
                                 String pushID, String replaceID,
                                 String papFilename ) {
        
        // Start the notification thread to receive push notifications from the MDS.
        new NotificationThread().start();
        
        try {
            // Push listener thread on the device listens to port 7874 for pushes from the Mobile Data Service.
            URL mdsUrl = new URL( "http", mdsHostName, mdsPort, "/pap" ); 
            HttpURLConnection mdsConn = (HttpURLConnection) mdsUrl.openConnection();
            if ( pushCommand.equals( PUSH ) || pushCommand.equals( REPLACE ) ) {
                mdsConn.setRequestProperty( "Content-Type", "multipart/related; type=\"application/xml\"; boundary=" + BOUNDARY );
            } else {
                mdsConn.setRequestProperty( "Content-Type", "application/xml" );
            }
            
            try {
                mdsConn.setRequestMethod( "POST" ); 
            } catch ( ProtocolException e ) {
                throw new RuntimeException( "Error setting request method: " + e.getMessage() );
            }

            mdsConn.setAllowUserInteraction( false );
            mdsConn.setDoInput( true );
            mdsConn.setDoOutput( true );
            
            // Get the pap file as one contiguous string so we can replace portions of it.
            InputStream ins = new BufferedInputStream( new FileInputStream( papFilename ) );
            ByteArrayOutputStream bouts = new ByteArrayOutputStream();
            copyStreams( ins, bouts );
            String output = new String( bouts.toByteArray() );
            
            // Replace all the placeholders in the pap file with actual data.
            output = output.replaceAll( "\\$\\(pushid\\)", pushID );
            output = output.replaceAll( "\\$\\(email\\)", replaceSpecialEmailChars( email ) );
            if ( pushCommand.equals( PUSH ) || pushCommand.equals( REPLACE ) ) {
                String headers = getContentHeaders( channelID, pushType, pushTitle,
                                                    contentUrlString, unreadIconUrl, readIconUrl,
                                                    pushReliability, notifyUrl );
                String content = getContent( contentUrlString );
                output = output.replaceAll( "\\$\\(boundary\\)", BOUNDARY );
                output = output.replaceAll( "\\$\\(headers\\)", headers );
                output = output.replaceAll( "\\$\\(content\\)", content );
                if ( pushCommand.equals( REPLACE ) ) {
                    output = output.replaceAll( "\\$\\(replaceid\\)", replaceID );
                }
            }
            output = output.replaceAll( "\r\n", "EOL" );
            output = output.replaceAll( "\n", "EOL" );
            output = output.replaceAll( "EOL", "\r\n" );
            
            // Copy the modified pap file to the MDS output stream.
            copyStreams( new ByteArrayInputStream( output.getBytes() ), mdsConn.getOutputStream() );
            System.out.println( "Connecting to: " + mdsHostName + ':' + mdsPort );
            mdsConn.connect();
            
            int rescode = mdsConn.getResponseCode();
            if ( rescode != HttpURLConnection.HTTP_ACCEPTED ) {
                throw new RuntimeException( "Cannot push data; received bad response code from Mobile Data Service: "
                                            + rescode + ", " + mdsConn.getResponseMessage() );
            }
            System.out.println( "Pushed page to handheld." );
            
            // Display the values read from the properties file.
            displayProperties( mdsHostName, mdsPort, email, channelID, pushCommand, pushType, pushTitle, contentUrlString,
                unreadIconUrl, readIconUrl, pushReliability, notifyUrl, pushID, replaceID, papFilename );
            
            // Display the data sent to the MDS.
            System.out.println( "\n------------------------------------" );
            System.out.println( "\nData sent to MDS:" );
            System.out.println ( '\n' + output );
            
            // Display the response from the MDS.
            bouts = new ByteArrayOutputStream();
            copyStreams( mdsConn.getInputStream(), bouts );
            output = new String( bouts.toByteArray() );
            System.out.println( "\n------------------------------------" );
            System.out.println( "\nMDS response:" );
            System.out.println ( '\n' + output );
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to send message: " + e.toString() );
        }
    }
    
    
    private static String getContentHeaders( String channelID, String pushType,
                                             String pushTitle, String contentUrlString,
                                             String unreadIconUrl, String readIconUrl,
                                             String pushReliability, String notifyUrl ) {
        StringBuffer contentHeaders = new StringBuffer();
        contentHeaders.append( "Content-Type: " ).append( "text/html" );
        contentHeaders.append( "\r\nX-Rim-Push-Title: " ).append( pushTitle );
        contentHeaders.append( "\r\nX-Rim-Push-Type: " ).append( pushType );
        if ( pushType.equals( CHANNEL ) || pushType.equals( CHANNEL_DELETE ) ) {
            contentHeaders.append( "\r\nX-Rim-Push-Channel-ID: " ).append( channelID );
            if ( pushType.equals( CHANNEL ) ) {
                contentHeaders.append( "\r\nContent-Location: " ).append( contentUrlString );
                contentHeaders.append( "\r\nX-Rim-Push-Unread-Icon-URL: " ).append( unreadIconUrl );
                contentHeaders.append( "\r\nX-Rim-Push-Read-Icon-URL: " ).append( readIconUrl );
            }
        }
        contentHeaders.append( "\r\nX-Rim-Push-Reliability: " ).append( pushReliability );
        contentHeaders.append( "\r\nX-Rim-Push-NotifyURL: " ).append( notifyUrl );
        
        return contentHeaders.toString();
    }
   

    /**
     * Retrieves the push content.
     * 
     * @param contentUrlString The URL from which to retrieve push content.
     * @return The push content.
     */
    private static String getContent( String contentUrlString ) throws IOException {
        URL contentUrl;
        HttpURLConnection contentConn;
        
        try {
            contentUrl = new URL( contentUrlString );
        } catch ( MalformedURLException e ) {
            throw new RuntimeException( "Invalid push URL: " + e.getMessage() );
        }
        contentConn = (HttpURLConnection) contentUrl.openConnection();
        contentConn.setAllowUserInteraction( false );
        contentConn.setDoInput( true );
        contentConn.setDoOutput( false );
        contentConn.setRequestMethod( "GET" );
        contentConn.connect();
        
        // Save content from the content connection.
        InputStream ins = contentConn.getInputStream();
        ByteArrayOutputStream bouts = new ByteArrayOutputStream();
        copyStreams( ins, bouts );
        ins.close();
        
        return new String( bouts.toByteArray() );
    }
    
    
    /**
     * Replaces special email characters with a format understood by the MDS.
     * 
     * @param email The original email address.
     * @return The MDS-encoded email address.
     */
    private static String replaceSpecialEmailChars( String email ) {
        StringBuffer buffer = new StringBuffer();
        for ( int i = 0; i < email.length(); ++i ) {
            char aChar = email.charAt( i );
            // non-alphanumeric characters other than '+', '-', '.' and '_' must
            // be converted to their hexadecimal representations with a leading '%'
            if ( ! ( Character.isLetterOrDigit( aChar ) || aChar == '+'
                     || aChar == '-' || aChar == '.' || aChar == '_' ) ) {
                buffer.append( '%' ).append( Integer.toHexString( (int) aChar ) );
            } else {
                buffer.append( aChar );
            }
        }
        
        return buffer.toString();
    }
    
    
    /**
     * Displays the values read from the properties file.
     */
    private static void displayProperties( String mdsHostName, int mdsPort,
                                    String email, String channelID,
                                    String pushCommand, String pushType,
                                    String pushTitle, String contentUrlString,
                                    String unreadIconUrl, String readIconUrl,
                                    String pushReliability, String notifyUrl,
                                    String pushID, String replaceID,
                                    String papFilename ) {
        System.out.println( "\n------------------------------------" );
        System.out.println( "\nProperties:\n" );
        System.out.println( "mdsHostName = " + mdsHostName );
        System.out.println( "mdsPort = " + mdsPort );
        System.out.println( "email = " + email );
        System.out.println( "channelID = " + channelID );
        System.out.println( "pushCommand = " + pushCommand );
        System.out.println( "pushType = " + pushType );
        System.out.println( "pushTitle = " + pushTitle );
        System.out.println( "contentUrlString = " + contentUrlString );
        System.out.println( "unreadIconUrl = " + unreadIconUrl );
        System.out.println( "readIconUrl = " + readIconUrl );
        System.out.println( "pushReliability = " + pushReliability );
        System.out.println( "notifyUrl = " + notifyUrl );
        System.out.println( "pushID = " + pushID );
        System.out.println( "replaceID = " + replaceID );
        System.out.println( "papFilename = " + papFilename );
    }


    /** 
     * Reads data from the input stream and copies it to the output stream.
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
                        System.out.println( "------------------------------------" );
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
