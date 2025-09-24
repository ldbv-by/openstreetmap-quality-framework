package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"k", "v"})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TagDto {
    @XmlAttribute(name = "k")
    private String k;

    @XmlAttribute(name = "v")
    private String v;
}
