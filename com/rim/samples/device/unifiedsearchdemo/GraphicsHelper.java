/*
 * GraphicsHelper.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.unifiedsearchdemo;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.SimpleSortingIntVector;
import net.rim.device.api.util.StringUtilities;

/**
 * Helper class to highlight keyword text
 */
public class GraphicsHelper {
    private int[] _highlightStartIndex = new int[0];
    private int[] _highlightLengths = new int[0];
    private int[] _highlightOffsets = new int[0];
    private String[] _highlightWords = new String[0];
    private String[] _queryWords = new String[0];
    private final SimpleSortingIntVector _highlightStartIndexIntVector =
            new SimpleSortingIntVector();
    private final IntVector _highlightLengthsIntVector = new IntVector(0);
    private String _currText;
    private String _currQuery;

    /**
     * Draws text with bolded highlight regions
     * 
     * @param g
     *            Graphics context
     * @param text
     *            Text for which to highlight keywords
     * @param query
     *            Keyword to be highlighted
     * @param rect
     *            The extent in which to draw
     */
    public void drawTextWithHighlight(final Graphics g, final String text,
            final String query, final XYRect rect) {
        final boolean isNewQuery =
                _currQuery == null ? true : !_currQuery.equals(query);
        final boolean isNewText =
                _currText == null ? true : !_currText.equals(text);

        String[] queryWords = _queryWords;

        if (isNewQuery || isNewText) {
            _currQuery = query;
            queryWords =
                    query == null ? null : StringUtilities.stringToWords(query
                            .toLowerCase().trim());
        }

        drawTextWithHighlight(g, text, queryWords, rect);
    }

    /**
     * Draws text with bolded highlight regions
     * 
     * @param g
     *            Graphics context
     * @param text
     *            Text for which to highlight keywords
     * @param queryWords
     *            Keywords to be highlighted
     * @param rect
     *            The extent in which to draw
     */
    public void drawTextWithHighlight(final Graphics g, final String text,
            final String[] queryWords, final XYRect rect) {
        boolean isNewQuery =
                _queryWords == null ? true
                        : _queryWords.length != queryWords.length;
        final boolean isNewText =
                _currText == null ? true : !_currText.equals(text);

        if (!isNewQuery) {
            // Before accepting that it is not a new query, verify that each
            // query word is identical.
            if (queryWords.length == _queryWords.length) {
                for (int i = 0; i < queryWords.length; i++) {
                    isNewQuery |= !queryWords[i].equals(_queryWords[i]);
                }
            }
        }

        if (isNewQuery || isNewText) {
            _queryWords = queryWords;
            _currText = text;

            // Since this is a new highlight request, create the highlight
            // regions, which are the highlight offsets and lengths.
            createHighlightRegions(_queryWords, text);
        }

        if (_highlightStartIndex.length > 0) {
            drawTextWithHighlight(g, text, rect, _highlightStartIndex,
                    _highlightLengths);
        } else {
            g.drawText(text, rect.x, rect.y);
        }
    }

    /**
     * Draws text with bolded highlight regions
     * 
     * @param g
     *            Graphics context
     * @param text
     *            Text for which to highlight keywords
     * @param rect
     *            The extent in which to draw
     * @param highlightStartIndex
     *            Index at which to start highlighting
     * @param highlightLength
     *            Length of region to highlight
     */
    public void drawTextWithHighlight(final Graphics g, final String text,
            final XYRect rect, final int[] highlightStartIndex,
            final int[] highlightLength) {
        final int x = rect.x;
        final int y = rect.y;
        final int textWidth = rect.width;

        final Font oldFont = g.getFont();

        final Font font = g.getFont();
        final Font highlightFont =
                font.derive(font.isBold() ? Font.EXTRA_BOLD : Font.BOLD);

        try {
            // Just draw the text normally if there's no highlight region
            final int numHighlightStartIndexes =
                    highlightStartIndex == null ? 0
                            : highlightStartIndex.length;

            if (numHighlightStartIndexes == 0) {
                g.setFont(oldFont);
                g.drawText(text, 0, text.length(), x, y, DrawStyle.ELLIPSIS,
                        textWidth);
                return;
            }

            int textStartIndex = 0;
            int offsetX = 0;

            boolean hasEndText = true;

            /**
             * <pre>
             *   Text Highlight Algorithm
             *   
             *   Given the set of highlightStartIndex values and the
             *   highlightLength values, we already know exactly which
             *   portions of the text need to be highlighted and how many
             *   characters to highlight.
             * 
             *   The algorithm below uses the aforementioned values to
             *   determine the highlighted and non-highlighted regions of
             *   text. Then in a single pass through the text, it draws the
             *   text regions as required.
             * 
             *   The text highlighting algorithm works as follows by iterating
             *   through the set of highlight indexes and performing the required
             *   text highlighting operations. These operations are as follows:
             * 
             *   1. Calculate the start index for the current portion of the text
             *      to draw.
             * 
             *   2. Determine if the start index for the text to draw is
             *      equal to the current highlight index. If it's not equal,
             *      the characters from the start index to the current highlight
             *      index are not highlighted. If it is equal, then the amount of
             *      characters to highlight is dictated by the highlight length.
             *       
             *   3. Draw the text before the current highlight index in a
             *      non-highlighted font.
             * 
             *   4. Draw the text from the highlight index to the
             *      highlight index + highlight length in a highlighted font.
             *      Repeat steps 1 to 4 until the last highlight region is drawn.
             *   
             *   5. There may be text after the last highlight index that hasn't
             *      been drawn. Therefore, there is a check to see if there is
             *      any such text and if so, it is drawn in the non-highlighted font.
             * </pre>
             */

            int i = 0;
            for (i = 0; i < numHighlightStartIndexes; i++) {
                if (i != 0) {
                    // Calculate the start index for current portion of the
                    // text to draw.
                    textStartIndex =
                            highlightStartIndex[i - 1] + highlightLength[i - 1];
                }

                // Draw the text before the highlight region
                if (highlightStartIndex[i] != textStartIndex) {
                    g.setFont(oldFont);
                    g.drawText(text, textStartIndex, highlightStartIndex[i]
                            - textStartIndex, x + offsetX, y, 0, textWidth
                            - offsetX);
                }

                // Draw the highlighted text
                offsetX +=
                        oldFont.getAdvance(text, textStartIndex,
                                highlightStartIndex[i] - textStartIndex);
                g.setFont(highlightFont);
                g.drawText(text, highlightStartIndex[i], highlightLength[i], x
                        + offsetX, y, 0, textWidth - offsetX);

                final int nextOffsetX =
                        highlightFont.getAdvance(text, highlightStartIndex[i],
                                highlightLength[i]);
                offsetX += nextOffsetX;
            }

            i--;

            // Draw the text after the highlight region
            hasEndText =
                    highlightStartIndex[i] + highlightLength[i] != text
                            .length();
            if (hasEndText) {
                g.setFont(oldFont);
                g.drawText(text, highlightStartIndex[i] + highlightLength[i],
                        text.length() - highlightStartIndex[i]
                                - highlightLength[i], x + offsetX, y, 0,
                        textWidth - offsetX);
            }

        } finally {
            g.setFont(oldFont);
        }
    }

    /**
     * Creates highlight regions for the given text based on the queryWords
     * parameter
     * 
     * @param queryWords
     *            Keywords for which to create highlight regions
     * @param text
     *            The text for which to create highlight regions
     */
    private void createHighlightRegions(final String[] queryWords,
            final String text) {
        _highlightStartIndex = new int[0];
        _highlightLengths = new int[0];
        _highlightWords = new String[0];
        _highlightStartIndexIntVector.removeAllElements();
        _highlightLengthsIntVector.removeAllElements();

        if (queryWords != null) {
            final int len = queryWords.length;

            // Create the initial set of highlight offsets based on the
            // the index of each word in the text value. These are equal
            // to the index of each word in the text.
            StringUtilities.stringToWords(text, _highlightWords, 0);
            _highlightOffsets = new int[_highlightWords.length];
            StringUtilities.stringToWords(text, _highlightOffsets, 0);

            boolean noFurtherMatches;

            // Iterate through each of the query words and determine the
            // corresponding text highlight start position and highlight
            // length.
            for (int i = 0; i < len; i++) {
                noFurtherMatches = false;

                for (int j = 0; j < _highlightOffsets.length
                        && !noFurtherMatches; j++) {

                    final int highlightIndex =
                            getHighlightStartIndex(text, i,
                                    _highlightOffsets[j]);

                    if (highlightIndex == -1) {
                        // If the given query was not found at the current
                        // offset position
                        // then there are no further matches for this query word
                        // and the
                        // algorithm can exit early.
                        noFurtherMatches = true;
                    } else if (j == _highlightOffsets.length - 1
                            || highlightIndex < _highlightOffsets[j + 1]) {
                        int position =
                                _highlightStartIndexIntVector
                                        .binarySearch(
                                                highlightIndex,
                                                SimpleSortingIntVector.SORT_TYPE_NUMERIC);

                        if (position < 0) {
                            position = -position;
                            if (position > _highlightStartIndexIntVector.size()
                                    || highlightIndex < _highlightStartIndexIntVector
                                            .elementAt(position - 1)) {
                                --position;
                            }

                            // Since the position is less than 0, the highlight
                            // index is not yet present in the highlight start
                            // index array. Therefore, we add the the current
                            // highlight index value to the collection of
                            // highlight start indexes.
                            _highlightStartIndexIntVector.insertElementAt(
                                    highlightIndex, position);

                            // Next we add the current query index as a
                            // place holder for the for the query length.
                            // This will be converted to the actual length
                            // of the highlight region later.
                            _highlightLengthsIntVector.insertElementAt(i,
                                    position);
                        } else {
                            final int prevHighlightLength =
                                    _queryWords[_highlightLengthsIntVector
                                            .elementAt(position)].length();
                            final int currHighlightLength =
                                    _queryWords[i].length();

                            // If the query is found at an existing highlight
                            // position in the text, and the current query is
                            // larger than the query previously associated with
                            // the found highlight position, then the algorithm
                            // replaces the previous query index with the
                            // current
                            // query index.
                            if (currHighlightLength > prevHighlightLength) {
                                _highlightLengthsIntVector.setElementAt(i,
                                        position);
                            }
                        }
                    }
                }
            }

            if (_highlightStartIndexIntVector.size() > 0) {
                final int size = _highlightStartIndexIntVector.size();
                _highlightStartIndex = new int[size];
                _highlightStartIndexIntVector.copyInto(_highlightStartIndex);
                _highlightLengths = new int[size];
                _highlightLengthsIntVector.copyInto(_highlightLengths);
                for (int i = 0; i < _highlightStartIndex.length; i++) {
                    // For each highlight length, the algorithm now replaces the
                    // query index with the actual highlight length. This
                    // highlight
                    // length is equal to the query length.
                    final int highlightLength =
                            _queryWords[_highlightLengths[i]].length();
                    _highlightLengths[i] = highlightLength;
                }
            }
        }
    }

    /**
     * Retrieves the highlight start index of a given string
     * 
     * @param text
     *            The text containing a region to highlight
     * @param queryIndex
     *            Index of the query word in the _queryWords array to highlight
     * @param fromIndex
     *            Index at which to start looking for for a highlight region in
     *            the given text
     * @return The highlight start index for the given text
     */
    private int getHighlightStartIndex(final String text, final int queryIndex,
            final int fromIndex) {
        final String lCaseElement = text.toLowerCase();

        int startIndex =
                lCaseElement.indexOf(_queryWords[queryIndex], fromIndex);

        if (startIndex > 0) {
            int realStartIndex = startIndex;
            char prevChar = lCaseElement.charAt(realStartIndex - 1);

            // Ensure that we don't incorrectly find a mid-phrase match
            while (realStartIndex != -1 && isAlphaNumericChar(prevChar)) {
                realStartIndex =
                        lCaseElement.indexOf(_queryWords[queryIndex],
                                realStartIndex + 1);

                if (realStartIndex != -1) {
                    prevChar = lCaseElement.charAt(realStartIndex - 1);
                }
            }

            startIndex = realStartIndex;
        }

        return startIndex;
    }

    /**
     * Checks whether a given char is alphanumeric
     * 
     * @param ch
     *            The char to evaluate
     * @return True if the char is alphanumeric, otherwise false
     */
    private static boolean isAlphaNumericChar(final char ch) {
        return Character.isDigit(ch) || CharacterUtilities.isLetter(ch);
    }
}
