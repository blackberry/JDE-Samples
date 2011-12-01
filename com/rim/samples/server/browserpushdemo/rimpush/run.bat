@echo off
REM Build and run script for the BrowserRimPushDemo 

del *.class
javac BrowserRimPushDemo.java
pushd ..\..\..\..\..\..
	java -cp . com.rim.samples.server.browserpushdemo.rimpush.BrowserRimPushDemo
popd
