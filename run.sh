#!/bin/bash

# =====================================================
# Stalingrado Battle Simulator - Quick Start Script
# =====================================================

set -e

echo "=========================================="
echo "Stalingrado Battle Simulator"
echo "Version 2.0.0 (2025)"
echo "=========================================="
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven 3.6+ and try again"
    echo "Visit: https://maven.apache.org/download.cgi"
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install JDK 17+ and try again"
    echo "Visit: https://adoptium.net/"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "ERROR: Java 17 or higher is required"
    echo "Current version: $(java -version 2>&1 | head -n 1)"
    exit 1
fi

echo "✓ Java $(java -version 2>&1 | awk -F '"' '/version/ {print $2}') detected"
echo "✓ Maven $(mvn -version | head -n 1 | awk '{print $3}') detected"
echo ""

# Build the project
echo "Building project..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Build successful!"
    echo ""
    echo "Starting Stalingrado Battle Simulator..."
    echo "=========================================="
    echo ""

    # Run the application
    java -jar target/stalingrado-battle-simulator-2.0.0-jar-with-dependencies.jar
else
    echo ""
    echo "✗ Build failed. Please check the errors above."
    exit 1
fi
