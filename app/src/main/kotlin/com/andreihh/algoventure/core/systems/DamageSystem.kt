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
import com.andreihh.algostorm.core.ecs.EntityRef
import com.andreihh.algostorm.core.ecs.EntityRef.Id
import com.andreihh.algostorm.core.event.Event
import com.andreihh.algostorm.core.event.Subscribe
import com.andreihh.algostorm.systems.EventSystem
import com.andreihh.algostorm.systems.lifecycle.LifecycleSystem.Delete
import com.andreihh.algoventure.core.systems.DamageSystem.Health

class DamageSystem : EventSystem() {
    data class Health(val maxHealth: Int, val health: Int) : Component {
        init {
            require(maxHealth > 0)
            require(health in 1..maxHealth)
        }
    }

    data class Damage(val entityId: Id, val damage: Int) : Event {
        init {
            require(damage >= 0)
        }
    }

    data class Death(val entityId: Id) : Event

    private val entities by context<EntityGroup>(ENTITY_POOL)

    @Subscribe
    fun onDamage(event: Damage) {
        val entity = entities[event.entityId] ?: return
        val (maxHealth, health) = entity.health
        val newHealth = health - event.damage
        if (newHealth <= 0) {
            post(Death(entity.id))
        } else {
            entity.health = Health(maxHealth, newHealth)
        }
    }

    @Subscribe
    fun onDeath(event: Death) {
        request(Delete(event.entityId))
    }
}

val EntityRef.isDamageable: Boolean get() = contains(Health::class)

private var EntityRef.health: Health
    get() = checkNotNull(get(Health::class))
    set(value) {
        set(value)
    }
