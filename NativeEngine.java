package com.royal.crypto.aura7;

/**
 * NativeEngine: Accelerated Aura-7 Core.
 * Engineered for hardware-level SIMD throughput.
 */
public class NativeEngine {
    static {
        System.loadLibrary("aura7_simd");
    }

    private native void foldSIMD(long[] state);

    public void processNative(long[] state, int rounds) {
        for (int i = 0; i < rounds; i++) {
            foldSIMD(state);
        }
    }
}
