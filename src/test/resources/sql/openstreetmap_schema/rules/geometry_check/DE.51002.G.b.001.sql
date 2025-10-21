INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51002.G.b.001',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerIndustrieUndGewerbe',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1251" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Leitung" } } }
        }
    }',
    'Ein Freileitungsmast muss immer auf einer Leitung liegen.')
ON CONFLICT (id) DO NOTHING;