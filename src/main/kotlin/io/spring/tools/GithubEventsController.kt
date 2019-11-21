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

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * @author Rob Winch
 */
@RestController
class GithubEventsController(val slack: SlackService) {
    @PostMapping(path = arrayOf("/github/events"), headers = arrayOf("X-GitHub-Event=ping"))
    fun ping(): String {
        return "ping OK"
    }

    @PostMapping(path = arrayOf("/github/events"), consumes = arrayOf("application/json"))
    fun events(@RequestBody milestoneEvent: MilestoneEvent): String {
        slack.notify(milestoneEvent)
        return "ok"
    }
}