package com.example.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.model.UploadedFile;
import com.example.repository.UploadedFileRepository;

@Repository
public class UploadedFileDaoImpl implements UploadedFileDao
{
	@Autowired
	private UploadedFileRepository repo;
	@Override
	public UploadedFile insertUploadedFile(UploadedFile uploadedFile)
	{
		return repo.save(uploadedFile);
	}
}
