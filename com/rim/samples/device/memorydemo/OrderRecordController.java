/*
 * OrderRecordController.java
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

package com.rim.samples.device.memorydemo;

import java.util.Vector;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.DateField;

/**
 * A controller for an order record. Used to display an order record's
 * information on the screen using fields.
 */
/* package */final class OrderRecordController {
    // Members
    // -------------------------------------------------------------------------------------
    private boolean _editable;
    private OrderRecord _orderRecord;
    private final DateField _date;
    private final BasicEditField _company;
    private final BasicEditField _product;
    private final BasicEditField _numOrdered;

    OrderRecordController(OrderRecord orderRecord, final boolean editable) {
        _editable = editable;

        if (editable) {
            // Record needs to be editable, but right now it is grouped and
            // read-only.
            // So get an upgrouped copy, and edit that record instead.
            _orderRecord = (OrderRecord) ObjectGroup.expandGroup(orderRecord);
            orderRecord = _orderRecord;
        } else {
            // Record is not editable, so just use the grouped object.
            _orderRecord = orderRecord;
        }

        _orderRecord = orderRecord;

        _date =
                new DateField("Order Date: ", orderRecord.getDate(),
                        DateField.DATE);
        _company = new BasicEditField("Company: ", orderRecord.getCompany());
        _product = new BasicEditField("Product: ", orderRecord.getProduct());
        _numOrdered =
                new BasicEditField("Number Ordered: ", Integer
                        .toString(orderRecord.getNumOrdered()),
                        Integer.MAX_VALUE, BasicEditField.FILTER_INTEGER);

        setFieldsEditable(editable);
    }

    /**
     * Retrieves the order record's information in a vector of fields so they
     * can be displayed and/or edited.
     * 
     * @return The vector of fields.
     */
    Vector getFields() {
        final Vector fields = new Vector();

        fields.addElement(_date);
        fields.addElement(_company);
        fields.addElement(_product);
        fields.addElement(_numOrdered);

        return fields;
    }

    /**
     * Retrieves an order record that is updated with the information currently
     * on the screen.
     * 
     * @return The updated order record.
     * @throws IllegalStateException
     *             If an attempt is made to retrieve an updated order record
     *             when the controller is not editable.
     */
    OrderRecord getUpdatedOrderRecord() {
        if (_editable) {
            _orderRecord.setDate(_date.getDate());
            _orderRecord.setCompany(_company.getText());
            _orderRecord.setProduct(_product.getText());
            _orderRecord.setNumOrdered(Integer.parseInt(_numOrdered.getText()));

            return _orderRecord;
        }

        throw new IllegalStateException(
                "Cannot retrieve an updated record from a non-editable screen.");
    }

    /**
     * Makes this controller editable, allowing the order record to be changed.
     */
    void makeEditable() {
        if (!_editable) {
            _editable = true;

            // Make an ungrouped copy of the order record, which can then be
            // edited.
            _orderRecord = (OrderRecord) ObjectGroup.expandGroup(_orderRecord);

            setFieldsEditable(true);
        }
    }

    /**
     * Sets the fields of this controller to be editable or non-editable.
     * 
     * @param editable
     *            Whether or not the fields should be editable.
     */
    private void setFieldsEditable(final boolean editable) {
        _date.setEditable(editable);
        _company.setEditable(editable);
        _product.setEditable(editable);
        _numOrdered.setEditable(editable);
    }
}
