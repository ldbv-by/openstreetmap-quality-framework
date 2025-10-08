INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.00001.A.a.002',
    'attribute-check',
    'AA_Objekt',
    '{
        "conditions" : { "type": "tag_exists", "tag_key": "lebenszeitintervall:endet" },
        "checks": { "type": "date_compare", "tag_key": "lebenszeitintervall:endet", "operator": ">", "compare_tag_key": "lebenszeitintervall:beginnt" }
    }',
    'Das Tag lebenszeitintervall:endet muss zeitlich nach dem Tag lebenszeitintervall:beginnt liegen.')
ON CONFLICT (id) DO NOTHING;
