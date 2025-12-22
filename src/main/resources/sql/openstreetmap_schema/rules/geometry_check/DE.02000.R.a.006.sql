INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.02000.R.a.006',
    'geometry-check',
    'AA_hatDirektUnten',
    '{
        "conditions": {
            "relation_members": {
                "role": "under",
                "checks": {
                    "all": [
                        { "type": "tag_in", "tag_key": "object_type", "values": ["AX_BauwerkImVerkehrsbereich", "AX_BauwerkImGewaesserbereich", "AX_DammWallDeich"] },
                        { "type": "geom_type", "value": "Polygon" }
                    ]
                }
            }
        },

        "checks": {
            "not": {
                "type": "spatial_compare",
                "reference_feature_role": "under",
                "operator": "overlaps",
                "data_set_filter": { "memberFilter": { "role": "over" } },
                "self_check": true
            }
        }
    }',
    'Objekt wird per hatDirektUnten referenziert, welches jedoch ganz oder teilweise außerhalb des Objekts liegt.')
ON CONFLICT (id) DO NOTHING;