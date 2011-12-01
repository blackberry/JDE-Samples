/*
 * FileExplorerListField.java
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

package com.rim.samples.device.attachmentdemo;

import java.util.Vector;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

/**
 * A ListField class to display file holder information
 */
public class FileExplorerListField extends ListField implements
        ListFieldCallback {
    private final Vector _elements = new Vector();

    /**
     * Creates a new FileExplorerListField object
     */
    public FileExplorerListField() {
        setCallback(this);
    }

    /**
     * Adds a given element to this list field
     * 
     * @param element
     *            The element to be added
     */
    void add(final Object element) {
        _elements.addElement(element);
        setSize(getSize());
    }

    /**
     * @see ListField#keyChar(char, int, int)
     */
    public boolean keyChar(final char key, final int status, final int time) {
        // Allow space bar to page down
        if (getSize() > 0 && key == Characters.SPACE) {
            getScreen().scroll(Manager.DOWNWARD);
            return true;
        }

        return super.keyChar(key, status, time);
    }

    /**
     * @see ListField#getSize()
     */
    public int getSize() {
        return _elements != null ? _elements.size() : 0;
    }

    /**
     * Removes all elements from this list field
     */
    void removeAll() {
        _elements.removeAllElements();
        setSize(0);
    }

    // ListFieldCallback implementation ----------------------------------------

    /**
     * @see ListFieldCallback#drawListRow(ListField, Graphics, int, int, int)
     */
    public void drawListRow(final ListField listField, final Graphics graphics,
            final int index, final int y, final int width) {
        if (index < getSize()) {
            final FileHolder fileholder =
                    (FileHolder) _elements.elementAt(index);

            String text;

            if (fileholder.isDirectory()) {
                int pathIndex = fileholder.getPath().lastIndexOf('/');
                pathIndex =
                        fileholder.getPath().substring(0, pathIndex)
                                .lastIndexOf('/');
                text = fileholder.getPath().substring(pathIndex + 1);
            } else {
                text = fileholder.getFileName();
            }

            graphics.drawText(text, 0, y);
        }
    }

    /**
     * @see ListFieldCallback#get(ListField, int)
     */
    public Object get(final ListField listField, final int index) {
        if (index >= 0 && index < getSize()) {
            return _elements.elementAt(index);
        }

        return null;
    }

    /**
     * @see ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(final ListField listField) {
        return Display.getWidth();
    }

    /**
     * @see ListFieldCallback#indexOfList(ListField, String, int)
     */
    public int indexOfList(final ListField listField, final String prefix,
            final int start) {
        return listField.indexOfList(prefix, start);
    }
}
