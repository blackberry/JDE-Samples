/*
 * MMSDemoSendScreen.java
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

package com.rim.samples.device.mmsdemo;

import javax.wireless.messaging.MessagePart;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.StringProvider;

/**
 * The client screen for the MMS Demo
 */
public final class MMSDemoSendScreen extends MainScreen {
    private EditField _subjectField;
    private EditField _messageField;
    private EditField _addressField;
    private EditField _status;
    private MMSDemo _app;
    private TableView _view;

    private static final int MAX_PHONE_NUMBER_LENGTH = 30;

    /**
     * Constructs a new MMSDemoSendScreen object
     * 
     * @param app
     *            The MMSDemo application instance
     */
    public MMSDemoSendScreen(final MMSDemo app) {
        super(Manager.NO_VERTICAL_SCROLL);

        _app = app;

        // Initialize UI components
        setTitle("MMS Demo");
        _addressField =
                new EditField("Destination:", "", MAX_PHONE_NUMBER_LENGTH,
                        BasicEditField.FILTER_PHONE);
        add(_addressField);
        add(new SeparatorField());
        _subjectField = new EditField("Subject:", "");
        add(_subjectField);
        _messageField = new EditField("Message:", "");
        add(_messageField);
        add(new SeparatorField());
        final LabelField attachmentText = new LabelField("Attachments");
        add(attachmentText);

        // Create table components
        _view = new TableView(_app.getMessageParts());
        final TableController controller =
                new TableController(_app.getMessageParts(), _view);
        controller.setFocusPolicy(TableController.ROW_FOCUS);
        _view.setController(controller);

        // Set the highlight style for the view
        _view.setDataTemplateFocus(BackgroundFactory
                .createLinearGradientBackground(Color.LIGHTBLUE,
                        Color.LIGHTBLUE, Color.BLUE, Color.BLUE));

        // Create a data template that will format the model data as an array of
        // LabelFields
        final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
            public Field[] getDataFields(final int modelRowIndex) {
                final MessagePart message =
                        (MessagePart) _app.getMessageParts().getRow(
                                modelRowIndex);
                final Field[] fields =
                        { new LabelField(message.getContentLocation(),
                                Field.NON_FOCUSABLE | DrawStyle.ELLIPSIS) };
                return fields;
            }
        };

        // Define the regions of the data template and column/row size
        dataTemplate.createRegion(new XYRect(0, 0, 1, 1));
        dataTemplate.setColumnProperties(0, new TemplateColumnProperties(
                Display.getWidth()));
        dataTemplate.setRowProperties(0, new TemplateRowProperties(24));

        _view.setDataTemplate(dataTemplate);
        dataTemplate.useFixedHeight(true);

        // Add the file to the screen
        add(_view);

        _status = new EditField();
        add(_status);

        // Menu item to attach a picture to the MMS
        final MenuItem attachPicture =
                new MenuItem(new StringProvider("Attach Picture"), 0x230010, 0);
        attachPicture.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                _app.attach(MMSDemo.PICTURE);
            }
        }));

        // Menu item to attach an audio file to the MMS
        final MenuItem attachAudio =
                new MenuItem(new StringProvider("Attach Audio"), 0x230020, 1);
        attachAudio.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                _app.attach(MMSDemo.AUDIO);
            }
        }));

        // Menu item to send the MMS
        final MenuItem sendMenuItem =
                new MenuItem(new StringProvider("Send"), 0x230030, 3);
        sendMenuItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                // Send MMS on non-event thread
                final Thread t = new Thread() {
                    public void run() {
                        _app.sendMMS(_addressField, _subjectField,
                                _messageField);
                    }
                };
                t.start();
            }
        }));

        _removeAttachment =
                new MenuItem(new StringProvider("Remove Attachment"), 0x230040,
                        4);
        _removeAttachment.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                _app.getMessageParts().removeRowAt(
                        _view.getRowNumberWithFocus());
            }
        }));

        // Add menu items to this screen
        addMenuItem(sendMenuItem);
        addMenuItem(attachPicture);
        addMenuItem(attachAudio);
    }

    // Menu items --------------------------------------------------------------

    /**
     * Menu item to remove an attachment from the attachment list
     */
    private MenuItem _removeAttachment;

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Prevent the save dialog from being displayed
        return true;
    }

    /**
     * @see Screen#close()
     */
    public void close() {
        _app.close();
        super.close();
    }

    /**
     * @see MainScreen#makeMenu(Menu, int)
     */
    protected void makeMenu(final Menu menu, final int context) {
        if (_app.getMessageParts().getNumberOfRows() > 0) {
            menu.add(_removeAttachment);
        }
        super.makeMenu(menu, context);
    }
}
