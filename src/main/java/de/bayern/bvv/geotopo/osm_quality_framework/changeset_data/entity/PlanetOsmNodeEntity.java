package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;

@Entity(name = "PlanetNodesChangesetData")
@Table(name = "planet_osm_nodes", schema = "changeset_data")
public class PlanetOsmNodeEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "lat")
    private Long lat;

    @Column(name = "lon")
    private Long lon;

    @Column(name = "tags")
    @JdbcTypeCode(SqlTypes.JSON)
    private HashMap<String, String> tags;
}
