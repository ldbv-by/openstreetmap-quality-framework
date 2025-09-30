INSERT INTO openstreetmap_schema.object_types (object_type, is_abstract) VALUES
    ('AX_Wohnbauflaeche', false)
ON CONFLICT (object_type) DO NOTHING;
