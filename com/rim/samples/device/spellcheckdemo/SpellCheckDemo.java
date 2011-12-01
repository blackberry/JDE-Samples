/*
 * SpellcheckDemo.java
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

import net.rim.blackberry.api.spellcheck.SpellCheckEngine;
import net.rim.blackberry.api.spellcheck.SpellCheckEngineFactory;
import net.rim.blackberry.api.spellcheck.SpellCheckUI;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.EditField;

/**
 * A sample application to showcase the SpellCheck API. The main screen provides
 * a RichTextField for the purpose of typing in words which can then be spell
 * checked by way of a menu item. An additional menu item allows for a specified
 * word to be treated as spelled correctly while another menu item allows for a
 * correction to be added to the list of corrections for the specified word.
 */
class SpellCheckDemo extends UiApplication {
    private final SpellCheckEngine _spellCheckEngine;
    private final SpellCheckUI _spellCheckUI;

    /**
     * Entry point for application.
     * 
     * @param args
     *            Command line arguments.
     */
    public static void main(final String[] args) {
        final SpellCheckDemo app = new SpellCheckDemo();
        app.enterEventDispatcher();
    }

    // Contructor
    private SpellCheckDemo() {
        // Create our spell check objects.
        _spellCheckUI = SpellCheckEngineFactory.createSpellCheckUI();
        _spellCheckUI.addSpellCheckUIListener(new SpellCheckListener());
        _spellCheckEngine = SpellCheckEngineFactory.getEngine();

        // Push a new SpellCheckDemoScreen onto the stack for rendering.
        final SpellCheckDemoScreen screen = new SpellCheckDemoScreen(this);
        pushScreen(screen);
    }

    /**
     * This method calls the learnCorrection() method on the SpellCheckEngine.
     * 
     * @param text
     *            The misspelled word.
     * @param correction
     *            The correction to learn.
     */
    void learnCorrection(final String text, final String correction) {
        _spellCheckEngine.learnCorrection(new StringBuffer(text),
                new StringBuffer(correction));
    }

    /**
     * This method calls the spellCheck() method on the SpellCheckUI.
     * 
     * @param field
     *            The field to be spell checked.
     */
    void spellCheck(final EditField field) {
        _spellCheckUI.spellCheck(field);
    }

    /**
     * This method calls the learnWord() method on the SpellCheckEngine.
     * 
     * @param word
     *            The word to learn.
     */
    void learnWord(final String word) {
        _spellCheckEngine.learnWord(new StringBuffer(word));
    }
}
