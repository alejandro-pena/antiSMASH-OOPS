package uk.ac.mib.antismashoops.web.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import uk.ac.mib.antismashoops.core.utils.FileUploadHandler;

@Controller
public class FileUploadController
{
	@Value("${app.files.uploadpath}")
	private String uploadPath;

	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

	@RequestMapping("/saveFile")
	public String saveFile(@RequestParam("zipFile") MultipartFile file, ModelMap model) throws IOException
	{
		logger.debug("Saving file: {}", file.getOriginalFilename());
		logger.info("Saving file: {}", file.getOriginalFilename());
		File storeLocation = new File(uploadPath);
		File compressedFile = new File(storeLocation, file.getOriginalFilename());
		storeLocation.mkdirs();
		FileCopyUtils.copy(file.getBytes(), compressedFile);

		FileUploadHandler.decompressFile(compressedFile, uploadPath);

		model.addAttribute("fileName", file.getOriginalFilename());
		return "dashboard";
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception)
	{
		req.setAttribute("message", exception.getMessage());
		return "error";
	}
}
