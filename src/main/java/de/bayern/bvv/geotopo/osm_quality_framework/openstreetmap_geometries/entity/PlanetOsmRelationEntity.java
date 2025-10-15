package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity(name = "PlanetRelsOsmGeometry")
@Table(name = "planet_osm_rels", schema = "openstreetmap_geometries")
public class PlanetOsmRelationEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "members", columnDefinition = "jsonb")
    @Type(JsonType.class)
    private List<Member> members;

    @Column(name = "tags")
    @JdbcTypeCode(SqlTypes.JSON)
    private HashMap<String, String> tags;

    @Data
    public static class Member {
        private String type;
        private Long ref;
        private String role;
    }
}
