package uk.ac.mib.antismashoops.web.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.mib.antismashoops.web.utils.WorkspaceManager;

@Controller
public class IndexController {
    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

    private final WorkspaceManager fm;


    @Autowired
    public IndexController(WorkspaceManager fm)
    {
        this.fm = fm;
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
        LOG.info("Loading Index View...");
        fm.getWorkspaces().clear();
        fm.populateWorkspaces();
        model.addAttribute("workspaceData", fm.getWorkspaces());
        return "index";
	}


    @RequestMapping("/addWorkspace/{wsName}")
    public String addWorkspace(@PathVariable String wsName, ModelMap model)
    {
        LOG.info("Adding new workspace...");
        fm.createWorkspace(wsName);
        fm.getWorkspaces().clear();
        fm.populateWorkspaces();
        model.addAttribute("workspaceData", fm.getWorkspaces());
        return "index";
    }


    @RequestMapping("/deleteWorkspace/{wsName}")
    public String deleteWorkspace(@PathVariable String wsName, ModelMap model) throws IOException
    {
        LOG.info("Deleting workspace...");
        fm.deleteWorkspace(wsName);
        fm.getWorkspaces().clear();
        fm.populateWorkspaces();
        model.addAttribute("workspaceData", fm.getWorkspaces());
        return "index";
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
