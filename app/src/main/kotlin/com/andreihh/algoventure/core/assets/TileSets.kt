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

import com.andreihh.algostorm.systems.graphics2d.TileSet.Companion.tileSet

object TileSets {
    val creatures = tileSet {
        name = "creatures"
        image {
            source = "res:///graphics/creatures.png"
            width = 192
            height = 192
        }
        tileWidth = 24
        tileHeight = 24
        animation(name = "knight:idle") {
            frame(tileId = 0, duration = 500)
            frame(tileId = 1, duration = 500)
        }
        animation(name = "rogue:idle") {
            frame(tileId = 2, duration = 500)
            frame(tileId = 3, duration = 500)
        }
        animation(name = "hunter:idle") {
            frame(tileId = 4, duration = 500)
            frame(tileId = 5, duration = 500)
        }
        animation(name = "wizard:idle") {
            frame(tileId = 6, duration = 500)
            frame(tileId = 7, duration = 500)
        }
        animation(name = "zombie:idle") {
            frame(tileId = 14, duration = 500)
            frame(tileId = 15, duration = 500)
        }
        animation(name = "zombie-headless:idle") {
            frame(tileId = 16, duration = 500)
            frame(tileId = 17, duration = 500)
        }
        animation(name = "skeleton:idle") {
            frame(tileId = 18, duration = 500)
            frame(tileId = 19, duration = 500)
        }
        animation(name = "skeleton-warrior:idle") {
            frame(tileId = 20, duration = 500)
            frame(tileId = 21, duration = 500)
        }
        animation(name = "vampire:idle") {
            frame(tileId = 30, duration = 500)
            frame(tileId = 31, duration = 500)
        }
        animation(name = "bone-golem:idle") {
            frame(tileId = 38, duration = 500)
            frame(tileId = 39, duration = 500)
        }
    }

    val world = tileSet {
        name = "world"
        image {
            source = "res:///graphics/world.png"
            width = 288
            height = 240
        }
        tileWidth = 24
        tileHeight = 24
        animation(name = "wall-torch:idle") {
            frame(tileId = 77, duration = 500)
            frame(tileId = 78, duration = 500)
        }
    }

    val healthBar = tileSet {
        name = "health-bar"
        image {
            source = "res:///graphics/health_bar.png"
            width = 24
            height = 24
        }
        tileWidth = 24
        tileHeight = 24
    }
}
