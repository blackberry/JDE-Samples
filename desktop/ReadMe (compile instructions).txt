/*
 * Copyright © 1998-2011 Research In Motion Limited
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

The Desktop project must be linked against desktopapi.tlb in order to compile.
If the BlackBerry JDE and Desktop Manager are installed in their default locations,
no configuration of the Desktop project is necessary, as it will know where to find
desktopapi.tlb.  Otherwise, follow these instructions before attempting to compile:

1) Locate desktopapi.tlb on your machine.  Its default location is
   C:\Program Files\Research In Motion\BlackBerry.
2) On line 29 of Desktop.idl, change the path argument of the importlib statement
   to reflect the location of desktopapi.tlb.
3) In the Microsoft Visual C++ window, click Project, Settings...
4) In the "Settings For" drop-down list, select "All Configurations".
5) Click the C/C++ tab.
6) In the Category drop-down list, select Preprocessor.
7) Change the path in the "Additional include directories" text box to reflect the
   location of desktopapi.tlb.

The project can now be compiled.
