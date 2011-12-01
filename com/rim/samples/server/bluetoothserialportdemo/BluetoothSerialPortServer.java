/*
 * BluetoothSerialPortServer.java
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

package com.rim.samples.server.bluetoothserialportdemo ;

import java.io.*;
import java.util.*;
import javax.comm.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * The server side of BluetoothSerialPortDemo. Note that the available Bluetooth services are 
 * exchanged when the devices pair. This means that you'll need the server side application up
 * and listening for a serial port connection prior to pairing your BlackBerry with your PC, 
 * in order to connect from the BlackBerry to your PC.
 */
public class BluetoothSerialPortServer extends JFrame 
{

    private static final String COM_PORT = "COM4"; 

    // These constants have to match the constants in the device code.
    private static final int INSERT = 1;
    private static final int REMOVE = 2;
    private static final int CHANGE = 3;
    private static final int JUST_OPEN = 4;
    private static final int CONTENTS = 5;
    private static final int NO_CONTENTS = 6;

    private SerialPort _serialPort;
    private DataInputStream _din;
    private DataOutputStream _dout;

    private JPanel _panel;
    private JTextArea _textArea;


    public static void main(String[] args) 
    {
        new BluetoothSerialPortServer().setVisible(true);    
    }
	
    public BluetoothSerialPortServer() 
    {
        initComponents();
        initSerialConnection();
        pack();
        setLocation(100,100);
    }

    private class MyDocumentListener implements DocumentListener 
    {
        public void insertUpdate(DocumentEvent e) 
        {
            int offset = e.getOffset();
            int changeLength = e.getLength();
            Document doc = (Document)e.getDocument();
            try 
            {
                String chars = doc.getText(offset, changeLength);
                
                if (_dout == null) 
                {
                    throw new IllegalStateException("DataOutputStream is null");
                } 
                else 
                {
                    _dout.writeInt(INSERT);
                    _dout.writeInt(offset);
                    _dout.writeUTF(chars);
                    _dout.flush();
                }
            } 
            catch(BadLocationException ble) 
            {
            } 
            catch(IOException ioe) 
            {
            }
        }
        
        public void removeUpdate(DocumentEvent e) 
        {
            int offset = e.getOffset();
            int changeLength = e.getLength();
            try 
            {
                if (_dout == null) 
                {
                    throw new IllegalStateException("DataOutputStream is null");
                } 
                else 
                {
                    _dout.writeInt(REMOVE);
                    _dout.writeInt(offset);
                    _dout.writeInt(changeLength);
                    _dout.flush();
                }
                
            } 
            catch(IOException ioe) 
            {
            }
        }
        
        public void changedUpdate(DocumentEvent e) 
        {
        }
    } 


    private void initComponents() 
    {
        _panel = new JPanel();
        _textArea = new JTextArea(20, 40);
        _textArea.setEditable(false);
        
        getContentPane().setLayout(null);
        setTitle("Bluetooth Serial Port Demo");
        setResizable(false);
        
        addWindowListener(new java.awt.event.WindowAdapter() 
        {
            public void windowClosing(java.awt.event.WindowEvent evt) 
            {
                closeSerialConnection();
                System.exit(0);
            }
        });

        _textArea.setFont(new Font("SansSerif", Font.PLAIN, 20));
        _textArea.setLineWrap(true);
        _textArea.setWrapStyleWord(true);
        JScrollPane areaScrollPane = new JScrollPane(_textArea);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        _textArea.getDocument().addDocumentListener(new MyDocumentListener());

        _panel.add(areaScrollPane);
        setContentPane(_panel);
    }

    public void initSerialConnection() 
    {
        boolean found = false;
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        System.out.println("Initializing serial connection...");

        if (!portList.hasMoreElements()) 
        {
            System.err.println("no elements");
            System.exit(1);
        }

        while (portList.hasMoreElements()) 
        {
            CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            if ((portId.getPortType() == CommPortIdentifier.PORT_SERIAL) && (portId.getName().equals(COM_PORT))) 
            {
                try 
                {
                    _serialPort = (SerialPort)portId.open("Serial Port Test", 2000);

                    _din = new DataInputStream( new BufferedInputStream( _serialPort.getInputStream() ) );
                    _dout = new DataOutputStream( new BufferedOutputStream( _serialPort.getOutputStream() ) );
                    new InputThread().start();
                    
                    _serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                    found = true;
                    break;
                    
                } 
                catch (PortInUseException e) 
                {
                	System.err.println( portId.getName() + ": port in use");
                    System.exit(1);
                } 
                catch (UnsupportedCommOperationException e) 
                {
                	System.err.println("UnsupportedCommOperationException");
                    System.exit(1);
                } 
                catch (IOException e) 
                {
                	System.err.println("IOException while poking device.");
                    System.exit(1);
                }
            }
        }
        
        if (!found) 
        {
            System.err.println("Unable to find serial port " + COM_PORT);
            System.exit(1);
        }
    }

    private class InputThread extends Thread 
    {
        public void run() 
        {
            int type;
            String value;
            try 
            {
                System.out.println("Establishing connection with device...");
                _dout.writeInt(JUST_OPEN);
                _dout.flush();
                
                for (;;) 
                {
                    type = _din.readInt();
                    
                    if (type == JUST_OPEN) 
                    {
                        System.out.println("Connection established.");
                        _textArea.setEditable(true);
                        value = _textArea.getText();
                        
                        if (value == null || value.equals("")) 
                        {
                            _dout.writeInt(NO_CONTENTS);
                            _dout.flush();
                        } 
                        else 
                        {
                            System.out.println("Sending initial contents to device.");
                            _dout.writeInt(CONTENTS);
                            _dout.writeUTF(value);
                            _dout.flush();
                        }
                    } 
                    else if (type == NO_CONTENTS) 
                    {
                        System.out.println("Connection established.");
                        _textArea.setEditable(true);
                    } 
                    else if (type == CONTENTS) 
                    {
                        System.out.println("Connection established.");
                        System.out.println("Reading initial contents from device.");
                        value =  _din.readUTF();
                        _textArea.setText(value);
                        _textArea.setEditable(true);
                    } 
                    else 
                    {
                        throw new RuntimeException();
                    }
                }
            } 
            catch(IOException ioe) 
            {
                throw new RuntimeException();
            }
        }
    }

    private void closeSerialConnection() 
    {
        System.out.println("Closing connection.");
        _serialPort.close();
        _serialPort = null;
        _din = null;
        _dout = null;
    }
}
