/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.ike.ikev2.message;

import com.android.ike.ikev2.exceptions.IkeException;
import com.android.ike.ikev2.exceptions.InvalidSyntaxException;

import java.security.SecureRandom;

/**
 * IkeNoncePayload represents a Nonce payload.
 *
 * <p>Length of nonce data must be at least half the key size of negotiated PRF. It must be between
 * 16 and 256 octets. IKE library always generates nonce of GENERATED_NONCE_LEN octets which is long
 * enough for all currently known PRFs.
 *
 * <p>Critical bit must be ignored when doing decoding and must not be set when doing encoding for
 * this payload.
 *
 * @see <a href="https://tools.ietf.org/html/rfc7296">RFC 7296, Internet Key Exchange Protocol
 *     Version 2 (IKEv2).
 */
public final class IkeNoncePayload extends IkePayload {
    // The longest key size of all currently known PRFs is 512 bits (64 bytes). Since we are
    // required to generate nonce that is long enough for all proposed PRFs, it is simple that we
    // always generate 32 bytes nonce, which is enough for all known PRFs.
    private static final int GENERATED_NONCE_LEN = 32;

    private static final int MIN_NONCE_LEN = 16;
    private static final int MAX_NONCE_LEN = 256;

    public final byte[] nonceData;

    /**
     * Construct an instance of IkeNoncePayload in the context of {@link IkePayloadFactory}.
     *
     * @param critical indicates if it is a critical payload.
     * @param payloadBody the nonce data
     */
    IkeNoncePayload(boolean critical, byte[] payloadBody) throws IkeException {
        super(PAYLOAD_TYPE_NONCE, critical);
        if (payloadBody.length < MIN_NONCE_LEN || payloadBody.length > MAX_NONCE_LEN) {
            throw new InvalidSyntaxException(
                    "Invalid nonce data with length of: " + payloadBody.length);
        }
        // Check that the length of payloadBody satisfies the "half the key size of negotiated PRF"
        // condition when processing IKE Message in upper layer. Cannot do this check here for
        // lacking PRF information.
        nonceData = payloadBody;
    }

    /** Generate Nonce data and construct an instance of IkeNoncePayload. */
    public IkeNoncePayload() {
        super(PAYLOAD_TYPE_NONCE, false);
        nonceData = new byte[GENERATED_NONCE_LEN];
        new SecureRandom().nextBytes(nonceData);
    }

    /**
     * Encode Nonce payload to byte array.
     *
     * @param nextPayload type of payload that follows this payload.
     * @return encoded Nonce payload
     */
    @Override
    byte[] encode(@PayloadType int nextPayload) {
        throw new UnsupportedOperationException(
                "It is not supported to encode a " + getTypeString());
        // TODO: Implement encoding Nonce payload.
    }

    /**
     * Return the payload type as a String.
     *
     * @return the payload type as a String.
     */
    @Override
    public String getTypeString() {
        return "Nonce Payload";
    }
}
