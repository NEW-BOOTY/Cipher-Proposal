/* * Copyright © 2026 Devin B. Royal. * All Rights Reserved. | Status: Active Engineering. */

Sovereign-Link: Automated Key Ceremony & HSM Initialization
To establish a Zero-Knowledge environment, the "Key Ceremony" must be executed during the initial provisioning of the Validator Node. This script automates the initialization of the Vault-based HSM simulator, configures the Transit Engine, and generates the unique 4096-bit identity key that will never leave the container's encrypted memory.

1. Orchestration Script (key_ceremony.sh)
This script is designed to run once upon the first boot of the Sovereign-Link pod.

Bash
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
2. Operational Doctrine: The "Shred" Protocol
Volatile Initialization: The vault_init_output.json contains the only copy of the Unseal Key. The script uses shred -u to overwrite the file multiple times before deletion, preventing recovery from the disk.

Non-Exportable Keys: By setting exportable=false in the Transit engine, we ensure that even if an attacker gains ROOT_TOKEN access, the underlying private key cannot be downloaded or moved. It can only be used for "Sign" or "Decrypt" operations within the Vault process.

3. ZKP Integration
The Java Validator Engine now uses the Vault Transit API to sign the ZKP Challenge (c).

Validator sends c to transit/sign/$TRANSIT_KEY_NAME.

Vault signs c using the hidden private key x.

Validator receives the signature and constructs the ZKP response (s).

Next Step: Network Sovereignty
To complete the loop, the Sovereign-Link nodes must discover each other. We can implement a Peer-to-Peer (P2P) Discovery Protocol using a gossip-based mechanism that propagates the node_public_identity.pem across the network.
