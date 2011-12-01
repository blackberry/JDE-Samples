OTA Backup Restore Demo
---------------------------

Note: 
Before 4.5 and multi-BES support, the sync service book that gets loaded on the BlackBerry device simulator by default (for use with the Sync Server SDK and the OTA Sync Demo) would get overwritten by a service book injection by whatever BES one was connecting to through Desktop Manager.  In order to activate a 4.5 or later device using Desktop Manager, you will need to modify the simulator.alx file found in the simulator directory of your 4.5 JDE or later installation.  Remove the reference to net_rim_sdk_simulationSB.cod from the <files> element and save the file before launching the BlackBerry device simulator.  This will prevent the default simulated sync service book from being loaded onto the BlackBerry device simulator and allow for a sync service book injection from the BES you are connecting to through Desktop Manager.


1. Open Desktop Manager.

2. In the BlackBerry Java Development Environment (JDE), build the OTA Backup Restore Demo project in the samples.jdw workspace.

3. Press F5 on your keyboard to launch the BlackBerry device simulator.

4. Launch the OTA Backup Restore Demo from the Downloads folder.

5. Select Add from the menu, fill in the fields and select Save from the menu.  Close the application.

6. In the simulator window, select: Simulate/USB Cable Connected.

7. Allow Enterprise Activation to complete.

8. Close the BlackBerry device simulator.

9. In the JDE, select File/Erase Simulator File/Erase File System and re-launch the BlackBerry device simulator.

10. Press F5 on your keyboard to launch the BlackBerry device simulator.

11. Repeat steps 6 and 7.

12. Launch the OTA Backup Restore Demo from the Downloads folder to verify that the contact has been restored.