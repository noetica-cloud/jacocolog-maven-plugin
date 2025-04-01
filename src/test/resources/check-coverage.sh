#!/bin/bash

set -e  # Exit on error

LOG_DIR="$1"  # First argument is the IT logs directory

ORDER=("Class Coverage:" "Method Coverage:" "Branch Coverage:" "Line Coverage:" "Instruction Coverage:" "Complexity Coverage:")
ORDER_KEYS=("CLASS" "METHOD" "BRANCH" "LINE" "INSTRUCTION" "COMPLEXITY")

# Define expected coverage per module
declare -A EXPECTED_RATIOS
EXPECTED_RATIOS["inheritance"]="55.55 66.66 unknown 50 55.55 66.66|10.52 33.33 unknown 25 10.52 33.33|19.14 50 unknown 37.5 19.14 50"
EXPECTED_RATIOS["simple"]="55.55 66.66 unknown 50 55.55 66.66"

# Validate log directory
if [ ! -d "$LOG_DIR" ]; then
    echo "❌ Coverage log directory not found: $LOG_DIR" && exit 1
fi

for PROJECT_PATH in "$LOG_DIR"/*; do
    echo "$PROJECT_PATH"
    [ -d "$PROJECT_PATH" ] || continue  # Skip non-directory entries
    PROJECT_NAME=$(basename "$PROJECT_PATH")
    LOG="$PROJECT_PATH/build.log"

    if [ ! -f "$LOG" ]; then
        echo "❌ Missing log file for module $PROJECT_NAME" && exit 1
    fi

    echo "🔍 Checking coverage for module: $PROJECT_NAME"

    # Convert the string into an array (split by "|")
    IFS='|' read -ra MODULE_COVERAGE <<< "${EXPECTED_RATIOS[$PROJECT_NAME]}"
    ITER=0
    grep -n "Test Coverage:" $LOG | cut -d: -f1 | while read -r LINE; do
        # Extract the correct set of coverage values
        RATIOS=( ${MODULE_COVERAGE[$ITER]} )
        LINE=$LINE+1
        for INDEX in "${!ORDER[@]}"; do
            METRIC="${ORDER[$INDEX]}"
            METRIC_KEY="${ORDER_KEYS[$INDEX]}"

            EXPECTED="${RATIOS[$INDEX]}"

            RESULT=$(sed -n "${LINE_NUM}p" "$LOG" | grep "$METRIC" | head -n 1)
            if [ -z "$RESULT" ]; then
                echo "❌ Missing $METRIC in $LOG for module $PROJECT_NAME" && exit 1
            fi

            VALUE=$(echo "$RESULT" | grep -oP '[0-9]+(\.[0-9]+)?' | head -n 1)
            if [ -z "$VALUE" ]; then
              VALUE="unknown"
            fi

            if [ "$VALUE" != "$EXPECTED" ]; then
                echo "❌ $METRIC value in $PROJECT_NAME ($VALUE), expected $EXPECTED" && exit 1
            fi
        done
    done

    echo "✅ $PROJECT_NAME coverage check passed!"
done

echo "🎉 All coverage checks passed!"