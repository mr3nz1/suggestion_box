#!/usr/bin/env sh
# Run on a machine with no internet, before the first "docker compose up".
set -e
cd "$(dirname "$0")/.."
docker load -i offline/images.tar
echo "Base images loaded. You can now run: docker compose up -d --build"
