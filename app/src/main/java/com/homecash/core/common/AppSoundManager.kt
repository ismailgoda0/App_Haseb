package com.homecash.core.common

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.view.View

/**
 * AppSoundManager - Native tactile audio & haptic feedback
 * Provides responsive click sounds, cash register chimes, and haptics without external heavy assets.
 */
object AppSoundManager {

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    /**
     * Standard UI click sound and subtle haptic feedback
     */
    fun playClick(view: View? = null) {
        view?.let {
            it.playSoundEffect(SoundEffectConstants.CLICK)
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        } ?: run {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
            } catch (_: Exception) {}
        }
    }

    fun click(view: View? = null) = playClick(view)

    /**
     * Joyful double-tone chime when cash is received or transaction is confirmed
     */
    fun playCashChime(view: View? = null) {
        view?.performHapticFeedback(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                HapticFeedbackConstants.CONFIRM
            } else {
                HapticFeedbackConstants.CONTEXT_CLICK
            }
        )
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
        } catch (_: Exception) {}
    }

    fun cashRegister(view: View? = null) = playCashChime(view)

    /**
     * Warning tone for discrepancy / flag alert
     */
    fun playAlertTone() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 180)
        } catch (_: Exception) {}
    }
}
