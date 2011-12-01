/*
 * ListFieldCallback.java
 *
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

package com.rim.samples.device.persistentstoredemo;

import net.rim.device.api.ui.component .*;
import net.rim.device.api.ui.*;
import net.rim.device.api.system .*;
import java.util.*;

/**
 * Implementation of ListFieldCallback.
 */
class ListCallback implements ListFieldCallback
{
    private Vector listElements = new Vector();

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
     */
    public void drawListRow(ListField list, Graphics g, int index, int y, int w)
    {
        String text = (String)listElements.elementAt(index);
        g.drawText(text, 0, y, 0, w);
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField , int)
     */
    public Object get(ListField list, int index)
    {
        return listElements.elementAt(index);
    }


    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField , String , int)
     */   
    public int indexOfList(ListField list, String p, int s)
    {
        return listElements.indexOf(p, s);
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(ListField list)
    {
        return Display.getWidth();
    }


    void insert(String toInsert, int index)
    {
        listElements.addElement(toInsert);
    }

    void erase()
    {
        listElements.removeAllElements();
    }
}

