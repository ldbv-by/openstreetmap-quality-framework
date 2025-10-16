INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.54001.A.b.003',
    'attribute-check',
    'AX_Vegetationsmerkmal',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "breiteDesObjekts" },
        "checks": {
            "all": [
                { "type": "tag_equals", "tag_key": "bewuchs", "value": "1300" },
                { "type": "geom_type", "value": "LineString" }
            ]
        }
    }',
    'Das Tag ''breiteDesObjekts'' darf nur bei der ''bewuchs'' 1300 und linienförmiger Modellierung vorkommen.')
ON CONFLICT (id) DO NOTHING;