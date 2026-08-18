#!/bin/sh
set -eu

project_root=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd -P)
findings=$(mktemp)
trap 'rm -f "$findings"' EXIT

find "$project_root/firefox-cloud" -type f \( \
  -name '*.yml' -o -name '*.yaml' -o -name '*.properties' \
\) ! -path '*/target/*' -print0 |
while IFS= read -r -d '' file; do
  case "$file" in
    */i18n/*) continue ;;
  esac
  awk '
    /^[[:space:]]*#/ { next }
    {
      line = $0
      lower = tolower(line)
      if (lower ~ /^[[:space:]]*[^#:=]*(username|password|passwd|secret|access[-_.]?key|secret[-_.]?key|app[-_.]?(id|key|secret)|token)[[:space:]]*[:=]/ ||
          lower ~ /^[[:space:]]*[^#:=]*(server-addr|jdbc[-_.]?url|datasource[-_.]?url)[[:space:]]*[:=]/ ||
          lower ~ /^[[:space:]]*(url|host)[[:space:]]*[:=].*(jdbc:|[0-9]{1,3}\.[0-9]{1,3}\.)/ ||
          lower ~ /(^|[^0-9])(10\.|192\.168\.|172\.(1[6-9]|2[0-9]|3[01])\.)/) {
        if (line !~ /\$\{[A-Z0-9_]+(:[^}]*)?\}/ &&
            lower !~ /(^|[:=][[:space:]]*)(null|change_me|changeme)[[:space:]]*$/ &&
            lower !~ /[:=][[:space:]]*$/) {
          key = line
          sub(/[=:].*$/, "", key)
          gsub(/^[[:space:]]+|[[:space:]]+$/, "", key)
          print FILENAME ":" FNR ":" key
        }
      }
    }
  ' "$file" >> "$findings"
done

if internal_endpoint_files=$(rg -l -P \
  --glob '*.{yml,yaml,properties,xml,html,sql,txt}' \
  --glob '!ui-ant-design-vue/file/**' \
  --glob '!**/target/**' \
  '(?<![0-9.])(?:10|192\.168|172\.(?:1[6-9]|2[0-9]|3[01]))(?:\.\d{1,3}){2,3}(?![0-9.])|caffeine\.kpinfo\.cn|repo\.rdc\.aliyun\.com' \
  "$project_root"); then
  printf '%s\n' "$internal_endpoint_files" >&2
  echo "Public-safety audit found internal endpoints." >&2
  exit 1
fi

if keystore_files=$(rg -l -U -i \
  --glob '!**/target/**' \
  'keytool.*-(key|store)pass[[:space:]]+[^$<[:space:]]+|new[[:space:]]+KeyStoreKeyFactory\([^,]+,[[:space:]]*"[^"]+"' \
  "$project_root"); then
  printf '%s\n' "$keystore_files" >&2
  echo "Public-safety audit found literal keystore credentials." >&2
  exit 1
fi

if [ -s "$findings" ]; then
  sort -u "$findings" >&2
  echo "Public-safety audit found literal credentials or private service endpoints." >&2
  exit 1
fi

echo "Public-safety audit passed."
