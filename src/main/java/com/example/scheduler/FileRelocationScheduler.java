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

    // Run every 1 minutes
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void relocateFiles() {
        List<UploadedFile> files = uploadedFileRepository.findAll();

        for (UploadedFile file : files) {
            String dbPath = file.getFilePath(); // Path from DB
            String fileName = file.getFileName();

            // Possible folders
            String userHome = System.getProperty("user.home");
            String[] folders = {"uploads", "uploads1", "uploads2"};

            File currentFile = null;

            // 1️⃣ Find the current file location
            for (String folder : folders) {
                File f = new File(userHome + File.separator + "Documents" + File.separator + folder + File.separator + fileName);
                if (f.exists()) {
                    currentFile = f;
                    break;
                }
            }

            // 2️⃣ Skip if file already in correct location
            if (currentFile != null && currentFile.getAbsolutePath().equals(dbPath)) {
                continue;
            }

            // 3️⃣ Move the file to the new path from DB
            if (currentFile != null) {
                try {
                    File targetFile = new File(dbPath);
                    File targetDir = targetFile.getParentFile();
                    if (!targetDir.exists()) targetDir.mkdirs();

                    Files.move(currentFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Moved file " + fileName + " to " + dbPath);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to move file: " + fileName);
                }
            }
        }
    }
}
