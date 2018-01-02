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

abstract class Level<T> private constructor(
    val width: Int,
    val height: Int,
    private val data: Array<T>
) {

    @Suppress("unchecked_cast")
    protected constructor(width: Int, height: Int, defaultTile: T) : this(
        width = width,
        height = height,
        data = Array<Any?>(size = width * height) { defaultTile } as Array<T>
    )

    protected val size: Int get() = width * height

    protected fun getIndex(x: Int, y: Int): Int = x * width + y

    fun contains(x: Int, y: Int): Boolean =
        x in 0 until width && y in 0 until height

    operator fun get(x: Int, y: Int): T = data[getIndex(x, y)]

    operator fun set(x: Int, y: Int, value: T) {
        data[getIndex(x, y)] = value
    }
}
