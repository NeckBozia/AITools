#!/bin/bash
# 编译并打包 加密工具箱 GUI
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo "=== 编译 ==="
mkdir -p target/classes
javac -d target/classes -encoding UTF-8 $(find src -name "*.java")

echo "=== 打包 JAR ==="
echo "Main-Class: org.example.encryption.gui.CryptoToolGUI" > target/MANIFEST.MF
cd target/classes
jar cfm ../crypto-tool-gui.jar ../MANIFEST.MF .
cd ../..

echo "=== 完成 ==="
echo "运行: java -jar target/crypto-tool-gui.jar"
