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

import com.andreihh.algostorm.core.ecs.Component
import com.andreihh.algostorm.core.ecs.EntityGroup
import com.andreihh.algostorm.core.event.Event
import com.andreihh.algostorm.core.event.Subscribe
import com.andreihh.algostorm.systems.EventSystem
import com.andreihh.algostorm.systems.lifecycle.LifecycleSystem.Create
import com.andreihh.algostorm.systems.lifecycle.LifecycleSystem.Delete
import com.andreihh.algostorm.systems.physics2d.position
import com.andreihh.algoventure.core.assets.EntityTemplates
import com.andreihh.algoventure.core.systems.ActingSystem.ActionCompleted
import com.andreihh.algoventure.core.systems.DamageSystem.Death
import com.andreihh.algoventure.core.systems.DamageSystem.Health

class HealthBarSystem : EventSystem() {
    private val entities by context<EntityGroup>(ENTITY_POOL)

    object HealthBar : Component

    object UpdateHealthBars : Event

    @Subscribe
    fun onDeath(event: Death) {
        post(UpdateHealthBars)
    }

    @Subscribe
    fun onActionCompleted(event: ActionCompleted) {
        post(UpdateHealthBars)
    }

    @Subscribe
    fun onUpdateHealthBars(event : UpdateHealthBars) {
        entities
            .filterTo(arrayListOf()) { HealthBar::class in it }
            .forEach { request(Delete(it.id)) }
        val healthBarRequests = arrayListOf<Create>()
        for (entity in entities) {
            val (x, y) = entity.position ?: continue
            val (maxHealth, health) = entity[Health::class] ?: continue
            val percent = 1F * health / maxHealth
            healthBarRequests +=
                Create(EntityTemplates.healthBar(x, y, percent))
        }
        healthBarRequests.forEach { request(it) }
    }
}
