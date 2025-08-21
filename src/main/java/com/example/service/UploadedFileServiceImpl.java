package com.example.service;

import com.example.entity.UploadedFile;
import com.example.repository.UploadedFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UploadedFileServiceImpl implements UploadedFileService {

	@Autowired
	private UploadedFileRepository repo;

	@Override
	public String addUploadedFile(UploadedFile uploadedFile) {
		// Check if file with same filename already exists
		Optional<UploadedFile> existing = repo.findByFileName(uploadedFile.getFileName());
		if (existing.isPresent()) {
			return "File with name '" + uploadedFile.getFileName() + "' already exists.";
		}

		// Save new file
		UploadedFile saved = repo.save(uploadedFile);
		if (saved != null) {
			return "UploadedFile Added Successfully";
		} else {
			return "UploadedFile Not Added..!!!";
		}
	}
}
