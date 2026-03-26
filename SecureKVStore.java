package com.royal.crypto.sovereign.db;

import com.royal.crypto.aura7.Aura7Engine;
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;

/**
 * SecureKVStore: Hardened persistence for the Sovereign-Link Ledger.
 * Uses Aura-7 for transparent data-at-rest encryption.
 */
public class SecureKVStore {

    private final Path dbPath;
    private final byte[] sessionKey; // Derived from HSM

    public SecureKVStore(String path, byte[] derivedKey) {
        this.dbPath = Paths.get(path);
        this.sessionKey = derivedKey;
    }

    /**
     * Encrypts and persists a peer identity or ledger entry.
     */
    public void put(String key, byte[] value) throws IOException {
        byte[] nonce = generateNonce(key);
        Aura7Engine engine = new Aura7Engine(sessionKey, nonce);
        
        byte[] encryptedData = engine.process(value);
        
        // Structure: [Nonce(16 bytes)][EncryptedData(n bytes)]
        byte[] record = new byte[nonce.length + encryptedData.length];
        System.arraycopy(nonce, 0, record, 0, nonce.length);
        System.arraycopy(encryptedData, 0, record, nonce.length, encryptedData.length);
        
        Files.write(dbPath.resolve(key + ".sovereign"), record, StandardOpenOption.CREATE);
    }

    public byte[] get(String key) throws IOException {
        byte[] record = Files.readAllBytes(dbPath.resolve(key + ".sovereign"));
        byte[] nonce = Arrays.copyOfRange(record, 0, 16);
        byte[] encryptedData = Arrays.copyOfRange(record, 16, record.length);
        
        Aura7Engine engine = new Aura7Engine(sessionKey, nonce);
        return engine.process(encryptedData);
    }

    private byte[] generateNonce(String key) {
        // Deterministic nonce based on key to ensure consistency across reboots
        // In production, use HmacSHA256(key, session_salt)
        return Arrays.copyOf(key.getBytes(), 16);
    }
}
