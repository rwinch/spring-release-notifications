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

import org.springframework.security.crypto.codec.Hex
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * @author Rob Winch
 */
class GitHubSignature(private val key: String) {
    fun check(gitHubSignature: String?, body: String): Boolean {
        if (gitHubSignature == null || !gitHubSignature.startsWith(SIGNATURE_PREFIX)) {
            return false
        }
        val providedHmac = gitHubSignature.substring(SIGNATURE_PREFIX.length)
        val providedHmacBytes = Hex.decode(providedHmac)
        val expectedBytes = sign(body, key)
        return MessageDigest.isEqual(providedHmacBytes, expectedBytes)
    }

    fun create(body: String, token: String): String {
        return SIGNATURE_PREFIX + String(Hex.encode(sign(body, token)))
    }

    private fun sign(body: String, token: String): ByteArray {
        return try {
            val signingKey = SecretKeySpec(token.toByteArray(),
                    HMAC_SHA1_ALGORITHM)
            val mac = Mac.getInstance(HMAC_SHA1_ALGORITHM)
            mac.init(signingKey)
            mac.doFinal(body.toByteArray())
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private const val SIGNATURE_PREFIX = "sha1="
        private const val HMAC_SHA1_ALGORITHM = "HmacSHA1"
    }

}