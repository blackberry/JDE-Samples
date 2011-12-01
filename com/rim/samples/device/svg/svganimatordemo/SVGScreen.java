/*
 * SVGScreen.java
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

package com.rim.samples.device.svg.svganimatordemo;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.m2g.SVGAnimator;
import javax.microedition.m2g.SVGImage;
import javax.microedition.m2g.ScalableImage;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGElement;

/**
 * Simple demo showing how to create an interactive svg file which contains
 * animation and acquire its hosted ui field to add it to the screen.
 */
public final class SVGScreen extends MainScreen {
    // String constants.
    private static final String BOUNCING_BALL = "bouncingBall";
    private static final String PLANETS = "planets";
    private static final String RUNNER = "runner";
    private static final String DISPLAY_ATTR = "display";
    private static final String NONE_VALUE = "none";
    private static final String INLINE_VALUE = "inline";

    // The svg files we want to demo.
    private static final String SAMPLE_URL = "/sample.svg";
    private static final String FIELD_PACKAGE = "net.rim.device.api.ui.Field";

    private SVGImage _image;

    private SVGAnimator _animator;

    // Holds the element id of the element that is currently being displayed.
    private String _currentAnimation;

    // The interactive ui field holding the animated svg context.
    private Field _field;

    // Components used to start the selected animation.
    private ObjectChoiceField _options;
    private ButtonField _playButton;

    /**
     * Constructor.
     */
    public SVGScreen() {
        setTitle("SVG Animator Demo");

        // Set up the components that allow the user to choose
        // an animation and then run it.
        final VerticalFieldManager vManager =
                new VerticalFieldManager(Manager.VERTICAL_SCROLL);
        final Manager fManager = new FlowFieldManager(Manager.VERTICAL_SCROLL);
        setChoiceField();
        fManager.add(_options);
        setButtonField();
        fManager.add(_playButton);

        vManager.add(fManager);
        add(vManager);

        // First animation is set to the bouncing ball animation by default.
        _currentAnimation = BOUNCING_BALL;
        try {
            // Load our svg image.
            _image = loadSVGImage(SAMPLE_URL);
            // Create an interactive svg animator which hosts our ui field.
            _animator = SVGAnimator.createAnimator(_image, FIELD_PACKAGE);
            // Get the ui field from the animator.
            _field =
                    (net.rim.device.api.ui.Field) _animator
                            .getTargetComponent();
            // Add the field to the screen.
            vManager.add(_field);
            _animator.play();
            _playButton.setFocus();

        } catch (final IOException e) {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert("SVGScreen#loadSVGImage() threw "
                            + e.toString());
                }
            });
        }
    }

    /**
     * Lays out the ChoiceField with a list of available animations.
     */
    private void setChoiceField() {
        final String[] items = { "Bouncing Ball", "Running Stick", "Planets" };
        _options = new ObjectChoiceField("Samples:", items);

        _options.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(final Field field, final int context) {
                if ((context & ChoiceField.CONTEXT_CHANGE_OPTION) != 0) {
                    String elementId = null;
                    final int index = _options.getSelectedIndex();
                    switch (index) {
                    case 0:
                        elementId = BOUNCING_BALL;
                        break;
                    case 1:
                        elementId = RUNNER;
                        break;
                    case 2:
                        elementId = PLANETS;
                        break;
                    }

                    setDisplayInline(elementId);
                    _playButton.setFocus();
                }
            }
        });
    }

    /**
     * Activates the given element.
     * 
     * @param elementId
     *            the id of the element being activated
     */
    private void activateElement(final String elementId) {
        final Document doc = _image.getDocument();
        final SVGElement element = (SVGElement) doc.getElementById(elementId);
        _image.focusOn(element);
        _image.activate();
    }

    /**
     * Displays the given element.
     * 
     * @param elementId
     *            the id of the element being displayed
     */
    private void setDisplayInline(final String elementId) {
        final Document doc = _image.getDocument();
        SVGElement animation = null;
        if (!_currentAnimation.equals("")) {
            animation = (SVGElement) doc.getElementById(_currentAnimation);
            animation.setTrait(DISPLAY_ATTR, NONE_VALUE);
        }

        animation = (SVGElement) doc.getElementById(elementId);
        _currentAnimation = elementId;
        animation.setTrait(DISPLAY_ATTR, INLINE_VALUE);
    }

    /**
     * Lays out the button that starts the current animation.
     */
    private void setButtonField() {
        _playButton = new ButtonField("Play", ButtonField.CONSUME_CLICK);
        _playButton.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(final Field field, final int context) {
                activateElement(_currentAnimation);
            }
        });
    }

    /**
     * Loads an SVGImage from a given URL.
     * 
     * @param url
     *            The path to the svg image we want to load.
     * @return The loaded svg image.
     */
    private SVGImage loadSVGImage(final String url) throws IOException {
        // Open our input stream of the svg file we want to load.
        final InputStream inputStream = getClass().getResourceAsStream(url);

        // Load our svg image from the input stream.
        return (SVGImage) ScalableImage.createImage(inputStream, null);
    }

    /**
     * Prevent the save dialog from being displayed.
     * 
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        return true;
    }
}
