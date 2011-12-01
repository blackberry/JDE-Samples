OTA Sync Demo
-------------

1. Download Sync Server SDK from http://na.blackberry.com/eng/developers/javaappdev/devtools.jsp.

2. Install Sync Server SDK as per instructions in Synchronization Server SDK Development Guide available here: http://na.blackberry.com/eng/support/docs/subcategories/?userType=21&category=BlackBerry+Java+Application+Development&subCategory=Synchronization+Server+SDK

3. Launch SampleConnectorApp.exe from e.g. C:\Program Files\Research In Motion\BlackBerry Sync Server SDK\Samples

4. Launch bbmgrw32.exe from e.g. C:\Program Files\Research In Motion\BlackBerry Sync Server SDK\BBMgr\Server and add a single user with PIN 2100000A.

5. In the BlackBerry Java Development Environment (JDE), build the OTA Sync Demo project in the samples.jdw workspace.

6. Create a new simulator profile under Edit/Preferences/Simulator.  Under the General tab, check "Launch Mobile Data System Connection Service (MDS-CS) with simulator".  

7. Press F5 on your keyboard to launch the BlackBerry device simulator.

8. Allow Enterprise Activation to complete.

9.  Launch the OTA Sync Demo from the home screen.

10. Select Add from the menu, fill in the fields and select Save from the menu.

11. Launch dbviewer.exe from e.g. C:\Program Files\Research In Motion\BlackBerry Sync Server SDK\Samples and verify that the contact was added to the database.

12.  Close the BlackBerry device simulator.

13.  In the JDE, select File/Erase Simulator File/Erase File System and re-launch the BlackBerry device simulator.

14.  Repeat steps 7 through 9.

15.  Verify that the contact has been restored.
