package com.friday.ar.base.system.environment

import com.google.ar.core.Plane
import com.google.ar.sceneform.FrameTime

/** This class manages all basic environment related callbacks
 *
 */
open class EnvironmentManager {

    /** Callbacks related to the augmented reality environment
     *
     */
    open class ArCallback {
        /**called whenever the detected planes change
         *
         */
        fun onDetectedPlanesChanged(plane: Collection<Plane>) {

        }
    }

    /** Callback for frame updates: currently calls a function on each frame update
     *
     */
    class SurfaceFrameCallback : ArCallback() {
        //TODO:Create stream instead of using callback method
        fun onArFrameUpdated(frameTime: FrameTime) {

        }
    }
}