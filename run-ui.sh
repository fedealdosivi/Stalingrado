#!/bin/bash

# =====================================================
# Stalingrado Battle Simulator - JavaFX UI Launcher
# =====================================================

set -e

echo "=========================================="
echo "Stalingrado Battle Simulator - GUI Mode"
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
    echo "Please install JDK 8+ and try again"
    echo "Visit: https://adoptium.net/"
    exit 1
fi

echo "✓ Java $(java -version 2>&1 | awk -F '"' '/version/ {print $2}') detected"
echo "✓ Maven $(mvn -version | head -n 1 | awk '{print $3}') detected"
echo ""

# Build the project if needed
if [ ! -f "target/stalingrado-battle-simulator-2.0.0-jar-with-dependencies.jar" ]; then
    echo "Building project..."
    mvn clean package -DskipTests
    echo ""
fi

echo "✓ Build complete!"
echo ""
echo "Starting Stalingrado Battle Simulator UI..."
echo "=========================================="
echo ""

# Run with JavaFX modules explicitly added
mvn exec:java -Dexec.mainClass="View.BattleSimulatorUI"