/*
 * ScreenReader.java
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
import net.rim.device.api.ui.accessibility.AccessibleEventListener;
import net.rim.device.api.ui.accessibility.AccessibleRole;

/**
 * The ScreenReader class implements the AccessibleEventListener interface to
 * allow the BlackBerry UI application to retrieve accessibility information.
 */
public final class ScreenReader implements AccessibleEventListener {
    /**
     * @see AccessibleEventListener#accessibleEventOccurred(int, Object, Object,
     *      AccessibleContext)
     */
    public synchronized void accessibleEventOccurred(final int event,
            final Object oldValue, final Object newValue,
            final AccessibleContext context) {
        if (context == null) {
            return;
        }

        System.out.println("ScreenReader Context: " + context.toString());

        final int oldState =
                oldValue instanceof Integer ? ((Integer) oldValue).intValue()
                        : 0;
        final int newState =
                newValue instanceof Integer ? ((Integer) newValue).intValue()
                        : 0;

        // Handle each accessible event by role
        switch (context.getAccessibleRole()) {
        case AccessibleRole.APP_ICON:
            ScreenReaderHandler.handleAppIcon(event, oldState, newState,
                    context);
            break;

        case AccessibleRole.ICON:
            ScreenReaderHandler.handleIcon(event, oldState, newState, context);
            break;

        case AccessibleRole.CHECKBOX:
            ScreenReaderHandler.handleCheckBox(event, oldState, newState,
                    context);
            break;

        case AccessibleRole.CHOICE:
            ScreenReaderHandler
                    .handleChoice(event, oldState, newState, context);
            break;

        case AccessibleRole.COMBO:
            ScreenReaderHandler.handleCombo(event, oldState, newState, context);
            break;

        case AccessibleRole.DATE:
            ScreenReaderHandler.handleDate(event, oldState, newState, context);
            break;

        case AccessibleRole.DATE_FIELD:
            ScreenReaderHandler.handleDateField(event, oldState, newState,
                    context);
            break;

        case AccessibleRole.DIALOG:
            ScreenReaderHandler
                    .handleDialog(event, oldState, newState, context);
            break;

        case AccessibleRole.LABEL:
            ScreenReaderHandler.handleLabel(event, oldState, newState, context);
            break;

        case AccessibleRole.LIST:
            ScreenReaderHandler.handleList(event, oldState, newState, context);
            break;

        case AccessibleRole.MENU:
            ScreenReaderHandler.handleMenu(event, oldState, newState, context);
            break;

        case AccessibleRole.MENU_ITEM:
            ScreenReaderHandler.handleMenuItem(event, oldState, newState,
                    context);
            break;

        case AccessibleRole.PUSH_BUTTON:
            ScreenReaderHandler.handlePushButton(event, oldState, newState,
                    context);
            break;

        case AccessibleRole.RADIO_BUTTON:
            ScreenReaderHandler.handleRadioButton(event, oldState, newState,
                    context);
            break;

        case AccessibleRole.TEXT_FIELD:
            ScreenReaderHandler.handleTextField(event, oldValue, newValue,
                    context);
            break;

        case AccessibleRole.SCREEN:
            // Check that screen really changed.
            ScreenReaderHandler
                    .handleScreen(event, oldState, newState, context);
            break;

        case AccessibleRole.TREE_FIELD:
            ScreenReaderHandler.handleTreeField(event, oldState, newState,
                    context);
            break;

        case AccessibleRole.SYMBOL:
            ScreenReaderHandler
                    .handleSymbol(event, oldState, newState, context);
            break;

        case AccessibleRole.HYPERLINK:
            ScreenReaderHandler.handleHyperLink(event, oldState, newState,
                    context);
            break;

        case AccessibleRole.TABLE:
            ScreenReaderHandler.handleTable(event, oldState, newState, context);
            break;

        case AccessibleRole.PANEL:
            ScreenReaderHandler.handlePanel(event, oldState, newState, context);
            break;

        case AccessibleRole.BITMAP:
            ScreenReaderHandler
                    .handleBitmap(event, oldState, newState, context);
            break;

        case AccessibleRole.GAUGE:
            ScreenReaderHandler.handleGauge(event, oldState, newState, context);
            break;

        case AccessibleRole.SEPARATOR:
            // Do nothing.
            break;

        default:
            System.out.println("Unsupported accessible role: "
                    + context.getAccessibleRole());
            break;
        }
    }
}
