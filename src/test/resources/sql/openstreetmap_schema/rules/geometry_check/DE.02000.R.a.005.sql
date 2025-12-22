INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.02000.R.a.005',
    'geometry-check',
    'AA_hatDirektUnten',
    '{
        "conditions": {
            "relation_members": {
                "role": "under",
                "checks": {
                    "all": [
                        { "type": "tag_in", "tag_key": "object_type", "values": ["AX_BauwerkImVerkehrsbereich", "AX_BauwerkImGewaesserbereich", "AX_DammWallDeich"] },
                        { "type": "geom_type", "value": "LineString" }
                    ]
                }
            }
        },

        "checks": {
            "type": "spatial_compare",
            "reference_feature_role": "under",
            "operator": "equals_topo",
            "data_set_filter": { "aggregator": "union", "memberFilter": { "role": "over" } },
            "self_check": true
        }
    }',
    'HDU hat keine Geometrieidentität.')
ON CONFLICT (id) DO NOTHING;