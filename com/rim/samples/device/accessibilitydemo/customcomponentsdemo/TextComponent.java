/*
 * TextComponent.java
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

import java.util.Vector;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.AccessibleEventDispatcher;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleRole;
import net.rim.device.api.ui.accessibility.AccessibleState;
import net.rim.device.api.ui.accessibility.AccessibleTable;
import net.rim.device.api.ui.accessibility.AccessibleText;
import net.rim.device.api.ui.accessibility.AccessibleValue;
import net.rim.device.api.ui.text.TextFilter;

/**
 * Sample implementation for a textual accessible UI component. Provides the
 * screen reader application with the text data contained in the the custom
 * field and information about changes to the textual data. This field's text is
 * split up into lines and words
 */
public final class TextComponent extends Field implements AccessibleContext,
        AccessibleText {
    private final String _text;
    private int _state;

    // Lines and words are stored in Vectors so that a line or word at a given
    // index can be returned by the AccessibleText.getAtIndex() method.
    private Vector _lines; // Contains the text split into lines based on
                           // component's width
    private Vector _words; // Contains the text split into words based on
                           // whitespaces

    /**
     * Constructs a new TextComponent
     * 
     * @param text
     *            The text to be displayed by this component
     */
    public TextComponent(final String text) {
        super(Field.FOCUSABLE);

        _text = text;
    }

    // *********************** Field implementation ****************************

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
        return Math.max(20, _lines.size() * 30);
    }

    /**
     * @see Field#layout(width, height)
     */
    protected void layout(final int width, final int height) {
        // Split text into lines based on the given width
        final StringBuffer currentLine = new StringBuffer();
        final Font font = getFont();
        _lines = new Vector();
        _words = new Vector();

        // Add words one by one and form lines
        final TextComponentStringTokenizer tokenizer =
                new TextComponentStringTokenizer(_text);
        while (tokenizer.hasMoreTokens()) {
            final String word = tokenizer.nextToken();
            _words.addElement(word);

            if (font.getAdvance(currentLine.toString() + word) < width) {
                // Current word still fits into the line, add it
                currentLine.append(word);
            } else {
                // The word doesn't fit, make a new line
                _lines.addElement(currentLine.toString());
                currentLine.setLength(0);
                currentLine.append(word);
            }
        }

        if (currentLine.length() > 0) {
            _lines.addElement(currentLine.toString());
        }

        setExtent(getPreferredWidth(), getPreferredHeight());
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
     * @see Field#onFocus(int)
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

    /**
     * @see Field#paint(Graphics)
     */
    protected void paint(final Graphics graphics) {
        int y = 0;
        final int fontHeight = graphics.getFont().getHeight();

        // Paint text line by line.
        final int linesCount = _lines.size();
        for (int i = 0; i < linesCount; i++) {
            graphics.drawText((String) _lines.elementAt(i), 0, y);
            y += fontHeight;
        }
    }

    // ******************** AccessibleContext implementation *******************

    /**
     * @see AccessibleContext#getAccessibleText()
     */
    public AccessibleText getAccessibleText() {
        // The component implements AccessibleText
        return this;
    }

    /**
     * @see AccessibleContext#getAccessibleName()
     */
    public String getAccessibleName() {
        // The name of the sample component,
        // will be read by the reader.
        return " My Text Field ";
    }

    /**
     * @see AccessibleContext#getAccessibleParent()
     */
    public AccessibleContext getAccessibleParent() {
        // Return manager where text component is added
        final Manager manager = getManager();
        return manager != null ? manager.getAccessibleContext() : null;
    }

    /**
     * @see AccessibleContext#getAccessibleRole()
     */
    public int getAccessibleRole() {
        // Component serves as a text field.
        return AccessibleRole.TEXT_FIELD;
    }

    /**
     * @see AccessibleContext#getAccessibleStateSet()
     */
    public int getAccessibleStateSet() {
        // Text component can be focused but not edited
        final boolean focused = isFocus();

        if (focused) {
            return AccessibleState.FOCUSED | AccessibleState.FOCUSABLE;
        } else {
            return AccessibleState.FOCUSABLE;
        }
    }

    /**
     * @see AccessibleContext#isAccessibleStateSet(int)
     */
    public boolean isAccessibleStateSet(final int state) {
        return (state & getAccessibleStateSet()) != 0;
    }

    /**
     * @see AccessibleContext#getAccessibleChildAt(int)
     */
    public AccessibleContext getAccessibleChildAt(final int index) {
        // Text field doesn't have any children
        return null;
    }

    /**
     * @see AccessibleContext#getAccessibleChildCount()
     */
    public int getAccessibleChildCount() {
        // Text field doesn't have any children
        return 0;
    }

    /**
     * @see AccessibleContext#getAccessibleSelectionAt(int)
     */
    public AccessibleContext getAccessibleSelectionAt(final int index) {
        // Text field doesn't have any children
        return null;
    }

    /**
     * @see AccessibleContext#getAccessibleSelectionCount()
     */
    public int getAccessibleSelectionCount() {
        // Text field doesn't have any children
        return 0;
    }

    /**
     * @see AccessibleContext#getAccessibleTable()
     */
    public AccessibleTable getAccessibleTable() {
        // This is a text component, not a table
        return null;
    }

    /**
     * @see AccessibleContext#getAccessibleValue()
     */
    public AccessibleValue getAccessibleValue() {
        // This is a text component, no numerical values
        return null;
    }

    /**
     * @see AccessibleContext#getAccessibleChildSelected(int)
     */
    public boolean isAccessibleChildSelected(final int index) {
        // Text field doesn't have any children
        return false;
    }

    // ******************** AccessibleText implementation *******************

    /**
     * @see AccessibleText#getAtIndex(int, int)
     */
    public String getAtIndex(final int part, final int index) {
        // Return character, line or word at the given index
        switch (part) {
        case AccessibleText.CHAR:
            return String.valueOf(_text.charAt(index));

        case AccessibleText.LINE:
            return (String) _lines.elementAt(index);

        case AccessibleText.WORD:
            return (String) _words.elementAt(index);
        }

        return null;
    }

    /**
     * @see AccessibleText#getCaretPosition()
     */
    public int getCaretPosition() {
        // Our text component is not editable and doesn't support caret
        // navigation
        return 0;
    }

    /**
     * @see AccessibleText#getCharCount()
     */
    public int getCharCount() {
        // Number of characters
        return _text.length();
    }

    /**
     * @see AccessibleText#getInputFilterStyle()
     */
    public int getInputFilterStyle() {
        return TextFilter.DEFAULT;
    }

    /**
     * @see AccessibleText#getLineCount()
     */
    public int getLineCount() {
        // Number of lines in the component, based on current width
        return _lines.size();
    }

    /**
     * @see AccessibleText#getSelectionEnd()
     */
    public int getSelectionEnd() {
        // Text component doesn't have text selection feature
        return 0;
    }

    /**
     * @see AccessibleText#getSelectionStart()
     */
    public int getSelectionStart() {
        // Text component doesn't have text selection feature
        return 0;
    }

    /**
     * @see AccessibleText#getSelectionText()
     */
    public String getSelectionText() {
        // Text component doesn't have text selection feature
        return _text;
    }

    /**
     * @see AccessibleText#getWholeText()
     */
    public String getWholeText() {
        // Return the whole text
        return _text;
    }

    /**
     * A string tokenizer class used by the TextComponent class
     */
    private static final class TextComponentStringTokenizer {
        private int _currentPosition;
        private int _newPosition;
        private final int _maxPosition;
        private final String _str;
        private final String _delimiter;
        private boolean _delimsChanged;

        /**
         * Constructor
         * 
         * @param str
         *            The string to be parsed
         * @param delim
         *            The delimiters to split the string on
         * @param returnDelims
         *            Flag indicating whether to return the delimiters as tokens
         */
        private TextComponentStringTokenizer(final String str) {
            // Initialize members
            _currentPosition = 0;
            _newPosition = -1;
            _str = str;
            _maxPosition = _str.length();
            _delimiter = " ";
        }

        /**
         * Tests if there are more tokens available from this tokenizer's string
         * 
         * @return True if there is at least one token in the string after the
         *         current position, otherwise false
         */
        boolean hasMoreTokens() {
            return _currentPosition < _maxPosition;
        }

        /**
         * Returns the next token from this string tokenizer
         * 
         * @return The next token from this string tokenizer
         */
        String nextToken() {
            _currentPosition =
                    _newPosition >= 0 && !_delimsChanged ? _newPosition
                            : _currentPosition;
            _delimsChanged = false;
            _newPosition = -1;

            if (_currentPosition >= _maxPosition) {
                return null;
            }
            final int start = _currentPosition;
            _currentPosition = scanToken(_currentPosition);
            return _str.substring(start, _currentPosition);
        }

        /**
         * Returns the end position of the next token
         * 
         * @param startPos
         *            Start position of the token
         * @return The end position of the next token
         */
        private int scanToken(final int startPos) {
            int position = startPos;
            while (position < _maxPosition) {
                final char c = _str.charAt(position);

                if (_delimiter.indexOf(c) >= 0) {
                    break;
                }
                ++position;
            }
            if (startPos == position) {
                final char c = _str.charAt(position);

                if (_delimiter.indexOf(c) >= 0) {
                    ++position;
                }
            }
            return position;
        }
    }
}
