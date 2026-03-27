# Sovereign-Link: High-Entropy Decentralized PKI
**Architect:** Devin B. Royal  
**Core Cipher:** Aura-7 (Poly-Dimensional Stream)  
**Security Model:** Zero-Knowledge Proof (ZKP) + HSM Sidecar  

### Quick Start (MacBook Air Terminal)
1. Ensure Podman is installed via Homebrew.
2. Run `make all` to compile the Aura-7 Engine and build the hardened image.
3. Execute `./scripts/key_ceremony.sh` to initialize the TPM-backed Vault.
4. Run `./scripts/deploy_validator.sh` to launch the Sovereign-Link Pod.

### Security Doctrine
All data-at-rest is encrypted via Aura-7. Private keys are non-exportable and reside within the Vault HSM sidecar. Network communication is verified via Schnorr ZKP.
