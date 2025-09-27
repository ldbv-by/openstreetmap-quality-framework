package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.util;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for serializing and deserializing {@link Changeset}.
 */
@UtilityClass
public class ChangesetXml {

    /**
     * Converts a {@link Changeset} to its XML representation as a String.
     */
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

    /**
     * Writes a {@link Changeset} as XML directly to the given OutputStream.
     */
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

    /**
     * Writes a {@link Changeset} as an XML file in the system temp directory.
     * Returns the path to the created file.
     */
    public Path toXmlFile(Changeset changeset) {
        try {
            Path baseTempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            Path changesetDir = baseTempDir.resolve("openstreetmap-quality-framework")
                                           .resolve("changesets");
            Files.createDirectories(changesetDir);
            Path file = Files.createTempFile(changesetDir,"changeset-" + changeset.getId() + "-", ".osc");

            try (OutputStream outputStream = Files.newOutputStream(file)) {
                toXml(changeset, outputStream);
            }

            return file;
        } catch (IOException e) {
            throw new IllegalStateException("XML creation file error for changeset", e);
        }
    }

    /**
     * Parses an XML string into a {@link ChangesetDto}.
     */
    public ChangesetDto fromXml(String xml) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(ChangesetDto.class);
            Unmarshaller um = ctx.createUnmarshaller();
            try (StringReader reader = new StringReader(xml)) {
                Object o = um.unmarshal(reader);
                return (ChangesetDto) o;
            }
        } catch (JAXBException e) {
            throw new IllegalStateException("XML parse error for changeset", e);
        }
    }
}
