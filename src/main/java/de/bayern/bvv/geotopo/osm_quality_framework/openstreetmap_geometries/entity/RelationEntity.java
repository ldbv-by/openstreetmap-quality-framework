package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "RelationsOsmGeometry")
@Table(name = "relations", schema = "openstreetmap_geometries")
@Data
public class RelationEntity extends OsmObjectEntity {

    @Column(name = "members", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Member> members;

    @Data
    public static class Member {
        private String type;
        private Long ref;
        private String role;
    }
}
