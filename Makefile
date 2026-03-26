# * Copyright © 2026 Devin B. Royal. * All Rights Reserved. | Status: Active Engineering.

# Variables
APP_NAME := sovereign-core
VERSION := 1.0.0
IMAGE_TAG := royal/$(APP_NAME):$(VERSION)
BUILD_DIR := ./build
SRC_DIR := ./src
SIGNING_KEY := ~/.ssh/sovereign_signing_key

.PHONY: all clean build image sign deploy

all: build image sign

# 1. Compile the Java Aura-7 Core
build:
	@echo "[*] Compiling Aura-7 Core Engine..."
	mkdir -p $(BUILD_DIR)
	javac -d $(BUILD_DIR) $(SRC_DIR)/com/royal/crypto/aura7/*.java
	jar cvf $(BUILD_DIR)/$(APP_NAME).jar -C $(BUILD_DIR) .

# 2. Build the Rootless Podman Image
image: build
	@echo "[*] Building Hardened Podman Image: $(IMAGE_TAG)"
	podman build --rm \
		--label "com.royal.project=Sovereign-Link" \
		--label "com.royal.cipher=Aura-7" \
		-t $(IMAGE_TAG) .

# 3. Cryptographic Artifact Signing
sign: image
	@echo "[*] Attesting Image Integrity with Sovereign Key..."
	# Simulating a container signing protocol (e.g., Cosign or internal ZKP attestation)
	sha256sum $(BUILD_DIR)/$(APP_NAME).jar > $(BUILD_DIR)/checksum.txt
	ssh-keygen -Y sign -f $(SIGNING_KEY) -n file $(BUILD_DIR)/checksum.txt

# 4. Cleanup build artifacts
clean:
	@echo "[*] Cleaning build environment..."
	rm -rf $(BUILD_DIR)
	podman rmi $(IMAGE_TAG) || true

# 5. Local Development Deploy
deploy:
	@echo "[*] Executing local deployment script..."
	./deploy_validator.sh
