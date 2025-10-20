INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53009.G.b.006',
    'geometry-check',
    'AX_BauwerkImGewaesserbereich',
    '{
        "conditions": {
            "all": [
                { "type": "geom_type", "value": "Point" },
                { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "2050" }
            ]
        },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_Gewaesserachse" } } }
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 2050 mus auf einem Objekt ''AX_Gewaesserachse'' liegen.')
ON CONFLICT (id) DO NOTHING;
