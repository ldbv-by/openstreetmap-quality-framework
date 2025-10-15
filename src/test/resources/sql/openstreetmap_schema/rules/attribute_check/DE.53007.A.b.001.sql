INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53007.A.b.001',
    'attribute-check',
    'AX_Flugverkehrsanlage',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "breiteDesObjekts" },
        "checks": {
            "all": [
                { "type": "tag_in", "tag_key": "art", "values": ["1310", "1320"] },
                { "type": "geom_type", "value": "LineString" }
            ]
        }
    }',
    'Das Tag ''breiteDesObjekts'' darf nur bei der ''art'' 1310 und 1320 bei linienförmiger Modellierung vorkommen.')
ON CONFLICT (id) DO NOTHING;
