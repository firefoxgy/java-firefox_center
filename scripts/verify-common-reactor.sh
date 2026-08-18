#!/bin/sh
set -eu

project_root=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd -P)
cloud_root="$project_root/firefox-cloud"

explicit_base_versions=$(rg -n -U \
  '<artifactId>firefox-base</artifactId>[[:space:]]*<version>[^<]+</version>' \
  --glob 'pom.xml' "$cloud_root" | grep -v "^$cloud_root/pom.xml:" || true)

if [ -n "$explicit_base_versions" ]; then
  printf '%s\n' "$explicit_base_versions" >&2
  echo "firefox-base consumers must inherit version 1.0.0 from root dependencyManagement." >&2
  exit 1
fi

if ! reactor_output=$(cd "$cloud_root" && mvn -o \
    -pl firefox-common/firefox-base \
    -am \
    validate 2>&1); then
  printf '%s\n' "$reactor_output" >&2
  echo "firefox-base is not buildable from the root Maven reactor." >&2
  exit 1
fi

if ! printf '%s\n' "$reactor_output" | grep -Eq 'Building firefox-base 1\.0\.0'; then
  printf '%s\n' "$reactor_output" >&2
  echo "Expected firefox-base reactor version 1.0.0." >&2
  exit 1
fi

echo "Firefox common reactor audit passed."
