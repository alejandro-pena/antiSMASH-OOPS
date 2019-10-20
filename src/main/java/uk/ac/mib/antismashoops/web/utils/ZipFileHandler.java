package uk.ac.mib.antismashoops.web.utils;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

@Slf4j
public class ZipFileHandler
{
    /**
     * Decompresses the specified file in the specified location
     *
     * @param compressedFile The ZIP file to decompress
     * @param uploadPath     The path where the file contents will be placed
     */

    public static void decompressFile(File compressedFile, String uploadPath)
    {
        try
        {
            ZipFile zipFile = new ZipFile(compressedFile);
            if (zipFile.isEncrypted())
            {
                log.info("The file is password protected... Unable to decompress.");
            }

            zipFile.extractAll(uploadPath + "/" + compressedFile.getName().split("\\.")[0]);
        }
        catch (ZipException e)
        {
            log.error(e.getMessage());
        }
    }
}
