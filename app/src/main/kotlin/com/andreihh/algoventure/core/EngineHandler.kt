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

package com.andreihh.algoventure.core

import com.andreihh.algostorm.core.drivers.io.File
import com.andreihh.algostorm.core.drivers.serialization.JsonDriver
import com.andreihh.algostorm.core.ecs.EntityRef
import com.andreihh.algostorm.core.ecs.EntityRef.Id
import com.andreihh.algostorm.core.ecs.System
import com.andreihh.algostorm.core.ecs.System.Companion.ENTITY_POOL
import com.andreihh.algostorm.core.engine.Handler
import com.andreihh.algostorm.core.event.EventBus
import com.andreihh.algostorm.systems.EventSystem.Companion.EVENT_BUS
import com.andreihh.algostorm.systems.MapObject
import com.andreihh.algostorm.systems.Update
import com.andreihh.algostorm.systems.audio.MusicSystem
import com.andreihh.algostorm.systems.audio.MusicSystem.PlayMusic
import com.andreihh.algostorm.systems.audio.SoundSystem
import com.andreihh.algostorm.systems.audio.AudioSystem.Companion.AUDIO_DRIVER
import com.andreihh.algostorm.systems.graphics2d.AnimationSystem
import com.andreihh.algostorm.systems.graphics2d.Camera
import com.andreihh.algostorm.systems.graphics2d.CameraSystem
import com.andreihh.algostorm.systems.graphics2d.CameraSystem.Follow
import com.andreihh.algostorm.systems.graphics2d.CameraSystem.UpdateCamera
import com.andreihh.algostorm.systems.graphics2d.GraphicsSystem.Companion.CAMERA
import com.andreihh.algostorm.systems.graphics2d.GraphicsSystem.Companion.GRAPHICS_DRIVER
import com.andreihh.algostorm.systems.graphics2d.GraphicsSystem.Companion.TILE_HEIGHT
import com.andreihh.algostorm.systems.graphics2d.GraphicsSystem.Companion.TILE_SET_COLLECTION
import com.andreihh.algostorm.systems.graphics2d.GraphicsSystem.Companion.TILE_WIDTH
import com.andreihh.algostorm.systems.graphics2d.RenderingSystem
import com.andreihh.algostorm.systems.graphics2d.RenderingSystem.Companion.BACKGROUND
import com.andreihh.algostorm.systems.graphics2d.RenderingSystem.Render
import com.andreihh.algostorm.systems.graphics2d.TileSetCollection
import com.andreihh.algostorm.systems.input.InputSystem.Companion.INPUT_DRIVER
import com.andreihh.algostorm.systems.input.InputSystem.HandleInput
import com.andreihh.algostorm.systems.lifecycle.LifecycleSystem
import com.andreihh.algostorm.systems.physics2d.PathFindingSystem
import com.andreihh.algostorm.systems.physics2d.PhysicsSystem
import com.andreihh.algoventure.core.assets.Sounds
import com.andreihh.algoventure.core.generation.dungeon.DungeonGenerator
import com.andreihh.algoventure.core.systems.ActingSystem
import com.andreihh.algoventure.core.systems.AiSystem
import com.andreihh.algoventure.core.systems.AttackSystem
import com.andreihh.algoventure.core.systems.DamageSystem
import com.andreihh.algoventure.core.systems.DoorSystem
import com.andreihh.algoventure.core.systems.FacingSystem
import com.andreihh.algoventure.core.systems.GameTerminationSystem
import com.andreihh.algoventure.core.systems.GameTerminationSystem.Companion.UI_DRIVER
import com.andreihh.algoventure.core.systems.InputInterpretingSystem
import com.andreihh.algoventure.core.systems.MovementSystem
import com.andreihh.algoventure.core.systems.VisionSystem
import com.andreihh.algoventure.core.systems.VisionSystem.UpdateFieldOfVision
import com.andreihh.algoventure.core.systems.isGameTerminated

class EngineHandler : Handler() {
    companion object {
        const val NEW_GAME: String = "NEW_GAME"
    }

    private val eventBus = EventBus()
    private val camera = Camera()
    private lateinit var mapObject: MapObject
    private val autosaveFile = File("user:///autosave.json")

    private val systems = listOf(
        RenderingSystem(),
        CameraSystem(),
        PhysicsSystem(),
        LifecycleSystem(),
        PathFindingSystem(),
        AnimationSystem(),
        MusicSystem(),
        SoundSystem(),
        FacingSystem(),
        InputInterpretingSystem(),
        MovementSystem(),
        ActingSystem(),
        AiSystem(),
        FacingSystem(),
        DoorSystem(),
        DamageSystem(),
        AttackSystem(),
        VisionSystem(),
        GameTerminationSystem()
    )

    private lateinit var knight: EntityRef

    override val millisPerUpdate: Int get() = 25

    private fun newGame(): MapObject = DungeonGenerator.newMap()

    private fun loadAutosave(): MapObject? =
        fileSystemDriver.openInput(autosaveFile)?.use(JsonDriver::deserialize)

    override fun onInit(args: Map<String, Any?>) {
        mapObject = loadAutosave() ?: newGame()
        for (tileSet in mapObject.tileSets) {
            graphicsDriver.loadBitmap(tileSet.image.source)
        }
        for (sound in mapObject.sounds) {
            audioDriver.loadAudioStream(sound)
        }
        knight = mapObject.entities[Id(1)] ?: error("")
        val context = mapOf(
            ENTITY_POOL to mapObject.entities,
            EVENT_BUS to eventBus,
            TILE_WIDTH to 24,
            TILE_HEIGHT to 24,
            BACKGROUND to mapObject.backgroundColor,
            TILE_SET_COLLECTION to TileSetCollection(mapObject.tileSets),
            CAMERA to camera,
            GRAPHICS_DRIVER to graphicsDriver,
            INPUT_DRIVER to inputDriver,
            AUDIO_DRIVER to audioDriver,
            UI_DRIVER to uiDriver
        )
        systems.forEach { it.initialize(context) }
        eventBus.post(Follow(Id(1)))
        eventBus.post(PlayMusic(music = Sounds.gameSoundtrack, loop = true))
    }

    override fun onStart() {
        systems.forEach(System::start)
    }

    private fun render() {
        eventBus.post(UpdateCamera)
        eventBus.post(UpdateFieldOfVision)
        eventBus.publishPosts()
        if (graphicsDriver.isCanvasReady) {
            graphicsDriver.lockCanvas()
            eventBus.post(Render(camera.x, camera.y))
            eventBus.publishPosts()
            graphicsDriver.unlockAndPostCanvas()
        }
    }

    private fun handleInput() {
        eventBus.post(HandleInput)
        eventBus.publishPosts()
    }

    private fun update() {
        eventBus.post(Update(millisPerUpdate))
        eventBus.publishPosts()
    }

    override fun onUpdate() {
        render()
        handleInput()
        update()
    }

    override fun onStop() {
        systems.forEach(System::stop)
        autosave()
    }

    private fun autosave() {
        if (isGameTerminated(mapObject.entities)) {
            fileSystemDriver.delete(autosaveFile)
        } else {
            fileSystemDriver.openOutput(autosaveFile).use { out ->
                JsonDriver.serialize(out, mapObject)
            }
        }
    }

    override fun onRelease() {
        mapObject.entities.clear()
    }

    override fun onError(cause: Exception) {
        cause.printStackTrace()
    }
}
