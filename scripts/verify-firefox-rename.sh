#!/bin/sh
set -eu

old_lower="c""cwb"
old_upper="C""CWB"
old_title="C""cwb"
failed=0

scan_paths() {
  find . \
    -path './.git' -prune -o \
    -path '*/target' -prune -o \
    -path './logs' -prune -o \
    -path './.idea' -prune -o \
    -path './.vscode' -prune -o \
    -path './docs/superpowers' -prune -o \
    -name 'spy.log' -prune -o \
    -print
}

old_paths=$(scan_paths | grep -E "${old_lower}|${old_upper}|${old_title}" || true)
if [ -n "$old_paths" ]; then
  echo "Old project identifiers remain in paths:"
  printf '%s\n' "$old_paths" | sed -n '1,40p'
  failed=1
fi

old_content=$(scan_paths | while IFS= read -r candidate; do
  [ -f "$candidate" ] || continue
  grep -IlE "${old_lower}|${old_upper}|${old_title}" "$candidate" 2>/dev/null || true
done)
if [ -n "$old_content" ]; then
  echo "Old project identifiers remain in file content:"
  printf '%s\n' "$old_content" | sed -n '1,80p'
  failed=1
fi

package_errors=$(find . \
  -path './.git' -prune -o \
  -path '*/target' -prune -o \
  -type f -name '*.java' -print | while IFS= read -r java_file; do
    printf '%s\n' "$java_file" | grep -q '/src/main/java/' || continue
    declared=$(sed -n 's/^package[[:space:]]\+\([^;]*\);.*/\1/p' "$java_file" | head -n 1)
    [ -n "$declared" ] || continue
    relative=${java_file#*/src/main/java/}
    expected=$(dirname "$relative" | tr '/' '.')
    if [ "$declared" != "$expected" ]; then
      printf '%s: package %s, expected %s\n' "$java_file" "$declared" "$expected"
    fi
  done)
if [ -n "$package_errors" ]; then
  echo "Java package/path mismatches:"
  printf '%s\n' "$package_errors" | sed -n '1,80p'
  failed=1
fi

if [ "$failed" -ne 0 ]; then
  exit 1
fi

echo "Firefox rename audit passed."
