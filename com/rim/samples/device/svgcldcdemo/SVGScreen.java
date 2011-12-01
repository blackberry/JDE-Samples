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

package com.rim.samples.device.svgcldcdemo;

import javax.microedition.m2g.SVGImage;
import javax.microedition.m2g.ScalableGraphics;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.MainScreen;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPath;
import org.w3c.dom.svg.SVGRGBColor;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * This is a custom users screen which builds a svg rendering model from scratch
 * via DOM and uses the ScalableGraphics renderer to render it as a custom drawn
 * background image.
 * 
 * Note: This sample is optimized to run on Simulators with a width of at least
 * 480 pixels, and a height of at least 320 pixels (e.g. 9000).
 */
final class SVGScreen extends MainScreen {
    private static final int DISPLAY_WIDTH = Display.getWidth();
    private static final int DISPLAY_HEIGHT = Display.getHeight();

    private static final String SVG_NAMESPACE_URI =
            "http://www.w3.org/2000/svg";
    private static final String XLINK_NAMESPACE_URI =
            "http://www.w3.org/1999/xlink";
    private static final String IMAGE_URL = "/img/blackberry.png";

    protected SVGImage _image; // This is our SVGImage which will be built up
    protected ScalableGraphics _scalablegraphics; // This is our scalable
                                                  // renderer.

    /**
     * Constructor.
     */
    SVGScreen() {
        setTitle("SVG CLDC Demo");

        _image = createSVGImage();
        _scalablegraphics = ScalableGraphics.createInstance();
    }

    /**
     * Builds a user defined SVGImage from scratch. This creates 3 circles build
     * from various svg element type and show the setting of various traits in a
     * variety of supported ways.
     * <p>
     * 
     * @return The user created SVGImage
     */
    private SVGImage createSVGImage() {
        // Create an empty image to be built.
        final SVGImage image = SVGImage.createEmptyImage(null);
        final Document document = image.getDocument();

        // Get our root svg element.
        final SVGSVGElement svgElement =
                (SVGSVGElement) document.getDocumentElement();
        svgElement.setTrait("width", DISPLAY_WIDTH + "");
        svgElement.setFloatTrait("height", DISPLAY_HEIGHT);

        // Create a circle element.
        final SVGElement redElement =
                (SVGElement) document.createElementNS(SVG_NAMESPACE_URI,
                        "circle");
        redElement.setId("redElement");
        redElement.setFloatTrait("cx", 230);
        redElement.setFloatTrait("cy", 140);
        redElement.setFloatTrait("r", 50);
        redElement.setTrait("fill", "#ff0000");
        redElement.setTrait("stroke", "black");
        redElement.setFloatTrait("stroke-width", 2);
        redElement.setFloatTrait("fill-opacity", 0.75f);

        // Create a ellipse element.
        final SVGElement greenElement =
                (SVGElement) document.createElementNS(SVG_NAMESPACE_URI,
                        "ellipse");
        greenElement.setId("greenElement");
        greenElement.setFloatTrait("cx", 200);
        greenElement.setFloatTrait("cy", 180);
        greenElement.setFloatTrait("rx", 50);
        greenElement.setFloatTrait("ry", 50);

        final SVGRGBColor green = svgElement.createSVGRGBColor(0, 255, 0);
        greenElement.setRGBColorTrait("fill", green);

        greenElement.setTrait("stroke", "rgb(0,0,0)");
        greenElement.setTrait("stroke-width", "2");
        greenElement.setFloatTrait("fill-opacity", 0.5f);

        // Create a path element.
        final SVGElement spiralElement =
                (SVGElement) document
                        .createElementNS(SVG_NAMESPACE_URI, "path");
        spiralElement.setId("spiralElement");
        // Lets build a path.
        final SVGPath path = svgElement.createSVGPath();
        path.moveTo(153.0f, 334.0f);
        path.curveTo(153.0f, 334.0f, 151.0f, 334.0f, 151.0f, 334.0f);
        path.curveTo(151.0f, 339.0f, 153.0f, 344.0f, 156.0f, 344.0f);
        path.curveTo(164.0f, 344.0f, 171.0f, 339.0f, 171.0f, 334.0f);
        path.curveTo(171.0f, 322.0f, 164.0f, 314.0f, 156.0f, 314.0f);
        path.curveTo(142.0f, 314.0f, 131.0f, 322.0f, 131.0f, 334.0f);
        path.curveTo(131.0f, 350.0f, 142.0f, 364.0f, 156.0f, 364.0f);
        path.curveTo(175.0f, 364.0f, 191.0f, 350.0f, 191.0f, 334.0f);
        path.curveTo(191.0f, 311.0f, 175.0f, 294.0f, 156.0f, 294.0f);
        path.curveTo(131.0f, 294.0f, 111.0f, 311.0f, 111.0f, 334.0f);
        path.curveTo(111.0f, 361.0f, 131.0f, 384.0f, 156.0f, 384.0f);
        path.curveTo(186.0f, 384.0f, 211.0f, 361.0f, 211.0f, 334.0f);
        path.curveTo(211.0f, 300.0f, 186.0f, 274.0f, 156.0f, 274.0f);
        spiralElement.setPathTrait("d", path);

        spiralElement.setTrait("stroke", "blue");
        spiralElement.setTrait("fill", "none");
        spiralElement.setFloatTrait("stroke-width", 3);
        spiralElement.setFloatTrait("fill-opacity", 0.9f);
        // Do some transformations on the path.
        final SVGMatrix transform = spiralElement.getMatrixTrait("transform");
        transform.mTranslate(110, -155);
        spiralElement.setMatrixTrait("transform", transform);

        // Create an image element.
        final SVGElement imageElement =
                (SVGElement) document.createElementNS(SVG_NAMESPACE_URI,
                        "image");
        imageElement.setId("imageElement");
        imageElement.setTraitNS(XLINK_NAMESPACE_URI, "href", IMAGE_URL);
        imageElement.setFloatTrait("x", 10);
        imageElement.setFloatTrait("y", 10);
        imageElement.setFloatTrait("width", 180);
        imageElement.setFloatTrait("height", 36);

        // Create a text element.
        final SVGElement textElement =
                (SVGElement) document
                        .createElementNS(SVG_NAMESPACE_URI, "text");
        textElement.setId("textElement");
        textElement.setTrait("font-family", "BBAlpha Sans");
        textElement.setTrait("font-size", "18");
        textElement.setFloatTrait("x", 10);
        textElement.setFloatTrait("y", 300);
        textElement.setTrait("#text",
                "JSR226 - Scalable 2D Vector Graphics API for J2ME");

        // Add all our elements in the order you want to render them.
        svgElement.appendChild(imageElement);
        svgElement.appendChild(spiralElement);
        svgElement.appendChild(redElement);
        svgElement.appendChild(greenElement);
        svgElement.appendChild(textElement);

        return image;
    }

    /**
     * @see Field#paint(Graphics)
     */
    protected void paint(final Graphics graphics) {
        if (_image == null) {
            return;
        }

        // Bind target Graphics target to render to.
        _scalablegraphics.bindTarget(graphics);

        // Set our viewport dimensions.
        _image.setViewportWidth(DISPLAY_WIDTH);
        _image.setViewportHeight(DISPLAY_HEIGHT);

        // Render the svg image (model) and x/y=0/0
        _scalablegraphics.render(0, 0, _image);

        // Release bindings on Graphics
        _scalablegraphics.releaseTarget();
    }
}
