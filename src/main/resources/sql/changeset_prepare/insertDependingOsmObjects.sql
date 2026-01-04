DO $$
DECLARE
    m_node_ids bigint[] := ___NODE_IDS___;
    m_way_ids  bigint[] := ___WAY_IDS___;
    m_rel_ids  bigint[] := ___RELATION_IDS___;

    m_new int;
    m_cnt int;
BEGIN
    CREATE TEMP TABLE copy_nodes (id bigint PRIMARY KEY, directlyAffected boolean NOT NULL DEFAULT FALSE, recursive_checked boolean NOT NULL DEFAULT FALSE) ON COMMIT DROP;
    CREATE TEMP TABLE copy_ways  (id bigint PRIMARY KEY, directlyAffected boolean NOT NULL DEFAULT FALSE, recursive_checked boolean NOT NULL DEFAULT FALSE) ON COMMIT DROP;
    CREATE TEMP TABLE copy_rels  (id bigint PRIMARY KEY, directlyAffected boolean NOT NULL DEFAULT FALSE, recursive_checked boolean NOT NULL DEFAULT FALSE) ON COMMIT DROP;

    CREATE TEMP TABLE todo_nodes (id bigint PRIMARY KEY) ON COMMIT DROP;
    CREATE TEMP TABLE todo_ways  (id bigint PRIMARY KEY) ON COMMIT DROP;
    CREATE TEMP TABLE todo_rels  (id bigint PRIMARY KEY) ON COMMIT DROP;

    -- init copy tables with changeset nodes, ways and relations
    INSERT INTO copy_nodes
    SELECT DISTINCT unnest(m_node_ids), TRUE WHERE m_node_ids IS NOT NULL
    ON CONFLICT DO NOTHING;

    INSERT INTO copy_ways
    SELECT DISTINCT unnest(m_way_ids), TRUE WHERE m_way_ids IS NOT NULL
    ON CONFLICT DO NOTHING;

    INSERT INTO copy_rels
    SELECT DISTINCT unnest(m_rel_ids), TRUE WHERE m_rel_ids IS NOT NULL
    ON CONFLICT DO NOTHING;

    -- Node -> Ways (only for changeset nodes)
    INSERT INTO copy_ways
    SELECT DISTINCT w.id, TRUE
    FROM openstreetmap_geometries.planet_osm_ways w,
         (SELECT array_agg(id)::bigint[] AS ids FROM copy_nodes) cn
    WHERE w.nodes && cn.ids
    ON CONFLICT DO NOTHING;
    GET DIAGNOSTICS m_cnt = ROW_COUNT; m_new := m_new + m_cnt;

    -- fill copy tables recursive
    LOOP
        TRUNCATE todo_nodes; TRUNCATE todo_ways; TRUNCATE todo_rels;

        INSERT INTO todo_nodes SELECT id FROM copy_nodes WHERE recursive_checked = FALSE;
        INSERT INTO todo_ways  SELECT id FROM copy_ways  WHERE recursive_checked = FALSE;
        INSERT INTO todo_rels  SELECT id FROM copy_rels  WHERE recursive_checked = FALSE;

        m_new := 0;

        -- 1. Way -> Nodes
        INSERT INTO copy_nodes
        SELECT DISTINCT unnest(w.nodes), FALSE
        FROM openstreetmap_geometries.planet_osm_ways w,
             copy_ways cw
        WHERE cw.id = w.id
          AND cw.recursive_checked = FALSE
        ON CONFLICT DO NOTHING;
        GET DIAGNOSTICS m_cnt = ROW_COUNT; m_new := m_new + m_cnt;

        -- 2. Relations -> Nodes, Ways, Relations (Downstream)
        -- 2a. Relations -> Nodes
        INSERT INTO copy_nodes
        SELECT DISTINCT (member->>'ref')::bigint, FALSE
        FROM openstreetmap_geometries.planet_osm_rels r,
             copy_rels cr,
             jsonb_array_elements(r.members) AS member
        WHERE cr.id = r.id
          AND cr.recursive_checked = FALSE
          AND member->>'type' = 'N'
        ON CONFLICT DO NOTHING;
        GET DIAGNOSTICS m_cnt = ROW_COUNT; m_new := m_new + m_cnt;

        -- 2b. Relations -> Ways
        INSERT INTO copy_ways
        SELECT DISTINCT (member->>'ref')::bigint, FALSE
        FROM openstreetmap_geometries.planet_osm_rels r,
             copy_rels cr,
             jsonb_array_elements(r.members) AS member
        WHERE cr.id = r.id
          AND cr.recursive_checked = FALSE
          AND member->>'type' = 'W'
        ON CONFLICT DO NOTHING;
        GET DIAGNOSTICS m_cnt = ROW_COUNT; m_new := m_new + m_cnt;

        -- 2c. Relations -> Relations
        INSERT INTO copy_rels
        SELECT DISTINCT (member->>'ref')::bigint, FALSE
        FROM openstreetmap_geometries.planet_osm_rels r,
             copy_rels cr,
             jsonb_array_elements(r.members) AS member
        WHERE cr.id = r.id
          AND cr.recursive_checked = FALSE
          AND member->>'type' = 'R'
        ON CONFLICT DO NOTHING;
        GET DIAGNOSTICS m_cnt = ROW_COUNT; m_new := m_new + m_cnt;

        -- 3. Node, Way, Relation -> Relation (Upstream)

        -- 3a. Node -> Relation
        INSERT INTO copy_rels
        SELECT r.id,
               CASE WHEN openstreetmap_geometries.planet_osm_member_ids(r.members, 'N'::char(1)) && cn.ids_direct THEN TRUE
                    ELSE FALSE
                   END AS directlyAffected
        FROM openstreetmap_geometries.planet_osm_rels r,
             (SELECT COALESCE(array_agg(id) FILTER (WHERE directlyAffected), '{}')::bigint[] AS ids_direct,
                  COALESCE(array_agg(id), '{}')::bigint[]                                 AS ids
              FROM copy_nodes
              WHERE recursive_checked = FALSE) cn
        WHERE openstreetmap_geometries.planet_osm_member_ids(members, 'N'::char(1)) && cn.ids
        ON CONFLICT (id) DO UPDATE
                                SET directlyAffected = copy_rels.directlyAffected OR EXCLUDED.directlyAffected;
        GET DIAGNOSTICS m_cnt = ROW_COUNT; m_new := m_new + m_cnt;

        -- 3b. Way -> Relation
        INSERT INTO copy_rels
        SELECT r.id,
               CASE WHEN openstreetmap_geometries.planet_osm_member_ids(r.members, 'W'::char(1)) && cw.ids_direct THEN TRUE
                    ELSE FALSE
                   END AS directlyAffected
        FROM openstreetmap_geometries.planet_osm_rels r,
             (SELECT COALESCE(array_agg(id) FILTER (WHERE directlyAffected), '{}')::bigint[] AS ids_direct,
                  COALESCE(array_agg(id), '{}')::bigint[]                                 AS ids
              FROM copy_ways
              WHERE recursive_checked = FALSE) cw
        WHERE openstreetmap_geometries.planet_osm_member_ids(members, 'W'::char(1)) && cw.ids
        ON CONFLICT DO NOTHING;
        GET DIAGNOSTICS m_cnt = ROW_COUNT; m_new := m_new + m_cnt;

        -- 3c. Relation -> Relation
        -- INFO: planet_osm_rels_rel_members_idx is necessary
        INSERT INTO copy_rels
        SELECT r.id,
               CASE WHEN openstreetmap_geometries.planet_osm_member_ids(r.members, 'R'::char(1)) && cr.ids_direct THEN TRUE
                    ELSE FALSE
                   END AS directlyAffected
        FROM openstreetmap_geometries.planet_osm_rels r,
             (SELECT COALESCE(array_agg(id) FILTER (WHERE directlyAffected), '{}')::bigint[] AS ids_direct,
                  COALESCE(array_agg(id), '{}')::bigint[]                                 AS ids
              FROM copy_rels
              WHERE recursive_checked = FALSE) cr
        WHERE openstreetmap_geometries.planet_osm_member_ids(members, 'R'::char(1)) && cr.ids
        ON CONFLICT DO NOTHING;
        GET DIAGNOSTICS m_cnt = ROW_COUNT; m_new := m_new + m_cnt;

        UPDATE copy_nodes cn SET recursive_checked = TRUE FROM todo_nodes tn WHERE cn.id = tn.id;
        UPDATE copy_ways  cw SET recursive_checked = TRUE FROM todo_ways  tw WHERE cw.id = tw.id;
        UPDATE copy_rels  cr SET recursive_checked = TRUE FROM todo_rels  tr WHERE cr.id = tr.id;

        -- Finish loop when no new object is found.
        IF m_new = 0 THEN
            EXIT;
        END IF;
    END LOOP;

    RAISE NOTICE 'Found Nodes: %', (SELECT COUNT(*) FROM copy_nodes);
    RAISE NOTICE 'Found Ways:  %', (SELECT COUNT(*) FROM copy_ways);
    RAISE NOTICE 'Found Rels:  %', (SELECT COUNT(*) FROM copy_rels);

    -- Copy planet to changeset_prepare
    INSERT INTO ___CHANGESET_PREPARE___.planet_osm_nodes
    SELECT pn.*
    FROM openstreetmap_geometries.planet_osm_nodes pn,
         copy_nodes cn
    WHERE cn.id = pn.id;

    INSERT INTO ___CHANGESET_PREPARE___.planet_osm_ways
    SELECT pw.*
    FROM openstreetmap_geometries.planet_osm_ways pw,
         copy_ways cw
    WHERE cw.id = pw.id;

    INSERT INTO ___CHANGESET_PREPARE___.planet_osm_rels
    SELECT pr.*
    FROM openstreetmap_geometries.planet_osm_rels pr,
         copy_rels cr
    WHERE cr.id = pr.id;

    -- Copy nodes, ways, areas to changeset_prepare
    INSERT INTO ___CHANGESET_PREPARE___.nodes
    SELECT nd.*
    FROM openstreetmap_geometries.nodes nd,
         ___CHANGESET_PREPARE___.planet_osm_nodes pn,
         copy_nodes cn
    WHERE pn.tags IS NOT NULL
      AND pn.id = nd.osm_id
      AND cn.id = pn.id
      AND cn.directlyAffected;

    INSERT INTO ___CHANGESET_PREPARE___.ways
    SELECT w.*
    FROM openstreetmap_geometries.ways w,
         ___CHANGESET_PREPARE___.planet_osm_ways pw,
         copy_ways cw
    WHERE pw.tags IS NOT NULL
      AND pw.id = w.osm_id
      AND cw.id = pw.id
      AND cw.directlyAffected;

    INSERT INTO ___CHANGESET_PREPARE___.areas
    SELECT a.*
    FROM openstreetmap_geometries.areas a,
         ___CHANGESET_PREPARE___.planet_osm_ways pw,
         copy_ways cw
    WHERE pw.tags IS NOT NULL
      AND pw.id = a.osm_id
      AND a.osm_geometry_type = 'W'
      AND cw.id = pw.id
      AND cw.directlyAffected;

    INSERT INTO ___CHANGESET_PREPARE___.areas
    SELECT a.*
    FROM openstreetmap_geometries.areas a,
         ___CHANGESET_PREPARE___.planet_osm_rels pr,
         copy_rels cr
    WHERE pr.tags IS NOT NULL
      AND pr.id = a.osm_id
      AND a.osm_geometry_type = 'R'
      AND cr.id = pr.id
      AND cr.directlyAffected;

    INSERT INTO ___CHANGESET_PREPARE___.relations
    SELECT r.*
    FROM openstreetmap_geometries.relations r,
         ___CHANGESET_PREPARE___.planet_osm_rels pr,
         copy_rels cr
    WHERE pr.tags IS NOT NULL
      AND pr.id = r.osm_id
      AND cr.id = pr.id
      AND cr.directlyAffected;

    INSERT INTO ___CHANGESET_PREPARE___.relation_members
    SELECT rm.*
    FROM openstreetmap_geometries.relation_members rm,
         ___CHANGESET_PREPARE___.planet_osm_rels pr,
         copy_rels cr
    WHERE pr.tags IS NOT NULL
      AND pr.id = rm.relation_osm_id
      AND cr.id = pr.id
      AND cr.directlyAffected;

    -- insert osm2pgsql properties
    INSERT INTO ___CHANGESET_PREPARE___.osm2pgsql_properties
    SELECT *
    FROM openstreetmap_geometries.osm2pgsql_properties;
END $$;