/*
    INFO:

    Im Schema werden nur die Objektarten und Datentypen berücksichtigt, welche auch für die
    Ausleitung des ATKIS-Objektartenkatalogs Basis-DLM relevant sind.

    Angaben zu Geometrien werden nicht berücksichtigt, weil die Geometrie in OSM eigenständig geführt wird und
    somit nur für die Ausspielung von NAS Daten relevant wird, nicht aber bei der Erfassung.

    Alle Objektarten die von der abstrakten Objektart AA_ZUSO erben, werden als Relationen geführt.
    Das Attribut istTeilVon entfällt somit.

    Komplexe Datentypen, welche multiple sind, müssen als Relationen geführt werden, damit sie im OSM Tagging-Schema
    abgebildet werden können.
 */

INSERT INTO openstreetmap_schema.object_types (object_type, is_abstract, is_relation) VALUES
    ('AA_Objekt', true, false),
    ('AA_zeigtAufExternes', false, true), -- wird als Relation (eigener object_type) geführt
    ('AA_modellart', false, true), -- wird als Relation (eigener object_type) geführt
    /* ('AA_ObjektOhneRaumbezug', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AA_NREO', true, false),
    ('AA_REO', true, false),
    ('AA_istAbgeleitetAus', false, true), -- wird als Relation (eigener object_type) geführt
    ('AA_hatDirektUnten', false, true), -- wird als Relation (eigener object_type) geführt
    ('AA_ZUSO', true, true), -- AA_ZUSO wird als Relation geführt
    /* ('AA_PMO', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AG_Objekt', true, false),
    /* ('AG_Punktobjekt', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AG_Linienobjekt', true, false),
    ('AG_Flaechenobjekt', true, false),
    ('AA_Benutzergruppe', true, false),
    ('AA_Benutzer', true, false),
    /* ('AP_GPO', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_PPO', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_Punkthaufenobjekt', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_LPO', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AU_Linienobjekt', true, false),
    /* ('AP_FPO', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AU_Flaechenobjekt', true, false),
    /* ('AP_TPO', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_PTO', true, false),  -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AU_Punktobjekt', true, false),
    /* ('AP_LTO', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AU_KontinuierlichesLinienobjekt', true, false),
    /* ('AP_Darstellung', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_KPO_3D', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_Punktobjekt_3D', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Antrag', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Projektsteuerung', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Vorgang', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Meilenstein', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Aktivitaet', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Antragsgebiet', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_PunktCoverage', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_GitterCoverage', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_ReferenzierbaresGitter', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_Wertematrix', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AA_PunktLinienThema', true, false),
    /* ('TA_PointComponent', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('TA_CurveComponent', true, false),
    ('TA_SurfaceComponent', true, false),
    ('TA_MultiSurfaceComponent', true, false),
    ('AU_ObjektMitUnabhaengigerGeometrie', true, false),
    ('AG_ObjektMitGemeinsamerGeometrie', true, false),
    ('AG_thema', true, true), -- wird als Relation (eigener object_type) geführt
    ('AU_Objekt', true, false),
    ('AU_ObjektMitUnabhaengigerGeometrie_3D', true, false),
    ('AU_MehrfachFlaechenObjekt_3D', true, false)
    /* ('AU_GeometrieObjekt_3D', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_KoerperObjekt_3D', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_MehrfachLinienObjekt_3D', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_TrianguliertesOberflaechenObjekt_3D', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_UmringObjekt_3D', true, false), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_PunkthaufenObjekt_3D', true, false) -- wird nicht in ATKIS Basis-DLM benötigt */
ON CONFLICT (object_type) DO NOTHING;

INSERT INTO openstreetmap_schema.object_types_inheritance (object_type, extends_object_type) VALUES
    ('AA_REO', 'AA_Objekt'),
    ('AA_NREO', 'AA_Objekt'),
    ('AA_ZUSO', 'AA_Objekt'),
    /* ('AA_PMO', 'AA_Objekt'), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AG_Objekt', 'AG_ObjektMitGemeinsamerGeometrie'),
    /* ('AG_Punktobjekt', 'AG_ObjektMitGemeinsamerGeometrie'), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AG_Linienobjekt', 'AG_ObjektMitGemeinsamerGeometrie'),
    ('AG_Flaechenobjekt', 'AG_ObjektMitGemeinsamerGeometrie'),
    ('AA_Benutzergruppe', 'AA_NREO'),
    ('AA_Benutzer', 'AA_NREO'),
    /* ('AP_PPO', 'AP_GPO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_PPO', 'AU_Punkthaufenobjekt'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_Punkthaufenobjekt', 'AU_ObjektMitUnabhaengigerGeometrie'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_LPO', 'AP_GPO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_LPO', 'AU_Linienobjekt'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_FPO', 'AP_GPO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_FPO', 'AU_Flaechenobjekt'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_TPO', 'AP_GPO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_PTO', 'AP_TPO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_PTO', 'AU_Punktobjekt'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_LTO', 'AP_TPO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_LTO', 'AU_KontinuierlichesLinienobjekt'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_Darstellung', 'AA_NREO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_Darstellung', 'AP_GPO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_KPO_3D', 'AP_GPO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_KPO_3D', 'AU_Punktobjekt_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Antrag', 'AA_NREO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Projektsteuerung', 'AA_NREO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Vorgang', 'AA_NREO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Meilenstein', 'AA_NREO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Aktivitaet', 'AA_NREO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Antragsgebiet', 'AU_Flaechenobjekt'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_PunktCoverage', 'AA_PMO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_GitterCoverage', 'AA_PMO'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('TA_PointComponent', 'AG_ObjektMitGemeinsamerGeometrie'), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('TA_CurveComponent', 'AG_ObjektMitGemeinsamerGeometrie'),
    ('TA_SurfaceComponent', 'AG_ObjektMitGemeinsamerGeometrie'),
    ('TA_MultiSurfaceComponent', 'AG_ObjektMitGemeinsamerGeometrie'),
    ('AU_ObjektMitUnabhaengigerGeometrie', 'AA_REO'),
    ('AG_ObjektMitGemeinsamerGeometrie', 'AA_REO'),
    ('AU_Punktobjekt', 'AU_ObjektMitUnabhaengigerGeometrie'),
    ('AU_Objekt', 'AU_ObjektMitUnabhaengigerGeometrie'),
    ('AU_ObjektMitUnabhaengigerGeometrie_3D', 'AA_REO'),
    ('AU_MehrfachFlaechenObjekt_3D', 'AA_REO'),
    ('AU_Flaechenobjekt', 'AU_ObjektMitUnabhaengigerGeometrie')
    /* ('AU_GeometrieObjekt_3D', 'AU_ObjektMitUnabhaengigerGeometrie_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_KoerperObjekt_3D', 'AU_ObjektMitUnabhaengigerGeometrie_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_MehrfachLinienObjekt_3D', 'AU_ObjektMitUnabhaengigerGeometrie_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_TrianguliertesOberflaechenObjekt_3D', 'AU_ObjektMitUnabhaengigerGeometrie_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_UmringObjekt_3D', 'AU_ObjektMitUnabhaengigerGeometrie_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_Punktobjekt_3D', 'AU_ObjektMitUnabhaengigerGeometrie_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_PunkthaufenObjekt_3D', 'AU_ObjektMitUnabhaengigerGeometrie_3D') -- wird nicht in ATKIS Basis-DLM benötigt */
ON CONFLICT (object_type, extends_object_type) DO NOTHING;

INSERT INTO openstreetmap_schema.datatypes (datatype_id, datatype_type) VALUES
    /* ('AA_Fachdatenverbindung', 'COMPLEX'), -- wird als Relation (eigener object_type) geführt */
    ('CharacterString', 'PRIMITIVE'),
    ('AA_UUID', 'COMPLEX'),
    ('AA_Lebenszeitintervall', 'COMPLEX'),
    /* ('AA_Modellart', 'COMPLEX'), -- wird als Relation (eigener object_type) geführt */
    ('AA_Anlassart', 'DICTIONARY'),
    ('URI', 'PRIMITIVE'),
    ('AA_Fachdatenobjekt', 'COMPLEX'),
    ('DateTime', 'PRIMITIVE'),
    ('AA_AdVStandardModell', 'DICTIONARY'),
    ('AA_WeitereModellart', 'DICTIONARY'),
    /* ('GM_Envelope', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AG_Geometrie', 'COMPLEX'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('GM_PointRef', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('GM_CompositeCurve', 'PRIMITIVE'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AA_Flaechengeometrie', 'COMPLEX'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AA_Auftrag', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AA_Empfaenger', 'COMPLEX'),
    /* ('AA_NAS_Ausgabeform', 'DICTIONARY'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Benutzungsauftrag', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AA_Anlassart_Benutzungsauftrag', 'DICTIONARY'),
    ('SC_CRS', 'PRIMITIVE'),
    ('Query', 'PRIMITIVE'),
    /* ('AA_Fortfuehrungsauftrag', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Koordinatenreferenzsystemangaben', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Transaction', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Ergebnis', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('Boolean', 'PRIMITIVE'),
    /* ('AA_Bestandsdatenauszug', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Objektliste', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('FeatureCollection', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('Integer', 'PRIMITIVE'),
    /* ('AA_Fortfuehrungsergebnis', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Themendefinition', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Art_Themendefinition', 'DICTIONARY'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Themendimension', 'DICTIONARY'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('DCP', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('HTTP', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('SMTP', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('DataContents', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AC_FeatureCatalogue', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Operation', 'DICTIONARY'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('ExceptionFortfuehrung', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('GetCapabilities', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('ServiceMetadata', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Filter_Capabilities', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('NAS_Filter_Capabilities', 'DICTIONARY'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Angle', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('Real', 'PRIMITIVE'),
    /* ('AP_HorizontaleAusrichtung', 'DICTIONARY'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_VertikaleAusrichtung', 'DICTIONARY'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_DateiTyp_3D', 'DICTIONARY'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_TransformationsMatrix_3D', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Antragsart', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Projektsteuerungsart', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Gebuehrenangaben', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Gebuehrenparameter', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Projektsteuerungskatalog', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Vorgangsart', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_BesondereMeilensteinkategorie', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_VorgangInProzess', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Dokumentationsbedarf', 'DICTIONARY'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Aktivitaetsart', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_ProzesszuordnungAktivitaet', 'DICTIONARY'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_AktivitaetInVorgang', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_DurchfuehrungAktivitaet', 'DICTIONARY'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('GM_MultiPoint', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Sequence<Record>', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('CV_GridEnvelope', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('DirectPosition', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Sequence<vector>', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Sequence<CharacterString>', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('CV_SequenceRule', 'PRIMITIVE'),Customize Toolbar…
    /* ('CV_GridCoordinate', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Liniengeometrie', 'COMPLEX'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('GM_Curve', 'PRIMITIVE'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('GM_PolyhedralSurface', 'PRIMITIVE'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('GM_MultiSurface', 'PRIMITIVE'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    ('TS_Face', 'PRIMITIVE')
    /* ('AU_Geometrie', 'COMPLEX'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('GM_Point', 'PRIMITIVE'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('GM_MultiCurve', 'PRIMITIVE'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AA_Punktgeometrie', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_MehrfachFlaechenGeometrie_3D', 'COMPLEX'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AU_Geometrie_3D', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('GM_Solid', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_MehrfachLinienGeometrie_3D', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('GM_TriangulatedSurface', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('GM_SurfaceBoundary', 'PRIMITIVE'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Punktgeometrie_3D', 'COMPLEX'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('GM_OrientableSurface', 'PRIMITIVE') -- entspricht der Geometrie (bzw. den Nodes) in OSM */
ON CONFLICT (datatype_id) DO NOTHING;

-- INSERT INTO openstreetmap_schema.datatypes_inheritance (datatype_id, extends_datatype_id) VALUES
    /* ('AA_Benutzungsauftrag', 'AA_Auftrag'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Fortfuehrungsauftrag', 'AA_Auftrag'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Bestandsdatenauszug', 'AA_Ergebnis'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Bestandsdatenauszug', 'AA_Objektliste'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Fortfuehrungsergebnis', 'AA_Ergebnis') -- wird nicht in ATKIS Basis-DLM benötigt */
-- ON CONFLICT (datatype_id, extends_datatype_id) DO NOTHING;

INSERT INTO openstreetmap_schema.datatypes_complex (datatype_id, tag_key, multiplicity, tag_datatype_id) VALUES
    ('AA_UUID', 'UUID', '1', 'CharacterString'),
    ('AA_UUID', 'UUIDundZeit', '1', 'CharacterString'),
    /* ('AA_Fachdatenverbindung', 'art', '1', 'URI'), -- wird als Relation (eigener object_type) geführt */
    /* ('AA_Fachdatenverbindung', 'fachdatenobjekt', '1', 'AA_Fachdatenobjekt'), -- wird als Relation (eigener object_type) geführt */
    ('AA_Fachdatenobjekt', 'name', '1', 'CharacterString'),
    ('AA_Fachdatenobjekt', 'uri', '1', 'URI'),
    ('AA_Lebenszeitintervall', 'beginnt', '1', 'DateTime'),
    ('AA_Lebenszeitintervall', 'endet', '0..1', 'DateTime'),
    /* ('AA_Modellart', 'advStandardModell', '1', 'AA_AdVStandardModell'), -- wird als Relation (eigener object_type) geführt */
    /* ('AA_Modellart', 'sonstigesModell', '1', 'AA_WeitereModellart'), -- wird als Relation (eigener object_type) geführt */
    /*('AG_Geometrie', 'punkt', '1', 'GM_PointRef'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /*('AG_Geometrie', 'linie', '1', 'GM_CompositeCurve'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /*('AG_Geometrie', 'flaeche', '1', 'AA_Flaechengeometrie'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AA_Auftrag', 'empfaenger', '1', 'AA_Empfaenger'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Auftrag', 'ausgabeform', '1', 'AA_NAS_Ausgabeform'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Benutzungsauftrag', 'art', '1', 'AA_Anlassart_Benutzungsauftrag'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Benutzungsauftrag', 'koordinatenreferenzsystem', '0..1', 'SC_CRS'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Benutzungsauftrag', 'anforderungsmerkmale', '1.*', 'Query'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Fortfuehrungsauftrag', 'koordinatenangaben', '0.*', 'AA_Koordinatenreferenzsystemangaben'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Fortfuehrungsauftrag', 'geaenderteObjekte', '1', 'Transaction'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Ergebnis', 'erlaeuterung', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Ergebnis', 'erfolgreich', '1', 'Boolean'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Objektliste', 'koordinatenangaben', '0..*', 'AA_Koordinatenreferenzsystemangaben'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Objektliste', 'enthaelt', '0..1', 'FeatureCollection'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Koordinatenreferenzsystemangaben', 'crs', '1', 'SC_CRS'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Koordinatenreferenzsystemangaben', 'anzahlDerNachkommastellen', '1', 'Integer'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Koordinatenreferenzsystemangaben', 'standard', '1', 'Boolean'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Themendefinition', 'name', '1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Themendefinition', 'art', '1', 'AA_Art_Themendefinition'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Themendefinition', 'objektart', '1..*', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Themendefinition', 'modellart', '1', 'AA_Modellart'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Themendefinition', 'dimension', '1', 'AA_Themendimension'), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AA_Empfaenger', 'direkt', '1', 'Boolean'),
    ('AA_Empfaenger', 'email', '1', 'URI'),
    ('AA_Empfaenger', 'http', '1', 'URI'),
    ('AA_Empfaenger', 'manuell', '1', 'CharacterString')
    /* ('DCP', 'HTTP', '1', 'HTTP'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('DCP', 'email', '1', 'SMTP'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('DCP', 'manuell', '1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('DataContents', 'beginnDerHistorie', '0..1', 'DateTime'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('DataContents', 'featureCatalogue', '1', 'AC_FeatureCatalogue'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('DataContents', 'defaultSRS', '1', 'URI'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('DataContents', 'otherSRS', '0..*', 'URI'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('DataContents', 'operations', '0..*', 'Operation'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('ExceptionFortfuehrung', 'bereitsGesperrteObjekte', '0..*', 'AA_UUID'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('ExceptionFortfuehrung', 'nichtMehrAktuelleObjekte', '0..*', 'AA_UUID'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('GetCapabilities', 'service', '1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('GetCapabilities', 'profilkennung', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('ServiceMetadata', 'contents', '0..1', 'DataContents'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('ServiceMetadata', 'filterCapabilities', '0..1', 'Filter_Capabilities'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('ServiceMetadata', 'extendedFilterCapabilities', '0..*', 'NAS_Filter_Capabilities'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_TransformationsMatrix_3D', 'parameter', '1..*', 'Real'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Antragsart', 'name', '1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Projektsteuerungsart', 'name', '1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Projektsteuerungsart', 'definition', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Projektsteuerungsart', 'erlaubterFortfuehrungsanlass', '0..*', 'AA_Anlassart'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Projektsteuerungsart', 'gebietPflicht', '0..1', 'Boolean'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Projektsteuerungsart', 'gebuehren', '0..*', 'AA_Gebuehrenangaben'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Gebuehrenangaben', 'parameterArt', '1', 'AA_Gebuehrenparameter'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Gebuehrenangaben', 'parameterWert', '1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Vorgangsart', 'name', '1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Vorgangsart', 'definition', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Vorgangsart', 'zulaessigeBenutzergruppe', '0..*', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Vorgangsart', 'synchronisiert', '1', 'Boolean'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_VorgangInProzess', 'dokumentation', '0..1', 'AA_Dokumentationsbedarf'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_VorgangInProzess', 'optional', '0..1', 'Boolean'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_VorgangInProzess', 'erlaeuterung', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Aktivitaetsart', 'name', '1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Aktivitaetsart', 'definition', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Aktivitaetsart', 'reihenfolge', '0..1', 'Integer'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Aktivitaetsart', 'zuordnung', '1', 'AA_ProzesszuordnungAktivitaet'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_AktivitaetInVorgang', 'erlaeuterung', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_AktivitaetInVorgang', 'durchfuehrung', '0..1', 'AA_DurchfuehrungAktivitaet'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Liniengeometrie', 'linie', '1', 'GM_Curve'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AA_Liniengeometrie', 'zusammengesetzteLinie', '1', 'GM_CompositeCurve'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AA_Flaechengeometrie', 'flaeche', '1', 'GM_PolyhedralSurface'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AA_Flaechengeometrie', 'getrennteFlaechen', '1', 'GM_MultiSurface'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AU_Geometrie', 'punkt', '1', 'GM_Point'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AU_Geometrie', 'zusammengesetzteLinie', '1', 'AA_Liniengeometrie'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AU_Geometrie', 'linie', '1', 'GM_MultiCurve'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AU_Geometrie', 'flaeche', '1', 'AA_Flaechengeometrie'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AA_Punktgeometrie', 'punkt', '1', 'GM_Point'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Punktgeometrie', 'punkthaufen', '1', 'GM_MultiPoint'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_MehrfachFlaechenGeometrie_3D', 'mehrfachFlaeche', '1', 'GM_MultiSurface'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AA_MehrfachFlaechenGeometrie_3D', 'flaeche', '1', 'GM_OrientableSurface') -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AA_MehrfachLinienGeometrie_3D', 'linie', '1', 'GM_Curve'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_MehrfachLinienGeometrie_3D', 'mehrfachLinie', '1', 'GM_MultiCurve'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Punktgeometrie_3D', 'punkt', '1', 'GM_Point'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Punktgeometrie_3D', 'punkthaufen', '1', 'GM_MultiPoint') -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_Geometrie_3D', 'koerper', '1', 'GM_Solid'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_Geometrie_3D', 'mehrfachLinie', '1', 'AA_MehrfachLinienGeometrie_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_Geometrie_3D', 'mehrfachFlaeche', '1', 'AA_MehrfachFlaechenGeometrie_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_Geometrie_3D', 'mehrfachPunkt', '1', 'AA_Punktgeometrie_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_Geometrie_3D', 'umring', '1', 'GM_SurfaceBoundary'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_Geometrie_3D', 'tin', '1', 'GM_TriangulatedSurface') -- wird nicht in ATKIS Basis-DLM benötigt */
ON CONFLICT (datatype_id, tag_key) DO NOTHING;

INSERT INTO openstreetmap_schema.datatypes_dictionary (datatype_id, dictionary_key, dictionary_description) VALUES
    ('AA_AdVStandardModell', 'DLKM', 'LiegenschaftskatasterModell'),
    ('AA_AdVStandardModell', 'DKKM500', 'KatasterkartenModell500'),
    ('AA_AdVStandardModell', 'DKKM1000', 'KatasterkartenModell1000'),
    ('AA_AdVStandardModell', 'DKKM2000', 'KatasterkartenModell2000'),
    ('AA_AdVStandardModell', 'DKKM5000', 'KatasterkartenModell5000'),
    ('AA_AdVStandardModell', 'Basis-DLM', 'BasisLandschaftsModell'),
    ('AA_AdVStandardModell', 'DLM50', 'LandschaftsModell50'),
    ('AA_AdVStandardModell', 'DLM250', 'LandschaftsModell250'),
    ('AA_AdVStandardModell', 'DLM1000', 'LandschaftsModell1000'),
    ('AA_AdVStandardModell', 'DTK10', 'TopographischeKarte10'),
    ('AA_AdVStandardModell', 'DTK25', 'TopographischeKarte25'),
    ('AA_AdVStandardModell', 'DTK50', 'TopographischeKarte50'),
    ('AA_AdVStandardModell', 'DTK100', 'TopographischeKarte100'),
    ('AA_AdVStandardModell', 'DTK250', 'TopographischeKarte250'),
    ('AA_AdVStandardModell', 'DTK1000', 'TopographischeKarte1000'),
    ('AA_AdVStandardModell', 'DFGM', 'Festpunktmodell'),
    ('AA_AdVStandardModell', 'DHM', 'DigitalesHoehenmodell'),
    ('AA_AdVStandardModell', 'LoD1', 'LevelOfDetail1'),
    ('AA_AdVStandardModell', 'LoD2', 'LevelOfDetail2'),
    ('AA_AdVStandardModell', 'LoD3', 'LevelOfDetail3'),
    ('AA_AdVStandardModell', 'GeoBasis-DE', 'LandbedeckungLandnutzung'),
    ('AA_AdVStandardModell', 'GVM', 'GeometrischesVerbesserungsModell'),
    ('AA_AdVStandardModell', 'BRM', 'Bodenrichtwertemodell'),
    ('AA_WeitereModellart', 'DTK10A', 'DigitaleTopographischeKarte10AKG'),
    ('AA_WeitereModellart', 'DTK25A', 'DigitaleTopographischeKarte25AKG'),
    ('AA_WeitereModellart', 'DTK50A', 'DigitaleTopographischeKarte50AKG'),
    ('AA_WeitereModellart', 'DTK100A', 'DigitaleTopographischeKarte100AKG'),
    ('AA_WeitereModellart', 'TFIS25', 'TopographischesFreizeitInformationsSystem25'),
    ('AA_WeitereModellart', 'TFIS50', 'TopographischesFreizeitInformationsSystem50'),
    /* ('AA_NAS_Ausgabeform', 'application/xml', 'application/xml'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_NAS_Ausgabeform', 'application/zip', 'application/zip'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_NAS_Ausgabeform', 'application/gzip', 'application/gzip'), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AA_Anlassart_Benutzungsauftrag', '0010', 'Bestandsdatenauszug'),
    /* ('AA_Art_Themendefinition', '1000', 'alleObjekte'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Themendimension', '1000', 'Punkt-Linien-Thema (Dimension 1)'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Themendimension', '1000', 'Topologiethema (Dimension 2)'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Operation', 'Insert', 'Insert'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Operation', 'Replace', 'Replace'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Operation', 'Delete', 'Delete'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Operation', 'Query', 'Query'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Operation', 'Lock', 'Lock'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Operation', 'Unlock', 'Unlock'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('Operation', 'Reserve', 'Reserve'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('NAS_Filter_Capabilities', 'transparentXlinks', 'transparentXlinks'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('NAS_Filter_Capabilities', 'multiplePropertyValues', 'multiplePropertyValues'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('NAS_Filter_Capabilities', 'PropertyName', 'PropertyName'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('NAS_Filter_Capabilities', 'XlinkPropertyPath_leafOnly', 'XlinkPropertyPath_leafOnly'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('NAS_Filter_Capabilities', 'PropertyIsOfType', 'PropertyIsOfType'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_HorizontaleAusrichtung', 'linksbündig', 'linksbündig'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_HorizontaleAusrichtung', 'rechtsbündig', 'rechtsbündig'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_HorizontaleAusrichtung', 'zentrisch', 'zentrisch'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_VertikaleAusrichtung', 'Basis', 'Basis'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_VertikaleAusrichtung', 'Mitte', 'Mitte'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_VertikaleAusrichtung', 'oben', 'oben'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_DateiTyp_3D', '1000', 'CityGML'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_DateiTyp_3D', '2000', 'VRML'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_DateiTyp_3D', '3000', 'kml'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_DateiTyp_3D', '4000', 'X3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_DateiTyp_3D', '5000', 'COLLADA'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_DateiTyp_3D', '9990', 'unbekannt'), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AA_Anlassart', '200100', 'Eintragen eines Gebäudes'),
    ('AA_Anlassart', '000000', 'Ersteinrichtung'),
    ('AA_Anlassart', '200300', 'Löschen eines Gebäudes'),
    ('AA_Anlassart', '300501', 'Veränderung aufgrund der Kartenanpassung'),
    ('AA_Anlassart', '300500', 'Veränderung aufgrund der Homogenisierung'),
    ('AA_Anlassart', '200200', 'Veränderung der Gebäudeeigenschaften'),
    ('AA_Anlassart', '300900', 'Veränderung der Geometrie durch Implizitbehandlung'),
    ('AA_Anlassart', '300300', 'Veränderung der tatsächlichen Nutzung'),
    ('AA_Anlassart', '300200', 'Veränderung von Bauwerken, Einrichtungen und sonstigen Angaben'),
    ('AA_Anlassart', '200000', 'Veränderung von Gebäudedaten')
    /* ('AA_Dokumentationsbedarf', '1000', 'Ja'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Dokumentationsbedarf', '2000', 'Nein'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Dokumentationsbedarf', '3000', 'Verminderte Dokumentation'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_ProzesszuordnungAktivitaet', '1000', 'Erhebung'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_ProzesszuordnungAktivitaet', '2000', 'Qualifizierung'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_ProzesszuordnungAktivitaet', '3000', 'Prozesskommunikation') -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_DurchfuehrungAktivitaet', '1000', 'erforderlich'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_DurchfuehrungAktivitaet', '2000', 'nicht möglich'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_DurchfuehrungAktivitaet', '3000', 'optional') -- wird nicht in ATKIS Basis-DLM benötigt */
ON CONFLICT (datatype_id, dictionary_key) DO NOTHING;

INSERT INTO openstreetmap_schema.tags (object_type, tag_key, multiplicity, tag_datatype_id) VALUES
    /*('AA_Objekt', 'zeigtAufExternes', '0..*', 'AA_Fachdatenverbindung'), -- wird als Relation (eigener object_type) geführt */
    ('AA_Objekt', 'quellobjektID', '0..1', 'CharacterString'),
    ('AA_Objekt', 'identifikator', '1', 'AA_UUID'),
    ('AA_Objekt', 'lebenszeitintervall', '1', 'AA_Lebenszeitintervall'),
    /*('AA_Objekt', 'modellart', '1..*', 'AA_Modellart'), -- wird als Relation (eigener object_type) geführt */
    ('AA_Objekt', 'anlass', '0..2', 'AA_Anlassart'),
    ('AA_zeigtAufExternes', 'art', '1', 'URI'),
    ('AA_zeigtAufExternes', 'fachdatenobjekt', '1', 'AA_Fachdatenobjekt'),
    ('AA_modellart', 'advStandardModell', '1', 'AA_AdVStandardModell'),
    ('AA_modellart', 'sonstigesModell', '0..1', 'AA_WeitereModellart'), -- Multiplicity angepasst, sonstigesModell wird in den Daten nicht geführt
    /* ('AA_PMO', 'name', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_PMO', 'beschreibung', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_PMO', 'ausdehnung', '1', 'GM_Envelope'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AG_Objekt', 'position', '1', 'AG_Geometrie'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AG_Punktobjekt', 'position', '1', 'GM_PointRef'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AG_Linienobjekt', 'position', '1', 'GM_CompositeCurve'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AG_Flaechenobjekt', 'position', '1', 'AA_Flaechengeometrie'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AP_GPO', 'signaturnummer', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_GPO', 'darstellungsprioritaet', '0..1', 'Integer'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_GPO', 'art', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_PPO', 'drehwinkel', '0..1', 'Angle'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_PPO', 'skalierung', '0..1', 'Real'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_TPO', 'schriftinhalt', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_TPO', 'fontSperrung', '1', 'Real'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_TPO', 'skalierung', '1', 'Real'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_TPO', 'horizontaleAusrichtung', '1', 'AP_HorizontaleAusrichtung'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_TPO', 'vertikaleAusrichtung', '1', 'AP_VertikaleAusrichtung'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_PTO', 'drehwinkel', '0..1', 'Angle'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_Darstellung', 'positionierungsregel', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_KPO_3D', 'dateiTyp', '1', 'AP_DateiTyp_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_KPO_3D', 'referenzZumFremdobjekt', '1', 'URI'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AP_KPO_3D', 'transformationsMatrix', '0..1', 'AP_TransformationsMatrix_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Antrag', 'kennzeichen', '1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Antrag', 'antragUnterbrochen', '1', 'Boolean'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Antrag', 'erlaeuterungZumStatus', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Vorgang', 'erlaeuterung', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Meilenstein', 'begonnen', '0..1', 'Boolean'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Meilenstein', 'abgeschlossen', '0..1', 'Boolean'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Meilenstein', 'erfolgreich', '0..1', 'Boolean'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Meilenstein', 'wannAbgeschlossen', '0..1', 'DateTime'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Meilenstein', 'kategorie', '0..1', 'AA_BesondereMeilensteinkategorie'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Meilenstein', 'bemerkung', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AA_Aktivitaet', 'erlaeuterung', '0..1', 'CharacterString'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_PunktCoverage', 'geometrie', '1', 'GM_MultiPoint'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_PunktCoverage', 'werte', '1', 'Sequence<Record>'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_ReferenzierbaresGitter', 'anzahlZeilenSpalten', '1', 'CV_GridEnvelope'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_ReferenzierbaresGitter', 'ursprung', '1', 'DirectPosition'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_ReferenzierbaresGitter', 'offsetVektoren', '1', 'Sequence<vector>'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_ReferenzierbaresGitter', 'achsenNamen', '1', 'Sequence<CharacterString>'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_Wertematrix', 'werte', '1', 'Sequence<Record>'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_Wertematrix', 'werteReihenfolge', '0..1', 'CV_SequenceRule'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AD_Wertematrix', 'startPunkt', '0..1', 'CV_GridCoordinate'), -- wird nicht in ATKIS Basis-DLM benötigt */
    ('AA_PunktLinienThema', 'name', '1', 'CharacterString'),
    ('TA_MultiSurfaceComponent', 'masche', '1..*', 'TS_Face')
    /* ('AU_Punktobjekt', 'position', '1', 'GM_Point'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AU_Linienobjekt', 'position', '1', 'GM_MultiCurve'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AU_KontinuierlichesLinienobjekt', 'position', '1', 'AA_Liniengeometrie'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AU_Flaechenobjekt', 'position', '1', 'AA_Flaechengeometrie'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AU_Punkthaufenobjekt', 'position', '1', 'AA_Punktgeometrie'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_MehrfachFlaechenObjekt_3D', 'position', '1', 'AA_MehrfachFlaechenGeometrie_3D'), -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AU_Objekt', 'position', '1', 'AU_Geometrie') -- entspricht der Geometrie (bzw. den Nodes) in OSM */
    /* ('AU_GeometrieObjekt_3D', 'position', '1', 'AU_Geometrie_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_KoerperObjekt_3D', 'position', '1', 'GM_Solid'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_MehrfachLinienObjekt_3D', 'position', '1', 'AA_MehrfachLinienGeometrie_3D'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_TrianguliertesOberflaechenObjekt_3D', 'position', '1', 'GM_TriangulatedSurface'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_UmringObjekt_3D', 'position', '1', 'GM_SurfaceBoundary'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_Punktobjekt_3D', 'position', '1', 'GM_Point'), -- wird nicht in ATKIS Basis-DLM benötigt */
    /* ('AU_PunkthaufenObjekt_3D', 'position', '1', 'AA_Punktgeometrie_3D') -- wird nicht in ATKIS Basis-DLM benötigt */
ON CONFLICT (object_type, tag_key) DO NOTHING;

INSERT INTO openstreetmap_schema.relations (object_type, relation_object_type, multiplicity) VALUES
    ('AA_Objekt', 'AA_zeigtAufExternes', '0..*'),
    ('AA_Objekt', 'AA_modellart', '1..*'),
    ('AA_REO', 'AA_istAbgeleitetAus', '0..*'),
    ('AA_REO', 'AA_hatDirektUnten', '0..*'),
    ('AG_ObjektMitGemeinsamerGeometrie', 'AG_thema', '0..*')
ON CONFLICT (object_type, relation_object_type) DO NOTHING;

INSERT INTO openstreetmap_schema.relation_members (object_type, relation_object_type, type, role, multiplicity) VALUES
    ('AA_Objekt', 'AA_zeigtAufExternes', '*', '', '1'),
    ('AA_Objekt', 'AA_modellart', '*', '', '1'),
    ('AA_REO', 'AA_istAbgeleitetAus', '*', '', '1'),
    ('AA_REO', 'AA_istAbgeleitetAus', '*', 'traegtBeiZu', '1..*'),
    ('AA_REO', 'AA_hatDirektUnten', '*', 'over', '1'),
    ('AA_REO', 'AA_hatDirektUnten', '*', 'under', '1..*'),
    ('AG_ObjektMitGemeinsamerGeometrie', 'AG_thema', '*', '', '1'),
    ('AG_ObjektMitGemeinsamerGeometrie', 'AG_thema', '*', 'element', '1..*')
ON CONFLICT (object_type, relation_object_type, role) DO NOTHING;

