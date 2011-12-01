/*
 * InfoScreen.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.bluetoothjsr82demo;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;

import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Screen to display Bluetooth information
 */
public class InfoScreen extends MainScreen {
    /**
     * Creates a new InfoScreen object
     */
    public InfoScreen() {
        setTitle("Info Screen");
        final String info = generateBluetoothString();
        add(new RichTextField(info));
    }

    /**
     * Assembles a text string containing the device's bluetooth information
     * 
     * @return String containing the device's bluetooth information
     */
    private String generateBluetoothString() {
        LocalDevice lc;

        try {
            // Get the LocalDevice
            lc = LocalDevice.getLocalDevice();
        } catch (final Exception ex) {
            return "Failed to initialize Bluetooth";
        }

        final StringBuffer sb = new StringBuffer();

        // Get the device's Bluetooth address
        sb.append("Bluetooth Address: ");
        sb.append(lc.getBluetoothAddress());
        sb.append('\n');

        // Get the device's Bluetooth friendly name
        sb.append("Bluetooth friendly name: ");
        sb.append(lc.getFriendlyName());
        sb.append('\n');

        // Get the device's discovery mode
        sb.append("Discovery Mode: ");

        switch (lc.getDiscoverable()) {
        case DiscoveryAgent.GIAC:
            sb.append("General/Unlimited Inquiry Access");
            break;

        case DiscoveryAgent.LIAC:
            sb.append("Limited Dedicated Inquiry Access");
            break;

        case DiscoveryAgent.NOT_DISCOVERABLE:
            sb.append("Not discoverable");
            break;

        default:
            sb.append("Unknown");
            break;
        }

        sb.append('\n');

        // Get the Bluetooth API version
        sb.append("API Version: ");
        sb.append(LocalDevice.getProperty("bluetooth.api.version"));
        sb.append('\n');

        // Get the Bluetooth master switch setting
        sb.append("Master/Slave Switch Allowed: ");
        sb.append(LocalDevice.getProperty("bluetooth.master.switch"));
        sb.append('\n');

        // Get the maximum number of service attributes per second
        sb.append("Max number of service attributes retrieved per record: ");
        sb.append(LocalDevice.getProperty("bluetooth.sd.attr.retrievable.max"));
        sb.append('\n');

        // Get the maximum number of connected devices
        sb.append("Max number of supported connected devices at one time: ");
        sb.append(LocalDevice.getProperty("bluetooth.connected.devices.max"));
        sb.append('\n');

        // Get the maximum receiveMTU size
        sb.append("Max receiveMTU size in bytes supported in L2CAP: ");
        sb.append(LocalDevice.getProperty("bluetooth.l2cap.receiveMTU.max"));
        sb.append('\n');

        // Get the maximum number of concurrent service discovery transactions
        sb.append("Maximum number of concurrent service discovery transactions: ");
        sb.append(LocalDevice.getProperty("bluetooth.sd.trans.max"));
        sb.append('\n');

        // Inquiry scanning allowed during connection setting
        sb.append("Inquiry scanning allowed during connection: ");
        sb.append(LocalDevice.getProperty("bluetooth.connected.inquiry.scan"));
        sb.append('\n');

        // Page scanning allowed during connection setting
        sb.append("Page scanning allowed during connection: ");
        sb.append(LocalDevice.getProperty("bluetooth.connected.page.scan"));
        sb.append('\n');

        // Inquiry allowed during a connection
        sb.append("Inquiry allowed during a connection: ");
        sb.append(LocalDevice.getProperty("bluetooth.connected.inquiry"));
        sb.append('\n');

        // Get the paging allowed during a connection setting
        sb.append("Paging allowed during a connection: ");
        sb.append(LocalDevice.getProperty("bluetooth.connected.page"));
        sb.append('\n');

        // Return the string with the Bluetooth info
        return sb.toString();
    }
}
