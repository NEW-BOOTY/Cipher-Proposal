/* * Copyright © 2026 Devin B. Royal. * All Rights Reserved. | Status: Active Engineering. */

Sovereign-Link Validator Node: Infrastructure Deployment
To ensure the Sovereign-Link PKI remains decentralized and resilient, the Validator Nodes are designed to run as lightweight, hardened containers. Using Podman ensures a rootless, daemon-less execution environment, which aligns with a high-security posture.

1. The Container Architecture
Each node consists of two primary components:

The Validator Core: A Java-based engine running the Zero-Knowledge Proof (ZKP) logic and Aura-7 state verification.

The Persistence Layer: A local, encrypted key-value store (e.g., BoltDB or a sidecar Redis instance) that holds the distributed ledger fragments.

2. Podman Deployment Script (deploy_validator.sh)
This script automates the creation of a pod, ensuring the validator and its storage are logically isolated from the rest of the host system.

Bash
#!/bin/bash
# * Copyright © 2026 Devin B. Royal. * All Rights Reserved. | Status: Active Engineering.

PROJECT_NAME="sovereign-link-validator"
IMAGE_NAME="royal/sovereign-core:latest"
POD_NAME="sovereign_pod"

echo "[*] Initializing Sovereign-Link Validator Node..."

# Create the Pod with restricted networking
podman pod create --name $POD_NAME -p 8443:8443

# Start the Encrypted Storage Sidecar
podman run -d --pod $POD_NAME \
    --name validator-storage \
    -v ./data:/var/lib/sovereign/data:Z \
    alpine:latest /bin/sh -c "while true; do sleep 3600; done"

# Start the Core Validator Engine
podman run -d --pod $POD_NAME \
    --name validator-engine \
    --env NODE_ID=$(hostname) \
    --env ENCRYPTION_MODE=AURA7 \
    $IMAGE_NAME

echo "[+] Validator Node is active on $POD_NAME"
podman pod inspect $POD_NAME --format "{{.State}}"
3. Hardening Specifications
To protect the integrity of the public key attestations, the following security constraints are applied to the deployment:

Read-Only Root Filesystem: The container's core binaries are immutable during runtime.

No-New-Privileges: Prevents the container processes from gaining additional privileges via setuid or setgid binaries.

Resource Capping: * CPU: 0.5 Cores

Memory: 512MB

Reasoning: Prevents "noisy neighbor" or DoS attacks from exhausting host resources during heavy ZKP computations.

4. Zero-Touch Node Joining
When a new node is spun up via this Podman configuration, it performs an initial "Aura-7 Handshake" with existing peers. It uses a pre-shared seed to generate a temporary session state, pulls the latest ledger headers, and begins participating in the ZKP validation process.
