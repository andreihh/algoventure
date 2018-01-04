/*
 * Copyright 2017 Andrei Heidelbacher <andrei.heidelbacher@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andreihh.algoventure.app

import android.app.Activity
import android.graphics.drawable.Animatable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import com.andreihh.algoventure.R
import com.andreihh.algoventure.core.EngineHandler
import kotlinx.android.synthetic.main.activity_main.endTorch
import kotlinx.android.synthetic.main.activity_main.startTorch

class MainActivity : Activity() {
    private fun createMediaPlayer(
        assetPath: String,
        loop: Boolean = false
    ): MediaPlayer {
        val afd = assets.openFd(assetPath)
        val player = MediaPlayer()
        player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        afd.close()
        player.isLooping = loop
        player.prepare()
        return player
    }

    private lateinit var uiInteraction: MediaPlayer
    private lateinit var mainTheme: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        uiInteraction = createMediaPlayer("audio/ui_click.mp3")
        mainTheme =
            createMediaPlayer(assetPath = "audio/main_theme.mp3", loop = true)
    }

    override fun onResume() {
        super.onResume()
        mainTheme.start()
    }

    override fun onPause() {
        super.onPause()
        mainTheme.pause()
        uiInteraction.stop()
    }

    override fun onDestroy() {
        mainTheme.release()
        uiInteraction.release()
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            (startTorch.drawable as Animatable).start()
            (endTorch.drawable as Animatable).start()
        }
    }

    private fun playUiInteractionSound() {
        if (uiInteraction.isPlaying) {
            uiInteraction.stop()
        }
        uiInteraction.start()
    }

    fun onPlay(v: View) {
        playUiInteractionSound()
        val args = Bundle().apply {
            putBoolean(EngineHandler.NEW_GAME, true)
        }
        EngineActivity.start(context = this, args = args)
    }

    fun onRankings(v: View) {
        playUiInteractionSound()
    }

    fun onBadges(v: View) {
        playUiInteractionSound()
    }

    fun onExit(v: View) {
        playUiInteractionSound()
        finishAffinity()
    }
}
