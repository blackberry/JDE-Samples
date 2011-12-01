REM @echo off
REM Build and run script for the HTTPPush Server side

del *.class
javac HTTPPushDemo.java
cd ..\..\..\..\..
	java -cp . com.rim.samples.server.httppushdemo.HTTPPushDemo
cd com\rim\samples\server\httppushdemo