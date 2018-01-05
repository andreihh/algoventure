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

import com.andreihh.algostorm.core.drivers.input.Input
import com.andreihh.algostorm.core.ecs.EntityGroup
import com.andreihh.algostorm.core.event.Subscribe
import com.andreihh.algostorm.systems.input.InputSystem
import com.andreihh.algostorm.systems.physics2d.geometry2d.Direction
import com.andreihh.algoventure.core.systems.ActingSystem.ActionRequest
import com.andreihh.algoventure.core.systems.InputInterpretingSystem.InputAction.MOVE_DOWN
import com.andreihh.algoventure.core.systems.InputInterpretingSystem.InputAction.MOVE_LEFT
import com.andreihh.algoventure.core.systems.InputInterpretingSystem.InputAction.MOVE_RIGHT
import com.andreihh.algoventure.core.systems.InputInterpretingSystem.InputAction.MOVE_UP
import com.andreihh.algoventure.core.systems.InputInterpretingSystem.InputAction.WAIT
import com.andreihh.algoventure.core.systems.MovementSystem.Move
import com.andreihh.algoventure.core.systems.MovementSystem.Wait

class InputInterpretingSystem : InputSystem() {
    enum class InputAction : Input {
        WAIT, MOVE_UP, MOVE_RIGHT, MOVE_DOWN, MOVE_LEFT
    }

    private val entities by context<EntityGroup>(ENTITY_POOL)
    private var nextPlayerInput: Input? = null

    override fun onScroll(dx: Int, dy: Int): Input? = null
    override fun onTouch(x: Int, y: Int): Input? = null
    override fun onInput(input: Input) {
        nextPlayerInput = input
    }

    @Subscribe
    fun onActionRequest(request: ActionRequest) {
        val playerId = entities.getPlayer()?.id
        if (request.entityId == playerId) {
            val action = when (nextPlayerInput) {
                WAIT -> Wait(playerId)
                MOVE_UP -> Move(playerId, Direction.N)
                MOVE_RIGHT -> Move(playerId, Direction.E)
                MOVE_DOWN -> Move(playerId, Direction.S)
                MOVE_LEFT -> Move(playerId, Direction.W)
                else -> null
            }
            request.complete(action)
            nextPlayerInput = null
        }
    }
}
