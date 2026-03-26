package com.royal.crypto.aura7;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Aura-7: High-Entropy Stream Cipher Implementation
 * Engineered for secure, non-linear data transformation.
 */
public class Aura7Engine {

    private static final long[] CONSTANTS = {
        0x5A827999L, 0x6ED9EBA1L, 0x8F1BBCDCL, 0xCA62C1D6L
    };

    private long[] state = new long[8]; // 512-bit state

    public Aura7Engine(byte[] key, byte[] nonce) {
        if (key.length != 32 || nonce.length != 16) {
            throw new IllegalArgumentException("Key must be 256-bit and Nonce 128-bit.");
        }
        initializeState(key, nonce);
    }

    private void initializeState(byte[] key, byte[] nonce) {
        ByteBuffer keyBuf = ByteBuffer.wrap(key);
        ByteBuffer nonceBuf = ByteBuffer.wrap(nonce);

        for (int i = 0; i < 4; i++) {
            state[i] = keyBuf.getLong() ^ CONSTANTS[i];
        }
        state[4] = nonceBuf.getLong();
        state[5] = nonceBuf.getLong();
        state[6] = state[0] ^ state[4];
        state[7] = state[1] ^ state[5];
        
        // Initial mixing rounds
        for (int i = 0; i < 16; i++) {
            fold();
        }
    }

    private void fold() {
        for (int i = 0; i < 8; i++) {
            state[i] = Long.rotateLeft(state[i], 17) ^ (state[(i + 1) % 8] + CONSTANTS[i % 4]);
        }
    }

    public byte[] process(byte[] data) {
        byte[] output = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            fold();
            long keyByte = state[i % 8] & 0xFF;
            output[i] = (byte) (data[i] ^ keyByte);
        }
        return output;
    }

    public void clear() {
        Arrays.fill(state, 0L);
    }
}
