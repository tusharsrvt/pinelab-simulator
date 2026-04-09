#!/bin/bash

# CBS Simulator Stop Script
# Usage: ./stop.sh

APP_NAME="pinelab-cbs-simulator"

# Find the Java process running the CBS Simulator
PID=$(ps -ef | grep "$APP_NAME.jar" | grep -v grep | awk '{print $2}')

if [ -z "$PID" ]; then
    echo "No running instance of $APP_NAME found."
    exit 0
fi

echo "Stopping $APP_NAME (PID: $PID)..."
kill $PID

# Wait for process to stop
MAX_WAIT=10
COUNT=0
while ps -p $PID > /dev/null 2>&1; do
    if [ $COUNT -ge $MAX_WAIT ]; then
        echo "Force killing process..."
        kill -9 $PID
        break
    fi
    sleep 1
    COUNT=$((COUNT + 1))
    echo "Waiting for process to stop..."
done

echo "$APP_NAME stopped successfully."
exit 0