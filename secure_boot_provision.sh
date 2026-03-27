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
