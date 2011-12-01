/**
 * EchoServer.java
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

package com.rim.samples.server.communicationapidemo;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


/**
 * A class to echo the body of an HTTP request back as the HTTP response. The URL
 * to access the HTTPServer is "http://[YOUR_IP]:8105/JSON".
 */
public final class EchoServer
{

    public final static int RESPONSE_DELAY = 1; 
    public static int reqCounter = 1;  
    private static DateFormat df = new SimpleDateFormat("HH:mm:ss MM/dd/yy");
    private static int port = 8105; // Server port 

    /**
     * Entry point
     */
    public static void main(String[] args) throws IOException
    {
        final InetSocketAddress addr;
        final HttpServer server;

        String address = InetAddress.getLocalHost().getHostAddress();

        addr = new InetSocketAddress(address, port);

        server = HttpServer.create(addr, 10);

        server.createContext("/ATOM", new GenericHandler("public_timeline.atom"));
        server.createContext("/RSS", new GenericHandler("public_timeline.rss"));
        server.createContext("/SOAP", new GenericHandler("simple_soap.xml"));
        server.createContext("/JSON", new GenericHandler("public_timeline.json"));
        server.createContext("/XML", new GenericHandler("public_timeline.xml"));

        server.createContext("/TEXT", new TEXTHandler());
        server.createContext("/TEXT2", new TEXTHandler());

        server.start();

        System.out.println("Server started on IP: " + address + ":" + port + " at " + df.format(new Date()));
        String serverUri = "http://" + address + ":" + port;

        String statusInfo = String.format("\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s", "Listening for requests:", serverUri + "/ATOM", serverUri
                        + "/RSS", serverUri + "/SOAP", serverUri + "/JSON", serverUri + "/XML", serverUri + "/TEXT", serverUri + "/TEXT2");
        System.out.println(statusInfo);
        System.out.println("Responses will be sent with a delay of " + RESPONSE_DELAY + " sec");
    }


    /**
     * Reads a file and returns its contents in a byte array
     * 
     * @param file File to read
     * @return byte array of file contents
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file) throws IOException
    {        
        InputStream is = new FileInputStream(file);

        System.out.println("\tReading file:" + file.getName());

        // Get the size of the file
        long length = file.length();        

        
         //Before converting length to an int type, check to ensure that file is
         // not larger than Integer.MAX_VALUE.         
        if(length > Integer.MAX_VALUE)
        {
            System.out.println("\tFile is too large to process");
            return null;
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while((offset < bytes.length) && ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0))
        {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if(offset < bytes.length)
        {
            throw new IOException("\tCould not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }
}


class GenericHandler implements HttpHandler
{

    private String path = "com\\rim\\samples\\server\\communicationapidemo\\";
    private String _fileName;
    private DateFormat df = new SimpleDateFormat("HH:mm:ss MM/dd/yy");


    public GenericHandler(String fileName)
    {
        _fileName = fileName;
    }


    public void handle(HttpExchange t) throws IOException
    {
        final OutputStream os;
        String response;
        System.out.println("\n>> incoming request [id: " + EchoServer.reqCounter++ + ", time: " + df.format(new Date()) + "]");

        byte[] fileArray = EchoServer.getBytesFromFile(new File(path + _fileName));
        response = "";
        if(fileArray != null)
        {
            for(int i = 0; i < fileArray.length; ++i)
            {
                response += (char) fileArray[i];
            }
        }

        try
        {
            System.out.print("\tWaiting " + EchoServer.RESPONSE_DELAY + " sec ...");
            Thread.sleep(EchoServer.RESPONSE_DELAY * 1000);
            System.out.println("DONE");
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        t.sendResponseHeaders(HTTP_OK, response.length());

        os = t.getResponseBody();

        os.write(response.getBytes());

        os.close();
        t.close();
        System.out.println("<< response [length = " + response.length() + ", time: " + df.format(new Date()) + "]");
        System.out.flush();
    }
}


class TEXTHandler implements HttpHandler
{
    private DateFormat df = new SimpleDateFormat("HH:mm:ss MM/dd/yy");


    public void handle(HttpExchange t) throws IOException
    {
        final InputStream is;
        final OutputStream os;
        StringBuilder buf;
        int b;
        final String request, response;
        System.out.println("\n>> incoming request [id: " + EchoServer.reqCounter++ + ", time: " + df.format(new Date()) + "]");
        buf = new StringBuilder();        

        is = t.getRequestBody();

        while((b = is.read()) != -1)
        {
            buf.append((char) b);
        }

        is.close();
        response = buf.toString();
        
        /*
         * Now send the response. We could have instead done this
         * dynamically, using 0 as the response size (forcing chunked encoding)
         * and writing the bytes of the response directly to the OutputStream,
         * but building the String first allows us to know the exact length so
         * we can send a response with a known size.
         */
        try
        {
            System.out.print("\tWaiting " + EchoServer.RESPONSE_DELAY + " sec ...");
            Thread.sleep(EchoServer.RESPONSE_DELAY * 1000);
            System.out.println("DONE");
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        t.sendResponseHeaders(HTTP_OK, response.length());

        os = t.getResponseBody();

        os.write(response.getBytes());        

        os.close();
        t.close();
        
        System.out.println("<< response [length = " + response.length() + ", time: " + df.format(new Date()) + "]");
        System.out.flush();
    }
}
