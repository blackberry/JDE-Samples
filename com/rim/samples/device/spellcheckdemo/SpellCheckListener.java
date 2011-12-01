/*
 * SpellCheckListener.java
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

package com.rim.samples.device.spellcheckdemo;

import net.rim.blackberry.api.spellcheck.AbstractSpellCheckUIListener;
import net.rim.blackberry.api.spellcheck.SpellCheckUI;
import net.rim.blackberry.api.spellcheck.SpellCheckUIListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * Implementation of AbstractSpellCheckUIListener. A listener for events on the
 * SpellCheckUI. The listener can be used for as little as determining when each
 * field is spell checked and as much as determining corrections to misspelled
 * words.
 */
class SpellCheckListener extends AbstractSpellCheckUIListener {
    /**
     * @see net.rim.blackberry.api.spellcheck.AbstractSpellCheckUIListener#wordLearned(SpellCheckUI
     *      ui, StringBuffer word)
     */
    public int wordLearned(final SpellCheckUI ui, final StringBuffer word) {
        UiApplication.getUiApplication().invokeLater(
                new popUpRunner("Word learned"));
        return SpellCheckUIListener.LEARNING_ACCEPT;
    }

    /**
     * @see net.rim.blackberry.api.spellcheck.AbstractSpellCheckUIListener#wordIgnored(SpellCheckUI
     *      ui, StringBuffer word, boolean ignoreAll)
     */
    public int wordIgnored(final SpellCheckUI ui, final StringBuffer word,
            final boolean ignoreAll) {
        UiApplication.getUiApplication().invokeLater(
                new popUpRunner("Word ignored"));
        return 0;
    }

    /**
     * @see net.rim.blackberry.api.spellcheck.AbstractSpellCheckUIListener#wordCorrectionLearned(SpellCheckUI
     *      ui, StringBuffer word, StringBuffer correction)
     */
    public int wordCorrectionLearned(final SpellCheckUI ui,
            final StringBuffer word, final StringBuffer correction) {
        UiApplication.getUiApplication().invokeLater(
                new popUpRunner("Correction learned"));
        return SpellCheckUIListener.LEARNING_ACCEPT;
    }

    /**
     * @see net.rim.blackberry.api.spellcheck.AbstractSpellCheckUIListener#spellCheckStarted(SpellCheckUI
     *      ui, Field field)
     */
    public boolean spellCheckStarted(final SpellCheckUI ui, final Field field) {
        final VerticalFieldManager vfm =
                new VerticalFieldManager(Field.FIELD_HCENTER);
        final StatusScreen popUp = new StatusScreen(vfm);
        final RichTextField rtf =
                new RichTextField("Spell check started",
                        RichTextField.USE_TEXT_WIDTH | Field.NON_FOCUSABLE);
        popUp.add(rtf);
        popUp.show(1500);
        return true;
    }

    /**
     * @see net.rim.blackberry.api.spellcheck.AbstractSpellCheckUIListener#misspelledWordFound(SpellCheckUI
     *      ui, Field field, int offset, int len)
     */
    public int misspelledWordFound(final SpellCheckUI ui, final Field field,
            final int offset, final int len) {
        UiApplication.getUiApplication().invokeLater(
                new popUpRunner("Misspelled word found"));
        return SpellCheckUIListener.ACTION_OPEN_UI;
    }

    /**
     * A runnable class which displays an instance of StatusScreen for a
     * specified interval.
     */
    private static final class popUpRunner implements Runnable {
        StatusScreen _popUp;
        RichTextField _rtf;

        private popUpRunner(final String msg) {
            final VerticalFieldManager vfm = new VerticalFieldManager();
            _popUp = new StatusScreen(vfm);
            _rtf =
                    new RichTextField(msg, RichTextField.USE_TEXT_WIDTH
                            | Field.NON_FOCUSABLE);
        }

        public void run() {
            _popUp.add(_rtf);
            _popUp.show(1500); // Display for 1.5 seconds.
        }
    }

    /**
     * Extends PopupScreen class providing a method for pushing the screen and
     * dismissing after a specified interval.
     */
    private static final class StatusScreen extends PopupScreen {
        private final StatusScreenPopper _popupPopper;

        private StatusScreen(final Manager manager) {
            // super(manager,Field.FIELD_HCENTER);
            super(manager);
            _popupPopper = new StatusScreenPopper(this);
        }

        public void show(final int time) {
            final UiApplication app = UiApplication.getUiApplication();
            app.invokeLater(_popupPopper, time, false);
            app.pushModalScreen(this);
        }
    }

    /**
     * A runnable class that can be invoked after a specified interval.
     */
    private static final class StatusScreenPopper implements Runnable {
        private final PopupScreen _popup;

        private StatusScreenPopper(final PopupScreen popup) {
            _popup = popup;
        }

        public void run() {
            Ui.getUiEngine().popScreen(_popup);
        }
    }
}
