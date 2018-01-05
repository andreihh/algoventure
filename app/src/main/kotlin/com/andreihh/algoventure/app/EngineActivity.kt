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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.andreihh.algostorm.android.ClientActivity
import com.andreihh.algostorm.core.drivers.ui.UiEvent
import com.andreihh.algostorm.core.drivers.ui.UiListener
import com.andreihh.algoventure.R
import com.andreihh.algoventure.core.systems.GameTerminationSystem.GameLost
import com.andreihh.algoventure.core.systems.GameTerminationSystem.GameWon
import com.andreihh.algoventure.core.systems.InputInterpretingSystem.InputAction

class EngineActivity : ClientActivity() {
    companion object {
        private const val GAME_LOST_TAG: String = "GAME_OVER"
        private const val GAME_WON_TAG: String = "GAME_WON"

        @JvmStatic
        fun start(context: Context, args: Bundle) {
            val intent = Intent(context, EngineActivity::class.java)
            intent.putExtras(args)
            context.startActivity(intent)
        }
    }

    override val clientLayoutId: Int get() = R.layout.activity_engine
    override val splashLayoutId: Int get() = R.layout.activity_all_splash
    override val surfaceViewId: Int get() = R.id.surfaceView
    override val listener: UiListener get() = object : UiListener {
        override fun notify(event: UiEvent) {
            when (event) {
                GameLost -> onGameLost()
                GameWon -> onGameWon()
            }
        }
    }

    fun onUp(v: View) {
        sendInput(InputAction.MOVE_UP)
    }

    fun onRight(v: View) {
        sendInput(InputAction.MOVE_RIGHT)
    }

    fun onDown(v: View) {
        sendInput(InputAction.MOVE_DOWN)
    }

    fun onLeft(v: View) {
        sendInput(InputAction.MOVE_LEFT)
    }

    private fun onGameLost() {
        val dialog = fragmentManager.findFragmentByTag(GAME_LOST_TAG)
        if (dialog != null) {
            fragmentManager.beginTransaction().remove(dialog).commit()
        }
        GameLostDialogFragment().show(fragmentManager, GAME_LOST_TAG)
    }

    private fun onGameWon() {
        val dialog = fragmentManager.findFragmentByTag(GAME_WON_TAG)
        if (dialog != null) {
            fragmentManager.beginTransaction().remove(dialog).commit()
        }
        GameWonDialogFragment().show(fragmentManager, GAME_WON_TAG)
    }
}
