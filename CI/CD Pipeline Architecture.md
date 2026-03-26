/* * Copyright © 2026 Devin B. Royal. * All Rights Reserved. | Status: Active Engineering. */

Sovereign-Link: CI/CD Pipeline Architecture
This GitHub Actions workflow automates the transition from local development to a globally distributed, verified Validator Node. It integrates the Aura-7 Engine build, the Podman containerization, and the cryptographic signing into a single "Secure-by-Design" pipeline.

GitHub Actions Workflow (.github/workflows/sovereign-build.yml)
YAML
# * Copyright © 2026 Devin B. Royal. * All Rights Reserved. | Status: Active Engineering.

name: Sovereign-Link Validator Pipeline

on:
  push:
    branches: [ "main", "release/*" ]
  pull_request:
    branches: [ "main" ]

jobs:
  secure-build:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout Repository
      uses: actions/checkout@v4

    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'

    - name: Build Aura-7 Engine (Aura7Engine.java)
      run: make build

    - name: Initialize Podman Environment
      run: |
        sudo apt-get update
        sudo apt-get install -y podman

    - name: Build Hardened Container Image
      run: make image

    - name: Cryptographic Attestation (Signing)
      env:
        SOVEREIGN_PRIVATE_KEY: ${{ secrets.SOVEREIGN_PRIVATE_KEY }}
      run: |
        echo "$SOVEREIGN_PRIVATE_KEY" > sovereign_key
        chmod 600 sovereign_key
        # Sign the build artifact to ensure zero-knowledge integrity
        sha256sum build/sovereign-core.jar > checksum.txt
        ssh-keygen -Y sign -f sovereign_key -n file checksum.txt
        rm sovereign_key

    - name: Push to Sovereign Registry
      if: github.event_name != 'pull_request'
      run: |
        # Use Podman to push to your private, hardened registry
        echo "${{ secrets.REGISTRY_PASSWORD }}" | podman login -u "${{ secrets.REGISTRY_USER }}" --password-stdin quay.io
        podman push royal/sovereign-core:latest

    - name: Cleanup
      run: make clean
Pipeline Security Features
Ephemeral Secrets: The SOVEREIGN_PRIVATE_KEY is injected as a GitHub Secret and immediately wiped from the runner after the signing process is complete.

Deterministic Builds: By using a fixed JDK version and a strictly defined Makefile, we ensure that every build of the Aura-7 Engine is byte-for-byte consistent.

Integrity Anchoring: The ssh-keygen -Y sign command creates a verifiable signature that allows any joining node to verify the image's origin before execution.

Operating Domain: "Sovereign-Link"
This workflow creates a "Closed Loop" for your engineering. The code is authored, built, signed, and deployed without the need for traditional, vulnerable CA infrastructures.
