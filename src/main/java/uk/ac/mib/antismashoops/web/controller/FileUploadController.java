package uk.ac.mib.antismashoops.web.controller;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.mib.antismashoops.web.utils.WorkspaceManager;

@Controller
public class FileUploadController
{

    private static final Logger LOG = LoggerFactory.getLogger(FileUploadController.class);

    private final WorkspaceManager fm;


    @Autowired
    public FileUploadController(WorkspaceManager fm)
    {
        this.fm = fm;
    }


    /**
     * Handles the URL call to /fileUpload path. Loads index view of the application where
     * the loading of files occur.
     *
     * @return The File Upload HTML view.
     */

    @RequestMapping("/fileUpload")
    public String fileUplaod(@RequestParam(value = "wsRadio") String workspace, ModelMap model)
    {
        LOG.info("Loading File Upload View...");

        fm.setCurrentWorkspace(workspace);

        model.addAttribute("fileData", fm.getWorkspace(workspace).getWorkspaceFiles());
        model.addAttribute("workspace", workspace);

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
