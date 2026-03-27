/* * Copyright © 2026 Devin B. Royal. * All Rights Reserved. | Status: Active Engineering. */

Sovereign-Link: Hardware Root-of-Trust (TPM 2.0 & LUKS)
To ensure the Sovereign-Link node remains secure even against physical theft, we anchor the disk encryption to the host's Trusted Platform Module (TPM 2.0). This ensures that the disk can only be decrypted if the hardware remains untampered and the boot sequence (PCR registers) is verified.

1. The Hardware Attestation Logic
We use PCR 0, 7, and 11 to seal the encryption key:

PCR 0: Core System Firmware (BIOS/UEFI)

PCR 7: Secure Boot State

PCR 11: Custom Sovereign-Link Boot Attestation

If any of these values change (e.g., someone attempts to boot a different OS or disable Secure Boot), the TPM will refuse to release the key.

2. TPM-LUKS Provisioning Script (secure_boot_provision.sh)
This script automates the binding of the LUKS partition to the hardware TPM.

Bash
#!/bin/bash
# * Copyright © 2026 Devin B. Royal. * All Rights Reserved. | Status: Active Engineering.

TARGET_PARTITION="/dev/nvme0n1p3"
KEY_FILE="/run/shm/tpm_luks_key.bin"

echo "[*] Initializing Hardware Root-of-Trust for Sovereign-Link..."

# 1. Generate a high-entropy transient key
head -c 64 /dev/urandom > $KEY_FILE

# 2. Enroll the key into a new LUKS slot
echo "[*] Enrolling key into LUKS slot..."
cryptsetup luksAddKey $TARGET_PARTITION $KEY_FILE

# 3. Seal the key to the TPM 2.0 using PCR 0+7+11
# This requires clevis or tpm2-tools
echo "[*] Binding partition to TPM 2.0 (PCR 0,7,11)..."
clevis luks bind -d $TARGET_PARTITION tpm2 '{"pcr_ids":"0,7,11"}'

# 4. Wipe the transient key from memory
shred -u $KEY_FILE

echo "[+] Hardware Lock Complete. Partition $TARGET_PARTITION is now Sovereign."
3. Operational Doctrine: The "Anti-Tamper" Trigger
By sealing to PCR 7, we ensure that the Sovereign-Link node is cryptographically tied to Secure Boot. If a "Evil Maid" attack replaces the bootloader, the Aura-7 Engine and the Vault HSM will never start because the underlying filesystem remains an encrypted blob.

Automatic Decryption: At boot, systemd-cryptsetup queries the TPM. If the PCRs match, the volume unlocks automatically.

Manual Recovery: A secondary LUKS slot is reserved for a physical Recovery Key stored in your offline vault.

4. Integration with Devin-OS
This hardware-level lock is the foundation of the Devin-OS architecture. It ensures that the "Turnkey OVA" environment is not just software-hardened, but physically immutable once deployed on target hardware.

Final Architecture Review: The Sovereign Stack
Hardware: TPM 2.0 / LUKS (Disk Integrity)

Orchestration: Podman (Process Isolation)

Security: Vault HSM Sidecar (Key Integrity)

Identity: ZKP / Schnorr (Authentication)

Cryptography: Aura-7 Engine (Data Confidentiality)

Network: P2P Gossip (Decentralized Availability)

Interface: gRPC Gateway (Controlled Access)
