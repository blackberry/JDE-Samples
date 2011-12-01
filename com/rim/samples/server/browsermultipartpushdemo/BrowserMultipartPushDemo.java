/*
 * BrowserMultipartPushDemo.java
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
package com.rim.samples.server.browsermultipartpushdemo;

import java.net.*;
import java.io.*;
import java.util.*;

import javax.mail.MessagingException;
import javax.mail.internet.*;

/**
 * Application which pushes a specified web page to a specified device.
 */
public class BrowserMultipartPushDemo {

    public static final String CHANNEL = "Browser-Channel";
    public static final String CONTENT = "Browser-Content";

    private static final String PROPERTIES_FILE = "com/rim/samples/server/browsermultipartpushdemo/browserpush.properties";

    public BrowserMultipartPushDemo() {
    }

    /**
     * main method which reads the property file and performs the push.
     * <p>Modify the properties file to specify the page and email address to push to, bes location, and additional properties</p>
     */
    public static void main(String[] args) {

        // load the properties file
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(PROPERTIES_FILE));
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException("property file not found: " + fnfe.getMessage());
        } catch (IOException ioe) {
            throw new RuntimeException("problems reading property file: " + ioe.getMessage());
        }

        // Hostname and Port of the BlackBerry Enterprise Server (BES) which will perform the push
        String besHostName = prop.getProperty("besHostName").trim();
        int besPort = (new Integer(prop.getProperty("besPort").trim())).intValue();

        // The Device PIN to push the page to
        String email = prop.getProperty("email");

        // The URL of the page to push
        String pushTitle = prop.getProperty("pushTitle", "Push Page").trim();

        // URLs of the icons to use for Browser-Channel push type
        String unreadIconUrl = prop.getProperty("unreadIconUrl", "").trim();
        String readIconUrl = prop.getProperty("readIconUrl", "").trim();

        // Priority of the push
        String pushPriority = prop.getProperty("pushPriority");

        // push the page to the device
        pushPage(besHostName, besPort, email, pushTitle, unreadIconUrl, readIconUrl, pushPriority);
    }

    /**
     * Pushes a web page to a device.
     */
    public static void pushPage(String besHostName, int besPort, String email, String pushTitle, String unreadIconUrl, String readIconUrl, String pushPriority) {

        System.out.println("besHostName = " + besHostName);
        System.out.println("besPort = " + besPort);
        System.out.println("email = " + email);
        System.out.println("pushTitle = " + pushTitle);
        System.out.println("pushPriority = " + pushPriority);

        HttpURLConnection besConn;
        
        URL pushUrl, besUrl;
        String pushUrlString = "cache://example.com/MultipartExample";

        try {
            /* push listener thread on the device listens to port 7874 for pushes from the bes */
            besUrl = new URL("http", besHostName, besPort, "/push?DESTINATION=" + email + "&PORT=7874&REQUESTURI=/"); 
            System.out.println("BES URL: " + besUrl.toString());
            besConn = (HttpURLConnection)besUrl.openConnection();
    
            // We are going to set up a multipart push that contains a channel push and a multipart content push
            MimeMultipart multipartResponse = new MimeMultipart();
            String type = multipartResponse.getContentType();
            type = type.replace('\r', ' ');
            type = type.replace('\n', ' ');
            besConn.setRequestProperty("content-type", type);
            besConn.setRequestProperty("X-RIM-Transcode-Content", "*/*");

            {
                Object[] content = getMultipartContent();
                byte[] data = (byte[])content[1];
                String dataContentType = (String)content[0];
                // Add the content push item
                InternetHeaders headers = new InternetHeaders();
                // write the content location, length and type
                headers.setHeader("Content-Location", pushUrlString);
                headers.setHeader("X-RIM-Push-Type", CONTENT);
                headers.setHeader("X-RIM-Transcode-Content", "*/*");
                headers.setHeader("Content-Type", dataContentType);
                headers.setHeader("content-length", Integer.toString(data.length));
                
                // add the body part
                multipartResponse.addBodyPart(new MimeBodyPart(headers, data));
            }
            
            {
                // Add the channel push item
                InternetHeaders headers = new InternetHeaders();
                // write the content location, length and type
                headers.setHeader("Content-Location", pushUrlString);
                headers.setHeader("X-RIM-Push-Title", pushTitle);
                headers.setHeader("X-RIM-Push-Type", CHANNEL);
                headers.setHeader("X-RIM-Push-Channel-ID", pushUrlString);
                headers.setHeader("X-RIM-Transcode-Content", "*/*");
                headers.setHeader("content-length", Integer.toString(0));
                
                // add the body part
                multipartResponse.addBodyPart(new MimeBodyPart(headers, new byte[0]));
            }
            
            if (pushPriority != null) {
                besConn.setRequestProperty("X-RIM-Push-Priority", pushPriority);
            }
            try {
                besConn.setRequestMethod("POST"); 
            } catch (ProtocolException e) {
                throw new RuntimeException("problems setting request method: " + e.getMessage());
            }
            
            besConn.setAllowUserInteraction(false);
            besConn.setDoInput(true);
            besConn.setDoOutput(true);
            OutputStream outs = besConn.getOutputStream();
            multipartResponse.writeTo(outs);
            outs.close();

            System.out.println("connecting to bes " + besHostName + ":" + besPort);
            besConn.connect();
            System.out.println("getting response code");
            int rescode = besConn.getResponseCode();
            if (rescode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Unable to push page, received bad response code from BES:" + rescode);
            }
            System.out.println("pushed page to device");
        } catch (IOException e) {
            throw new RuntimeException("Unable to push page:" + e.toString());
        } catch (MessagingException me) {
            throw new RuntimeException("Unable to push page:" + me.toString());
        }
    
    
    }

    /**
     * Return some multipart content
     **/
    private static final Object[] getMultipartContent() throws IOException, MessagingException
    {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        // We are going to coax java's mail API to prepare Multipart for us
        MimeMultipart multipartResponse = new MimeMultipart();
        String type = multipartResponse.getContentType();
        type = type.replace('\r', ' ');
        type = type.replace('\n', ' ');

        // prepare html that references each image; because the images are coming in the cache later
        StringBuffer htmlOutput = new StringBuffer("<html>There should be a number of images in the cache when this is pushed<br>");
        File imageDir = new File("com/rim/samples/server/browsermultipartpushdemo/images");
        File[] imageFiles = imageDir.listFiles();
        for (int i=0; i < imageFiles.length; ++i) {
            htmlOutput.append("<img src=\"" + imageFiles[i].getName() + "\"> Image " + i + "<br>" );
        }
        
        htmlOutput.append("</html>");

        // Write the first body element
        byte[] content = htmlOutput.toString().getBytes();
        InternetHeaders headers = new InternetHeaders();
        headers.setHeader("content-length", Integer.toString(content.length));
        headers.setHeader("content-type", "text/html");
        headers.setHeader("X-RIM-Transcode-Content", "*/*");
        MimeBodyPart mainContent = new MimeBodyPart(headers, content);
        multipartResponse.addBodyPart(mainContent);
        
        // Now for all files in a particular directory append them as body parts
        for (int i=0; i < imageFiles.length; ++i) {
            FileInputStream fileIn = new FileInputStream(imageFiles[i]);
            if (fileIn != null) {
                try {
                    
                    // just read what is available
                    int avail = fileIn.available();
                    byte[] bytes = new byte[avail];
                    fileIn.read(bytes);
                    
                    headers = new InternetHeaders();
                    
                    // write the content location, length and type
                    headers.setHeader("content-location", imageFiles[i].getName());
                    headers.setHeader("content-length", Integer.toString(bytes.length));
                    headers.setHeader("content-type", "image/gif");
                    headers.setHeader("X-RIM-Transcode-Content", "*/*");
                    
                    // add the body part
                    mainContent = new MimeBodyPart(headers, bytes);
                    multipartResponse.addBodyPart(mainContent);
                } finally {
                    fileIn.close();
                }
            }
        }
        
        // write out the result
        multipartResponse.writeTo(bytesOut);
        
        // We return the result as a string and the data
        Object[] result = new Object[2];
        result[0] = type;
        result[1] = bytesOut.toByteArray();
        return result;
    }
} 
