# ---------- Build-Stage: Windows-Binary via MinGW + vcpkg ----------
FROM ubuntu:22.04 AS build

ARG DEBIAN_FRONTEND=noninteractive
ARG VCPKG_ROOT=/opt/vcpkg
ARG VCPKG_TRIPLET=x64-mingw-dynamic
ARG REPO_URL=https://github.com/ldbv-by/osm2pgsql.git
ARG REPO_REF=v2.0.0-ldbv

# Basics + Cross-Compiler + CMake/Ninja
RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates curl git build-essential pkg-config \
    cmake ninja-build \
    mingw-w64 g++-mingw-w64-x86-64 \
    zip unzip tar python3 bison flex autoconf \
 && rm -rf /var/lib/apt/lists/*

# POSIX-Thread-Variante für MinGW (wichtig für -posix Toolchain)
RUN update-alternatives --set x86_64-w64-mingw32-gcc /usr/bin/x86_64-w64-mingw32-gcc-posix && \
    update-alternatives --set x86_64-w64-mingw32-g++ /usr/bin/x86_64-w64-mingw32-g++-posix

# vcpkg holen + bootstrappen
RUN git clone https://github.com/microsoft/vcpkg.git ${VCPKG_ROOT} \
 && ${VCPKG_ROOT}/bootstrap-vcpkg.sh

# Dependencies (dynamisch!) – proj[core] ohne Curl/Netzwerkfeatures
RUN ${VCPKG_ROOT}/vcpkg install --clean-after-build --triplet ${VCPKG_TRIPLET} \
    zlib bzip2 expat proj libpq \
    boost-program-options

# Pfad zum Triplet
ENV VCPKG_INSTALLED=${VCPKG_ROOT}/installed/${VCPKG_TRIPLET}

# Source holen
WORKDIR /src
RUN echo "Git Clone 14" && git clone --depth 1 --branch ${REPO_REF} ${REPO_URL} osm2pgsql

# Konfigurieren (kein statisches Linken; AppLocal von vcpkg deaktiviert -> wir kopieren DLLs manuell)
WORKDIR /src/osm2pgsql
RUN cmake -S . -B build -G Ninja \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_SYSTEM_NAME=Windows \
    -DCMAKE_C_COMPILER=x86_64-w64-mingw32-gcc-posix \
    -DCMAKE_CXX_COMPILER=x86_64-w64-mingw32-g++-posix \
    -DCMAKE_TOOLCHAIN_FILE=${VCPKG_ROOT}/scripts/buildsystems/vcpkg.cmake \
    -DVCPKG_TARGET_TRIPLET=${VCPKG_TRIPLET} \
    -DVCPKG_APPLOCAL_DEPS=OFF \
    -DCMAKE_FIND_PACKAGE_PREFER_CONFIG=ON \
    -DWITH_LUAJIT=ON

# Bauen
RUN cmake --build build --target osm2pgsql -j

# ---------- Paket zusammenstellen ----------
#  - EXE
#  - alle DLLs aus vcpkg/bin (inkl. libproj_9.dll, libpq.dll, libsqlite3.dll, usw.)
#  - MinGW-Runtime-DLLs (libstdc++-6.dll, libgcc_s_seh-1.dll, libwinpthread-1.dll)
#  - PROJ-Daten (proj.db, Grids) -> ./proj-data
RUN set -eux; \
    mkdir -p /dist; \
    cp build/osm2pgsql.exe /dist/; \
    # alle vcpkg-DLLs
    cp -n ${VCPKG_INSTALLED}/bin/*.dll /dist/ || true; \
    # MinGW-Runtime-DLLs dazulegen (Versionsordner über Wildcard)
    cp -n /usr/x86_64-w64-mingw32/lib/libwinpthread-1.dll /dist/ || true; \
    cp -n /usr/lib/gcc/x86_64-w64-mingw32/*-posix/libstdc++-6.dll /dist/ || true; \
    cp -n /usr/lib/gcc/x86_64-w64-mingw32/*-posix/libgcc_s_seh-1.dll /dist/ || true; \
    # PROJ-Daten
    mkdir -p /dist/proj-data; \
    cp -r ${VCPKG_INSTALLED}/share/proj/* /dist/proj-data/ || true;

# ZIP für bequemen Download
RUN (cd /dist && zip -r osm2pgsql-win64.zip .)

# ---------- Runtime-Stage: einfacher Download-Server ----------
FROM python:3.12-slim AS runtime
COPY --from=build /dist/osm2pgsql-win64.zip /dist/osm2pgsql-win64.zip
WORKDIR /dist
EXPOSE 8000
CMD ["python", "-m", "http.server", "8000"]
