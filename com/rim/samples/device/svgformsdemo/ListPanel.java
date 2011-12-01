/*
 * ListPanel.java
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

package com.rim.samples.device.svgformsdemo;

import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.svg.SVGElement;

/**
 * A panel containing List and Combobox
 */
class ListPanel extends SVGPanel implements org.w3c.dom.events.EventListener {
    // Listpanel elements
    private SVGElement _list;
    private SVGElement _listItem1;
    private SVGElement _listItem1_text;
    private SVGElement _listItem2;
    private SVGElement _listItem2_text;
    private SVGElement _listItem3;
    private SVGElement _listItem3_text;
    private SVGElement _listItem4;
    private SVGElement _listItem4_text;
    private SVGElement _listItem5;
    private SVGElement _listItem5_text;
    private SVGElement _combobox;
    private SVGElement _combobox_group;
    private SVGElement _combobox_downarrow;
    private SVGElement _combobox_uparrow;
    private SVGElement _combobox_currentItem;
    private SVGElement _combobox_currentItem_text;

    private SVGElement _combobox1;
    private SVGElement _combobox1_text;
    private SVGElement _combobox2;
    private SVGElement _combobox2_text;
    private SVGElement _combobox3;
    private SVGElement _combobox3_text;

    private SVGElement _dialogbox;
    private SVGElement _dialogboxbutton;
    private SVGElement _dialogbox_text1;
    private SVGElement _dialogbox_text2;

    private SVGElement _button;

    // Listbox states
    private boolean _listActivated = false;
    public boolean _listModeNavigation = false;
    private SVGElement _firstListItem, _lastListItem;
    private SVGElement _currentListItem;

    // Combobox states
    private boolean _comboboxActivated = false;
    private boolean _comboboxModeNavigation = false;
    private SVGElement _firstComboboxItem, _lastComboboxItem;
    private SVGElement _currentComboboxItem;
    private SVGElement _currentComboboxItemText;

    /**
     * Constructs a new List Panel
     * 
     * @param svgFormsScreen
     *            The applications's main screen
     */
    ListPanel(final SVGFormsScreen svgFormsScreen) {
        super(svgFormsScreen);
        initializeLists();
        this.activateLists();
        super.setFirstElement(_list);
        super.setLastElement(_button);
        this.setFirstListElement(_listItem1);
        this.setFirstComboboxElement(_combobox1);
        this.setLastListElement(_listItem5);
        this.setLastComboboxElement(_combobox3);
        _currentListItem = this.getFirstListElement();
        _currentComboboxItem = this.getFirstComboboxElement();
    }

    /**
     * Initialize list elements
     */
    private void initializeLists() {
        _list = _svgFormsScreen.getElementById("list");
        _listItem1 = _svgFormsScreen.getElementById("listitem1rect");
        _listItem1_text = _svgFormsScreen.getElementById("listitem1text");
        _listItem2 = _svgFormsScreen.getElementById("listitem2rect");
        _listItem2_text = _svgFormsScreen.getElementById("listitem2text");
        _listItem3 = _svgFormsScreen.getElementById("listitem3rect");
        _listItem3_text = _svgFormsScreen.getElementById("listitem3text");
        _listItem4 = _svgFormsScreen.getElementById("listitem4rect");
        _listItem4_text = _svgFormsScreen.getElementById("listitem4text");
        _listItem5 = _svgFormsScreen.getElementById("listitem5rect");
        _listItem5_text = _svgFormsScreen.getElementById("listitem5text");

        _combobox = _svgFormsScreen.getElementById("combobox");
        _combobox_group = _svgFormsScreen.getElementById("comboboxgroup");
        _combobox_downarrow =
                _svgFormsScreen.getElementById("comboboxarrowdown");
        _combobox_uparrow = _svgFormsScreen.getElementById("comboboxarrowup");
        _combobox_currentItem =
                _svgFormsScreen.getElementById("currentcomboboxitem");
        _combobox_currentItem_text =
                _svgFormsScreen.getElementById("currentcomboboxitemtext");

        _combobox1 = _svgFormsScreen.getElementById("comboboxrect1");
        _combobox1_text =
                (SVGElement) _svgFormsScreen
                        .getElementById("comboboxitemtext1");
        _combobox2 =
                (SVGElement) _svgFormsScreen.getElementById("comboboxrect2");
        _combobox2_text =
                (SVGElement) _svgFormsScreen
                        .getElementById("comboboxitemtext2");
        _combobox3 =
                (SVGElement) _svgFormsScreen.getElementById("comboboxrect3");
        _combobox3_text =
                (SVGElement) _svgFormsScreen
                        .getElementById("comboboxitemtext3");

        _dialogbox = (SVGElement) _svgFormsScreen.getElementById("dialogbox");
        _dialogboxbutton =
                (SVGElement) _svgFormsScreen.getElementById("dialogboxbutton");
        _dialogbox_text1 =
                (SVGElement) _svgFormsScreen.getElementById("dialogboxtext1");
        _dialogbox_text2 =
                (SVGElement) _svgFormsScreen.getElementById("dialogboxtext2");

        _button = (SVGElement) _svgFormsScreen.getElementById("button2");
    }

    /**
     * Registers the events associated with the list panel components
     */
    private void activateLists() {
        activateSVGElement(_list, this);
        activateSVGElement(_combobox, this);
        activateSVGElement(_button, this);
    }

    /**
     * Registers the events associated with the listbox elements
     */
    private void activateListBox() {
        _listActivated = true;
        activateSVGElement(_listItem1, this);
        activateSVGElement(_listItem2, this);
        activateSVGElement(_listItem3, this);
        activateSVGElement(_listItem4, this);
        activateSVGElement(_listItem5, this);
    }

    /**
     * Un-Registers the events associated with the listbox elements
     */
    private void deActivateListBox() {
        _listActivated = false;
        deActivateSVGElement(_listItem1, this);
        deActivateSVGElement(_listItem2, this);
        deActivateSVGElement(_listItem3, this);
        deActivateSVGElement(_listItem4, this);
        deActivateSVGElement(_listItem5, this);
    }

    /**
     * Registers the events associated with the combobox elements
     */
    private void activateComboboxBox() {
        _comboboxActivated = true;
        activateSVGElement(_combobox1, this);
        activateSVGElement(_combobox2, this);
        activateSVGElement(_combobox3, this);
    }

    /**
     * Un-Registers the events associated with the combobox elements
     */
    private void deActivateComboboxBox() {
        _comboboxActivated = false;
        deActivateSVGElement(_combobox1, this);
        deActivateSVGElement(_combobox2, this);
        deActivateSVGElement(_combobox3, this);
    }

    /**
     * Sets the first focusable svglist item
     * 
     * @param svgElement
     *            The element to set
     */
    private void setFirstListElement(final SVGElement svgElement) {
        _firstListItem = svgElement;
    }

    /**
     * Sets the last focussable svglist item.
     * 
     * @param svgElement
     *            The element to be set
     */
    private void setLastListElement(final SVGElement svgElement) {
        _lastListItem = svgElement;
    }

    /**
     * Returns the first list element
     * 
     * @return The first element in the list
     */
    SVGElement getFirstListElement() {
        return _firstListItem;
    }

    /**
     * Returns the last list element
     * 
     * @return The last element in the list
     */
    SVGElement getLastListElement() {
        return _lastListItem;
    }

    /**
     * Sets the first focusable combobox item
     * 
     * @param svgElement
     *            The combobox item to be set
     */
    private void setFirstComboboxElement(final SVGElement svgElement) {
        _firstComboboxItem = svgElement;
    }

    /**
     * Sets the last focusable combobox item
     * 
     * @param svgElement
     *            The combobox item to be set
     */
    private void setLastComboboxElement(final SVGElement svgElement) {
        _lastComboboxItem = svgElement;
    }

    /**
     * Returns the first combobox element
     * 
     * @return The first combobox element
     */
    SVGElement getFirstComboboxElement() {
        return _firstComboboxItem;
    }

    /**
     * Returns the last combobox element
     * 
     * @return The last combobox element
     */
    SVGElement getLastComboboxElement() {
        return _lastComboboxItem;
    }

    /**
     * Returns the status of the listbox
     * 
     * @return True if the list box is activated, otherwise false
     */
    boolean isListBoxActivated() {
        return this._listActivated;
    }

    /**
     * Returns the status of the combobox
     * 
     * @return True if the combo box is activated, otherwise false
     */
    boolean isComboboxActivated() {
        return this._comboboxActivated;
    }

    /**
     * Turns list mode navigation on
     */
    void listModeNavigationOn() {
        _listModeNavigation = true;
    }

    /**
     * Turn list mode navigation off
     */
    void listModeNavigationOff() {
        _listModeNavigation = false;
    }

    /**
     * Turns combobox mode navigation on
     */
    void comboboxModeNavigationOn() {
        _comboboxModeNavigation = true;
    }

    /**
     * Turn combobox mode navigation off
     */
    void comboboxModeNavigationOff() {
        _comboboxModeNavigation = false;
    }

    /**
     * Checks if list box navigation is on
     * 
     * @return True if list mode navigation is turned on, otherwise false
     */
    boolean inListModeNavigation() {
        return _listModeNavigation;
    }

    /**
     * Checks if combobox navigation is on.
     * 
     * @return True is combobox navigation is turned on, otherwise false
     */
    boolean inComboboxModeNavigation() {
        return _comboboxModeNavigation;
    }

    /**
     * Checks if the current element is a listbox element.
     * 
     * @return True if current element is a listbox element, otherwise false
     */
    boolean isListBoxElement() {
        final Node currentNode = _currentFocusInElement.getParentNode();
        if (currentNode.getParentNode() == (Node) _list) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the current element is a combobox element
     * 
     * @return True if current element is a combobox element, otherwise false
     */
    boolean isComboboxElement() {
        final Node currentNode = _currentFocusInElement.getParentNode();
        final Node comboboxsubgroupNode = currentNode.getParentNode();
        if (comboboxsubgroupNode.getParentNode() == (Node) _combobox) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Handles the DOMActivate event
     * 
     * @param evt
     *            The event to be handled
     */
    public void handleEvent(final Event evt) {
        // Help avoid bubbled event handling
        if (_currentEvent.equals(evt.getType())) {
            return;
        } else {
            _currentEvent = evt.getType();
        }

        // Handle the DOMFocusIn event
        if (evt.getType().equals("DOMFocusIn")) {
            _currentFocusInElement = (SVGElement) evt.getCurrentTarget();
            setCurrentFocusElement(_currentFocusInElement);
            _animator.invokeLater(new Runnable() {
                public void run() {
                    if (isListBoxActivated() || isComboboxActivated()) {
                        _currentFocusInElement.setTrait("fill", "red");
                    } else {
                        _currentFocusInElement.setFloatTrait("stroke-opacity",
                                1.0f);
                    }
                }
            });
        }
        // Handle the DOMFocusOut event
        else if (evt.getType().equals("DOMFocusOut")) {
            _currentFocusOutElement = (SVGElement) evt.getCurrentTarget();
            _animator.invokeLater(new Runnable() {
                public void run() {
                    if (isListBoxActivated() || isComboboxActivated()) {
                        _currentFocusOutElement.setTrait("fill", "#FF6600");
                    } else {
                        _currentFocusOutElement.setFloatTrait("stroke-opacity",
                                0.25f);
                    }
                }
            });
        }
        // Handle the click event
        else if (evt.getType().equals("click")) {
            _currentClickedElement = (SVGElement) evt.getCurrentTarget();
            if (_currentClickedElement == _list) {
                activateListBox();
            } else if (_currentClickedElement == _listItem1
                    || _currentClickedElement == _listItem2
                    || _currentClickedElement == _listItem3
                    || _currentClickedElement == _listItem4
                    || _currentClickedElement == _listItem5) {
                _animator.invokeLater(new Runnable() {
                    public void run() {
                        if (_currentListItem != _currentClickedElement) {
                            _currentFocusInElement.setTrait("fill", "#FF6600");
                            _currentListItem.setFloatTrait("fill-opacity",
                                    0.25f);
                            _currentListItem.setFloatTrait("stroke-opacity",
                                    0.1f);
                            _currentListItem = _currentClickedElement;
                            _currentListItem
                                    .setFloatTrait("fill-opacity", 0.5f);
                            deActivateListBox();
                        }
                    }
                });
            }

            else if (_currentClickedElement == _combobox) {
                _combobox_downarrow.setTrait("display", "none");
                _combobox_uparrow.setTrait("display", "inline");
                _combobox_currentItem.setTrait("display", "none");
                _combobox_group.setTrait("display", "inline");
                _animator.invokeLater(new Runnable() {
                    public void run() {
                        _currentComboboxItem
                                .setFloatTrait("fill-opacity", 0.5f);
                        activateComboboxBox();
                    }
                });
            } else if (_currentClickedElement == _combobox1
                    || _currentClickedElement == _combobox2
                    || _currentClickedElement == _combobox3) {
                _animator.invokeLater(new Runnable() {
                    public void run() {
                        if (_currentComboboxItem != _currentClickedElement) {
                            _currentFocusInElement.setTrait("fill", "#FF6600");
                            _currentComboboxItem.setFloatTrait(
                                    "stroke-opacity", 0.1f);
                            _currentComboboxItem.setFloatTrait("fill-opacity",
                                    0.25f);
                            _currentComboboxItem = _currentClickedElement;
                            _currentComboboxItem.setFloatTrait("fill-opacity",
                                    0.5f);
                        }
                        _combobox_downarrow.setTrait("display", "inline");
                        _combobox_uparrow.setTrait("display", "none");
                        _combobox_currentItem.setTrait("display", "inline");
                        _combobox_group.setTrait("display", "none");

                        if (_currentClickedElement == _combobox1) {
                            _currentComboboxItemText = _combobox1_text;
                        } else if (_currentClickedElement == _combobox2) {
                            _currentComboboxItemText = _combobox2_text;
                        } else if (_currentClickedElement == _combobox3) {
                            _currentComboboxItemText = _combobox3_text;
                        }
                        _combobox_currentItem_text.setTrait("#text",
                                _currentComboboxItemText.getTrait("#text"));
                        deActivateComboboxBox();
                    }
                });
            } else if (_currentClickedElement == _button) {
                activateSVGElement(_dialogboxbutton, this);
                _svgFormsScreen.dialogboxMode(this, true);
                _animator.invokeLater(new Runnable() {
                    public void run() {
                        String item = "Item = ";
                        String quantity = "Quantity = ";
                        _dialogbox.setTrait("display", "inline");
                        String itemName = "";
                        if (_currentListItem == _listItem1) {
                            itemName = _listItem1_text.getTrait("#text");
                        } else if (_currentListItem == _listItem2) {
                            itemName = _listItem2_text.getTrait("#text");
                        } else if (_currentListItem == _listItem3) {
                            itemName = _listItem3_text.getTrait("#text");
                        } else if (_currentListItem == _listItem4) {
                            itemName = _listItem4_text.getTrait("#text");
                        } else if (_currentListItem == _listItem5) {
                            itemName = _listItem5_text.getTrait("#text");
                        }
                        item = item + itemName;
                        quantity =
                                quantity
                                        + _combobox_currentItem_text
                                                .getTrait("#text") + "  ";
                        _dialogbox_text1.setTrait("#text", item);
                        _dialogbox_text2.setTrait("#text", quantity);
                    }
                });
            } else if (_currentClickedElement == _dialogboxbutton) {
                deActivateSVGElement(_dialogboxbutton, this);
                _svgFormsScreen.dialogboxMode(this, false);
                _animator.invokeLater(new Runnable() {
                    public void run() {
                        _dialogbox.setTrait("display", "none");
                    }
                });
            }
        }
    }
}
