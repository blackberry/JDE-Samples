/*
 * Category.java
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

package com.rim.samples.device.sqlitedemo;

/**
 * Class to represent a directory category
 */
public final class Category {
    int _id;
    String _name;
    int _node;

    /**
     * Constructs a Category object
     * 
     * @param id
     *            The unique identifier associated with the category
     * @param name
     *            The descriptive name of the category
     */
    public Category(final int id, final String name) {
        _id = id;
        _name = name;
    }

    /**
     * Returns the unique identifier associated with the category
     * 
     * @return The unique identifier associated with this Category object
     */
    int getId() {
        return _id;
    }

    /**
     * Returns the descriptive name of the category
     * 
     * @return The descriptive name of this Category object
     */
    String getName() {
        return _name;
    }

    /**
     * Returns the tree field node number that maps to the category
     * 
     * @return The tree field node number that maps to this Category object
     */
    int getNode() {
        return _node;
    }

    /**
     * Sets the tree field node number that maps to the category
     * 
     * @param node
     *            The tree field node number that maps to this Category object
     */
    void setNode(final int node) {
        _node = node;
    }
}
