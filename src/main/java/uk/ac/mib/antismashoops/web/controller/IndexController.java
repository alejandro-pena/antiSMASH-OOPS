package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.mib.antismashoops.web.utils.WorkspaceManager;

@Slf4j
@Controller
public class IndexController
{
    private final WorkspaceManager workspaceManager;


    @Autowired
    public IndexController(WorkspaceManager workspaceManager)
    {
        this.workspaceManager = workspaceManager;
    }


    /**
     * Handles the URL call to /index path. Loads index view of the application
     * where the loading of files occur.
     *
     * @return The index HTML view.
     */

    @RequestMapping("/")
    public String index(ModelMap model)
    {
        log.info("Loading Index View...");
        workspaceManager.getWorkspaces().clear();
        workspaceManager.populateWorkspaces();
        model.addAttribute("workspaceData", workspaceManager.getWorkspaces());
        return "index";
    }


    @RequestMapping("/addWorkspace/{wsName}")
    public String addWorkspace(@PathVariable String wsName, ModelMap model)
    {
        log.info("Adding new workspace...");
        workspaceManager.createWorkspace(wsName);
        workspaceManager.getWorkspaces().clear();
        workspaceManager.populateWorkspaces();
        model.addAttribute("workspaceData", workspaceManager.getWorkspaces());
        return "index";
    }


    @RequestMapping("/deleteWorkspace/{wsName}")
    public String deleteWorkspace(@PathVariable String wsName, ModelMap model) throws IOException
    {
        log.info("Deleting workspace...");
        workspaceManager.deleteWorkspace(wsName);
        workspaceManager.getWorkspaces().clear();
        workspaceManager.populateWorkspaces();
        model.addAttribute("workspaceData", workspaceManager.getWorkspaces());
        return "index";
    }


    @ExceptionHandler(Exception.class)
    public String exceptionHandler(HttpServletRequest req, Exception e)
    {
        req.setAttribute("message", e.getClass() + " - " + e.getMessage());
        log.error("Unexpected exception", e);
        return "error";
    }
}
