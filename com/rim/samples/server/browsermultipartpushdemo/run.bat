@echo off
REM Build and run script for the BrowserMultipartPushDemo 

del *.class
javac -classpath mail.jar BrowserMultipartPushDemo.java
pushd ..\..\..\..\..
	java -cp com\rim\samples\server\browsermultipartpushdemo\activation.jar;com\rim\samples\server\browsermultipartpushdemo\mail.jar;. com.rim.samples.server.browsermultipartpushdemo.BrowserMultipartPushDemo
popd
