@echo off
REM Build and run script for the BrowserPapPushDemo 

del *.class
javac BrowserPapPushDemo.java
pushd ..\..\..\..\..\..
	java -cp . com.rim.samples.server.browserpushdemo.pappush.BrowserPapPushDemo
popd
