FROM azul/zulu-openjdk:24.0.2-24.32 AS openstreetmap-quality-framework

ARG OSM2PGSQL_REPO_URL
ARG OSM2PGSQL_REPO_BRANCH

# Build dependencies
RUN apt-get update && apt-get install -y \
    make cmake g++ libboost-dev \
    libexpat1-dev zlib1g-dev libpotrace-dev \
    libopencv-dev libbz2-dev libpq-dev libproj-dev lua5.3 liblua5.3-dev \
    pandoc nlohmann-json3-dev pyosmium \
    libluajit-5.1-dev \
    git \
    maven \
    postgresql-client \
 && rm -rf /var/lib/apt/lists/*

# ---- Build osm2pgsql ----
WORKDIR /src
RUN git clone --branch $OSM2PGSQL_REPO_BRANCH $OSM2PGSQL_REPO_URL

WORKDIR /src/osm2pgsql/build
RUN cmake -D WITH_LUAJIT=ON -D CMAKE_BUILD_TYPE=RelWithDebInfo ..
RUN make
RUN make install

# ---- Build openstreetmap-quality-framework ----
WORKDIR /src/openstreetmap-quality-framework
COPY pom.xml .
RUN mvn -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package && \
    install -d /app && \
    JAR="$(find target -maxdepth 1 -type f -name '*.jar' \
           ! -name '*-sources.jar' ! -name '*-javadoc.jar' | head -n 1)" && \
    cp "$JAR" /app/openstreetmap-quality-framework.jar

# ---- Clean ----
RUN rm -rf /src

# ---- Copy initial openstreetmap-geometries data ----
COPY docker/openstreetmap_geometries.lua /app/import/openstreetmap_geometries.lua

# ---- Entrypoint Script ----
COPY docker/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["docker-entrypoint.sh"]
