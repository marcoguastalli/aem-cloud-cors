#!/bin/bash
set -e

# ─────────────────────────────────────────────────────────────────────────────
# AEM Cloud Cors Deployment Script
#
# This script automatically sets up Java 21 and deploys to AEM instances
#
# Usage:
#   ./deploy.sh                         # Deploy to author (localhost:4502)
#   ./deploy.sh publish                 # Deploy to publish (localhost:4503)
#   ./deploy.sh author                  # Deploy to author (explicit)
#   ./deploy.sh author -Daem.port=7502  # Deploy to custom author port
# ─────────────────────────────────────────────────────────────────────────────

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Default target is author
TARGET="${1:-author}"
MAVEN_ARGS=("${@:2}")

# Extract Maven properties from custom args
for arg in "${MAVEN_ARGS[@]}"; do
  case "$arg" in
    -Daem.port=*)
      MAVEN_PORT="${arg#-Daem.port=}"
      ;;
    -Daem.host=*)
      MAVEN_HOST="${arg#-Daem.host=}"
      ;;
  esac
done

# Map target to deployment profile and port
case "$TARGET" in
  author)
    PROFILE="autoInstallSinglePackage"
    PORT="${AEM_PORT:-${MAVEN_PORT:-4502}}"
    HOST="${AEM_HOST:-${MAVEN_HOST:-localhost}}"
    ;;
  publish)
    PROFILE="autoInstallSinglePackagePublish"
    PORT="${AEM_PUBLISH_PORT:-${MAVEN_PORT:-4503}}"
    HOST="${AEM_PUBLISH_HOST:-${MAVEN_HOST:-localhost}}"
    ;;
  *)
    echo "Usage: $0 [author|publish] [additional Maven args...]"
    echo ""
    echo "Examples:"
    echo "  $0 author                           # Deploy to localhost:4502"
    echo "  $0 publish                          # Deploy to localhost:4503"
    echo "  $0 author -Daem.port=4503          # Deploy to custom port"
    echo "  $0 author -Daem.host=example.com   # Deploy to custom host"
    exit 1
    ;;
esac

echo "════════════════════════════════════════════════════════════════════════════════"
echo "  AEM Cloud Cors Deployment Script"
echo "════════════════════════════════════════════════════════════════════════════════"
echo ""
echo "  Target:    $TARGET (${HOST}:${PORT})"
echo "  Profile:   $PROFILE"
echo "  Directory: $SCRIPT_DIR"
echo ""

# Set Java 21
echo "[Setup] Configuring Java 21..."
export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
if [ ! -d "$JAVA_HOME" ]; then
  echo "  ✗ Error: Temurin 21 JDK not found at $JAVA_HOME"
  echo "  Install via: /usr/libexec/java_home -V"
  exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -1)
echo "  $JAVA_VERSION"
echo ""

# Run Maven clean install with auto-deploy
echo "[Build] Starting Maven build and deployment..."
printf "  mvn clean install -P%s" "$PROFILE"
if [ "${#MAVEN_ARGS[@]}" -gt 0 ]; then
  printf " %q" "${MAVEN_ARGS[@]}"
fi
printf "\n"
echo ""

mvn clean install "-P$PROFILE" "${MAVEN_ARGS[@]}"

echo ""
echo "════════════════════════════════════════════════════════════════════════════════"
echo "  ✓ Deployment complete!"
echo "  ✓ Access at: http://${HOST}:${PORT}/aem/start.html"
echo "════════════════════════════════════════════════════════════════════════════════"
