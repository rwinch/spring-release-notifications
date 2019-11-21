/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.tools

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer


/**
 * @author Rob Winch
 */
data class MilestoneEvent(val action: Action,
                          val milestone: Milestone,
                          val repository: Repository,
                          val changes: Changes? = null) {

    enum class Action {
        CREATED,
        CLOSED,
        OPENED,
        EDITED,
        DELETED
    }

    data class Milestone(val title: String,
                         val htmlUrl: String,
                         val description: String? = null,
                         val dueOn: String? = null)

    data class Repository(val fullName: String)

    @JsonDeserialize(using = ChangesDeserializer::class)
    data class Changes(val title: ChangeProperty,
                       var description: ChangeProperty,
                       var dueOn: ChangeProperty)

    data class ChangeProperty(val value: String?, val isChanged: Boolean) {
        constructor(value: String?): this(value, true)

        companion object {
            val UNCHANGED = ChangeProperty(null, false)
        }
    }

    class ChangesDeserializer(type: Class<Any>?): StdDeserializer<Changes>(type) {
        constructor(): this(null)

        override fun deserialize(jp: JsonParser?, ctxt: DeserializationContext?): Changes {
            val changesNode: JsonNode = jp!!.codec.readTree(jp)
            fun from(propertyName: String): ChangeProperty {
                val p = changesNode.get(propertyName)
                return ChangeProperty(p?.get("from")?.textValue(), p != null)
            }
            val description = from("description")
            val dueOn = from("due_on")
            val title = from("title")
            return Changes(title, description, dueOn)
        }

    }
}