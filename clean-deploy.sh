#!/bin/bash
set -e

APP_NAME="aemcors"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "Cleaning build artifacts from: $SCRIPT_DIR"

# ─────────────────────────────────────────────────────────────────────────────
# Maven clean: removes all target/ directories across every submodule.
# Equivalent to running "mvn clean" but faster (no Maven overhead).
# ─────────────────────────────────────────────────────────────────────────────
echo "[1/3] Removing Maven target/ directories (all submodules)..."
find . -type d -name "target" -prune -exec rm -rf {} +

# ─────────────────────────────────────────────────────────────────────────────
# Node.js runtime and npm dependencies: the frontend build (frontend-maven-plugin)
# ─────────────────────────────────────────────────────────────────────────────
echo "[2/3] Removing node/ and node_modules/ directories..."
find . -type d -name "node" -prune -exec rm -rf {} +
find . -type d -name "node_modules" -prune -exec rm -rf {} +

# ─────────────────────────────────────────────────────────────────────────────
# Webpack-generated clientlibs: compiled JS/CSS bundles output by the Webpack
# ─────────────────────────────────────────────────────────────────────────────
echo "[3/3] Removing webpack-generated clientlibs..."

CLIENTLIBS_DIR="ui.apps/src/main/content/jcr_root/apps/$APP_NAME/clientlibs"

# Individual webpack bundle clientlib directories
rm -rf "$CLIENTLIBS_DIR/clientlib-dependencies"
rm -rf "$CLIENTLIBS_DIR/clientlib-site"

echo "Done."
