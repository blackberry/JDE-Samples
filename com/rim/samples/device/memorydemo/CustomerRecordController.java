/*
 * CustomerRecordController.java
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
 * A controller for a customer record. Used to display a customer record's
 * information on the screen using fields.
 */
/* package */final class CustomerRecordController {
    // Members
    // -------------------------------------------------------------------------------------
    private boolean _editable;
    private CustomerRecord _customerRecord;
    private final BasicEditField _firstName;
    private final BasicEditField _lastName;
    private final DateField _lastAccessDate;

    // Constructor
    public CustomerRecordController(CustomerRecord customerRecord,
            final boolean editable) {
        _editable = editable;

        if (editable) {
            // Record needs to be editable, but right now it is grouped and
            // read-only. So
            // get an ungrouped copy, and edit that record instead.
            _customerRecord =
                    (CustomerRecord) ObjectGroup.expandGroup(customerRecord);
            customerRecord = _customerRecord;
        } else {
            // Record is not editable, so just use the grouped object.
            _customerRecord = customerRecord;
        }

        _firstName =
                new BasicEditField("First Name: ", customerRecord
                        .getFirstName());
        _lastName =
                new BasicEditField("Last Name: ", customerRecord.getLastName());
        _lastAccessDate =
                new DateField("Last Access Date: ", customerRecord
                        .getLastAccessDate(), DateField.DATE);

        setFieldsEditable(editable);
    }

    /**
     * Retrieves the customer record's information in a vector of fields so they
     * can be displayed and/or edited.
     * 
     * @return The vector of fields.
     */
    public Vector getFields() {
        final Vector fields = new Vector();

        fields.addElement(_firstName);
        fields.addElement(_lastName);
        fields.addElement(_lastAccessDate);

        return fields;
    }

    /**
     * Retrieves a customer record that is updated with the information
     * currently on the screen.
     * 
     * @return The updated customer record.
     * @throws IllegalStateException
     *             If an attempt is made to retrieve an updated customer record
     *             when it is not editable.
     */
    public CustomerRecord getUpdatedCustomerRecord() {
        if (_editable) {
            _customerRecord.setFirstName(_firstName.getText());
            _customerRecord.setLastName(_lastName.getText());
            _customerRecord.setLastAccessDate(_lastAccessDate.getDate());

            return _customerRecord;
        }

        throw new IllegalStateException(
                "Cannot retrieve an updated record from a non-editable screen.");
    }

    /**
     * Makes this controller editable, allowing the customer record to be
     * changed.
     */
    public void makeEditable() {
        if (!_editable) {
            _editable = true;

            // Make an ungrouped copy of the customer record, which can then be
            // edited.
            _customerRecord =
                    (CustomerRecord) ObjectGroup.expandGroup(_customerRecord);
            setFieldsEditable(true);
        }
    }

    /**
     * Sets the fields in this controller to be editable or non-editable.
     * 
     * @param editable
     *            Whether or not the fields should be editable.
     */
    private void setFieldsEditable(final boolean editable) {
        _firstName.setEditable(editable);
        _lastName.setEditable(editable);
        _lastAccessDate.setEditable(editable);
    }
}
