Instructions on how to use the Bluetooth Demo:

---------------------------------------
Pairing with another BlackBerry device
---------------------------------------

1) Build and sign the application. For more information on code signing and controlled APIs, please refer to the "BlackBerry Signature Tool Developer Guide".

2) Load the application onto two BlackBerry devices ('A' and 'B'). 

Note : The following instructions demonstrate how to initiate the connection from BlackBerry 'A' : 

3) Launch the BluetoothDemo sample on BlackBerry 'B', and select "Listen for connections" from the menu.

4) On BlackBerry 'A', select Options > Bluetooth.

5) Pair BlackBerry 'A' with BlackBerry 'B'.

6) Launch the BluetoothDemo sample on BlackBerry 'A' and initiate the connection by selecting "Connect to <BlackBerry 'B' device name>" from the menu.

7) On BlackBerry 'B', accept the connection request.

8) Type away. Characters typed on BlackBerry 'A' should appear on BlackBerry 'B' and characters typed in BlackBerry   'B' should appear on BlackBerry 'A'. 
 
You can also select various options in the menu to change the serial port state.



------------------
Pairing with a PC
------------------

1) Build and sign the application. For more information on code signing and controlled APIs, please refer to the "BlackBerry Signature Tool Developer Guide".

2) Load the application onto a Bluetooth enabled BlackBerry device.


Perform one of the following procedures:


If using a server side connection (i.e. the computer initiates the connection)
-------------------------------------------------------------------------------

1) Launch the BluetoothDemo sample on the BlackBerry device, and select "Listen for connections" from the menu.

2) Pair the computer with the BlackBerry device. For more information, please refer to the following article:

http://www.blackberry.com/btsc/articles/835/KB04132_f.SAL_Public.html#Task%204

3) From Start/Settings/Control Panel/Bluetooth Devices/COM Ports tab, take note of the outgoing COM Port that was added when the computer was paired with the BlackBerry device (will appear as "Hi there").

4) Make sure that the sample is running and listening for connections on the BlackBerry device.

5) Create a new connection using HyperTerminal specifying the (outgoing) COM port that the Bluetooth connection is using. 
To create a connection in HyperTerminal, please complete the following steps :

  a. Select Start > Programs > Accessories > Communications > Hyper Terminal.
  
  b. Name the session and select an icon for the connection.
  
  c. In the next pop-up window, choose the COM port for the Bluetooth connection from the drop-down list of "Connect  using:".

6) Open the connection to the port (if it is not already opened).

7) On the BlackBerry device, accept the connection request.

8) Type away. Characters typed on the BlackBerry should appear in HyperTerminal and characters typed in HyperTerminal should appear on the BlackBerry device.  You can also select various options in the menu to change the serial port state.


If using a client side connection (i.e. the BlackBerry device initiates the connection)
----------------------------------------------------------------------------------------

1) Before pairing the BlackBerry device to the computer, create a connection using HyperTerminal to the (incoming) COM port specified in Start/Settings/Control Panel/Bluetooth Devices/COM Ports tab. 
To create a connection in HyperTerminal, please complete the following steps :

  a. Select Start > Programs > Accessories > Communications > Hyper Terminal.
  
  b. Name the session and select an icon for the connection.
  
  c. In the next pop-up window, choose the COM port for the Bluetooth connection from the drop-down list of "Connect  using:".
  
2) Open the connection to the port (if it is not already opened).

3) Pair the BlackBerry device with the computer. For more information, please refer to the following article:

http://www.blackberry.com/btsc/articles/835/KB04132_f.SAL_Public.html#Task%204

4) Launch the BluetoothDemo sample on the BlackBerry device and initiate the connection by selecting "Connect to: xxxx" from the menu, where xxxx is your computer's Bluetooth device name.

5) Type away. Characters typed on the BlackBerry device should appear in HyperTerminal and characters typed in HyperTerminal should appear on the BlackBerry device.  

You can also select various options in the menu to change the serial port state.