@echo off
REM Build and run script for the Socket Server side

del *.class
javac SimpleSocketServer.java
cd ..\..\..\..\..
	java -cp . com.rim.samples.server.socketdemo.SimpleSocketServer	
cd com\rim\samples\server\socketdemo