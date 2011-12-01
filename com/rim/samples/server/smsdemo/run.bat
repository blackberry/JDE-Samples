@echo off
REM Build and run the smsdemo server
javac SMSServer.java
pushd ..\..\..\..\..\
	java -cp . com.rim.samples.server.smsdemo.SMSServer %1
popd