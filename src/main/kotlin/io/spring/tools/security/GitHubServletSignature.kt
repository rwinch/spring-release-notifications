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

package io.spring.tools.security

import org.springframework.util.StreamUtils
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest

/**
 * @author Rob Winch
 */
class GitHubServletSignature(secret: String) {
    val github: GitHubSignature

    @Throws(IOException::class)
    fun check(request: HttpServletRequest): Boolean {
        val githubSignature = request.getHeader(GITHUB_SIGNATURE_NAME)
        val body = StreamUtils.copyToString(request.inputStream, StandardCharsets.UTF_8)
        return github.check(githubSignature, body)
    }

    companion object {
        private const val GITHUB_SIGNATURE_NAME = "X-Hub-Signature"
    }

    init {
        github = GitHubSignature(secret)
    }
}