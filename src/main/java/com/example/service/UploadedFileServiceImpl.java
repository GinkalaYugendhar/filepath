package com.example.service;

import com.example.entity.UploadedFile;
import com.example.repository.UploadedFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UploadedFileServiceImpl implements UploadedFileService {

	private static final Logger logger = LoggerFactory.getLogger(UploadedFileServiceImpl.class);

	@Autowired
	private UploadedFileRepository repo;

	@Override
	public String addUploadedFile(UploadedFile uploadedFile) {
		// Check if file with same filename or path already exists
		Optional<UploadedFile> existingByPath = repo.findByFilePath(uploadedFile.getFilePath());
		Optional<UploadedFile> existingByName = repo.findByFileName(uploadedFile.getFileName());

		if (existingByPath.isPresent() || existingByName.isPresent()) {
			String conflictPath = existingByPath.map(UploadedFile::getFilePath)
					.orElse(existingByName.get().getFilePath());
			logger.warn("Duplicate file detected: {} at {}", uploadedFile.getFileName(), conflictPath);
			return "File with name '" + uploadedFile.getFileName() + "' already exists at - " + conflictPath;
		}

		// Save new file
		UploadedFile saved = repo.save(uploadedFile);
		if (saved != null) {
			logger.info("File saved successfully: {}", saved.getFileName());
			return "UploadedFile Added Successfully";
		} else {
			logger.error("Failed to save file: {}", uploadedFile.getFileName());
			return "UploadedFile Not Added..!!!";
		}
	}

	@Override
	@Transactional
	public void deleteFile(String filePath) {
		logger.info("Deleting file at path: {}", filePath);
		repo.deleteByFilePath(filePath);
	}
}
