package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.util;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.NodeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.RelationDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.WayDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class ChangesetXml {

    public String toXml(Changeset changeset) {
        ChangesetDto changesetDto = ChangesetMapper.toDto(changeset);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ChangesetDto.class);

            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            StringWriter sw = new StringWriter();
            marshaller.marshal(changesetDto, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new IllegalStateException("XML creation error for changeset", e);
        }
    }

    public void toXml(Changeset changeset, OutputStream outputStream) {
        ChangesetDto changesetDto = ChangesetMapper.toDto(changeset);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ChangesetDto.class);

            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(changesetDto, outputStream);
        } catch (JAXBException e) {
            throw new IllegalStateException("XML creation error for changeset", e);
        }
    }

    public Path toXmlFile(Changeset changeset) {
        try {
            Path file = Files.createTempFile("changeset-" + changeset.getId(), ".osc");
            if (file.getParent() != null) Files.createDirectories(file.getParent());

            try (OutputStream outputStream = Files.newOutputStream(file)) {
                toXml(changeset, outputStream);
            }

            return file;
        } catch (IOException e) {
            throw new IllegalStateException("XML creation file error for changeset", e);
        }
    }
}
