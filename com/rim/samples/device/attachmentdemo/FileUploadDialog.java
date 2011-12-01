/*
 * FileUploadDialog.java
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

package com.rim.samples.device.attachmentdemo;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * Dialog to upload an attachment
 */
public class FileUploadDialog extends PopupScreen implements
        FieldChangeListener {
    private final ButtonField _okButton;
    private final ButtonField _cancelButton;
    private final BasicEditField _emailEdit;
    private final FileHolder _fileHolder;
    private final FileExplorerScreen _screen;

    /**
     * Creates a new FileUploadDialog object
     * 
     * @param fileHolder
     *            FileHolder containing information on the file to be uploaded
     * @param screen
     *            The screen that spawned this Dialog
     */
    public FileUploadDialog(final FileHolder fileHolder,
            final FileExplorerScreen screen) {
        super(new VerticalFieldManager());

        _fileHolder = fileHolder;
        _screen = screen;

        setPadding(getPaddingTop(), 0, getPaddingBottom(), 0);
        setMargin(getMarginTop(), 0, getMarginBottom(), 0);

        add(new RichTextField("Upload " + _fileHolder.getFileName(),
                Field.NON_FOCUSABLE | Field.FIELD_LEFT));
        add(new SeparatorField());

        _emailEdit = new BasicEditField("Recipient Email: ", "");
        add(_emailEdit);

        _okButton = new ButtonField("OK", Field.FIELD_HCENTER);
        _okButton.setChangeListener(this);
        _cancelButton = new ButtonField("Cancel", Field.FIELD_HCENTER);
        _cancelButton.setChangeListener(this);

        final HorizontalFieldManager hfm =
                new HorizontalFieldManager(Field.FIELD_HCENTER
                        | Field.FIELD_VCENTER);
        hfm.add(_okButton);
        hfm.add(_cancelButton);
        add(hfm);
    }

    /**
     * @see PopupScreen#sublayout(int, int)
     */
    protected void sublayout(int width, int height) {
        width -= getMarginLeft() + getMarginRight();
        height -= getMarginTop() + getMarginBottom();

        setPositionDelegate(0, 0);
        layoutDelegate(width, height);

        // Center the dialog on the screen
        final XYRect fmExtent = getDelegate().getExtent();

        final int newX = width - fmExtent.width >> 1;
        final int newY = height - fmExtent.height >> 1;

        setPosition(newX + getMarginLeft(), newY + getMarginTop());
        setExtent(fmExtent.width, fmExtent.height);
    }

    /**
     * @see FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field == _okButton) {
            final String email = _emailEdit.getText().trim();
            if (email == null || email.length() == 0) {
                _screen.displayStatus("Email address not defined");
            } else {
                try {
                    new FileUploadAction().upload(_fileHolder, email);
                    _screen.displayStatus(_fileHolder.getFileName()
                            + " uploaded");
                } catch (final Exception ex) {
                    _screen.displayStatus(ex.getMessage());
                }
            }
            super.close();
            _screen.close();
        } else if (field == _cancelButton) {
            close();
        }
    }
}
