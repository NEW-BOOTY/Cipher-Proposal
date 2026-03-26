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
