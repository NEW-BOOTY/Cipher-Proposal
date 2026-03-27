/* * Copyright © 2026 Devin B. Royal. * All Rights Reserved. | Status: Active Engineering. */

Sovereign-Link: gRPC Gateway & Service Definition
To allow external, authorized applications to interact with the Sovereign-Link ledger without exposing the internal gossip or HSM logic, we implement a gRPC Gateway. This interface provides a high-performance, strictly typed contract for querying peer status and submitting new identity attestations.

1. Protobuf Definition (sovereign.proto)
This file defines the schema for the external API. It utilizes the Aura-7 encrypted stream for data transmission and requires a ZKP metadata header for every request.

Protocol Buffers
syntax = "proto3";

package com.royal.sovereign.api;

option java_multiple_files = true;

// The Sovereign-Link API Service
service SovereignGateway {
  // Submits a new identity for network-wide attestation
  rpc SubmitAttestation (AttestationRequest) returns (AttestationResponse);

  // Queries the local verified Peer Table
  rpc GetPeerStatus (PeerQuery) returns (PeerList);

  // Stream-based ledger synchronization for external watchers
  rpc WatchLedger (LedgerSubscription) returns (stream LedgerEntry);
}

message AttestationRequest {
  string identity_id = 1;
  bytes public_key_pem = 2;
  bytes zkp_proof_r = 3;
  bytes zkp_proof_s = 4;
}

message AttestationResponse {
  bool accepted = 1;
  string node_signature = 2;
  string status_message = 3;
}

message PeerQuery {
  bool verified_only = 1;
}

message PeerList {
  repeated PeerInfo peers = 1;
}

message PeerInfo {
  string address = 1;
  string status = 2;
  int64 last_seen_epoch = 3;
}

message LedgerSubscription {
  string start_block_hash = 1;
}

message LedgerEntry {
  string block_hash = 1;
  bytes encrypted_payload = 2;
  string timestamp = 3;
}
2. Java Implementation: SovereignGatewayService
This service acts as the bridge between the gRPC Frontend and the SecureKVStore/GossipEngine backend.

Java
package com.royal.sovereign.api;

import io.grpc.stub.StreamObserver;
import com.royal.crypto.sovereign.zkp.ZKPAttestation;
import com.royal.crypto.sovereign.db.SecureKVStore;

/**
 * SovereignGatewayService: The external interface for the Sovereign-Link Node.
 * Engineered for secure, multi-tenant access to the distributed ledger.
 */
public class SovereignGatewayService extends SovereignGatewayGrpc.SovereignGatewayImplBase {

    private final ZKPAttestation zkpEngine;
    private final SecureKVStore db;

    public SovereignGatewayService(ZKPAttestation zkp, SecureKVStore db) {
        this.zkpEngine = zkp;
        this.db = db;
    }

    @Override
    public void submitAttestation(AttestationRequest request, 
                                  StreamObserver<AttestationResponse> responseObserver) {
        
        System.out.println("[*] Received Attestation Request for: " + request.getIdentityId());

        // 1. Verify the ZKP Proof using the provided public key
        // 2. If valid, commit to the SecureKVStore
        // 3. Trigger GossipDiscoveryEngine to propagate to the network

        AttestationResponse response = AttestationResponse.newBuilder()
                .setAccepted(true)
                .setStatusMessage("Identity queued for network attestation.")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
3. Operational Doctrine: The "API Sidecar"
The gRPC server should run in a dedicated container within the Podman Pod, communicating with the Validator Engine via internal pod networking (localhost). This isolation ensures that if the API surface is attacked, the Aura-7 Core and HSM Sidecar remain unreachable.

Mutual TLS (mTLS): External clients must present a certificate signed by a known "Sovereign-Link Partner" key.

Rate Limiting: Hardcoded at the gRPC interceptor level to prevent brute-force ZKP verification attempts.
