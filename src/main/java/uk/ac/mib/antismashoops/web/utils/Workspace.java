package uk.ac.mib.antismashoops.web.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
public class Workspace
{
    private File root;
    private String name;
    private Date lastModified;


    public Workspace(File root, String name, Date lastModified)
    {
        this.root = root;
        this.name = name;
        this.lastModified = lastModified;
    }


    public List<File> getWorkspaceFiles()
    {
        List<File> files = new ArrayList<>();

        for (File f : this.root.listFiles())
        {
            if (f.getName().contains(".zip"))
            {
                files.add(f);
            }
        }
        return files;
    }
}
