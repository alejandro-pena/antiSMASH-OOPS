package uk.ac.mib.antismashoops.web.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uk.ac.mib.antismashoops.core.domainvalue.FileMetadata;
import uk.ac.mib.antismashoops.web.utils.WorkspaceManager;

@Controller
public class FileController {
    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);
    private final WorkspaceManager fm;
    List<FileMetadata> files = new ArrayList<>();
    List<FileMetadata> filesFull = new ArrayList<>();
    @Value("${app.files.uploadpath}")
	private String uploadPath;


    @Autowired
    public FileController(WorkspaceManager fm)
    {
        this.fm = fm;
    }

	/**
	 * Handles the URL call to /upload path. Loads the files into the
	 * application data folder.
	 *
     * @param request
     *            The servlet request object
     * @param response
     *            The servlet response object
     *
	 * @return The files list uploaded
	 */

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void upload(MultipartHttpServletRequest request, HttpServletResponse response)
    {
        // 1. build an iterator
		Iterator<String> itr = request.getFileNames();
		FileMetadata fileMeta = null;
		FileMetadata fileMeta2 = null;

		// 2. get each file
		while (itr.hasNext()) {
			// 2.1 get next MultipartFile
			MultipartFile mpf = request.getFile(itr.next());

            // 2.2 create new fileMeta
            fileMeta = new FileMetadata();
			fileMeta.setFileName(mpf.getOriginalFilename());
			fileMeta.setFileSize(mpf.getSize() / 1024 + " Kb");
			fileMeta.setFileType(mpf.getContentType());

			fileMeta2 = new FileMetadata();
			fileMeta2.setFileName(mpf.getOriginalFilename());
			fileMeta2.setFileSize(mpf.getSize() / 1024 + " Kb");
			fileMeta2.setFileType(mpf.getContentType());

			try {
				fileMeta2.setBytes(mpf.getBytes());
                FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream(
                    fm.getCurrentWorkspace().getAbsolutePath() + "/" + mpf.getOriginalFilename()));

			} catch (IOException e) {
				e.printStackTrace();
			}
            // 2.3 add to files
            files.add(0, fileMeta);
			filesFull.add(0, fileMeta2);

            LOG.info(mpf.getOriginalFilename() + " loaded!");
        }
	}

	/**
	 * Handles the URL call to /get path specifying the file id to download.
	 *
     * @param response
     *            The servlet response object
     * @param value
     *            The file id to download
     *
	 */

	@RequestMapping(value = "/get/{value}", method = RequestMethod.GET)
	public void get(HttpServletResponse response, @PathVariable String value) {
		FileMetadata getFile = filesFull.get(Integer.parseInt(value));
		try {
			response.setContentType(getFile.getFileType());
			response.setHeader("Content-disposition", "attachment; filename=\"" + getFile.getFileName() + "\"");
			FileCopyUtils.copy(getFile.getBytes(), response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@ExceptionHandler(Exception.class)
	public String exceptionHandler(HttpServletRequest req, Exception exception) {
		req.setAttribute("message", exception.getClass() + " - " + exception.getMessage());
        LOG.error("Exception thrown: " + exception.getClass());
        LOG.error("Exception message: " + exception.getMessage());
        exception.printStackTrace();
		return "error";
	}
}
