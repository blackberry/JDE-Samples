How To Run BluetoothSerialPortDemo

------------------------------------
Using Sun’s Java Communications API 
------------------------------------

1. Download the Windows Platform - Java(TM) Communications API Specification 2.0

2. Unzip the downloaded file and copy win32com.dll to your <JRE_HOME>\bin directory, e.g. ‘C:\Program Files\Java\jre1.6.0_05\bin’ (not to be confused with ‘C:\Program Files\Java\jdk1.6.0_05\jre\bin’).

3. Copy ‘comm.jar’ and ‘javax.comm.properties’ to your ‘com\rim\samples\server\bluetoothserialportdemo’ directory.

4. Modify ‘BluetoothSerialPortServer.java’ so that COM_PORT refers to the serial port you wish to use (look for an incoming COM port in the "COM Ports" tab of the "Bluetooth Devices" configuration in the Control Panel, if you can't see any COM ports, then add an incoming port).

5. Before pairing the BlackBerry device with your computer, execute ‘run.bat’ from the ‘com\rim\samples\server\bluetoothserialportdemo’ directory.  A command prompt and a Swing window should appear.

6. While the server component is running, pair the BlackBerry device with the computer.

7. On the BlackBerry, select Options > Bluetooth, and make sure that the computer is the only paired device to the BlackBerry. Also check the “Device properties” for the computer that it is paired with. If the “Services:” area is blank, then you have not paired properly, remove the pairing, close the server and repeat from Step 5 until you see a "COMX" service under "Services:", where X is the incoming COM port number.

8. Run  the BluetoothSerialPortDemo client application on the BlackBerry device.

9. After the command prompt window displays "Connection established",  type a message into the text area of the Swing window.  The characters typed should appear in the BluetoothSerialPortDemo application on the BlackBerry device. 


-----------
Using RXTX
-----------

RXTX is an open-source alternative to Sun's Java Communications API. The RXTX library comes in two flavors, one that uses the Sun Java Comm package (i.e. the javax.comm namespace) and one that does not (i.e. gnu.io namespace). 


1. Download the Windows version of RXTX that does not use the Sun Java Comm package from the following URL:

   http://users.frii.com/jarvi/rxtx/download.html
 
2. Unzip the downloaded file and copy rxtxSerial.dll to your <JRE_HOME>\bin directory, e.g. ‘C:\Program Files\Java\jre1.6.0_05\bin’ (not to be confused with ‘C:\Program Files\Java\jdk1.6.0_05\jre\bin’).

3. Copy ‘RXTXcomm.jar’ to your ‘com\rim\samples\server\bluetoothserialportdemo’ directory and update the ‘run.bat’ file (change all occurrences of comm.jar to RXTXcomm.jar).

4. In BluetoothSerialPortServer.java, change ”import javax.comm.*” to ”import gnu.io.*” and change the COM_PORT value to that of the serial port you wish to use (look for an incoming COM port in the "COM Ports" tab of the "Bluetooth Devices" configuration in the Control Panel, if you can't see any COM ports, then add an incoming port).

5. Before pairing the BlackBerry device with your computer, execute ‘run.bat’ from the ‘com\rim\samples\server\bluetoothserialportdemo’ directory.  A command prompt and a Swing window should appear.

6. While the server component is running, pair the BlackBerry device with the computer.

7. On the BlackBerry, select Options > Bluetooth, and make sure that the computer is the only paired device to the BlackBerry. Also check the “Device properties” for the computer that it is paired with. If the “Services:” area is blank, then you have not paired properly, remove the pairing, close the server and repeat from Step 5 until you see a "COMX" service under "Services:", where X is the incoming COM port number.

8. Run the BluetoothSerialPortDemo client application on the BlackBerry device.

9. After the command prompt window displays "Connection established",  type a message into the text area of the Swing window.  The characters typed should appear in the BluetoothSerialPortDemo application on the BlackBerry device.


Issues when running the sample with RXTX 
-----------------------------------------

When the sample was tested with RXTX 2.1.7, we noticed the following issues:

1. The server side program throws an exception when a connection is established.  This will not prevent the program from running as expected in all other respects.

2. When the BluetoothSerialPortDemo client application on the BlackBerry device is closed and then run again, text displayed on  the Swing screen will be sent by the server to the client application.   The client application throws an IllegalArgumentException.

