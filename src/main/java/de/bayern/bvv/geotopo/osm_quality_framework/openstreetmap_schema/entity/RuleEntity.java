package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.istack.NotNull;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Represents a rule for an object type.
 */
@Entity
@Table(name = "rules", schema = "openstreetmap_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleEntity {

    @Id
    @Column(name = "id")
    private String id;

    @NotNull
    @Column(name = "type")
    private String type; // e.g. attribute_check, overlay_check, ...

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "object_type",
            referencedColumnName = "object_type",
            foreignKey = @ForeignKey(name = "FK_rules_object_types")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ObjectTypeEntity objectType;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "expression")
    private JsonNode expression;

    @NotNull
    @Column(name = "error_text", length = 4000)
    private String errorText;
}
