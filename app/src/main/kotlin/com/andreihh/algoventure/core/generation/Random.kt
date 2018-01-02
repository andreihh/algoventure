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

package com.andreihh.algoventure.core.generation

import com.andreihh.algostorm.systems.physics2d.geometry2d.Point
import java.util.Random

object Random {
    private val random = Random()

    fun nextInt(lower: Int, upperExclusive: Int): Int =
        lower + random.nextInt(upperExclusive - lower)

    fun nextFloat(): Float = random.nextFloat()

    fun nextBoolean(p: Double = 0.5): Boolean = nextFloat() < p

    fun nextPoint(lower: Int, upperExclusive: Int): Point = Point(
        x = nextInt(lower, upperExclusive),
        y = nextInt(lower, upperExclusive)
    )
}
