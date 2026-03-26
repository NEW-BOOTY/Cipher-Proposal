#!/bin/bash
# * Copyright © 2026 Devin B. Royal. * All Rights Reserved. | Status: Active Engineering.

export VAULT_ADDR="http://127.0.0.1:8200"
VAULT_INIT_FILE="./vault_init_output.json"
TRANSIT_KEY_NAME="sovereign-identity-v1"

echo "[*] Initiating Sovereign-Link Key Ceremony..."

# 1. Initialize Vault and capture Unseal Keys
vault operator init -key-shares=1 -key-threshold=1 -format=json > $VAULT_INIT_FILE

# 2. Extract the Unseal Key and Root Token
UNSEAL_KEY=$(jq -r '.unseal_keys_b64[0]' $VAULT_INIT_FILE)
ROOT_TOKEN=$(jq -r '.root_token' $VAULT_INIT_FILE)

# 3. Unseal the HSM Simulator
vault operator unseal $UNSEAL_KEY
export VAULT_TOKEN=$ROOT_TOKEN

# 4. Enable the Transit Secret Engine (The HSM logic)
vault secrets enable transit

# 5. Generate the Sovereign Identity Key (RSA-4096 or Ed25519)
# Note: 'exportable=false' ensures the private key never leaves the HSM boundary.
vault write -f transit/keys/$TRANSIT_KEY_NAME \
    type=rsa-4096 \
    exportable=false \
    allow_plaintext_backup=false

echo "[+] Identity Key Generated: $TRANSIT_KEY_NAME"

# 6. Shred the sensitive init file after extracting the Public Key
vault read -field=keys transit/keys/$TRANSIT_KEY_NAME | jq -r '.[-1].public_key' > ./node_public_identity.pem
shred -u $VAULT_INIT_FILE

echo "[!] Key Ceremony Complete. Unseal key must be moved to air-gapped storage."
