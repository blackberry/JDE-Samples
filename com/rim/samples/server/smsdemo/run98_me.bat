@echo off
REM Build and run the smsdemo server
javac SMSServer.java
cd ..\..\..\..\..\
	java -cp . com.rim.samples.server.smsdemo.SMSServer
cd com\rim\samples\server\smsdemo
