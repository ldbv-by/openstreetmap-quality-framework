INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42015.F.b.003',
    'attribute-check',
    'AX_Flugverkehr',
    '{
        "conditions": { "not": { "type": "tag_equals", "tag_key": "funktion", "value": "1200" } },
        "checks": {
            "all": [
                { "type": "tag_exists", "tag_key": "name:unverschluesselt" },
                { "type": "tag_exists", "tag_key": "name:verschluesselt:land" },
                { "type": "tag_exists", "tag_key": "name:verschluesselt:kreis" },
                { "type": "tag_exists", "tag_key": "name:verschluesselt:gemeinde" },
                { "type": "tag_exists", "tag_key": "name:verschluesselt:lage" }
            ]
        }
    }',
    'Das komplexe Tag ''name'' muss belegt sein, außer bei der ''funktion'' 1200.')
ON CONFLICT (id) DO NOTHING;
