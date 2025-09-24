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

import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "planet_osm_nodes", schema = "openstreetmap_geometries")
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
