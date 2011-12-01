@echo off
REM Build and run script for the UdpDemo Server side

del *.class
javac UdpServer.java
pushd ..\..\..\..\..
	java -cp . com.rim.samples.server.udpdemo.UdpServer
popd
pause
