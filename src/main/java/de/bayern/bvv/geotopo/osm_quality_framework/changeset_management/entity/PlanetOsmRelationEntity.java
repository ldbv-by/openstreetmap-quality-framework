package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.List;

@Entity(name = "PlanetRelsChangesetData")
@Table(name = "planet_osm_rels", schema = "changeset_data")
public class PlanetOsmRelationEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "members", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
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
