#!/bin/bash

# Source directory where your zip files are located
SOURCE_DIR="/var/lib/jenkins/workspace/Testbed_Deploy_With_Docker/gitb-docker/tht-automation-testcases"

# Destination directory where you want to copy the zip files
DEST_DIR="/srv/tht/files"

# Check if the destination directory exists, if not create it
if [ ! -d "$DEST_DIR" ]; then
    mkdir -p "$DEST_DIR"
fi

# Loop through each zip file in the source directory
find "$SOURCE_DIR" -mindepth 2 -type f -name "*.zip" | while read -r zip_file; do
    # Get the parent directory name of the zip file
    parent_dir=$(dirname "$zip_file")

    # Extract the random directory name from the parent directory path
    random_dir=$(basename "$parent_dir")

    # Copy the zip file to the destination directory
    cp "$zip_file" "$DEST_DIR"

    echo "Copied $zip_file to $DEST_DIR"

done

echo "All zip files copied to $DEST_DIR"

