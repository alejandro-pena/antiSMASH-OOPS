package uk.ac.mib.antismashoops.web.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import uk.ac.mib.antismashoops.core.model.FileMetadata;

@Controller
public class FileController
{
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Value("${app.files.uploadpath}")
	private String uploadPath;

	List<FileMetadata> files = new ArrayList<>();
	List<FileMetadata> filesFull = new ArrayList<>();

	/***************************************************
	 * URL: /rest/controller/upload upload(): receives files
	 * 
	 * @param request
	 *            : MultipartHttpServletRequest auto passed
	 * @param response
	 *            : HttpServletResponse auto passed
	 * @return LinkedList<FileMeta> as json format
	 ****************************************************/
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody List<FileMetadata> upload(MultipartHttpServletRequest request, HttpServletResponse response)
	{
		// 1. build an iterator
		Iterator<String> itr = request.getFileNames();
		FileMetadata fileMeta = null;
		FileMetadata fileMeta2 = null;

		// 2. get each file
		while (itr.hasNext())
		{
			// 2.1 get next MultipartFile
			MultipartFile mpf = request.getFile(itr.next());

			// 2.2 if files > 10 remove the first from the list
			if (files.size() > 10)
				files.remove(0);

			// 2.3 create new fileMeta
			fileMeta = new FileMetadata();
			fileMeta.setFileName(mpf.getOriginalFilename());
			fileMeta.setFileSize(mpf.getSize() / 1024 + " Kb");
			fileMeta.setFileType(mpf.getContentType());

			fileMeta2 = new FileMetadata();
			fileMeta2.setFileName(mpf.getOriginalFilename());
			fileMeta2.setFileSize(mpf.getSize() / 1024 + " Kb");
			fileMeta2.setFileType(mpf.getContentType());

			try
			{
				fileMeta2.setBytes(mpf.getBytes());
				FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream(uploadPath + mpf.getOriginalFilename()));

			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 2.4 add to files
			files.add(0, fileMeta);
			filesFull.add(0, fileMeta2);

			System.out.println(mpf.getOriginalFilename() + " uploaded!");
		}

		logger.info(files.toString());

		return files;

	}

	/***************************************************
	 * URL: /rest/controller/get/{value} get(): get file as an attachment
	 * 
	 * @param response
	 *            : passed by the server
	 * @param value
	 *            : value from the URL
	 * @return void
	 ****************************************************/
	@RequestMapping(value = "/get/{value}", method = RequestMethod.GET)
	public void get(HttpServletResponse response, @PathVariable String value)
	{
		FileMetadata getFile = filesFull.get(Integer.parseInt(value));
		try
		{
			response.setContentType(getFile.getFileType());
			response.setHeader("Content-disposition", "attachment; filename=\"" + getFile.getFileName() + "\"");
			FileCopyUtils.copy(getFile.getBytes(), response.getOutputStream());
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
