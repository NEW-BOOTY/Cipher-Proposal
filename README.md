/* * Copyright © 2026 Devin B. Royal. * All Rights Reserved. | Status: Active Engineering. */

Sovereign-Link: Engineering Repository
This repository contains the core architecture for the Sovereign-Link decentralized PKI and the Aura-7 High-Entropy Stream Cipher. This is a production-ready, security-first ecosystem designed for autonomous engineering and hardware-anchored trust.

1. Project Overview
Sovereign-Link is a multi-layered security stack that eliminates reliance on centralized Certificate Authorities (CAs). It utilizes:

Aura-7 Cipher: A proprietary poly-dimensional stream cipher.

ZKP Attestation: Schnorr-based Zero-Knowledge Proofs for identity.

Gossip P2P: Decentralized node discovery and ledger synchronization.

Vault HSM Sidecar: Hardened key management via Podman orchestration.

2. Technical Stack
Language: Java 21 (Object-Oriented, Production-Ready)

Orchestration: Podman (Rootless/Daemon-less)

Security: HashiCorp Vault (Transit Engine), TPM 2.0, LUKS

Communication: gRPC (mTLS), Unix Domain Sockets (UDS)

Automation: GNU Makefile, Bash, GitHub Actions

3. Repository Structure
Plaintext
.
├── src/com/royal/crypto/       # Core Aura-7 and ZKP Logic
├── api/                        # gRPC Protobuf and Service Definitions
├── scripts/                    # Key Ceremony and TPM Provisioning
├── build/                      # Compiled Artifacts
├── Dockerfile                  # Hardened Podman Image Definition
└── Makefile                    # Global Build and Deploy Orchestrator
INTELLECTUAL PROPERTY & USAGE LICENSE
NOTICE: THIS IS A LEGALLY BINDING INSTRUMENT.

1. Ownership & Copyright
All source code, architectural designs, mathematical derivations (specifically the Aura-7 algorithm), and integrated logic contained within this repository are the exclusive Intellectual Property of Devin Benard Royal.

Copyright © 2026 Devin B. Royal. All Rights Reserved.

2. Grant of License
No license, expressed or implied, is granted to any individual, corporation, or entity to use, copy, modify, merge, publish, distribute, sublicense, or sell this software or its underlying algorithms without the explicit, written, and notarized authorization of Devin Benard Royal.

3. Restrictions
Unauthorized Use: Use of this algorithm or architecture in any commercial, private, or governmental capacity without authority is strictly prohibited.

Reverse Engineering: Any attempt to decompile, reverse engineer, or derive the logic of the Aura-7 cipher is a violation of this license.

Derivative Works: Any software or hardware system that incorporates parts of this architecture is considered a derivative work and remains the property of the original author under this license.

4. Legal Binding & Enforcement
This license is governed by the laws of the jurisdiction of the author's residence. Unauthorized use constitutes a breach of contract and an infringement of copyright and trade secret laws. The author reserves the right to seek injunctive relief and statutory damages to the fullest extent of the law.

Status: Active Engineering | Access: Restricted to Authorized Personnel Only
