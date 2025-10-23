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


# ---- Initialize CHANGESET_DATA ----
SCHEMA_NAME="changeset_data"
HAS_SCHEMA="$(psql -tAc "SELECT 1 FROM information_schema.schemata WHERE schema_name='${SCHEMA_NAME}'" || true)"

if [ "$HAS_SCHEMA" != "1" ]; then
  echo "Schema ${SCHEMA_NAME} not found. Creating and cloning structure from openstreetmap_geometries ..."
  psql -v ON_ERROR_STOP=1 -c "CREATE SCHEMA IF NOT EXISTS ${SCHEMA_NAME};"

  TMP_DUMP="$(mktemp)"
  pg_dump --schema-only --no-owner --no-privileges -n openstreetmap_geometries -d "$PGDATABASE" > "$TMP_DUMP"

  sed -E '
      s/\bopenstreetmap_geometries\b/changeset_data/g;
      s/CREATE SCHEMA changeset_data;/CREATE SCHEMA IF NOT EXISTS changeset_data;/g
    ' "$TMP_DUMP" | psql -v ON_ERROR_STOP=1 -d "$PGDATABASE"

  rm -f "$TMP_DUMP"

  psql -v ON_ERROR_STOP=1 <<'SQL'
CREATE TABLE IF NOT EXISTS changeset_data.changeset_objects (
  id                bigserial    PRIMARY KEY,
  osm_id            bigint       NOT NULL,
  geometry_type     text         NOT NULL,
  changeset_id      bigint       NOT NULL,
  operation_type    text         NOT NULL,   -- 'CREATE','MODIFY','DELETE'
  created_at        timestamptz  NOT NULL DEFAULT now(),
  CONSTRAINT chk_changeset_objects_geom_type
    CHECK (geometry_type IN ('NODE','WAY','AREA', 'MULTIPOLYGON', 'RELATION')),
  CONSTRAINT chk_changeset_objects_op_type
    CHECK (operation_type IN ('CREATE','MODIFY','DELETE'))
);

CREATE INDEX IF NOT EXISTS idx_changeset_objects_changeset_id
  ON changeset_data.changeset_objects (changeset_id);

CREATE INDEX IF NOT EXISTS idx_changeset_objects_osm
  ON changeset_data.changeset_objects (geometry_type, osm_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_changeset_objects
  ON changeset_data.changeset_objects (changeset_id, geometry_type, osm_id, operation_type);

ALTER TABLE IF EXISTS changeset_data.nodes             ADD COLUMN IF NOT EXISTS changeset_id bigint;
ALTER TABLE IF EXISTS changeset_data.ways              ADD COLUMN IF NOT EXISTS changeset_id bigint;
ALTER TABLE IF EXISTS changeset_data.areas             ADD COLUMN IF NOT EXISTS changeset_id bigint;
ALTER TABLE IF EXISTS changeset_data.relations         ADD COLUMN IF NOT EXISTS changeset_id bigint;
ALTER TABLE IF EXISTS changeset_data.relation_members  ADD COLUMN IF NOT EXISTS changeset_id bigint;
SQL

else
  echo "Schema ${SCHEMA_NAME} already exists – skipping import."
fi

exec java -jar /app/openstreetmap-quality-framework.jar
