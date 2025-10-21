INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53008.G.b.002',
    'geometry-check',
    'AX_EinrichtungenFuerDenSchiffsverkehr',
    '{
        "conditions": {
            "all": [
                { "type": "geom_type", "value": "LineString" },
                { "type": "tag_equals", "tag_key": "art", "value": "1460" }
            ]
        },
        "checks": {
            "any": [
                {
                    "type": "spatial_compare",
                    "operator": "touches",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Fliessgewaesser|AX_Hafenbecken|AX_StehendesGewaesser|AX_Meer" } } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "intersects",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_EinrichtungenFuerDenSchiffsverkehr", "art": "1460" } } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "intersects",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_BauwerkImGewaesserbereich", "art": "2133" } } }
                }
            ]
        }
    }',
    'Ein linienförmiger Anleger liegt immer auf der Umrissgeometrie eines Objekts ''AX_Fliessgewaesser'', ''AX_Hafenbecken'', ''AX_StehendesGewaesser'' oder ''AX_Meer'' oder berühren ''AX_BauwerkImGewaesserbereich'' mit ''bauwerksfunktion'' 2133 oder einen weiteren Anleger.')
ON CONFLICT (id) DO NOTHING;