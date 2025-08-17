#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

main_dir="$REPO_ROOT/src/main/java"
test_dir="$REPO_ROOT/src/test/unit/java"

# Get class names from main_dir
mapfile -t main_classes < <(find "$main_dir" -type f -name "*.java" | awk -F/ '{print $NF}' | sed 's/.java$//')
# Get class names from test_dir (ending with Test)
mapfile -t test_classes < <(find "$test_dir" -type f -name "*Test.java" | awk -F/ '{print $NF}' | sed 's/.java$//')

echo "Classes without corresponding unit tests:"
for class in "${main_classes[@]}"; do
  test_class="${class}Test"
  found=0
  for tclass in "${test_classes[@]}"; do
    if [[ "$tclass" == "$test_class" ]]; then
      found=1
      break
    fi
  done
  if [[ $found -eq 0 ]]; then
    echo "$class"
  fi
done
