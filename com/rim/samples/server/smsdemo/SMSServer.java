/*
 * SMSServer.java
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

package com.rim.samples.server.smsdemo;

import java.net.*;
import java.util.*;
import java.io.*;

/**
 * A simple SMS loopback server that will return to the source any
 * messages sent to it. For port bound messages, the source and destination
 * port must be the same. On the device, listen on the same port used for
 * sending a message and this server will send the received message back
 * on that port.
 */
public final class SMSServer implements Runnable
{
    // Constants ---------------------------------------------------------------
    private static final String RESOURCES = "com/rim/samples/server/smsdemo/resources";
    private static final String INTRODUCTION = "SMS Server";   

    private static final int SMSPORT = 0x5345;

    private static final int MAX_SMSPACKET_SIZE = 424;
    private static final int SIZE_OF_ADDRESS = 44;
    
    private static final int ADDRESS_SEGMENT_LENGTH = 60;

    private static final int PAYLOAD_INDEX = 335;
    
    // Resource strings
    private static final String USAGE = "Usage";
    private static final String RECEIVED = "Received";
    private static final String SOURCE = "Source";
    private static final String DATA = "Data";
    private static final String DEST = "Dest";
    private static final String SENDING = "Sending";
    private static final String RAW = "Raw";

    // Members
    private volatile boolean _stop = false;
    private DatagramSocket _socket;
    private String[] _charmap = {"A", "B", "C", "D", "E", "F"};
    
    // Statics -----------------------------------------------------------------
    private static ResourceBundle _resources = ResourceBundle.getBundle(RESOURCES);

    /**
     * Entry point
     * @param Command line args (not used)
     */
    public static void main(String[] args)
    {    	
    	new SMSServer();
    }

    // Constructor
    SMSServer()
    {
	run();
    }

    /**
     * This inner class represents an SMS address
     */
    private static final class SMSAddress
    {
	// Constants -----------------------------------------------------------
	private static final int INDEX_LENGTH = 4;
	private static final int INDEX_START_OF_ADDRESS = 8;		
	
	// Members -------------------------------------------------------------
	private byte[] _data;
	private int _length;
	
	public SMSAddress(byte[] data)
	{
	    this(data, 0, data.length);
	}
	
	public SMSAddress(byte[] data, int start, int length)
	{
	    _data = new byte[length];
	    for (int i = 0; i < length; ++i)
	    {
	    	_data[i] = data[start + i];
	    }
	
	    _length = data[INDEX_LENGTH];
	}
	
	public String getAddress()
	{
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < _length; ++i)
	    {
	    	sb.append(Byte.toString(_data[INDEX_START_OF_ADDRESS + i]));
	    }
	    return sb.toString();
	}
	
	public byte[] getAddressBytes()
	{
	    byte[] address = new byte[_length];
	    for (int i = 0; i < _length; ++i)
	    {
		address[i] = _data[INDEX_START_OF_ADDRESS + i];
	    }
	    return address;
	}
	
	public void setAddress(byte[] address)
	{
	    _data[INDEX_LENGTH] = (byte)address.length;
	    for (int i = 0; i < address.length; ++i)
	    {
		_data[INDEX_START_OF_ADDRESS + i] = address[i];
	    }
	}
	
	public byte[] getBytes()
	{
	    return _data;
	}
    }
    
    /**
     * Runs a thread that listens for incoming messages
     */
    public void run()
    {
	System.out.println(INTRODUCTION);
	System.out.println(_resources.getString(USAGE));
	
	Thread t = new Thread() {
	    public void run()
	    {
		try
		{
		    _socket = new DatagramSocket(SMSPORT );
		    System.out.println("Listening on port:"+SMSPORT);
		
		    byte[] data = new byte[MAX_SMSPACKET_SIZE];
	
		    while (!_stop)
		    {
			DatagramPacket p = new DatagramPacket(data, data.length);
			_socket.receive(p);
			receivedSms(p);
			returnSms(p);
			Arrays.fill(data, (byte)0);
		    }		
		}
		catch (IOException e)
		{
		    System.err.println(e);
		    return;
		}
	    }
	};
	t.start();

	try
	{
	    while ( 'x' != System.in.read() )
	    {}
	}
	catch (IOException e)
	{
	    System.err.println(e);
	}
	
	_stop = true;
	_socket.close();
	try
	{
	    t.join();
	}
	catch (InterruptedException e)
	{
	    System.err.println(e);
	}
    }

    /**
     * Some simple parsing on the received datagram
     * @param p A received Datagram containing an SMS message
     */
    private void receivedSms(DatagramPacket p)
    {
	byte[] data = p.getData();
	
	// Extract the source address
	SMSAddress src = new SMSAddress(data, 0, SIZE_OF_ADDRESS);
	String srcaddress = src.getAddress();
		
	// Extract the destination address
	SMSAddress dest = new SMSAddress(data, ADDRESS_SEGMENT_LENGTH, SIZE_OF_ADDRESS ); 
	String destaddress = dest.getAddress();
	
	StringBuffer sb = new StringBuffer();
	sb.append(_resources.getString(RECEIVED) + "\n");
	sb.append(_resources.getString(SOURCE) + srcaddress + "\n");
	sb.append(_resources.getString(DEST) + destaddress + "\n");
	
	sb.append(_resources.getString(DATA) + new String(data, PAYLOAD_INDEX, p.getLength() - PAYLOAD_INDEX));
	
	System.out.println(sb.toString());
        System.out.println("\n");
        
    
	System.out.println(_resources.getString(RAW));
        System.out.println("\n");

	printAsHex(data);
	System.out.println("\n");
    }
    
    /**
     * Utility method to print data to the console
     * @param data The data to print
     */
    private void printAsHex(byte[] data)
    {
	for (int i = 1; i < data.length + 1; ++i)
	{
	    byte b = data[i-1];
	    String octet = Integer.toString((int)b, 16);
	    octet = getChar((b>>4)&0x0f);
	    octet += getChar(b&0xf);
	    System.out.print(octet + " ");

	    if ( i % 8 == 0 )
	    {
		// Loop over the last 8 chars and print out as ascii
		System.out.print("\t");
		for (int j = i-7; j <= i; ++j)
		{
		    char c = (char)data[j-1];
		    System.out.print(c > 0x0032 && c < 0x00FF ? c : '.');
		}
		System.out.println();
	    }
	}
    }
    
    /**
     *Returns a string representation of a hex value
     */
    private String getChar(int nibble)
    {
	if ( nibble < 10 )
	{
       	    return Integer.toString(nibble);
	}
	else if( nibble < 16 )
	{
	    return _charmap[nibble - 10];
	}
	throw new IllegalArgumentException("bad char: " + nibble);
    }


    /**
     * Send the message back to the source
     * @param p A received Datagram containing an SMS message
     */
    private void returnSms(DatagramPacket p) throws IOException
    {
	System.out.println(_resources.getString(SENDING));
	byte[] data = p.getData();
	SMSAddress src = new SMSAddress(data, 0, SIZE_OF_ADDRESS);
	SMSAddress dest = new SMSAddress(data, ADDRESS_SEGMENT_LENGTH, SIZE_OF_ADDRESS); 
	
	// Swap the addresses
	byte[] srcBytes = src.getAddressBytes();
	src.setAddress(dest.getAddressBytes());
	dest.setAddress(srcBytes);
	
	
	// Now swap them in the main data array
	byte[] srcbytes = src.getBytes();
	for (int i = 0; i < srcbytes.length; ++i)
	{
	    data[i] = srcbytes[i];
	}
	
	byte[] destbytes = dest.getBytes();
	for (int i = 0; i < destbytes.length; ++i)
	{
	    data[i + SIZE_OF_ADDRESS] = destbytes[i];
	}
	
	// Set the user data header present flag to true in case it has been corrupted
	data[232] = 1;	
		
	DatagramPacket returnpacket = new DatagramPacket(data, p.getLength());
	returnpacket.setAddress(p.getAddress());
	returnpacket.setPort(p.getPort());
	
	//Dump to hex just for a check
	System.out.println("\n");
	printAsHex(data);
	
	_socket.send(returnpacket);
    }
}
