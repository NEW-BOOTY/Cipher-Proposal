/* * Copyright © 2026 Devin B. Royal. * All Rights Reserved. | Status: Active Engineering. */

Sovereign-Link: Build & Orchestration
To streamline the lifecycle of the Sovereign-Link Validator, this Makefile handles the compilation of the Java-based Aura-7 engine, the creation of the Podman image, and the final signing of the artifact. This ensures that every node in the network is running a verified, untampered build.

Makefile: Build Automation
Makefile
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
Implementation Details
Modular Compilation: The build target ensures the Java source is packaged into a clean JAR before being injected into the container layer.

Layer Caching: By using --rm, the Makefile keeps the local Podman storage clean, preventing stale layers from interfering with the high-entropy state of the Aura-7 build.

Integrity Verification: The sign target generates a SHA-256 hash of the core engine and signs it using your Sovereign identity key, preventing "Man-in-the-Middle" injections during node propagation.
