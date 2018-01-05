/*
 * Copyright 2018 Andrei Heidelbacher <andrei.heidelbacher@gmail.com>
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

package com.andreihh.algoventure.core.systems

import com.andreihh.algostorm.core.drivers.ui.UiDriver
import com.andreihh.algostorm.core.drivers.ui.UiEvent
import com.andreihh.algostorm.core.ecs.EntityGroup
import com.andreihh.algostorm.core.ecs.EntityRef
import com.andreihh.algostorm.core.event.Event
import com.andreihh.algostorm.core.event.Subscribe
import com.andreihh.algostorm.systems.EventSystem
import com.andreihh.algostorm.systems.audio.MusicSystem.PlayMusic
import com.andreihh.algoventure.core.assets.Sounds
import com.andreihh.algoventure.core.systems.ActingSystem.Actor
import com.andreihh.algoventure.core.systems.DamageSystem.Death

class GameTerminationSystem : EventSystem() {
    companion object {
        const val UI_DRIVER: String = "UI_DRIVER"
    }

    object CheckGameTermination : Event

    object GameLost : UiEvent
    object GameWon : UiEvent

    private val entities by context<EntityGroup>(ENTITY_POOL)
    private val uiDriver by context<UiDriver>(UI_DRIVER)

    private fun isGameLost(): Boolean = entities.getPlayer() == null
    private fun isGameWon(): Boolean = entities.count(EntityRef::isActor) == 1

    private fun removeAllActors() {
        for (entity in entities) {
            entity.remove(Actor::class)
        }
    }

    @Subscribe
    fun onDeath(event: Death) {
        post(CheckGameTermination)
    }

    @Subscribe
    fun onCheckGameTermination(event: CheckGameTermination) {
        when {
            isGameLost() -> {
                post(PlayMusic(music = Sounds.gameLost, loop = false))
                removeAllActors()
                uiDriver.notify(GameLost)
            }
            isGameWon() -> {
                post(PlayMusic(music = Sounds.gameWon, loop = false))
                removeAllActors()
                uiDriver.notify(GameWon)
            }
        }
    }
}

fun isGameTerminated(entities: EntityGroup): Boolean =
    entities.count(EntityRef::isActor) == 0
