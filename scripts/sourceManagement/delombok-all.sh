#!/bin/bash

# Lombok version
LOMBOK_VERSION=1.18.38
LOMBOK_JAR="lombok-${LOMBOK_VERSION}.jar"
LOMBOK_DIR="target/lombok"
LOMBOK_JAR_PATH="$LOMBOK_DIR/$LOMBOK_JAR"
LOMBOK_URL="https://repo1.maven.org/maven2/org/projectlombok/lombok/${LOMBOK_VERSION}/${LOMBOK_JAR}"

# Create Lombok directory if it doesn't exist
mkdir -p "$LOMBOK_DIR"

# Download Lombok jar if not present
if [ ! -f "$LOMBOK_JAR_PATH" ]; then
  echo "Downloading Lombok $LOMBOK_VERSION..."
  wget "$LOMBOK_URL" -O "$LOMBOK_JAR_PATH"
fi

# Set and clean output directory for delomboked code
OUT_DIR="$LOMBOK_DIR/delombok/main"
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Delombok the entire project
echo "Delomboking all Java files in src/main/java..."
java -jar "$LOMBOK_JAR_PATH" delombok src/main/java -d "$OUT_DIR"

echo "Delombok complete. See $OUT_DIR for output."
