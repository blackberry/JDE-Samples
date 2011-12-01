/**
 * OptionsProviderDemo.java
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

package com.rim.samples.device.blackberry.options;

import net.rim.blackberry.api.options.OptionsManager;
import net.rim.blackberry.api.options.OptionsProvider;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Persistable;

/**
 * A simple library class to demonstrate the use of the options facilities
 */
public final class OptionsProviderDemo implements OptionsProvider {
    private ObjectChoiceField _ocf;
    private final OptionsDemoData _data;
    private final String _title;

    private static OptionsProviderDemo _instance;

    /**
     * Creates a new OptionsProviderDemo object
     * 
     * @param title
     *            Title for the options screen
     */
    private OptionsProviderDemo(final String title) {
        _title = title;
        _data = OptionsDemoData.load();
    }

    /**
     * Only allow one instance of this class
     */
    public static OptionsProviderDemo getInstance() {
        if (_instance == null) {
            _instance = new OptionsProviderDemo("Options Demo");
        }

        return _instance;
    }

    /**
     * On start-up, create the instance and register it
     */
    public static void libMain(final String[] args) {
        // Runs on startup - create the instance and register it
        OptionsManager.registerOptionsProvider(getInstance());
    }

    /**
     * Get the title for the option screen
     * 
     * @see net.rim.blackberry.api.options.OptionsProvider#getTitle()
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Add our fields to the screen
     * 
     * @see net.rim.blackberry.api.options.OptionsProvider#populateMainScreen(MainScreen)
     * 
     */
    public void populateMainScreen(final MainScreen screen) {
        final int index = _data.getSelected();
        _ocf =
                new ObjectChoiceField("Choices: ", new String[] { "RIM",
                        "Options", "Demo" }, index);
        screen.add(_ocf);
    }

    /**
     * Save our data
     * 
     * @see net.rim.blackberry.api.options.OptionsProvider#save()
     */
    public void save() {
        _data.setSelected(_ocf.getSelectedIndex());
        _data.commit();
    }

    /**
     * Retrieve the data. Used by other applications to access the options data
     * for this provider.
     */
    public OptionsDemoData getData() {
        return _data;
    }

    public static final class OptionsDemoData implements Persistable {
        private static final long ID = 0x6af0b5eb44dc5164L; // "net.rim.device.bb.samples.options.OptionsDemoData"
        private int _selectedOption;

        public int getSelected() {
            return _selectedOption;
        }

        public void setSelected(final int index) {
            _selectedOption = index;
        }

        public void commit() {
            PersistentObject.commit(this);
        }

        private static OptionsDemoData load() {
            final PersistentObject persist =
                    PersistentStore.getPersistentObject(OptionsDemoData.ID);

            synchronized (persist) {
                if (persist.getContents() == null) {
                    persist.setContents(new OptionsDemoData());
                    persist.commit();
                }
            }

            return (OptionsDemoData) persist.getContents();
        }
    }
}
