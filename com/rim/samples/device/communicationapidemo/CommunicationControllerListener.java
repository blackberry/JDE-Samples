/**
 * CommunicationControllerListener.java
 *
 * Research In Motion Limited proprietary and confidential
 * Copyright Research In Motion Limited, 2010-2010
 */

package com.rim.samples.device.communicationapidemo;

public interface CommunicationControllerListener {

    void onWaitTimerCounterChanged(int time);

    void onWaitTimerCompleted();
}
