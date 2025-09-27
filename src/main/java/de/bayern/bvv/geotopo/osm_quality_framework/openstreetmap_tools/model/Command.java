package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.model;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Base class for tool commands.
 */
@Data
public abstract class Command {
    private final String commandId = UUID.randomUUID().toString();
    private final String tool;
    private Path uploadFilesDirectory;
    private Path downloadFilesDirectory;
    private Path workingDirectory;

    private List<String> command = new ArrayList<>();
    private Map<String, String> environments;

    /**
     * Creates a command for a specific tool.
     */
    public Command(String tool, String executable, CommandRequest commandRequest) {
        this.tool = tool;
        this.environments = commandRequest.getEnvironments();
        this.setupWorkingDirectories(commandRequest);

        // Build command string
        this.command.add(executable);
        this.command.addAll(commandRequest.getArgs());

        for (Path original : commandRequest.getUploadPaths()) {
            String fileName = original.getFileName().toString();
            Path copiedPath = this.uploadFilesDirectory.resolve(fileName);

            for (int i = 0; i < this.command.size(); i++) {
                String arg = this.command.get(i);
                if (arg.contains(fileName)) {
                    this.command.set(i, arg.replace(fileName, copiedPath.toString()));
                }
            }
        }
    }

    /**
     * Creates working directories and saves uploaded files.
     */
    private void setupWorkingDirectories(CommandRequest commandRequest) {
        try {
            // Create upload directories and copy request files in upload folder
            this.uploadFilesDirectory = Path.of(System.getProperty("java.io.tmpdir"),
                    "openstreetmap-quality-framework", this.tool, this.commandId, "upload");
            Files.createDirectories(this.uploadFilesDirectory);

            if (!commandRequest.getUploadPaths().isEmpty()) {
                for (Path original : commandRequest.getUploadPaths()) {
                    String fileName = original.getFileName().toString();
                    Path copiedPath = this.uploadFilesDirectory.resolve(fileName);
                    Files.copy(original, copiedPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // Create download directories
            this.downloadFilesDirectory = Path.of(System.getProperty("java.io.tmpdir"), "openstreetmap-quality-framework", this.tool, this.commandId, "download");
            Path downloadFilesPath = Files.createDirectories(this.downloadFilesDirectory);

            this.setWorkingDirectory(downloadFilesPath);
        } catch (IOException e) {
            throw new IllegalStateException("Setup working directories failed", e);
        }
    }

    /**
     * Executes a command.
     */
    public String execute() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(this.command);
            processBuilder.directory(this.workingDirectory.toFile());
            processBuilder.environment().putAll(this.environments);

            Process process = processBuilder.start();

            String commandOutput = new String(process.getInputStream().readAllBytes());
            String commandErrorOutput = new String(process.getErrorStream().readAllBytes());

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IllegalStateException("Execute command failed with exit code " + exitCode + "\n" + commandErrorOutput);
            }

            return commandOutput + "\n" + commandErrorOutput;
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Execute command failed.\n" + e);
        }
    }
}
