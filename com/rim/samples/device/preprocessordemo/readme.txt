//////////////// ABOUT THE PREPROCESSOR //////////////

At times it is necessary for developers to deploy BlackBerry applications on 
different Blackberry smart phone devices with different software versions. This 
can lead to binary compatibility issues if an application is compiled in a later
JDE version and leverages APIs which do not exist in earlier software versions. 
To allow for compilation of source code from a single code base, the BlackBerry 
JDE preprocessor can be utilized. The preprocessor may also be used to easily 
remove debugging output before deployment.

///////////// HOW TO DEFINE/REMOVE TAGS //////////////

To define a tag from within the JDE:
       1. Double click the project (JDP file).
       2. Select the "Compile" tab.
       3. Under "Preprocessor defines" click the "Add" button.
       4. Type the tag name.
       5. Click "Ok" to add the tag.
       6. Click "Ok" to add the changes to the project.
       
To remove a tag from within the JDE:
       1. Double click the project (JDP file).
       2. Select the "Compile" tab.
       3. Under "Preprocessor defines" select the tag to delete.
       4. Click the "Delete" button to the right.
       5. Click "Ok" to add the changes to the project.
       
To define a tag from within the BlackBerry JDE Plug-in for Eclipse:
       1. Right click the "PreprocessorDemo" project and click "Properties"
       2. Select the "Compile" tab
       3. Under "Preprocessor defines" click the "Add" button
       4. Type the tag name
       5. Click "Ok" to add the tag.
       6. Click "Ok" or "Apply" to add the changes to the project.
       
To remove a tag from within the BlackBerry JDE Plug-in for Eclipse:
       1. Right click the "PreprocessorDemo" project and click "Properties"
       2. Select the "Compile" tab
       3. Under "Preprocessor defines" click the "Add" button
       4. Type the tag name
       5. Click "Ok" to add the tag.
       6. Click "Ok" or "Apply" to add the changes to the project.
  
To manually define tags:
       1. Open the JDP file with a text editor.
       2. Go to the line that starts with Options.
       3. At the end of the line, add -define <tag1>;<tag2>;...;<tagN> as in the
          following example:
          
          Options=-quiet -define=FOO;PREPROCESSOR         

 
Note: The -define statement is simply an argument passed to the RIM 
      Application Program Compiler (RAPC). Therefore, you can use it with
      your own command line build scripts. This functionality has been 
      present since RAPC version 4.0.
 
IMPORTANT:
      - If you use the preprocessor, make sure at least one definition is 
        defined, typically PREPROCESSOR, or it will not engage. 
        
/////////// HOW TO USE THE PREPROCESSOR ////////////

The preprocessor uses a set of directives to determine its behavior during
preprocessing. All directives begin with "//#" and the following directives
are suppported:

    1. //#preprocess
    
            This enables the preprocessor. It must be the very first line of the
            source file with no characters or whitespace preceding it.
            
    2. //#ifdef <tag>
       ... code
       //#else
       ... code
       //#endif
            If <tag> is defined, then the code between //#ifdef and else is 
            executed; otherwise, //#else to //#endif is executed. The else 
            section is not mandatory. For example:
            
                  //#ifdef <tag>
                  ... code
                  //#endif
                  
            This will run the code between //#ifdef and //#endif if the <tag> is
            defined, otherwise the code will behave as though the section was 
            removed.
            
    3. //#ifndef <tag>
       ... code
       //#else
       ... code
       //#endif     
       
            //#ifndef is the opposite of //#ifdef. If <tag> is not defined, the 
            code is executed. The else section is optional.
            
Note: it is not possible to nest #if directives. The following example will 
      not compile:
      
          //#ifdef <tag1>
          
          //#ifdef <tag2>
          ... code
          //#endif
              
          //#endif
            
////////////////// More information ////////////////

For more information, please refer to the "How To - Use the preprocessor" knowledge base article at:
http://www.blackberry.com/knowledgecenterpublic/livelink.exe/fetch/2000/348583/1407892/How_To_-_Use_the_preprocessor.html?nodeid=1487658&vernum=0



            
