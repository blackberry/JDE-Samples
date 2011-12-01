/*
 * SMS Server
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
 * a simple SMS Server implementation.
 * A loopback server that will return back to their source any messages sent to it.
 * For port bound messages, the source and dest port must be the same, that is, on the device, listen on the same
 * port as that use for sending a message and this server will send the received message back on that port
 */
public final class SMSServer implements Runnable
{
    //constants ---------------------------------------------------------------
    private static final String RESOURCES = "com/rim/samples/server/smsdemo/resources";
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

    //resource strings
    private static final String USAGE = "Usage";
    private static final String RECEIVED = "Received";
    private static final String SOURCE = "Source";
    private static final String DATA = "Data";
    private static final String DEST = "Dest";
    private static final String SENDING = "Sending";

    private static final int SMSPORT = 0x5345;

/*
    private static final int MAX_SMSPACKET_SIZE = 256;
    private static final int SIZE_OF_ADDRESS = 28;
*/
    private static final int MAX_SMSPACKET_SIZE = 424;
    private static final int SIZE_OF_ADDRESS = 44;


    private static final int SIZE_OF_HEADER = SIZE_OF_ADDRESS * 3 + 64;

    //members
    private volatile boolean _stop = false;
    private DatagramSocket _socket;



    //statics -----------------------------------------------------------------
    private static ResourceBundle _resources = ResourceBundle.getBundle(RESOURCES);

    public static void main(String[] args)
    {
	new SMSServer(args).run();
    }


    //inner classes -----------------------------------------------------------
    private static final class SMSAddress
    {
	//constants -----------------------------------------------------------
	private static final int INDEX_LENGTH = 4;
	private static final int INDEX_START_OF_ADDRESS = 8;

	//members -------------------------------------------------------------
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

    //constructors ------------------------------------------------------------
    public SMSServer(String[] args)
    {
	//nothing to do with args yet...
	if ( args != null & args.length > 0 )
	{
	    for (int i = 0; i < args.length; ++i)
	    {
		if ( args[0].equals("-testhex") )
		{
		    byte[] testdata = {(byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, (byte)0x90, (byte)0xAB, (byte)0xCD, (byte)0xEF,
		    		       (byte)0xAA, (byte)0xBB, (byte)0xCC, (byte)0xDD, (byte)0xEE, (byte)0xFF, (byte)0x11, (byte)0x22 };
		    printAsHex(testdata);
		}
	    }
	}
    }


    public void run()
    {
	System.out.println(INTRODUCTION);
	System.out.println(_resources.getString(USAGE));

	Thread t = new Thread() {
	    public void run()
	    {
		try {
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

		} catch (IOException e) {
		    System.err.println(e);
		    return;
		}
	    }
	};
	t.start();

	try {
	    while ( 'x' != System.in.read() )
	    {}
	} catch (IOException e) {
	    System.err.println(e);
	}

	_stop = true;
	_socket.close();
	try {
	    t.join();
	} catch (InterruptedException e) {
	    System.err.println(e);
	}
    }

    /**
     * Some simple parsing on the received datagram
     * @param p a received Datagram containing an SMS message
     */
    private void receivedSms(DatagramPacket p)
    {
	byte[] data = p.getData();
	//extract the source address
	SMSAddress src = new SMSAddress(data, 0, SIZE_OF_ADDRESS);
	String srcaddress = src.getAddress();
	SMSAddress dest = new SMSAddress(data, SIZE_OF_ADDRESS, SIZE_OF_ADDRESS); //destination is the second instance of this structure
	String destaddress = dest.getAddress();

	StringBuffer sb = new StringBuffer();
	sb.append(_resources.getString(RECEIVED) + "\n");
	sb.append(_resources.getString(SOURCE) + srcaddress + "\n");
	sb.append(_resources.getString(DEST) + destaddress + "\n");

	sb.append(_resources.getString(DATA) + "[" + p.getLength() + "]" + new String(data, SIZE_OF_HEADER, data.length - SIZE_OF_HEADER));

	System.out.println(sb.toString());

	System.out.println("Raw message:");
	printAsHex(data);
	System.out.println("");
    }

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
		//loop over the last 8 chars and print out as ascii
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

    String[] charmap = {"A", "B", "C", "D", "E", "F"};

    private String getChar(int nibble)
    {
	if ( nibble < 10 ) return Integer.toString(nibble);
	else if ( nibble < 16 ) return charmap[nibble - 10];
	throw new IllegalArgumentException("bad char: " + nibble);
    }


    /**
     * Send the message back to the source
     * @param p a received Datagram containing an SMS message
     */
    private void returnSms(DatagramPacket p) throws IOException
    {
	System.out.println(_resources.getString(SENDING));
	byte[] data = p.getData();
	SMSAddress src = new SMSAddress(data, 0, SIZE_OF_ADDRESS);
	SMSAddress dest = new SMSAddress(data, SIZE_OF_ADDRESS, SIZE_OF_ADDRESS); //destination is the second instance of this structure

	//swap the addresses
	byte[] srcBytes = src.getAddressBytes();
	src.setAddress(dest.getAddressBytes());
	dest.setAddress(srcBytes);


	//now swap them in the main data array
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


// if message has just one segment, then use following code to swap ports
/*

	byte p1=data[267];

	byte p2=data[268];
	byte p3=data[269];
	byte p4=data[270];

	data[267]=p3;
	data[268]=p4;
	data[269]=p1;
	data[270]=p2;

//*/


// if message has more than one segment, then use following code to swap ports
/*
	byte p1=data[272];
	byte p2=data[273];
	byte p3=data[274];
	byte p4=data[275];

	data[272]=p3;
	data[273]=p4;
	data[274]=p1;
	data[275]=p2;
//*/


	DatagramPacket returnpacket = new DatagramPacket(data, p.getLength());
	returnpacket.setAddress(p.getAddress());
	returnpacket.setPort(p.getPort());

	//dump to hex just for a check
  	System.out.println("sending:");
  	printAsHex(data);

	_socket.send(returnpacket);
    }
}
