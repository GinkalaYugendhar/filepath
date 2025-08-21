package com.example.controller;

import java.io.IOException;

import javax.servlet.annotation.MultipartConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.entity.UploadedFile;
import com.example.service.UploadedFileService;

@Controller
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, maxFileSize = 1024 * 1024 * 50, maxRequestSize = 1024 * 1024 * 100)
public class FileController
{
	@Autowired
	private UploadedFileService service;

	@RequestMapping("showinsert")
	public String showInsert()
	{
		return "FileInsert";
	}

	@RequestMapping(path = "uploadfile", method = RequestMethod.POST)
	public String takeFile(@RequestParam("file") MultipartFile multipartFile, Model model) throws IOException {
	    if (multipartFile.isEmpty()) {
	        model.addAttribute("msg", "Please select a file to upload!");
	        return "FileInsert";
	    }

	    // ✅ Always store in Documents/uploads folder
	    String userHome = System.getProperty("user.home"); 
	    String uploadDir = userHome + java.io.File.separator + "Documents" + java.io.File.separator + "uploads";
	    java.io.File dir = new java.io.File(uploadDir);
	    if (!dir.exists()) {
	        dir.mkdirs();
	    }

	    // ✅ Absolute path where file will be stored
	    String filePath = uploadDir + java.io.File.separator + multipartFile.getOriginalFilename();

	    // ✅ 1) Read file bytes before transfer
	    byte[] fileBytes = multipartFile.getBytes();

	    // ✅ 2) Save to DB
	    UploadedFile f = new UploadedFile();
	    f.setFileData(fileBytes); // binary data
	    f.setFileName(multipartFile.getOriginalFilename());
	    f.setFilePath(filePath);  // absolute path
	    String msg = service.addUploadedFile(f);

	    // ✅ 3) Save to disk
	    java.io.File dest = new java.io.File(filePath);
	    multipartFile.transferTo(dest);

	    model.addAttribute("msg", msg + filePath);
	    return "FileInsert";
	}



}
