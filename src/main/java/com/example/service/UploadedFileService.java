package com.example.service;

import com.example.entity.UploadedFile;

public interface UploadedFileService
{

	String addUploadedFile(UploadedFile uploadedFile);


	void deleteFile(String filePath);
}
