INSERT INTO openstreetmap_schema.datatypes (datatype_id, datatype_type) VALUES
    ('OSM_admin_level', 'DICTIONARY')
ON CONFLICT (datatype_id) DO NOTHING;

INSERT INTO openstreetmap_schema.datatypes_dictionary (datatype_id, dictionary_key, dictionary_description) VALUES
    ('OSM_admin_level', '4', 'Grenze des Bundeslandes'),
    ('OSM_admin_level', '5', 'Grenze des Regierungsbezirks'),
    ('OSM_admin_level', '6', 'Grenze des Kreises / Kreisfreien Stadt / Region'),
    ('OSM_admin_level', '7', 'Grenze der Verwaltungsgemeinschaft'),
    ('OSM_admin_level', '8', 'Grenze der Gemeinde'),
    ('OSM_admin_level', '9', 'Grenze des Gemeindeteils'),
    ('OSM_admin_level', '10', 'Grenze eines Kondominiums')
ON CONFLICT (datatype_id, dictionary_key) DO NOTHING;

INSERT INTO openstreetmap_schema.tags (object_type, tag_key, multiplicity, tag_datatype_id)
-- object_type
SELECT ot.object_type, 'object_type', '1',    'CharacterString'
  FROM openstreetmap_schema.object_types ot
  UNION ALL

-- type
SELECT ot.object_type, 'type','0..1', 'CharacterString'
  FROM openstreetmap_schema.object_types ot
UNION ALL

-- admin_level
SELECT ot.object_type, 'admin_level','1', 'OSM_admin_level'
  FROM openstreetmap_schema.object_types ot
 WHERE ot.object_type in ('AX_KommunalesGebiet', 'AX_Gebiet_Bundesland', 'AX_Gebiet_Regierungsbezirk', 'AX_Gebiet_Kreis',
                          'AX_Gebiet', 'AX_Gebiet_Verwaltungsgemeinschaft', 'AX_KommunalesTeilgebiet', 'AX_Kondominium', 'AX_Gebietsgrenze')
UNION ALL

-- boundary
SELECT ot.object_type, 'boundary','1', 'CharacterString'
FROM openstreetmap_schema.object_types ot
WHERE ot.object_type in ('AX_KommunalesGebiet', 'AX_Gebiet_Bundesland', 'AX_Gebiet_Regierungsbezirk', 'AX_Gebiet_Kreis',
                         'AX_Gebiet', 'AX_Gebiet_Verwaltungsgemeinschaft', 'AX_KommunalesTeilgebiet', 'AX_Kondominium', 'AX_Gebietsgrenze')
ON CONFLICT (object_type, tag_key) DO NOTHING;

