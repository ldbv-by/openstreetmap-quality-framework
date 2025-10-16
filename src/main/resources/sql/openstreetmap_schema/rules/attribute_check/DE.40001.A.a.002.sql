INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.40001.A.a.002',
    'attribute-check',
    'AX_TatsaechlicheNutzung',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "ergebnisDerUeberpruefung" },
        "checks": { "type": "tag_exists", "tag_key": "datumDerLetztenUeberpruefung" }
    }',
    'Das Tag ''datumDerLetztenUeberpruefung'' muss belegt sein, sobald ''ergebnisDerUeberpruefung'' belegt ist.')
ON CONFLICT (id) DO NOTHING;
