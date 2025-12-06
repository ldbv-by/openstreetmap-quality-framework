package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class WayNodeId implements Serializable {
    private Long nodeOsmId;
    private Long wayOsmId;
    private Long changesetId;
    private Integer seq;
}
