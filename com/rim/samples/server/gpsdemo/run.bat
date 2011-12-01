@echo off
REM Build and run script for the GPSDemo 
del *.class
set home=%cd%
javac -classpath "%classpath%;jcommon-0.9.6.jar;jfreechart-0.9.21.jar" *.java 
pushd ..\..\..\..\..
	java -cp "%classpath%;%home%\jcommon-0.9.6.jar;%home%\jfreechart-0.9.21.jar;." com.rim.samples.server.gpsdemo.GPSServer
popd
