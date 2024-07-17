#!/bin/bash
# Define variables
CONTAINER_NAME="testing-harness-tool_tht-postgressql_1"  # This should match your container name
DB_NAME="testing_harness_tool"
DB_USER="postgres"
DB_PASSWORD="argusadmin"
BACKUP_DIR="/srv/tht/backup"
DATE=$(date +"%Y%m%d%H%M")

# Run pg_dump inside the container
docker exec -e PGPASSWORD=$DB_PASSWORD $CONTAINER_NAME pg_dump -U $DB_USER $DB_NAME > $BACKUP_DIR/db_backup_$DATE.sql

# List all backup files, sort them by modification time (oldest first), and keep the latest two
ls -t $BACKUP_DIR/db_backup_*.sql | tail -n +3 | xargs rm --

