/*
 * UpdateManager.java
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

package com.rim.samples.device.dynamicmappabledemo;

import java.util.Random;

import net.rim.device.api.lbs.maps.model.MapDataModel;

/**
 * Class that "updates" all dynamic mappables in it. In a real world scenario
 * this could be some sort of service that knows about location aware data and
 * is constantly updating the mappables it manages based on changes to the data.
 */
public class UpdateManager {
    private final UpdatableMappable _updatableMappable;

    private static final UpdateManager INSTANCE = new UpdateManager();

    /**
     * Creates a new UpdateManager object, private access prevents external
     * instantiation.
     */
    private UpdateManager() {
        // Create a new UpdatableMappable with lat and lon derived from this
        // app's screen map origin
        _updatableMappable =
                new UpdatableMappable(DynamicMappableScreen.ORIGIN_LATITUDE,
                        DynamicMappableScreen.ORIGIN_LONGITUDE, "Lat: "
                                + DynamicMappableScreen.ORIGIN_LATITUDE
                                + "\nLon: "
                                + DynamicMappableScreen.ORIGIN_LONGITUDE,
                        "Dynamic Mappable Demo");
    }

    /**
     * Returns the singleton instance of this class
     * 
     * @return The singleton instance of this class
     */
    public static final UpdateManager getInstance() {
        return INSTANCE;
    }

    /**
     * Convenience method that adds any managed mappables to the provided model
     * 
     * @param model
     *            Model to add data to
     */
    public void addMappablesToModel(final MapDataModel model) {
        model.add(_updatableMappable, "dynamic", true);
    }

    /**
     * For demo purposes we need some way for the "service" to be started. We
     * only want the service started once the Map is up and running. Typically
     * the service would be started by some other process.
     */
    public void startService() {
        new UpdaterThread().start();

    }

    /**
     * Thread class that updates any Mappables with random location changes at 5
     * second intervals.
     */
    class UpdaterThread extends Thread {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            final Random random = new Random();

            while (true) {
                // Create a random value between 0 and 0.03 in steps of 0.0001
                final double dlat = random.nextInt(300) / 10000.0;
                final double dlon = random.nextInt(300) / 10000.0;

                // Set the new value and force an update of any map field
                // or listener that is interested in this update.
                _updatableMappable.setLat(DynamicMappableScreen.ORIGIN_LATITUDE
                        + dlat);
                _updatableMappable
                        .setLon(DynamicMappableScreen.ORIGIN_LONGITUDE + dlon);
                _updatableMappable.setName("Lat: "
                        + _updatableMappable.getLat() + "\nLon: "
                        + _updatableMappable.getLon());

                _updatableMappable.update();

                try {
                    Thread.sleep(5000);
                } catch (final InterruptedException e) {
                }
            }
        }
    }
}
