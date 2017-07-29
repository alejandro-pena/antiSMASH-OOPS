package uk.ac.mib.antismashoops.core.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uk.ac.mib.antismashoops.web.utils.WorkspaceManager;

@Slf4j
@Controller
public class FileController
{
    private final WorkspaceManager workspaceManager;


    @Autowired
    public FileController(WorkspaceManager workspaceManager)
    {
        this.workspaceManager = workspaceManager;
    }


    /**
     * Handles the URL call to /upload path. Loads the files into the
     * application data folder.
     *
     * @param request The servlet request object
     * @return The files list uploaded
     */

    @PostMapping(value = "/upload")
    public String upload(MultipartHttpServletRequest request)
    {
        Iterator<String> itr = request.getFileNames();
        while (itr.hasNext())
        {
            MultipartFile multipartFile = request.getFile(itr.next());
            try
            {
                FileCopyUtils.copy(multipartFile.getBytes(), new FileOutputStream(
                    workspaceManager.getCurrentWorkspace().getRoot().getAbsolutePath() + "/" + multipartFile.getOriginalFilename()));

            }
            catch (IOException e)
            {
                log.error("Exception uploading file: {}", multipartFile.getOriginalFilename(), e);
            }
            log.info(multipartFile.getOriginalFilename() + " loaded!");
        }

        return "fileUpload";
    }


    @GetMapping(value = "/delete/{fileName:.+}")
    public String deleteFile(@PathVariable("fileName") String fileName) throws FileNotFoundException
    {
        File f = new File(workspaceManager.getCurrentWorkspace().getRoot(), fileName);
        log.info("Deleting file: {}", fileName);
        if (!f.delete())
        {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
        return "redirect:/fileUpload?wsRadio=" + workspaceManager.getCurrentWorkspace().getName();
    }


    @ExceptionHandler(Exception.class)
    public String exceptionHandler(HttpServletRequest req, Exception e)
    {
        req.setAttribute("message", e.getClass() + " - " + e.getMessage());
        log.error("Unexpected exception", e);
        return "error";
    }
}
