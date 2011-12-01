/*
 * ValueComponent.java
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

package com.rim.samples.device.accessibilitydemo.customcomponentsdemo;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.AccessibleEventDispatcher;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleRole;
import net.rim.device.api.ui.accessibility.AccessibleState;
import net.rim.device.api.ui.accessibility.AccessibleTable;
import net.rim.device.api.ui.accessibility.AccessibleText;
import net.rim.device.api.ui.accessibility.AccessibleValue;

/**
 * Sample implementation of a numerical accessible component. Provides the
 * screen reader application with information on the numerical data the field
 * contains. A user can change the value by pressing the U (up )and D (down)
 * keys.
 */
public final class ValueComponent extends Field implements AccessibleContext,
        AccessibleValue {
    private final int _min;
    private int _current;
    private final int _max;
    private int _state;

    /**
     * Constructs a new ValueComponent
     * 
     * @param min
     *            The minimum numerical value to be displayed in the field
     * @param current
     *            The initial numerical value to be displayed in the field
     * @param max
     *            The minimum numerical value to be displayed in the field
     */
    public ValueComponent(final int min, final int current, final int max) {
        super(Field.FOCUSABLE);

        _min = min;
        _current = current;
        _max = max;

        // The component can recieve focused
        _state = AccessibleState.FOCUSABLE;
    }

    // ********************Field implementation ********************************

    /**
     * @see Field#getAccesssibleContext()
     */
    public AccessibleContext getAccessibleContext() {
        return this;
    }

    /**
     * @see Field#getPreferredWidth()
     */
    public int getPreferredWidth() {
        return Display.getWidth();
    }

    /**
     * @see Field#getPreferredHeight()
     */
    public int getPreferredHeight() {
        return getFont().getHeight();
    }

    /**
     * @see Field#layout(int, int)
     */
    protected void layout(final int width, final int height) {
        setExtent(getPreferredWidth(), getPreferredHeight());
    }

    /**
     * @see Field#paint(Graphics)
     */
    protected void paint(final Graphics graphics) {
        // Draw the current value.
        graphics.drawText(String.valueOf(_current), getPreferredWidth() / 2, 0);

    }

    /**
     * @see Field#keyChar(char, int, int)
     */
    protected boolean keyChar(final char character, final int status,
            final int time) {
        final int previous = _current;

        // U key increases the value, D decreases
        if (character == 'u') {

            if (_current < _max) {
                _current++;
            }

            invalidate();

            // Notify screen reader that value has changed
            if (previous != _current) {
                AccessibleEventDispatcher.dispatchAccessibleEvent(
                        AccessibleContext.ACCESSIBLE_VALUE_CHANGED,
                        new Integer(previous), new Integer(_current), this);
            }

            return true;
        } else if (character == 'd') {
            if (_current > _min) {
                _current--;
            }
            invalidate();

            // Notify screen reader that value has changed
            if (previous != _current) {
                AccessibleEventDispatcher.dispatchAccessibleEvent(
                        AccessibleContext.ACCESSIBLE_VALUE_CHANGED,
                        new Integer(previous), new Integer(_current), this);
            }

            return true;
        } else {
            return super.keyChar(character, status, time);
        }
    }

    /**
     * @see Field#onFocus(int)
     */
    protected void onFocus(final int direction) {
        super.onFocus(direction);

        // Update accessible state and notify screen reader
        final int oldState = _state;
        _state = _state | AccessibleState.FOCUSED;
        AccessibleEventDispatcher.dispatchAccessibleEvent(
                AccessibleContext.ACCESSIBLE_STATE_CHANGED, new Integer(
                        oldState), new Integer(_state), this);
    }

    /**
     * @see Field#onUnFocus()
     */
    protected void onUnfocus() {
        super.onUnfocus();

        // Update accessible state and notify screen reader
        final int oldState = _state;
        _state = _state & ~AccessibleState.FOCUSED;
        AccessibleEventDispatcher.dispatchAccessibleEvent(
                AccessibleContext.ACCESSIBLE_STATE_CHANGED, new Integer(
                        oldState), new Integer(_state), this);
    }

    // ********************** AccessibleContext implementation *****************

    /**
     * @see AccessibleContext#getAccessibleName()
     */
    public String getAccessibleName() {
        return " My Gauge Field ";
    }

    /**
     * @see AccessibleContext#getAccessibleValue()
     */
    public AccessibleValue getAccessibleValue() {
        // The component implements AccessibleValue itself
        return this;
    }

    /**
     * @see AccessibleContext#getAccessibleParent()
     */
    public AccessibleContext getAccessibleParent() {
        // Return manager where value component is added
        final Manager manager = getManager();

        return manager != null ? manager.getAccessibleContext() : null;
    }

    /**
     * @see AccessibleContext#getAccessibleRole()
     */
    public int getAccessibleRole() {
        // This is a gauge field, user can change its value by
        // pressing U (up) and D (down) keys.
        return AccessibleRole.GAUGE;
    }

    /**
     * @see AccessibleContext#getAccessibleStateSet()
     */
    public int getAccessibleStateSet() {
        return _state;
    }

    /**
     * @see AccessibleContext#isAccessibleStateSet(int)
     */
    public boolean isAccessibleStateSet(final int state) {
        return (getAccessibleStateSet() & state) != 0;
    }

    /**
     * @see AccessibleContext#getAccessibleChildAt(int
     */
    public AccessibleContext getAccessibleChildAt(final int index) {
        // No children in the value component
        return null;
    }

    /**
     * @see AccessibleContext#getAccessibleChildCount()
     */
    public int getAccessibleChildCount() {
        // No children in the value component
        return 0;
    }

    /**
     * @see AccessibleContext#getAccessibleSelectionAt(int)
     */
    public AccessibleContext getAccessibleSelectionAt(final int index) {
        // No children in the value component
        return null;
    }

    /**
     * @see AccessibleContext#getAccessibleSelectionCount()
     */
    public int getAccessibleSelectionCount() {
        // No children in the value component
        return 0;
    }

    /**
     * @see AccessibleContext#isAccessibleChildSelected(int)
     */
    public boolean isAccessibleChildSelected(final int index) {
        // No children in the value component
        return false;
    }

    /**
     * @see AccessibleContext#getAccessibleTable()
     */
    public AccessibleTable getAccessibleTable() {
        // This is a numerical component, not a table
        return null;
    }

    /**
     * @see AccessibleContext#getAccessibleText()
     */
    public AccessibleText getAccessibleText() {
        // This is a numerical component, not text
        return null;
    }

    // *************************************** AccessibleValue implementation

    /**
     * @see AccessibleValue#getCurrentAccessibleValue()
     */
    public int getCurrentAccessibleValue() {
        // Return current value for this field
        return _current;
    }

    /**
     * @see AccessibleValue#getMaxAccessibleValue()
     */
    public int getMaxAccessibleValue() {
        // Return maximum value for this field
        return _max;
    }

    /**
     * @see AccessibleValue#getMinAccessibleValue()
     */
    public int getMinAccessibleValue() {
        // Return minimum value for this field
        return _min;
    }
}
