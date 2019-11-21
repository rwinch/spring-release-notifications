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

import io.spring.tools.MilestoneEvent.ChangeProperty
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

/**
 * See https://api.slack.com/messaging/sending
 *
 * @author Rob Winch
 */
@Component
class SlackService(private val slackProperties: SlackProperties,
                   private val webClient: WebClient = WebClient.create()) {

    fun notify(event: MilestoneEvent) {
        val text = text(event)
        webClient.post()
                .uri("${slackProperties.baseApiUrl}/chat.postMessage")
                .headers({headers -> headers.setBearerAuth(slackProperties.oauthToken) })
                .bodyValue(Message(slackProperties.channelId, text))
                .exchange()
                .block()
    }

    fun text(event: MilestoneEvent): String {
        val summary = "`${event.repository.fullName}` milestone <${event.milestone.htmlUrl}|${event.milestone.title}> was `${event.action}`."
        val changes: String = if (event.changes == null) {
            ""
        } else {
            fun diff(name: String, changeProperty: ChangeProperty, currentValue: String?): String {
                return if (changeProperty.isChanged) {
                    "\n- The property `$name` has been changed from `${changeProperty.value}` to `$currentValue`"
                } else {
                    ""
                }
            }
            "\n" + diff("title", event.changes.title, event.milestone.title) +
                    diff("description", event.changes.description, event.milestone.description) +
                    diff("Due On", event.changes.dueOn, event.milestone.dueOn)
        }
        return summary + changes
    }

    data class Message(val channel: String, val text: String)
}