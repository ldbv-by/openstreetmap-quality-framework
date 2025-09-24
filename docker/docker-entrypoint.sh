#!/bin/sh
set -e

export PGHOST="$OSM_QUALITY_FRAMEWORK_DATABASE_HOST"
export PGPORT="$OSM_QUALITY_FRAMEWORK_DATABASE_PORT"
export PGDATABASE="$OSM_QUALITY_FRAMEWORK_DATABASE"
export PGUSER="$OSM_QUALITY_FRAMEWORK_DATABASE_USERNAME"
export PGPASSWORD="$OSM_QUALITY_FRAMEWORK_DATABASE_PASSWORD"

# ---- Create .pgpass ----
echo "$PGHOST:$PGPORT:$PGDATABASE:$PGUSER:$PGPASSWORD" > ~/.pgpass
chmod 600 ~/.pgpass

# ---- Wait on database ----
echo "Wait on database $PGHOST:$PGPORT ..."
until pg_isready -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" >/dev/null 2>&1; do
  sleep 2
done
echo "Database is ready."

# ---- Ensure PostGIS ----
psql -v ON_ERROR_STOP=1 -c "CREATE EXTENSION IF NOT EXISTS postgis;"

# ---- Initialize OPENSTREETMAP_GEOMETRIES ----
SCHEMA_NAME="openstreetmap_geometries"
HAS_SCHEMA="$(psql -tAc "SELECT 1 FROM information_schema.schemata WHERE schema_name='${SCHEMA_NAME}'" || true)"

if [ "$HAS_SCHEMA" != "1" ]; then
  echo "Schema ${SCHEMA_NAME} not found. Creating and importing with osm2pgsql ..."
  psql -v ON_ERROR_STOP=1 -c "CREATE SCHEMA IF NOT EXISTS ${SCHEMA_NAME};"

  LUA_STYLE="/app/import/openstreetmap_geometries.lua"
  PBF_FILE="${PBF_FILE:-/app/import/data.osm.pbf}"

  if [ ! -f "$LUA_STYLE" ]; then
    echo "ERROR: Lua style '$LUA_STYLE' not found." >&2
    exit 1
  fi

  if [ -s "$PBF_FILE" ]; then
    osm2pgsql -c -s --log-level=debug --log-sql \
      -d "$PGDATABASE" \
      --schema="$SCHEMA_NAME" \
      -U "$PGUSER" \
      -H "$PGHOST" \
      -P "$PGPORT" \
      -O flex \
      -S "$LUA_STYLE" \
      "$PBF_FILE"

    echo "Import finished."
  else
    echo "No import is necessary. New Project!"
  fi

  psql -v ON_ERROR_STOP=1 -c "CREATE INDEX IF NOT EXISTS planet_osm_rels_rel_members_idx
                              ON ${SCHEMA_NAME}.planet_osm_rels
                              USING gin (${SCHEMA_NAME}.planet_osm_member_ids(members, 'R'::character(1)))
                              WITH (fastupdate = off);"

else
  echo "Schema ${SCHEMA_NAME} already exists – skipping import."
fi

exec java -jar /app/openstreetmap-quality-framework.jar
