@echo off
REM Build and run script for the HTTP Echo Server

del *.class
javac EchoServer.java
pushd ..\..\..\..\..
    java -cp . com.rim.samples.server.communicationapidemo.EchoServer
popd