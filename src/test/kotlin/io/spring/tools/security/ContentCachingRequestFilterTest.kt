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

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mock.web.DelegatingServletInputStream
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * @author Rob Winch
 */
internal class ContentCachingRequestFilterTest {
    val filter = ContentCachingRequestFilter()

    @Test
    fun inputStreamWhenTwiceThenCached() {
        val request = mock<HttpServletRequest> {
            on { inputStream } doReturn DelegatingServletInputStream("".byteInputStream())
        }

        filter.doFilter(request, MockHttpServletResponse()) { request, _ ->
            request.inputStream
            request.inputStream
        }

        verify(request).inputStream
        verify(request, never()).reader
    }

    @Test
    fun inputStreamWhenReadThenContainsOriginalContent() {
        val body = "body"
        val request = mock<HttpServletRequest> {
            on { inputStream } doReturn DelegatingServletInputStream(body.byteInputStream())
        }
        val chain = MockFilterChain()

        filter.doFilter(request, MockHttpServletResponse(), chain)

        assertThat(chain.request?.inputStream).hasContent(body);
    }

    @Test
    fun readerWhenTwiceThenCached() {
        val request = mock<HttpServletRequest> {
            // the implementation uses inputStream for obtaining the reader
            on { inputStream } doReturn DelegatingServletInputStream("".byteInputStream())
        }

        filter.doFilter(request, MockHttpServletResponse()) { request, _ ->
            request.reader
            request.reader
        }

        verify(request).inputStream
        verify(request, never()).reader
    }

    @Test
    fun readerWhenReadThenContainsOriginalContent() {
        val body = "body"
        val request = mock<HttpServletRequest> {
            // the implementation uses inputStream for obtaining the reader
            on { inputStream } doReturn DelegatingServletInputStream(body.byteInputStream())
        }
        val chain = MockFilterChain()

        filter.doFilter(request, MockHttpServletResponse(), chain)

        assertThat(chain.request?.reader?.readText()).isEqualTo(body)
    }
}