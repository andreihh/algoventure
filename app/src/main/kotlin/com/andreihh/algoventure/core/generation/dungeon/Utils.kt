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

import com.andreihh.algostorm.systems.physics2d.geometry2d.Direction
import com.andreihh.algostorm.systems.physics2d.geometry2d.Point
import com.andreihh.algoventure.core.generation.Random.nextFloat
import com.andreihh.algoventure.core.generation.Random.nextInt
import com.andreihh.algoventure.core.generation.dungeon.DungeonLevel.Tile
import java.util.LinkedList

private fun nextOddInt(lower: Int, upper: Int): Int =
    2 * nextInt(lower = lower / 2, upperExclusive =  (upper + 1) / 2) + 1

private fun DungeonLevel.canPlaceRoomAt(
    x: Int,
    y: Int,
    w: Int,
    h: Int
): Boolean {
    for (ry in y until y + h) {
        for (rx in x until x + w) {
            if (get(rx, ry) != Tile.EMPTY) {
                return false
            }
        }
    }
    return true
}

private fun DungeonLevel.placeRoomAt(x: Int, y: Int, w: Int, h: Int, c: Int) {
    for (ry in y until y + h) {
        for (rx in x until x + w) {
            set(rx, ry, Tile.FLOOR)
            setColor(rx, ry, c)
        }
    }
}

fun DungeonLevel.placeRooms(
    minSize: Int,
    maxSize: Int,
    placementAttempts: Int
): Int {
    var usedColors = 0
    repeat(placementAttempts) {
        val w = nextOddInt(minSize, maxSize)
        val h = nextOddInt(minSize, maxSize)
        val x = nextOddInt(lower = 1, upper = width - w - 1)
        val y = nextOddInt(lower = 1, upper = height - h - 1)
        if (canPlaceRoomAt(x, y, w, h)) {
            usedColors += 1
            placeRoomAt(x, y, w, h, usedColors)
        }
    }
    return usedColors
}

private fun DungeonLevel.floodFill(
    x: Int,
    y: Int,
    color: Int,
    straightness: Double,
    previousDirection: Direction? = null
) {
    fun expand(d: Direction) {
        val nx = x + d.dx * 2
        val ny = y + d.dy * 2
        val canExpand = nx in 1 until width - 1 && ny in 1 until height - 1
            && get(nx, ny) == Tile.EMPTY
        if (canExpand) {
            set(x = x + d.dx, y = y + d.dy, value = Tile.FLOOR)
            setColor(x = x + d.dx, y = y + d.dy, color = color)
            floodFill(nx, ny, color, straightness, d)
        }
    }

    set(x, y, Tile.FLOOR)
    setColor(x, y, color)
    if (nextFloat() < straightness && previousDirection != null) {
        expand(previousDirection)
    }
    val directions = Direction.CARDINAL.shuffled()
    directions.forEach(::expand)
}

fun DungeonLevel.placeCorridors(roomColors: Int, straightness: Double) {
    var usedColors = roomColors
    for (x in 1 until width - 1 step 2) {
        for (y in 1 until height - 1 step 2) {
            if (get(x, y) == Tile.EMPTY) {
                usedColors += 1
                floodFill(x, y, usedColors, straightness)
            }
        }
    }
}

private fun DungeonLevel.countAdjacent(
    x: Int,
    y: Int,
    vararg tiles: Tile
): Int = Direction.CARDINAL.count { d ->
    val nx = x + d.dx
    val ny = y + d.dy
    contains(nx, ny) && get(nx, ny) in tiles
}

private fun DungeonLevel.countSurrounding(
    x: Int,
    y: Int,
    vararg tiles: Tile
): Int = Direction.ORDINAL.count { d ->
    val nx = x + d.dx
    val ny = y + d.dy
    contains(nx, ny) && get(nx, ny) in tiles
}

fun DungeonLevel.placeDoors() {
    val father = hashMapOf<Int, Int>()

    fun getFather(color: Int): Int = father[color]?.let(::getFather) ?: color

    val doors = mutableListOf<Point>()
    for (x in 0 until width) {
        for (y in 0 until height) {
            val adjacentFloors = countAdjacent(x, y, Tile.FLOOR)
            if (get(x, y) == Tile.EMPTY && adjacentFloors == 2) {
                doors += Point(x, y)
            }
        }
    }
    doors.shuffle()

    for ((x, y) in doors) {
        var firstColor = 0
        var secondColor = 0
        for (d in Direction.CARDINAL) {
            val nx = x + d.dx
            val ny = y + d.dy
            if (contains(nx, ny) && get(nx, ny) == Tile.FLOOR) {
                if (firstColor == 0) {
                    firstColor = getColor(nx, ny)
                } else {
                    secondColor = getColor(nx, ny)
                }
            }
        }
        firstColor = getFather(firstColor)
        secondColor = getFather(secondColor)
        if (firstColor != secondColor) {
            father[secondColor] = firstColor
            set(x, y, Tile.DOOR)
        }
    }
}

private fun DungeonLevel.isDeadEnd(x: Int, y: Int): Boolean =
    (get(x, y) == Tile.FLOOR || get(x, y) == Tile.DOOR)
        && countAdjacent(x, y, Tile.FLOOR, Tile.DOOR) < 1

fun DungeonLevel.removeDeadEnds() {
    val queue = LinkedList<Point>()
    for (x in 0 until width) {
        for (y in 0 until height) {
            if (isDeadEnd(x, y)) {
                queue += Point(x, y)
            }
        }
    }
    while (queue.isNotEmpty()) {
        val (x, y) = queue.remove()
        set(x, y, Tile.EMPTY)
        for (d in Direction.CARDINAL) {
            val nx = x + d.dx
            val ny = y + d.dy
            if (contains(nx, ny) && isDeadEnd(nx, ny)) {
                queue += Point(nx, ny)
            }
        }
    }
}

fun DungeonLevel.getAdjacencyMask(x: Int, y: Int, tile: Tile): Int {
    var mask = 0
    for ((bit, d) in Direction.CARDINAL.withIndex()) {
        val nx = x + d.dx
        val ny = y + d.dy
        if (contains(nx, ny) && get(nx, ny) == tile) {
            mask = mask or (1 shl bit)
        }
    }
    return mask
}

fun DungeonLevel.placeWalls() {
    for (x in 0 until width) {
        for (y in 0 until height) {
            val surroundingFloorsAndDoors =
                countSurrounding(x, y, Tile.FLOOR, Tile.DOOR)
            if (get(x, y) == Tile.EMPTY && surroundingFloorsAndDoors > 0) {
                set(x, y, Tile.WALL)
            }
        }
    }
}
