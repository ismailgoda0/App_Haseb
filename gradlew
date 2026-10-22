#!/usr/bin/env sh
set -eu
PROJECT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
GRADLE_VERSION="9.3.1"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
GRADLE_HOME="$GRADLE_USER_HOME/homecash-distributions/gradle-$GRADLE_VERSION"
GRADLE_EXE="$GRADLE_HOME/bin/gradle"

if [ ! -x "$GRADLE_EXE" ]; then
  echo "[HomeCash] Gradle $GRADLE_VERSION is not cached; downloading and verifying the official distribution..."
  command -v curl >/dev/null 2>&1 || { echo "curl is required to bootstrap Gradle." >&2; exit 1; }
  command -v unzip >/dev/null 2>&1 || { echo "unzip is required to bootstrap Gradle." >&2; exit 1; }
  CACHE_ROOT="$GRADLE_USER_HOME/homecash-distributions"
  mkdir -p "$CACHE_ROOT"
  ZIP="$CACHE_ROOT/gradle-$GRADLE_VERSION-bin.zip"
  EXPECTED_FILE="$ZIP.sha256"
  URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
  curl --fail --location --silent --show-error "$URL.sha256" -o "$EXPECTED_FILE"
  EXPECTED="$(awk '{print tolower($1)}' "$EXPECTED_FILE")"
  case "$EXPECTED" in
    [0-9a-f][0-9a-f][0-9a-f][0-9a-f]*) : ;;
    *) echo "Invalid checksum downloaded from the official Gradle site." >&2; exit 1 ;;
  esac
  curl --fail --location --retry 2 --silent --show-error "$URL" -o "$ZIP"
  ACTUAL="$(sha256sum "$ZIP" | awk '{print tolower($1)}')"
  [ "$ACTUAL" = "$EXPECTED" ] || { echo "Gradle distribution SHA-256 verification failed." >&2; rm -f "$ZIP"; exit 1; }
  TMP="$CACHE_ROOT/.extract-$GRADLE_VERSION-$$"
  rm -rf "$TMP"
  mkdir -p "$TMP"
  unzip -q "$ZIP" -d "$TMP"
  [ -x "$TMP/gradle-$GRADLE_VERSION/bin/gradle" ] || { echo "Gradle archive has an unexpected structure." >&2; rm -rf "$TMP"; exit 1; }
  rm -rf "$GRADLE_HOME"
  mkdir -p "$(dirname "$GRADLE_HOME")"
  mv "$TMP/gradle-$GRADLE_VERSION" "$GRADLE_HOME"
  rm -rf "$TMP"
fi

cd "$PROJECT_DIR"
exec "$GRADLE_EXE" "$@"
