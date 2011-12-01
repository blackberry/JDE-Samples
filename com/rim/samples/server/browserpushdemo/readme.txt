Instructions for browserpushdemo
================================

1) Copy the contents of the com\rim\samples\server\browserpushdemo\testpage
   directory to a web server.

2) Update the contentUrlString, unreadIconUrl, and readIconUrl properties of
   the browserpush.properties file under both the pappush and rimpush directories.
   They should point to the relevant files now located on your web server, as
   listed below:
      contentUrlString = http://path_to_push_files/sample.html
      unreadIconUrl    = http://path_to_push_files/smile_unread.png
      readIconUrl      = http://path_to_push_files/smile.png

3) Launch an MDS Simulator and Device Simulator.
   
4) Update the properties in the relevant "browserpush.properties" file under the 
   pappush or rimpush directory, depending on what type of push is desired.  Note
   that for each new push request, a unique pushID must be specified; otherwise,
   the push request will fail.

5) Run the "run.bat" file under the pappush or rimpush directory, depending on what
   type of push is desired.

6) Examine the Device Simulator's home screen for changes, such as a new or updated
   push channel icon.

7) Repeat steps 4-6 as desired.
