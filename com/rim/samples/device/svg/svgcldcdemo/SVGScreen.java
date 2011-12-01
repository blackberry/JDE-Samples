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

package com.rim.samples.device.svg.svgcldcdemo;

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

public final class SVGScreen extends MainScreen {
    private static final String SVG_NAMESPACE_URI =
            "http://www.w3.org/2000/svg";
    private static final String XLINK_NAMESPACE_URI =
            "http://www.w3.org/1999/xlink";
    private static final String IMAGE_URL = "img/blackberry.png";

    protected SVGImage _image; // This is our SVGImage which will be built up
    protected ScalableGraphics _scalablegraphics; // This is our scalable
                                                  // renderer.

    private int _displayWidth;
    private int _displayHeight;

    private SVGElement _redElement;
    private SVGElement _greenElement;
    private SVGElement _spiralElement;
    private SVGElement _textElement;
    private SVGElement _imageElement;

    /**
     * Creates a new SVGScreen object
     */
    public SVGScreen() {
        setTitle("SVG CLDC Demo");

        _displayWidth = Display.getWidth();
        _displayHeight = Display.getHeight();

        _image = createSVGImage();
        _scalablegraphics = ScalableGraphics.createInstance();
    }

    /**
     * Builds a user defined SVGImage from scratch. This creates 3 circles build
     * from various svg element type and show the setting of various traits in a
     * variety of supported ways.
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
        svgElement.setTrait("width", _displayWidth + "");
        svgElement.setFloatTrait("height", _displayHeight);

        // Create a circle element.
        _redElement =
                (SVGElement) document.createElementNS(SVG_NAMESPACE_URI,
                        "circle");
        _redElement.setId("_redElement");
        _redElement.setFloatTrait("cx", Util.convertDefaultValue(230, true));
        _redElement.setFloatTrait("cy", Util.convertDefaultValue(140, false));

        final float r = Util.convertDefaultValue(50, true);
        _redElement.setFloatTrait("r", r);

        _redElement.setTrait("fill", "#ff0000");
        _redElement.setTrait("stroke", "black");
        _redElement.setFloatTrait("stroke-width", 2);
        _redElement.setFloatTrait("fill-opacity", 0.75f);

        // Create a ellipse element.
        _greenElement =
                (SVGElement) document.createElementNS(SVG_NAMESPACE_URI,
                        "ellipse");
        _greenElement.setId("_greenElement");
        _greenElement.setFloatTrait("cx", Util.convertDefaultValue(200, true));
        _greenElement.setFloatTrait("cy", Util.convertDefaultValue(180, false));
        _greenElement.setFloatTrait("rx", r);
        _greenElement.setFloatTrait("ry", r);

        final SVGRGBColor green = svgElement.createSVGRGBColor(0, 255, 0);
        _greenElement.setRGBColorTrait("fill", green);

        _greenElement.setTrait("stroke", "rgb(0,0,0)");
        _greenElement.setTrait("stroke-width", "2");
        _greenElement.setFloatTrait("fill-opacity", 0.5f);

        // Create a path element.
        _spiralElement =
                (SVGElement) document
                        .createElementNS(SVG_NAMESPACE_URI, "path");
        _spiralElement.setId("_spiralElement");
        // Lets build a path.
        final SVGPath path = svgElement.createSVGPath();

        final float x = Util.convertDefaultValue(153.0f, true);
        final float y = Util.convertDefaultValue(334.0f, true);
        path.moveTo(x, y);

        float[] defaultValues =
                { 153.0f, 334.0f, 151.0f, 334.0f, 151.0f, 334.0f };
        float[] convertedValues = Util.convertDefaultValue(defaultValues);
        path.curveTo(convertedValues[0], convertedValues[1],
                convertedValues[2], convertedValues[3], convertedValues[4],
                convertedValues[5]);

        defaultValues =
                new float[] { 151.0f, 339.0f, 153.0f, 344.0f, 156.0f, 344.0f };
        convertedValues = Util.convertDefaultValue(defaultValues);
        path.curveTo(convertedValues[0], convertedValues[1],
                convertedValues[2], convertedValues[3], convertedValues[4],
                convertedValues[5]);

        defaultValues =
                new float[] { 164.0f, 344.0f, 171.0f, 339.0f, 171.0f, 334.0f };
        convertedValues = Util.convertDefaultValue(defaultValues);
        path.curveTo(convertedValues[0], convertedValues[1],
                convertedValues[2], convertedValues[3], convertedValues[4],
                convertedValues[5]);

        defaultValues =
                new float[] { 171.0f, 322.0f, 164.0f, 314.0f, 156.0f, 314.0f };
        convertedValues = Util.convertDefaultValue(defaultValues);
        path.curveTo(convertedValues[0], convertedValues[1],
                convertedValues[2], convertedValues[3], convertedValues[4],
                convertedValues[5]);

        defaultValues =
                new float[] { 142.0f, 314.0f, 131.0f, 322.0f, 131.0f, 334.0f };
        convertedValues = Util.convertDefaultValue(defaultValues);
        path.curveTo(convertedValues[0], convertedValues[1],
                convertedValues[2], convertedValues[3], convertedValues[4],
                convertedValues[5]);

        defaultValues =
                new float[] { 131.0f, 350.0f, 142.0f, 364.0f, 156.0f, 364.0f };
        convertedValues = Util.convertDefaultValue(defaultValues);
        path.curveTo(convertedValues[0], convertedValues[1],
                convertedValues[2], convertedValues[3], convertedValues[4],
                convertedValues[5]);

        defaultValues =
                new float[] { 175.0f, 364.0f, 191.0f, 350.0f, 191.0f, 334.0f };
        convertedValues = Util.convertDefaultValue(defaultValues);
        path.curveTo(convertedValues[0], convertedValues[1],
                convertedValues[2], convertedValues[3], convertedValues[4],
                convertedValues[5]);

        defaultValues =
                new float[] { 191.0f, 311.0f, 175.0f, 294.0f, 156.0f, 294.0f };
        convertedValues = Util.convertDefaultValue(defaultValues);
        path.curveTo(convertedValues[0], convertedValues[1],
                convertedValues[2], convertedValues[3], convertedValues[4],
                convertedValues[5]);

        defaultValues =
                new float[] { 131.0f, 294.0f, 111.0f, 311.0f, 111.0f, 334.0f };
        convertedValues = Util.convertDefaultValue(defaultValues);
        path.curveTo(convertedValues[0], convertedValues[1],
                convertedValues[2], convertedValues[3], convertedValues[4],
                convertedValues[5]);

        defaultValues =
                new float[] { 111.0f, 361.0f, 131.0f, 384.0f, 156.0f, 384.0f };
        convertedValues = Util.convertDefaultValue(defaultValues);
        path.curveTo(convertedValues[0], convertedValues[1],
                convertedValues[2], convertedValues[3], convertedValues[4],
                convertedValues[5]);

        defaultValues =
                new float[] { 186.0f, 384.0f, 211.0f, 361.0f, 211.0f, 334.0f };
        convertedValues = Util.convertDefaultValue(defaultValues);
        path.curveTo(convertedValues[0], convertedValues[1],
                convertedValues[2], convertedValues[3], convertedValues[4],
                convertedValues[5]);

        defaultValues =
                new float[] { 211.0f, 300.0f, 186.0f, 274.0f, 156.0f, 274.0f };
        convertedValues = Util.convertDefaultValue(defaultValues);
        path.curveTo(convertedValues[0], convertedValues[1],
                convertedValues[2], convertedValues[3], convertedValues[4],
                convertedValues[5]);

        _spiralElement.setPathTrait("d", path);

        _spiralElement.setTrait("stroke", "blue");
        _spiralElement.setTrait("fill", "none");
        _spiralElement.setFloatTrait("stroke-width", 3);
        _spiralElement.setFloatTrait("fill-opacity", 0.9f);

        // Do some transformations on the path.
        final SVGMatrix transform = _spiralElement.getMatrixTrait("transform");
        if (_displayHeight > _displayWidth) {
            transform.mTranslate(Util.convertDefaultValue(110, true), 0);
        } else {
            transform.mTranslate(0, Util.convertDefaultValue(-110, true));
        }

        _spiralElement.setMatrixTrait("transform", transform);

        // Create an image element.
        _imageElement =
                (SVGElement) document.createElementNS(SVG_NAMESPACE_URI,
                        "image");
        _imageElement.setId("_imageElement");
        _imageElement.setTraitNS(XLINK_NAMESPACE_URI, "href", IMAGE_URL);

        final float f = Util.convertDefaultValue(10, true);
        _imageElement.setFloatTrait("x", f);
        _imageElement.setFloatTrait("y", f);
        _imageElement.setFloatTrait("width", Util
                .convertDefaultValue(180, true));
        _imageElement.setFloatTrait("height", Util.convertDefaultValue(36,
                false));

        // Create a text element.
        _textElement =
                (SVGElement) document
                        .createElementNS(SVG_NAMESPACE_URI, "text");
        _textElement.setId("_textElement");
        _textElement.setTrait("font-family", "BBAlpha Sans");
        _textElement.setTrait("font-size", "18");
        _textElement.setFloatTrait("x", f);
        _textElement.setFloatTrait("y", Util.convertDefaultValue(300, false));
        _textElement.setTrait("#text",
                "JSR226 - Scalable 2D Vector Graphics API for J2ME");

        // Add all our elements in the order you want to render them.
        svgElement.appendChild(_imageElement);
        svgElement.appendChild(_spiralElement);
        svgElement.appendChild(_redElement);
        svgElement.appendChild(_greenElement);
        svgElement.appendChild(_textElement);

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
        _image.setViewportWidth(_displayWidth);
        _image.setViewportHeight(_displayHeight);

        // Render the svg image (model) and x/y=0/0
        _scalablegraphics.render(0, 0, _image);

        // Release bindings on Graphics
        _scalablegraphics.releaseTarget();
    }

    /**
     * @see net.rim.device.api.ui.container.FullScreen#sublayout(int, int)
     */
    protected void sublayout(final int width, final int height) {
        if (_displayWidth != width || _displayHeight != height) {
            _displayWidth = width;
            _displayHeight = height;

            _redElement
                    .setFloatTrait("cx", Util.convertDefaultValue(230, true));
            _redElement.setFloatTrait("cy", Util
                    .convertDefaultValue(140, false));

            _greenElement.setFloatTrait("cx", Util.convertDefaultValue(200,
                    true));
            _greenElement.setFloatTrait("cy", Util.convertDefaultValue(180,
                    false));

            final SVGMatrix transform =
                    _spiralElement.getMatrixTrait("transform");
            if (_displayHeight > _displayWidth) {
                transform.mTranslate(Util.convertDefaultValue(-70, true), Util
                        .convertDefaultValue(40, false));
            } else {
                transform.mTranslate(Util.convertDefaultValue(60, true), Util
                        .convertDefaultValue(-50, false));
            }

            _spiralElement.setMatrixTrait("transform", transform);

            _textElement.setFloatTrait("x", Util.convertDefaultValue(10, true));
            _textElement.setFloatTrait("y", Util
                    .convertDefaultValue(300, false));
        }

        super.sublayout(width, height);
    }
}
