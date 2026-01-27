#!/bin/bash

# Konfigurasi Direktori
SRC_DIR="src"
BUILD_DIR="build/classes"
LIB_DIR="."

# Library JAR
LIB_FLATLAF="flatlaf-3.4.1.jar"
LIB_MYSQL="mysql-connector-j-9.5.0.jar"

# Gabungkan Classpath
CP="${LIB_DIR}/${LIB_FLATLAF}:${LIB_DIR}/${LIB_MYSQL}:${BUILD_DIR}"

# Buat direktori build jika belum ada
mkdir -p "$BUILD_DIR"

echo "🔨 Mengkompilasi source code..."
# Compile semua file Java
find "$SRC_DIR" -name "*.java" > sources.txt
javac -cp "$CP" -d "$BUILD_DIR" @sources.txt
rm sources.txt

if [ $? -eq 0 ]; then
    echo "✅ Kompilasi berhasil!"
    echo "🚀 Menjalankan aplikasi..."
    # Copy assets ke build folder jika ada (penting untuk logo/gambar)
    cp -r src/pendataankecamatan/assets build/classes/pendataankecamatan/ 2>/dev/null
    
    java -cp "$CP" pendataankecamatan.Main
else
    echo "❌ Kompilasi gagal."
fi
