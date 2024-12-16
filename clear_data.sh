#!/bin/bash

# Define the target directory
TARGET_DIR="/srv/tht"

# List of folders to clear
FOLDERS=("backup" "error-logs" "files" "files-dev")

# Check if the target directory exists
if [ ! -d "$TARGET_DIR" ]; then
  echo "Error: Target directory $TARGET_DIR does not exist."
  exit 1
fi

# Iterate through each folder and clear its contents
for FOLDER in "${FOLDERS[@]}"; do
  FOLDER_PATH="$TARGET_DIR/$FOLDER"

  if [ -d "$FOLDER_PATH" ]; then
    echo "Clearing contents of $FOLDER_PATH..."
    rm -rf "${FOLDER_PATH:?}"/*  # Use ':?' to prevent accidental deletion of root directories
    echo "Contents of $FOLDER_PATH cleared."
  else
    echo "Warning: $FOLDER_PATH does not exist. Skipping..."
  fi
done

echo "All specified folders have been cleared."

