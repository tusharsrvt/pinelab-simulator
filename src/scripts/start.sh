#!/bin/bash

# CBS Simulator Start Script
# Usage: ./scripts/start.sh [port]
# Place this script in the same folder as the JAR and config/ folder

APP_NAME="pinelab-cbs-simulator"
JAR_FILE="${APP_NAME}.jar"

# Default port (will be overridden by application.properties if set)
PORT=${1:-10002}

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Go to the parent directory (where JAR and config are)
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_DIR"
echo "Starting from directory: $(pwd)"

# Check if JAR exists
if [ -f "$JAR_FILE" ]; then
    echo "Found JAR: $JAR_FILE"
else
    echo "Error: JAR file '$JAR_FILE' not found!"
    exit 1
fi

# Check if required files exist
MISSING=0

# Check for config file (supports both .json and .yaml)
if [ -f "config/simulator-config.json" ]; then
    echo "Found config: config/simulator-config.json"
elif [ -f "config/simulator-config.yaml" ]; then
    echo "Found config: config/simulator-config.yaml"
else
    echo "Warning: 'config/simulator-config.json' or 'config/simulator-config.yaml' not found."
    MISSING=1
fi

if [ ! -f "config/response-codes.yaml" ]; then
    echo "Warning: 'config/response-codes.yaml' not found."
    MISSING=1
fi

if [ ! -f "config/application.properties" ]; then
    echo "Warning: 'config/application.properties' not found."
    MISSING=1
fi

TEMPLATE_COUNT=$(ls templates/*.json 2>/dev/null | wc -l)
if [ "$TEMPLATE_COUNT" -lt 1 ]; then
    echo "Warning: No template files found in templates/"
    MISSING=1
fi

if [ $MISSING -eq 1 ]; then
    echo "Some files are missing. Please ensure all required files are present."
    echo "Required: config/simulator-config.json (or .yaml), config/response-codes.yaml, config/application.properties, templates/*.json"
    exit 1
fi

# Try to read port from application.properties (command line arg takes precedence)
if [ -f "config/application.properties" ]; then
    CONFIG_PORT=$(grep -E "^server\.port=" config/application.properties | cut -d'=' -f2 | tr -d '[:space:]')
    if [ -n "$CONFIG_PORT" ]; then
        PORT=$CONFIG_PORT
    fi
fi

# Create logs directory
mkdir -p logs

# Start the application
echo "Starting $APP_NAME on port $PORT..."

# Start the application with external config from config/ folder
java -jar "$JAR_FILE" --spring.config.location=file:./config/ > logs/app.log 2>&1 &
PID=$!

echo "Started with PID: $PID"
echo "Log file: logs/app.log"

# Wait a moment and check if it's running
sleep 3
if ps -p $PID > /dev/null; then
    echo "Application started successfully!"
    echo "Health check: http://localhost:$PORT/api/health"
else
    echo "Error: Application failed to start. Check logs/app.log"
    cat logs/app.log 2>/dev/null
    exit 1
fi