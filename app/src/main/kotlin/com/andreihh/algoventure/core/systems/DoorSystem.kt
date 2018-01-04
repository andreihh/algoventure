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
import com.andreihh.algostorm.systems.graphics2d.sprite
import com.andreihh.algostorm.systems.physics2d.position
import com.andreihh.algostorm.systems.physics2d.transformed
import com.andreihh.algoventure.core.systems.ActingSystem.ActionCompleted
import com.andreihh.algoventure.core.systems.DoorSystem.Door
import com.andreihh.algoventure.core.systems.MovementSystem.Move
import com.andreihh.algoventure.core.systems.VisionSystem.Opaque

class DoorSystem : EventSystem() {
    data class Door(
        val openGid: Int,
        val closedGid: Int,
        val isOpen: Boolean
    ) : Component

    private val entities by context<EntityGroup>(ENTITY_POOL)
    private val doors get() = entities.filter(EntityRef::isDoor)

    @Subscribe
    fun onActionCompleted(event: ActionCompleted) {
        val action = event.action as? Move ?: return
        val entity = entities[action.entityId] ?: return
        val position = entity.position ?: return
        val previousPosition =
            position.transformed(-action.direction.dx, -action.direction.dy)
        for (doorEntity in doors) {
            when (doorEntity.position) {
                previousPosition -> doorEntity.closeDoor()
                position -> doorEntity.openDoor()
            }
        }
    }
}

private val EntityRef.isDoor: Boolean
    get() = contains(Door::class)

private var EntityRef.door: Door
    get() = checkNotNull(get(Door::class))
    set(value) {
        set(value)
    }

private fun EntityRef.openDoor() {
    door = door.copy(isOpen = true)
    set(sprite.copy(gid = door.openGid))
    remove(Opaque::class)
}

private fun EntityRef.closeDoor() {
    door = door.copy(isOpen = false)
    set(sprite.copy(gid = door.closedGid))
    set(Opaque)
}
