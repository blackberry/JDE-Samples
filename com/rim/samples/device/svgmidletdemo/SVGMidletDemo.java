/*
 * SVGMidletDemo.java
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

package com.rim.samples.device.svgmidletdemo;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.m2g.SVGImage;
import javax.microedition.m2g.ScalableGraphics;
import javax.microedition.m2g.ScalableImage;
import javax.microedition.midlet.MIDlet;

/**
 * Simple demo midlet which uses JSR226 to load a SVGImage and self render it
 * using ScalableGraphics.
 * 
 * Since this sample renders a static image on the screen (i.e. we are not
 * updating any of the attribute values), the attribute values of some SVG
 * elements in sample.svg were hard-coded in a way to display the image
 * correctly on a 9500 device. However, one can programmatically adjust those
 * values by calling setFloatTrait() on the SVGElement.
 * 
 */
public class SVGMidletDemo extends MIDlet {

    // The svg file
    private static final String SVG_URL = "/sample.svg";

    /**
     * @see javax.microedition.midlet.MIDlet#startApp()
     */
    protected void startApp() {
        try {
            final SVGImage image = loadSVGImage(SVG_URL);
            final Canvas canvas = new MySVGCanvas(image);

            // Get the display and set our canvas as the currently display one.
            final Display display = Display.getDisplay(this);
            display.setCurrent(canvas);
        } catch (final IOException ex) {
            System.exit(1);
        }
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
     * @see javax.microedition.midlet.MIDlet#pauseApp()
     */
    protected void pauseApp() {
        // Not implemented.
    }

    /**
     * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
     */
    protected void destroyApp(final boolean unconditional) {
        // Not implemented.
    }

}

/**
 * Custom canvas used to render the svg image.
 */
class MySVGCanvas extends Canvas {
    private int _canvasWidth;
    private int _canvasHeight;

    private final SVGImage _image;
    private final ScalableGraphics _sg;

    /**
     * Constructor.
     * 
     * @param image
     *            The svg image we want to render in the canvas.
     */
    MySVGCanvas(final SVGImage image) throws IOException {

        _canvasWidth = this.getWidth();
        _canvasHeight = this.getHeight();

        _image = image;
        _sg = ScalableGraphics.createInstance();
    }

    /**
     * Paints the contents of the canvas using the ScalableGraphics rendering
     * class and the SVGimage we want to render.
     * 
     * @param g
     *            The graphics rendering context.
     */
    public void paint(final Graphics g) {
        if (_image == null) {
            return;
        }

        // Bind target Graphics target to render to.
        _sg.bindTarget(g);

        // Set our viewport dimensions.
        _image.setViewportWidth(_canvasWidth);
        _image.setViewportHeight(_canvasHeight);

        // Render the svg image (model) and x/y=0/0
        _sg.render(0, 0, _image);

        // Release bindings on Graphics
        _sg.releaseTarget();
    }

    /**
     * Invoked when the sample runs and when the screen is tilted.
     * 
     * @see javax.microedition.lcdui.Canvas#sizeChanged(int, int)
     */
    protected void sizeChanged(final int w, final int h) {
        if (_canvasWidth != w || _canvasHeight != h) {
            _canvasWidth = w;
            _canvasHeight = h;
        }

        super.sizeChanged(w, h);
    }

}
