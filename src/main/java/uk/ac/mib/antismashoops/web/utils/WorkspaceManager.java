package uk.ac.mib.antismashoops.web.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class WorkspaceManager
{
    private final File rootDirectory;
    private List<Workspace> workspaces;

    @Setter
    private Workspace currentWorkspace;


    public WorkspaceManager(@Value("${app.files.uploadpath}") String uploadPath)
    {
        rootDirectory = new File(uploadPath);
        workspaces = new ArrayList<>();
    }


    public Workspace getWorkspace(String wsName)
    {
        Optional<Workspace> workspace = this.workspaces.stream().filter(ws -> ws.getName().equalsIgnoreCase(wsName)).findFirst();
        if (workspace.isPresent())
        {
            return workspace.get();
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
        log.info("Workspace size: " + workspaces.size() + " directories.");
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
        FileUtils.delete(new File(this.getRootDirectory(), wsName));
    }
}
