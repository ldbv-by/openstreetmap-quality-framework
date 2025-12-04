package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity.id.RelationMemberId;
import jakarta.persistence.*;

@Entity(name = "RelationMembersChangesetData")
@Table(name = "relation_members", schema = "changeset_data")
public class RelationMemberEntity {
    @EmbeddedId
    private RelationMemberId memberId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "changeset_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_changeset_id")
    )
    private ChangesetEntity changeset;

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
