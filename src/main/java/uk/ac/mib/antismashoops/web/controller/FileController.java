package uk.ac.mib.antismashoops.web.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uk.ac.mib.antismashoops.web.utils.WorkspaceManager;

@Controller
public class FileController
{
    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);
    private final WorkspaceManager fm;


    @Autowired
    public FileController(WorkspaceManager fm)
    {
        this.fm = fm;
    }


    /**
     * Handles the URL call to /upload path. Loads the files into the
     * application data folder.
     *
     * @param request The servlet request object
     * @return The files list uploaded
     */

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(MultipartHttpServletRequest request)
    {
        Iterator<String> itr = request.getFileNames();
        while (itr.hasNext())
        {
            MultipartFile mpf = request.getFile(itr.next());
            try
            {
                FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream(
                    fm.getCurrentWorkspace().getAbsolutePath() + "/" + mpf.getOriginalFilename()));

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            LOG.info(mpf.getOriginalFilename() + " loaded!");
        }

        return "fileUpload";
    }


    @ExceptionHandler(Exception.class)
    public String exceptionHandler(HttpServletRequest req, Exception exception)
    {
        req.setAttribute("message", exception.getClass() + " - " + exception.getMessage());
        LOG.error("Exception thrown: " + exception.getClass());
        LOG.error("Exception message: " + exception.getMessage());
        exception.printStackTrace();
        return "error";
    }
}
