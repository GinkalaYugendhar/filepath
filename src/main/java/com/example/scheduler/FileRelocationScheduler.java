package com.example.scheduler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.UploadedFile;
import com.example.repository.UploadedFileRepository;
@Component
public class FileRelocationScheduler {

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
                    System.out.println("File not found on disk: " + fileName);
                    continue;
                }

                File dbFile = new File(dbPath);

                // Skip if already in correct location
                if (currentFile.getCanonicalPath().equals(dbFile.getCanonicalPath())) {
                    continue;
                }

                // Ensure target folder exists
                if (!dbFile.getParentFile().exists()) dbFile.getParentFile().mkdirs();

                // Move the file
                Files.move(currentFile.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Moved file " + fileName + " to " + dbPath);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to move file: " + fileName);
            }
        }
    }
}
