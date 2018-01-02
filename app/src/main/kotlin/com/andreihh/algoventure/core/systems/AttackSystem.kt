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

import com.andreihh.algostorm.core.ecs.EntityGroup
import com.andreihh.algostorm.core.ecs.EntityRef.Id
import com.andreihh.algostorm.core.event.Subscribe
import com.andreihh.algostorm.systems.EventSystem
import com.andreihh.algostorm.systems.physics2d.Collision
import com.andreihh.algoventure.core.systems.ActingSystem.Action
import com.andreihh.algoventure.core.systems.ActingSystem.ActionCompleted
import com.andreihh.algoventure.core.systems.DamageSystem.Damage

class AttackSystem : EventSystem() {
    data class Attack(val attackerId: Id, val attackedId: Id) : Action {
        override val entityId: Id get() = attackerId
    }

    private val entities by context<EntityGroup>(ENTITY_POOL)

    @Subscribe
    fun onCollision(event: Collision) {
        val isActor = entities[event.selfId]?.isActor == true
        val otherIsDamageable = entities[event.otherId]?.isDamageable == true
        if (isActor && otherIsDamageable) {
            post(Attack(event.selfId, event.otherId))
        }
    }

    @Subscribe
    fun onAttack(event: Attack) {
        val attacker = entities[event.attackerId] ?: return
        post(Damage(event.attackedId, attacker.actor.damage))
        post(ActionCompleted(event))
    }
}
