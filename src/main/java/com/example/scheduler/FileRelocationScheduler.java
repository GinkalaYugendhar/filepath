package com.example.scheduler;

import com.example.entity.UploadedFile;
import com.example.repository.UploadedFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Component
public class FileRelocationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(FileRelocationScheduler.class);

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    // Run every 1 minute
    @Scheduled(fixedRate = 60000)
    public void relocateFiles() {
        List<UploadedFile> files = uploadedFileRepository.findAll();
        String userHome = System.getProperty("user.home");
        String[] folders = {"uploads", "uploads1", "uploads2"};

        for (UploadedFile file : files) {
            String dbPath = file.getFilePath();
            String fileName = file.getFileName();

            File currentFile = null;

            // Find the current file location
            for (String folder : folders) {
                File f = new File(userHome + File.separator + "Documents" + File.separator + folder + File.separator + fileName);
                if (f.exists()) {
                    currentFile = f;
                    break;
                }
            }

            try {
                if (currentFile == null) {
                    logger.warn("File not found on disk: {}", fileName);
                    continue;
                }

                File dbFile = new File(dbPath);

                // Skip if already in correct location
                if (currentFile.getCanonicalPath().equals(dbFile.getCanonicalPath())) {
                    logger.debug("File {} already in correct location: {}", fileName, dbPath);
                    continue;
                }

                // Ensure target folder exists
                if (!dbFile.getParentFile().exists()) {
                    boolean created = dbFile.getParentFile().mkdirs();
                    if (created) {
                        logger.info("Created missing target directory: {}", dbFile.getParentFile().getAbsolutePath());
                    }
                }

                // Move the file
                Files.move(currentFile.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info("Moved file {} to {}", fileName, dbPath);

            } catch (Exception e) {
                logger.error("Failed to move file: {}", fileName, e);
            }
        }
    }
}
