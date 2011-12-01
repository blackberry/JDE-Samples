/*
 * CustomComponentsDemoStringTokenizer.java
 *
 * Research In Motion Limited proprietary and confidential
 * Copyright Research In Motion Limited, 2008-2008
 */

package com.rim.samples.device.accessibilitydemo.customcomponentsdemo;

class CustomComponentsDemoStringTokenizer 
{
    private int _currentPosition;
    private int _newPosition;
    private int _maxPosition;
    private String _str;
    private String _delimiters;
    private boolean _retDelims;
    private boolean _delimsChanged;
    private char _maxDelimChar;    

    CustomComponentsDemoStringTokenizer(String str, String delim, boolean returnDelims)
    {
        _currentPosition = 0;
        _newPosition = -1;
        _str = str;
        _maxPosition = _str.length();
        _delimiters = delim;
        _retDelims = returnDelims;
        setMaxDelimChar();
    }   
    
    boolean hasMoreTokens()
    {
        _newPosition = skipDelimiters(_currentPosition);
        return (_newPosition < _maxPosition);
    }
    
    public String nextToken()
    {
        _currentPosition = (_newPosition >= 0 && !_delimsChanged) ?  _newPosition : skipDelimiters(_currentPosition);
        _delimsChanged = false;
        _newPosition = -1;

        if (_currentPosition >= _maxPosition)
            return null;
        int start = _currentPosition;
        _currentPosition = scanToken(_currentPosition);
        return _str.substring(start, _currentPosition);
    }
    
    private int skipDelimiters(int startPos)
    {
        int position = startPos;
        while (!_retDelims && position < _maxPosition)
        {
            char c = _str.charAt(position);
            if ((c > _maxDelimChar) || (_delimiters.indexOf(c) < 0))
            {
                break;
            }
            ++position;
        }
        return position;
    }
    
    private void setMaxDelimChar()
    {
        if (_delimiters == null)
        {
            _maxDelimChar = 0;
            return;
        }
        char m = 0;
        int delimitersLength = _delimiters.length();
        for (int i = 0; i < delimitersLength; ++i)
        {
            char c = _delimiters.charAt(i);
            if (m < c)
            {
                m = c;
            }
        }
        _maxDelimChar = m;
    }
    
    private int scanToken(int startPos)
    {
        int position = startPos;
        while (position < _maxPosition)
        {
            char c = _str.charAt(position);
            if ((c <= _maxDelimChar) && (_delimiters.indexOf(c) >= 0))
            {
                break;
            }
            ++position;
        }
        if (_retDelims && (startPos == position))
        {
            char c = _str.charAt(position);
            if ((c <= _maxDelimChar) && (_delimiters.indexOf(c) >= 0))
            {
                ++position;
            }
        }
        return position;
    }
}
