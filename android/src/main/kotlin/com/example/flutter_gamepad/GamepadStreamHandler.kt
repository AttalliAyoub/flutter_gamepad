package com.example.flutter_gamepad

import android.os.Build
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import io.flutter.plugin.common.EventChannel

val InputDevice.isGamepad: Boolean
    get() = sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD

object GamepadStreamHandler : EventChannel.StreamHandler {
    private var eventSink: EventChannel.EventSink? = null
    private var gamepadCache: GamepadCache? = null
    override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink?) {
        this.eventSink = eventSink
        this.gamepadCache = GamepadCache(eventSink!!)
        for (id in InputDevice.getDeviceIds()) {
            val device = InputDevice.getDevice(id)
            if (device.isGamepad) {
                gamepadCache!!.touchGamepad(id)
            }
        }
    }

    override fun onCancel(arguments: Any?) {
        this.eventSink = null
        this.gamepadCache = null
    }

    fun allGamepadInfoDictionaries(): List<HashMap<String, Any?>> =
            InputDevice.getDeviceIds()
                    .map(InputDevice::getDevice)
                    .filter { it.isGamepad }
                    .map { gamepadInfoDictionary(it) }

    // Returns true if the cache handled the event, or false if it should be bubbled up.
    fun processMotionEvent(event: MotionEvent): Boolean {
        return gamepadCache != null && gamepadCache!!.processMotionEvent(event)
    }

    // Returns true if the cache handled the event, or false if it should be bubbled up.
    fun processKeyDownEvent(keyEvent: KeyEvent): Boolean {
        return gamepadCache != null && gamepadCache!!.processKeyDownEvent(keyEvent)
    }

    // Returns true if the cache handled the event, or false if it should be bubbled up.
    fun processKeyUpEvent(keyEvent: KeyEvent): Boolean {
        return gamepadCache != null && gamepadCache!!.processKeyUpEvent(keyEvent)
    }
}
