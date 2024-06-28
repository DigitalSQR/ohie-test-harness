#!/bin/bash
# Define variables
CONTAINER_NAME="testing-harness-tool_tht-postgressql_1"  # This should match your container name
DB_NAME="testing_harness_tool"
DB_USER="postgres"
DB_PASSWORD="argusadmin"
BACKUP_DIR="/srv/tht/backup"

# Find the latest backup file
LATEST_BACKUP=$(ls -t $BACKUP_DIR/db_backup_*.sql | head -1)

# Check if the backup file exists
if [ -z "$LATEST_BACKUP" ]; then
  echo "No backup file found in $BACKUP_DIR"
  exit 1
fi

echo "Restoring database from the latest backup file: $LATEST_BACKUP"

# Copy the backup file to the container
docker cp $LATEST_BACKUP $CONTAINER_NAME:/tmp/latest_backup.sql

# Uncompress the backup file inside the container (if compressed)
docker exec $CONTAINER_NAME bash -c "gunzip -f /tmp/latest_backup.sql"

# Restore the database from the backup file
docker exec -e PGPASSWORD=$DB_PASSWORD $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -f /tmp/latest_backup.sql

# Clean up the temporary backup file inside the container
docker exec $CONTAINER_NAME rm /tmp/latest_backup.sql

echo "Database restoration from the latest backup completed successfully."

