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

import com.andreihh.algostorm.core.ecs.EntityRef.Id
import com.andreihh.algostorm.core.event.Subscribe
import com.andreihh.algostorm.systems.EventSystem
import com.andreihh.algostorm.systems.physics2d.PhysicsSystem.TransformIntent
import com.andreihh.algostorm.systems.physics2d.Transformed
import com.andreihh.algostorm.systems.physics2d.geometry2d.Direction
import com.andreihh.algoventure.core.systems.ActingSystem.Action
import com.andreihh.algoventure.core.systems.ActingSystem.ActionCompleted

class MovementSystem : EventSystem() {
    data class Move(
        override val entityId: Id,
        val direction: Direction
    ) : Action {

        init {
            require(direction in Direction.CARDINAL)
        }
    }

    data class Wait(override val entityId: Id) : Action

    @Subscribe
    fun onMove(event: Move) {
        val (entityId, direction) = event
        post(TransformIntent(entityId, direction.dx, direction.dy))
    }

    @Subscribe
    fun onWait(event: Wait) {
        post(ActionCompleted(event))
    }

    @Subscribe
    fun onTransformed(event: Transformed) {
        val direction = Direction.from(event.dx, event.dy) ?: error("")
        post(ActionCompleted(Move(event.entityId, direction)))
    }
}
