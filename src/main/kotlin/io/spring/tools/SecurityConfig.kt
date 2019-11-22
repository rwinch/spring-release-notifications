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

import io.spring.tools.security.ContentCachingRequestFilter
import io.spring.tools.security.GitHubServletSignature
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry
import org.springframework.security.web.access.channel.ChannelProcessingFilter

/**
 * @author Rob Winch
 */
@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
                .addFilterBefore(ContentCachingRequestFilter(), ChannelProcessingFilter::class.java)
                .csrf().disable()
                .authorizeRequests { requests ->
                    requests
                            .mvcMatchers("/github/events").access("@github.check(request)")
                }
    }

    companion object {
        @Bean
        fun github(github: GithubProperties): GitHubServletSignature {
            return GitHubServletSignature(github.secret)
        }
    }
}