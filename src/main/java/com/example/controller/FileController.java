package com.example.controller;

import com.example.entity.UploadedFile;
import com.example.repository.UploadedFileRepository;
import com.example.service.UploadedFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,
        maxFileSize = 1024 * 1024 * 50,
        maxRequestSize = 1024 * 1024 * 100)
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private UploadedFileService service;

    @Autowired
    private UploadedFileRepository repo;

    @RequestMapping("showinsert")
    public String showInsert() {
        logger.info("Navigating to FileInsert page");
        return "FileInsert";
    }

    @RequestMapping(path = "uploadfile", method = RequestMethod.POST)
    public String takeFile(@RequestParam("file") MultipartFile multipartFile,
                           @RequestParam(value = "folder", required = false) String folder,
                           Model model) throws IOException {
        if (multipartFile.isEmpty()) {
            logger.warn("No file selected for upload");
            model.addAttribute("msg", "Please select a file to upload!");
            return "FileInsert";
        }

        String userHome = System.getProperty("user.home");

        if (folder == null || folder.isEmpty()) {
            folder = "uploads";
        }

        String uploadDir = userHome + File.separator + "Documents" + File.separator + folder;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            logger.info("Created upload directory: {} -> {}", uploadDir, created ? "SUCCESS" : "FAILED");
        }

        String filePath = uploadDir + File.separator + multipartFile.getOriginalFilename();
        logger.debug("Saving file to path: {}", filePath);

        // Save file metadata in DB
        UploadedFile f = new UploadedFile();
        f.setFileData(multipartFile.getBytes());
        f.setFileName(multipartFile.getOriginalFilename());
        f.setFilePath(filePath);
        String msg = service.addUploadedFile(f);
        logger.info("Database save result: {}", msg);

        // Save file physically
        multipartFile.transferTo(new File(filePath));
        logger.info("File saved physically at {}", filePath);

        model.addAttribute("msg", msg);
        model.addAttribute("filePath", msg.equals("UploadedFile Added Successfully") ? "" : "filePresent");

        return "FileInsert";
    }

    @RequestMapping(path = "relocatefile", method = RequestMethod.POST)
    public String relocateFile(@RequestParam("file") MultipartFile multipartFile,
                               @RequestParam("newpath") String newPath,
                               Model model) throws IOException {

        if (multipartFile.isEmpty()) {
            logger.warn("No file selected for relocation");
            model.addAttribute("msg", "Please select a file to upload!");
            return "FileInsert";
        }

        String fileName = multipartFile.getOriginalFilename();
        logger.debug("Relocating file: {} -> {}", fileName, newPath);

        Optional<UploadedFile> existingFileOpt = repo.findByFileName(fileName);

        if (existingFileOpt.isPresent()) {
            UploadedFile existingFile = existingFileOpt.get();
            File oldFile = new File(existingFile.getFilePath());

            if (oldFile.exists()) {
                if (oldFile.delete()) {
                    logger.info("Deleted old file from disk: {}", oldFile.getAbsolutePath());
                } else {
                    logger.error("Failed to delete old file from disk: {}", oldFile.getAbsolutePath());
                }
            }

            service.deleteFile(existingFile.getFilePath());
            logger.info("Deleted old file metadata from DB: {}", existingFile.getFilePath());
        } else {
            logger.warn("No existing file found in DB with name: {}", fileName);
        }

        // Save new file to DB
        UploadedFile f = new UploadedFile();
        f.setFileData(multipartFile.getBytes());
        f.setFileName(fileName);
        f.setFilePath(newPath);
        String msg = service.addUploadedFile(f);
        logger.info("Database relocation save result: {}", msg);

        // Save file physically
        File dest = new File(newPath);
        if (!dest.getParentFile().exists()) {
            boolean created = dest.getParentFile().mkdirs();
            logger.info("Created new directory for relocation: {} -> {}", dest.getParent(), created ? "SUCCESS" : "FAILED");
        }
        multipartFile.transferTo(dest);
        logger.info("File relocated physically to {}", dest.getAbsolutePath());

        model.addAttribute("msg", msg + " at " + newPath);
        model.addAttribute("filePath", msg.equals("UploadedFile Added Successfully") ? "" : "filePresent");

        return "FileInsert";
    }
}
