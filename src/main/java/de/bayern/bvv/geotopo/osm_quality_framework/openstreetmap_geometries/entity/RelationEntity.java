package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "RelationsOsmGeometry")
@Table(name = "relations", schema = "openstreetmap_geometries")
@Data
public class RelationEntity extends OsmObjectEntity {

    @Column(name = "members", columnDefinition = "jsonb")
    @Type(JsonType.class)
    private List<Member> members;

    @Data
    public static class Member {
        private String type;
        private Long ref;
        private String role;
    }
}
