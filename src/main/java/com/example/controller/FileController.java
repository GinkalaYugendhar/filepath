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
		if (msg.equals("UploadedFile Added Successfully")) {
			model.addAttribute("filePath", ""); // empty string instead of "null"
		} else {
			model.addAttribute("filePath", filePath);
		}

		return "FileInsert";
	}
	@RequestMapping(path = "relocatefile", method = RequestMethod.POST)
	public String relocateFile(@RequestParam("file") MultipartFile multipartFile,
							   @RequestParam("newpath") String newPath, Model model) throws IOException {
		if (multipartFile.isEmpty()) {
			model.addAttribute("msg", "Please select a file to upload!");
			return "FileInsert";
		}

		System.out.println(newPath);

		// Store in Documents/uploads folder
		String userHome = System.getProperty("user.home");
		String uploadDir = userHome + java.io.File.separator + "Documents" + java.io.File.separator + "uploads";
		java.io.File dir = new java.io.File(uploadDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// Absolute path of the old file to delete
		String oldFilePath = uploadDir + java.io.File.separator + multipartFile.getOriginalFilename();

		// 1️⃣ Delete file from DB
		service.deleteFile(oldFilePath);

		// 2️⃣ Delete file from disk
		java.io.File oldFile = new java.io.File(oldFilePath);
		if (oldFile.exists()) {
			boolean deleted = oldFile.delete();
			if (!deleted) {
				System.out.println("Failed to delete file from disk: " + oldFilePath);
			}
		}

		// 3️⃣ Read file bytes before transfer
		byte[] fileBytes = multipartFile.getBytes();

		// 4️⃣ Save new file to DB
		UploadedFile f = new UploadedFile();
		f.setFileData(fileBytes);
		f.setFileName(multipartFile.getOriginalFilename());
		f.setFilePath(newPath);  // new absolute path
		String msg = service.addUploadedFile(f);

		// 5️⃣ Save new file to disk
		java.io.File dest = new java.io.File(newPath);
		multipartFile.transferTo(dest);

		model.addAttribute("msg", msg + newPath);
		if (msg.equals("UploadedFile Added Successfully")) {
			model.addAttribute("filePath", ""); // empty string instead of "null"
		} else {
			model.addAttribute("filePath", newPath);
		}

		return "FileInsert";
	}

}
