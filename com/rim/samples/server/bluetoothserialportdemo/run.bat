@echo off
REM Build and run script for the BluetoothSerialPortServer.

del *.class
javac -classpath .;comm.jar BluetoothSerialPortServer.java
pushd ..\..\..\..\..
	java -cp .;com\rim\samples\server\bluetoothserialportdemo\comm.jar com.rim.samples.server.bluetoothserialportdemo.BluetoothSerialPortServer
popd


