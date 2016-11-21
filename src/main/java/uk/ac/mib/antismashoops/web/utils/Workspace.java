package uk.ac.mib.antismashoops.web.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    public File getRoot()
    {
        return root;
    }


    public void setRoot(File root)
    {
        this.root = root;
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public Date getLastModified()
    {
        return lastModified;
    }


    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }
}
