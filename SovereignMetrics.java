package com.royal.sovereign.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Summary;
import io.prometheus.client.exporter.HTTPServer;
import java.io.IOException;

/**
 * SovereignMetrics: Telemetry for Aura-7 and Sovereign-Link operations.
 * Engineered for high-fidelity performance monitoring.
 */
public class SovereignMetrics {

    // Metrics Definitions
    private static final Counter zkpAttestationsTotal = Counter.build()
            .name("sovereign_zkp_attestations_total")
            .help("Total ZKP identity attestations processed.")
            .labelNames("result") // "success" or "failure"
            .register();

    private static final Summary encryptionLatency = Summary.build()
            .name("sovereign_aura7_encryption_latency_seconds")
            .help("Latency of Aura-7 stream encryption operations.")
            .register();

    private static final Counter gossipPeersDiscovered = Counter.build()
            .name("sovereign_gossip_peers_discovered_total")
            .help("Cumulative count of peers discovered via Gossip protocol.")
            .register();

    public static void start(int port) throws IOException {
        new HTTPServer(port);
        System.out.println("[*] Sovereign-Link Metrics Exporter active on port " + port);
    }

    public static void recordZKP(boolean success) {
        zkpAttestationsTotal.labels(success ? "success" : "failure").inc();
    }

    public static Summary.Timer startEncryptionTimer() {
        return encryptionLatency.startTimer();
    }

    public static void incrementPeers() {
        gossipPeersDiscovered.inc();
    }
}
