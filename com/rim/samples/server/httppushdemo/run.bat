@echo off
REM Build and run script for the HTTPPush Server side

del *.class
javac HTTPPushDemo.java
pushd ..\..\..\..\..
	java -cp . com.rim.samples.server.httppushdemo.HTTPPushDemo
popd