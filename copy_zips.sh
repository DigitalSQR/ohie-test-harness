#!/bin/bash

# Define directory paths
BASE_DIR="/srv"
TH_T_DIR="$BASE_DIR/tht"
ERROR_LOG_DIR="$TH_T_DIR/error-logs"
FILES_DIR="$TH_T_DIR/files-testbed"

# Create directories
sudo mkdir -p "$TH_T_DIR"
sudo mkdir -p "$ERROR_LOG_DIR"
sudo mkdir -p "$FILES_DIR"

# Source directory where your zip files are located
SOURCE_DIR="./gitb-prerequisite/tht-automation-testcases"

# Destination directory where you want to copy the zip files
FILES_DIR="/srv/tht/files-testbed"

# Check if the destination directory exists, if not create it
if [ ! -d "$FILES_DIR" ]; then
    mkdir -p "$FILES_DIR"
fi

# Loop through each zip file in the source directory
find "$SOURCE_DIR" -mindepth 2 -type f -name "*.zip" | while read -r zip_file; do
    # Get the parent directory name of the zip file
    parent_dir=$(dirname "$zip_file")

    # Extract the random directory name from the parent directory path
    random_dir=$(basename "$parent_dir")

    # Copy the zip file to the destination directory
    cp "$zip_file" "$FILES_DIR"

    echo "Copied $zip_file to $FILES_DIR"

done

echo "All zip files copied to $FILES_DIR"

