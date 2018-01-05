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

package com.andreihh.algoventure.core.generation.dungeon

import com.andreihh.algostorm.core.drivers.graphics2d.Color
import com.andreihh.algostorm.core.ecs.EntityRef.Id
import com.andreihh.algostorm.systems.MapObject
import com.andreihh.algostorm.systems.MapObject.Builder.Companion.mapObject
import com.andreihh.algoventure.core.assets.EntityTemplates.boneGolem
import com.andreihh.algoventure.core.assets.EntityTemplates.door
import com.andreihh.algoventure.core.assets.EntityTemplates.floor
import com.andreihh.algoventure.core.assets.EntityTemplates.knight
import com.andreihh.algoventure.core.assets.EntityTemplates.skeleton
import com.andreihh.algoventure.core.assets.EntityTemplates.skeletonWarrior
import com.andreihh.algoventure.core.assets.EntityTemplates.vampire
import com.andreihh.algoventure.core.assets.EntityTemplates.wall
import com.andreihh.algoventure.core.assets.EntityTemplates.wallTorch
import com.andreihh.algoventure.core.assets.EntityTemplates.zombie
import com.andreihh.algoventure.core.assets.Sounds
import com.andreihh.algoventure.core.assets.TileSets
import com.andreihh.algoventure.core.generation.MapGenerator
import com.andreihh.algoventure.core.generation.Random.nextBoolean
import com.andreihh.algoventure.core.generation.Random.nextInt
import com.andreihh.algoventure.core.generation.dungeon.DungeonLevel.Tile

class DungeonGenerator(
    private val mapWidth: Int,
    private val mapHeight: Int,
    private val minRoomSize: Int,
    private val maxRoomSize: Int,
    private val roomPlacementAttempts: Int,
    private val corridorStraightness: Double,
    private val spawningPoints: Int
) : MapGenerator {

    private fun generateLevel() = DungeonLevel(mapWidth, mapHeight).apply {
        val colors = placeRooms(minRoomSize, maxRoomSize, roomPlacementAttempts)
        placeCorridors(colors, corridorStraightness)
        placeDoors()
        removeDeadEnds()
        placeWalls()
        placeSpawningPoints(spawningPoints)
    }

    private fun randomMonster(x: Int, y: Int) =
        when (nextInt(lower = 0, upperExclusive = 100)) {
            in 0..10 -> zombie(x = x, y = y, headless = false)
            in 11..20 -> zombie(x = x, y = y, headless = true)
            in 21..40 -> skeletonWarrior(x, y)
            in 41..60 -> vampire(x, y)
            in 61..80 -> boneGolem(x, y)
            else -> skeleton(x, y)
        }

    override fun generateMap(): MapObject = mapObject {
        width = mapWidth
        height = mapHeight
        tileWidth = 24
        tileHeight = 24
        backgroundColor = Color("#FF000000")
        tileSet(TileSets.creatures)
        tileSet(TileSets.world)
        sound(Sounds.gameSoundtrack)
        sound(Sounds.gameLost)
        sound(Sounds.gameWon)

        val level = generateLevel()
        for (x in 0 until mapWidth) {
            for (y in 0 until mapHeight) {
                val wallAdjacencyMask = level.getAdjacencyMask(x, y, Tile.WALL)
                when (level[x, y]) {
                    Tile.FLOOR -> entity(floor(x, y, nextBoolean(0.2)))
                    Tile.WALL -> {
                        entity(wall(x, y, wallAdjacencyMask, nextBoolean(0.3)))
                        val canPlaceTorch = wallAdjacencyMask and 0b0101 == 0
                        if (canPlaceTorch && nextBoolean(0.2)) {
                            entity(wallTorch(x, y))
                        }
                    }
                    Tile.DOOR -> {
                        entity(floor(x, y))
                        entity(door(x, y))
                    }
                    Tile.SPAWNING_POINT -> {
                        entity(floor(x, y, nextBoolean(0.2)))
                        entity(randomMonster(x, y))
                    }
                    Tile.ENTRANCE -> {}
                    Tile.EXIT -> {}
                    Tile.EMPTY -> {}
                }
            }
        }
        var x: Int
        var y: Int
        do {
            x = nextInt(lower = 0, upperExclusive = mapWidth - 1)
            y = nextInt(lower = 0, upperExclusive = mapHeight - 1)
        } while (level[x, y] != Tile.FLOOR)
        entity(Id(1), knight(x, y))
    }

    companion object {
        fun newMap(): MapObject = DungeonGenerator(
            mapWidth = 32,
            mapHeight = 32,
            minRoomSize = 4,
            maxRoomSize = 8,
            roomPlacementAttempts = 32 * 32 / 8,
            corridorStraightness = 0.8,
            spawningPoints = 5
        ).generateMap()
    }
}
