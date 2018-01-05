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
import com.andreihh.algostorm.core.event.Request
import com.andreihh.algostorm.core.event.Subscribe
import com.andreihh.algostorm.systems.EventSystem
import com.andreihh.algostorm.systems.Update
import com.andreihh.algoventure.core.systems.ActingSystem.Actor

class ActingSystem : EventSystem() {
    data class Actor(val stamina: Int, val damage: Int) : Component

    interface Action : Event {
        val entityId: Id
    }

    class ActionRequest(val entityId: Id) : Request<Action?>()

    data class ActionCompleted(val action: Action) : Event

    object NewAct : Event

    object NewTurn : Event

    private val entities: EntityGroup by context(ENTITY_POOL)

    @Subscribe
    fun onUpdate(event: Update) {
        post(NewAct)
    }

    @Subscribe
    fun onNewAct(event: NewAct) {
        val actors = entities.filter(EntityRef::isActor)
        val nextActor = actors.maxBy { it.actor.stamina } ?: return
        if (nextActor.actor.stamina <= 0) {
            for (entity in actors) {
                entity.actor = entity.actor.copy(stamina = 1)
            }
            post(NewTurn)
        } else {
            val action = request(ActionRequest(nextActor.id))
            if (action != null) {
                check(action.entityId == nextActor.id)
                post(action)
            }
        }
    }

    @Subscribe
    fun onNewTurn(event: NewTurn) {
        post(NewAct)
    }

    @Subscribe
    fun onActionCompleted(event: ActionCompleted) {
        val entity = entities[event.action.entityId] ?: return
        entity.actor = entity.actor.copy(stamina = entity.actor.stamina - 1)
        post(NewAct)
    }
}

val EntityRef.isActor: Boolean get() = contains(Actor::class)

var EntityRef.actor: Actor
    get() = checkNotNull(get(Actor::class))
    private set(value) {
        set(value)
    }

