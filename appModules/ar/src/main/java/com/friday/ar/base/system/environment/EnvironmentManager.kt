package com.friday.ar.base.system.environment

import com.google.ar.sceneform.FrameTime

/** This class manages all basic environment related callbacks
 *
 */
open class EnvironmentManager {

    /** Callbacks related to the augmented reality environment
     *
     */
    open class ArCallback

    /** Callback for frame updates: currently calls a function on each frame update
     *
     */
    class SurfaceFrameCallback : ArCallback() {
        //TODO:Create stream instead of using callback method
        fun onArFrameUpdated(frameTime: FrameTime) {

        }
    }
}