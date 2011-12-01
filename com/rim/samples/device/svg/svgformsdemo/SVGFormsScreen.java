/*
 * SVGFormsScreen.java
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

package com.rim.samples.device.svg.svgformsdemo;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.m2g.SVGAnimator;
import javax.microedition.m2g.SVGImage;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGElement;

/**
 * A MainScreen on which to render SVG elements
 */
public final class SVGFormsScreen extends MainScreen {
    // Scroll directions
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int UP = 3;
    private static final int DOWN = 4;

    // Panel Ids
    static final int BUTTON = 1;
    static final int SLIDERS = 2;
    static final int LISTS = 3;
    static final int TEXT = 4;

    // SVGImage instance to store the parsed SVG data
    private SVGImage _image;

    // SVGAnimator to obtain an SVGField
    private SVGAnimator _animator;

    // Document to hold SVGImage contents
    private Document _document;

    // Panels
    private final TabEventHandler _tabEventHandler;
    private final ButtonPanel _buttonPanel;
    private final SliderPanel _sliderPanel;
    private final ListPanel _listPanel;
    private final TextPanel _textPanel;

    // Current view
    private int _currentView = BUTTON;

    // Indicates the current mode
    private boolean _tabMode = true;
    private boolean _controlsMode = false;
    private boolean _dialogboxMode = false;

    private int _direction;

    /**
     * Creates a new SVGFormsScreen object
     */
    public SVGFormsScreen() {
        super(Screen.NO_VERTICAL_SCROLL);

        try {
            // Obtain an input stream to the SVG file
            final InputStream inputStream =
                    getClass().getResourceAsStream("/svgforms.svg");

            // Load the SVG image using the input stream connection
            _image = (SVGImage) SVGImage.createImage(inputStream, null);

            // Create an interactive SVG animator that hosts SVG field
            _animator =
                    SVGAnimator.createAnimator(_image,
                            "net.rim.device.api.ui.Field");

            // Obtain the images document
            _document = _image.getDocument();
        } catch (final IOException ioe) {
            System.out.println("Error while opening the .svg file.");
        }

        // Get the SVG field
        final Field _svgField = (Field) _animator.getTargetComponent();

        // Manager to lay out checkboxes
        final VerticalFieldManager vfm = new VerticalFieldManager();
        this.add(vfm);
        vfm.add(_svgField);

        // Initialize the event handlers
        _tabEventHandler = new TabEventHandler(this);
        _listPanel = new ListPanel(this);
        _buttonPanel = new ButtonPanel(this);
        _sliderPanel = new SliderPanel(this);
        _textPanel = new TextPanel(this);

        // Start the animator
        _animator.play();
    }

    /**
     * Returns the animator
     * 
     * @return SVGAnimator The animator
     */
    SVGAnimator getAnimator() {
        return _animator;
    }

    /**
     * Returns element corresponding to a given ID
     * 
     * @param elementId
     *            The ID of the element to return
     * @return SVGElement An SVG element corresponding to the given ID
     */
    SVGElement getElementById(final String elementId) {
        final SVGElement _svgElement =
                (SVGElement) _document.getElementById(elementId);
        return _svgElement;
    }

    /**
     * Sets focus to a specified element or null
     */
    void setFocus(final SVGElement svgElement) {
        _image.focusOn(svgElement);
    }

    /**
     * Sets the current panel view
     * 
     * @param view
     *            Integer representing button, sliders, lists, or text views
     */
    void setView(final int view) {
        this._currentView = view;
    }

    /**
     * Handles navigation movement inside the tabs
     */
    private void tabControlsNavigation(final int dx, final int dy,
            final int status, final int time) {
        switch (_direction) {
        case UP:
            break;

        case DOWN: {
            setFocus(null);

            // Set the focus to the first element of the chosen panel when
            // scroll ball direction is "down"
            switch (_currentView) {
            case BUTTON:
                _image.focusOn(_buttonPanel.getFirstElement());
                break;
            case SLIDERS:
                _image.focusOn(_sliderPanel.getFirstElement());
                break;
            case LISTS:
                _image.focusOn(_listPanel.getFirstElement());
                break;
            case TEXT:
                _image.focusOn(_textPanel.getFirstElement());
                break;
            }

            _tabMode = false;
            _controlsMode = true;
            break;
        }
        case LEFT: {
            super.navigationMovement(dx, dy, status, time);
            break;
        }
        case RIGHT: {
            // If the scroll direction is "right" on the last element then stay
            // at the last tab element.
            if (_tabEventHandler.isLastTabElement() == false) {
                super.navigationMovement(dx, dy, status, time);
            }
            break;
        }
        }
    }

    /**
     * Handles navigation movement inside a panel
     * 
     * @param svgPanel
     *            The SVGPanel corresponding to the current view
     * @param dx
     *            Magnitude of navigational motion: negative for a move left and
     *            postive for a move right
     * @param dy
     *            dy - Magnitude of navigational motion: negative for an upwards
     *            move, and positive for a downwards move
     * @param status
     *            Modifier key status at time of move
     * @param time
     *            Number of milliseconds since the device was turned on
     */
    private void panelControlsNavigation(final SVGPanel svgPanel, int dx,
            int dy, final int status, final int time) {
        /*
         * It is essential that the first panel element is never skipped. The
         * following changes to the dx/dy ensures it.
         */
        if (dx < 0) {
            dx = -1;
        }
        if (dx > 0) {
            dx = 1;
        }
        if (dy < 0) {
            dy = -1;
        }
        if (dy > 0) {
            dy = 1;
        }

        switch (_direction) {
        case UP: {
            // If the scroll direction is "up" while on the first panel element,
            // shift the focus to
            // to Tabmode and the focus to the first tab element.
            if (svgPanel.isFirstPanelElement()) {
                setFocus(null);
                _image.focusOn(_tabEventHandler.getFirstTabElement());
                _controlsMode = false;
                _tabMode = true;
            }
            super.navigationMovement(dx, dy, status, time);
            break;
        }
        case LEFT: {
            // If the scroll direction is left on the first element then do
            // nothing
            if (svgPanel.isFirstPanelElement() == false) {
                super.navigationMovement(dx, dy, status, time);
            }
            break;
        }
        case RIGHT:
        case DOWN: {
            super.navigationMovement(dx, dy, status, time);
            break;
        }
        }
    }

    /**
     * Handles navigation movement inside a ListBox/Combobox
     * 
     * @param dx
     *            Magnitude of navigational motion: negative for a move left and
     *            postive for a move right
     * @param dy
     *            dy - Magnitude of navigational motion: negative for an upwards
     *            move, and positive for a downwards move
     * @param status
     *            Modifier key status at time of move
     * @param time
     *            Number of milliseconds since the device was turned on
     */
    private void listboxNavigation(final int dx, final int dy,
            final int status, final int time) {
        switch (_direction) {
        case UP: {
            if (_listPanel.inListModeNavigation()) {
                // If the current focus element jumps beyond the list box
                // elements,
                // focus back to the top list element.
                if (_listPanel.isListBoxElement() == false) {
                    _image.focusOn(_listPanel.getFirstListElement());
                }

                // If scroll direction is "up" and the element is the first,
                // then stay there.
                if (_listPanel.getCurrentFocusElement() != _listPanel
                        .getFirstListElement()) {
                    super.navigationMovement(dx, dy, status, time);
                }
            } else if (_listPanel.inComboboxModeNavigation()) {
                // If the current focus element jumps beyond the list box
                // elements,
                // focus back to the top combobox element.
                if (_listPanel.isComboboxElement() == false) {
                    _image.focusOn(_listPanel.getFirstComboboxElement());
                }

                // If scroll direction is "up" and the element is the first,
                // then stay there
                if (_listPanel.getCurrentFocusElement() != _listPanel
                        .getFirstComboboxElement()) {
                    super.navigationMovement(dx, dy, status, time);
                }
            }
            break;
        }
        case DOWN: {
            if (_listPanel.inListModeNavigation()) {
                // If the current focus element jumps beyond the combobox
                // elements,
                // focus back to the top bottom-most element.
                if (_listPanel.isListBoxElement() == false) {
                    _image.focusOn(_listPanel.getLastListElement());
                }

                // If scroll direction is "down" and the element is the last,
                // then stay there.
                if (_listPanel.getCurrentFocusElement() != _listPanel
                        .getLastListElement()) {
                    super.navigationMovement(dx, dy, status, time);
                }
            } else if (_listPanel.inComboboxModeNavigation()) {
                // If the current focus element jumps beyond the combobox
                // elements,
                // focus back to the top bottom-most element.
                if (_listPanel.isComboboxElement() == false) {
                    _image.focusOn(_listPanel.getLastComboboxElement());
                }

                // If scroll direction is "down" and the element is the last,
                // then stay there.
                if (_listPanel.getCurrentFocusElement() != _listPanel
                        .getLastComboboxElement()) {
                    super.navigationMovement(dx, dy, status, time);
                }
            }
            break;
        }
        case RIGHT:
        case LEFT:
            break;
        }
    }

    /**
     * Handles navigation movement inside a Textbox
     */
    private void textBoxNavigation() {
        switch (_direction) {
        case LEFT:
        case RIGHT:
        case UP:
        case DOWN: {
            _image.focusOn(_textPanel.getCurrentFocusElement());
            break;
        }
        }
    }

    /**
     * Restricts the navigation mode to the dialog box area. Also sets the focus
     * to dialogbox button when in dialog box mode and to the last element when
     * in controls mode.
     * 
     * @param svgPanel
     *            The SVGPanel corresponding to the current view
     * @param dialogboxmode
     *            Boolean indicating the current dialog box mode status
     */
    void dialogboxMode(final SVGPanel svgPanel, final boolean dialogboxmode) {
        // If dialog box pops up, restrict the navigation movement within the
        // dialog box
        if (dialogboxmode == true) {
            this._controlsMode = false;
            final SVGElement dialogboxbutton =
                    this.getElementById("dialogboxbutton");
            _image.focusOn(dialogboxbutton);
        } else {
            this._controlsMode = true;
            _image.focusOn(svgPanel.getLastElement());
        }
        _dialogboxMode = dialogboxmode;
    }

    /**
     * @see net.rim.device.api.ui.Manager#sublayout(int,int)
     * @param width
     *            Width available for this screen
     * @param height
     *            Width available for this screen
     */
    protected void sublayout(final int width, final int height) {
        final int tabWidth = width / 6;
        final int slider1BasePosition = width / 14;
        final int listBasePosition = width / 14;
        final int textboxBasePosition = width / 2;
        final int dialogBasePosition = width / 10;
        final int dialogButtonBasePosition = width / 4;

        final float colorSliderBasePosition = (float) (width / 1.60);
        final float comboboxBasePosition = (float) (width / 1.75);

        /***********************************************************************
         * Border *
         ***********************************************************************/

        final SVGElement border = (SVGElement) getElementById("border");
        border.setFloatTrait("width", width - 20);
        border.setFloatTrait("x", 10);

        /***********************************************************************
         * Tab rect (section that contains the tab name) *
         ***********************************************************************/

        // Get tab rect elements
        final SVGElement buttonTabRect =
                (SVGElement) getElementById("buttontabrect");
        final SVGElement sliderTabRect =
                (SVGElement) getElementById("slidertabrect");
        final SVGElement listTabRect =
                (SVGElement) getElementById("listtabrect");
        final SVGElement textTabRect =
                (SVGElement) getElementById("texttabrect");

        // Set tab rect width
        buttonTabRect.setFloatTrait("width", tabWidth);
        sliderTabRect.setFloatTrait("width", tabWidth);
        listTabRect.setFloatTrait("width", tabWidth);
        textTabRect.setFloatTrait("width", tabWidth);

        // Set tab rect x position
        buttonTabRect.setFloatTrait("x", 20);
        sliderTabRect.setFloatTrait("x", tabWidth + 25);
        listTabRect.setFloatTrait("x", tabWidth * 2 + 30);
        textTabRect.setFloatTrait("x", tabWidth * 3 + 35);

        /***********************************************************************
         * Tab name text *
         ***********************************************************************/

        // Get tab name text elements
        final SVGElement buttonTabNameText =
                (SVGElement) getElementById("buttontabnametext");
        final SVGElement sliderTabNameText =
                (SVGElement) getElementById("slidertabnametext");
        final SVGElement listTabNameText =
                (SVGElement) getElementById("listtabnametext");
        final SVGElement textTabNameText =
                (SVGElement) getElementById("texttabnametext");

        // Set tab name text x position
        buttonTabNameText.setFloatTrait("x", 22);
        sliderTabNameText.setFloatTrait("x", tabWidth + 27);
        listTabNameText.setFloatTrait("x", tabWidth * 2 + 32);
        textTabNameText.setFloatTrait("x", tabWidth * 3 + 37);

        /***********************************************************************
         * Tab enabled (erased border underneath selected tab) *
         ***********************************************************************/

        // Get tab enabled elements
        final SVGElement buttonTabEnabled =
                (SVGElement) getElementById("buttontabenabled");
        final SVGElement sliderTabEnabled =
                (SVGElement) getElementById("slidertabenabled");
        final SVGElement listTabEnabled =
                (SVGElement) getElementById("listtabenabled");
        final SVGElement textTabEnabled =
                (SVGElement) getElementById("texttabenabled");

        // Set tab enabled widths
        buttonTabEnabled.setFloatTrait("width", tabWidth);
        sliderTabEnabled.setFloatTrait("width", tabWidth);
        listTabEnabled.setFloatTrait("width", tabWidth);
        textTabEnabled.setFloatTrait("width", tabWidth);

        // Set tab enabled x positions
        buttonTabEnabled.setFloatTrait("x", 20);
        sliderTabEnabled.setFloatTrait("x", tabWidth + 25);
        listTabEnabled.setFloatTrait("x", tabWidth * 2 + 30);
        textTabEnabled.setFloatTrait("x", tabWidth * 3 + 35);

        /***********************************************************************
         * Buttons tab *
         ***********************************************************************/

        // Get button 1 elements
        final SVGElement button1Rect =
                (SVGElement) getElementById("button1rect");
        final SVGElement button1Text =
                (SVGElement) getElementById("button1text");

        // Set button 1 positions
        button1Rect.setFloatTrait("x", width / 2 - 50);
        button1Text.setFloatTrait("x", width / 2 - 33);

        /***********************************************************************
         * Sliders tab *
         ***********************************************************************/

        // Get slider 1 elements
        final SVGElement slider1Bar = (SVGElement) getElementById("slider1bar");
        final SVGElement slider1Control =
                (SVGElement) getElementById("slider1control");
        final SVGElement progressBarRect =
                (SVGElement) getElementById("progressbarrect");
        final SVGElement progressBarStatusValue =
                (SVGElement) getElementById("progressbarstatusvalue");
        final SVGElement progressBarStatusPercent =
                (SVGElement) getElementById("progressbarstatuspercent");
        final SVGElement progressBarLine =
                (SVGElement) getElementById("progressbarline");
        final SVGElement progressBarText =
                (SVGElement) getElementById("progressbartext");

        // Get current value of progress bar
        final String percentStr = progressBarStatusValue.getTrait("#text");
        final Integer percentInteger = Integer.valueOf(percentStr);
        final int percent = percentInteger.intValue();

        // Set position of slider 1 bar and control
        slider1Control.setFloatTrait("cx", slider1BasePosition + percent);
        slider1Bar.setFloatTrait("x1", slider1BasePosition);
        slider1Bar.setFloatTrait("x2", slider1BasePosition + 100);

        // Set position of slider 1 progress bar elements
        progressBarLine.setFloatTrait("x1", slider1BasePosition);
        progressBarLine.setFloatTrait("x2", slider1BasePosition + percent);
        progressBarRect.setFloatTrait("x", slider1BasePosition);
        progressBarText.setFloatTrait("x", slider1BasePosition);
        progressBarStatusValue.setFloatTrait("x", slider1BasePosition + 105);
        progressBarStatusPercent.setFloatTrait("x", slider1BasePosition + 120);

        // Get color slider elements
        final SVGElement slider2Bar = (SVGElement) getElementById("slider2bar");
        final SVGElement slider2Control =
                (SVGElement) getElementById("slider2control");
        final SVGElement slider2Text =
                (SVGElement) getElementById("slider2text");
        final SVGElement slider3Bar = (SVGElement) getElementById("slider3bar");
        final SVGElement slider3Control =
                (SVGElement) getElementById("slider3control");
        final SVGElement slider3Text =
                (SVGElement) getElementById("slider3text");
        final SVGElement slider4Bar = (SVGElement) getElementById("slider4bar");
        final SVGElement slider4Control =
                (SVGElement) getElementById("slider4control");
        final SVGElement slider4Text =
                (SVGElement) getElementById("slider4text");

        // Calculate relative position of slider 2
        final String slider2PositionString = slider2Control.getTrait("cx");
        final Float slider2PositionFloat = Float.valueOf(slider2PositionString);
        final float slider2Position = slider2PositionFloat.floatValue();
        final String slider2BarX1PostionStr = slider2Bar.getTrait("x1");
        final Float slider2BarX1PositionFloat =
                Float.valueOf(slider2BarX1PostionStr);
        final float slider2BarX1Position =
                slider2BarX1PositionFloat.floatValue();
        final float slider2RelativePosition =
                slider2Position - slider2BarX1Position;

        // Calculate relative position of slider 3
        final String slider3PositionString = slider3Control.getTrait("cx");
        final Float slider3PositionFloat = Float.valueOf(slider3PositionString);
        final float slider3Position = slider3PositionFloat.floatValue();
        final String slider3BarX1PostionStr = slider3Bar.getTrait("x1");
        final Float slider3BarX1PositionFloat =
                Float.valueOf(slider3BarX1PostionStr);
        final float slider3BarX1Position =
                slider3BarX1PositionFloat.floatValue();
        final float slider3RelativePosition =
                slider3Position - slider3BarX1Position;

        // Calculate relative position of slider 4
        final String slider4PositionString = slider4Control.getTrait("cx");
        final Float slider4PositionFloat = Float.valueOf(slider4PositionString);
        final float slider4Position = slider4PositionFloat.floatValue();
        final String slider4BarX1PostionStr = slider4Bar.getTrait("x1");
        final Float slider4BarX1PositionFloat =
                Float.valueOf(slider4BarX1PostionStr);
        final float slider4BarX1Position =
                slider4BarX1PositionFloat.floatValue();
        final float slider4RelativePosition =
                slider4Position - slider4BarX1Position;

        // Set position of color slider controls
        slider2Control.setFloatTrait("cx", colorSliderBasePosition
                + slider2RelativePosition);
        slider3Control.setFloatTrait("cx", colorSliderBasePosition
                + slider3RelativePosition);
        slider4Control.setFloatTrait("cx", colorSliderBasePosition
                + slider4RelativePosition);

        // Set position of color slider bars
        slider2Bar.setFloatTrait("x1", colorSliderBasePosition);
        slider2Bar.setFloatTrait("x2", colorSliderBasePosition + 60);
        slider3Bar.setFloatTrait("x1", colorSliderBasePosition);
        slider3Bar.setFloatTrait("x2", colorSliderBasePosition + 60);
        slider4Bar.setFloatTrait("x1", colorSliderBasePosition);
        slider4Bar.setFloatTrait("x2", colorSliderBasePosition + 60);

        // Set position of color slider text
        slider2Text.setFloatTrait("x", colorSliderBasePosition - 45);
        slider3Text.setFloatTrait("x", colorSliderBasePosition - 45);
        slider4Text.setFloatTrait("x", colorSliderBasePosition - 45);

        // Set position of colorbox
        final SVGElement colorBox = (SVGElement) getElementById("colorbox");
        colorBox.setFloatTrait("x", colorSliderBasePosition);

        /***********************************************************************
         * Lists tab *
         ***********************************************************************/

        // List rect
        final SVGElement listRect = (SVGElement) getElementById("listrect");
        listRect.setFloatTrait("x", width / 14);

        // Get list item rect elements
        final SVGElement listItem1Rect =
                (SVGElement) getElementById("listitem1rect");
        final SVGElement listItem2Rect =
                (SVGElement) getElementById("listitem2rect");
        final SVGElement listItem3Rect =
                (SVGElement) getElementById("listitem3rect");
        final SVGElement listItem4Rect =
                (SVGElement) getElementById("listitem4rect");
        final SVGElement listItem5Rect =
                (SVGElement) getElementById("listitem5rect");

        // Set list item rect positions
        listItem1Rect.setFloatTrait("x", listBasePosition);
        listItem2Rect.setFloatTrait("x", listBasePosition);
        listItem3Rect.setFloatTrait("x", listBasePosition);
        listItem4Rect.setFloatTrait("x", listBasePosition);
        listItem5Rect.setFloatTrait("x", listBasePosition);

        // Get list item text elements
        final SVGElement listItem1Text =
                (SVGElement) getElementById("listitem1text");
        final SVGElement listItem2Text =
                (SVGElement) getElementById("listitem2text");
        final SVGElement listItem3Text =
                (SVGElement) getElementById("listitem3text");
        final SVGElement listItem4Text =
                (SVGElement) getElementById("listitem4text");
        final SVGElement listItem5Text =
                (SVGElement) getElementById("listitem5text");

        // Set list item text positions
        listItem1Text.setFloatTrait("x", listBasePosition);
        listItem2Text.setFloatTrait("x", listBasePosition);
        listItem3Text.setFloatTrait("x", listBasePosition);
        listItem4Text.setFloatTrait("x", listBasePosition);
        listItem5Text.setFloatTrait("x", listBasePosition);

        // Get combo box rect and text elements
        final SVGElement comboBoxRect =
                (SVGElement) getElementById("comboboxrect");
        final SVGElement comboBoxRect1 =
                (SVGElement) getElementById("comboboxrect1");
        final SVGElement comboBoxRect2 =
                (SVGElement) getElementById("comboboxrect2");
        final SVGElement comboBoxRect3 =
                (SVGElement) getElementById("comboboxrect3");
        final SVGElement comboBoxItemText1 =
                (SVGElement) getElementById("comboboxitemtext1");
        final SVGElement comboBoxItemText2 =
                (SVGElement) getElementById("comboboxitemtext2");
        final SVGElement comboBoxItemText3 =
                (SVGElement) getElementById("comboboxitemtext3");
        final SVGElement currentComboBoxRect =
                (SVGElement) getElementById("currentcomboboxrect");
        final SVGElement currentComboBoxText =
                (SVGElement) getElementById("currentcomboboxitemtext");
        final SVGElement button2Rect =
                (SVGElement) getElementById("button2rect");
        final SVGElement button2Text =
                (SVGElement) getElementById("button2text");

        // Set combo box rect and text positions
        comboBoxRect.setFloatTrait("x", comboboxBasePosition);
        comboBoxRect1.setFloatTrait("x", comboboxBasePosition);
        comboBoxRect2.setFloatTrait("x", comboboxBasePosition);
        comboBoxRect3.setFloatTrait("x", comboboxBasePosition);
        comboBoxItemText1.setFloatTrait("x", comboboxBasePosition + 50);
        comboBoxItemText2.setFloatTrait("x", comboboxBasePosition + 50);
        comboBoxItemText3.setFloatTrait("x", comboboxBasePosition + 50);
        currentComboBoxRect.setFloatTrait("x", comboboxBasePosition);
        currentComboBoxText.setFloatTrait("x", comboboxBasePosition + 50);
        button2Rect.setFloatTrait("x", width / 2 - 50);
        button2Text.setFloatTrait("x", width / 2 - 33);

        // Get combo box arrow elements
        final SVGElement comboBoxArrowRect =
                (SVGElement) getElementById("comboboxarrowrect");
        final SVGElement comboBoxArrowUp =
                (SVGElement) getElementById("comboboxarrowup");
        final SVGElement comboBoxArrowDown =
                (SVGElement) getElementById("comboboxarrowdown");

        // Create points string for up and down arrows
        final float point1x = comboboxBasePosition + 105;
        final float point2x = comboboxBasePosition + 110;
        final float point3x = comboboxBasePosition + 115;
        final String point1xString = Float.toString(point1x);
        final String point2xString = Float.toString(point2x);
        final String point3xString = Float.toString(point3x);
        final String upArrowPointsString =
                point1xString + ", 82, " + point2xString + ",77, "
                        + point3xString + ",82";
        final String downArrowPointsString =
                point1xString + ", 77, " + point2xString + ",82, "
                        + point3xString + ",77";

        // Set combo box arrow positions
        comboBoxArrowRect.setFloatTrait("x", comboboxBasePosition + 100);
        comboBoxArrowUp.setTrait("points", upArrowPointsString);
        comboBoxArrowDown.setTrait("points", downArrowPointsString);

        // Get dialog box elements
        final SVGElement dialogBoxRect =
                (SVGElement) getElementById("dialogboxrect");
        final SVGElement dialogBoxText1 =
                (SVGElement) getElementById("dialogboxtext1");
        final SVGElement dialogBoxText2 =
                (SVGElement) getElementById("dialogboxtext2");
        final SVGElement dialogBoxButtonRect =
                (SVGElement) getElementById("dialogboxbuttonrect");
        final SVGElement dialogBoxButtonText =
                (SVGElement) getElementById("dialogboxbuttontext");

        // Set dialog box positions
        dialogBoxRect.setFloatTrait("x", dialogBasePosition);
        dialogBoxText1.setFloatTrait("x", dialogBasePosition + 40);
        dialogBoxText2.setFloatTrait("x", dialogBasePosition + 40);
        dialogBoxButtonRect.setFloatTrait("x", dialogButtonBasePosition);
        dialogBoxButtonText.setFloatTrait("x", dialogButtonBasePosition + 5);

        /***********************************************************************
         * Text tab *
         ***********************************************************************/

        // Get text box elements
        final SVGElement textBoxLabelText =
                (SVGElement) getElementById("textboxlabeltext");
        final SVGElement textBoxRect =
                (SVGElement) getElementById("textboxrect");
        final SVGElement textBoxText =
                (SVGElement) getElementById("textboxtext");
        final SVGElement textBoxCursor =
                (SVGElement) getElementById("textboxcursor");

        // Calculate position of cursor relative to text field
        final String textBoxRectPositionString = textBoxRect.getTrait("x");
        final Float textBoxRectPositionFloat =
                Float.valueOf(textBoxRectPositionString);
        final float textBoxRectPosition = textBoxRectPositionFloat.floatValue();
        final String textBoxCursorPostionStr = textBoxCursor.getTrait("x");
        final Float textBoxCursorPositionFloat =
                Float.valueOf(textBoxCursorPostionStr);
        final float textBoxCursorPosition =
                textBoxCursorPositionFloat.floatValue();
        final float textBoxCursorRelativePosition =
                textBoxCursorPosition - textBoxRectPosition;

        // Set text box positions
        textBoxCursor.setFloatTrait("x", textboxBasePosition - 90
                + textBoxCursorRelativePosition);
        textBoxLabelText.setFloatTrait("x", textboxBasePosition - 90);
        textBoxRect.setFloatTrait("x", textboxBasePosition - 90);
        textBoxText.setFloatTrait("x", textboxBasePosition - 85);

        super.sublayout(width, height);
    }

    /**
     * Prevent the save dialog from being displayed
     * 
     * @see MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        return true;
    }

    /**
     * @see Screen#onMenu(int)
     */
    public boolean onMenu(final int instance) {
        if (instance == Menu.INSTANCE_CONTEXT) {
            return true;
        } else {
            return super.onMenu(instance);
        }
    }

    /**
     * @see Manager#navigationMovement(int, int, int, int)
     */
    protected boolean navigationMovement(final int dx, final int dy,
            final int status, final int time) {
        if (dx < 0) {
            _direction = LEFT;
        }
        if (dy < 0) {
            _direction = UP;
        }
        if (dx > 0) {
            _direction = RIGHT;
        }
        if (dy > 0) {
            _direction = DOWN;
        }

        // Control movement inside the tabs
        if (_tabMode) {
            tabControlsNavigation(dx, dy, status, time);
        } else if (_controlsMode) {
            switch (_currentView) {
            // Control movement inside the button panel
            case BUTTON: {
                panelControlsNavigation(_buttonPanel, dx, dy, status, time);
                break;
            }

                // Controls movement inside the sliders panel
            case SLIDERS: {
                // Control movement inside a slider
                if (_sliderPanel.isSliderActivated()) {
                    // If sliders are activated, the scroll will directly
                    // correspond to the chosen slider's movement
                    _sliderPanel.sliderMovementNavigation(dx, dy, status, time);
                } else {
                    panelControlsNavigation(_sliderPanel, dx, dy, status, time);
                }
                break;
            }

                // Control movement inside the list panel
            case LISTS: {
                // Control movement inside a ListBox
                if (_listPanel.isListBoxActivated()) {
                    if (_listPanel.inListModeNavigation() == false) {
                        _listPanel.listModeNavigationOn();
                        _image.focusOn(_listPanel.getFirstListElement());
                    }
                    this.listboxNavigation(dx, dy, status, time);
                }

                // Control movement inside a Combobox
                else if (_listPanel.isComboboxActivated()) {
                    if (_listPanel.inComboboxModeNavigation() == false) {
                        _listPanel.comboboxModeNavigationOn();
                        _image.focusOn(_listPanel.getFirstComboboxElement());
                    }
                    this.listboxNavigation(dx, dy, status, time);
                } else {
                    // If the listbox mode still exists then turn it off
                    if (_listPanel.inListModeNavigation() == true) {
                        _listPanel.listModeNavigationOff();
                        _image.focusOn(_listPanel.getFirstElement());
                    }

                    // If the combobox mode still exists then turn it off
                    else if (_listPanel.inComboboxModeNavigation() == true) {
                        _listPanel.comboboxModeNavigationOff();
                        _image.focusOn(_listPanel.getFirstElement());
                    }
                    panelControlsNavigation(_listPanel, dx, dy, status, time);
                }
                break;
            }

                // Control movement inside the text panel
            case TEXT: {
                if (_textPanel.isTextBoxActive()) {
                    this.textBoxNavigation();
                } else {
                    panelControlsNavigation(_textPanel, dx, dy, status, time);
                }
                break;
            }
            }
        } else if (_dialogboxMode == true) {
            // Keep the focus on to the dialog box button
            final SVGElement dialogboxbutton =
                    this.getElementById("dialogboxbutton");
            _image.focusOn(dialogboxbutton);
        }
        return true;
    }

    /**
     * @see Screen#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(final TouchEvent message) {
        switch (_currentView) {
        case SLIDERS: {
            if (message.getEvent() == TouchEvent.MOVE) {
                _sliderPanel.SliderTouchEvent(message.getX(1));
                break;
            }
        }
        }
        return super.touchEvent(message);
    }
}
