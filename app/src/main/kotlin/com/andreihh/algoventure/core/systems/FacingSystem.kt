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

import com.andreihh.algostorm.core.ecs.Component
import com.andreihh.algostorm.core.ecs.EntityGroup
import com.andreihh.algostorm.core.ecs.EntityRef
import com.andreihh.algostorm.core.event.Subscribe
import com.andreihh.algostorm.systems.EventSystem
import com.andreihh.algostorm.systems.graphics2d.TileSet.Companion.flipHorizontally
import com.andreihh.algostorm.systems.graphics2d.TileSet.Companion.clearFlags
import com.andreihh.algostorm.systems.graphics2d.sprite
import com.andreihh.algostorm.systems.physics2d.geometry2d.Direction
import com.andreihh.algoventure.core.systems.ActingSystem.ActionCompleted
import com.andreihh.algoventure.core.systems.FacingSystem.Facing
import com.andreihh.algoventure.core.systems.MovementSystem.Move

class FacingSystem : EventSystem() {
    enum class Facing : Component {
        LEFT, RIGHT
    }

    private val entities by context<EntityGroup>(ENTITY_POOL)

    @Subscribe
    fun onActionCompleted(event: ActionCompleted) {
        val action = event.action as? Move ?: return
        val entity = entities[action.entityId] ?: return
        val facing = entity.facing ?: return
        val newFacing = when (action.direction) {
            Direction.W -> Facing.LEFT
            Direction.E -> Facing.RIGHT
            else -> facing
        }
        entity.facing = newFacing
        val sprite = entity.sprite ?: return
        if (newFacing != facing) {
            val newGid = sprite.gid.flipHorizontally()
            val newSprite = sprite.copy(gid = sprite.gid.flipHorizontally())
            println("Old gid: ${sprite.gid}; New gid: $newGid; New gid without flags: ${newGid.clearFlags()}")
            println("Old sprite: $sprite")
            println("New sprite: $newSprite")
            entity.set(newSprite)
        }
    }
}

private var EntityRef.facing: Facing?
    get() = get(Facing::class)
    set(value) {
        if (value != null) {
            set(value)
        } else {
            remove(Facing::class)
        }
    }
