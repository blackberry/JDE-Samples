/*
 * ActivityIndicatorFactory.java
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

package com.rim.samples.device.ui.progressindicatordemo;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.progressindicator.ActivityIndicatorModel;
import net.rim.device.api.ui.component.progressindicator.ActivityIndicatorView;

/**
 * A factory class containing static methods to create ActivityIndicatorView
 * objects
 */
public class ActivityIndicatorFactory {
    // Prevent instantiation
    private ActivityIndicatorFactory() {
    }

    /**
     * Creates an ActivityIndicatorView object based on supplied arguments
     * 
     * @param viewStyle
     *            Field style to create ActivityIndicatorView with
     * @param bitmap
     *            Image used to create activity indicator animation
     * @param numFrames
     *            The number of frames in the bitmap image
     * @param animationFieldStyle
     *            Style bit used to create animation field
     * @param label
     *            Label text for the activity indicator
     * @param lableStyle
     *            Style bit for the view's label field
     * @return ActivityIndicatorView object
     */
    public static ActivityIndicatorView createActivityIndicator(
            final long viewStyle, final Bitmap bitmap, final int numFrames,
            final long animationFieldStyle, final String label,
            final long lableStyle) {
        final ActivityIndicatorView view =
                createActivityIndicator(viewStyle, bitmap, numFrames,
                        animationFieldStyle);
        view.createLabel(label, lableStyle);

        return view;
    }

    /**
     * Creates an ActivityIndicatorView object based on supplied arguments
     * 
     * @param viewStyle
     *            Field style to create ActivityIndicatorView with
     * @param bitmap
     *            Image used to create activity indicator animation
     * @param numFrames
     *            The number of frames in the bitmap image
     * @param animationFieldStyle
     *            Style bit used to create animation field
     * @return ActivityIndicatorView object
     */
    public static ActivityIndicatorView createActivityIndicator(
            final long viewStyle, final Bitmap bitmap, final int numFrames,
            final long animationFieldStyle) {
        final ActivityIndicatorView view = new ActivityIndicatorView(viewStyle);
        final ActivityIndicatorModel model = new ActivityIndicatorModel();

        view.setModel(model);

        view.createActivityImageField(bitmap, numFrames, animationFieldStyle);

        return view;
    }

    /**
     * Creates an ActivityIndicatorView object based on supplied arguments
     * 
     * @param delegate
     *            Manager used as a delegate for layout and focus
     * @param viewStyle
     *            Field style to create ActivityIndicatorView with
     * @param bitmap
     *            Image used to create activity indicator animation
     * @param numFrames
     *            The number of frames in the bitmap image
     * @param animationFieldStyle
     *            Style bit used to create animation field
     * @param label
     *            Label text for the activity indicator
     * @param lableStyle
     *            Style bit for the view's label field
     * @return ActivityIndicatorView object
     */
    public static ActivityIndicatorView createActivityIndicator(
            final Manager delegate, final long viewStyle, final Bitmap bitmap,
            final int numFrames, final long animationFieldStyle,
            final String label, final long lableStyle) {
        final ActivityIndicatorView view =
                createActivityIndicator(delegate, viewStyle, bitmap, numFrames,
                        animationFieldStyle);

        view.createLabel(label, lableStyle);

        return view;
    }

    /**
     * Creates an ActivityIndicatorView object based on supplied arguments
     * 
     * @param delegate
     *            Manager used as a delegate for layout and focus
     * @param viewStyle
     *            Field style to create ActivityIndicatorView with
     * @param bitmap
     *            Image used to create activity indicator animation
     * @param numFrames
     *            The number of frames in the bitmap image
     * @param animationFieldStyle
     *            Style bit used to create animation field
     * @return ActivityIndicatorView object
     */
    public static ActivityIndicatorView createActivityIndicator(
            final Manager delegate, final long viewStyle, final Bitmap bitmap,
            final int numFrames, final long animationFieldStyle) {
        final ActivityIndicatorView view =
                new ActivityIndicatorView(viewStyle, delegate);
        final ActivityIndicatorModel model = new ActivityIndicatorModel();

        view.setModel(model);

        view.createActivityImageField(bitmap, numFrames, animationFieldStyle);

        return view;
    }
}
