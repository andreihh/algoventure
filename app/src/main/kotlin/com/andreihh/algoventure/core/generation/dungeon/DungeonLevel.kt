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

import com.andreihh.algoventure.core.generation.Level
import com.andreihh.algoventure.core.generation.dungeon.DungeonLevel.Tile

class DungeonLevel(width: Int, height: Int) :
    Level<Tile>(width, height, Tile.EMPTY) {

    private val colors = IntArray(size) { 0 }

    fun getColor(x: Int, y: Int): Int = colors[getIndex(x, y)]

    fun setColor(x: Int, y: Int, color: Int) {
        colors[getIndex(x, y)] = color
    }

    enum class Tile {
        EMPTY, FLOOR, WALL, DOOR, SPAWNING_POINT, ENTRANCE, EXIT
    }
}
