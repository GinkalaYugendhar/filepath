package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dao.UploadedFileDao;
import com.example.model.UploadedFile;

@Service
public class UploadedFileServiceImpl implements UploadedFileService
{
	@Autowired
	private UploadedFileDao dao;
	@Override
	public String addUploadedFile(UploadedFile uploadedFile)
	{
		UploadedFile uploadedFile2=dao.insertUploadedFile(uploadedFile);
		if (uploadedFile2!=null)
		{
			return "UploadedFile Added";
		}
		else
		{
			return "UploadedFile Not added..!!!";
		}
	}
	

}
