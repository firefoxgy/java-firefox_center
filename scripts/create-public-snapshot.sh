#!/bin/sh
set -eu

project_root=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd -P)
cd "$project_root"

previous_main=''
if git show-ref --verify --quiet refs/heads/main; then
  previous_main=$(git rev-parse refs/heads/main)
  if [ "$(git rev-list --count refs/heads/main)" -ne 1 ] ||
     git show-ref --verify --quiet refs/remotes/github/main; then
    echo "Existing main is not an unpublished one-commit snapshot; refusing to overwrite it." >&2
    exit 1
  fi
fi

snapshot_tmp=$(mktemp -d)
snapshot_index="$snapshot_tmp/index"
trap 'rm -f "$snapshot_index"; rmdir "$snapshot_tmp"' EXIT
export GIT_INDEX_FILE="$snapshot_index"

git read-tree HEAD
git add -A -- . ':!spy.log'
git rm -r --cached --ignore-unmatch .idea ui-ant-design-vue/file >/dev/null
git ls-files -z '*.iml' | xargs -0 git rm --cached --ignore-unmatch >/dev/null
snapshot_tree=$(git write-tree)
snapshot_commit=$(printf '%s\n' 'Initial public release of java-firefox_center' | git commit-tree "$snapshot_tree")
git update-ref refs/heads/main "$snapshot_commit" "$previous_main"

echo "Created sanitized public snapshot at $snapshot_commit"
