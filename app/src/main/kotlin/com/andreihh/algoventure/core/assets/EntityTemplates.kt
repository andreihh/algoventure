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
import com.andreihh.algoventure.core.systems.HealthBarSystem.HealthBar
import com.andreihh.algoventure.core.systems.Player
import com.andreihh.algoventure.core.systems.VisionSystem.Opaque
import com.andreihh.algoventure.core.systems.VisionSystem.SightRange
import kotlin.math.roundToInt

object EntityTemplates {
    fun knight(x: Int, y: Int) = entityTemplate {
        +Animation(
            aid = "knight",
            name = "idle",
            elapsedMillis = 0,
            loop = true
        )
        +Sprite(
            width = 24, height = 24,
            z = 1, priority = 1, gid = 1,
            offsetY = -4
        )
        +Facing.LEFT
        +Body.KINEMATIC
        +Position(x, y)
        +Actor(stamina = 0, damage = 25)
        +Player
        +SightRange(range = 8)
        +Health(maxHealth = 300, health = 300)
    }

    fun zombie(x: Int, y: Int, headless: Boolean = false) = entityTemplate {
        +Animation(
            aid = if (headless) "zombie-headless" else "zombie",
            name = "idle",
            elapsedMillis = 0,
            loop = true
        )
        +Sprite(
            width = 24, height = 24,
            z = 1, priority = 1, gid = if (headless) 16 else 14,
            offsetY = -4
        )
        +Facing.LEFT
        +Body.KINEMATIC
        +Position(x, y)
        +Actor(stamina = 0, damage = 15)
        +Health(maxHealth = 50, health = 50)
    }

    fun skeleton(x: Int, y: Int) = entityTemplate {
        +Animation(
            aid = "skeleton",
            name = "idle",
            elapsedMillis = 0,
            loop = true
        )
        +Sprite(
            width = 24, height = 24,
            z = 1, priority = 1, gid = 19,
            offsetY = -4)
        +Facing.LEFT
        +Body.KINEMATIC
        +Position(x, y)
        +Actor(stamina = 0, damage = 15)
        +Health(maxHealth = 50, health = 50)
    }

    fun skeletonWarrior(x: Int, y: Int) = entityTemplate {
        +Animation(
            aid = "skeleton-warrior",
            name = "idle",
            elapsedMillis = 0,
            loop = true
        )
        +Sprite(
            width = 24, height = 24,
            z = 1, priority = 1, gid = 21,
            offsetY = -4
        )
        +Facing.LEFT
        +Body.KINEMATIC
        +Position(x, y)
        +Actor(stamina = 0, damage = 20)
        +Health(maxHealth = 75, health = 75)
    }

    fun vampire(x: Int, y: Int) = entityTemplate {
        +Animation(
            aid = "vampire",
            name = "idle",
            elapsedMillis = 0,
            loop = true
        )
        +Sprite(
            width = 24, height = 24,
            z = 1, priority = 1, gid = 30,
            offsetY = -4
        )
        +Facing.LEFT
        +Body.KINEMATIC
        +Position(x, y)
        +Actor(stamina = 0, damage = 25)
        +Health(maxHealth = 100, health = 100)
    }

    fun boneGolem(x: Int, y: Int) = entityTemplate {
        +Animation(
            aid = "bone-golem",
            name = "idle",
            elapsedMillis = 0,
            loop = true
        )
        +Sprite(
            width = 24, height = 24,
            z = 1, priority = 1, gid = 38,
            offsetY = -4
        )
        +Facing.LEFT
        +Body.KINEMATIC
        +Position(x, y)
        +Actor(stamina = 0, damage = 35)
        +Health(maxHealth = 200, health = 200)
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
            +Opaque
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
        if (!isOpen) {
            +Opaque
        }
        +Door(openGid, closedGid, isOpen)
        +Sprite(width = 24, height = 24, z = 1, priority = 2, gid = gid)
        +Position(x, y)
    }

    fun healthBar(x: Int, y: Int, percent: Float) = entityTemplate {
        +Sprite(
            width = maxOf(1, (percent * 24).roundToInt()), height = 4,
            z = 1, priority = 2,
            gid = 185,
            offsetY = 20
        )
        +Position(x, y)
        +HealthBar
    }
}
