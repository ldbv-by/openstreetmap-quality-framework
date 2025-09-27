package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.util;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.model.Command;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Helper to generate Zip files.
 */
@UtilityClass
public class ZipHelper {

    public record Zip(
            Path path,
            String name,
            Resource resource
    ) {}

    /**
     * Creates a Zip File for a directory.
     */
    public Zip createZip(String fileName, Path directory) {
        try {
            Path zipFilePath = directory.resolve(fileName);

            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()));
                 Stream<Path> filePaths = Files.walk(directory)) {

                filePaths
                        .filter(Files::isRegularFile)
                        .filter(fp -> !fp.equals(zipFilePath))
                        .forEach(fp -> {
                            try {
                                Path relativePath = directory.relativize(fp);
                                zipOutputStream.putNextEntry(new ZipEntry(relativePath.toString()));
                                Files.copy(fp, zipOutputStream);
                                zipOutputStream.closeEntry();
                            } catch (IOException e) {
                                throw new IllegalStateException("Error on adding file to zip file.", e);
                            }
                        });
            }


            String zipFileName = zipFilePath.getFileName().toString();
            Resource zipFileResource = new InputStreamResource(
                    new ByteArrayInputStream(Files.readAllBytes(zipFilePath))
            );

            return new Zip(zipFilePath, zipFileName, zipFileResource);
        } catch (IOException e) {
            throw new IllegalStateException("Error on creating zip file.", e);
        }
    }

    /**
     * Creates a Zip File for command result.
     */
    public Zip createZipForCommandResult(Command command) {
        Path downloadPath = command.getDownloadFilesDirectory();

        if (Files.isDirectory(downloadPath)) {
            try (Stream<Path> files = Files.list(downloadPath)) {
                if (files.findAny().isPresent()) {
                    return createZip(command.getTool() + ".zip", downloadPath);
                }
            } catch (IOException e) {
                System.out.println("No command results found");
            }
        }

        return null;
    }

}
