package com.example.controller;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.servlet.annotation.MultipartConfig;

import com.example.repository.UploadedFileRepository;
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

	@Autowired
	private UploadedFileRepository repo;

	@RequestMapping("showinsert")
	public String showInsert()
	{
		return "FileInsert";
	}

	@RequestMapping(path = "uploadfile", method = RequestMethod.POST)
	public String takeFile(@RequestParam("file") MultipartFile multipartFile,
						   @RequestParam(value="folder", required=false) String folder,
						   Model model) throws IOException {
		if (multipartFile.isEmpty()) {
			model.addAttribute("msg", "Please select a file to upload!");
			return "FileInsert";
		}

		String userHome = System.getProperty("user.home");

		// Use provided folder or default "uploads"
		if (folder == null || folder.isEmpty()) {
			folder = "uploads";
		}

		String uploadDir = userHome + File.separator + "Documents" + File.separator + folder;
		File dir = new File(uploadDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String filePath = uploadDir + File.separator + multipartFile.getOriginalFilename();

		// Save file bytes in DB
		UploadedFile f = new UploadedFile();
		f.setFileData(multipartFile.getBytes());
		f.setFileName(multipartFile.getOriginalFilename());
		f.setFilePath(filePath);
		String msg = service.addUploadedFile(f);

		// Save physical file
		multipartFile.transferTo(new File(filePath));

		model.addAttribute("msg", msg);
		model.addAttribute("filePath", msg.equals("UploadedFile Added Successfully") ? "" : "filePresent");

		return "FileInsert";
	}

	@RequestMapping(path = "relocatefile", method = RequestMethod.POST)
	public String relocateFile(@RequestParam("file") MultipartFile multipartFile,
							   @RequestParam("newpath") String newPath, Model model) throws IOException {

		if (multipartFile.isEmpty()) {
			model.addAttribute("msg", "Please select a file to upload!");
			return "FileInsert";
		}

		String fileName = multipartFile.getOriginalFilename();

		// 1️⃣ Find existing file by name in DB
		Optional<UploadedFile> existingFileOpt = repo.findByFileName(fileName);

		if (existingFileOpt.isPresent()) {
			UploadedFile existingFile = existingFileOpt.get();

			// 2️⃣ Delete old file from disk
			File oldFile = new File(existingFile.getFilePath());
			if (oldFile.exists() && !oldFile.delete()) {
				System.out.println("Failed to delete old file: " + oldFile.getAbsolutePath());
			}

			// 3️⃣ Delete old file record from DB
			service.deleteFile(existingFile.getFilePath());
		}

		// 4️⃣ Save new file to DB
		UploadedFile f = new UploadedFile();
		f.setFileData(multipartFile.getBytes());
		f.setFileName(fileName);
		f.setFilePath(newPath);  // new absolute path
		String msg = service.addUploadedFile(f);

		// 5️⃣ Save file to new path on disk
		File dest = new File(newPath);
		if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
		multipartFile.transferTo(dest);

		model.addAttribute("msg", msg + " at " + newPath);
		model.addAttribute("filePath", msg.equals("UploadedFile Added Successfully") ? "" : "filePresent");

		return "FileInsert";
	}


}
