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

import com.andreihh.algostorm.core.ecs.EntityGroup
import com.andreihh.algostorm.core.ecs.EntityRef
import com.andreihh.algostorm.core.event.Subscribe
import com.andreihh.algostorm.systems.EventSystem
import com.andreihh.algostorm.systems.physics2d.PathFindingSystem.FindPath
import com.andreihh.algostorm.systems.physics2d.geometry2d.Direction
import com.andreihh.algostorm.systems.physics2d.position
import com.andreihh.algoventure.core.generation.Random.nextInt
import com.andreihh.algoventure.core.systems.ActingSystem.Action
import com.andreihh.algoventure.core.systems.ActingSystem.ActionRequest
import com.andreihh.algoventure.core.systems.MovementSystem.Move
import com.andreihh.algoventure.core.systems.MovementSystem.Wait

class AiSystem : EventSystem() {
    private val entities: EntityGroup by context(ENTITY_POOL)

    private fun EntityRef.getRandomAction(): Action =
        when (nextInt(lower = 0, upperExclusive = 5)) {
            0 -> Move(id, Direction.N)
            1 -> Move(id, Direction.E)
            2 -> Move(id, Direction.S)
            3 -> Move(id, Direction.W)
            else -> Wait(id)
        }

    private fun EntityRef.getCloserToPlayer(): Action {
        val (dx, dy) = entities.getPlayer()?.position
            ?: return getRandomAction()
        val path = request(FindPath(id, dx, dy, Direction.CARDINAL))
        path ?: return getRandomAction()
        return if (path.size in 1..9) Move(id, path.first())
        else getRandomAction()
    }

    @Subscribe
    fun onActionRequest(request: ActionRequest) {
        val entity = entities[request.entityId] ?: return
        if (Player::class !in entity) {
            request.complete(entity.getCloserToPlayer())
        }
    }
}
