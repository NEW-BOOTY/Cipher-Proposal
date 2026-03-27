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
