/*
 * ChildReader.java
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

package com.rim.samples.device.accessibilitydemo.screenreaderdemo;

import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleRole;
import net.rim.device.api.ui.accessibility.AccessibleState;
import net.rim.device.api.ui.accessibility.AccessibleTable;
import net.rim.device.api.ui.accessibility.AccessibleText;

/**
 * This class contains methods to read accessible child elements
 */
class ChildReader {
    /**
     * Reads an accessible child element
     * 
     * @param context
     *            The accessible child element to read
     */
    static void readChildElement(final AccessibleContext context) {
        if (context == null) {
            return;
        }

        String name = context.getAccessibleName();
        if (name == null) {
            name = "";
        }

        // Evaluate the states set for the accessible element
        final int statesSet = context.getAccessibleStateSet();
        final boolean focused = (statesSet & AccessibleState.FOCUSED) != 0;
        final boolean expanded = (statesSet & AccessibleState.EXPANDED) != 0;
        final boolean collapsed = (statesSet & AccessibleState.COLLAPSED) != 0;
        final boolean selected = (statesSet & AccessibleState.SELECTED) != 0;
        final boolean selectable =
                (statesSet & AccessibleState.SELECTABLE) != 0;
        final boolean multiSelectable =
                (statesSet & AccessibleState.MULTI_SELECTABLE) != 0;
        final boolean editable = (statesSet & AccessibleState.EDITABLE) != 0;
        final boolean checked = (statesSet & AccessibleState.CHECKED) != 0;
        final boolean busy = (statesSet & AccessibleState.BUSY) != 0;
        final boolean expandable =
                (statesSet & AccessibleState.EXPANDABLE) != 0;
        final boolean focusable = (statesSet & AccessibleState.FOCUSABLE) != 0;

        // Create strings representing the accessible element states set
        final String focusedText = focused ? " focused" : "";
        final String expandedText = expanded ? " expanded" : "";
        final String collapsedText = collapsed ? " collapsed" : "";
        final String expandableText = expandable ? " expandable" : "";
        final String selectedText = selected ? " selected" : "";
        final String editableText = editable ? " editable" : "";
        final String checkedText = checked ? " checked" : " unchecked";
        final String multiSelectableText =
                multiSelectable ? " multi selectable" : "";
        final String focusableText = focusable ? " focusable" : "";

        // This buffer will contain text to be spoken
        final StringBuffer toSpeak = new StringBuffer();

        // Evaluate the navagational orientation state(s) set for the accessible
        // element
        final String orientation = Util.getOrientation(statesSet);

        final AccessibleText text = context.getAccessibleText();

        final int childCount = context.getAccessibleChildCount();

        // In cases where there are many components on a
        // screen/dialog/menu...most of
        // which are not frequently used, it's more practical to set a limit on
        // the
        // number of components to be read:
        // int maxCount = Math.min( childCount, 10);
        // and then use maxCount instead of childCount.

        switch (context.getAccessibleRole()) {
        case AccessibleRole.SCREEN:
            if (busy) {
                Util.speak("Screen " + name + " loading");
            } else {
                Util.speak("Screen " + name);
                for (int i = 0; i < childCount; i++) {
                    final AccessibleContext child =
                            context.getAccessibleChildAt(i);
                    readChildElement(child);
                }
            }
            break;

        case AccessibleRole.TEXT_FIELD:
            if (text != null) {
                String currentText = text.getWholeText();
                currentText = currentText != null ? currentText.trim() : "";
                final String textToSpeak =
                        currentText.length() > 0 ? " with text " + currentText
                                : " empty";
                toSpeak.append(name + " text field " + textToSpeak);
                toSpeak.append(focusedText);
                toSpeak.append(editableText);
            }
            break;

        case AccessibleRole.LABEL:
            toSpeak.append(name);
            toSpeak.append(focusableText);
            toSpeak.append(focusedText);
            toSpeak.append(selectedText);
            toSpeak.append(expandedText);
            break;

        case AccessibleRole.APP_ICON:
            toSpeak.append(name + "application icon");
            toSpeak.append(focusedText);
            break;

        case AccessibleRole.ICON:
            toSpeak.append(name + " icon ");
            toSpeak.append(focusedText);
            break;

        case AccessibleRole.DATE:
            toSpeak.append(name + " date field ");
            toSpeak.append(selectedText);
            if (text != null) {
                toSpeak.append(" with current value " + text.getWholeText());
            }
            break;

        case AccessibleRole.LIST:
            if (busy) {
                Util.speak(orientation + "list " + name + " loading");
            } else {
                Util.speak(orientation + name + "list with " + childCount
                        + " elements" + focusedText + multiSelectableText);
                for (int i = 0; i < childCount; i++) {
                    final AccessibleContext child =
                            context.getAccessibleChildAt(i);
                    readChildElement(child);
                }
            }
            break;

        case AccessibleRole.PANEL:
            if (busy) {
                Util.speak("panel " + name + " loading");
            } else {
                Util.speak(name + " panel with " + childCount + " items");
                for (int i = 0; i < childCount; i++) {
                    final AccessibleContext child =
                            context.getAccessibleChildAt(i);
                    readChildElement(child);
                }
            }
            break;

        case AccessibleRole.GAUGE:
            if (busy) {
                Util.speak("gauge " + name + " loading");
            } else {
                Util.speak(name
                        + " gauge with value "
                        + context.getAccessibleValue()
                                .getCurrentAccessibleValue());
            }
            break;

        case AccessibleRole.PUSH_BUTTON:
            toSpeak.append(name + " button");
            toSpeak.append(focusedText);
            break;

        case AccessibleRole.MENU_ITEM:
            toSpeak.append(name + " menu item ");
            toSpeak.append(selectedText);
            break;

        case AccessibleRole.CHECKBOX:
            toSpeak.append(name);
            toSpeak.append(" check box ");
            toSpeak.append(focusedText);
            toSpeak.append(checkedText);
            break;

        case AccessibleRole.TABLE:
            if (busy) {
                Util.speak("table " + name + " loading");
            } else {
                final AccessibleTable table = context.getAccessibleTable();
                if (table != null) {
                    readTableElement(context);
                    if (selectable) {
                        ScreenReaderHandler.handleTableSelection(table);
                    }
                }
            }
            break;

        case AccessibleRole.BITMAP:
            Util.speak(name + " image");
            break;

        case AccessibleRole.COMBO:
            toSpeak.append(name + " combobox ");
            toSpeak.append(expandedText);
            if (expanded) {
                Util.speak(toSpeak.toString());
                toSpeak.setLength(0);
                for (int i = 0; i < childCount; i++) {
                    final AccessibleContext child =
                            context.getAccessibleChildAt(i);
                    readChildElement(child);
                }
            } else {
                final String value = context.getAccessibleText().getWholeText();
                if (value != null && value.length() > 0) {
                    toSpeak.append(" current value " + value);
                } else {
                    toSpeak.append(" empty");
                }
            }
            break;

        case AccessibleRole.HYPERLINK:
            toSpeak.append(name + " hyperlink ");
            break;

        case AccessibleRole.SEPARATOR:
            toSpeak.append("separator");
            break;

        case AccessibleRole.TREE_FIELD:
            Util.speak(name + " tree field" + expandableText + collapsedText
                    + expandedText + selectedText);
            for (int i = 0; i < childCount; i++) {
                final AccessibleContext child = context.getAccessibleChildAt(i);
                readChildElement(child);
            }
            break;

        case AccessibleRole.CHOICE:
            toSpeak.append(name + " choice with " + childCount + " elements");
            toSpeak.append(focusedText);
            toSpeak.append(expandedText);
            if (expanded) {
                Util.speak(toSpeak.toString());
                toSpeak.setLength(0);
                for (int i = 0; i < childCount; i++) {
                    final AccessibleContext child =
                            context.getAccessibleChildAt(i);
                    readChildElement(child);
                }
            } else {
                toSpeak.append(" current value "
                        + context.getAccessibleSelectionAt(0)
                                .getAccessibleName());
            }
            break;
        }

        Util.speak(toSpeak.toString());
    }

    /**
     * Reads an accessible table element
     * 
     * @param context
     *            The accessible element to read
     */
    static void readTableElement(final AccessibleContext context) {
        if (!(context instanceof AccessibleTable)) {
            return;
        }

        final AccessibleTable table = (AccessibleTable) context;

        // Evaluate number of columns and rows in the accessible table
        final int tableColCount = table.getAccessibleColumnCount();
        final int tableRowCount = table.getAccessibleRowCount();

        String name = context.getAccessibleName();

        if (name == null) {
            name = "";
        }

        // Describe the table
        final StringBuffer tableHeader = new StringBuffer();
        tableHeader.append(name);
        tableHeader.append(" table with ");
        tableHeader.append(tableColCount);
        tableHeader.append(" columns and ");
        tableHeader.append(tableRowCount);
        tableHeader.append(" rows");
        Util.speak(tableHeader.toString());

        // Get the column headers
        final AccessibleContext[] tableColumnsNames =
                table.getAccessibleColumnHeader();

        if (tableColumnsNames != null) {
            // Read cells, column by column
            for (int i = 0; i < tableColumnsNames.length; i++) {
                final AccessibleContext column = tableColumnsNames[i];
                Util.speak("column " + (i + 1));
                if (column == null) {
                    Util.speak("empty");
                } else {
                    readChildElement(column);
                }

                // Read cells in the column
                for (int row = 0; row < tableRowCount; row++) {
                    final AccessibleContext accessibleCell =
                            table.getAccessibleAt(row, i);
                    if (accessibleCell == null) {
                        Util.speak("empty");
                    } else {
                        readChildElement(accessibleCell);
                    }
                }
            }
        } else {
            // Read cells, row by row
            Util.speak("table data");
            for (int row = 0; row < tableRowCount; row++) {
                for (int col = 0; col < tableColCount; col++) {
                    final AccessibleContext accessibleCell =
                            table.getAccessibleAt(row, col);
                    if (accessibleCell == null) {
                        Util.speak("empty");
                    } else {
                        readChildElement(accessibleCell);
                    }
                }
            }
        }
    }
}
