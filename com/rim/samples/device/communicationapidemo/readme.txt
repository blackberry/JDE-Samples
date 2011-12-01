To demonstrate the Communication API Demo sample application in its entirety, you will need the following applications:
Note: The instructions in this file assume that the application will be run on a BlackBerry Smartphone simulator.


 - MDS-CS simulator (Server side application)
 - Communication API Demo (Device side application)
 - HTTP Push Demo Server (Device side application)
 - Communication API Local Helper (Device side application)
 - Communication API Echo Server (Server side application)

 
 MDS-CS Simulator
 ----------------
 You will need this application to demonstrate Communication API Demo functionality.
 The simulator can be run from the MDS folder in your BlackBerry JDE installation. 
 

 Communication API Demo 
 ----------------------
 Demonstrates main features of the Communication and Message Processing API:
        Send Messages - Fire-and-Forget 
        Send Messages - Non-Blocking - (ATOM, JSON, RSS, SOAP, XML)
        Send Messages - Blocking 
        Receive Messages - BES Push blocking
        Receive Messages - BES Push non-blocking
        Receive Messages - IPC Push blocking
        Receive Messages - IPC Push non-blocking
        Receive Messages - BPS Push 
        Cancel Messages
        Basic Authentication 
        Stream Data Upload 
 
In order to start the application, perform the following steps: 
 
 1) Configure parameters config.xml.
      /timeout - Set timeout duration (default -> 30 seconds)
      /echo-server-uri - set IP of the computer running Communication API Echo Server 
      /mdscs/ip - set IP of the computer running MDS-CS
      /mdscs/port - set MDSCS port (default - > 8080)
   
 2) In the BlackBerry JDE, open the samples.jdw workspace (located in the "samples"
    directory under the "BlackBerry JDE" installation directory).
    
 3) Press F5 to build the samples.jdw workspace and launch the BlackBerry Simulator.
  
 4) Run application "CommunicationAPIDemo"

 5) The following notes pertain to the parameters that should be specified for each feature:
        Send Messages - Fire-and-Forget (local): Sender URI: local://<ApplicationName><path>  (e.g.local://CommunicationAPIDemo/test)
        
        Send Messages - Fire-and-Forget (non-local): Sender URI: http://<echo_server_uri> (e.g.http://10.22.33.44:8105)
        
        Send Messages - Non-Blocking - (ATOM, JSON, RSS, SOAP, XML): Sender URI: http://<echo_server_uri><resource_path> (e.g.http://10.22.33.44:8105/ATOM)
        
        Send Messages - Blocking  : Sender URI: http://<echo_server_uri>/<resource_path> (e.g.http://10.22.33.44:8105/TEXT)
        
        Receive Messages - BES Push blocking  : Receiver URI: local://:<device_port><path> (e.g. "local://:1992/test8", in HTTP Push Demo Server path must be: "/test8") 
          Receiver destination port <device_port> MUST match the constant DEVICE_PORT in HttpPushDemo.java class of HTTP Push Demo Server
        
        Receive Messages - BES Push non-blocking : Receiver URI: local://:<device_port><path> (e.g. "local://:1992/test8", in HTTP Push Demo Server path must be: "/test8") 
          Receiver destination port <device_port> MUST match the constant DEVICE_PORT in HttpPushDemo.java class of HTTP Push Demo Server      
        
        Receive Messages - IPC Push blocking : Receiver URI: local://<path> (e.g. "local:///test2", in Communication API Local Helper path must be: "local://CommunicationAPIDemo/test2")
        
        Receive Messages - IPC Push non-blocking : Receiver URI: local://<path> (e.g. "local:///test2", in Communication API Local Helper path must be: "local://CommunicationAPIDemo/test2")
        
        Receive Messages - BPS Push : can be tested on physical BlackBerry Smartphone devices ONLY
        
        Cancel Messages: Destination: http://<echo_server_uri>/<resource_path> (e.g.http://10.22.33.44:8105/TEXT)
        
        Basic Authentication: specify url that needs basic authentication, user name and password (in the sample twitter account is used)
        
        Stream Data Upload:  Upload URI: http://<echo_server_uri>/<resource_path> (e.g.http://10.22.33.44:8105/TEXT)
  
 
 
 HTTP Push Demo Server
 ---------------------
  You will need this application to demonstrate "Receive Push Messages (BES)/(BPS)" functionality.  The application is located under
  com/rim/samples/server/httppushdemo.  
  
  To send a push message to a device, follow those steps:
    1) Make sure that  constant DEVICE_PORT in HttpPushDemo.java class is matching receiver destination port specified for push in Communication API Demo
    
    2) Launch the HTTP Push Demo Server by executing the run.bat file, located in the samples\com\rim\samples\server\httppushdemo directory .
    
    3) In the HTTP Push Demo Server window, type a text message in the to text field
    
    4) In "Device PIN" text field, type the PIN of device that is running in simulator. Default is "2100000A"
    
    5) Select the "rim" radio button.
    
    6) In the text field below "Device PIN" field, type the path part of destination where you want to push. Examples: "/test1", "/demo/d1"
         For detailed information about destination and destination path, please, see the "Communication API Demo" section above.
    
    7) Click "Send".

        
 
 Communication API Local Helper
 ------------------------------
  You will need this application to demonstrate "Receive Messages (IPC)":
        Receive Messages - IPC Push blocking
        Receive Messages - IPC Push non-blocking
  The Communication API Local Helper application allows pushing of IPC Fire-and-Forget messages to the Communication API Demo application.
  
  This application is a part of sample application workspace and will be built and deployed to BlackBerry Simulator by doing following steps:  
 
  1) Follow steps 2 and 3 in the Communication API Demo section above.
  
  2) Open CommunicationAPILocalHelper application.
  
  3) Go to  Send IPC messages Screen and specify in the "Sender URI" text field the URI to where the push message
     will be sent. The format of URI as follows: 
            local://<ApplicationName><path> 
            Example: local://CommunicationAPIDemo/test 
       (Note: To receive IPC push message sent to local://CommunicationAPIDemo/test, start the receiver in the in Communication API Demo "Receive Messages (IPC)"
       screen with URI "local:///test" specified.) 
  
  
  Communication API Echo Server
  ---------------------
  This application is required to demonstrate features specified below.
        Send Messages - Fire-and-Forget : (Non-Local)
        Send Messages - Non-Blocking - (ATOM, JSON, RSS, SOAP, XML)
        Send Messages - Blocking 
        Cancel Messages
        Stream Data Upload 
  
  To start the Communication API Echo Server execute run.bat located under com\rim\samples\server\communicationapidemo.
  When you start the server, verify that the IP of the Echo Server in the console is the same 
  as was entered in /config/config.xml of Communication API Demo (see step 1 of the Communication API Demo section above)
 
