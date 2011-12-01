@echo off
REM Build and run script for the Socket Server side

del *.class
javac SimpleSocketServer.java
pushd ..\..\..\..\..
	java -cp . com.rim.samples.server.socketdemo.SimpleSocketServer
popd