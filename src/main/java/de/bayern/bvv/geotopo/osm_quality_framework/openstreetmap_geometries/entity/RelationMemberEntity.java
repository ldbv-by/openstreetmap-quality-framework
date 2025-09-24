package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.id.RelationMemberId;
import jakarta.persistence.*;

@Entity
@Table(name = "relation_members", schema = "openstreetmap_geometries")
public class RelationMemberEntity {
    @EmbeddedId
    private RelationMemberId memberId;

    /*
    -- Add in osm2pgsql version 2.1.0. Add to lua create_indes='primary_key'
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("relationOsmId")
    @JoinColumn(
            name = "relation_osm_id",
            referencedColumnName = "osm_id",
            foreignKey = @ForeignKey(name = "fk_relation_members_relations")
    )
    private RelationEntity relation;
    */
}
