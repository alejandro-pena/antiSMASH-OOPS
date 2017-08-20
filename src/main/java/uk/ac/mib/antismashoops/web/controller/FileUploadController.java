package uk.ac.mib.antismashoops.web.controller;

import java.io.File;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.mib.antismashoops.web.utils.Workspace;
import uk.ac.mib.antismashoops.web.utils.WorkspaceManager;

@Slf4j
@Controller
public class FileUploadController
{
    private final WorkspaceManager workspaceManager;


    @Autowired
    public FileUploadController(WorkspaceManager workspaceManager)
    {
        this.workspaceManager = workspaceManager;
    }


    /**
     * Handles the URL call to /fileUpload path. Loads index view of the application where
     * the loading of files occur.
     *
     * @return The File Upload HTML view.
     */

    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam(value = "wsRadio") String workspace, ModelMap model)
    {
        File workspaceRoot = new File(workspaceManager.getRootDirectory(), workspace);
        workspaceManager.setCurrentWorkspace(new Workspace(workspaceRoot, workspace, new Date(workspaceRoot.lastModified())));

        if ("antiSMASH_Actinobacterial_BGCs".equals(workspaceManager.getCurrentWorkspace().getName()))
        {
            log.info("This is a read-only workspace. Redirecting to main Dashboard...");
            return "redirect:/dashboard";
        }

        log.info("Loading File Upload View...");

        model.addAttribute("fileData", workspaceManager.getWorkspace(workspace).getWorkspaceFiles());
        model.addAttribute("workspace", workspace);

        return "fileUpload";
    }


    @ExceptionHandler(Exception.class)
    public String exceptionHandler(HttpServletRequest req, Exception e)
    {
        req.setAttribute("message", e.getClass() + " - " + e.getMessage());
        log.error("Unexpected exception", e);
        return "error";
    }

}
