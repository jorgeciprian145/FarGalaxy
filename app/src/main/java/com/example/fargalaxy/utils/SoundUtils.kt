package com.example.fargalaxy.utils

import android.content.Context
import android.media.MediaPlayer
import com.example.fargalaxy.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * Utility function to play the mouseclick sound effect in parallel.
 * This function launches a coroutine to play the sound without blocking the calling thread.
 * 
 * @param context The Android context to create the MediaPlayer
 * @param scope The coroutine scope to launch the sound playback
 */
fun playMouseClickSound(context: Context, scope: CoroutineScope) {
    scope.launch(Dispatchers.Main) {
        val player = MediaPlayer.create(context, R.raw.mouseclick)
        player?.let {
            try {
                it.setVolume(1f, 1f)
                it.start()
                
                // Wait for sound to finish, then clean up
                val duration = it.duration
                if (duration > 0) {
                    delay(duration.toLong())
                }
                
                // Clean up after sound finishes
                try {
                    if (it.isPlaying) {
                        it.stop()
                    }
                    it.release()
                } catch (e: Exception) {
                    // Ignore cleanup errors
                }
            } catch (e: Exception) {
                // If starting fails, try to release
                try {
                    if (it.isPlaying) {
                        it.stop()
                    }
                    it.release()
                } catch (e2: Exception) {
                    // Ignore release errors
                }
            }
        }
    }
}
