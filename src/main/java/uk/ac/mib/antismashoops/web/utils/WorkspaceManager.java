package uk.ac.mib.antismashoops.web.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("singleton")
@Component
public class WorkspaceManager
{

    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceManager.class);

    private final File rootDirectory;
    private final String UPLOADPATH = "appData/";
    private List<Workspace> workspaces;
    private File currentWorkspace;


    public WorkspaceManager()
    {
        rootDirectory = new File(UPLOADPATH);
        workspaces = new ArrayList<>();
    }


    public Workspace getWorkspace(String wsName)
    {
        for (Workspace ws : this.workspaces)
        {
            if (ws.getName().equalsIgnoreCase(wsName))
            {
                return ws;
            }
        }
        return null;
    }


    public void populateWorkspaces()
    {

        if (!rootDirectory.exists())
        {
            rootDirectory.mkdir();
            return;
        }

        for (File f : rootDirectory.listFiles())
        {
            if (f.isDirectory())
            {
                workspaces.add(new Workspace(f, f.getName(), new Date(f.lastModified())));
            }
        }
        LOG.info("Workspace size: " + workspaces.size() + " directories.");
    }


    public void createWorkspace(String wsName)
    {
        File newWS = new File(this.getRootDirectory(), wsName);
        if (!newWS.exists())
        {
            newWS.mkdir();
        }
    }


    public void deleteWorkspace(String wsName) throws IOException
    {
        this.delete(new File(this.getRootDirectory(), wsName));
    }


    public void delete(File f) throws IOException
    {
        if (f.isDirectory())
        {
            for (File c : f.listFiles())
            {
                delete(c);
            }
        }
        if (!f.delete())
        {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }


    public File getRootDirectory()
    {
        return rootDirectory;
    }


    public List<Workspace> getWorkspaces()
    {
        return workspaces;
    }


    public File getCurrentWorkspace()
    {
        return currentWorkspace;
    }


    public void setCurrentWorkspace(String currentWorkspace)
    {
        this.currentWorkspace = new File(this.getRootDirectory(), currentWorkspace);
    }
}
