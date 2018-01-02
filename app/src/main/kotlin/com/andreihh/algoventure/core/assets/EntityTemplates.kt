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

package com.andreihh.algoventure.core.assets

import com.andreihh.algostorm.core.ecs.EntityRef.Companion.entityTemplate
import com.andreihh.algostorm.systems.graphics2d.Animation
import com.andreihh.algostorm.systems.graphics2d.Sprite
import com.andreihh.algostorm.systems.physics2d.Body
import com.andreihh.algostorm.systems.physics2d.Position
import com.andreihh.algoventure.core.systems.ActingSystem.Actor
import com.andreihh.algoventure.core.systems.DamageSystem.Health
import com.andreihh.algoventure.core.systems.DoorSystem.Door
import com.andreihh.algoventure.core.systems.FacingSystem.Facing
import com.andreihh.algoventure.core.systems.Player

object EntityTemplates {
    fun knight(x: Int, y: Int) = entityTemplate {
        +Animation(
            aid = "knight",
            name = "idle",
            elapsedMillis = 0,
            loop = true
        )
        +Sprite(width = 24, height = 24, z = 1, priority = 1, gid = 1)
        +Facing.LEFT
        +Body.KINEMATIC
        +Position(x, y)
        +Actor(stamina = 1, damage = 25)
        +Player
    }

    fun skeleton(x: Int, y: Int) = entityTemplate {
        +Animation(
            aid = "skeleton",
            name = "idle",
            elapsedMillis = 0,
            loop = true
        )
        +Sprite(width = 24, height = 24, z = 1, priority = 1, gid = 19)
        +Facing.LEFT
        +Body.KINEMATIC
        +Position(x, y)
        +Actor(stamina = 1, damage = 15)
        +Health(maxHealth = 75, health = 75)
    }

    fun skeletonWarrior(x: Int, y: Int) = entityTemplate {
        +Animation(
            aid = "skeleton-warrior",
            name = "idle",
            elapsedMillis = 0,
            loop = true
        )
        +Sprite(width = 24, height = 24, z = 1, priority = 1, gid = 21)
        +Facing.LEFT
        +Body.KINEMATIC
        +Position(x, y)
        +Actor(stamina = 1, damage = 25)
        +Health(maxHealth = 100, health = 100)
    }

    fun floor(x: Int, y: Int, broken: Boolean = false) = entityTemplate {
        val gid = if (broken) 66 else 65
        +Sprite(width = 24, height = 24, z = 0, priority = 0, gid = gid)
        +Position(x, y)
    }

    fun wall(x: Int, y: Int, adjacencyMask: Int, broken: Boolean = false) =
        entityTemplate {
            val gid = when(adjacencyMask) {
                0b0000 -> 67
                0b0001 -> 73
                0b0010 -> 68
                0b0011 -> 76
                0b0100 -> 71
                0b0101 -> if (broken) 83 else 72
                0b0110 -> 74
                0b0111 -> 81
                0b1000 -> 70
                0b1001 -> 77
                0b1010 -> if (broken) 84 else 69
                0b1011 -> 82
                0b1100 -> 75
                0b1101 -> 80
                0b1110 -> 79
                0b1111 -> 78
                else -> throw AssertionError("Invalid mask '$adjacencyMask'!")
            }
            +Sprite(width = 24, height = 24, z = 1, priority = 1, gid = gid)
            +Body.STATIC
            +Position(x, y)
        }

    fun wallTorch(x: Int, y: Int) = entityTemplate {
        +Animation(
            aid = "wall-torch",
            name = "idle",
            elapsedMillis = 0,
            loop = true
        )
        +Sprite(width = 24, height = 24, z = 1, priority = 2, gid = 142)
        +Position(x, y)
    }

    fun door(x: Int, y: Int, isOpen: Boolean = false) = entityTemplate {
        val openGid = 88
        val closedGid = 87
        val gid = if (isOpen) openGid else closedGid
        +Door(openGid, closedGid, isOpen)
        +Sprite(width = 24, height = 24, z = 1, priority = 2, gid = gid)
        +Position(x, y)
    }
}
